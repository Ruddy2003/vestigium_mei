package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.Letra
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.Pagina
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.bisel
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
 * boca del abrigo salen las estrellas; en la laja, la luna en lugar de la mano. La tapa es esa laja tumbada y grande, con
 * la fecha en ocre, la mano, el ciervo de carbón y la cara alisada para el extracto; al abrirse se aparta rodando y deja ver
 * el hueco de la pared con una mano en negativo. Distinto del Ideario, que es la pared honda de la cueva con sus nichos
 * tallados, las manos en negativo y el bisonte.
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

    // ---------------------------------------------------------------- la tapa

    override val altoTapa: Dp = 184.dp
    override val cajaTapa = Rect(0.3f, 0.3f, 0.92f, 0.73f)
    override val letraTapa: FontFamily = LetraExtra.abrigo
    override val tamTapa = 13.5f
    override val lineasTapa = 3
    override val centradoTapa = false

    /** La cara alisada de la laja, donde va el extracto: de noche la alumbran las brasas, así que se hunde menos. */
    private fun caraTapa(oscuro: Boolean, noche: Boolean): Color {
        var r = CALIZAS[0]
        if (oscuro) r = mezcla(r, Color(0xFF0F0B08), 0.36f)
        if (noche) r = mezcla(r, NOCHE, 0.34f)
        return r
    }

    override fun tintaTapa(oscuro: Boolean, noche: Boolean): Color = legibleAnt(caraTapa(oscuro, noche), CARBON, CALIZAS[0])
    override fun palabrasTapa(n: Int): String = palabrasAnt(n) + " en la laja"

    /** Un ciervo trazado con carbón, de perfil: el lomo, el vientre, las patas, el cuello y la cornamenta. */
    private fun DrawScope.ciervo(o: Offset, m: Float, color: Color) {
        val u = density
        val g = 1.4f * u
        val lomo = Path().apply { moveTo(o.x - m * 0.5f, o.y); quadraticTo(o.x, o.y - m * 0.16f, o.x + m * 0.42f, o.y - m * 0.02f) }
        val vientre = Path().apply { moveTo(o.x - m * 0.46f, o.y + m * 0.08f); quadraticTo(o.x, o.y + m * 0.26f, o.x + m * 0.4f, o.y + m * 0.12f) }
        drawPath(lomo, color, style = Stroke(g, cap = StrokeCap.Round))
        drawPath(vientre, color, style = Stroke(g, cap = StrokeCap.Round))
        listOf(-0.4f to -0.06f, -0.3f to 0.05f, 0.28f to -0.04f, 0.36f to 0.07f).forEach { (x0, d) ->
            drawLine(color, Offset(o.x + m * x0, o.y + m * 0.12f), Offset(o.x + m * (x0 + d), o.y + m * 0.5f), g, StrokeCap.Round)
        }
        drawLine(color, Offset(o.x + m * 0.4f, o.y), Offset(o.x + m * 0.58f, o.y - m * 0.3f), g * 1.5f, StrokeCap.Round)
        drawLine(color, Offset(o.x + m * 0.58f, o.y - m * 0.3f), Offset(o.x + m * 0.74f, o.y - m * 0.23f), g * 1.2f, StrokeCap.Round)
        val raiz = Offset(o.x + m * 0.57f, o.y - m * 0.33f)
        drawLine(color, raiz, Offset(raiz.x - m * 0.14f, raiz.y - m * 0.36f), g * 0.8f, StrokeCap.Round)
        for (k in 1..3) {
            val p = Offset(raiz.x - m * 0.045f * k, raiz.y - m * 0.11f * k)
            drawLine(color, p, Offset(p.x + m * 0.12f, p.y - m * 0.07f), g * 0.7f, StrokeCap.Round)
        }
        drawLine(color, Offset(o.x - m * 0.5f, o.y), Offset(o.x - m * 0.6f, o.y + m * 0.1f), g, StrokeCap.Round)
    }

    /** La tapa, recortada a su lienzo: lo que se aparta al abrir y la luz de noche no se salen de su lugar. */
    override fun DrawScope.tapa(e: EstadoTapa, medidor: TextMeasurer, oscuro: Boolean) = clipRect { dibujarTapa(e, medidor, oscuro) }

    private fun DrawScope.dibujarTapa(e: EstadoTapa, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, e.noche)
        val a = e.abierto.coerceIn(0f, 1f)
        val laja = Rect(w * 0.02f, h * 0.04f, w * 0.98f, h * 0.96f)
        val pintura = mezcla(OCRE_AMARILLO, FUEGO, 0.35f)
        // Detrás, el hueco de la pared que la laja tapaba (se ve al apartarla): la roca honda con sus capas, una mano en
        // negativo soplada con ocre, hileras de puntos y las rayas de cuenta de otros días.
        if (a > 0.01f) {
            val hueco = contornoIrregular(laja.deflate(3 * u), e.semilla + 7, 36, 0.05f)
            drawPath(hueco, c(ROCA_HONDA))
            clipPath(hueco) {
                for (j in 0 until 6) {
                    val y = laja.top + laja.height * (0.1f + 0.16f * j)
                    val capa = Path()
                    capa.moveTo(laja.left, y)
                    var x = laja.left
                    var k = 0
                    while (x < laja.right) {
                        capa.quadraticTo(x + 14 * u, y + (azar(e.semilla + j, k) - 0.5f) * 8 * u, x + 28 * u, y)
                        x += 28 * u; k++
                    }
                    drawPath(capa, c(ROCA_OSC).copy(alpha = 0.5f), style = Stroke(1f * u))
                }
                val cm = Offset(laja.left + w * 0.16f, h * 0.44f)
                mancha(cm, h * 0.26f, c(OCRE).copy(alpha = 0.8f))
                mano(Offset(cm.x, cm.y - h * 0.05f), h * 0.12f, c(ROCA_HONDA), e.semilla + 1)
                for (f in 0 until 3) for (j in 0 until 7) {
                    drawCircle(c(OCRE), 2.2f * u, Offset(laja.left + w * (0.36f + 0.045f * j), h * (0.28f + 0.09f * f) + (j % 2) * 2 * u))
                }
                for (g in 0 until 4) for (j in 0 until 5) {
                    val x0 = laja.left + w * (0.36f + 0.08f * g) + j * 3.2f * u
                    if (j < 4) drawLine(c(CARBON), Offset(x0, h * 0.62f), Offset(x0 + 0.6f * u, h * 0.76f), 1.6f * u, StrokeCap.Round)
                    else drawLine(c(CARBON), Offset(x0 - 14 * u, h * 0.74f), Offset(x0 + 1 * u, h * 0.64f), 1.6f * u, StrokeCap.Round)
                }
            }
            drawPath(hueco, Color.Black.copy(alpha = 0.4f), style = Stroke(2 * u))
        }
        // La laja se aparta hacia la derecha, rodando un poco sobre su esquina.
        withTransform({ translate(w * 0.7f * a, h * 0.05f * a); rotate(8f * a, Offset(laja.right, laja.bottom)) }) {
            val sem = e.semilla and 0x7fffffff
            val piedra = c(CALIZAS[1 + sem % (CALIZAS.size - 1)])
            val contorno = contornoIrregular(laja, e.semilla, 44, 0.035f)
            translate(3 * u, 4 * u) { drawPath(contorno, Color.Black.copy(alpha = 0.3f)) }
            drawPath(contorno, piedra)
            clipPath(contorno) {
                // Luz a la izquierda, sombra a la derecha y abajo (planas), las capas de la caliza, los poros y una grieta.
                drawRect(Color.White.copy(alpha = 0.12f), laja.topLeft, Size(laja.width * 0.1f, laja.height))
                drawRect(Color.Black.copy(alpha = 0.18f), Offset(laja.right - laja.width * 0.07f, laja.top), Size(laja.width * 0.07f, laja.height))
                drawRect(Color.Black.copy(alpha = 0.12f), Offset(laja.left, laja.bottom - laja.height * 0.1f), Size(laja.width, laja.height * 0.1f))
                for (j in 0 until 5) {
                    val yy = laja.top + laja.height * (0.1f + 0.2f * j + 0.05f * azar(e.semilla, 10 + j))
                    drawLine(c(LAJA_OSC).copy(alpha = 0.35f), Offset(laja.left, yy), Offset(laja.right, yy + (azar(e.semilla, 20 + j) - 0.5f) * 10 * u), 0.8f * u)
                }
                for (j in 0 until 46) {
                    drawCircle(c(LAJA_OSC).copy(alpha = 0.45f), (0.6f + 1.1f * azar(e.semilla, 30 + j)) * u,
                        Offset(laja.left + laja.width * azar(e.semilla, 80 + j), laja.top + laja.height * azar(e.semilla, 130 + j)))
                }
                val grieta = Path().apply {
                    moveTo(w * 0.27f, laja.bottom)
                    for (s in 1..5) lineTo(w * 0.27f + (azar(e.semilla, 180 + s) - 0.5f) * 10 * u, laja.bottom - h * 0.045f * s)
                }
                drawPath(grieta, Color.Black.copy(alpha = 0.35f), style = Stroke(0.9f * u))
                // De noche, la luz de las brasas que cae sobre la piedra (el único degradado de la laja).
                if (e.noche) mancha(Offset(laja.left, laja.bottom), h * 0.95f, BRASA.copy(alpha = 0.22f))
            }
            drawPath(contorno, c(ROCA_OSC), style = Stroke(1f * u))
            // La cara alisada a mano, donde va el extracto.
            val caja = Rect(w * cajaTapa.left, h * cajaTapa.top, w * cajaTapa.right, h * cajaTapa.bottom)
            val cara = contornoIrregular(caja.inflate(6 * u), e.semilla + 3, 30, 0.03f)
            drawPath(cara, caraTapa(oscuro, e.noche))
            drawPath(cara, c(LAJA_OSC).copy(alpha = 0.5f), style = Stroke(0.8f * u))
            // La fecha pintada con ocre arriba (en dos renglones si es larga) y un trazo ondulado debajo.
            val ocre = legibleAnt(piedra, c(OCRE), pintura)
            val zona = Rect(w * 0.3f, h * 0.065f, w * 0.93f, h * 0.25f)
            val fechas = renglonesFechaAnt(e.fecha, 26)
            if (fechas.size == 1) {
                textoEn(medidor, fechas[0], zona, LetraExtra.trazo, ocre, 17f, peso = FontWeight.Bold, minimo = 8f)
            } else {
                val corte = zona.top + zona.height * 0.58f
                textoEn(medidor, fechas[0], Rect(zona.left, zona.top, zona.right, corte), LetraExtra.trazo, ocre, 15f, peso = FontWeight.Bold, minimo = 8f)
                textoEn(medidor, fechas[1], Rect(zona.left, corte, zona.right, zona.bottom), LetraExtra.trazo, ocre.copy(alpha = 0.85f), 11f, minimo = 7f)
            }
            val trazo = Path()
            trazo.moveTo(w * 0.33f, h * 0.265f)
            var tx = w * 0.33f
            var k = 0
            while (tx < w * 0.88f) {
                trazo.quadraticTo(tx + 6 * u, h * 0.265f + (if (k % 2 == 0) -2.4f else 2.4f) * u, tx + 12 * u, h * 0.265f)
                tx += 12 * u; k++
            }
            drawPath(trazo, ocre.copy(alpha = 0.7f), style = Stroke(1.8f * u, cap = StrokeCap.Round))
            // A la izquierda, la mano en positivo; de noche, la luna creciente y unas estrellas, en ocre amarillo.
            val cm = Offset(w * 0.15f, h * 0.29f)
            val m = h * 0.13f
            if (e.noche) {
                val luna = Offset(cm.x, cm.y + m * 0.25f)
                drawCircle(pintura, m * 0.72f, luna)
                drawCircle(piedra, m * 0.62f, Offset(luna.x + m * 0.34f, luna.y - m * 0.2f))
                for (j in 0 until 6) {
                    val p = Offset(w * (0.06f + 0.035f * j), h * (0.1f + 0.05f * ((j * 7) % 3)) + if (j % 2 == 0) m * 1.25f else 0f)
                    if (j % 3 == 0) drawPath(estrella(p, 3.2f * u, 4, 0.35f), pintura) else drawCircle(pintura, 1.3f * u, p)
                }
            } else {
                mano(cm, m, c(OCRE).copy(alpha = 0.85f), e.semilla)
            }
            // La hilera de puntos de ocre y el ciervo de carbón.
            for (j in 0 until 5) drawCircle(ocre, 2.3f * u, Offset(w * (0.075f + 0.04f * j), h * 0.535f + (j % 2) * 2 * u))
            ciervo(Offset(w * 0.14f, h * 0.74f), w * 0.13f, legibleAnt(piedra, c(CARBON), c(ROCA_LUZ)).copy(alpha = 0.85f))
            // Abajo, las rayas de cuenta en grupos de cinco (más cuanto más escribiste) y cuántas palabras hay.
            if (e.palabras > 0) {
                val rayas = (1 + e.palabras / 40).coerceAtMost(20)
                for (j in 0 until rayas) {
                    val g = j / 5; val i = j % 5
                    val x0 = w * 0.32f + g * 18 * u + i * 3.4f * u
                    if (i < 4) drawLine(ocre, Offset(x0, h * 0.79f), Offset(x0 + 0.6f * u, h * 0.9f), 1.5f * u, StrokeCap.Round)
                    else drawLine(ocre, Offset(x0 - 14.5f * u, h * 0.88f), Offset(x0 + 0.5f * u, h * 0.8f), 1.5f * u, StrokeCap.Round)
                }
                textoEn(medidor, palabrasTapa(e.palabras), Rect(w * 0.6f, h * 0.78f, w * 0.93f, h * 0.91f), letraTapa,
                    legibleAnt(piedra, c(CARBON), CALIZAS[0]).copy(alpha = 0.85f), 11f, minimo = 7f)
            }
        }
    }

    // ---------------------------------------------------------------- la página y el escritor

    override val altoEncabezado: Dp = 118.dp

    /**
     * El encabezado: una laja de caliza alisada a todo el ancho, de contorno desportillado, con sus capas y sus poros. A la
     * izquierda, la mano en positivo (de noche, la luna creciente y unas estrellas, en ocre amarillo); a la derecha, el ciervo
     * de carbón; en medio, el rótulo pintado con ocre, la fecha con carbón (la protagonista), el trazo ondulado y el detalle;
     * abajo, la hilera de puntos de ocre.
     */
    override fun DrawScope.encabezado(e: EstadoEncabezado, p: Pagina, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val laja = Rect(2 * u, 4 * u, w - 3 * u, h - 5 * u)
        val contorno = contornoIrregular(laja, e.numero * 7 + 3, 52, 0.03f)
        val piedra = if (p.oscura) mezcla(p.color, p.tinta, 0.1f) else tonoAnt(CALIZAS[1], p, oscuro)
        translate(2 * u, 3 * u) { drawPath(contorno, Color.Black.copy(alpha = if (p.oscura) 0.35f else 0.18f)) }
        drawPath(contorno, piedra)
        clipPath(contorno) {
            // Las capas de la caliza, los poros y, de noche, la luz de las brasas que sube desde abajo (el único degradado).
            for (j in 0 until 4) {
                val y = laja.top + laja.height * (0.18f + 0.22f * j)
                val capa = Path()
                capa.moveTo(laja.left, y)
                var x = laja.left
                var k = 0
                while (x < laja.right) {
                    capa.quadraticTo(x + 16 * u, y + (azar(e.numero + j, k) - 0.5f) * 6 * u, x + 32 * u, y)
                    x += 32 * u; k++
                }
                drawPath(capa, p.tinta.copy(alpha = 0.07f), style = Stroke(0.8f * u))
            }
            for (j in 0 until 60) {
                drawCircle(p.tinta.copy(alpha = 0.08f + 0.08f * azar(e.numero, 300 + j)), (0.5f + azar(e.numero, 400 + j)) * u,
                    Offset(laja.left + laja.width * azar(e.numero, 500 + j), laja.top + laja.height * azar(e.numero, 600 + j)))
            }
            if (e.noche) mancha(Offset(laja.left + w * 0.12f, laja.bottom + 10 * u), h * 1.3f, BRASA.copy(alpha = 0.2f))
        }
        drawPath(contorno, p.tinta.copy(alpha = 0.3f), style = Stroke(1f * u))
        val pintura = mezcla(OCRE_AMARILLO, FUEGO, 0.35f)
        // La mano (o, de noche, la luna y sus estrellas).
        val cm = Offset(laja.left + 27 * u, h * 0.45f)
        val m = 27 * u
        if (e.noche) {
            val luna = Offset(cm.x, h * 0.5f)
            drawCircle(pintura, m * 0.62f, luna)
            drawCircle(piedra, m * 0.52f, Offset(luna.x + m * 0.3f, luna.y - m * 0.18f))
            for (j in 0 until 5) {
                val s = Offset(laja.left + (8 + 9 * j) * u, h * (if (j % 2 == 0) 0.18f else 0.82f))
                if (j % 2 == 0) drawPath(estrella(s, 3.4f * u, 4, 0.35f), pintura) else drawCircle(pintura, 1.3f * u, s)
            }
        } else {
            mano(cm, m, p.acento.copy(alpha = 0.85f), e.numero)
        }
        // El ciervo de carbón, a la derecha.
        ciervo(Offset(w - 40 * u, h * 0.56f), 38 * u, p.tinta.copy(alpha = 0.72f))
        // El rótulo, la fecha, el trazo ondulado y el detalle.
        val izq = laja.left + 58 * u
        val der = w - 72 * u
        textoEn(medidor, e.rotulo, Rect(izq - 4 * u, 9 * u, w - 14 * u, 29 * u), LetraExtra.trazo, p.acento, 15f, espaciado = 0.3f, minimo = 8f)
        fechaEncAnt(medidor, e.fecha, Rect(izq, 30 * u, der, 74 * u), LetraExtra.abrigo, p.tinta, 22f, 22, FontWeight.Bold)
        val trazo = Path()
        val ty = 78 * u
        trazo.moveTo(izq + 10 * u, ty)
        var tx = izq + 10 * u
        var k = 0
        while (tx < der - 10 * u) {
            trazo.quadraticTo(tx + 5 * u, ty + (if (k % 2 == 0) -2f else 2f) * u, tx + 10 * u, ty)
            tx += 10 * u; k++
        }
        drawPath(trazo, p.acento.copy(alpha = 0.6f), style = Stroke(1.5f * u, cap = StrokeCap.Round))
        textoEn(medidor, e.detalle, Rect(laja.left + 10 * u, 85 * u, laja.right - 10 * u, 99 * u), LetraExtra.abrigo, p.tenue, 11f, minimo = 7f)
        for (j in 0 until 7) drawCircle(p.acento.copy(alpha = 0.75f), 2.1f * u, Offset((izq + der) / 2 + (j - 3) * 9 * u, 105 * u + (j % 2) * 1.6f * u))
    }

    /**
     * La pared donde se escribe: el hollín de la lámpara arriba (en franjas, sin degradado), las capas de la roca, la hilera de
     * puntos de ocre, las rayas de cuenta que bajan por el margen izquierdo en grupos de cinco, el ciervo de carbón apenas
     * trazado abajo a la izquierda y, abajo a la derecha, una mano en negativo con el ocre soplado alrededor (de noche, una luna
     * creciente arriba). En modo oscuro, la pared en la penumbra y la luz del fuego que sube desde abajo a la izquierda.
     */
    override fun DrawScope.pagina(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        for (k in 0 until 6) drawRect(p.tinta.copy(alpha = 0.022f), Offset.Zero, Size(w, (60 + k * 22) * u))
        for (j in 0 until 8) {
            val y = h * (0.1f + 0.115f * j)
            val capa = Path()
            capa.moveTo(0f, y)
            var x = 0f
            var k = 0
            while (x < w) {
                capa.quadraticTo(x + 22 * u, y + (azar(31 + j, k) - 0.5f) * 14 * u, x + 44 * u, y + (azar(37 + j, k) - 0.5f) * 4 * u)
                x += 44 * u; k++
            }
            drawPath(capa, p.tinta.copy(alpha = 0.045f), style = Stroke(1f * u))
        }
        for (j in 0 until 7) drawCircle(p.acento.copy(alpha = 0.28f), 2.3f * u, Offset(w / 2 + (j - 3) * 10 * u, 62 * u + (j % 2) * 2 * u))
        // Las rayas de cuenta en el margen: cuatro de pie y una que las cruza, cada 64 dp.
        var y0 = 90 * u
        var g = 0
        while (y0 < h - 120 * u) {
            val x0 = 8 * u
            for (i in 0 until 4) drawLine(p.tinta.copy(alpha = 0.16f), Offset(x0 + i * 3.4f * u, y0), Offset(x0 + i * 3.4f * u + 0.6f * u, y0 + 13 * u), 1.3f * u, StrokeCap.Round)
            if (g % 3 != 2) drawLine(p.tinta.copy(alpha = 0.16f), Offset(x0 - 2 * u, y0 + 11 * u), Offset(x0 + 13 * u, y0 + 2 * u), 1.3f * u, StrokeCap.Round)
            y0 += 64 * u; g++
        }
        ciervo(Offset(52 * u, h - 52 * u), 46 * u, p.tinta.copy(alpha = 0.12f))
        // La mano en negativo: el ocre soplado en puntos (más denso cerca de la mano) y la mano del color de la piedra.
        val cm = Offset(w - 48 * u, h - 74 * u)
        for (j in 0 until 220) {
            val a = azar(71, j) * 6.283f
            val r = 44 * u * kotlin.math.sqrt(azar(73, j))
            val o = Offset(cm.x + r * cos(a), cm.y + 8 * u + r * sin(a))
            drawCircle(p.acento.copy(alpha = 0.2f * (1f - r / (46 * u))), (0.8f + 1.6f * azar(79, j)) * u, o)
        }
        mano(cm, 22 * u, p.color, 5)
        if (noche) {
            val luna = Offset(w - 40 * u, h - 160 * u)
            drawCircle(p.acento.copy(alpha = 0.4f), 13 * u, luna)
            drawCircle(p.color, 11 * u, Offset(luna.x + 6 * u, luna.y - 4 * u))
        }
        if (oscuro && !p.oscura) penumbraAnt(Offset(w * 0.06f, h * 1.02f), FUEGO, 0.24f)
    }

    /**
     * La visera de la roca sobre la página: una franja de caliza (un degradado, más honda arriba) con el borde de abajo
     * desportillado, sus capas y una hilera de puntos de ocre pintados en el canto.
     */
    override fun DrawScope.barra(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val base = if (p.oscura) mezcla(p.color, p.tinta, 0.07f) else tonoAnt(CALIZAS[3], p, oscuro)
        val honda = if (p.oscura) mezcla(p.color, Color.Black, 0.35f) else mezcla(base, ROCA_OSC, 0.45f)
        val borde = Path().apply {
            moveTo(0f, 0f); lineTo(w, 0f); lineTo(w, h - 4 * u)
            var x = w
            var k = 0
            while (x > 0f) {
                x -= (10 + 14 * azar(17, k)) * u
                lineTo(maxOf(x, 0f), h - (1 + 5 * azar(19, k)) * u)
                k++
            }
            close()
        }
        drawPath(borde, Brush.verticalGradient(0f to honda, 0.45f to base, 1f to base, startY = 0f, endY = h))
        for (j in 0 until 2) {
            val y = h * (0.22f + 0.2f * j)
            drawLine(p.tinta.copy(alpha = 0.06f), Offset(0f, y), Offset(w, y + (azar(23, j) - 0.5f) * 4 * u), 0.8f * u)
        }
        drawPath(borde, p.tinta.copy(alpha = 0.35f), style = Stroke(1.2f * u))
        var x = 30 * u
        var k = 0
        while (x < w - 30 * u) {
            drawCircle(p.acento.copy(alpha = 0.45f), 1.6f * u, Offset(x, h - 9 * u + (k % 2) * 1.5f * u))
            x += 13 * u; k++
        }
    }

    /**
     * El botón de guardar: un canto rodado pintado con ocre, de contorno irregular, con su sombra plana, la luz arriba (un
     * degradado), el borde de carbón y dos puntos de ocre amarillo en las puntas.
     */
    override fun DrawScope.botonGuardar(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val r = Rect(2 * u, 2 * u, w - 3 * u, h - 4 * u)
        val canto = contornoIrregular(r, 11, 30, 0.07f)
        translate(1.5f * u, 2.5f * u) { drawPath(canto, Color.Black.copy(alpha = 0.32f)) }
        val ocre = if (oscuro) mezcla(OCRE, Color.Black, 0.12f) else OCRE
        drawPath(canto, Brush.verticalGradient(listOf(mezcla(ocre, OCRE_AMARILLO, 0.4f), ocre, mezcla(ocre, Color.Black, 0.28f)), r.top, r.bottom))
        drawPath(canto, CARBON.copy(alpha = 0.55f), style = Stroke(1f * u))
        listOf(r.left + 7 * u, r.right - 7 * u).forEach { x -> drawCircle(mezcla(OCRE_AMARILLO, FUEGO, 0.4f), 2.2f * u, Offset(x, r.center.y)) }
    }

    override fun tintaBoton(p: Pagina, oscuro: Boolean, noche: Boolean): Color = CALIZAS[0]

    // ---------------------------------------------------------------- la libreta de momentos

    override val altoLibreta: Dp = 250.dp

    /**
     * El color de la libreta con su luz: de día, tal cual; en modo oscuro, hundido en la cueva salvo donde llega el fuego
     * ([luz], de 0 lejos a 1 junto a las llamas, que además lo entibia).
     */
    private fun lc(x: Color, oscuro: Boolean, luz: Float = 0f): Color =
        if (!oscuro) x else mezcla(mezcla(x, Color(0xFF0A0705), 0.72f - 0.44f * luz), BRASA, 0.16f * luz)

    /**
     * La libreta de momentos: la bolsa de piel en el abrigo, sobre una laja plana junto al fogón, con un hilo de cuentas de
     * concha, un terrón de ocre con su molejón, un punzón de hueso y una cornamenta apoyada en la roca. La bolsa se hincha con
     * las pieles guardadas (asoman más por la boca) y lleva colgada del cordón una piel raspada con el nombre en ocre y la
     * cuenta en carbón. Al abrirse, el cordón se afloja y baja, la boca se abre y las pieles enrolladas suben. En modo oscuro
     * la cueva se apaga y solo alumbra el fuego. Un degradado: el resplandor del fogón.
     */
    override fun DrawScope.libreta(e: EstadoLibreta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val a = e.abierto.coerceIn(0f, 1f)
        val gordo = (kotlin.math.sqrt(e.hojas.coerceAtLeast(1).toFloat()) / 14f).coerceIn(0.07f, 1f)
        val s = e.semilla
        fun c(x: Color, luz: Float = 0f) = lc(x, oscuro, luz)
        // La pared del abrigo: la roca honda con sus capas onduladas, sus grietas, las estalactitas del techo y unos puntos
        // de ocre ya viejos.
        drawRect(c(ROCA_HONDA, 0.12f))
        var y = h * 0.05f
        var k = 0
        while (y < h * 0.72f) {
            val capa = Path(); capa.moveTo(0f, y)
            var x = 0f; var j = 0
            while (x < w) { capa.quadraticTo(x + 18 * u, y + (azar(k * 31 + j, 4) - 0.5f) * 9 * u, x + 36 * u, y); x += 36 * u; j++ }
            if (k % 3 == 1) drawPath(Path().apply { addPath(capa); lineTo(w, y + 7 * u); lineTo(0f, y + 7 * u); close() }, c(ROCA_OSC, 0.12f).copy(alpha = 0.35f))
            drawPath(capa, c(ROCA_OSC, 0.12f).copy(alpha = 0.6f), style = Stroke(1.1f * u))
            y += (16 + 10 * azar(k, 9)) * u; k++
        }
        for (j in 0 until 3) {
            val x0 = w * (0.36f + 0.24f * j + 0.06f * azar(j, 3))
            val grieta = Path().apply { moveTo(x0, 0f); for (q in 1..7) lineTo(x0 + (azar(j * 13 + q, 5) - 0.5f) * 12 * u, h * 0.08f * q) }
            drawPath(grieta, Color.Black.copy(alpha = 0.3f), style = Stroke(0.9f * u))
        }
        drawRect(c(ROCA_OSC, 0.1f), Offset.Zero, Size(w, 9 * u))
        for (j in 0 until 9) {
            val x0 = w * (0.05f + 0.11f * j + 0.04f * azar(j, 21)); val largo = (8 + 22 * azar(j, 22)) * u; val ancho = (3 + 4 * azar(j, 23)) * u
            drawPath(Path().apply { moveTo(x0 - ancho, 8 * u); quadraticTo(x0 - ancho * 0.3f, 8 * u + largo * 0.6f, x0, 8 * u + largo); quadraticTo(x0 + ancho * 0.3f, 8 * u + largo * 0.6f, x0 + ancho, 8 * u); close() },
                c(if (j % 2 == 0) ROCA else LAJA_OSC, 0.1f))
            drawCircle(c(ROCA_LUZ, 0.1f).copy(alpha = 0.7f), 1.1f * u, Offset(x0, 8 * u + largo + 2 * u))
        }
        for (f in 0 until 2) for (j in 0 until 6) {
            drawCircle(c(OCRE, 0.1f).copy(alpha = 0.55f), 2.2f * u, Offset(w * (0.4f + 0.035f * j), h * (0.16f + 0.06f * f) + (j % 2) * 2 * u))
        }
        // El suelo de tierra, con guijarros.
        val suelo = h * 0.8f
        drawRect(c(TIERRA, 0.35f), Offset(0f, suelo), Size(w, h - suelo))
        drawRect(Color.Black.copy(alpha = 0.18f), Offset(0f, suelo), Size(w, 3 * u))
        for (j in 0 until 16) {
            val p = Offset(w * azar(s, 300 + j), suelo + (h - suelo) * (0.2f + 0.75f * azar(s, 320 + j)))
            drawOval(c(if (j % 2 == 0) ROCA else LAJA_OSC, 0.4f), Offset(p.x - 3 * u, p.y - 1.6f * u), Size(6 * u, 3.2f * u))
        }
        // El resplandor del fogón sobre la pared y el suelo (el único degradado).
        val fuego = Offset(w * 0.1f, h * 0.83f)
        mancha(fuego, if (oscuro) w * 0.95f else w * 0.42f, (if (oscuro) BRASA else FUEGO).copy(alpha = if (oscuro) 0.42f else 0.3f))
        // La cornamenta apoyada contra la roca, a la derecha.
        val hueso = c(ROCA_LUZ, 0.3f)
        val base = Offset(w * 0.93f, h * 0.66f)
        drawLine(hueso, base, Offset(w * 0.86f, h * 0.2f), 3.4f * u, StrokeCap.Round)
        for (j in 1..4) {
            val p = Offset(base.x + (w * 0.86f - base.x) * j / 5f, base.y + (h * 0.2f - base.y) * j / 5f)
            drawLine(hueso, p, Offset(p.x + (if (j % 2 == 0) -1 else 1) * w * 0.05f, p.y - h * 0.07f), 2.2f * u, StrokeCap.Round)
        }
        // La laja-mesa: el canto oscuro y la cara de arriba, alisada, con sus capas.
        val canto = contornoIrregular(Rect(w * 0.15f, h * 0.68f, w * 1.01f, h * 0.9f), s + 11, 40, 0.06f)
        drawPath(canto, c(ROCA_OSC, 0.55f))
        clipPath(canto) {
            for (j in 0 until 3) drawLine(Color.Black.copy(alpha = 0.22f), Offset(w * 0.15f, h * (0.77f + 0.04f * j)), Offset(w, h * (0.78f + 0.035f * j)), 1 * u)
            drawRect(Color.Black.copy(alpha = 0.2f), Offset(w * 0.15f, h * 0.84f), Size(w, h * 0.06f))
        }
        val cara = contornoIrregular(Rect(w * 0.16f, h * 0.62f, w * 1f, h * 0.77f), s + 12, 44, 0.05f)
        drawPath(cara, c(LAJA, 0.55f))
        clipPath(cara) {
            drawRect(Color.White.copy(alpha = 0.12f), Offset(w * 0.16f, h * 0.62f), Size(w, h * 0.03f))
            for (j in 0 until 30) drawCircle(c(LAJA_OSC, 0.55f).copy(alpha = 0.5f), (0.6f + azar(s, 40 + j)) * u, Offset(w * (0.18f + 0.8f * azar(s, 80 + j)), h * (0.63f + 0.13f * azar(s, 120 + j))))
        }
        drawPath(cara, c(ROCA_OSC, 0.5f).copy(alpha = 0.7f), style = Stroke(1 * u))
        // A la izquierda de la bolsa: el molejón y el terrón de ocre con su polvo.
        drawOval(c(ROCA, 0.7f), Offset(w * 0.17f, h * 0.665f), Size(w * 0.12f, h * 0.06f))
        drawOval(Color.Black.copy(alpha = 0.2f), Offset(w * 0.19f, h * 0.68f), Size(w * 0.08f, h * 0.03f))
        drawCircle(c(OCRE, 0.8f).copy(alpha = 0.5f), 4 * u, Offset(w * 0.23f, h * 0.693f))
        drawPath(contornoIrregular(Rect(w * 0.27f, h * 0.67f, w * 0.31f, h * 0.71f), s + 5, 10, 0.18f), c(OCRE, 0.75f))
        // El fogón: el cerco de piedras, la leña cruzada, las llamas y las chispas.
        for (j in 0 until 7) {
            val px = w * (0.005f + 0.035f * j); val py = h * (0.9f + 0.035f * (j % 2))
            val piedra = contornoIrregular(Rect(px, py, px + w * 0.05f, py + h * 0.06f), s + 60 + j, 12, 0.14f)
            drawPath(piedra, c(if (j % 2 == 0) ROCA else LAJA_OSC, 1f))
            drawRect(Color.Black.copy(alpha = 0.18f), Offset(px, py + h * 0.035f), Size(w * 0.05f, h * 0.03f))
        }
        val lena = c(LENA, 0.9f)
        drawLine(lena, Offset(w * 0.02f, h * 0.92f), Offset(w * 0.19f, h * 0.84f), 5 * u, StrokeCap.Round)
        drawLine(lena, Offset(w * 0.18f, h * 0.93f), Offset(w * 0.03f, h * 0.84f), 5 * u, StrokeCap.Round)
        drawLine(c(BRASA, 1f), Offset(w * 0.05f, h * 0.9f), Offset(w * 0.15f, h * 0.86f), 1.4f * u, StrokeCap.Round)
        val vaiven = (azar(s, 7) - 0.5f) * 0.03f
        fun llama(ancho: Float, alto: Float, color: Color) = drawPath(Path().apply {
            moveTo(fuego.x - ancho, h * 0.89f)
            cubicTo(fuego.x - ancho * 1.1f, h * 0.89f - alto * 0.5f, fuego.x - ancho * 0.2f, h * 0.89f - alto * 0.6f, fuego.x + w * vaiven, h * 0.89f - alto)
            cubicTo(fuego.x + ancho * 0.3f, h * 0.89f - alto * 0.55f, fuego.x + ancho * 1.2f, h * 0.89f - alto * 0.45f, fuego.x + ancho, h * 0.89f)
            close()
        }, color)
        llama(w * 0.07f, h * 0.3f, BRASA)
        llama(w * 0.05f, h * 0.22f, OCRE_AMARILLO)
        llama(w * 0.028f, h * 0.13f, FUEGO)
        for (j in 0 until 6) drawCircle(FUEGO.copy(alpha = 0.8f), (0.8f + azar(s, 200 + j)) * u, Offset(w * (0.05f + 0.12f * azar(s, 210 + j)), h * (0.42f + 0.18f * azar(s, 220 + j))))
        // La bolsa: se hincha con las pieles guardadas.
        val bx = w * 0.55f; val by = h * 0.705f
        val bw = w * (0.36f + 0.2f * gordo); val bh = h * (0.5f + 0.08f * gordo)
        val cuello = by - bh * 0.78f
        val boca = bw * (0.28f + 0.2f * a)
        val piel = c(Color(0xFF8A6440), 0.45f)
        val pielOsc = c(Color(0xFF5E4026), 0.45f)
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(bx - bw * 0.52f, by - h * 0.03f), Size(bw * 1.04f, h * 0.06f))
        val cuerpo = Path().apply {
            moveTo(bx - boca / 2, cuello)
            cubicTo(bx - bw * 0.72f, cuello + bh * 0.2f, bx - bw * 0.64f, by, bx - bw * 0.2f, by)
            lineTo(bx + bw * 0.2f, by)
            cubicTo(bx + bw * 0.64f, by, bx + bw * 0.72f, cuello + bh * 0.2f, bx + boca / 2, cuello)
            close()
        }
        drawPath(cuerpo, piel)
        clipPath(cuerpo) {
            drawOval(Color.White.copy(alpha = 0.1f), Offset(bx - bw * 0.48f, cuello + bh * 0.12f), Size(bw * 0.42f, bh * 0.55f))
            drawOval(Color.Black.copy(alpha = 0.22f), Offset(bx + bw * 0.14f, cuello + bh * 0.05f), Size(bw * 0.5f, bh * 0.9f))
            drawRect(Color.Black.copy(alpha = 0.18f), Offset(bx - bw, by - bh * 0.1f), Size(bw * 2, bh * 0.1f))
            // Arrugas de la piel, la mano pintada con ocre y una fila de puntos.
            for (j in 0 until 5) {
                val yy = cuello + bh * (0.25f + 0.13f * j)
                drawPath(Path().apply { moveTo(bx - bw * 0.5f, yy); quadraticTo(bx, yy + (6 + 4 * azar(s, 400 + j)) * u, bx + bw * 0.5f, yy - 2 * u) },
                    pielOsc.copy(alpha = 0.35f), style = Stroke(0.9f * u))
            }
            mano(Offset(bx - bw * 0.22f, cuello + bh * 0.34f), bh * 0.15f, c(OCRE, 0.45f).copy(alpha = 0.85f), s)
            for (j in 0 until 7) drawCircle(c(OCRE_AMARILLO, 0.45f), 1.8f * u, Offset(bx - bw * 0.3f + j * bw * 0.1f, cuello + bh * 0.13f + (j % 2) * 2 * u))
        }
        // La costura del costado.
        drawPath(Path().apply { moveTo(bx + boca / 2 + 2 * u, cuello + 6 * u); cubicTo(bx + bw * 0.62f, cuello + bh * 0.24f, bx + bw * 0.58f, by - 6 * u, bx + bw * 0.22f, by - 3 * u) },
            c(Color(0xFFD9C29A), 0.45f), style = Stroke(1.1f * u, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(3 * u, 3 * u))))
        drawPath(cuerpo, pielOsc, style = Stroke(1.2f * u))
        // La boca fruncida: las solapas se abren hacia afuera y las pieles enrolladas suben.
        val abre = bw * (0.06f + 0.26f * a)
        val alto = bh * (0.2f - 0.07f * a)
        val izq = bx - boca / 2 - abre; val der = bx + boca / 2 + abre
        val arriba = cuello - alto
        val solapa = Path().apply {
            moveTo(bx - boca / 2, cuello + 2 * u); lineTo(izq, arriba + 4 * u)
            var x = izq; var j = 0
            while (x < der - 1) { val nx = minOf(der, x + (der - izq) / 6f); quadraticTo((x + nx) / 2, arriba - (if (j % 2 == 0) 5 else 1) * u, nx, arriba + 3 * u); x = nx; j++ }
            lineTo(bx + boca / 2, cuello + 2 * u); close()
        }
        drawPath(solapa, mezcla(piel, pielOsc, 0.4f))
        for (j in 0 until 6) drawLine(pielOsc.copy(alpha = 0.6f), Offset(bx - boca / 2 + j * boca / 5f, cuello), Offset(izq + j * (der - izq) / 5f, arriba + 5 * u), 0.9f * u)
        val hondo = Rect(izq + 4 * u, arriba - 1 * u, der - 4 * u, arriba + (6 + 12 * a) * u)
        drawOval(c(Color(0xFF1E140C), 0.3f), hondo.topLeft, hondo.size)
        val pieles = (1 + e.hojas / 25).coerceIn(1, 6)
        val sube = bh * (0.05f + 0.05f * gordo + 0.3f * a)
        for (j in 0 until pieles) {
            val px = hondo.left + hondo.width * (j + 0.5f) / pieles
            val ancho = (7 + 5 * azar(s, 500 + j)) * u
            val tope = hondo.center.y - sube * (0.6f + 0.4f * azar(s, 510 + j))
            rotate((j - (pieles - 1) / 2f) * (7f + 10f * a), Offset(px, hondo.center.y)) {
                val rollo = c(CALIZAS[j % CALIZAS.size], 0.45f)
                drawRoundRect(rollo, Offset(px - ancho / 2, tope), Size(ancho, hondo.center.y - tope + 2 * u), CornerRadius(ancho / 2))
                drawOval(mezcla(rollo, pielOsc, 0.35f), Offset(px - ancho / 2, tope), Size(ancho, ancho * 0.55f))
                drawOval(pielOsc.copy(alpha = 0.6f), Offset(px - ancho * 0.25f, tope + ancho * 0.12f), Size(ancho * 0.5f, ancho * 0.3f), style = Stroke(0.7f * u))
                drawLine(c(OCRE, 0.45f).copy(alpha = 0.6f), Offset(px - ancho * 0.3f, tope + ancho), Offset(px + ancho * 0.3f, tope + ancho * 1.6f), 1 * u)
            }
        }
        drawPath(Path().apply { moveTo(hondo.left, hondo.center.y); quadraticTo(hondo.center.x, hondo.bottom + 4 * u, hondo.right, hondo.center.y) },
            mezcla(piel, pielOsc, 0.25f), style = Stroke(3 * u))
        // El cordón de tendón: ceñido al cuello; al abrir se afloja y baja. Del nudo cuelgan dos cuentas de concha.
        val tendon = c(Color(0xFFD9C29A), 0.45f)
        val baja = bh * 0.12f * a
        val nudo = Offset(bx + boca / 2 + 3 * u, cuello + 4 * u + baja)
        drawPath(Path().apply { moveTo(bx - boca / 2 - 3 * u, cuello + 3 * u + baja * 0.7f); quadraticTo(bx, cuello + 9 * u + baja * 1.4f, nudo.x, nudo.y) }, tendon, style = Stroke(2.2f * u))
        drawPath(Path().apply { moveTo(bx - boca / 2 - 2 * u, cuello + 7 * u + baja * 0.7f); quadraticTo(bx, cuello + 13 * u + baja * 1.4f, nudo.x, nudo.y + 2 * u) }, tendon, style = Stroke(1.6f * u))
        drawCircle(tendon, 3 * u, nudo)
        listOf(Offset(nudo.x + w * 0.05f, nudo.y + h * 0.1f), Offset(nudo.x + w * 0.075f, nudo.y + h * 0.06f)).forEach { p ->
            drawLine(tendon, nudo, p, 1.3f * u, StrokeCap.Round)
            drawCircle(c(Color(0xFFF2E8D6), 0.45f), 2.8f * u, p)
            drawCircle(Color.Black.copy(alpha = 0.3f), 0.8f * u, p)
        }
        // La piel raspada colgada del cordón: el nombre en ocre y la cuenta en carbón.
        val anchoEt = maxOf(w * 0.42f, bw * 0.78f)
        val et = Rect(bx - anchoEt / 2 + w * 0.02f, by - bh * 0.44f, bx + anchoEt / 2 + w * 0.02f, by - bh * 0.44f + h * 0.2f)
        drawLine(tendon, nudo, Offset(et.right - et.width * 0.2f, et.top + 4 * u), 1.4f * u)
        rotate(-2f + 4f * a, Offset(et.right - et.width * 0.2f, et.top)) {
            val forma = contornoIrregular(et, s + 31, 28, 0.06f)
            translate(2 * u, 3 * u) { drawPath(forma, Color.Black.copy(alpha = 0.3f)) }
            val raspada = c(CALIZAS[0], 0.85f)
            drawPath(forma, raspada)
            clipPath(forma) {
                for (j in 0 until 18) drawCircle(c(LAJA_OSC, 0.5f).copy(alpha = 0.35f), (0.5f + azar(s, 600 + j)) * u, Offset(et.left + et.width * azar(s, 620 + j), et.top + et.height * azar(s, 640 + j)))
            }
            drawPath(forma, c(Color(0xFF8A6440), 0.5f), style = Stroke(1.2f * u))
            drawCircle(c(ROCA_HONDA, 0.3f), 2 * u, Offset(et.right - et.width * 0.2f, et.top + 4 * u))
            textoEn(medidor, e.nombre, Rect(et.left + 8 * u, et.top + et.height * 0.12f, et.right - 8 * u, et.top + et.height * 0.58f), LetraExtra.trazo,
                legibleAnt(raspada, c(OCRE, 0.3f), OCRE_AMARILLO), 16f, peso = FontWeight.Bold, minimo = 8f)
            textoEn(medidor, e.cuenta, Rect(et.left + 8 * u, et.top + et.height * 0.58f, et.right - 8 * u, et.top + et.height * 0.88f), letra,
                legibleAnt(raspada, c(CARBON, 0.3f), CALIZAS[0]), 10.5f, minimo = 6.5f)
        }
        // Delante, sobre la laja: el punzón de hueso; a la derecha, el hilo de cuentas de concha que cuelga por el canto.
        drawLine(c(ROCA_LUZ, 0.6f), Offset(w * 0.3f, h * 0.745f), Offset(w * 0.47f, h * 0.73f), 2.6f * u, StrokeCap.Round)
        drawLine(c(ROCA_LUZ, 0.6f), Offset(w * 0.47f, h * 0.73f), Offset(w * 0.52f, h * 0.728f), 1 * u, StrokeCap.Round)
        drawCircle(c(ROCA_LUZ, 0.6f), 3.2f * u, Offset(w * 0.3f, h * 0.745f))
        val hilo = Path().apply { moveTo(w * 0.8f, h * 0.66f); cubicTo(w * 0.9f, h * 0.7f, w * 0.96f, h * 0.69f, w * 0.95f, h * 0.73f); quadraticTo(w * 0.93f, h * 0.8f, w * 0.97f, h * 0.86f) }
        drawPath(hilo, tendon.copy(alpha = 0.7f), style = Stroke(0.9f * u))
        val medida = androidx.compose.ui.graphics.PathMeasure().apply { setPath(hilo, false) }
        val n = 16
        for (j in 0..n) {
            val p = medida.getPosition(medida.length * j / n)
            drawCircle(c(if (j % 3 == 2) OCRE else Color(0xFFF2E8D6), 0.4f), (if (j % 3 == 2) 2.2f else 2.8f) * u, p)
            drawCircle(Color.Black.copy(alpha = 0.25f), 0.7f * u, p)
        }
    }

    override fun DrawScope.pestana(e: EstadoPestana, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val s = e.semilla
        // En modo oscuro la piel se hunde y la pintura se aclara, para que el día se lea.
        val piel = lc(CALIZAS[(s and 0x7fffffff) % CALIZAS.size], oscuro, 0.3f)
        val pintura = if (oscuro) mezcla(OCRE_AMARILLO, FUEGO, 0.4f) else OCRE
        val carbon = if (oscuro) CALIZAS[0].copy(alpha = 0.85f) else CARBON
        val tendon = lc(Color(0xFFB8A27A), oscuro, 0.55f)
        rotate((azar(s, 1) - 0.5f) * 6f) {
            val r = Rect(w * 0.08f, h * 0.1f, w * 0.92f, h * 0.97f)
            val forma = contornoIrregular(r, s, 22, 0.05f)
            translate(1.5f * u, 2 * u) { drawPath(forma, Color.Black.copy(alpha = 0.28f)) }
            drawPath(forma, piel)
            clipPath(forma) {
                for (j in 0 until 8) drawCircle(lc(LAJA_OSC, oscuro, 0.55f).copy(alpha = 0.4f), (0.5f + 0.6f * azar(s, 10 + j)) * u, Offset(w * azar(s, 20 + j), h * azar(s, 30 + j)))
                if (e.tono != null) {
                    // El color del ánimo: una franja pintada con el dedo al pie de la piel.
                    drawRect(e.tono, Offset(0f, h * 0.8f), Size(w, h * 0.2f))
                    drawPath(Path().apply { moveTo(0f, h * 0.8f); quadraticTo(w * 0.3f, h * 0.76f, w * 0.55f, h * 0.8f); quadraticTo(w * 0.8f, h * 0.84f, w, h * 0.79f) },
                        e.tono, style = Stroke(2.4f * u))
                }
            }
            drawPath(forma, lc(Color(0xFF8A6440), oscuro, 0.55f), style = Stroke(1 * u))
            drawCircle(lc(ROCA_HONDA, oscuro, 0.4f), 2 * u, Offset(w / 2, h * 0.19f))
            drawLine(tendon, Offset(w / 2, 0f), Offset(w / 2, h * 0.19f), 1.6f * u)
            textoEn(medidor, e.dia, Rect(r.left, h * 0.24f, r.right, h * 0.6f), LetraExtra.trazo, pintura, 21f,
                peso = FontWeight.Bold, minimo = 10f)
            textoEn(medidor, e.semana, Rect(r.left, h * 0.59f, r.right, h * 0.76f), letra, carbon, 10f, minimo = 7f)
            if (e.tono == null) for (j in 0 until 3) drawLine(carbon.copy(alpha = 0.6f), Offset(w * (0.38f + 0.12f * j), h * 0.82f), Offset(w * (0.39f + 0.12f * j), h * 0.92f), 1.3f * u, StrokeCap.Round)
        }
    }

    override fun DrawScope.rotuloMes(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val s = texto.hashCode()
        val r = Rect(3 * u, 2 * u, w - 3 * u, h - 3 * u)
        val forma = contornoIrregular(r, s, 70, 0.07f)
        val piedra = lc(LAJA, oscuro, 0.5f)
        translate(2 * u, 2 * u) { drawPath(forma, Color.Black.copy(alpha = 0.3f)) }
        drawPath(forma, piedra)
        clipPath(forma) {
            drawRect(Color.White.copy(alpha = 0.12f), r.topLeft, Size(r.width, h * 0.18f))
            drawRect(Color.Black.copy(alpha = 0.16f), Offset(r.left, h * 0.78f), Size(r.width, h * 0.22f))
            drawLine(lc(LAJA_OSC, oscuro, 0.5f).copy(alpha = 0.5f), Offset(0f, h * 0.4f), Offset(w, h * 0.46f), 0.8f * u)
            for (j in 0 until 40) drawCircle(lc(LAJA_OSC, oscuro, 0.5f).copy(alpha = 0.45f), (0.5f + azar(s, j)) * u, Offset(w * azar(s, 50 + j), h * azar(s, 100 + j)))
        }
        drawPath(forma, lc(ROCA_OSC, oscuro, 0.5f), style = Stroke(1 * u))
        val ocre = legibleAnt(piedra, lc(OCRE, oscuro, 0.5f), OCRE_AMARILLO)
        val carbon = legibleAnt(piedra, lc(CARBON, oscuro, 0.5f), CALIZAS[0])
        // Los puntos de ocre y las rayas de cuenta de carbón a los dos lados.
        for (j in 0 until 4) {
            drawCircle(ocre, 2.2f * u, Offset(w * 0.05f + j * 7 * u, h * 0.5f + (j % 2) * 2 * u))
            drawCircle(ocre, 2.2f * u, Offset(w * 0.95f - j * 7 * u, h * 0.5f + (j % 2) * 2 * u))
        }
        for (j in 0 until 5) {
            val x0 = w * 0.05f + 32 * u + j * 3.4f * u; val x1 = w * 0.95f - 32 * u - j * 3.4f * u
            if (j < 4) { drawLine(carbon, Offset(x0, h * 0.3f), Offset(x0, h * 0.7f), 1.3f * u); drawLine(carbon, Offset(x1, h * 0.3f), Offset(x1, h * 0.7f), 1.3f * u) }
        }
        textoEn(medidor, texto, Rect(w * 0.05f + 52 * u, 0f, w * 0.95f - 52 * u, h), LetraExtra.trazo, ocre, 16f, peso = FontWeight.Bold, minimo = 8f)
    }

    // ---------------------------------------------------------------- las cartas

    private val PIEL = Color(0xFFCDB089)
    private val PIEL_LUZ = Color(0xFFDDC29A)
    private val PIEL_OSC = Color(0xFF8C6B48)
    private val PIEL_CRUDA = Color(0xFFE8DCC4)
    private val PELO = Color(0xFF5E4630)
    private val TENDON = Color(0xFFE2CFA6)
    private val TENDON_OSC = Color(0xFF9C8460)

    /** El color de las cartas con su luz: de día, tal cual; en modo oscuro, hundido en la penumbra de la cueva. */
    private fun cc(x: Color, oscuro: Boolean): Color = if (oscuro) mezcla(x, Color(0xFF0F0B08), 0.55f) else x

    /**
     * El sello de la cueva, centrado en [c]: un disco de arcilla teñida de ocre, aplastado con la palma; en medio, la mano en
     * negativo de quien lo cerró (la arcilla sin teñir, con el ocre soplado alrededor) y una corona de diez puntos de carbón.
     */
    private fun DrawScope.selloCueva(c: Offset, r: Float, oscuro: Boolean, semilla: Int) {
        // La arcilla se hunde menos que la piel en modo oscuro, para que el sello siga leyéndose.
        fun k(x: Color) = if (oscuro) mezcla(x, Color(0xFF0F0B08), 0.3f) else x
        val disco = discoAnt(c, r, semilla, 22, 0.07f)
        translate(r * 0.08f, r * 0.14f) { drawPath(disco, Color.Black.copy(alpha = 0.32f)) }
        drawPath(disco, k(OCRE))
        drawPath(discoAnt(c, r * 0.8f, semilla + 3, 18, 0.06f), k(mezcla(OCRE, Color.Black, 0.2f)), style = Stroke(r * 0.07f))
        // El ocre soplado alrededor de la mano: puntitos más oscuros.
        for (j in 0 until 26) {
            val a = (j * 2.4f) + azar(semilla, j) * 0.6f
            val d = r * (0.36f + 0.3f * azar(semilla, j + 40))
            drawCircle(k(Color(0xFF7E2E14)).copy(alpha = 0.7f), r * 0.035f, Offset(c.x + d * cos(a), c.y + d * sin(a)))
        }
        mano(Offset(c.x, c.y - r * 0.5f), r * 0.36f, k(PIEL_CRUDA), semilla)
        for (j in 0 until 10) {
            val a = (j * Math.PI / 5).toFloat()
            drawCircle(k(CARBON), r * 0.055f, Offset(c.x + r * 0.84f * cos(a), c.y + r * 0.84f * sin(a)))
        }
        drawPath(disco, k(Color(0xFF5A2410)), style = Stroke(r * 0.05f))
    }

    /** El borde de pelo de una piel: pelitos cortos a lo largo de [p], hacia afuera (los pinta un trazo punteado grueso). */
    private fun DrawScope.peloPiel(p: Path, color: Color, grosor: Float) {
        val u = density
        drawPath(p, color, style = Stroke(grosor, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.1f * u, 1.7f * u))))
    }

    /**
     * La carta de la cueva: un hatillo de piel curtida, doblada sobre sí misma con una solapa redonda de pelo en la orilla, y
     * dentro la carta en una piel raspada. Por fuera, un ciervo de carbón y puntos de ocre pintados en la solapa. Las de al
     * futuro se atan con dos vueltas de tendón ([EstadoCarta.cordon]) y se cierran con el disco de arcilla de la mano en
     * negativo; al abrirse el disco se parte, la solapa se levanta por detrás y la piel raspada sube. A quién va y las fechas
     * se pintan con carbón al pie de la piel (en modo oscuro, con la arcilla clara). Un degradado: el brillo del sello.
     */
    override fun DrawScope.sobre(e: EstadoCarta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val q = e.cuerpo
        val W = q.width; val H = q.height; val cx = q.center.x
        val ab = e.abrir.coerceIn(0f, 1f)
        val s = e.semilla
        fun c(x: Color) = cc(x, oscuro)
        val caja = Rect(q.left + W * 0.03f, q.top + H * 0.05f, q.right - W * 0.03f, q.bottom - H * 0.05f)
        val atras = contornoIrregular(caja, s, 30, 0.022f)
        // La piel de atrás, con su sombra en el suelo.
        translate(2.5f * u, 3.5f * u) { drawPath(atras, Color.Black.copy(alpha = 0.28f)) }
        drawPath(atras, c(PIEL_OSC))
        // La boca del hatillo: sombra honda entre las dos caras cuando se abre.
        val boca = caja.top + H * 0.16f
        if (ab > 0f) clipPath(atras) {
            drawOval(c(Color(0xFF2A1C12)).copy(alpha = 0.85f * ab), Offset(caja.left + W * 0.04f, caja.top - H * 0.02f), Size(caja.width - W * 0.08f, boca - caja.top + H * 0.08f))
        }
        // La solapa: una curva redonda que baja hasta el sello; al abrirse se voltea y queda de pie detrás de la carta.
        val k = 1f - 2f * ab
        val puntaCerrada = q.top + H * 0.44f
        val puntaY = caja.top + (puntaCerrada - caja.top) * k
        val solapa = Path().apply {
            moveTo(caja.left + W * 0.005f, caja.top + H * 0.02f)
            cubicTo(caja.left + W * 0.04f, caja.top + (puntaY - caja.top) * 0.95f, cx - W * 0.26f, puntaY, cx, puntaY)
            cubicTo(cx + W * 0.26f, puntaY, caja.right - W * 0.04f, caja.top + (puntaY - caja.top) * 0.95f, caja.right - W * 0.005f, caja.top + H * 0.02f)
            close()
        }
        if (k < 0f) {
            // Por dentro la piel está raspada y clara, con las marcas del raspador.
            drawPath(solapa, c(PIEL_CRUDA))
            clipPath(solapa) { for (j in 0 until 7) drawLine(c(PIEL_OSC).copy(alpha = 0.25f), Offset(caja.left + W * (0.1f + 0.12f * j), caja.top), Offset(caja.left + W * (0.16f + 0.12f * j), puntaY), 0.8f * u) }
            peloPiel(solapa, c(PELO), 3 * u)
            drawPath(solapa, c(PIEL_OSC), style = Stroke(1 * u))
        }
        // La carta: una piel raspada del color del papel, de orilla irregular.
        if (ab > 0.3f || e.subir > 0f) hojaCartaAnt(q, e.subir, e.hoja, e.renglones, oscuro, 0.13f, 0.87f, borde = s)
        // El frente del hatillo: la piel con su pelo, tapando la carta de la boca para abajo.
        clipRect(q.left - W, boca, q.right + W, q.bottom + H) {
            drawPath(atras, c(PIEL))
            clipPath(atras) {
                for (j in 0 until 16) drawCircle(c(PIEL_OSC).copy(alpha = 0.22f), (0.8f + 1.6f * azar(s, j)) * u, Offset(caja.left + caja.width * azar(s, j + 20), boca + (caja.bottom - boca) * azar(s, j + 40)))
                for (j in 0 until 5) {
                    val y = boca + (caja.bottom - boca) * (0.2f + 0.16f * j)
                    drawLine(c(PIEL_LUZ).copy(alpha = 0.5f), Offset(caja.left + W * 0.05f, y), Offset(caja.left + W * (0.3f + 0.1f * azar(s, j + 60)), y + 2 * u), 0.8f * u)
                }
            }
            peloPiel(atras, c(PELO), 3.2f * u)
            drawPath(atras, c(Color(0xFF6E5034)), style = Stroke(1 * u))
        }
        // La orilla de la boca: el doblez de la piel.
        drawLine(c(Color(0xFF5E4630)), Offset(caja.left + W * 0.01f, boca), Offset(caja.right - W * 0.01f, boca), 1.4f * u, StrokeCap.Round)
        drawLine(c(PIEL_LUZ).copy(alpha = 0.6f), Offset(caja.left + W * 0.03f, boca + 1.6f * u), Offset(caja.right - W * 0.03f, boca + 1.6f * u), 0.8f * u)
        if (k >= 0f) {
            // La solapa cerrada (o levantándose): la piel con su pelo, el ciervo de carbón y una hilera de puntos de ocre.
            drawPath(solapa, Color.Black.copy(alpha = 0.18f), style = Stroke(3 * u))
            drawPath(solapa, c(PIEL_LUZ))
            clipPath(solapa) {
                for (j in 0 until 10) drawCircle(c(PIEL_OSC).copy(alpha = 0.2f), (0.8f + 1.4f * azar(s, j + 80)) * u, Offset(caja.left + caja.width * azar(s, j + 90), caja.top + (puntaCerrada - caja.top) * azar(s, j + 100)))
                val visto = (1f - ab * 2.5f).coerceIn(0f, 1f)
                if (visto > 0f) {
                    ciervo(Offset(caja.left + W * 0.24f, caja.top + (puntaY - caja.top) * 0.42f), H * 0.1f, c(CARBON).copy(alpha = 0.7f * visto))
                    for (j in 0 until 5) drawCircle(c(OCRE).copy(alpha = 0.85f * visto), H * 0.02f, Offset(caja.right - W * (0.3f - 0.04f * j), caja.top + (puntaY - caja.top) * (0.3f + 0.04f * (j % 2))))
                }
            }
            peloPiel(solapa, c(PELO), 3.2f * u)
            drawPath(solapa, c(Color(0xFF6E5034)), style = Stroke(1 * u))
        }
        // El sello, en la punta de la solapa cerrada.
        val rS = minOf(H * 0.15f, W * 0.1f)
        val cS = Offset(cx, puntaCerrada)
        // Dos vueltas de tendón que ciñen el hatillo de lado a lado y se anudan bajo el sello.
        if (e.cordon > 0f) {
            listOf(-0.3f, 0.3f).forEachIndexed { i, dy ->
                val y = cS.y + rS * dy
                val av = ((e.cordon - i * 0.4f) / 0.6f).coerceIn(0f, 1f)
                val ruta = listOf(Offset(caja.left - W * 0.005f, y + 2 * u), Offset(caja.left + W * 0.25f, y - 1 * u), Offset(cx, y), Offset(caja.right - W * 0.25f, y + 1 * u), Offset(caja.right + W * 0.005f, y - 2 * u))
                trazoParcialAnt(ruta.map { it + Offset(0f, 1.2f * u) }, av, Color.Black.copy(alpha = 0.3f), 2.6f * u)
                trazoParcialAnt(ruta, av, c(TENDON), 2.2f * u)
                trazoParcialAnt(ruta, av, c(TENDON_OSC), 0.6f * u)
            }
            if (e.cordon > 0.9f) {
                // Las dos puntas del nudo, colgando bajo el sello.
                drawLine(c(TENDON), Offset(cx - rS * 0.3f, cS.y + rS * 0.6f), Offset(cx - rS * 0.6f, cS.y + rS * 1.35f), 2 * u, StrokeCap.Round)
                drawLine(c(TENDON), Offset(cx + rS * 0.3f, cS.y + rS * 0.6f), Offset(cx + rS * 0.5f, cS.y + rS * 1.4f), 2 * u, StrokeCap.Round)
            }
        }
        selloCartaAnt(cS, rS, e.brillo, e.romper.coerceIn(0f, 1f), e.escalaSello, e.alfaSello, FUEGO) { selloCueva(cS, rS, oscuro, s) }
        // A quién va y las fechas, pintados con carbón al pie de la piel.
        val tinta = legibleAnt(c(PIEL), c(CARBON), PIEL_CRUDA)
        lineasCartaAnt(medidor, e.linea1, e.linea2, Rect(caja.left + W * 0.07f, q.top + H * 0.63f, caja.right - W * 0.07f, q.top + H * 0.79f),
            Rect(caja.left + W * 0.1f, q.top + H * 0.78f, caja.right - W * 0.1f, q.top + H * 0.9f), LetraExtra.trazo, tinta, 12f, 9.5f)
    }

    override val cajaPrevia: Rect = Rect(0.17f, 0.175f, 0.83f, 0.345f)

    override val altoBuzon: Dp = 200.dp

    /** Un hatillo chico del buzón, de [w] de ancho con el centro en [c]: listo (el sello brilla), enterrado y atado, o abierto. */
    private fun DrawScope.hatillo(c: Offset, w: Float, estado: Int, oscuro: Boolean, semilla: Int) {
        val u = density
        val h = w * 0.66f
        val r = Rect(c.x - w / 2, c.y - h / 2, c.x + w / 2, c.y + h / 2)
        val forma = contornoIrregular(r, semilla, 20, 0.03f)
        translate(1.2f * u, 1.8f * u) { drawPath(forma, Color.Black.copy(alpha = 0.3f)) }
        if (estado == 2) {
            // Abierto: la solapa de pie detrás y la piel raspada que asoma, un poco ladeada, con dos renglones de carbón.
            drawPath(Path().apply { moveTo(r.left, r.top + h * 0.15f); quadraticTo(c.x, r.top - h * 0.5f, r.right, r.top + h * 0.15f); close() }, cc(PIEL_OSC, oscuro))
            rotate(-8f + 16f * azar(semilla, 1), Offset(c.x, r.top)) {
                drawRect(Color.Black.copy(alpha = 0.25f), Offset(r.left + w * 0.17f, r.top - h * 0.52f), Size(w * 0.68f, h * 0.8f))
                drawRect(cc(PIEL_CRUDA, oscuro), Offset(r.left + w * 0.15f, r.top - h * 0.55f), Size(w * 0.68f, h * 0.8f))
                for (j in 0 until 3) drawLine(cc(CARBON, oscuro).copy(alpha = 0.5f), Offset(r.left + w * 0.24f, r.top - h * (0.4f - 0.15f * j)), Offset(r.right - w * (0.26f + 0.1f * j), r.top - h * (0.4f - 0.15f * j)), 0.9f * u)
            }
        }
        val piel = cc(if (estado == 1) mezcla(PIEL, PIEL_OSC, 0.3f) else PIEL, oscuro)
        clipRect(r.left - w, if (estado == 2) r.top + h * 0.15f else r.top - h, r.right + w, r.bottom + h) { drawPath(forma, piel) }
        peloPiel(forma, cc(PELO, oscuro), 2 * u)
        if (estado != 2) {
            drawPath(Path().apply { moveTo(r.left + w * 0.02f, r.top + h * 0.06f); quadraticTo(c.x, r.top + h * 0.95f, r.right - w * 0.02f, r.top + h * 0.06f); close() }, cc(PIEL_LUZ, oscuro))
            if (estado == 1) listOf(-0.08f, 0.08f).forEach { dy -> drawLine(cc(TENDON, oscuro), Offset(r.left, c.y + h * dy + h * 0.06f), Offset(r.right, c.y + h * dy + h * 0.06f), 1.4f * u) }
            val rs = h * 0.17f
            val cs = Offset(c.x, c.y + h * 0.06f)
            drawCircle(cc(OCRE, oscuro), rs, cs)
            drawCircle(cc(PIEL_CRUDA, oscuro), rs * 0.35f, cs)
            drawCircle(cc(Color(0xFF5A2410), oscuro), rs, cs, style = Stroke(0.8f * u))
        }
    }

    /**
     * El buzón de la cueva: el escondrijo en el suelo del abrigo, visto en corte. Arriba, la pared de roca con sus capas y una
     * mano en negativo; en el suelo, tres piedras paradas con la cuenta pintada en ocre. A la izquierda, el hoyo recién cavado
     * con el palo de cavar: ahí esperan, encima de la tierra, las cartas que ya se pueden desenterrar, con su sello brillando;
     * en medio, bajo sus lajas, las enterradas, atadas con tendón en las capas de tierra; a la derecha, sobre una laja plana, las
     * ya abiertas. En modo oscuro, la cueva se apaga y solo se ve lo que alumbra el resplandor de las listas. Un degradado:
     * ese resplandor.
     */
    override fun DrawScope.buzon(e: EstadoBuzon, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = cc(x, oscuro)
        val suelo = h * 0.5f
        // La pared del abrigo con sus capas onduladas y una mano en negativo soplada en ocre.
        drawRect(c(ROCA_OSC), Offset.Zero, Size(w, suelo))
        var y = 8 * u; var k = 0
        while (y < suelo) {
            val capa = Path().apply {
                moveTo(0f, y)
                var x = 0f; var j = 0
                while (x < w) { quadraticTo(x + 18 * u, y + (azar(k * 31 + j, 3) - 0.5f) * 8 * u, x + 36 * u, y); x += 36 * u; j++ }
            }
            drawPath(capa, c(ROCA_HONDA).copy(alpha = 0.5f), style = Stroke(1 * u))
            y += (16 + 8 * azar(k, 5)) * u; k++
        }
        val cm = Offset(w * 0.5f, suelo * 0.42f)
        for (j in 0 until 60) {
            val a = j * 2.39f; val d = (10 + 14 * azar(j, 9)) * u
            drawCircle(c(OCRE).copy(alpha = 0.35f), (1 + azar(j, 2)) * u, Offset(cm.x + d * cos(a), cm.y + d * sin(a)))
        }
        mano(Offset(cm.x, cm.y - 9 * u), 7 * u, c(ROCA_LUZ).copy(alpha = 0.7f), 5)
        // Las capas de tierra en corte: tierra clara arriba, más oscura y con guijarros hacia abajo.
        drawRect(c(TIERRA), Offset(0f, suelo), Size(w, h - suelo))
        listOf(0.18f to Color(0xFF7C6448), 0.5f to Color(0xFF5E4834), 0.78f to Color(0xFF4E3B2A)).forEach { (f, col) ->
            val y0 = suelo + (h - suelo) * f
            drawPath(Path().apply {
                moveTo(0f, y0)
                var x = 0f
                while (x < w) { quadraticTo(x + 25 * u, y0 + (azar((x / u).toInt(), 7) - 0.5f) * 6 * u, x + 50 * u, y0); x += 50 * u }
                lineTo(w, h); lineTo(0f, h); close()
            }, c(col))
        }
        for (j in 0 until 40) drawOval(c(LAJA_OSC).copy(alpha = 0.55f), Offset(w * azar(j, 11), suelo + (h - suelo) * (0.1f + 0.88f * azar(j, 12))), Size((2 + 3 * azar(j, 13)) * u, (1.5f + 2 * azar(j, 14)) * u))
        // El suelo del abrigo: la orilla con unas briznas de pasto.
        drawRect(c(PASTO), Offset(0f, suelo - 2 * u), Size(w, 4 * u))
        for (j in 0 until 30) { val x = w * azar(j, 21); drawLine(c(PASTO), Offset(x, suelo), Offset(x + (azar(j, 22) - 0.5f) * 4 * u, suelo - (3 + 4 * azar(j, 23)) * u), 1 * u) }

        val zw = w / 3f
        // Cada grupo: a la izquierda su piedra parada con la cuenta; a la derecha, sus hatillos.
        val centro = List(3) { zw * it + zw * 0.64f }
        // Las listas: el hoyo abierto, con su montón de tierra, el palo de cavar y los hatillos encima, brillando.
        val hoyo = Rect(centro[0] - zw * 0.3f, suelo - 2 * u, centro[0] + zw * 0.3f, suelo + (h - suelo) * 0.36f)
        drawPath(Path().apply { moveTo(hoyo.left, hoyo.top); quadraticTo(hoyo.center.x, hoyo.bottom + 10 * u, hoyo.right, hoyo.top); close() }, c(Color(0xFF2E2218)))
        drawPath(Path().apply { moveTo(hoyo.right - 6 * u, suelo); quadraticTo(hoyo.right + 4 * u, suelo - 12 * u, hoyo.right + 16 * u, suelo); close() }, c(Color(0xFF7C6448)))
        drawLine(c(LENA), Offset(hoyo.right + 2 * u, suelo - 34 * u), Offset(hoyo.right - 6 * u, suelo + 8 * u), 2.4f * u, StrokeCap.Round)
        val nL = minOf(e.listas, 3)
        if (nL > 0) mancha(Offset(centro[0], suelo - 8 * u), 48 * u, FUEGO.copy(alpha = if (oscuro) 0.55f else 0.42f))
        for (i in 0 until nL) {
            val x = centro[0] + (i - (nL - 1) / 2f) * 20 * u
            hatillo(Offset(x, suelo - (6 + 9 * (i % 2)) * u), 28 * u, 0, oscuro, 300 + i)
            drawPath(estrella(Offset(x + 10 * u, suelo - (22 + 8 * (i % 2)) * u), 3.5f * u, 4, 0.25f, 0f), FUEGO)
        }
        // Las enterradas: en las capas de tierra, cada una bajo su laja, atadas con tendón.
        val nS = minOf(e.selladas, 4)
        for (i in 0 until nS) {
            val col = i % 2; val fila = i / 2
            val x = centro[1] + (col - 0.5f) * 38 * u + fila * 6 * u
            val yy = suelo + (h - suelo) * (0.32f + 0.38f * fila)
            hatillo(Offset(x, yy), 30 * u, 1, oscuro, 400 + i)
            if (fila == 0) laja(Rect(x - 18 * u, suelo - 5 * u, x + 17 * u, suelo + 3 * u), 410 + i, c(CALIZAS[i % CALIZAS.size]), oscuro)
        }
        if (nS == 0) laja(Rect(centro[1] - 20 * u, suelo - 5 * u, centro[1] + 18 * u, suelo + 3 * u), 499, c(LAJA), oscuro)
        // Las abiertas: sobre una laja plana, una encima de otra.
        val mesa = Rect(centro[2] - zw * 0.32f, suelo - 9 * u, centro[2] + zw * 0.32f, suelo + 3 * u)
        laja(mesa, 77, c(LAJA), oscuro)
        val nA = minOf(e.abiertas, 3)
        for (i in 0 until nA) hatillo(Offset(centro[2] + (i - (nA - 1) / 2f) * 22 * u, mesa.top - 9 * u - (i % 2) * 4 * u), 28 * u, 2, oscuro, 500 + i)
        // La cuenta: una piedra parada por grupo, con el número pintado en ocre; el nombre, con carbón en la pared.
        val nombres = listOf("para desenterrar", "enterradas", "abiertas")
        val cuentas = listOf(e.listas, e.selladas, e.abiertas)
        for (i in 0 until 3) {
            val x = zw * i + zw * 0.17f
            val pz = Rect(x - 15 * u, suelo - 44 * u, x + 15 * u, suelo + 4 * u)
            val forma = contornoIrregular(pz, 600 + i, 14, 0.07f)
            translate(2.5f * u, 1.5f * u) { drawPath(forma, Color.Black.copy(alpha = 0.32f)) }
            val tono = c(CALIZAS[(i + 1) % CALIZAS.size])
            drawPath(forma, tono)
            clipPath(forma) {
                drawRect(Color.Black.copy(alpha = 0.16f), Offset(pz.right - 7 * u, pz.top), Size(7 * u, pz.height))
                drawRect(Color.White.copy(alpha = 0.12f), pz.topLeft, Size(5 * u, pz.height))
                drawRect(Color.Black.copy(alpha = 0.22f), Offset(pz.left, suelo - 3 * u), Size(pz.width, 8 * u))
            }
            textoEn(medidor, cuentas[i].toString(), Rect(pz.left + 2 * u, pz.top + 4 * u, pz.right - 4 * u, suelo - 6 * u), LetraExtra.trazo,
                legibleAnt(tono, c(OCRE), OCRE_AMARILLO), 20f, peso = FontWeight.Bold, minimo = 9f)
            textoEn(medidor, nombres[i], Rect(zw * i + 4 * u, 5 * u, zw * (i + 1) - 4 * u, 22 * u), LetraExtra.trazo, legibleAnt(c(ROCA_OSC), c(CARBON), ROCA_LUZ), 11f, minimo = 7f)
        }
        // Cuando no hay nada: un brote en el hoyo.
        if (e.listas + e.selladas + e.abiertas == 0) {
            drawLine(c(PASTO), Offset(centro[0], suelo + 4 * u), Offset(centro[0], suelo - 8 * u), 1.4f * u)
            drawOval(c(PASTO), Offset(centro[0] - 7 * u, suelo - 11 * u), Size(7 * u, 4 * u))
            drawOval(c(PASTO), Offset(centro[0], suelo - 13 * u), Size(7 * u, 4 * u))
        }
    }

    /** Una laja de caliza tumbada en [r], con su canto en sombra y su sombra en el suelo. */
    private fun DrawScope.laja(r: Rect, semilla: Int, tono: Color, oscuro: Boolean) {
        val u = density
        val forma = contornoIrregular(r, semilla, 16, 0.1f)
        translate(1.5f * u, 2 * u) { drawPath(forma, Color.Black.copy(alpha = 0.3f)) }
        drawPath(forma, tono)
        clipPath(forma) { drawRect(cc(LAJA_OSC, oscuro), Offset(r.left, r.bottom - r.height * 0.4f), Size(r.width, r.height * 0.4f)) }
        drawPath(forma, cc(ROCA_OSC, oscuro), style = Stroke(0.7f * u))
    }
}
/**
 * Roma: los nichos (loculamenta) hundidos en un muro de mármol de una domus, entre dos pilastras estriadas. Cada repisa es un
 * nicho pintado de rojo pompeyano por dentro, con su filete ocre, y la tabla de mármol lleva su astrágalo de cuentas; el mes
 * va en un titulus de mármol rotulado en minio. Arriba, la cornisa con dentículos y rosetas, un busto sobre su herma, dos
 * guirnaldas de laurel y una lucerna de bronce sobre su candelabro; abajo, el zócalo negro con paneles ocres y el piso de
 * mosaico. Cada entrada es un rollo tumbado, con su umbilicus y sus perillas de hueso o de ébano, que engorda con lo escrito,
 * el mes abreviado en tinta y el sillybos rojo colgando con el día; un sello de plomo en el hilo (de noche, una lunita). De
 * noche la lucerna se enciende. La tapa es ese rollo tumbado y grande, ceñido con su correa de púrpura, con el sillybos
 * colgando con la fecha y, al lado, el tintero con su cálamo (de noche, la lucerna encendida); al abrirse, la correa se
 * suelta y el rollo baja desenrollándose y deja ver las columnas escritas. Distinto del Ideario, que es el armarium de nogal
 * con su frontón y las capsae de cuero.
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

    // ---------------------------------------------------------------- la tapa

    override val altoTapa: Dp = 184.dp
    override val cajaTapa = Rect(0.17f, 0.2f, 0.83f, 0.55f)
    override val letraTapa: FontFamily = Letra.inscripcion
    override val tamTapa = 12.5f
    override val lineasTapa = 3
    override val centradoTapa = false

    /** El papiro de la vuelta de fuera, donde va el extracto: de noche lo alumbra la lucerna, así que se hunde menos. */
    private fun hojaTapa(oscuro: Boolean, noche: Boolean): Color {
        var r = PAPIROS[0]
        if (oscuro) r = mezcla(r, Color(0xFF100D0B), 0.36f)
        if (noche) r = mezcla(r, NOCHE, 0.3f)
        return r
    }

    override fun tintaTapa(oscuro: Boolean, noche: Boolean): Color = legibleAnt(hojaTapa(oscuro, noche), TINTA, HUESO)
    override fun palabrasTapa(n: Int): String = palabrasAnt(n)

    /** La tapa, recortada a su lienzo: lo que se aparta al abrir y la luz de noche no se salen de su lugar. */
    override fun DrawScope.tapa(e: EstadoTapa, medidor: TextMeasurer, oscuro: Boolean) = clipRect { dibujarTapa(e, medidor, oscuro) }

    private fun DrawScope.dibujarTapa(e: EstadoTapa, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, e.noche)
        val a = e.abierto.coerceIn(0f, 1f)
        val sem = e.semilla and 0x7fffffff
        val papiro = c(PAPIROS[sem % PAPIROS.size])
        val hoja = hojaTapa(oscuro, e.noche)
        val caja = Rect(w * cajaTapa.left, h * cajaTapa.top, w * cajaTapa.right, h * cajaTapa.bottom)
        // El rollo tumbado: al abrirse, la hoja se queda pegada arriba y el rollo baja desenrollándose (y adelgaza).
        val cil = Rect(w * 0.06f, h * 0.05f, w * 0.94f, h * 0.68f)
        val r0 = cil.height / 2
        val cy = cil.center.y + (h * 0.665f - cil.center.y) * a
        val radio = r0 * (1f - 0.62f * a)
        val rollo = Rect(cil.left, cy - radio, cil.right, cy + radio)
        val extremo = rollo.height * 0.2f

        // Abajo a la izquierda: de día, el tintero de bronce con su cálamo; de noche, la lucerna encendida.
        if (e.noche) {
            val cuerpo = Rect(w * 0.09f, h * 0.84f, w * 0.27f, h * 0.95f)
            val boca = Offset(cuerpo.right + w * 0.07f, cuerpo.top + cuerpo.height * 0.3f)
            mancha(boca, h * 0.42f, LLAMA.copy(alpha = 0.38f))
            drawOval(Color.Black.copy(alpha = 0.25f), Offset(cuerpo.left, cuerpo.bottom - 2 * u), Size(cuerpo.width + w * 0.07f, 4 * u))
            val pico = Path().apply {
                moveTo(cuerpo.right - cuerpo.width * 0.2f, cuerpo.top + 1 * u); lineTo(boca.x, boca.y - 2 * u)
                lineTo(boca.x + 1 * u, boca.y + 2.2f * u); lineTo(cuerpo.right - cuerpo.width * 0.2f, cuerpo.bottom - 2 * u); close()
            }
            drawPath(pico, c(BRONCE))
            drawOval(c(BRONCE), cuerpo.topLeft, cuerpo.size)
            drawOval(c(BRONCE_LUZ).copy(alpha = 0.6f), Offset(cuerpo.left + cuerpo.width * 0.2f, cuerpo.top + 1.5f * u), Size(cuerpo.width * 0.45f, cuerpo.height * 0.3f))
            drawCircle(c(BRONCE_OSC), cuerpo.height * 0.16f, Offset(cuerpo.center.x, cuerpo.top + cuerpo.height * 0.45f))
            drawCircle(c(BRONCE_OSC), cuerpo.height * 0.3f, Offset(cuerpo.left - cuerpo.height * 0.12f, cuerpo.center.y), style = Stroke(1.6f * u))
            val llama = Path().apply {
                moveTo(boca.x - 2.4f * u, boca.y)
                quadraticTo(boca.x - 3 * u, boca.y - 6 * u, boca.x + 0.6f * u, boca.y - 13 * u)
                quadraticTo(boca.x + 3.4f * u, boca.y - 6 * u, boca.x + 2.4f * u, boca.y)
                close()
            }
            drawPath(llama, LLAMA)
            drawOval(Color.White.copy(alpha = 0.8f), Offset(boca.x - 0.9f * u, boca.y - 5 * u), Size(1.8f * u, 4 * u))
        } else {
            val tintero = Rect(w * 0.1f, h * 0.8f, w * 0.17f, h * 0.95f)
            drawRect(Color.Black.copy(alpha = 0.2f), Offset(tintero.left + 2 * u, tintero.bottom - 1 * u), Size(tintero.width, 2.5f * u))
            drawRect(c(BRONCE), tintero.topLeft, tintero.size)
            drawRect(c(BRONCE_LUZ).copy(alpha = 0.5f), Offset(tintero.left + tintero.width * 0.15f, tintero.top), Size(tintero.width * 0.18f, tintero.height))
            drawRect(c(BRONCE_OSC).copy(alpha = 0.5f), Offset(tintero.right - tintero.width * 0.25f, tintero.top), Size(tintero.width * 0.25f, tintero.height))
            drawOval(c(BRONCE_OSC), Offset(tintero.left - 1.5f * u, tintero.top - 2 * u), Size(tintero.width + 3 * u, 4 * u))
            drawOval(c(NEGRO), Offset(tintero.left + tintero.width * 0.25f, tintero.top - 1.2f * u), Size(tintero.width * 0.5f, 2.4f * u))
            listOf(0.3f, 0.7f).forEach { f -> drawLine(c(BRONCE_OSC), Offset(tintero.left, tintero.top + tintero.height * f), Offset(tintero.right, tintero.top + tintero.height * f), 0.8f * u) }
            // El cálamo, apoyado en el tintero, con su punta partida.
            val p0 = Offset(tintero.center.x, tintero.top); val p1 = Offset(w * 0.36f, h * 0.73f)
            drawLine(c(PAPIRO_OSC), p0, p1, 2.4f * u, StrokeCap.Round)
            for (j in 1..3) {
                val f = j / 4f
                val p = Offset(p0.x + (p1.x - p0.x) * f, p0.y + (p1.y - p0.y) * f)
                drawLine(c(BRONCE_OSC), Offset(p.x - 1 * u, p.y - 1.6f * u), Offset(p.x + 1 * u, p.y + 1.6f * u), 0.7f * u)
            }
            drawLine(c(TINTA), p0, Offset(p0.x - 1.5f * u, p0.y + 3 * u), 1f * u)
        }

        // La hoja ya desenrollada (sale por abajo del rollo): las juntas de las hojas pegadas y las columnas de escritura.
        if (a > 0.01f) {
            val desen = Rect(rollo.left + extremo * 0.2f, cil.top + r0 * 0.12f, rollo.right - extremo * 0.6f, cy)
            drawRect(Color.Black.copy(alpha = 0.15f), Offset(desen.left + 2 * u, desen.top + 2 * u), desen.size)
            drawRect(papiro, desen.topLeft, desen.size)
            drawRect(c(PAPIRO_OSC).copy(alpha = 0.5f), desen.topLeft, Size(desen.width, 1.4f * u))
            listOf(0.33f, 0.66f).forEach { f -> drawRect(c(PAPIRO_OSC).copy(alpha = 0.2f), Offset(desen.left + desen.width * f, desen.top), Size(w * 0.012f, desen.height)) }
            val columnas = 3
            val ancho = desen.width / columnas
            for (k in 0 until columnas) {
                val x0 = desen.left + ancho * k + ancho * 0.1f
                var y = desen.top + 8 * u
                var n = 0
                while (y < desen.bottom - 4 * u) {
                    val largo = ancho * (0.62f + 0.18f * azar(e.semilla + k, n))
                    drawLine(c(TINTA).copy(alpha = 0.45f), Offset(x0, y), Offset(x0 + largo, y), 1.1f * u)
                    y += 4.6f * u; n++
                }
            }
        }

        // El umbilicus, la varilla que atraviesa el rollo, con sus perillas (cornua) de hueso o de ébano.
        val perilla = c(if (azar(e.semilla, 3) > 0.5f) HUESO else EBANO)
        val xi = rollo.left - extremo * 0.5f - w * 0.02f
        val xd = rollo.right + w * 0.02f
        drawRect(c(BRONCE_OSC), Offset(xi, cy - 1.8f * u), Size(xd - xi, 3.6f * u))
        // El cuerpo del rollo, con su luz y su sombra (el primer degradado), y el canto izquierdo redondeado.
        drawOval(c(PAPIRO_OSC), Offset(rollo.left - extremo / 2, rollo.top), Size(extremo, rollo.height))
        val cuerpo = Rect(rollo.left, rollo.top, rollo.right - extremo / 2, rollo.bottom)
        drawRect(Brush.verticalGradient(0f to c(PAPIRO_OSC), 0.14f to mezcla(hoja, Color.White, 0.25f), 0.26f to hoja, 0.8f to hoja, 1f to c(PAPIRO_OSC),
            startY = rollo.top, endY = rollo.bottom), cuerpo.topLeft, cuerpo.size)
        // Las fibras del papiro, fuera de la caja del extracto, y el borde de la vuelta de fuera, abajo.
        clipRect(cuerpo.left, cuerpo.top, cuerpo.right, cuerpo.bottom) {
            clipRect(caja.left - 4 * u, caja.top - 3 * u, caja.right + 4 * u, caja.bottom + 3 * u, clipOp = ClipOp.Difference) {
                var y = cuerpo.top + 2 * u
                while (y < cuerpo.bottom) { drawLine(c(PAPIRO_OSC).copy(alpha = 0.22f), Offset(cuerpo.left, y), Offset(cuerpo.right, y), 0.5f * u); y += 3 * u }
                for (j in 0 until 30) {
                    val x = cuerpo.left + cuerpo.width * azar(e.semilla, 40 + j)
                    drawLine(c(PAPIRO_OSC).copy(alpha = 0.14f), Offset(x, cuerpo.top), Offset(x, cuerpo.bottom), 0.5f * u)
                }
            }
        }
        val borde = rollo.top + rollo.height * 0.86f
        drawLine(c(PAPIRO_OSC), Offset(cuerpo.left, borde), Offset(cuerpo.right, borde + 0.5f * u), 0.9f * u)
        drawRect(Color.Black.copy(alpha = 0.12f), Offset(cuerpo.left, borde), Size(cuerpo.width, rollo.bottom - borde))
        // El canto derecho: la espiral de las vueltas, más cuanto más escribiste.
        val canto = Rect(rollo.right - extremo, rollo.top, rollo.right, rollo.bottom)
        drawOval(papiro, canto.topLeft, canto.size)
        val vueltas = 4 + ((e.palabras / 800f).coerceIn(0f, 1f) * 4).toInt()
        for (k in 1..vueltas) {
            val f = k / (vueltas + 1f)
            drawOval(c(PAPIRO_OSC).copy(alpha = 0.6f), Offset(canto.center.x - canto.width * f / 2, cy - canto.height * f / 2), Size(canto.width * f, canto.height * f), style = Stroke(0.7f * u))
        }
        drawOval(c(PAPIRO_OSC), canto.topLeft, canto.size, style = Stroke(1f * u))
        // Las perillas en las puntas de la varilla.
        val altoPerilla = rollo.height * 0.34f
        listOf(xi, xd).forEach { px ->
            drawOval(perilla, Offset(px - w * 0.016f, cy - altoPerilla / 2), Size(w * 0.032f, altoPerilla))
            drawOval(Color.White.copy(alpha = 0.25f), Offset(px - w * 0.009f, cy - altoPerilla * 0.38f), Size(w * 0.01f, altoPerilla * 0.32f))
            drawOval(c(BRONCE_OSC), Offset(px - w * 0.016f, cy - altoPerilla / 2), Size(w * 0.032f, altoPerilla), style = Stroke(0.6f * u))
        }
        // La correa (lorum) de púrpura que ciñe el rollo, con su sello de plomo colgando: se suelta al abrir.
        val suelta = (1f - a * 3f).coerceIn(0f, 1f)
        if (suelta > 0f) {
            val xc = w * 0.11f
            drawRect(c(PURPURA).copy(alpha = 0.95f * suelta), Offset(xc, rollo.top - 1 * u), Size(4 * u, rollo.height + 2 * u))
            drawLine(Color.White.copy(alpha = 0.15f * suelta), Offset(xc + 1 * u, rollo.top), Offset(xc + 1 * u, rollo.bottom), 0.7f * u)
            val sello = Offset(xc + 2 * u, rollo.bottom + 7 * u)
            drawLine(c(PURPURA).copy(alpha = suelta), Offset(xc + 2 * u, rollo.bottom), sello, 1 * u)
            if (e.noche) {
                drawCircle(LUNA.copy(alpha = suelta), 3.4f * u, sello)
                drawCircle(c(PAPIROS[0]).copy(alpha = suelta), 2.8f * u, Offset(sello.x + 1.6f * u, sello.y - 1 * u))
            } else {
                drawCircle(c(PLOMO).copy(alpha = suelta), 3.2f * u, sello)
                drawCircle(Color.White.copy(alpha = 0.3f * suelta), 1 * u, Offset(sello.x - 1 * u, sello.y - 1 * u))
            }
        }
        // El sillybos: la etiqueta de pergamino teñido de minio que cuelga de la perilla derecha, con la fecha y las palabras.
        val etiqueta = Rect(w * 0.5f, h * 0.755f, w * 0.93f, h * 0.965f)
        val amarre = Offset(xd, cy + altoPerilla * 0.3f)
        drawLine(c(TINTA).copy(alpha = 0.7f), amarre, Offset(etiqueta.right - etiqueta.width * 0.12f, etiqueta.top + 1 * u), 0.9f * u)
        drawLine(c(TINTA).copy(alpha = 0.7f), amarre, Offset(etiqueta.left + etiqueta.width * 0.7f, etiqueta.top + 1 * u), 0.9f * u)
        rotate(-1.5f + 3f * azar(e.semilla, 5), Offset(etiqueta.center.x, etiqueta.top)) {
            drawRect(Color.Black.copy(alpha = 0.22f), Offset(etiqueta.left + 1.5f * u, etiqueta.top + 1.5f * u), etiqueta.size)
            drawRect(c(MINIO), etiqueta.topLeft, etiqueta.size)
            drawRect(Color.Black.copy(alpha = 0.15f), Offset(etiqueta.left, etiqueta.bottom - 2 * u), Size(etiqueta.width, 2 * u))
            val filete = etiqueta.deflate(2 * u)
            drawRect(c(HUESO).copy(alpha = 0.55f), filete.topLeft, filete.size, style = Stroke(0.6f * u))
            val claro = legibleAnt(c(MINIO), TINTA, HUESO)
            val conPalabras = e.palabras > 0
            val corte = if (conPalabras) filete.top + filete.height * 0.6f else filete.bottom
            textoEn(medidor, e.fecha, Rect(filete.left + 3 * u, filete.top + 1 * u, filete.right - 3 * u, corte), Letra.inscripcion, claro, 13f,
                peso = FontWeight.SemiBold, espaciado = 0.6f, minimo = 7f)
            if (conPalabras) {
                drawLine(claro.copy(alpha = 0.4f), Offset(filete.left + filete.width * 0.3f, corte), Offset(filete.right - filete.width * 0.3f, corte), 0.5f * u)
                textoEn(medidor, palabrasTapa(e.palabras), Rect(filete.left + 3 * u, corte, filete.right - 3 * u, filete.bottom - 0.5f * u), Letra.inscripcion,
                    claro.copy(alpha = 0.85f), 9.5f, espaciado = 0.4f, minimo = 6f)
            }
        }
    }

    // ---------------------------------------------------------------- la página y el escritor

    override val altoEncabezado: Dp = 114.dp

    /** Una corona de laurel abierta abajo, con su cinta: dos ramas de hojas en arco alrededor de [c]. */
    private fun DrawScope.coronaLaurel(c: Offset, r: Float, hoja: Color, cinta: Color) {
        val u = density
        listOf(-1f, 1f).forEach { s ->
            drawArc(hoja, if (s < 0) 110f else -70f, 140f, false, Offset(c.x - r * 0.82f, c.y - r * 0.82f), Size(r * 1.64f, r * 1.64f), style = Stroke(0.9f * u))
            for (k in 0 until 7) {
                val a = Math.toRadians((if (s < 0) 112.0 + k * 21.0 else 68.0 - k * 21.0))
                val o = Offset(c.x + r * 0.82f * cos(a).toFloat(), c.y + r * 0.82f * sin(a).toFloat())
                val giro = Math.toDegrees(a).toFloat() + 90f + s * 32f
                rotate(giro, o) {
                    drawOval(hoja, Offset(o.x - r * 0.1f, o.y - r * 0.24f), Size(r * 0.2f, r * 0.48f))
                }
            }
        }
        drawLine(cinta, Offset(c.x - r * 0.18f, c.y + r * 0.86f), Offset(c.x - r * 0.38f, c.y + r * 1.1f), 1.4f * u, StrokeCap.Round)
        drawLine(cinta, Offset(c.x + r * 0.18f, c.y + r * 0.86f), Offset(c.x + r * 0.38f, c.y + r * 1.1f), 1.4f * u, StrokeCap.Round)
        drawCircle(cinta, 1.8f * u, Offset(c.x, c.y + r * 0.84f))
    }

    /** Una hedera (la hoja de hiedra que separa los textos en las inscripciones), centrada en [o], de alto [m]. */
    private fun DrawScope.hedera(o: Offset, m: Float, color: Color) {
        val hoja = Path().apply {
            moveTo(o.x, o.y + m * 0.5f)
            cubicTo(o.x - m * 0.7f, o.y + m * 0.05f, o.x - m * 0.45f, o.y - m * 0.55f, o.x, o.y - m * 0.2f)
            cubicTo(o.x + m * 0.45f, o.y - m * 0.55f, o.x + m * 0.7f, o.y + m * 0.05f, o.x, o.y + m * 0.5f)
            close()
        }
        drawPath(hoja, color)
        drawLine(color, Offset(o.x, o.y - m * 0.2f), Offset(o.x - m * 0.25f, o.y - m * 0.62f), m * 0.1f, StrokeCap.Round)
    }

    /**
     * El encabezado: la tabula ansata, la placa de mármol con sus dos asas de cola de milano, el doble marco en minio y las
     * vetas; dentro, a la izquierda, la corona de laurel (de noche, la luna creciente con dos estrellas) y, en medio, el
     * rótulo en capitales espaciadas entre dos hederae, la fecha grande y el detalle.
     */
    override fun DrawScope.encabezado(e: EstadoEncabezado, p: Pagina, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val marmol = if (p.oscura) mezcla(p.color, p.tinta, 0.1f) else tonoAnt(MARMOL, p, oscuro)
        val asa = 22 * u
        val placa = Rect(asa, 6 * u, w - asa, h - 8 * u)
        val forma = Path().apply {
            moveTo(placa.left, placa.top); lineTo(placa.right, placa.top)
            lineTo(placa.right, placa.center.y - placa.height * 0.2f); lineTo(w - 2 * u, placa.top + placa.height * 0.1f)
            lineTo(w - 2 * u, placa.bottom - placa.height * 0.1f); lineTo(placa.right, placa.center.y + placa.height * 0.2f)
            lineTo(placa.right, placa.bottom); lineTo(placa.left, placa.bottom)
            lineTo(placa.left, placa.center.y + placa.height * 0.2f); lineTo(2 * u, placa.bottom - placa.height * 0.1f)
            lineTo(2 * u, placa.top + placa.height * 0.1f); lineTo(placa.left, placa.center.y - placa.height * 0.2f)
            close()
        }
        translate(2 * u, 3 * u) { drawPath(forma, Color.Black.copy(alpha = if (p.oscura) 0.35f else 0.16f)) }
        drawPath(forma, marmol)
        clipPath(forma) {
            for (j in 0 until 5) {
                val veta = Path()
                val y = placa.top + placa.height * (0.1f + 0.2f * j)
                veta.moveTo(0f, y)
                veta.cubicTo(w * 0.3f, y + (azar(e.numero, j) - 0.5f) * 30 * u, w * 0.6f, y + (azar(e.numero, j + 9) - 0.5f) * 30 * u, w, y + 6 * u)
                drawPath(veta, (if (p.oscura) p.tinta else MARMOL_VETA).copy(alpha = 0.18f), style = Stroke(0.7f * u))
            }
            // De noche, la luz de la lucerna desde la izquierda (el único degradado).
            if (e.noche) mancha(Offset(placa.left, placa.center.y), w * 0.5f, LLAMA.copy(alpha = 0.16f))
        }
        drawPath(forma, p.tinta.copy(alpha = 0.35f), style = Stroke(1f * u))
        drawRect(p.acento.copy(alpha = 0.8f), Offset(placa.left + 5 * u, placa.top + 5 * u), Size(placa.width - 10 * u, placa.height - 10 * u), style = Stroke(1.4f * u))
        drawRect(p.tinta.copy(alpha = 0.3f), Offset(placa.left + 8 * u, placa.top + 8 * u), Size(placa.width - 16 * u, placa.height - 16 * u), style = Stroke(0.7f * u))
        // El clavo de bronce en cada asa.
        listOf(10 * u, w - 10 * u).forEach { x ->
            drawCircle(tonoAnt(BRONCE, p, oscuro), 3 * u, Offset(x, placa.center.y))
            drawCircle(Color.Black.copy(alpha = 0.35f), 3 * u, Offset(x, placa.center.y), style = Stroke(0.7f * u))
        }
        // La corona de laurel o, de noche, la luna.
        val ce = Offset(placa.left + 30 * u, placa.center.y - 2 * u)
        if (e.noche) {
            drawCircle(LUNA, 13 * u, ce)
            drawCircle(marmol, 11 * u, Offset(ce.x + 6 * u, ce.y - 4 * u))
            drawPath(estrella(Offset(ce.x + 13 * u, ce.y + 12 * u), 3.2f * u, 4, 0.35f), LUNA)
            drawCircle(LUNA, 1.3f * u, Offset(ce.x - 12 * u, ce.y - 14 * u))
        } else {
            coronaLaurel(ce, 17 * u, (if (p.oscura) p.detalle else tonoAnt(LAUREL, p, oscuro)), p.acento)
        }
        val izq = placa.left + 54 * u
        val der = placa.right - 12 * u
        textoEn(medidor, e.rotulo, Rect(izq + 14 * u, 15 * u, der - 14 * u, 33 * u), Letra.inscripcion, p.acento, 13f, espaciado = 2.4f, minimo = 8f)
        hedera(Offset(izq + 5 * u, 24 * u), 8 * u, p.acento.copy(alpha = 0.8f))
        hedera(Offset(der - 5 * u, 24 * u), 8 * u, p.acento.copy(alpha = 0.8f))
        drawLine(p.acento.copy(alpha = 0.45f), Offset(izq + 10 * u, 35 * u), Offset(der - 10 * u, 35 * u), 0.8f * u)
        fechaEncAnt(medidor, e.fecha, Rect(izq, 37 * u, der, 74 * u), Letra.inscripcion, p.tinta, 24f, 24, FontWeight.Bold)
        textoEn(medidor, e.detalle, Rect(placa.left + 12 * u, 77 * u, placa.right - 12 * u, 92 * u), Letra.inscripcion, p.tenue, 10.5f, minimo = 7f)
    }

    /**
     * El papiro del volumen: las juntas de las hojas pegadas (kolleseis) cada 37 % del ancho, las fibras horizontales, la
     * doble regla roja del margen, las marcas de cuenta de líneas en el margen derecho y, al pie, tres hederae; de noche, la
     * luna creciente abajo a la derecha. En modo oscuro, el papiro en la penumbra y la luz de la lucerna desde arriba a la
     * izquierda.
     */
    override fun DrawScope.pagina(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        var x = w * 0.29f
        while (x < w) {
            drawRect(p.detalle.copy(alpha = 0.06f), Offset(x, 0f), Size(7 * u, h))
            drawLine(p.detalle.copy(alpha = 0.1f), Offset(x + 7 * u, 0f), Offset(x + 7 * u, h), 0.8f * u)
            x += w * 0.37f
        }
        var y = 0f
        var k = 0
        while (y < h) {
            drawLine(p.detalle.copy(alpha = 0.02f + 0.03f * azar(41, k)), Offset(0f, y), Offset(w, y + (azar(43, k) - 0.5f) * 2 * u), (0.6f + azar(47, k)) * u)
            y += (4 + 5 * azar(53, k)) * u; k++
        }
        listOf(16 * u, 20 * u).forEach { x0 -> drawLine(p.acento.copy(alpha = 0.3f), Offset(x0, 0f), Offset(x0, h), 1f * u) }
        var ym = 120 * u
        var n = 1
        while (ym < h - 80 * u) {
            drawLine(p.acento.copy(alpha = 0.28f), Offset(w - 16 * u, ym), Offset(w - 9 * u, ym), 1f * u)
            if (n % 5 == 0) drawCircle(p.acento.copy(alpha = 0.3f), 1.6f * u, Offset(w - 12.5f * u, ym + 6 * u))
            ym += 96 * u; n++
        }
        for (j in -1..1) hedera(Offset(w / 2 + j * 22 * u, h - 22 * u), 9 * u, p.acento.copy(alpha = 0.35f))
        listOf(h - 34 * u, h - 11 * u).forEach { yy -> drawLine(p.acento.copy(alpha = 0.2f), Offset(w * 0.3f, yy), Offset(w * 0.7f, yy), 0.8f * u) }
        if (noche) {
            val luna = Offset(w - 46 * u, h - 128 * u)
            drawCircle(p.acento.copy(alpha = 0.35f), 12 * u, luna)
            drawCircle(p.color, 10 * u, Offset(luna.x + 6 * u, luna.y - 4 * u))
        }
        if (oscuro && !p.oscura) penumbraAnt(Offset(-w * 0.04f, h * 0.06f), LLAMA, 0.22f)
    }

    /**
     * La barra: el volumen enrollado arriba, un cilindro de papiro (un degradado: luz arriba, sombra abajo) con las vueltas
     * del rollo marcadas y las perillas de hueso del umbilicus asomando en los dos extremos.
     */
    override fun DrawScope.barra(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val papiro = if (p.oscura) mezcla(p.color, p.tinta, 0.08f) else tonoAnt(PAPIROS[2], p, oscuro)
        val luz = if (p.oscura) mezcla(p.color, p.tinta, 0.14f) else mezcla(papiro, MARMOL_LUZ, 0.35f)
        val sombra = if (p.oscura) mezcla(p.color, Color.Black, 0.3f) else mezcla(papiro, PAPIRO_OSC, 0.7f)
        drawRect(Brush.verticalGradient(0f to luz, 0.3f to papiro, 0.8f to papiro, 1f to sombra, startY = 0f, endY = h - 3 * u), Offset(10 * u, 0f), Size(w - 20 * u, h - 3 * u))
        listOf(0.12f, 0.88f).forEach { f -> drawLine(p.detalle.copy(alpha = 0.18f), Offset(10 * u, h * f), Offset(w - 10 * u, h * f), 0.8f * u) }
        drawLine(p.acento.copy(alpha = 0.4f), Offset(10 * u, h - 3 * u), Offset(w - 10 * u, h - 3 * u), 1f * u)
        listOf(0f, w - 10 * u).forEach { x0 ->
            val perilla = if (p.oscura) mezcla(HUESO, p.color, 0.45f) else tonoAnt(HUESO, p, oscuro)
            drawRoundRect(perilla, Offset(x0 + 1 * u, h * 0.26f), Size(8 * u, h * 0.42f), CornerRadius(4 * u))
            drawRoundRect(Color.Black.copy(alpha = 0.3f), Offset(x0 + 1 * u, h * 0.26f), Size(8 * u, h * 0.42f), CornerRadius(4 * u), style = Stroke(0.8f * u))
            drawRect(tonoAnt(EBANO, p, oscuro), Offset(x0 + 3.5f * u, h * 0.42f), Size(3 * u, h * 0.1f))
        }
    }

    /**
     * El botón de guardar: el sillybos, la etiqueta de pergamino teñida de minio que cuelga del rollo con el título, con su
     * hilo a la izquierda, su borde de hueso, la sombra plana y un degradado de arriba abajo.
     */
    override fun DrawScope.botonGuardar(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val r = Rect(8 * u, 3 * u, w - 3 * u, h - 5 * u)
        val minio = if (oscuro) mezcla(MINIO, Color.Black, 0.12f) else MINIO
        drawRoundRect(Color.Black.copy(alpha = 0.3f), Offset(r.left + 1.5f * u, r.top + 2.5f * u), r.size, CornerRadius(3 * u))
        drawRoundRect(Brush.verticalGradient(listOf(mezcla(minio, OCRE, 0.25f), minio, mezcla(minio, Color.Black, 0.25f)), r.top, r.bottom), r.topLeft, r.size, CornerRadius(3 * u))
        drawRoundRect(HUESO.copy(alpha = 0.55f), Offset(r.left + 3 * u, r.top + 3 * u), Size(r.width - 6 * u, r.height - 6 * u), CornerRadius(2 * u), style = Stroke(0.8f * u))
        drawLine(tonoAnt(PAPIRO_OSC, p, oscuro), Offset(0f, h * 0.3f), Offset(r.left + 4 * u, r.center.y), 1.2f * u, StrokeCap.Round)
        drawCircle(HUESO, 1.6f * u, Offset(r.left + 4 * u, r.center.y))
    }

    override fun tintaBoton(p: Pagina, oscuro: Boolean, noche: Boolean): Color = HUESO

    // ---------------------------------------------------------------- la libreta de momentos

    override val altoLibreta: Dp = 256.dp

    /**
     * El color de la libreta con su luz: de día, tal cual; en modo oscuro, hundido en la penumbra del tablinum salvo donde llega
     * la lucerna ([luz], de 0 lejos a 1 junto a la llama, que además lo entibia).
     */
    private fun lc(x: Color, oscuro: Boolean, luz: Float = 0f): Color =
        if (!oscuro) x else mezcla(mezcla(x, Color(0xFF0C0908), 0.7f - 0.42f * luz), LLAMA, 0.12f * luz)

    /**
     * Un rollo de pie, visto de frente: el cilindro de papiro con sus franjas de luz y de sombra, las fibras, las vueltas
     * arriba y las perillas del umbilicus arriba y abajo.
     */
    private fun DrawScope.rolloDePie(cx: Float, arriba: Float, abajo: Float, diam: Float, papiro: Color, perilla: Color, vueltas: Int) {
        val u = density
        val elipse = diam * 0.28f
        drawRoundRect(papiro, Offset(cx - diam / 2, arriba), Size(diam, abajo - arriba), CornerRadius(diam * 0.08f))
        drawRect(Color.White.copy(alpha = 0.22f), Offset(cx - diam * 0.3f, arriba), Size(diam * 0.16f, abajo - arriba))
        drawRect(Color.Black.copy(alpha = 0.12f), Offset(cx + diam * 0.14f, arriba), Size(diam * 0.18f, abajo - arriba))
        drawRect(Color.Black.copy(alpha = 0.22f), Offset(cx + diam * 0.32f, arriba), Size(diam * 0.18f, abajo - arriba))
        var y = arriba + 4 * u
        while (y < abajo - 2 * u) { drawLine(Color(0x225A4020), Offset(cx - diam / 2, y), Offset(cx + diam / 2, y), 0.6f * u); y += 4.2f * u }
        drawOval(mezcla(papiro, Color.Black, 0.25f), Offset(cx - diam / 2, abajo - elipse / 2), Size(diam, elipse))
        drawOval(mezcla(papiro, Color.White, 0.12f), Offset(cx - diam / 2, arriba - elipse / 2), Size(diam, elipse))
        for (k in 1..vueltas) {
            val f = k / (vueltas + 1f)
            drawOval(mezcla(papiro, Color.Black, 0.3f), Offset(cx - diam / 2 * (1 - f), arriba - elipse / 2 * (1 - f)), Size(diam * (1 - f), elipse * (1 - f)), style = Stroke(0.7f * u))
        }
        listOf(arriba - elipse * 0.2f - 9 * u, abajo + elipse * 0.1f).forEachIndexed { i, yy ->
            drawRect(mezcla(perilla, Color.Black, 0.3f), Offset(cx - 2.2f * u, yy), Size(4.4f * u, 9 * u))
            val bola = Offset(cx, if (i == 0) yy - 2 * u else yy + 11 * u)
            drawCircle(perilla, 5.5f * u, bola)
            drawCircle(Color.White.copy(alpha = 0.35f), 1.8f * u, Offset(bola.x - 1.8f * u, bola.y - 1.8f * u))
        }
    }

    /**
     * La libreta de momentos: el rollo en el atril de mármol del tablinum. El atril, de pie sobre su fuste estriado, lleva
     * dos rollos: el de la derecha, grueso, guarda lo escrito y engorda con cada columna; el de la izquierda, delgado, es lo ya
     * leído. Del rollo grueso cuelga el sillybos de pergamino con el nombre en minio y la cuenta en tinta; en la repisa del
     * atril, el tintero de bronce con su cálamo; a la izquierda, la lucerna en su candelabro; a la derecha, la capsa con otros
     * rollos. Al abrirse, el rollo delgado se aparta hacia la izquierda y el papiro se tiende entre los dos, con sus columnas
     * escritas. En modo oscuro el cuarto se apaga y alumbra la lucerna. Un degradado: el resplandor de la lucerna.
     */
    override fun DrawScope.libreta(e: EstadoLibreta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val a = e.abierto.coerceIn(0f, 1f)
        val gordo = (kotlin.math.sqrt(e.hojas.coerceAtLeast(1).toFloat()) / 14f).coerceIn(0.07f, 1f)
        val s = e.semilla
        fun c(x: Color, luz: Float = 0f) = lc(x, oscuro, luz)
        // La pared: el panel rojo pompeyano con su marco ocre y un candelabro pintado; abajo, el zócalo negro con paneles.
        val zocalo = h * 0.66f; val piso = h * 0.91f
        drawRect(c(POMPEYANO, 0.15f), Offset.Zero, Size(w, zocalo))
        drawRect(c(NEGRO, 0.15f), Offset.Zero, Size(w, h * 0.05f))
        listOf(0.02f to 0.3f, 0.32f to 0.68f, 0.7f to 0.98f).forEach { (x0, x1) ->
            drawRect(c(OCRE, 0.15f), Offset(w * x0, h * 0.09f), Size(w * (x1 - x0), zocalo - h * 0.13f), style = Stroke(1.6f * u))
            drawRect(c(OCRE, 0.15f).copy(alpha = 0.5f), Offset(w * x0 + 4 * u, h * 0.09f + 4 * u), Size(w * (x1 - x0) - 8 * u, zocalo - h * 0.13f - 8 * u), style = Stroke(0.7f * u))
        }
        val pintado = c(OCRE, 0.15f).copy(alpha = 0.7f)
        drawLine(pintado, Offset(w * 0.84f, h * 0.14f), Offset(w * 0.84f, zocalo - h * 0.07f), 1.4f * u)
        for (k in 0 until 4) drawOval(pintado, Offset(w * 0.84f - 4 * u, h * (0.18f + 0.1f * k)), Size(8 * u, 4 * u))
        drawPath(Path().apply { moveTo(w * 0.78f, h * 0.15f); quadraticTo(w * 0.84f, h * 0.22f, w * 0.9f, h * 0.15f) }, pintado, style = Stroke(1.2f * u))
        drawRect(c(NEGRO, 0.15f), Offset(0f, zocalo), Size(w, piso - zocalo))
        drawRect(c(OCRE, 0.15f), Offset(0f, zocalo), Size(w, 2 * u))
        listOf(0.03f, 0.36f, 0.69f).forEach { x0 -> drawRect(c(OCRE, 0.15f).copy(alpha = 0.55f), Offset(w * x0, zocalo + h * 0.04f), Size(w * 0.28f, piso - zocalo - h * 0.08f), style = Stroke(1 * u)) }
        // El piso de mosaico: teselas blancas con una cenefa negra.
        drawRect(c(MARMOL, 0.3f), Offset(0f, piso), Size(w, h - piso))
        var tx = 0f
        while (tx < w) { var ty = piso; while (ty < h) { drawRect(Color.Black.copy(alpha = 0.08f), Offset(tx, ty), Size(4 * u, 4 * u), style = Stroke(0.4f * u)); ty += 4 * u }; tx += 4 * u }
        drawRect(c(NEGRO, 0.3f), Offset(0f, piso + (h - piso) * 0.45f), Size(w, 3 * u))
        // La lucerna en su candelabro de bronce, a la izquierda, y su resplandor (el único degradado).
        val llama = Offset(w * 0.075f, h * 0.24f)
        mancha(llama, if (oscuro) w * 0.9f else w * 0.3f, LLAMA.copy(alpha = if (oscuro) 0.4f else 0.28f))
        val bronce = c(BRONCE, 0.8f)
        drawLine(bronce, Offset(w * 0.075f, h * 0.31f), Offset(w * 0.075f, piso - 4 * u), 2.6f * u)
        for (k in 0 until 3) drawCircle(bronce, 3 * u, Offset(w * 0.075f, h * (0.42f + 0.14f * k)))
        listOf(-1f, 0f, 1f).forEach { d -> drawLine(bronce, Offset(w * 0.075f, piso - 6 * u), Offset(w * 0.075f + d * 9 * u, piso + 1 * u), 2 * u, StrokeCap.Round) }
        drawOval(bronce, Offset(w * 0.075f - 9 * u, h * 0.3f), Size(18 * u, 5 * u))
        drawPath(Path().apply { moveTo(llama.x - 9 * u, h * 0.29f); quadraticTo(llama.x - 2 * u, h * 0.255f, llama.x + 10 * u, h * 0.275f); lineTo(llama.x + 13 * u, h * 0.268f); lineTo(llama.x + 9 * u, h * 0.296f); close() }, c(BRONCE_LUZ, 1f))
        drawPath(Path().apply { moveTo(llama.x + 9 * u, h * 0.27f); quadraticTo(llama.x + 7 * u, h * 0.22f, llama.x + 12 * u, h * 0.19f); quadraticTo(llama.x + 15 * u, h * 0.23f, llama.x + 13 * u, h * 0.27f); close() }, LLAMA)
        drawCircle(MARMOL_LUZ, 1.6f * u, Offset(llama.x + 12 * u, h * 0.245f))
        // El atril: el plinto, el fuste estriado, el capitel, el tablero inclinado y su repisa.
        val marmol = c(MARMOL, 0.45f)
        val veta = c(MARMOL_VETA, 0.45f)
        drawRect(c(MARMOL_SOMBRA, 0.4f), Offset(w * 0.38f, h * 0.88f), Size(w * 0.26f, h * 0.07f))
        drawRect(marmol, Offset(w * 0.4f, h * 0.86f), Size(w * 0.22f, h * 0.025f))
        val fuste = Rect(w * 0.445f, h * 0.76f, w * 0.575f, h * 0.86f)
        drawRect(marmol, fuste.topLeft, fuste.size)
        for (k in 1..6) drawLine(veta, Offset(fuste.left + fuste.width * k / 7f, fuste.top), Offset(fuste.left + fuste.width * k / 7f, fuste.bottom), 1.2f * u)
        drawRect(Color.Black.copy(alpha = 0.15f), Offset(fuste.right - fuste.width * 0.25f, fuste.top), Size(fuste.width * 0.25f, fuste.height))
        drawRect(c(MARMOL_LUZ, 0.45f), Offset(w * 0.42f, h * 0.745f), Size(w * 0.18f, h * 0.02f))
        drawOval(veta, Offset(w * 0.41f, h * 0.742f), Size(w * 0.04f, h * 0.025f), style = Stroke(1 * u))
        drawOval(veta, Offset(w * 0.57f, h * 0.742f), Size(w * 0.04f, h * 0.025f), style = Stroke(1 * u))
        val tablero = Path().apply { moveTo(w * 0.2f, h * 0.12f); lineTo(w * 0.84f, h * 0.12f); lineTo(w * 0.87f, h * 0.7f); lineTo(w * 0.17f, h * 0.7f); close() }
        translate(4 * u, 5 * u) { drawPath(tablero, Color.Black.copy(alpha = 0.3f)) }
        drawPath(tablero, marmol)
        clipPath(tablero) {
            vetas(Rect(w * 0.17f, h * 0.12f, w * 0.87f, h * 0.7f), veta.copy(alpha = 0.45f), s + 3, densidad = 0.35f, ondulacion = 2.4f)
            drawRect(Color.Black.copy(alpha = 0.1f), Offset(w * 0.17f, h * 0.12f), Size(w * 0.7f, h * 0.025f))
        }
        drawPath(tablero, c(MARMOL_SOMBRA, 0.45f), style = Stroke(1.2f * u))
        drawRect(c(MARMOL_LUZ, 0.45f), Offset(w * 0.15f, h * 0.69f), Size(w * 0.74f, h * 0.025f))
        drawRect(c(MARMOL_SOMBRA, 0.45f), Offset(w * 0.15f, h * 0.715f), Size(w * 0.74f, h * 0.03f))
        for (k in 0 until 26) drawCircle(veta, 1.2f * u, Offset(w * 0.16f + k * w * 0.028f, h * 0.73f))
        // La capsa de cuero con otros rollos, a la derecha.
        val capsa = Rect(w * 0.84f, h * 0.7f, w * 0.98f, h * 0.95f)
        listOf(0.2f, 0.45f, 0.7f).forEachIndexed { k, f ->
            val x = capsa.left + capsa.width * f
            drawRect(c(PAPIROS[k + 1], 0.25f), Offset(x - 5 * u, capsa.top - (12 + 5 * k) * u), Size(10 * u, 20 * u))
            drawOval(c(PAPIRO_OSC, 0.25f), Offset(x - 5 * u, capsa.top - (14 + 5 * k) * u), Size(10 * u, 4 * u))
            drawRect(c(MINIO, 0.25f), Offset(x + 3 * u, capsa.top - (8 + 5 * k) * u), Size(5 * u, 7 * u))
        }
        drawRect(c(Color(0xFF6A3E22), 0.25f), capsa.topLeft, capsa.size)
        drawRect(Color.Black.copy(alpha = 0.2f), Offset(capsa.right - capsa.width * 0.3f, capsa.top), Size(capsa.width * 0.3f, capsa.height))
        listOf(0.15f, 0.85f).forEach { f -> drawRect(c(BRONCE, 0.25f), Offset(capsa.left, capsa.top + capsa.height * f - 1.5f * u), Size(capsa.width, 3 * u)) }
        drawOval(c(Color(0xFF4A2A14), 0.25f), Offset(capsa.left, capsa.top - 3 * u), Size(capsa.width, 6 * u))
        // Los dos rollos en el tablero: el grueso (lo escrito) y el delgado (lo leído), y entre ellos el papiro tendido.
        val arriba = h * 0.17f; val abajo = h * 0.66f
        val diam = w * (0.12f + 0.14f * gordo) * (1f - 0.12f * a)
        val delgado = w * 0.065f
        val xr = w * 0.5f
        val xl0 = xr - diam / 2 - delgado / 2 + 2 * u
        val xl = xl0 - (xl0 - w * 0.24f) * a
        val papiro = c(PAPIROS[(s and 0x7fffffff) % PAPIROS.size], 0.55f)
        val hoja = Rect(xl, arriba + 3 * u, xr, abajo - 3 * u)
        if (hoja.width > diam / 2 + delgado / 2 + 1) {
            drawRect(Color.Black.copy(alpha = 0.22f), Offset(hoja.left + 3 * u, hoja.top + 4 * u), hoja.size)
            drawRect(papiro, hoja.topLeft, hoja.size)
            clipRect(hoja.left, hoja.top, hoja.right - diam / 2, hoja.bottom) {
                var y = hoja.top + 3 * u
                while (y < hoja.bottom) { drawLine(Color(0x1A5A4020), Offset(hoja.left, y), Offset(hoja.right, y), 0.6f * u); y += 4.2f * u }
                // Las columnas escritas, de derecha a izquierda desde el rollo grueso, con su intercolumnio.
                val tinta = c(TINTA, 0.55f).copy(alpha = 0.7f)
                var x1 = hoja.right - diam / 2 - 6 * u
                var col = 0
                while (x1 > hoja.left) {
                    val x0 = x1 - 30 * u
                    var ry = hoja.top + 12 * u; var r = 0
                    while (ry < hoja.bottom - 10 * u) {
                        val largo = (30 - 8 * azar(s + col, r)) * u
                        drawLine(tinta, Offset(x0, ry), Offset(x0 + largo, ry), 1.3f * u)
                        ry += 6 * u; r++
                    }
                    if (col == 0) drawRect(c(MINIO, 0.55f), Offset(x0, hoja.top + 5 * u), Size(18 * u, 3 * u))
                    x1 = x0 - 9 * u; col++
                }
            }
            drawLine(c(PAPIRO_OSC, 0.55f), Offset(hoja.left, hoja.top), Offset(hoja.right, hoja.top), 0.8f * u)
            drawLine(c(PAPIRO_OSC, 0.55f), Offset(hoja.left, hoja.bottom), Offset(hoja.right, hoja.bottom), 0.8f * u)
        }
        val hueso = c(HUESO, 0.55f)
        val perilla = if ((s and 1) == 0) hueso else c(EBANO, 0.55f)
        translate(5 * u, 6 * u) {
            drawRoundRect(Color.Black.copy(alpha = 0.22f), Offset(xr - diam / 2, arriba), Size(diam, abajo - arriba), CornerRadius(diam * 0.2f))
        }
        rolloDePie(xl, arriba, abajo, delgado, mezcla(papiro, Color.Black, 0.05f), perilla, 2)
        rolloDePie(xr, arriba, abajo, diam, papiro, perilla, (3 + e.hojas / 20).coerceAtMost(9))
        // La cinta de púrpura que lo ciñe: ceñida en el rollo cerrado; al abrir, suelta y colgando.
        val purpura = c(PURPURA, 0.55f)
        if (a < 0.2f) drawRect(purpura.copy(alpha = 1f - a * 5f), Offset(xl - delgado / 2 - 1 * u, h * 0.42f), Size(xr + diam / 2 - xl + delgado / 2 + 2 * u, 5 * u))
        if (a > 0.05f) drawPath(Path().apply {
            moveTo(xl, h * 0.705f); cubicTo(xl + w * 0.08f, h * 0.68f, xl + w * 0.12f, h * 0.73f, xl + w * 0.2f * a + w * 0.06f, h * 0.705f)
        }, purpura.copy(alpha = minOf(1f, a * 4f)), style = Stroke(3 * u, cap = StrokeCap.Round))
        // El sillybos: la etiqueta de pergamino colgada de la perilla de arriba, con el nombre y la cuenta.
        val anclaje = Offset(xr, arriba - 12 * u)
        val et = Rect(minOf(xr + diam / 2 + 8 * u, w * 0.6f), h * 0.26f, w * 0.985f, h * 0.26f + 46 * u)
        drawPath(Path().apply { moveTo(anclaje.x, anclaje.y); quadraticTo(et.left - 4 * u, anclaje.y, et.left + 6 * u, et.top + 4 * u) }, c(MINIO, 0.55f), style = Stroke(1.4f * u))
        rotate(2f - 3f * a, Offset(et.left, et.top)) {
            drawRect(Color.Black.copy(alpha = 0.28f), Offset(et.left + 3 * u, et.top + 4 * u), et.size)
            val perg = c(HUESO, 0.75f)
            drawPath(Path().apply { moveTo(et.left, et.top); lineTo(et.right, et.top); lineTo(et.right - 5 * u, et.center.y); lineTo(et.right, et.bottom); lineTo(et.left, et.bottom); close() }, perg)
            drawRect(c(MINIO, 0.75f), Offset(et.left, et.top), Size(et.width, 3 * u))
            drawRect(c(MINIO, 0.75f), Offset(et.left, et.bottom - 3 * u), Size(et.width, 3 * u))
            drawRect(c(MINIO, 0.75f).copy(alpha = 0.6f), Offset(et.left + 4 * u, et.top + 6 * u), Size(et.width - 14 * u, et.height - 12 * u), style = Stroke(0.7f * u))
            drawCircle(c(MINIO, 0.75f), 2.2f * u, Offset(et.left + 6 * u, et.top + 9 * u))
            textoEn(medidor, e.nombre, Rect(et.left + 10 * u, et.top + 6 * u, et.right - 8 * u, et.top + 26 * u), letra,
                legibleAnt(perg, c(MINIO, 0.4f), MARMOL_LUZ), 13f, peso = FontWeight.Bold, minimo = 7f)
            textoEn(medidor, e.cuenta, Rect(et.left + 8 * u, et.top + 26 * u, et.right - 10 * u, et.bottom - 6 * u), Letra.garamond,
                legibleAnt(perg, c(TINTA, 0.4f), MARMOL_LUZ), 10.5f, minimo = 6.5f)
        }
        // El tintero de bronce en la repisa, con su tapa y su cadenita, y el cálamo apoyado.
        val tx0 = w * 0.67f; val tw = w * 0.07f
        drawLine(c(Color(0xFFB58A4A), 0.5f), Offset(tx0 + tw * 0.6f, h * 0.62f), Offset(tx0 + tw * 1.9f, h * 0.43f), 2.2f * u, StrokeCap.Round)
        drawLine(c(TINTA, 0.5f), Offset(tx0 + tw * 1.85f, h * 0.44f), Offset(tx0 + tw * 1.95f, h * 0.425f), 2.2f * u, StrokeCap.Round)
        drawRect(c(BRONCE, 0.5f), Offset(tx0, h * 0.6f), Size(tw, h * 0.095f))
        drawRect(c(BRONCE_LUZ, 0.5f), Offset(tx0 + tw * 0.15f, h * 0.6f), Size(tw * 0.15f, h * 0.095f))
        drawRect(c(BRONCE_OSC, 0.5f), Offset(tx0 + tw * 0.7f, h * 0.6f), Size(tw * 0.3f, h * 0.095f))
        drawOval(c(BRONCE_OSC, 0.5f), Offset(tx0 - 1 * u, h * 0.59f), Size(tw + 2 * u, 5 * u))
        drawOval(c(TINTA, 0.5f), Offset(tx0 + tw * 0.25f, h * 0.594f), Size(tw * 0.5f, 2.5f * u))
        drawRect(c(BRONCE_OSC, 0.5f), Offset(tx0, h * 0.64f), Size(tw, 1.4f * u))
        drawOval(c(BRONCE, 0.5f), Offset(tx0 + tw + 1 * u, h * 0.66f), Size(tw * 0.8f, 4 * u))
        for (k in 0 until 4) drawCircle(c(BRONCE_OSC, 0.5f), 1 * u, Offset(tx0 + tw - k * 2.4f * u, h * 0.655f + k * 1.4f * u), style = Stroke(0.6f * u))
    }

    override fun DrawScope.pestana(e: EstadoPestana, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val s = e.semilla
        val perg = lc(PAPIROS[(s and 0x7fffffff) % PAPIROS.size], oscuro, 0.35f)
        val minio = if (oscuro) mezcla(MINIO, LLAMA, 0.45f) else MINIO
        val tinta = if (oscuro) MARMOL_LUZ.copy(alpha = 0.85f) else TINTA
        // El cordel del que cuelga y un trozo de la perilla.
        drawLine(lc(MINIO, oscuro, 0.5f), Offset(w / 2, 0f), Offset(w / 2, h * 0.12f), 1.4f * u)
        drawCircle(lc(if ((s and 1) == 0) HUESO else EBANO, oscuro, 0.5f), 3 * u, Offset(w / 2, 3 * u))
        rotate((azar(s, 2) - 0.5f) * 5f, Offset(w / 2, h * 0.1f)) {
            val r = Rect(w * 0.1f, h * 0.1f, w * 0.9f, h * 0.86f)
            val forma = Path().apply { moveTo(r.left, r.top); lineTo(r.right, r.top); lineTo(r.right, r.bottom); lineTo(r.center.x, r.bottom - 6 * u); lineTo(r.left, r.bottom); close() }
            translate(1.5f * u, 2 * u) { drawPath(forma, Color.Black.copy(alpha = 0.28f)) }
            drawPath(forma, perg)
            drawRect(lc(MINIO, oscuro, 0.5f), r.topLeft, Size(r.width, 3 * u))
            var y = r.top + 6 * u
            while (y < r.bottom - 6 * u) { drawLine(Color(0x185A4020), Offset(r.left, y), Offset(r.right, y), 0.5f * u); y += 3.5f * u }
            drawPath(forma, lc(PAPIRO_OSC, oscuro, 0.4f), style = Stroke(0.8f * u))
            textoEn(medidor, e.dia, Rect(r.left, r.top + 4 * u, r.right, r.top + r.height * 0.56f), letra, minio, 20f, peso = FontWeight.Bold, minimo = 10f)
            textoEn(medidor, e.semana.uppercase(), Rect(r.left, r.top + r.height * 0.54f, r.right, r.top + r.height * 0.74f), letra, tinta, 9f, minimo = 7f)
            // El sello que cierra el cordel: de cera del color del ánimo; si no hay, de plomo.
            val sello = Offset(r.center.x, r.bottom - 4 * u)
            drawCircle(Color.Black.copy(alpha = 0.3f), 6.6f * u, sello + Offset(1 * u, 1.4f * u))
            drawCircle(e.tono ?: lc(PLOMO, oscuro, 0.5f), 6.2f * u, sello)
            drawCircle(Color.Black.copy(alpha = 0.22f), 4 * u, sello, style = Stroke(0.8f * u))
            drawCircle(Color.White.copy(alpha = 0.35f), 1.6f * u, sello + Offset(-2.2f * u, -2.2f * u))
        }
    }

    override fun DrawScope.rotuloMes(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // La tabula ansata de mármol: la placa con sus dos asas en cola de milano, el marco moldurado y las letras en minio.
        val marmol = lc(MARMOL, oscuro, 0.35f)
        val oreja = h * 0.9f
        val r = Rect(oreja, 2 * u, w - oreja, h - 3 * u)
        val placa = Path().apply {
            moveTo(r.left, r.top); lineTo(r.right, r.top)
            lineTo(w - 3 * u, r.top + 2 * u); lineTo(r.right + oreja * 0.35f, r.center.y); lineTo(w - 3 * u, r.bottom - 2 * u)
            lineTo(r.right, r.bottom); lineTo(r.left, r.bottom)
            lineTo(3 * u, r.bottom - 2 * u); lineTo(r.left - oreja * 0.35f, r.center.y); lineTo(3 * u, r.top + 2 * u); close()
        }
        translate(2 * u, 2.5f * u) { drawPath(placa, Color.Black.copy(alpha = 0.3f)) }
        drawPath(placa, marmol)
        clipPath(placa) { vetas(Rect(0f, 0f, w, h), lc(MARMOL_VETA, oscuro, 0.35f).copy(alpha = 0.4f), texto.length + 7, densidad = 0.4f, ondulacion = 2f) }
        drawPath(placa, lc(MARMOL_SOMBRA, oscuro, 0.35f), style = Stroke(1 * u))
        bisel(r.deflate(3 * u), Color.Black.copy(alpha = 0.16f), Color.White.copy(alpha = 0.4f), 1.6f * u)
        val minio = legibleAnt(marmol, lc(MINIO, oscuro, 0.2f), mezcla(MINIO, LLAMA, 0.55f))
        listOf(r.left - oreja * 0.08f, r.right + oreja * 0.08f).forEach { x -> drawCircle(lc(BRONCE, oscuro, 0.5f), 2.4f * u, Offset(x, r.center.y)) }
        drawPath(estrella(Offset(r.left + 12 * u, r.center.y), 4 * u, 4, 0.4f), minio)
        drawPath(estrella(Offset(r.right - 12 * u, r.center.y), 4 * u, 4, 0.4f), minio)
        textoEn(medidor, texto.uppercase(), Rect(r.left + 22 * u, 0f, r.right - 22 * u, h), letra, minio, 15f, espaciado = 1.5f, minimo = 8f)
    }

    // ---------------------------------------------------------------- las cartas

    private val HAYA = Color(0xFFCDA670)
    private val HAYA_LUZ = Color(0xFFE2C48E)
    private val HAYA_OSC = Color(0xFF8E6A3A)
    private val HAYA_VETA = Color(0xFF7A5630)
    private val CERA_NEGRA = Color(0xFF2E241C)
    private val CERA_RAYA = Color(0xFFB59A70)
    private val LINO = Color(0xFFEDE4CC)
    private val LACRE = Color(0xFF8E2B1F)

    /** El color de las cartas con su luz: de día, tal cual; en modo oscuro, hundido en la penumbra del tablinum. */
    private fun cc(x: Color, oscuro: Boolean): Color = if (oscuro) mezcla(x, Color(0xFF100D0B), 0.55f) else x

    /**
     * El sello del anillo, centrado en [c]: un óvalo de cera roja más alto que ancho, con su orla hundida y, tallados en la
     * piedra, la corona de laurel y una hedera en medio.
     */
    private fun DrawScope.selloRomano(c: Offset, r: Float, oscuro: Boolean) {
        // El lacre se hunde menos que la madera en modo oscuro, para que el sello siga leyéndose.
        fun k(x: Color) = if (oscuro) mezcla(x, Color(0xFF100D0B), 0.3f) else x
        val o = Rect(c.x - r * 0.84f, c.y - r * 1.04f, c.x + r * 0.84f, c.y + r * 1.04f)
        val cera = k(LACRE)
        // La cera derramada alrededor de la piedra, y el óvalo apretado por el anillo.
        drawPath(discoAnt(c, r * 1.12f, 31, 18, 0.12f), Color.Black.copy(alpha = 0.3f), style = Stroke(r * 0.12f))
        drawPath(discoAnt(c, r * 1.1f, 31, 18, 0.12f), cera)
        drawOval(k(mezcla(LACRE, Color.Black, 0.28f)), o.topLeft, o.size)
        drawOval(k(mezcla(LACRE, Color.White, 0.12f)), Offset(o.left + r * 0.1f, o.top + r * 0.1f), Size(o.width - r * 0.2f, o.height - r * 0.2f))
        drawOval(k(mezcla(LACRE, Color.Black, 0.35f)), Offset(o.left + r * 0.2f, o.top + r * 0.2f), Size(o.width - r * 0.4f, o.height - r * 0.4f), style = Stroke(r * 0.06f))
        val relieve = k(mezcla(LACRE, Color(0xFFF3D9B0), 0.5f))
        coronaLaurel(Offset(c.x, c.y - r * 0.05f), r * 0.58f, relieve, relieve)
        hedera(Offset(c.x, c.y - r * 0.05f), r * 0.42f, relieve)
        drawOval(Color.White.copy(alpha = 0.22f), Offset(o.left + r * 0.24f, o.top + r * 0.22f), Size(r * 0.36f, r * 0.2f))
    }

    /** Una tablilla de haya en [r]: el marco levantado con su veta y, si [cera], la cera negra con renglones rayados con el estilo. */
    private fun DrawScope.tablilla(r: Rect, oscuro: Boolean, cera: Boolean, semilla: Int) {
        val u = density
        val rad = CornerRadius(r.height * 0.04f)
        drawRoundRect(cc(HAYA, oscuro), r.topLeft, r.size, rad)
        clipRect(r.left, r.top, r.right, r.bottom) { vetas(r, cc(HAYA_VETA, oscuro).copy(alpha = 0.35f), semilla, densidad = 0.5f, grosor = 0.7f) }
        val m = minOf(r.width, r.height) * 0.09f
        val dentro = Rect(r.left + m, r.top + m, r.right - m, r.bottom - m)
        if (cera) {
            drawRect(cc(CERA_NEGRA, oscuro), dentro.topLeft, dentro.size)
            var y = dentro.top + dentro.height * 0.16f; var i = 0
            while (y < dentro.bottom - 3 * u) {
                drawLine(cc(CERA_RAYA, oscuro).copy(alpha = 0.5f), Offset(dentro.left + dentro.width * 0.06f, y), Offset(dentro.right - dentro.width * (if (i % 3 == 2) 0.4f else 0.08f), y), 0.8f * u)
                y += maxOf(dentro.height * 0.13f, 4 * u); i++
            }
            drawRect(Color.Black.copy(alpha = 0.35f), dentro.topLeft, Size(dentro.width, 1.6f * u))
        } else {
            // La cara de fuera: un rebaje liso para la dirección, con su filete de minio.
            drawRect(cc(HAYA_LUZ, oscuro).copy(alpha = 0.55f), dentro.topLeft, dentro.size)
            drawRect(cc(MINIO, oscuro).copy(alpha = 0.7f), Offset(dentro.left + 2 * u, dentro.top + 2 * u), Size(dentro.width - 4 * u, dentro.height - 4 * u), style = Stroke(0.8f * u))
        }
        bisel(r, cc(HAYA_LUZ, oscuro).copy(alpha = 0.7f), cc(HAYA_OSC, oscuro).copy(alpha = 0.8f), 1.4f * u)
        drawRoundRect(cc(HAYA_OSC, oscuro), r.topLeft, r.size, rad, style = Stroke(0.8f * u))
    }

    /**
     * La carta de Roma: unas tabellae, el díptico de haya. Atrás, la tablilla con su cera negra rayada; delante, la tablilla
     * de fuera con la dirección escrita a tinta en el rebaje con su filete de minio, y los dos agujeros por donde pasa el hilo
     * de lino. Las de al futuro se ciñen con el lino ([EstadoCarta.cordon]), que da la vuelta a las dos tablillas; encima del
     * nudo, el sello del anillo. Al abrirse, el sello se parte, la tablilla de fuera se inclina hacia delante sobre sus
     * bisagras de abajo y de entre las dos sube la hoja de papiro. Un degradado: el brillo del sello.
     */
    override fun DrawScope.sobre(e: EstadoCarta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val q = e.cuerpo
        val W = q.width; val H = q.height; val cx = q.center.x
        val ab = e.abrir.coerceIn(0f, 1f)
        fun c(x: Color) = cc(x, oscuro)
        val caja = Rect(q.left + W * 0.04f, q.top + H * 0.04f, q.right - W * 0.04f, q.bottom - H * 0.06f)
        // La sombra del díptico en la mesa.
        drawRoundRect(Color.Black.copy(alpha = 0.28f), caja.topLeft + Offset(2.5f * u, 4 * u), caja.size, CornerRadius(H * 0.05f))
        // La tablilla de atrás, con la cera; asoma por arriba un poco, más alta que la de delante.
        val atras = Rect(caja.left + W * 0.01f, caja.top - H * 0.02f, caja.right - W * 0.01f, caja.bottom - H * 0.02f)
        tablilla(atras, oscuro, true, e.semilla)
        // La hoja de papiro, que sale de entre las dos tablillas.
        if (ab > 0.3f || e.subir > 0f) hojaCartaAnt(q, e.subir, e.hoja, e.renglones, oscuro, 0.12f, 0.88f, fibras = true)
        // La tablilla de fuera: con [ab] su canto de arriba se inclina hacia delante (baja) y se ve el grosor de la madera.
        val baja = H * 0.22f * ab
        val frente = Rect(caja.left, caja.top + baja, caja.right, caja.bottom)
        if (ab > 0f) {
            drawRect(Color.Black.copy(alpha = 0.35f * ab), Offset(atras.left, frente.top - 3 * u), Size(atras.width, 3 * u))
            drawRect(c(HAYA_LUZ), Offset(frente.left, frente.top - H * 0.035f * ab), Size(frente.width, H * 0.035f * ab))
        }
        tablilla(frente, oscuro, false, e.semilla + 7)
        // Los dos agujeros del hilo, a media altura del sello, y las bisagras de cuerda abajo.
        val rS = minOf(H * 0.14f, W * 0.1f) * (1f - 0.2f * ab)
        val cS = Offset(cx, frente.top + H * (0.27f - 0.1f * ab))
        listOf(frente.left + W * 0.035f, frente.right - W * 0.035f).forEach { x ->
            drawCircle(c(Color(0xFF3A2614)), 2.2f * u, Offset(x, cS.y))
            drawCircle(c(HAYA_LUZ).copy(alpha = 0.6f), 2.2f * u, Offset(x, cS.y), style = Stroke(0.6f * u))
        }
        listOf(0.22f, 0.78f).forEach { f ->
            val x = frente.left + frente.width * f
            drawLine(c(LINO), Offset(x - 3 * u, frente.bottom - 1 * u), Offset(x + 3 * u, frente.bottom + 2.5f * u), 2 * u, StrokeCap.Round)
        }
        // El lino que ciñe las dos tablillas: entra por un agujero, cruza por delante hasta el sello y sale por el otro.
        if (e.cordon > 0f) {
            val y = cS.y
            val ruta = listOf(Offset(frente.left - W * 0.02f, y + 3 * u), Offset(frente.left + W * 0.035f, y), Offset(cx - rS * 0.4f, y + 1 * u),
                Offset(cx + rS * 0.4f, y + 1 * u), Offset(frente.right - W * 0.035f, y), Offset(frente.right + W * 0.02f, y + 3 * u))
            trazoParcialAnt(ruta.map { it + Offset(0f, 1.4f * u) }, e.cordon, Color.Black.copy(alpha = 0.3f), 2.4f * u)
            trazoParcialAnt(ruta, e.cordon, c(LINO), 2 * u)
            // La vuelta por detrás asoma por los cantos.
            if (e.cordon > 0.95f) listOf(frente.left - W * 0.02f, frente.right + W * 0.02f).forEach { x ->
                drawLine(c(LINO), Offset(x, y - 3 * u), Offset(x, y + 4 * u), 2 * u, StrokeCap.Round)
            }
        }
        selloCartaAnt(cS, rS, e.brillo, e.romper.coerceIn(0f, 1f), e.escalaSello, e.alfaSello, LLAMA) { selloRomano(cS, rS, oscuro) }
        // La dirección, a tinta en el rebaje.
        val m = minOf(frente.width, frente.height) * 0.09f
        val fondo = c(mezcla(HAYA, HAYA_LUZ, 0.55f))
        val tinta = legibleAnt(fondo, c(TINTA), HUESO)
        val alto1 = maxOf(cS.y + rS * 1.15f, frente.top + frente.height * 0.5f)
        val pie = frente.bottom - m - 2 * u
        if (e.linea1.isBlank() && e.linea2.isBlank()) {
            // Sin dirección (al abrirla o sellarla, en grande): dos hederas y una raya de minio.
            hedera(Offset(cx - W * 0.2f, (alto1 + pie) / 2), H * 0.1f, c(MINIO).copy(alpha = 0.8f))
            hedera(Offset(cx + W * 0.2f, (alto1 + pie) / 2), H * 0.1f, c(MINIO).copy(alpha = 0.8f))
            drawLine(c(TINTA).copy(alpha = 0.45f), Offset(cx - W * 0.12f, (alto1 + pie) / 2), Offset(cx + W * 0.12f, (alto1 + pie) / 2), 1.2f * u)
        }
        val corte = alto1 + (pie - alto1) * 0.58f
        lineasCartaAnt(medidor, e.linea1, e.linea2, Rect(frente.left + m + 4 * u, alto1, frente.right - m - 4 * u, corte),
            Rect(frente.left + m + 6 * u, corte, frente.right - m - 6 * u, pie), Letra.inscripcion, tinta, 12f, 9.5f)
    }

    override val cajaPrevia: Rect = Rect(0.17f, 0.175f, 0.83f, 0.345f)

    override val altoBuzon: Dp = 200.dp

    /** Una carta chica dentro del arca, de pie, con el centro de su canto de arriba en [o]: lista, atada o abierta. */
    private fun DrawScope.tabellaChica(o: Offset, w: Float, h: Float, estado: Int, oscuro: Boolean, semilla: Int) {
        val u = density
        val r = Rect(o.x - w / 2, o.y, o.x + w / 2, o.y + h)
        if (estado == 2) {
            // Abierta: la hoja de papiro afuera, un poco ladeada, delante de la tablilla.
            drawRoundRect(cc(HAYA_OSC, oscuro), r.topLeft, r.size, CornerRadius(1.5f * u))
            rotate(-10f + 20f * azar(semilla, 2), Offset(o.x, o.y + h)) {
                val p = Rect(r.left + w * 0.1f, r.top - h * 0.25f, r.right - w * 0.1f, r.bottom)
                drawRect(Color.Black.copy(alpha = 0.2f), p.topLeft + Offset(1.5f * u, 1.5f * u), p.size)
                drawRect(cc(PAPIROS[semilla % PAPIROS.size], oscuro), p.topLeft, p.size)
                for (j in 0 until 4) drawLine(cc(TINTA, oscuro).copy(alpha = 0.45f), Offset(p.left + 3 * u, p.top + h * (0.14f + 0.12f * j)), Offset(p.right - (3 + 4 * (j % 2)) * u, p.top + h * (0.14f + 0.12f * j)), 0.8f * u)
            }
            return
        }
        drawRoundRect(Color.Black.copy(alpha = 0.25f), r.topLeft + Offset(1.5f * u, 1.5f * u), r.size, CornerRadius(1.5f * u))
        drawRoundRect(cc(HAYA, oscuro), r.topLeft, r.size, CornerRadius(1.5f * u))
        drawRect(cc(HAYA_LUZ, oscuro), Offset(r.left + 2 * u, r.top + 2 * u), Size(w - 4 * u, h - 4 * u), style = Stroke(0.8f * u))
        drawLine(cc(HAYA_OSC, oscuro), Offset(r.left, r.top + 1 * u), Offset(r.right, r.top + 1 * u), 1 * u)
        val cy = r.top + h * 0.3f
        if (estado == 1) {
            drawLine(cc(LINO, oscuro), Offset(r.left - 1 * u, cy), Offset(r.right + 1 * u, cy), 1.6f * u)
            drawLine(cc(LINO, oscuro), Offset(o.x, r.top - 1 * u), Offset(o.x, r.bottom), 1.2f * u)
        }
        drawOval(cc(LACRE, oscuro), Offset(o.x - w * 0.16f, cy - w * 0.2f), Size(w * 0.32f, w * 0.4f))
        drawOval(cc(mezcla(LACRE, Color.White, 0.3f), oscuro), Offset(o.x - w * 0.09f, cy - w * 0.12f), Size(w * 0.18f, w * 0.24f), style = Stroke(0.7f * u))
    }

    /**
     * El buzón de Roma: el arca de los tabellarii, un cofre de haya con cantoneras y bandas de bronce, sobre patas de león, en el
     * atrio de muro pompeyano y suelo de teselas. La tapa está abierta hacia atrás y dentro, de pie en tres apartados, asoman
     * las tabellae: a la izquierda las que ya se pueden abrir, con el sello brillando; en medio las selladas, ceñidas con lino;
     * a la derecha las abiertas, con su papiro afuera. En el frente, bajo cada apartado, un titulus de bronce con el nombre y la
     * cuenta en números romanos; en medio, la cerradura. En modo oscuro el atrio se apaga. Un degradado: el brillo de las listas.
     */
    override fun DrawScope.buzon(e: EstadoBuzon, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = cc(x, oscuro)
        // El muro: el panel rojo pompeyano con su marco ocre, el zócalo negro y el suelo de teselas.
        drawRect(c(POMPEYANO))
        drawRect(c(OCRE), Offset(w * 0.03f, h * 0.05f), Size(w * 0.94f, h * 0.62f), style = Stroke(1.2f * u))
        drawRect(c(NEGRO), Offset(0f, h * 0.7f), Size(w, h * 0.18f))
        drawLine(c(OCRE), Offset(0f, h * 0.7f), Offset(w, h * 0.7f), 1.4f * u)
        drawRect(c(MARMOL), Offset(0f, h * 0.88f), Size(w, h * 0.12f))
        var x = 0f; var k = 0
        while (x < w) { if (k % 2 == 0) drawRect(c(NEGRO), Offset(x, h * 0.9f), Size(5 * u, 5 * u)); x += 5 * u; k++ }
        // La tapa del arca, abierta hacia atrás contra el muro.
        val arca = Rect(w * 0.05f, h * 0.36f, w * 0.95f, h * 0.84f)
        val tapa = Rect(arca.left + w * 0.02f, h * 0.06f, arca.right - w * 0.02f, arca.top)
        drawRect(Color.Black.copy(alpha = 0.3f), tapa.topLeft + Offset(3 * u, 3 * u), tapa.size)
        drawRect(c(HAYA_OSC), tapa.topLeft, tapa.size)
        vetas(tapa, c(HAYA_VETA).copy(alpha = 0.5f), 71, densidad = 0.5f)
        drawRect(c(BRONCE), Offset(tapa.left, tapa.top), Size(tapa.width, 3 * u))
        listOf(0.06f, 0.94f).forEach { f -> drawRect(c(BRONCE_OSC), Offset(tapa.left + tapa.width * f - 3 * u, tapa.top), Size(6 * u, tapa.height)) }
        // El hueco del arca, oscuro, entre la tapa y el canto del frente.
        drawRect(c(Color(0xFF241810)), Offset(arca.left + 4 * u, arca.top - h * 0.06f), Size(arca.width - 8 * u, h * 0.08f))
        // Las cartas de pie en sus apartados.
        val zw = arca.width / 3f
        val cuentas = listOf(e.listas, e.selladas, e.abiertas)
        for (g in 0 until 3) {
            val n = minOf(cuentas[g], 3)
            val cx = arca.left + zw * (g + 0.5f)
            if (g == 0 && n > 0) mancha(Offset(cx, arca.top - 8 * u), zw * 0.62f, LLAMA.copy(alpha = if (oscuro) 0.6f else 0.5f))
            for (i in 0 until n) {
                val xx = cx + (i - (n - 1) / 2f) * 24 * u
                tabellaChica(Offset(xx, arca.top - (36 + 5 * ((i + g) % 2)) * u), 20 * u, 52 * u, g, oscuro, 40 * g + i)
            }
            if (g == 0 && n > 0) for (i in 0 until n) drawPath(estrella(Offset(cx + (i - (n - 1) / 2f) * 24 * u + 8 * u, arca.top - (40 + 5 * (i % 2)) * u), 3.4f * u, 4, 0.25f, 0f), LLAMA)
        }
        // El frente del arca, con su veta, las bandas y cantoneras de bronce y los clavos.
        drawRect(Color.Black.copy(alpha = 0.3f), arca.topLeft + Offset(3 * u, 4 * u), arca.size)
        drawRect(c(HAYA), arca.topLeft, arca.size)
        vetas(arca, c(HAYA_VETA).copy(alpha = 0.4f), 72, densidad = 0.55f)
        bisel(arca, c(HAYA_LUZ).copy(alpha = 0.6f), c(HAYA_OSC).copy(alpha = 0.7f), 2 * u)
        listOf(arca.top + 2 * u, arca.bottom - 7 * u).forEach { y -> drawRect(c(BRONCE), Offset(arca.left, y), Size(arca.width, 5 * u)) }
        for (g in 1 until 3) drawRect(c(BRONCE_OSC), Offset(arca.left + zw * g - 2.5f * u, arca.top), Size(5 * u, arca.height))
        listOf(arca.left, arca.right - 9 * u).forEach { xx -> drawRect(c(BRONCE_OSC), Offset(xx, arca.top), Size(9 * u, arca.height)) }
        var cl = arca.left + 14 * u
        while (cl < arca.right - 10 * u) {
            listOf(arca.top + 4.5f * u, arca.bottom - 4.5f * u).forEach { y -> drawCircle(c(BRONCE_LUZ), 1.4f * u, Offset(cl, y)) }
            cl += 12 * u
        }
        // Las patas de león.
        listOf(arca.left + 14 * u, arca.right - 14 * u).forEach { xx ->
            drawPath(Path().apply {
                moveTo(xx - 7 * u, arca.bottom); lineTo(xx + 7 * u, arca.bottom); lineTo(xx + 9 * u, h * 0.9f); lineTo(xx - 9 * u, h * 0.9f); close()
            }, c(BRONCE_OSC))
            for (j in -1..1) drawCircle(c(BRONCE), 3 * u, Offset(xx + j * 5 * u, h * 0.9f - 1 * u))
        }
        // Los tituli de bronce, con el nombre y la cuenta en números romanos; la cerradura, en el apartado de en medio.
        val nombres = listOf("PARA ABRIR", "SELLADAS", "ABIERTAS")
        for (g in 0 until 3) {
            val cx = arca.left + zw * (g + 0.5f)
            val placa = Rect(cx - zw * 0.38f, arca.top + arca.height * 0.2f, cx + zw * 0.38f, arca.top + arca.height * 0.74f)
            drawRect(Color.Black.copy(alpha = 0.3f), placa.topLeft + Offset(1.5f * u, 2 * u), placa.size)
            drawRect(c(BRONCE), placa.topLeft, placa.size)
            bisel(placa, c(BRONCE_LUZ), c(BRONCE_OSC), 1.6f * u)
            // Las orejas de la tabula ansata.
            listOf(-1f, 1f).forEach { s ->
                val bx = if (s < 0) placa.left else placa.right
                drawPath(Path().apply { moveTo(bx, placa.top + placa.height * 0.3f); lineTo(bx + s * 6 * u, placa.center.y); lineTo(bx, placa.bottom - placa.height * 0.3f); close() }, c(BRONCE))
            }
            val letra = legibleAnt(c(BRONCE), c(NEGRO), MARMOL_LUZ)
            textoEn(medidor, nombres[g], Rect(placa.left + 3 * u, placa.top + 2 * u, placa.right - 3 * u, placa.top + placa.height * 0.4f), Letra.inscripcion, letra, 9f, peso = FontWeight.Bold, espaciado = 0.3f, minimo = 5.5f)
            drawLine(letra.copy(alpha = 0.5f), Offset(placa.left + 6 * u, placa.top + placa.height * 0.42f), Offset(placa.right - 6 * u, placa.top + placa.height * 0.42f), 0.7f * u)
            textoEn(medidor, romanoAnt(cuentas[g]), Rect(placa.left + 3 * u, placa.top + placa.height * 0.44f, placa.right - 3 * u, placa.bottom - 2 * u), Letra.inscripcion, letra, 17f, peso = FontWeight.Bold, minimo = 8f)
        }
        // La cerradura: la placa con el ojo de la llave, bajo el titulus de en medio.
        val ck = Offset(arca.center.x, arca.top + arca.height * 0.86f)
        drawCircle(c(BRONCE_LUZ), 5 * u, ck)
        drawCircle(c(NEGRO), 1.6f * u, Offset(ck.x, ck.y - 0.8f * u))
        drawRect(c(NEGRO), Offset(ck.x - 0.8f * u, ck.y), Size(1.6f * u, 2.6f * u))
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
 * con sus ojos. La tapa es ese pinax grande y de frente: el marco con su meandro tallado y el estilo apoyado, la fecha
 * incisa en la cera, el olivo (de noche, la luna y la lechuza) y las otras hojas por el canto; al abrirse, la hoja de fuera
 * gira sobre su cordón y deja ver la de dentro, llena de renglones. Distinto del Ideario, que es la stoa dórica con sus
 * ánforas y sus óstraka.
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

    // ---------------------------------------------------------------- la tapa

    override val altoTapa: Dp = 184.dp
    override val cajaTapa = Rect(0.15f, 0.31f, 0.86f, 0.71f)
    override val letraTapa: FontFamily = Letra.garamond
    override val tamTapa = 13.5f
    override val lineasTapa = 3
    override val centradoTapa = false

    /** Lo escrito con el estilo deja la cera clara a la vista: sobre la cera oscura se lee en las cuatro luces. */
    override fun tintaTapa(oscuro: Boolean, noche: Boolean): Color = INCISO
    override fun palabrasTapa(n: Int): String = palabrasAnt(n)

    /** Una hoja del díptico: el marco de madera con su veta y su bisel y la cera hundida; devuelve dónde quedó la cera. */
    private fun DrawScope.hojaPinax(r: Rect, madera: Color, cera: Color, semilla: Int): Rect {
        val u = density
        drawRoundRect(madera, r.topLeft, r.size, CornerRadius(3 * u))
        vetas(r.deflate(1.5f * u), Color.Black.copy(alpha = 0.2f), semilla, densidad = 0.45f, grosor = 0.7f, ondulacion = 1.6f)
        bisel(r, Color.White.copy(alpha = 0.16f), Color.Black.copy(alpha = 0.24f), 1.6f * u)
        val dentro = Rect(r.left + r.width * 0.075f, r.top + r.height * 0.1f, r.right - r.width * 0.035f, r.bottom - r.height * 0.1f)
        drawRect(cera, dentro.topLeft, dentro.size)
        drawRect(Color.Black.copy(alpha = 0.45f), dentro.topLeft, Size(dentro.width, 2.2f * u))
        drawRect(Color.Black.copy(alpha = 0.28f), dentro.topLeft, Size(2 * u, dentro.height))
        drawRect(Color.White.copy(alpha = 0.1f), Offset(dentro.left, dentro.bottom - 1.4f * u), Size(dentro.width, 1.4f * u))
        drawRect(Color.White.copy(alpha = 0.06f), Offset(dentro.right - 1.2f * u, dentro.top), Size(1.2f * u, dentro.height))
        return dentro
    }

    /** El meandro (la greca) en una franja que empieza en [o]: cada vuelta, un gancho cuadrado. */
    private fun DrawScope.meandro(o: Offset, largo: Float, alto: Float, color: Color) {
        val u = density
        val m = alto / 3f
        val paso = m * 4
        val p = Path()
        var x = 0f
        while (x + paso <= largo) {
            val x0 = o.x + x
            p.moveTo(x0, o.y + alto / 2); p.lineTo(x0, o.y - alto / 2); p.lineTo(x0 + m * 3, o.y - alto / 2); p.lineTo(x0 + m * 3, o.y + m / 2)
            p.lineTo(x0 + m, o.y + m / 2); p.lineTo(x0 + m, o.y - m / 2); p.lineTo(x0 + m * 2, o.y - m / 2)
            p.moveTo(x0, o.y + alto / 2); p.lineTo(x0 + paso, o.y + alto / 2)
            x += paso
        }
        drawPath(p, color, style = Stroke(0.9f * u))
    }

    /** La tapa, recortada a su lienzo: lo que se aparta al abrir y la luz de noche no se salen de su lugar. */
    override fun DrawScope.tapa(e: EstadoTapa, medidor: TextMeasurer, oscuro: Boolean) = clipRect { dibujarTapa(e, medidor, oscuro) }

    private fun DrawScope.dibujarTapa(e: EstadoTapa, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, e.noche)
        val a = e.abierto.coerceIn(0f, 1f)
        val sem = e.semilla and 0x7fffffff
        val madera = c(MADERAS[sem % MADERAS.size])
        val cera = c(CERA)
        // Lo inciso deja ver la cera clara de abajo: se hunde poco, aun de noche, para que la fecha se siga leyendo.
        val inciso = mezcla(mezcla(INCISO, NOCHE, if (e.noche) 0.18f else 0f), Color.Black, if (oscuro) 0.14f else 0f)
        val tabla = Rect(w * 0.03f, h * 0.05f, w * 0.95f, h * 0.91f)
        // Las otras hojas del díptico por el canto de abajo y de la derecha: más cuanto más escribiste.
        val hojas = 1 + ((e.palabras / 800f).coerceIn(0f, 1f) * 3).toInt()
        for (j in hojas downTo 1) {
            val o = Offset(j * 1.7f * u, j * 2.3f * u)
            drawRoundRect(mezcla(madera, Color.Black, 0.16f + 0.07f * j), tabla.topLeft + o, tabla.size, CornerRadius(3 * u))
            drawRoundRect(Color.Black.copy(alpha = 0.3f), tabla.topLeft + o, tabla.size, CornerRadius(3 * u), style = Stroke(0.6f * u))
        }
        // La hoja de dentro (se ve al abrir): la cera llena de renglones incisos.
        if (a > 0.01f) {
            val dentro = hojaPinax(tabla, madera, cera, sem + 11)
            var y = dentro.top + 9 * u
            var n = 0
            while (y < dentro.bottom - 6 * u) {
                val largo = dentro.width * (0.6f + 0.3f * azar(e.semilla, 300 + n))
                drawLine(inciso.copy(alpha = 0.55f), Offset(dentro.left + 9 * u, y), Offset(dentro.left + 9 * u + largo, y), 1f * u, StrokeCap.Round)
                y += 6.5f * u; n++
            }
        }
        // La hoja de fuera gira sobre el cordón de la izquierda y deja ver la de dentro.
        withTransform({ scale(1f - 0.93f * a, 1f, Offset(tabla.left, tabla.center.y)) }) {
            val dentro = hojaPinax(tabla, madera, cera, sem)
            // El meandro tallado en el marco de arriba, y el estilo de bronce apoyado a su lado.
            meandro(Offset(tabla.left + tabla.width * 0.1f, tabla.top + (dentro.top - tabla.top) / 2), tabla.width * 0.42f, (dentro.top - tabla.top) * 0.45f,
                mezcla(madera, Color.Black, 0.45f))
            val ye = tabla.top + (dentro.top - tabla.top) * 0.5f
            val e0 = Offset(tabla.left + tabla.width * 0.58f, ye); val e1 = Offset(tabla.right - tabla.width * 0.05f, ye - 0.6f * u)
            drawLine(Color.Black.copy(alpha = 0.3f), e0 + Offset(1 * u, 1.5f * u), e1 + Offset(1 * u, 1.5f * u), 2.4f * u, StrokeCap.Round)
            drawLine(c(PICO), e0, e1, 2.2f * u, StrokeCap.Round)
            drawPath(Path().apply { moveTo(e0.x, e0.y - 1.1f * u); lineTo(e0.x - 7 * u, e0.y); lineTo(e0.x, e0.y + 1.1f * u); close() }, c(PICO))
            drawRect(c(PICO), Offset(e1.x - 1 * u, e1.y - 2.6f * u), Size(3.6f * u, 5.2f * u))
            drawLine(Color.White.copy(alpha = 0.35f), e0 + Offset(0f, -0.6f * u), e1 + Offset(-2 * u, -0.6f * u), 0.6f * u)
            // La fecha incisa arriba de la cera, con un filete doble debajo.
            val zona = Rect(dentro.left + 6 * u, dentro.top + 3 * u, dentro.right - 6 * u, h * 0.265f)
            val fechas = renglonesFechaAnt(e.fecha, 30)
            if (fechas.size == 1) {
                textoEn(medidor, fechas[0], zona, letra, inciso, 16f, peso = FontWeight.SemiBold, espaciado = 0.5f, minimo = 8f)
            } else {
                val corte = zona.top + zona.height * 0.58f
                textoEn(medidor, fechas[0], Rect(zona.left, zona.top, zona.right, corte), letra, inciso, 14f, peso = FontWeight.SemiBold, minimo = 8f)
                textoEn(medidor, fechas[1], Rect(zona.left, corte, zona.right, zona.bottom), letra, inciso.copy(alpha = 0.85f), 11f, minimo = 7f)
            }
            listOf(0.278f, 0.29f).forEach { f -> drawLine(inciso.copy(alpha = 0.55f), Offset(dentro.left + 10 * u, h * f), Offset(dentro.right - 10 * u, h * f), 0.7f * u) }
            // Abajo: un ramito de olivo inciso (de noche, la luna y unas estrellas) y las palabras.
            val cr = Offset(w * 0.27f, h * 0.78f)
            val tam = h * 0.05f
            if (e.noche) {
                drawCircle(LUNA, tam, cr)
                drawCircle(cera, tam * 0.82f, Offset(cr.x + tam * 0.42f, cr.y - tam * 0.26f))
                listOf(Offset(-2.4f, -0.6f), Offset(1.8f, 0.9f), Offset(2.8f, -0.9f)).forEach { o ->
                    drawPath(estrella(Offset(cr.x + tam * o.x, cr.y + tam * o.y), 2.6f * u, 4, 0.35f), LUNA.copy(alpha = 0.85f))
                }
            } else {
                drawLine(inciso.copy(alpha = 0.85f), Offset(cr.x - tam * 2f, cr.y + tam * 0.5f), Offset(cr.x + tam * 2f, cr.y - tam * 0.5f), 0.9f * u)
                for (j in 0 until 7) {
                    val f = -1.7f + 0.55f * j
                    val o = Offset(cr.x + tam * f, cr.y - tam * f * 0.25f)
                    val lado0 = if (j % 2 == 0) -1f else 1f
                    drawLine(inciso.copy(alpha = 0.85f), o, Offset(o.x + tam * 0.35f, o.y + lado0 * tam * 0.55f), 1.6f * u, cap = StrokeCap.Round)
                }
                drawCircle(inciso.copy(alpha = 0.85f), 1.6f * u, Offset(cr.x + tam * 0.4f, cr.y + tam * 0.35f))
                drawCircle(inciso.copy(alpha = 0.85f), 1.6f * u, Offset(cr.x - tam * 0.9f, cr.y - tam * 0.05f))
            }
            if (e.palabras > 0) {
                textoEn(medidor, palabrasTapa(e.palabras), Rect(w * 0.44f, h * 0.73f, dentro.right - 6 * u, dentro.bottom - 2 * u), letra,
                    inciso.copy(alpha = 0.8f), 11f, espaciado = 0.4f, minimo = 7f)
            }
            // Los dos agujeros del canto izquierdo, con el cordón que une las hojas.
            val hx = tabla.left + tabla.width * 0.037f
            val a1 = Offset(hx, tabla.top + tabla.height * 0.3f); val a2 = Offset(hx, tabla.top + tabla.height * 0.7f)
            drawCircle(Color.Black.copy(alpha = 0.55f), 2 * u, a1)
            drawCircle(Color.Black.copy(alpha = 0.55f), 2 * u, a2)
            drawLine(c(CORDON), a1, a2, 1.6f * u)
            // Al cerrar, el girar de la hoja la oscurece.
            drawRect(Color.Black.copy(alpha = 0.45f * a), tabla.topLeft, tabla.size)
        }
        // El cordón cuelga del agujero de abajo, con su nudo.
        val hx = tabla.left + tabla.width * 0.037f
        val a2 = Offset(hx, tabla.top + tabla.height * 0.7f)
        val nudo = Offset(hx + 3 * u, tabla.bottom + 5 * u)
        drawPath(Path().apply { moveTo(a2.x, a2.y); quadraticTo(hx - 4 * u, tabla.bottom - 6 * u, nudo.x, nudo.y) }, c(CORDON), style = Stroke(1.6f * u))
        drawCircle(c(CORDON), 2.2f * u, nudo)
        drawLine(c(CORDON), nudo, Offset(nudo.x - 2 * u, nudo.y + 4 * u), 1.2f * u)
        drawLine(c(CORDON), nudo, Offset(nudo.x + 2.5f * u, nudo.y + 3.5f * u), 1.2f * u)
        // De noche llega la lechuza y se posa en la esquina de abajo de la tablilla.
        if (e.noche) lechuza(Offset(tabla.left + tabla.width * 0.085f, tabla.bottom - 1 * u), h * 0.22f)
    }

    // ---------------------------------------------------------------- la página y el escritor

    override val altoEncabezado: Dp = 118.dp

    /** La ola corrida de los vasos: espirales que ruedan una tras otra sobre su línea de base, a partir de [o]. */
    private fun DrawScope.olaCorrida(o: Offset, largo: Float, alto: Float, color: Color) {
        val u = density
        val r = alto / 2
        val paso = alto * 1.4f
        val p = Path()
        var x = 0f
        while (x + paso <= largo) {
            val x0 = o.x + x
            p.moveTo(x0, o.y + r)
            p.cubicTo(x0 + paso * 0.45f, o.y + r, x0 + paso * 0.25f, o.y - r, x0 + paso * 0.65f, o.y - r)
            p.cubicTo(x0 + paso * 0.98f, o.y - r, x0 + paso * 0.98f, o.y + r * 0.15f, x0 + paso * 0.7f, o.y + r * 0.15f)
            p.cubicTo(x0 + paso * 0.5f, o.y + r * 0.15f, x0 + paso * 0.52f, o.y - r * 0.45f, x0 + paso * 0.7f, o.y - r * 0.4f)
            x += paso
        }
        drawPath(p, color, style = Stroke(1.1f * u))
        drawLine(color, Offset(o.x, o.y + r), Offset(o.x + x, o.y + r), 0.9f * u)
    }

    /** Una palmeta (anthemion): el abanico de hojas sobre su volutas, centrada en [o], de alto [m]. */
    private fun DrawScope.palmeta(o: Offset, m: Float, color: Color) {
        val u = density
        for (k in -3..3) {
            val a = Math.toRadians(-90.0 + k * 22.0)
            val largo = m * (0.75f - kotlin.math.abs(k) * 0.07f)
            val fin = Offset(o.x + largo * cos(a).toFloat(), o.y + largo * sin(a).toFloat())
            drawLine(color, o, fin, 1.6f * u, StrokeCap.Round)
            drawCircle(color, 1.6f * u, fin)
        }
        listOf(-1f, 1f).forEach { s ->
            val rv = m * 0.14f
            drawArc(color, -90f, s * -270f, false, Offset(o.x + s * rv - rv, o.y), Size(rv * 2, rv * 2), style = Stroke(1f * u))
        }
    }

    /**
     * La lechuza de Atenea acuñada en un tetradracma: el disco de plata con su borde, la lechuza de perfil de frente con sus
     * ojos grandes y la ramita de olivo; de noche, la moneda se vuelve la luna y la lechuza abre los ojos dorados.
     */
    private fun DrawScope.monedaLechuza(c: Offset, r: Float, p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val plata = if (noche) LUNA else if (p.oscura) mezcla(p.color, p.tinta, 0.3f) else tonoAnt(Color(0xFFCFCBC0), p, oscuro)
        val figura = if (noche) mezcla(NOCHE, BARNIZ, 0.5f) else BARNIZ
        drawCircle(Color.Black.copy(alpha = 0.22f), r, Offset(c.x + 1.2f * u, c.y + 1.8f * u))
        drawCircle(plata, r, c)
        drawCircle(figura.copy(alpha = 0.4f), r * 0.9f, c, style = Stroke(0.8f * u))
        val pie = Offset(c.x - r * 0.08f, c.y + r * 0.6f)
        val a = r * 1.15f
        drawOval(figura, Offset(pie.x - a * 0.26f, pie.y - a * 0.68f), Size(a * 0.52f, a * 0.68f))
        val cabeza = Rect(pie.x - a * 0.25f, pie.y - a * 1.0f, pie.x + a * 0.25f, pie.y - a * 0.58f)
        drawOval(figura, cabeza.topLeft, cabeza.size)
        listOf(-1f, 1f).forEach { s ->
            val o = Offset(pie.x + s * a * 0.11f, cabeza.center.y)
            drawCircle(plata, a * 0.095f, o)
            drawCircle(if (noche) OJO else figura, a * 0.05f, o)
        }
        drawLine(figura, Offset(pie.x - a * 0.1f, pie.y), Offset(pie.x + a * 0.15f, pie.y), 1f * u)
        // La ramita de olivo a la derecha y la luna creciente arriba a la izquierda, como en la moneda.
        val rama = Offset(c.x + r * 0.5f, c.y - r * 0.15f)
        drawLine(figura, Offset(rama.x - r * 0.1f, rama.y + r * 0.35f), Offset(rama.x + r * 0.1f, rama.y - r * 0.3f), 0.8f * u)
        for (k in 0 until 3) drawOval(figura, Offset(rama.x - r * (0.02f + 0.06f * k) + (if (k % 2 == 0) r * 0.04f else -r * 0.14f), rama.y - r * 0.25f + k * r * 0.2f), Size(r * 0.12f, r * 0.07f))
        val luna = Offset(c.x - r * 0.52f, c.y - r * 0.42f)
        drawCircle(figura, r * 0.11f, luna)
        drawCircle(plata, r * 0.09f, Offset(luna.x + r * 0.05f, luna.y - r * 0.02f))
    }

    /** Un ánfora de figuras negras: el cuerpo, el cuello, las asas, el pie y la franja de terracota con su meandro. */
    private fun DrawScope.anfora(c: Offset, alto: Float, barniz: Color, barro: Color) {
        val u = density
        val cuerpo = Path().apply {
            moveTo(c.x - alto * 0.1f, c.y - alto * 0.36f)
            cubicTo(c.x - alto * 0.38f, c.y - alto * 0.2f, c.x - alto * 0.3f, c.y + alto * 0.3f, c.x - alto * 0.06f, c.y + alto * 0.4f)
            lineTo(c.x + alto * 0.06f, c.y + alto * 0.4f)
            cubicTo(c.x + alto * 0.3f, c.y + alto * 0.3f, c.x + alto * 0.38f, c.y - alto * 0.2f, c.x + alto * 0.1f, c.y - alto * 0.36f)
            close()
        }
        drawPath(cuerpo, barniz)
        drawRect(barniz, Offset(c.x - alto * 0.08f, c.y - alto * 0.5f), Size(alto * 0.16f, alto * 0.15f))
        drawRect(barniz, Offset(c.x - alto * 0.12f, c.y - alto * 0.52f), Size(alto * 0.24f, alto * 0.04f))
        drawRect(barniz, Offset(c.x - alto * 0.1f, c.y + alto * 0.4f), Size(alto * 0.2f, alto * 0.08f))
        listOf(-1f, 1f).forEach { s ->
            drawArc(barniz, if (s < 0) 90f else -90f, 180f, false, Offset(c.x + s * alto * 0.14f - alto * 0.07f, c.y - alto * 0.47f), Size(alto * 0.14f, alto * 0.22f), style = Stroke(1.4f * u))
        }
        drawRect(barro, Offset(c.x - alto * 0.3f, c.y - alto * 0.04f), Size(alto * 0.6f, alto * 0.13f))
        clipPath(cuerpo) {
            val y = c.y + alto * 0.025f
            var x = c.x - alto * 0.3f
            while (x < c.x + alto * 0.3f) {
                drawLine(barniz, Offset(x, y + alto * 0.04f), Offset(x, y - alto * 0.04f), 0.8f * u)
                drawLine(barniz, Offset(x, y - alto * 0.04f), Offset(x + alto * 0.04f, y - alto * 0.04f), 0.8f * u)
                x += alto * 0.07f
            }
        }
    }

    /**
     * El encabezado: el friso de un vaso. Arriba, el meandro entre sus dos filetes; abajo, la ola corrida; a la izquierda,
     * la lechuza en su tetradracma (de noche, en la luna y con los ojos dorados) y, a la derecha, un ánfora de figuras negras;
     * en medio, el rótulo en mayúsculas griegas espaciadas, la fecha grande y el detalle.
     */
    override fun DrawScope.encabezado(e: EstadoEncabezado, p: Pagina, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val friso = Rect(0f, 2 * u, w, h - 2 * u)
        // La franja del friso, apenas más honda que el papel (de noche, un poco más clara), con su luz de lámpara de noche.
        drawRect(if (p.oscura) p.tinta.copy(alpha = 0.05f) else p.acento.copy(alpha = 0.06f), friso.topLeft, friso.size)
        if (e.noche) mancha(Offset(w * 0.12f, h * 0.5f), h * 1.1f, LLAMA.copy(alpha = 0.12f))
        val linea = p.tinta.copy(alpha = 0.75f)
        meandro(Rect(6 * u, 6 * u, w - 6 * u, 17 * u), linea)
        olaCorrida(Offset(8 * u, h - 13 * u), w - 16 * u, 9 * u, p.acento.copy(alpha = 0.85f))
        drawLine(linea, Offset(6 * u, h - 4 * u), Offset(w - 6 * u, h - 4 * u), 1.2f * u)
        monedaLechuza(Offset(30 * u, h * 0.5f), 22 * u, p, oscuro, e.noche)
        anfora(Offset(w - 28 * u, h * 0.5f + 1 * u), 46 * u, if (p.oscura) p.tinta.copy(alpha = 0.85f) else BARNIZ, if (p.oscura) p.acento else tonoAnt(TERRACOTA, p, oscuro))
        val izq = 60 * u
        val der = w - 54 * u
        textoEn(medidor, e.rotulo, Rect(44 * u, 20 * u, w - 44 * u, 38 * u), Letra.garamond, p.acento, 13.5f, espaciado = 1.2f, minimo = 8f)
        fechaEncAnt(medidor, e.fecha, Rect(izq, 39 * u, der, 78 * u), Letra.garamond, p.tinta, 24f, 22, FontWeight.Bold)
        textoEn(medidor, e.detalle, Rect(izq - 6 * u, 79 * u, der + 6 * u, 94 * u), Letra.garamond, p.tenue, 11f, minimo = 7f)
    }

    /**
     * El pergamino de Pérgamo: la doble regla de cada margen con su hilera de puntos, la palmeta en las esquinas de abajo y,
     * al pie, el meandro sobre la ola corrida; de noche, la luna creciente abajo a la derecha. En modo oscuro, el
     * pergamino en la penumbra y la luz de una lámpara de aceite desde abajo a la derecha.
     */
    override fun DrawScope.pagina(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val tinta = p.detalle.copy(alpha = 0.3f)
        listOf(11 * u, 15 * u, w - 15 * u, w - 11 * u).forEach { x -> drawLine(tinta, Offset(x, 0f), Offset(x, h - 52 * u), 0.8f * u) }
        var y = 80 * u
        while (y < h - 70 * u) {
            listOf(13 * u, w - 13 * u).forEach { x -> drawCircle(p.acento.copy(alpha = 0.3f), 1.3f * u, Offset(x, y)) }
            y += 22 * u
        }
        meandro(Rect(16 * u, h - 46 * u, w - 16 * u, h - 36 * u), p.detalle.copy(alpha = 0.35f))
        olaCorrida(Offset(18 * u, h - 22 * u), w - 36 * u, 9 * u, p.acento.copy(alpha = 0.4f))
        palmeta(Offset(13 * u, h - 30 * u), 20 * u, p.acento.copy(alpha = 0.35f))
        palmeta(Offset(w - 13 * u, h - 30 * u), 20 * u, p.acento.copy(alpha = 0.35f))
        if (noche) {
            val luna = Offset(w - 44 * u, h - 128 * u)
            drawCircle(p.acento.copy(alpha = 0.3f), 12 * u, luna)
            drawCircle(p.color, 10 * u, Offset(luna.x + 6 * u, luna.y - 4 * u))
        }
        if (oscuro && !p.oscura) penumbraAnt(Offset(w * 1.03f, h * 0.98f), LLAMA, 0.22f)
    }

    /**
     * La barra: la cornisa de mármol de la stoa (un degradado, con luz arriba) y, al pie, el meandro de figuras negras entre
     * sus dos filetes.
     */
    override fun DrawScope.barra(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val marmol = if (p.oscura) mezcla(p.color, p.tinta, 0.07f) else tonoAnt(MARMOL, p, oscuro)
        val luz = if (p.oscura) mezcla(p.color, p.tinta, 0.12f) else tonoAnt(MARMOL_LUZ, p, oscuro)
        drawRect(Brush.verticalGradient(0f to luz, 0.6f to marmol, 1f to mezcla(marmol, MARMOL_SOMBRA, 0.5f), startY = 0f, endY = h))
        drawLine(p.tinta.copy(alpha = 0.15f), Offset(0f, 3 * u), Offset(w, 3 * u), 0.8f * u)
        meandro(Rect(0f, h - 9 * u, w, h - 1.5f * u), (if (p.oscura) p.tinta else BARNIZ).copy(alpha = 0.45f))
    }

    /**
     * El botón de guardar: una franja de vaso de figuras negras, el barniz negro (un degradado, con su brillo arriba) con un
     * filete de terracota arriba y abajo, la hilera de puntos y la sombra plana.
     */
    override fun DrawScope.botonGuardar(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val r = Rect(2 * u, 2 * u, w - 3 * u, h - 4 * u)
        drawRoundRect(Color.Black.copy(alpha = 0.3f), Offset(r.left + 1.5f * u, r.top + 2.5f * u), r.size, CornerRadius(h * 0.22f))
        drawRoundRect(Brush.verticalGradient(listOf(Color(0xFF3A302A), BARNIZ, Color(0xFF0E0A08)), r.top, r.bottom), r.topLeft, r.size, CornerRadius(h * 0.22f))
        listOf(r.top + 4 * u, r.bottom - 4 * u).forEach { y -> drawLine(TERRACOTA, Offset(r.left + 8 * u, y), Offset(r.right - 8 * u, y), 1.2f * u) }
        listOf(r.left + 7 * u, r.right - 7 * u).forEach { x -> drawCircle(TERRACOTA, 1.8f * u, Offset(x, r.center.y)) }
    }

    override fun tintaBoton(p: Pagina, oscuro: Boolean, noche: Boolean): Color = Color(0xFFEBB58A)

    // ---------------------------------------------------------------- la libreta de momentos

    override val altoLibreta: Dp = 252.dp

    /**
     * El color de la libreta con su luz: de día, tal cual; en modo oscuro, hundido en la penumbra del cuarto salvo donde llega
     * la lámpara ([luz], de 0 lejos a 1 junto a la llama, que además lo entibia).
     */
    private fun lc(x: Color, oscuro: Boolean, luz: Float = 0f): Color =
        if (!oscuro) x else mezcla(mezcla(x, Color(0xFF0B0908), 0.7f - 0.42f * luz), LLAMA, 0.12f * luz)

    /** Una pata de león tallada: el muslo que se abomba, el tobillo delgado y la garra con sus dedos, de [arriba] a [abajo]. */
    private fun DrawScope.pataLeon(x: Float, arriba: Float, abajo: Float, ancho: Float, madera: Color, hacia: Float) {
        val u = density
        val alto = abajo - arriba
        val pata = Path().apply {
            moveTo(x - ancho / 2, arriba)
            cubicTo(x - ancho * (0.9f - 0.4f * hacia), arriba + alto * 0.3f, x - ancho * 0.1f, arriba + alto * 0.55f, x - ancho * 0.22f, abajo - alto * 0.2f)
            lineTo(x - ancho * 0.55f + hacia * ancho * 0.3f, abajo)
            lineTo(x + ancho * 0.55f + hacia * ancho * 0.3f, abajo)
            lineTo(x + ancho * 0.22f, abajo - alto * 0.2f)
            cubicTo(x + ancho * 0.3f, arriba + alto * 0.55f, x + ancho * (0.9f + 0.4f * hacia), arriba + alto * 0.3f, x + ancho / 2, arriba)
            close()
        }
        drawPath(pata, madera)
        drawPath(pata, Color.Black.copy(alpha = 0.3f), style = Stroke(0.8f * u))
        drawPath(Path().apply { moveTo(x + ancho * 0.1f, arriba + 2 * u); cubicTo(x + ancho * 0.6f, arriba + alto * 0.3f, x + ancho * 0.1f, arriba + alto * 0.55f, x + ancho * 0.12f, abajo - alto * 0.22f) },
            Color.Black.copy(alpha = 0.22f), style = Stroke(ancho * 0.18f))
        for (k in 0 until 3) {
            val dx = x - ancho * 0.36f + hacia * ancho * 0.3f + k * ancho * 0.36f
            drawOval(mezcla(madera, Color.White, 0.12f), Offset(dx - ancho * 0.17f, abajo - ancho * 0.32f), Size(ancho * 0.34f, ancho * 0.36f))
            drawLine(Color.Black.copy(alpha = 0.35f), Offset(dx + ancho * 0.17f, abajo - ancho * 0.3f), Offset(dx + ancho * 0.17f, abajo), 0.7f * u)
        }
        for (k in 0 until 3) {
            val y = arriba + alto * (0.62f + 0.04f * k)
            drawLine(Color.Black.copy(alpha = 0.25f), Offset(x - ancho * 0.2f, y), Offset(x + ancho * 0.2f, y + 2 * u), 0.7f * u)
        }
    }

    /**
     * La libreta de momentos: las tablillas de cera atadas, sobre la mesa de patas de león. Las tablillas se apilan, vistas un
     * poco desde arriba: por el canto se ven sus maderas (más cuanto más escribiste) y por detrás las atan los cordones. La de
     * encima es la tapa: madera con un filete de hueso embutido y una plaquita de boj con el nombre y la cuenta escritos a
     * tinta. En la mesa, la lámpara de barro con su llama, el estilo de bronce y una jarrita con una rama de olivo. Al abrirse,
     * la tapa gira sobre los cordones de atrás y se levanta: deja ver la cera de la primera tablilla, con sus renglones
     * incisos, y por dentro la cera de la tapa. En modo oscuro el cuarto se apaga y alumbra la lámpara. Un degradado: el
     * resplandor de la lámpara.
     */
    override fun DrawScope.libreta(e: EstadoLibreta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val a = e.abierto.coerceIn(0f, 1f)
        val gordo = (kotlin.math.sqrt(e.hojas.coerceAtLeast(1).toFloat()) / 14f).coerceIn(0.07f, 1f)
        val s = e.semilla
        fun c(x: Color, luz: Float = 0f) = lc(x, oscuro, luz)
        // La pared encalada con su banda de olas azules; abajo, el zócalo rojo y el piso de baldosas.
        val cal = mezcla(MARMOL, TERRACOTA, 0.12f)
        val zocalo = h * 0.6f; val piso = h * 0.88f
        drawRect(c(cal, 0.15f), Offset.Zero, Size(w, zocalo))
        drawRect(c(EGEO, 0.15f), Offset(0f, h * 0.07f), Size(w, 2 * u))
        olaCorrida(Offset(4 * u, h * 0.115f), w - 8 * u, h * 0.05f, c(EGEO, 0.15f))
        drawRect(c(EGEO, 0.15f), Offset(0f, h * 0.155f), Size(w, 1.4f * u))
        drawRect(c(MURO, 0.15f), Offset(0f, zocalo), Size(w, piso - zocalo))
        drawRect(c(MURO_OSC, 0.15f), Offset(0f, zocalo), Size(w, 3 * u))
        drawRect(c(BALDOSA, 0.3f), Offset(0f, piso), Size(w, h - piso))
        var bx = -((s and 0xff) % 20) * u
        while (bx < w) { drawLine(Color.Black.copy(alpha = 0.25f), Offset(bx, piso), Offset(bx - 10 * u, h), 1 * u); bx += 30 * u }
        drawLine(Color.Black.copy(alpha = 0.25f), Offset(0f, piso + (h - piso) * 0.5f), Offset(w, piso + (h - piso) * 0.5f), 1 * u)
        // Una corona de olivo colgada de un clavo, a la derecha.
        val corona = Offset(w * 0.86f, h * 0.36f)
        drawCircle(c(PLUMA, 0.15f), 1.6f * u, Offset(corona.x, h * 0.2f))
        drawLine(c(CORDON, 0.15f), Offset(corona.x, h * 0.2f), Offset(corona.x, corona.y - h * 0.09f), 0.8f * u)
        drawCircle(c(Color(0xFF6B7A44), 0.15f), h * 0.09f, corona, style = Stroke(1.4f * u))
        for (k in 0 until 14) {
            val ang = k / 14f * 6.283f
            val p = Offset(corona.x + h * 0.09f * cos(ang), corona.y + h * 0.09f * sin(ang))
            rotate(ang * 57.3f + 40f, p) { drawOval(c(if (k % 2 == 0) Color(0xFF6B7A44) else Color(0xFF8A9A5E), 0.15f), Offset(p.x - 5 * u, p.y - 2 * u), Size(10 * u, 4 * u)) }
        }
        // El resplandor de la lámpara (el único degradado).
        val llama = Offset(w * 0.12f, h * 0.38f)
        mancha(llama, if (oscuro) w * 0.9f else w * 0.3f, LLAMA.copy(alpha = if (oscuro) 0.42f else 0.28f))
        // La mesa: el tablero de madera con su canto moldurado, el faldón y tres patas de león.
        val madera = c(MADERAS[3], 0.5f)
        val maderaLuz = c(MADERAS[2], 0.5f)
        pataLeon(w * 0.5f, h * 0.68f, piso + 2 * u, w * 0.06f, mezcla(madera, Color.Black, 0.25f), 0f)
        pataLeon(w * 0.13f, h * 0.68f, piso + 8 * u, w * 0.075f, madera, -0.6f)
        pataLeon(w * 0.87f, h * 0.68f, piso + 8 * u, w * 0.075f, madera, 0.6f)
        val tablero = Rect(w * 0.03f, h * 0.57f, w * 0.97f, h * 0.635f)
        drawRect(c(MADERAS[0], 0.5f), Offset(tablero.left, h * 0.54f), Size(tablero.width, h * 0.035f))
        drawRect(madera, tablero.topLeft, tablero.size)
        vetas(tablero, Color.Black.copy(alpha = 0.18f), s + 4, densidad = 0.6f)
        drawRect(maderaLuz, tablero.topLeft, Size(tablero.width, 2 * u))
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(tablero.left, tablero.bottom - 2 * u), Size(tablero.width, 2 * u))
        drawRect(mezcla(madera, Color.Black, 0.2f), Offset(w * 0.08f, tablero.bottom), Size(w * 0.84f, h * 0.05f))
        for (k in 0 until 18) drawCircle(maderaLuz, 1.4f * u, Offset(w * 0.1f + k * w * 0.047f, tablero.bottom + h * 0.025f))
        // La lámpara de barro, a la izquierda, con su asa, su pico y la llama.
        val barro = c(TERRACOTA, 1f)
        val lb = Offset(w * 0.1f, h * 0.535f)
        drawOval(Color.Black.copy(alpha = 0.25f), Offset(lb.x - 18 * u, lb.y + 2 * u), Size(40 * u, 6 * u))
        drawPath(Path().apply { moveTo(lb.x - 16 * u, lb.y); quadraticTo(lb.x - 16 * u, lb.y - 13 * u, lb.x, lb.y - 13 * u); quadraticTo(lb.x + 12 * u, lb.y - 12 * u, lb.x + 22 * u, lb.y - 6 * u)
            lineTo(lb.x + 25 * u, lb.y - 3 * u); quadraticTo(lb.x + 16 * u, lb.y + 2 * u, lb.x - 16 * u, lb.y); close() }, barro)
        asaLampara(Offset(lb.x - 20 * u, lb.y - 12 * u), 10 * u, c(BARNIZ, 1f))
        drawOval(c(BARNIZ, 1f), Offset(lb.x - 7 * u, lb.y - 14 * u), Size(10 * u, 3 * u))
        drawLine(c(BARNIZ, 1f).copy(alpha = 0.7f), Offset(lb.x - 14 * u, lb.y - 4 * u), Offset(lb.x + 18 * u, lb.y - 4 * u), 1 * u)
        drawPath(Path().apply { moveTo(lb.x + 21 * u, lb.y - 6 * u); quadraticTo(lb.x + 18 * u, llama.y + 6 * u, lb.x + 25 * u, llama.y - 4 * u); quadraticTo(lb.x + 30 * u, llama.y + 8 * u, lb.x + 25 * u, lb.y - 6 * u); close() }, LLAMA)
        drawCircle(MARMOL_LUZ, 1.6f * u, Offset(lb.x + 24 * u, lb.y - 10 * u))
        // La jarrita con la rama de olivo, a la derecha.
        val jx = w * 0.88f; val jy = tablero.top + 3 * u
        val rama = c(Color(0xFF6B7A44), 0.4f)
        drawLine(c(PLUMA, 0.4f), Offset(jx, jy - h * 0.12f), Offset(jx + w * 0.04f, jy - h * 0.3f), 1.2f * u)
        for (k in 0 until 7) {
            val p = Offset(jx + w * 0.04f * k / 7f, jy - h * 0.12f - h * 0.18f * k / 7f)
            rotate(if (k % 2 == 0) -50f else 30f, p) { drawOval(rama, Offset(p.x, p.y - 1.6f * u), Size(11 * u, 3.4f * u)) }
        }
        val jarra = Path().apply { moveTo(jx - 6 * u, jy - h * 0.13f); lineTo(jx + 6 * u, jy - h * 0.13f); quadraticTo(jx + 4 * u, jy - h * 0.1f, jx + 12 * u, jy - h * 0.05f)
            quadraticTo(jx + 14 * u, jy, jx + 6 * u, jy); lineTo(jx - 6 * u, jy); quadraticTo(jx - 14 * u, jy, jx - 12 * u, jy - h * 0.05f); quadraticTo(jx - 4 * u, jy - h * 0.1f, jx - 6 * u, jy - h * 0.13f); close() }
        drawPath(jarra, c(BARNIZ, 0.4f))
        drawRect(c(TERRACOTA, 0.4f), Offset(jx - 12 * u, jy - h * 0.055f), Size(24 * u, 2.4f * u))
        drawLine(c(TERRACOTA, 0.4f), Offset(jx - 9 * u, jy - h * 0.035f), Offset(jx + 9 * u, jy - h * 0.035f), 1 * u)
        // Las tablillas apiladas: el canto con sus maderas, la primera tablilla con su cera y la tapa encima.
        val ancho = w * 0.54f
        val x0 = w * 0.5f - ancho / 2; val x1 = x0 + ancho
        val base = tablero.top + h * 0.03f
        val grosor = (5 + 26 * gordo) * u
        val fondo = h * 0.27f
        val tope = base - grosor - fondo
        val n = (1 + e.hojas / 6).coerceIn(1, 16)
        drawRect(Color.Black.copy(alpha = 0.28f), Offset(x0 + 5 * u, tope + 6 * u), Size(ancho, fondo + grosor))
        for (k in 0 until n) {
            val y0 = base - grosor + grosor * k / n
            val m = c(MADERAS[(s + k).and(0x7fffffff) % MADERAS.size], 0.55f)
            drawRect(m, Offset(x0 + (k % 2) * 1.2f * u, y0), Size(ancho - (k % 2) * 1.2f * u, grosor / n + 0.5f))
            drawLine(Color.Black.copy(alpha = 0.3f), Offset(x0, y0), Offset(x1, y0), 0.6f * u)
        }
        drawRect(Color.Black.copy(alpha = 0.18f), Offset(x1 - ancho * 0.08f, base - grosor), Size(ancho * 0.08f, grosor))
        // La primera tablilla (debajo de la tapa): el marco y la cera con los renglones incisos.
        val superficie = Rect(x0, tope, x1, base - grosor)
        drawRect(c(MADERAS[0], 0.55f), superficie.topLeft, superficie.size)
        val cera = superficie.deflate(ancho * 0.05f)
        drawRect(c(CERA, 0.55f), cera.topLeft, cera.size)
        val inciso = c(INCISO, 0.55f).copy(alpha = 0.8f)
        var ry = cera.top + 7 * u; var r = 0
        while (ry < cera.bottom - 4 * u) {
            val largo = cera.width * (0.7f + 0.25f * azar(s, 700 + r))
            var rx = cera.left + 6 * u
            while (rx < cera.left + largo) { val l = (6 + 14 * azar(s + r, (rx / u).toInt())) * u; drawLine(inciso, Offset(rx, ry), Offset(minOf(rx + l, cera.right - 5 * u), ry), 1 * u); rx += l + 4 * u }
            ry += 7 * u; r++
        }
        // La tapa: gira sobre los cordones de atrás. Mientras se ve por fuera, se acorta; al pasar de canto, se ve por dentro.
        val ang = Math.toRadians(62.0 * kotlin.math.sqrt(a.toDouble()))
        val k = (kotlin.math.cos(ang) - 1.5 * kotlin.math.sin(ang)).toFloat()
        val tapa = c(MADERAS[(s and 0x7fffffff) % MADERAS.size], 0.55f)
        val bisagra = Offset(w * 0.5f, tope)
        if (k > 0.02f) {
            if (k < 0.99f) drawRect(mezcla(tapa, Color.Black, 0.3f), Offset(x0, tope + fondo * k), Size(ancho, 3 * u))
            withTransform({ scale(1f, k, bisagra) }) {
                drawRect(tapa, superficie.topLeft, superficie.size)
                vetas(superficie, Color.Black.copy(alpha = 0.16f), s + 9, densidad = 0.5f)
                drawRect(Color.White.copy(alpha = 0.1f), superficie.topLeft, Size(ancho, 3 * u))
                // El filete de hueso embutido y las rosetas de las esquinas.
                val filete = superficie.deflate(ancho * 0.045f)
                drawRect(c(MARMOL_LUZ, 0.55f), filete.topLeft, filete.size, style = Stroke(1.6f * u))
                drawRect(c(MARMOL_LUZ, 0.55f).copy(alpha = 0.6f), filete.deflate(4 * u).topLeft, filete.deflate(4 * u).size, style = Stroke(0.7f * u))
                listOf(filete.topLeft, Offset(filete.right, filete.top), Offset(filete.left, filete.bottom), filete.bottomRight).forEach { p ->
                    drawPath(estrella(p, 5 * u, 8, 0.45f), c(MARMOL_OSC, 0.55f))
                }
                // La plaquita de boj con el nombre y la cuenta.
                val placa = Rect(filete.left + ancho * 0.07f, filete.top + fondo * 0.15f, filete.right - ancho * 0.07f, filete.bottom - fondo * 0.13f)
                val boj = c(Color(0xFFE6CF98), 0.75f)
                drawRect(Color.Black.copy(alpha = 0.3f), Offset(placa.left + 1.5f * u, placa.top + 2 * u), placa.size)
                drawRect(boj, placa.topLeft, placa.size)
                bisel(placa, Color.White.copy(alpha = 0.35f), Color.Black.copy(alpha = 0.2f), 1.4f * u)
                drawRect(c(EGEO, 0.75f), Offset(placa.left + 4 * u, placa.top + 4 * u), Size(placa.width - 8 * u, placa.height - 8 * u), style = Stroke(0.8f * u))
                textoEn(medidor, e.nombre, Rect(placa.left + 8 * u, placa.top + placa.height * 0.1f, placa.right - 8 * u, placa.top + placa.height * 0.58f), letra,
                    legibleAnt(boj, c(BARNIZ, 0.4f), MARMOL_LUZ), 17f, peso = FontWeight.Bold, minimo = 7f)
                textoEn(medidor, e.cuenta, Rect(placa.left + 8 * u, placa.top + placa.height * 0.56f, placa.right - 8 * u, placa.bottom - placa.height * 0.1f), letra,
                    legibleAnt(boj, c(EGEO, 0.4f), MARMOL_LUZ), 12f, minimo = 6.5f)
            }
        } else if (k < -0.02f) {
            // Por dentro: la cera de la tapa, levantada detrás de la bisagra, con unos renglones ya escritos.
            val dentro = Rect(x0, tope - fondo * -k, x1, tope)
            drawRect(mezcla(tapa, Color.Black, 0.12f), dentro.topLeft, dentro.size)
            val ceraDentro = Rect(dentro.left + ancho * 0.05f, dentro.top + dentro.height * 0.08f, dentro.right - ancho * 0.05f, dentro.bottom - dentro.height * 0.08f)
            drawRect(c(CERA, 0.55f), ceraDentro.topLeft, ceraDentro.size)
            val paso = 7 * u * -k
            var yy = ceraDentro.top + paso; var j = 0
            while (yy < ceraDentro.bottom - 2 * u && paso > 2 * u) {
                drawLine(inciso, Offset(ceraDentro.left + 6 * u, yy), Offset(ceraDentro.left + ceraDentro.width * (0.5f + 0.4f * azar(s, 800 + j)), yy), 1 * u); yy += paso; j++
            }
            drawRect(mezcla(tapa, Color.White, 0.15f), Offset(x0, tope - 3 * u), Size(ancho, 3 * u))
        }
        // Los cordones de atrás, que atan las tablillas y hacen de bisagra.
        val cordon = c(CORDON, 0.55f)
        listOf(0.25f, 0.75f).forEach { f ->
            val cx = x0 + ancho * f
            drawOval(cordon, Offset(cx - 7 * u, tope - 4 * u), Size(14 * u, 8 * u), style = Stroke(2 * u))
            drawLine(cordon, Offset(cx, tope + 3 * u), Offset(cx + 3 * u, tope + 11 * u), 1.6f * u, StrokeCap.Round)
        }
        // El estilo de bronce delante, con su punta y su espátula para borrar.
        val bronce = c(Color(0xFFB98B4A), 0.5f)
        val e0 = Offset(w * 0.24f, tablero.top + h * 0.035f); val e1 = Offset(w * 0.56f, tablero.top + h * 0.02f)
        drawLine(Color.Black.copy(alpha = 0.25f), e0 + Offset(0f, 3 * u), e1 + Offset(0f, 3 * u), 2.4f * u, StrokeCap.Round)
        drawLine(bronce, e0, e1, 2.2f * u, StrokeCap.Round)
        drawLine(bronce, e1, Offset(e1.x + 10 * u, e1.y - 0.5f * u), 1 * u, StrokeCap.Round)
        drawRoundRect(bronce, Offset(e0.x - 9 * u, e0.y - 3 * u), Size(10 * u, 6 * u), CornerRadius(2 * u))
        for (j in 0 until 3) drawLine(Color.Black.copy(alpha = 0.35f), Offset(e0.x + (8 + 3 * j) * u, e0.y - 2 * u), Offset(e0.x + (8 + 3 * j) * u, e0.y + 2 * u), 0.7f * u)
    }

    /** Un asa: medio aro abierto hacia la derecha, de radio [r] y su centro en [o]. */
    private fun DrawScope.asaLampara(o: Offset, r: Float, color: Color) =
        drawArc(color, 90f, 180f, false, Offset(o.x, o.y), Size(r, r * 1.2f), style = Stroke(2.2f * density))

    override fun DrawScope.pestana(e: EstadoPestana, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val s = e.semilla
        // Una tablilla chica: el marco de madera, la cera oscura con el día inciso y el cordón con su cuenta del color del ánimo.
        val madera = lc(MADERAS[(s and 0x7fffffff) % MADERAS.size], oscuro, 0.4f)
        val r = Rect(w * 0.06f, h * 0.16f, w * 0.94f, h * 0.98f)
        drawRect(Color.Black.copy(alpha = 0.28f), Offset(r.left + 1.5f * u, r.top + 2 * u), r.size)
        drawRect(madera, r.topLeft, r.size)
        vetas(r, Color.Black.copy(alpha = 0.15f), s, densidad = 0.5f, vertical = true)
        bisel(r, Color.White.copy(alpha = 0.2f), Color.Black.copy(alpha = 0.25f), 1.2f * u)
        val cera = Rect(r.left + 4 * u, r.top + 4 * u, r.right - 4 * u, r.bottom - 4 * u)
        drawRect(lc(CERA, oscuro, 0.4f), cera.topLeft, cera.size)
        drawLine(Color.Black.copy(alpha = 0.4f), cera.topLeft, Offset(cera.right, cera.top), 1 * u)
        val inciso = if (oscuro) INCISO else mezcla(INCISO, Color.White, 0.3f)
        textoEn(medidor, e.dia, Rect(cera.left, cera.top + 2 * u, cera.right, cera.top + cera.height * 0.62f), letra, inciso, 20f, peso = FontWeight.Bold, minimo = 10f)
        textoEn(medidor, e.semana, Rect(cera.left, cera.top + cera.height * 0.6f, cera.right, cera.bottom - 2 * u), letra, inciso.copy(alpha = 0.75f), 9.5f, minimo = 7f)
        // El cordón que sale por el agujero de arriba; la cuenta lleva el color del ánimo.
        val cordon = lc(CORDON, oscuro, 0.5f)
        drawCircle(Color.Black.copy(alpha = 0.5f), 1.8f * u, Offset(w / 2, r.top + 2.2f * u))
        drawPath(Path().apply { moveTo(w / 2, r.top + 2 * u); quadraticTo(w / 2 - 6 * u, 0f, w / 2 + (azar(s, 3) - 0.5f) * 8 * u, 1 * u) }, cordon, style = Stroke(1.4f * u))
        if (e.tono != null) {
            val p = Offset(w * 0.78f, h * 0.12f)
            drawLine(cordon, Offset(w / 2, r.top + 2 * u), p, 1.2f * u)
            drawCircle(Color.Black.copy(alpha = 0.3f), 5.4f * u, p + Offset(0.8f * u, 1 * u))
            drawCircle(e.tono, 5 * u, p)
            drawCircle(Color.White.copy(alpha = 0.4f), 1.4f * u, p + Offset(-1.6f * u, -1.6f * u))
        }
    }

    override fun DrawScope.rotuloMes(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // La banda de mármol con el meandro azul a los dos lados y el mes en azul del Egeo.
        val marmol = lc(MARMOL, oscuro, 0.35f)
        val r = Rect(2 * u, 3 * u, w - 2 * u, h - 3 * u)
        drawRect(Color.Black.copy(alpha = 0.28f), Offset(r.left + 2 * u, r.top + 2.5f * u), r.size)
        drawRect(marmol, r.topLeft, r.size)
        vetas(r, lc(MARMOL_OSC, oscuro, 0.35f).copy(alpha = 0.5f), texto.length + 3, densidad = 0.4f, ondulacion = 2f)
        bisel(r, Color.White.copy(alpha = 0.4f), Color.Black.copy(alpha = 0.18f), 1.4f * u)
        val azul = legibleAnt(marmol, lc(EGEO, oscuro, 0.2f), Color(0xFF9FC2E6))
        val lado = w * 0.17f
        meandro(Rect(r.left + 6 * u, r.top + 6 * u, r.left + lado, r.bottom - 6 * u), azul)
        meandro(Rect(r.right - lado, r.top + 6 * u, r.right - 6 * u, r.bottom - 6 * u), azul)
        textoEn(medidor, texto, Rect(r.left + lado + 8 * u, 0f, r.right - lado - 8 * u, h), letra, azul, 16f, peso = FontWeight.Bold, espaciado = 0.5f, minimo = 8f)
    }

    // ---------------------------------------------------------------- las cartas

    private val CUERO = Color(0xFF9A5A34)
    private val CUERO_LUZ = Color(0xFFB87448)
    private val CUERO_OSC = Color(0xFF65371E)
    private val PURPURA = Color(0xFF5B2A6E)
    private val PAPIRO = Color(0xFFF1E3C0)
    private val ARCILLA = Color(0xFFB5652F)
    private val HILO = Color(0xFF2B1D14)
    private val OLIVO = Color(0xFF6B7A3E)

    /** El color de las cartas con su luz: de día, tal cual; en modo oscuro, hundido en la penumbra de la casa. */
    private fun cc(x: Color, oscuro: Boolean): Color = if (oscuro) mezcla(x, Color(0xFF0F0C0A), 0.55f) else x

    /**
     * El sello de Grecia, centrado en [c]: arcilla aplastada con el dedo, de borde irregular, con el sol de Vergina de
     * dieciséis rayos impreso y el surco del pulgar alrededor.
     */
    private fun DrawScope.selloHeleno(c: Offset, r: Float, oscuro: Boolean, semilla: Int) {
        fun k(x: Color) = if (oscuro) mezcla(x, Color(0xFF0F0C0A), 0.3f) else x
        val forma = discoAnt(c, r, semilla, 20, 0.1f)
        translate(r * 0.07f, r * 0.12f) { drawPath(forma, Color.Black.copy(alpha = 0.3f)) }
        drawPath(forma, k(ARCILLA))
        drawPath(discoAnt(c, r * 0.86f, semilla + 1, 16, 0.06f), k(mezcla(ARCILLA, Color.Black, 0.25f)).copy(alpha = 0.6f), style = Stroke(r * 0.06f))
        val hondo = k(mezcla(ARCILLA, Color.Black, 0.42f))
        for (j in 0 until 16) {
            val a = (j * Math.PI / 8).toFloat()
            val largo = if (j % 2 == 0) 0.66f else 0.46f
            drawLine(hondo, Offset(c.x + r * 0.2f * cos(a), c.y + r * 0.2f * sin(a)), Offset(c.x + r * largo * cos(a), c.y + r * largo * sin(a)), r * 0.08f, StrokeCap.Round)
        }
        drawCircle(hondo, r * 0.15f, c)
        drawCircle(k(mezcla(ARCILLA, Color.White, 0.35f)).copy(alpha = 0.5f), r * 0.14f, c - Offset(r * 0.42f, r * 0.42f))
    }

    /** Una ramita de olivo incisa: el tallo en curva y sus hojas largas, de [o] hacia la derecha, de largo [m]. */
    private fun DrawScope.ramitaOlivo(o: Offset, m: Float, color: Color) {
        val u = density
        drawPath(Path().apply { moveTo(o.x, o.y); quadraticTo(o.x + m * 0.5f, o.y - m * 0.18f, o.x + m, o.y - m * 0.05f) }, color, style = Stroke(0.9f * u))
        for (j in 0 until 5) {
            val x = o.x + m * (0.15f + 0.17f * j)
            val y = o.y - m * 0.1f - m * 0.03f * j * (1 - j / 5f)
            val lado = if (j % 2 == 0) -1f else 1f
            rotate(lado * 35f - 10f, Offset(x, y)) { drawOval(color, Offset(x - m * 0.035f, y - m * 0.15f * (if (lado < 0) 1f else 0f)), Size(m * 0.07f, m * 0.15f)) }
        }
    }

    /**
     * La carta de Grecia: la funda de cuero del mensajero, con la solapa recta de esquinas redondas y un meandro grabado en su
     * orilla, tachuelas de bronce en las esquinas y, cosida al frente, una etiqueta de papiro con a quién va y las fechas. Las
     * de al futuro se ciñen con dos vueltas de cordón ([EstadoCarta.cordon]); en la orilla de la solapa, el sello de arcilla con
     * el sol de Vergina. Al abrirse, la arcilla se parte, la solapa se levanta y deja ver su forro de púrpura, y sale el papiro.
     * Un degradado: el brillo del sello.
     */
    override fun DrawScope.sobre(e: EstadoCarta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val q = e.cuerpo
        val W = q.width; val H = q.height; val cx = q.center.x
        val ab = e.abrir.coerceIn(0f, 1f)
        val s = e.semilla
        fun c(x: Color) = cc(x, oscuro)
        val caja = Rect(q.left + W * 0.04f, q.top + H * 0.05f, q.right - W * 0.04f, q.bottom - H * 0.05f)
        val rad = CornerRadius(H * 0.07f)
        // La funda: sombra, cuero de atrás.
        drawRoundRect(Color.Black.copy(alpha = 0.28f), caja.topLeft + Offset(2.5f * u, 4 * u), caja.size, rad)
        drawRoundRect(c(CUERO_OSC), caja.topLeft, caja.size, rad)
        val boca = caja.top + H * 0.12f
        if (ab > 0f) drawRect(c(Color(0xFF1E120A)).copy(alpha = ab), Offset(caja.left + 4 * u, caja.top + 2 * u), Size(caja.width - 8 * u, boca - caja.top))
        // La solapa: recta, con las esquinas de abajo redondas; al abrirse se levanta y queda de pie detrás, con su forro.
        val kk = 1f - 2f * ab
        val bordeCerrado = q.top + H * 0.46f
        val bordeY = caja.top + (bordeCerrado - caja.top) * kk
        fun solapa(): Path = Path().apply {
            val r0 = minOf(H * 0.14f, kotlin.math.abs(bordeY - caja.top) * 0.9f)
            addRoundRect(androidx.compose.ui.geometry.RoundRect(Rect(caja.left - 1 * u, minOf(caja.top, bordeY), caja.right + 1 * u, maxOf(caja.top, bordeY)),
                topLeft = CornerRadius(if (kk < 0f) r0 else 0f), topRight = CornerRadius(if (kk < 0f) r0 else 0f),
                bottomLeft = CornerRadius(if (kk >= 0f) r0 else 0f), bottomRight = CornerRadius(if (kk >= 0f) r0 else 0f)))
        }
        if (kk < 0f) {
            drawPath(solapa(), c(PURPURA))
            clipPath(solapa()) { var x = caja.left; while (x < caja.right) { drawLine(c(mezcla(PURPURA, Color.White, 0.2f)).copy(alpha = 0.4f), Offset(x, bordeY), Offset(x + H * 0.2f, caja.top), 1 * u); x += 6 * u } }
            drawPath(solapa(), c(CUERO_OSC), style = Stroke(2.5f * u))
        }
        if (ab > 0.3f || e.subir > 0f) hojaCartaAnt(q, e.subir, e.hoja, e.renglones, oscuro, 0.12f, 0.88f, fibras = true)
        // El frente de la funda.
        val frente = Rect(caja.left, boca, caja.right, caja.bottom)
        drawRoundRect(c(CUERO), frente.topLeft, frente.size, CornerRadius(H * 0.07f))
        clipRect(frente.left, frente.top, frente.right, frente.bottom) {
            for (j in 0 until 18) drawCircle(c(CUERO_OSC).copy(alpha = 0.25f), (0.6f + 1.2f * azar(s, j)) * u, Offset(frente.left + frente.width * azar(s, j + 10), frente.top + frente.height * azar(s, j + 30)))
        }
        drawRoundRect(c(CUERO_LUZ).copy(alpha = 0.7f), Offset(frente.left + 3 * u, frente.top + 3 * u), Size(frente.width - 6 * u, frente.height - 6 * u), CornerRadius(H * 0.05f),
            style = Stroke(0.9f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(3 * u, 2.2f * u))))
        drawLine(c(CUERO_OSC), Offset(frente.left + 2 * u, boca), Offset(frente.right - 2 * u, boca), 1.6f * u)
        // La etiqueta de papiro cosida al frente, con a quién va y las fechas.
        val etiqueta = Rect(caja.left + W * 0.07f, q.top + H * 0.6f, caja.right - W * 0.07f, caja.bottom - H * 0.06f)
        val papiro = c(PAPIRO)
        drawRect(Color.Black.copy(alpha = 0.25f), etiqueta.topLeft + Offset(1.2f * u, 1.6f * u), etiqueta.size)
        drawRect(papiro, etiqueta.topLeft, etiqueta.size)
        clipRect(etiqueta.left, etiqueta.top, etiqueta.right, etiqueta.bottom) {
            var y = etiqueta.top + 2 * u
            while (y < etiqueta.bottom) { drawLine(c(Color(0xFF8A6E3A)).copy(alpha = 0.12f), Offset(etiqueta.left, y), Offset(etiqueta.right, y), 0.7f * u); y += 2.4f * u }
        }
        drawRect(c(HILO).copy(alpha = 0.6f), Offset(etiqueta.left + 2 * u, etiqueta.top + 2 * u), Size(etiqueta.width - 4 * u, etiqueta.height - 4 * u),
            style = Stroke(0.8f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2.5f * u, 2.5f * u))))
        val tinta = legibleAnt(papiro, c(BARNIZ), PAPIRO)
        if (e.linea1.isBlank() && e.linea2.isBlank()) {
            meandro(Offset(etiqueta.left + 8 * u, etiqueta.center.y), etiqueta.width - 16 * u, minOf(etiqueta.height * 0.4f, 10 * u), c(PURPURA).copy(alpha = 0.7f))
        }
        val corte = etiqueta.top + etiqueta.height * 0.58f
        lineasCartaAnt(medidor, e.linea1, e.linea2, Rect(etiqueta.left + 5 * u, etiqueta.top + 2 * u, etiqueta.right - 5 * u, corte),
            Rect(etiqueta.left + 6 * u, corte - 1 * u, etiqueta.right - 6 * u, etiqueta.bottom - 2.5f * u), Letra.garamond, tinta, 12f, 9.5f)
        // Las tachuelas de bronce de las esquinas de abajo.
        listOf(caja.left + W * 0.04f, caja.right - W * 0.04f).forEach { x -> drawCircle(c(Color(0xFFC9A04A)), 2 * u, Offset(x, caja.bottom - H * 0.07f)) }
        // La solapa cerrada (o levantándose): el cuero con el meandro grabado en la orilla y una ramita de olivo.
        if (kk >= 0f) {
            drawPath(solapa(), Color.Black.copy(alpha = 0.22f), style = Stroke(3 * u))
            drawPath(solapa(), c(CUERO_LUZ))
            clipPath(solapa()) {
                for (j in 0 until 12) drawCircle(c(CUERO_OSC).copy(alpha = 0.22f), (0.6f + 1.2f * azar(s, j + 50)) * u, Offset(caja.left + caja.width * azar(s, j + 60), caja.top + (bordeY - caja.top) * azar(s, j + 70)))
                val banda = minOf(H * 0.07f, 9 * u)
                if (bordeY - caja.top > banda * 2) {
                    meandro(Offset(caja.left + W * 0.06f, bordeY - banda * 1.2f), caja.width - W * 0.12f, banda, c(CUERO_OSC))
                    if (ab < 0.25f) {
                        ramitaOlivo(Offset(caja.left + W * 0.08f, caja.top + (bordeY - caja.top) * 0.42f), W * 0.2f, c(CUERO_OSC).copy(alpha = 0.8f))
                        ramitaOlivo(Offset(caja.right - W * 0.28f, caja.top + (bordeY - caja.top) * 0.42f), W * 0.2f, c(CUERO_OSC).copy(alpha = 0.8f))
                    }
                }
            }
            drawPath(solapa(), c(CUERO_OSC), style = Stroke(1 * u))
        }
        // El cordón: dos vueltas que ciñen la funda a la altura de la orilla de la solapa.
        val rS = minOf(H * 0.14f, W * 0.1f)
        val cS = Offset(cx, bordeCerrado)
        if (e.cordon > 0f) listOf(-0.32f, 0.32f).forEachIndexed { i, dy ->
            val y = cS.y + rS * dy
            val av = ((e.cordon - i * 0.4f) / 0.6f).coerceIn(0f, 1f)
            val ruta = listOf(Offset(caja.left - W * 0.01f, y + 1.5f * u), Offset(cx, y), Offset(caja.right + W * 0.01f, y - 1.5f * u))
            trazoParcialAnt(ruta.map { it + Offset(0f, 1.2f * u) }, av, Color.Black.copy(alpha = 0.3f), 2.2f * u)
            trazoParcialAnt(ruta, av, c(HILO), 1.8f * u)
        }
        selloCartaAnt(cS, rS, e.brillo, e.romper.coerceIn(0f, 1f), e.escalaSello, e.alfaSello, LLAMA) { selloHeleno(cS, rS, oscuro, s) }
    }

    override val cajaPrevia: Rect = Rect(0.17f, 0.175f, 0.83f, 0.345f)

    override val altoBuzon: Dp = 200.dp

    /** Una funda chica dentro del canasto, con el centro de su canto de abajo en [o], inclinada [giro] grados: lista, atada o abierta. */
    private fun DrawScope.fundaChica(o: Offset, w: Float, h: Float, giro: Float, estado: Int, oscuro: Boolean) {
        val u = density
        rotate(giro, o) {
            val r = Rect(o.x - w / 2, o.y - h, o.x + w / 2, o.y)
            if (estado == 2) {
                drawRect(cc(PURPURA, oscuro), Offset(r.left, r.top - h * 0.3f), Size(w, h * 0.42f))
                drawRect(cc(PAPIRO, oscuro), Offset(r.left + w * 0.14f, r.top - h * 0.45f), Size(w * 0.72f, h * 0.7f))
                for (j in 0 until 3) drawLine(cc(BARNIZ, oscuro).copy(alpha = 0.45f), Offset(r.left + w * 0.24f, r.top - h * (0.32f - 0.12f * j)), Offset(r.right - w * 0.24f, r.top - h * (0.32f - 0.12f * j)), 0.8f * u)
            }
            drawRoundRect(Color.Black.copy(alpha = 0.25f), r.topLeft + Offset(1.2f * u, 1.2f * u), r.size, CornerRadius(2 * u))
            drawRoundRect(cc(CUERO, oscuro), Offset(r.left, if (estado == 2) r.top + h * 0.1f else r.top), Size(w, if (estado == 2) h * 0.9f else h), CornerRadius(2 * u))
            if (estado != 2) {
                drawRoundRect(cc(CUERO_LUZ, oscuro), r.topLeft, Size(w, h * 0.45f), CornerRadius(2 * u))
                meandro(Offset(r.left + w * 0.1f, r.top + h * 0.37f), w * 0.8f, h * 0.08f, cc(CUERO_OSC, oscuro))
                if (estado == 1) listOf(0.38f, 0.5f).forEach { f -> drawLine(cc(HILO, oscuro), Offset(r.left - 1 * u, r.top + h * f), Offset(r.right + 1 * u, r.top + h * f), 1.1f * u) }
                drawCircle(cc(ARCILLA, oscuro), w * 0.15f, Offset(o.x, r.top + h * 0.45f))
                drawCircle(cc(mezcla(ARCILLA, Color.Black, 0.4f), oscuro), w * 0.07f, Offset(o.x, r.top + h * 0.45f))
            }
            drawRect(cc(PAPIRO, oscuro), Offset(r.left + w * 0.2f, r.top + h * 0.62f), Size(w * 0.6f, h * 0.24f))
        }
    }

    /** Un canasto de mimbre (kalathos) que se abre hacia arriba: boca en [boca] de ancho [w], de alto [h]. */
    private fun DrawScope.kalathos(cx: Float, boca: Float, w: Float, h: Float, oscuro: Boolean) {
        val u = density
        val forma = Path().apply {
            moveTo(cx - w / 2, boca); lineTo(cx + w / 2, boca); lineTo(cx + w * 0.34f, boca + h); lineTo(cx - w * 0.34f, boca + h); close()
        }
        drawPath(forma, cc(Color(0xFFC9A46A), oscuro))
        clipPath(forma) {
            var y = boca + 4 * u; var fila = 0
            while (y < boca + h) {
                var x = cx - w / 2 + (if (fila % 2 == 0) 0f else 3 * u)
                while (x < cx + w / 2) { drawLine(cc(Color(0xFF8E6A3A), oscuro), Offset(x, y - 1.6f * u), Offset(x + 3 * u, y + 1.6f * u), 1 * u); x += 6 * u }
                drawLine(cc(Color(0xFF7A5A30), oscuro).copy(alpha = 0.5f), Offset(cx - w, y + 2.4f * u), Offset(cx + w, y + 2.4f * u), 0.7f * u)
                y += 5 * u; fila++
            }
            drawRect(Color.Black.copy(alpha = 0.18f), Offset(cx + w * 0.18f, boca), Size(w, h))
        }
        drawRoundRect(cc(Color(0xFFA27A44), oscuro), Offset(cx - w / 2 - 2 * u, boca - 3 * u), Size(w + 4 * u, 5 * u), CornerRadius(2.5f * u))
        drawRoundRect(cc(Color(0xFF7A5A30), oscuro), Offset(cx - w * 0.36f, boca + h - 2 * u), Size(w * 0.72f, 4 * u), CornerRadius(2 * u))
    }

    /**
     * El buzón de Grecia: el banco de mármol del vestíbulo, contra el muro rojo con su friso de meandro y el caduceo de Hermes,
     * el de los mensajeros, pintado en medio. Sobre el banco, tres canastos de mimbre (kalathoi) con las fundas de cuero: a la
     * izquierda las que ya se pueden abrir, con el sello brillando; en medio las atadas; a la derecha las abiertas, con el
     * papiro afuera. En el frente del banco, una placa de mármol por canasto con el nombre y la cuenta incisos. En modo oscuro el
     * vestíbulo se apaga. Un degradado: el brillo de las listas.
     */
    override fun DrawScope.buzon(e: EstadoBuzon, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = cc(x, oscuro)
        // El muro con su friso y el caduceo.
        drawRect(c(MURO))
        drawRect(c(MURO_OSC), Offset(0f, 0f), Size(w, h * 0.14f))
        meandro(Offset(6 * u, h * 0.07f), w - 12 * u, h * 0.07f, c(MARMOL_OSC))
        drawLine(c(MARMOL_OSC), Offset(0f, h * 0.14f), Offset(w, h * 0.14f), 1.2f * u)
        val cd = Offset(w * 0.5f, h * 0.25f)
        val oro = c(Color(0xFFD9B25B))
        withTransform({ scale(0.72f, 0.72f, cd) }) {
            drawLine(oro, Offset(cd.x, cd.y - 22 * u), Offset(cd.x, cd.y + 26 * u), 2 * u)
            drawCircle(oro, 3 * u, Offset(cd.x, cd.y - 23 * u))
            listOf(-1f, 1f).forEach { lado ->
                drawPath(Path().apply {
                    moveTo(cd.x, cd.y + 22 * u)
                    for (j in 0..3) { val y0 = cd.y + 22 * u - j * 10 * u; quadraticTo(cd.x + lado * (if (j % 2 == 0) 9f else -9f) * u, y0 - 5 * u, cd.x, y0 - 10 * u) }
                }, oro, style = Stroke(1.5f * u))
                drawPath(Path().apply { moveTo(cd.x, cd.y - 16 * u); quadraticTo(cd.x + lado * 10 * u, cd.y - 24 * u, cd.x + lado * 16 * u, cd.y - 20 * u); quadraticTo(cd.x + lado * 10 * u, cd.y - 18 * u, cd.x, cd.y - 13 * u); close() }, oro)
            }
        }
        // Una franja azul del Egeo a media pared.
        drawLine(c(EGEO), Offset(0f, h * 0.43f), Offset(w, h * 0.43f), 2 * u)
        // El banco de mármol: la tabla de encima, el frente y el piso.
        val tabla = h * 0.7f
        drawRect(c(BALDOSA), Offset(0f, h * 0.94f), Size(w, h * 0.06f))
        drawRect(c(MARMOL), Offset(w * 0.02f, tabla), Size(w * 0.96f, h * 0.24f))
        drawRect(c(MARMOL_LUZ), Offset(0f, tabla - 4 * u), Size(w, 6 * u))
        drawRect(c(MARMOL_SOMBRA), Offset(0f, tabla + 2 * u), Size(w, 2 * u))
        // Los canastos y sus fundas.
        val zonas = listOf(w * 0.18f, w * 0.5f, w * 0.82f)
        val cuentas = listOf(e.listas, e.selladas, e.abiertas)
        val boca = h * 0.47f
        for (g in 0 until 3) {
            val x = zonas[g]
            val n = minOf(cuentas[g], 3)
            if (g == 0 && n > 0) mancha(Offset(x, boca - 6 * u), w * 0.17f, LLAMA.copy(alpha = if (oscuro) 0.6f else 0.5f))
            for (i in 0 until n) {
                val dx = (i - (n - 1) / 2f) * 18 * u
                fundaChica(Offset(x + dx, boca + 14 * u), 24 * u, 40 * u, dx / u * 0.8f, g, oscuro)
            }
            kalathos(x, boca, w * 0.2f, tabla - 4 * u - boca, oscuro)
            if (g == 0 && n > 0) for (i in 0 until n) drawPath(estrella(Offset(x + (i - (n - 1) / 2f) * 18 * u + 9 * u, boca - 28 * u), 3.4f * u, 4, 0.25f, 0f), LLAMA)
            // La placa con el nombre y la cuenta.
            val placa = Rect(x - w * 0.13f, tabla + 8 * u, x + w * 0.13f, h * 0.91f)
            drawRect(c(MARMOL_OSC), placa.topLeft, placa.size)
            bisel(placa, c(MARMOL_LUZ), c(MARMOL_SOMBRA), 1.4f * u)
            val tinta = legibleAnt(c(MARMOL_OSC), c(BARNIZ), MARMOL_LUZ)
            textoEn(medidor, listOf("para abrir", "atadas", "abiertas")[g], Rect(placa.left + 2 * u, placa.top + 1 * u, placa.right - 2 * u, placa.top + placa.height * 0.42f),
                Letra.garamond, tinta, 10f, minimo = 6f)
            textoEn(medidor, cuentas[g].toString(), Rect(placa.left + 2 * u, placa.top + placa.height * 0.36f, placa.right - 2 * u, placa.bottom - 1 * u),
                Letra.garamond, tinta, 18f, peso = FontWeight.Bold, minimo = 8f)
        }
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
 * estrellas amarillas. La tapa es ese papiro tumbado y grande, atado con lino y sellado con barro (de noche, la estrella de
 * Nut), con la viñeta de signos y la fecha en un cartucho, la paleta del escriba al pie y las palabras en una etiqueta de
 * lino; al abrirse, el atado se suelta y el rollo sube desenrollándose y deja ver el hierático con su rúbrica roja. Distinto
 * del Ideario, que es la Casa de la Vida con el friso jekeru, el disco alado y los cofres de cedro.
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

    // ---------------------------------------------------------------- la tapa

    override val altoTapa: Dp = 184.dp
    override val cajaTapa = Rect(0.12f, 0.35f, 0.84f, 0.68f)
    override val letraTapa: FontFamily = Letra.escriba
    override val tamTapa = 13f
    override val lineasTapa = 3
    override val centradoTapa = false

    /** El papiro de la vuelta de fuera, donde va el extracto: de noche se hunde menos, para que se siga leyendo. */
    private fun hojaTapa(oscuro: Boolean, noche: Boolean): Color {
        var r = PAPIROS[0]
        if (oscuro) r = mezcla(r, Color(0xFF110D08), 0.36f)
        if (noche) r = mezcla(r, NOCHE, if (oscuro) 0.42f else 0.3f)
        return r
    }

    override fun tintaTapa(oscuro: Boolean, noche: Boolean): Color = legibleAnt(hojaTapa(oscuro, noche), NEGRO, LINO)
    override fun palabrasTapa(n: Int): String = palabrasAnt(n)

    /** Un signo pintado de la viñeta, centrado en [o] y de alto [m]: el ankh (0), la pluma de Maat (1), el ojo (2) o el escarabajo (3). */
    private fun DrawScope.signo(k: Int, o: Offset, m: Float, color: Color) {
        val u = density
        val g = 1.3f * u
        when (k % 4) {
            0 -> {
                drawOval(color, Offset(o.x - m * 0.17f, o.y - m * 0.5f), Size(m * 0.34f, m * 0.4f), style = Stroke(g))
                drawLine(color, Offset(o.x - m * 0.3f, o.y - m * 0.08f), Offset(o.x + m * 0.3f, o.y - m * 0.08f), g * 1.3f)
                drawLine(color, Offset(o.x, o.y - m * 0.1f), Offset(o.x, o.y + m * 0.5f), g * 1.4f)
            }
            1 -> {
                val pluma = Path().apply {
                    moveTo(o.x, o.y + m * 0.5f)
                    cubicTo(o.x - m * 0.2f, o.y + m * 0.1f, o.x - m * 0.22f, o.y - m * 0.3f, o.x + m * 0.12f, o.y - m * 0.5f)
                    cubicTo(o.x + m * 0.16f, o.y - m * 0.2f, o.x + m * 0.1f, o.y + m * 0.15f, o.x, o.y + m * 0.5f)
                    close()
                }
                drawPath(pluma, color)
                drawLine(color, Offset(o.x, o.y + m * 0.5f), Offset(o.x + m * 0.06f, o.y + m * 0.62f), g)
            }
            2 -> {
                val ojo = Path().apply {
                    moveTo(o.x - m * 0.42f, o.y)
                    quadraticTo(o.x, o.y - m * 0.32f, o.x + m * 0.42f, o.y)
                    quadraticTo(o.x, o.y + m * 0.22f, o.x - m * 0.42f, o.y)
                    close()
                }
                drawPath(ojo, color, style = Stroke(g))
                drawCircle(color, m * 0.1f, o)
                drawLine(color, Offset(o.x - m * 0.38f, o.y - m * 0.2f), Offset(o.x + m * 0.4f, o.y - m * 0.26f), g)
                drawLine(color, Offset(o.x - m * 0.05f, o.y + m * 0.1f), Offset(o.x - m * 0.12f, o.y + m * 0.45f), g)
                drawPath(Path().apply { moveTo(o.x + m * 0.05f, o.y + m * 0.1f); quadraticTo(o.x + m * 0.35f, o.y + m * 0.3f, o.x + m * 0.2f, o.y + m * 0.45f) }, color, style = Stroke(g))
            }
            else -> {
                drawOval(color, Offset(o.x - m * 0.22f, o.y - m * 0.2f), Size(m * 0.44f, m * 0.6f))
                drawOval(color, Offset(o.x - m * 0.14f, o.y - m * 0.42f), Size(m * 0.28f, m * 0.2f))
                for (s in listOf(-1f, 1f)) for (j in 0 until 3) {
                    val y = o.y - m * 0.05f + j * m * 0.17f
                    drawLine(color, Offset(o.x + s * m * 0.2f, y), Offset(o.x + s * m * 0.38f, y + (j - 1) * m * 0.1f), g * 0.8f)
                }
                drawLine(color.copy(alpha = 0.5f), Offset(o.x, o.y - m * 0.18f), Offset(o.x, o.y + m * 0.38f), 0.6f * u)
            }
        }
    }

    /** La tapa, recortada a su lienzo: lo que se aparta al abrir y la luz de noche no se salen de su lugar. */
    override fun DrawScope.tapa(e: EstadoTapa, medidor: TextMeasurer, oscuro: Boolean) = clipRect { dibujarTapa(e, medidor, oscuro) }

    private fun DrawScope.dibujarTapa(e: EstadoTapa, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, e.noche)
        val a = e.abierto.coerceIn(0f, 1f)
        val sem = e.semilla and 0x7fffffff
        val papiro = c(PAPIROS[sem % PAPIROS.size])
        val hoja = hojaTapa(oscuro, e.noche)
        val caja = Rect(w * cajaTapa.left, h * cajaTapa.top, w * cajaTapa.right, h * cajaTapa.bottom)
        // El papiro tumbado: al abrirse, la hoja se queda pegada abajo y el rollo sube desenrollándose (y adelgaza).
        val cil = Rect(w * 0.05f, h * 0.06f, w * 0.95f, h * 0.74f)
        val r0 = cil.height / 2
        val cy = cil.center.y + (h * 0.15f - cil.center.y) * a
        val radio = r0 * (1f - 0.62f * a)
        val rollo = Rect(cil.left, cy - radio, cil.right, cy + radio)
        val extremo = rollo.height * 0.18f
        val k = rollo.height / cil.height

        // Abajo a la derecha, la paleta del escriba: la tablilla de madera con sus dos pocillos, negro y rojo, y los cálamos.
        val paleta = Rect(w * 0.73f, h * 0.81f, w * 0.95f, h * 0.91f)
        drawRect(Color.Black.copy(alpha = 0.2f), Offset(paleta.left + 2 * u, paleta.top + 2.5f * u), paleta.size)
        drawRect(c(ARENISCA_OSC), paleta.topLeft, paleta.size)
        drawRect(c(HONDO).copy(alpha = 0.5f), Offset(paleta.left, paleta.bottom - 1.6f * u), Size(paleta.width, 1.6f * u))
        listOf(c(NEGRO), c(ROJO)).forEachIndexed { i, tinta ->
            val p = Offset(paleta.left + paleta.width * (0.12f + 0.13f * i), paleta.center.y)
            drawCircle(c(HONDO), paleta.height * 0.3f, p)
            drawCircle(tinta, paleta.height * 0.22f, p)
        }
        val ranura = Rect(paleta.left + paleta.width * 0.36f, paleta.center.y - paleta.height * 0.14f, paleta.right - paleta.width * 0.06f, paleta.center.y + paleta.height * 0.14f)
        drawRect(c(HONDO), ranura.topLeft, ranura.size)
        listOf(-0.06f, 0.05f).forEach { f -> drawLine(c(LINO_OSC), Offset(ranura.left + 2 * u, ranura.center.y + paleta.height * f), Offset(ranura.right + w * 0.02f, ranura.center.y + paleta.height * f - 1 * u), 1.4f * u, StrokeCap.Round) }

        // La etiqueta de lino atada abajo, con las palabras escritas en negro.
        if (e.palabras > 0) {
            val tag = Rect(w * 0.36f, h * 0.8f, w * 0.68f, h * 0.93f)
            drawLine(c(LINO_OSC), Offset(tag.left + tag.width * 0.2f, tag.top), Offset(w * 0.3f, cil.bottom - 2 * u), 0.9f * u)
            drawRect(Color.Black.copy(alpha = 0.18f), Offset(tag.left + 1.5f * u, tag.top + 1.5f * u), tag.size)
            drawRect(c(LINO), tag.topLeft, tag.size)
            drawRect(c(LAPIS), tag.topLeft, tag.size, style = Stroke(1.2f * u))
            drawRect(c(LAPIS).copy(alpha = 0.5f), tag.deflate(2.4f * u).topLeft, tag.deflate(2.4f * u).size, style = Stroke(0.5f * u))
            textoEn(medidor, palabrasTapa(e.palabras), tag.deflate(3.4f * u), Letra.messiri, legibleAnt(c(LINO), c(NEGRO), LINO), 10.5f, minimo = 6.5f)
        }

        // La hoja ya desenrollada (sale por arriba del rollo, de abajo hacia arriba): las juntas, la rúbrica roja y el hierático.
        if (a > 0.01f) {
            val desen = Rect(rollo.left + extremo * 0.3f, cy, rollo.right - extremo * 0.5f, cil.bottom - r0 * 0.06f)
            drawRect(Color.Black.copy(alpha = 0.15f), Offset(desen.left + 2 * u, desen.top + 2 * u), desen.size)
            drawRect(papiro, desen.topLeft, desen.size)
            listOf(0.3f, 0.62f).forEach { f -> drawRect(c(PAPIRO_OSC).copy(alpha = 0.22f), Offset(desen.left + desen.width * f, desen.top), Size(w * 0.01f, desen.height)) }
            drawLine(c(ROJO), Offset(desen.left + 6 * u, desen.bottom - 5 * u), Offset(desen.right - 6 * u, desen.bottom - 5 * u), 1.3f * u)
            // El hierático corre de derecha a izquierda, en grupos de trazos; cada tercer renglón empieza con rúbrica roja.
            var y = desen.bottom - 12 * u
            var n = 0
            while (y > desen.top + 5 * u) {
                var x = desen.right - 10 * u
                var j = 0
                val inicio = x - desen.width * (0.12f + 0.08f * azar(e.semilla, 500 + n))
                val fin = desen.left + 10 * u + desen.width * 0.25f * azar(e.semilla, 600 + n)
                while (x > fin) {
                    val trazos = 2 + (azar(e.semilla + n, j) * 3).toInt()
                    val rubrica = x > inicio && n % 3 == 0
                    for (q in 0 until trazos) {
                        val xx = x - q * 2.4f * u
                        val alto = (1.4f + 2.4f * azar(e.semilla + q, j + n * 7)) * u
                        drawLine(c(if (rubrica) ROJO else NEGRO).copy(alpha = 0.62f), Offset(xx, y), Offset(xx - 1.2f * u, y - alto), 1f * u, StrokeCap.Round)
                    }
                    x -= trazos * 2.4f * u + 4.5f * u; j++
                }
                y -= 8.5f * u; n++
            }
        }

        // El rollo: el cuerpo con su luz y su sombra (el único degradado) y los dos cantos con la espiral de las vueltas.
        drawOval(c(PAPIRO_OSC), Offset(rollo.left - extremo / 2, rollo.top), Size(extremo, rollo.height))
        val cuerpo = Rect(rollo.left, rollo.top, rollo.right - extremo / 2, rollo.bottom)
        drawRect(Brush.verticalGradient(0f to c(PAPIRO_OSC), 0.14f to mezcla(hoja, Color.White, 0.22f), 0.28f to hoja, 0.85f to hoja, 1f to c(PAPIRO_OSC),
            startY = rollo.top, endY = rollo.bottom), cuerpo.topLeft, cuerpo.size)
        clipRect(cuerpo.left, cuerpo.top, cuerpo.right, cuerpo.bottom) {
            clipRect(caja.left - 4 * u, caja.top - 3 * u, caja.right + 4 * u, caja.bottom + 3 * u, clipOp = ClipOp.Difference) {
                var y = cuerpo.top + 2 * u
                while (y < cuerpo.bottom) { drawLine(c(PAPIRO_OSC).copy(alpha = 0.2f), Offset(cuerpo.left, y), Offset(cuerpo.right, y), 0.5f * u); y += 3 * u }
            }
            listOf(0.3f, 0.66f).forEach { f -> drawRect(c(PAPIRO_OSC).copy(alpha = 0.12f), Offset(cuerpo.left + cuerpo.width * f, cuerpo.top), Size(w * 0.01f, cuerpo.height)) }
        }
        val canto = Rect(rollo.right - extremo, rollo.top, rollo.right, rollo.bottom)
        drawOval(papiro, canto.topLeft, canto.size)
        val vueltas = 4 + ((e.palabras / 800f).coerceIn(0f, 1f) * 4).toInt()
        for (j in 1..vueltas) {
            val f = j / (vueltas + 1f)
            drawOval(c(PAPIRO_OSC).copy(alpha = 0.6f), Offset(canto.center.x - canto.width * f / 2, cy - canto.height * f / 2), Size(canto.width * f, canto.height * f), style = Stroke(0.7f * u))
        }
        drawOval(c(PAPIRO_OSC), canto.topLeft, canto.size, style = Stroke(1f * u))

        // La viñeta de arriba, entre filetes de rúbrica roja: dos signos a cada lado y, en medio, el cartucho con la fecha.
        val banda = Rect(cuerpo.left + extremo * 0.4f, rollo.top + rollo.height * 0.07f, cuerpo.right - extremo * 0.2f, rollo.top + rollo.height * 0.255f)
        drawLine(c(ROJO), Offset(banda.left, banda.top), Offset(banda.right, banda.top), 1.4f * u * k)
        drawLine(c(ROJO), Offset(banda.left, banda.bottom), Offset(banda.right, banda.bottom), 1.4f * u * k)
        drawLine(c(ROJO), Offset(banda.left, rollo.top + rollo.height * 0.94f), Offset(banda.right, rollo.top + rollo.height * 0.94f), 1.2f * u * k)
        val signoAlto = banda.height * 0.72f
        listOf(0.15f to 0, 0.21f to 1, 0.79f to 2, 0.85f to 3).forEach { (fx, s) ->
            signo(s, Offset(w * fx, banda.center.y), signoAlto, c(if (s % 2 == 0) LAPIS else VERDE))
        }
        val cartucho = Rect(w * 0.26f, banda.top + banda.height * 0.1f, w * 0.74f, banda.bottom - banda.height * 0.1f)
        drawRoundRect(c(LINO), cartucho.topLeft, cartucho.size, CornerRadius(cartucho.height / 2))
        drawRoundRect(c(ROJO), cartucho.topLeft, cartucho.size, CornerRadius(cartucho.height / 2), style = Stroke(1.3f * u * k))
        drawLine(c(ROJO), Offset(cartucho.right + 1.6f * u * k, cartucho.top + 1 * u), Offset(cartucho.right + 1.6f * u * k, cartucho.bottom - 1 * u), 1.8f * u * k)
        textoEn(medidor, e.fecha, Rect(cartucho.left + cartucho.height * 0.4f, cartucho.top + 1 * u, cartucho.right - cartucho.height * 0.4f, cartucho.bottom - 1 * u),
            letra, legibleAnt(c(LINO), c(NEGRO), LINO), 12.5f * k.coerceAtLeast(0.6f), peso = FontWeight.SemiBold, minimo = 5f)

        // El atado de lino a la izquierda, con su sello de barro colgando (de noche, una estrella de Nut): se suelta al abrir.
        val suelta = (1f - a * 3f).coerceIn(0f, 1f)
        if (suelta > 0f) {
            val xa = w * 0.085f
            drawRect(c(LINO).copy(alpha = suelta), Offset(xa, rollo.top - 1 * u), Size(w * 0.022f, rollo.height + 2 * u))
            drawLine(c(LINO_OSC).copy(alpha = suelta), Offset(xa + w * 0.011f, rollo.top), Offset(xa + w * 0.011f, rollo.bottom), 0.6f * u)
            val sc = Offset(xa + w * 0.011f, rollo.bottom + h * 0.1f + a * h * 0.1f)
            drawLine(c(LINO).copy(alpha = suelta), Offset(xa + w * 0.006f, rollo.bottom), Offset(sc.x - 2 * u, sc.y), 1.4f * u)
            drawLine(c(LINO).copy(alpha = suelta), Offset(xa + w * 0.016f, rollo.bottom), Offset(sc.x + 2 * u, sc.y), 1.4f * u)
            val rs = h * 0.04f
            if (e.noche) {
                drawCircle(c(NUT).copy(alpha = suelta), rs, sc)
                drawPath(estrella(sc, rs * 0.82f, 5, 0.42f), AMARILLO.copy(alpha = suelta))
            } else {
                drawOval(c(BARRO).copy(alpha = suelta), Offset(sc.x - rs, sc.y - rs * 0.8f), Size(rs * 2, rs * 1.6f))
                drawCircle(Color.Black.copy(alpha = 0.3f * suelta), rs * 0.45f, sc, style = Stroke(0.7f * u))
                drawLine(Color.Black.copy(alpha = 0.25f * suelta), Offset(sc.x - rs * 0.2f, sc.y - rs * 0.2f), Offset(sc.x + rs * 0.2f, sc.y + rs * 0.2f), 0.7f * u)
            }
        }
        // De noche, unas estrellas de Nut pintadas en la franja de abajo del rollo.
        if (e.noche) {
            for (j in 0 until 9) {
                val p = Offset(cuerpo.left + cuerpo.width * (0.14f + 0.09f * j), rollo.top + rollo.height * 0.97f - 2.6f * u)
                if (rollo.height > 20 * u) drawPath(estrella(p, 2.2f * u * k, 5, 0.42f), AMARILLO.copy(alpha = 0.9f))
            }
        }
    }

    // ---------------------------------------------------------------- la página y el escritor

    override val altoEncabezado: Dp = 122.dp

    /** El friso jeker: los manojos de juncos atados que coronan las paredes pintadas, alternando [a] y [b], a lo largo de [r]. */
    private fun DrawScope.jeker(r: Rect, a: Color, b: Color) {
        val u = density
        val paso = 8 * u
        var x = r.left + paso / 2
        var i = 0
        while (x < r.right - paso / 2) {
            val c = if (i % 2 == 0) a else b
            drawRect(c, Offset(x - 1.3f * u, r.top + r.height * 0.3f), Size(2.6f * u, r.height * 0.7f))
            drawCircle(c, 2.1f * u, Offset(x, r.top + r.height * 0.2f))
            drawLine(c, Offset(x - 2.2f * u, r.top + r.height * 0.55f), Offset(x + 2.2f * u, r.top + r.height * 0.55f), 0.8f * u)
            x += paso; i++
        }
    }

    /** La cenefa de rectángulos de colores separados por una línea clara, en [r], con transparencia [alfa]. */
    private fun DrawScope.cenefa(r: Rect, alfa: Float) {
        val u = density
        val colores = listOf(LAPIS, ROJO, VERDE, ROJO)
        val paso = 7 * u
        var x = r.left
        var k = 0
        while (x < r.right) {
            drawRect(colores[k % colores.size].copy(alpha = alfa), Offset(x, r.top), Size(minOf(paso - 1.4f * u, r.right - x), r.height))
            x += paso; k++
        }
        drawLine(NEGRO.copy(alpha = alfa), Offset(r.left, r.top), Offset(r.right, r.top), 0.6f * u)
        drawLine(NEGRO.copy(alpha = alfa), Offset(r.left, r.bottom), Offset(r.right, r.bottom), 0.6f * u)
    }

    /**
     * El encabezado: arriba, el friso jeker de juncos en lapislázuli y cornalina; debajo, el rótulo; en medio, el cartucho (el
     * anillo de cuerda alargado que encierra los nombres de los reyes, con su nudo a la derecha) que aquí encierra la fecha;
     * debajo, el detalle y la cenefa de colores. A la izquierda, el ojo udjat; a la derecha, el ankh; de noche, la luna
     * creciente y la estrella del cielo de Nut.
     */
    override fun DrawScope.encabezado(e: EstadoEncabezado, p: Pagina, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        jeker(Rect(4 * u, 2 * u, w - 4 * u, 13 * u), (if (p.oscura) p.detalle else tonoAnt(LAPIS, p, oscuro)).copy(alpha = 0.8f),
            (if (p.oscura) p.acento else tonoAnt(ROJO, p, oscuro)).copy(alpha = 0.8f))
        drawLine(p.tinta.copy(alpha = 0.5f), Offset(4 * u, 14.5f * u), Offset(w - 4 * u, 14.5f * u), 0.8f * u)
        textoEn(medidor, e.rotulo, Rect(48 * u, 17 * u, w - 48 * u, 34 * u), Letra.messiri, p.acento, 14f, espaciado = 0.6f, minimo = 8f)
        // El cartucho: el óvalo de cuerda, doble, con el nudo y la barra a la derecha.
        val cart = Rect(46 * u, 36 * u, w - 52 * u, 82 * u)
        val fondo = if (p.oscura) mezcla(p.color, p.tinta, 0.08f) else tonoAnt(PAPIROS[0], p, oscuro)
        drawRoundRect(Color.Black.copy(alpha = if (p.oscura) 0.3f else 0.12f), Offset(cart.left + 1.5f * u, cart.top + 2 * u), cart.size, CornerRadius(cart.height / 2))
        drawRoundRect(fondo, cart.topLeft, cart.size, CornerRadius(cart.height / 2))
        if (e.noche) clipRect(cart.left, cart.top, cart.right, cart.bottom) { mancha(cart.center, cart.width * 0.5f, AMARILLO.copy(alpha = 0.1f)) }
        drawRoundRect(p.tinta.copy(alpha = 0.8f), cart.topLeft, cart.size, CornerRadius(cart.height / 2), style = Stroke(1.6f * u))
        drawRoundRect(p.tinta.copy(alpha = 0.35f), Offset(cart.left + 3.5f * u, cart.top + 3.5f * u), Size(cart.width - 7 * u, cart.height - 7 * u),
            CornerRadius(cart.height / 2 - 3.5f * u), style = Stroke(0.8f * u))
        drawLine(p.tinta.copy(alpha = 0.8f), Offset(cart.right + 4 * u, cart.top + 3 * u), Offset(cart.right + 4 * u, cart.bottom - 3 * u), 2.2f * u)
        drawLine(p.tinta.copy(alpha = 0.8f), Offset(cart.right - 1 * u, cart.center.y), Offset(cart.right + 4 * u, cart.center.y), 1.4f * u)
        fechaEncAnt(medidor, e.fecha, Rect(cart.left + 14 * u, cart.top + 4 * u, cart.right - 12 * u, cart.bottom - 4 * u), Letra.escriba, p.tinta, 21f, 22, FontWeight.Bold)
        textoEn(medidor, e.detalle, Rect(40 * u, 85 * u, w - 40 * u, 100 * u), Letra.escriba, p.tenue, 11f, minimo = 7f)
        cenefa(Rect(14 * u, h - 14 * u, w - 14 * u, h - 9 * u), if (p.oscura) 0.55f else 0.75f)
        // Los signos a los lados.
        val lapis = if (p.oscura) p.detalle else tonoAnt(LAPIS, p, oscuro)
        val roja = if (p.oscura) p.acento else tonoAnt(ROJO, p, oscuro)
        if (e.noche) {
            val luna = Offset(22 * u, 58 * u)
            drawCircle(ORO, 12 * u, luna)
            drawCircle(p.color, 10 * u, Offset(luna.x + 5 * u, luna.y - 4 * u))
            drawPath(estrella(Offset(w - 20 * u, 56 * u), 11 * u, 5, 0.42f), ORO)
            drawCircle(ORO, 1.3f * u, Offset(w - 34 * u, 40 * u)); drawCircle(ORO, 1.1f * u, Offset(34 * u, 82 * u))
        } else {
            signo(2, Offset(22 * u, 58 * u), 32 * u, lapis)
            signo(0, Offset(w - 20 * u, 58 * u), 38 * u, roja)
        }
    }

    /**
     * El papiro de la página: las guías de las columnas en los márgenes, el punto rojo de verso que marca cada estrofa, al pie
     * la cenefa de colores y, sobre ella, tres signos tenues (udjat, pluma de Maat, escarabajo); de noche, una estrella de
     * cinco puntas abajo a la derecha. En modo oscuro, el papiro en la penumbra y la luz de una lámpara de aceite desde
     * arriba a la derecha.
     */
    override fun DrawScope.pagina(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        listOf(13 * u, 17 * u, w - 17 * u, w - 13 * u).forEach { x -> drawLine(p.detalle.copy(alpha = 0.18f), Offset(x, 0f), Offset(x, h - 34 * u), 0.8f * u) }
        var y = 108 * u
        while (y < h - 80 * u) { drawCircle(p.acento.copy(alpha = 0.45f), 2.2f * u, Offset(24 * u, y)); y += 132 * u }
        cenefa(Rect(14 * u, h - 22 * u, w - 14 * u, h - 17 * u), 0.4f)
        listOf(0 to -26f, 1 to 0f, 3 to 26f).forEach { (k, dx) -> signo(k, Offset(w / 2 + dx * u, h - 40 * u), 16 * u, p.detalle.copy(alpha = 0.25f)) }
        if (noche) drawPath(estrella(Offset(w - 46 * u, h - 96 * u), 10 * u, 5, 0.42f), p.acento.copy(alpha = 0.35f))
        if (oscuro && !p.oscura) penumbraAnt(Offset(w * 1.03f, h * 0.05f), AMARILLO, 0.2f)
    }

    /** La barra: una franja de papiro (un degradado, más claro arriba) con el friso jeker a lo largo del borde de abajo. */
    override fun DrawScope.barra(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val papiro = if (p.oscura) mezcla(p.color, p.tinta, 0.07f) else tonoAnt(PAPIROS[1], p, oscuro)
        drawRect(Brush.verticalGradient(listOf(mezcla(papiro, Color.White, 0.25f), papiro), 0f, h))
        jeker(Rect(0f, h - 11 * u, w, h - 2 * u), (if (p.oscura) p.detalle else tonoAnt(LAPIS, p, oscuro)).copy(alpha = 0.55f),
            (if (p.oscura) p.acento else tonoAnt(ROJO, p, oscuro)).copy(alpha = 0.55f))
        drawLine(p.tinta.copy(alpha = 0.45f), Offset(0f, h - 1 * u), Offset(w, h - 1 * u), 1f * u)
    }

    /**
     * El botón de guardar: una placa de fayenza de lapislázuli (un degradado, con su brillo arriba), con el borde de oro, una
     * línea de oro adentro y un punto de oro en cada punta; sombra plana.
     */
    override fun DrawScope.botonGuardar(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val r = Rect(2 * u, 2 * u, w - 3 * u, h - 4 * u)
        val lapis = if (oscuro) mezcla(LAPIS, Color.Black, 0.12f) else LAPIS
        drawRoundRect(Color.Black.copy(alpha = 0.3f), Offset(r.left + 1.5f * u, r.top + 2.5f * u), r.size, CornerRadius(5 * u))
        drawRoundRect(Brush.verticalGradient(listOf(mezcla(lapis, Color(0xFF6A8AD8), 0.35f), lapis, NUT), r.top, r.bottom), r.topLeft, r.size, CornerRadius(5 * u))
        drawRoundRect(ORO, r.topLeft, r.size, CornerRadius(5 * u), style = Stroke(1.6f * u))
        drawRoundRect(ORO.copy(alpha = 0.5f), Offset(r.left + 3.5f * u, r.top + 3.5f * u), Size(r.width - 7 * u, r.height - 7 * u), CornerRadius(3 * u), style = Stroke(0.7f * u))
        listOf(r.left + 8 * u, r.right - 8 * u).forEach { x -> drawCircle(ORO, 2 * u, Offset(x, r.center.y)) }
    }

    override fun tintaBoton(p: Pagina, oscuro: Boolean, noche: Boolean): Color = Color(0xFFF4E3A6)

    // ---------------------------------------------------------------- la libreta de momentos

    override val altoLibreta: Dp = 250.dp

    /**
     * El color de la libreta con su luz: de día, tal cual; en modo oscuro, hundido en la penumbra del cuarto salvo donde llega
     * la lámpara ([luz], de 0 lejos a 1 junto a la mecha, que además lo entibia).
     */
    private fun lc(x: Color, oscuro: Boolean, luz: Float = 0f): Color =
        if (!oscuro) x else mezcla(mezcla(x, Color(0xFF0B0805), 0.7f - 0.42f * luz), AMARILLO, 0.1f * luz)

    /** Una banda de rectángulos de colores ([lc]) entre dos filetes negros, como la de las pinturas. */
    private fun DrawScope.bandaLib(b: Rect, oscuro: Boolean, luz: Float) {
        val u = density
        val colores = listOf(LAPIS, ROJO, VERDE, ROJO)
        var x = b.left; var k = 0
        while (x < b.right) { drawRect(lc(colores[k % 4], oscuro, luz), Offset(x, b.top), Size(minOf(5.6f * u, b.right - x), b.height)); x += 7 * u; k++ }
        drawLine(lc(NEGRO, oscuro, luz), Offset(b.left, b.top), Offset(b.right, b.top), 0.8f * u)
        drawLine(lc(NEGRO, oscuro, luz), Offset(b.left, b.bottom), Offset(b.right, b.bottom), 0.8f * u)
    }

    /**
     * La libreta de momentos: el papiro del escriba, tendido sobre la estera de junco. El rollo, atado con lino y sellado con
     * barro, engorda con cada columna; a su lado, la etiqueta de madera atada al cordón, con el nombre dentro de un cartucho de
     * lapislázuli y la cuenta a tinta. En la estera, la paleta del escriba con sus dos pocillos (negro y rojo) y sus cálamos,
     * el jarrito del agua y, en su pie, la lámpara de barro con la mecha flotando en aceite; en la pared de arenisca, la
     * banda de colores y una columna de signos. Al abrirse, el lino se suelta y el rollo rueda hacia delante: queda tendida la
     * hoja con sus columnas de signos, la primera con su rúbrica roja. En modo oscuro el cuarto se apaga y alumbra la lámpara.
     * Un degradado: el resplandor de la lámpara.
     */
    override fun DrawScope.libreta(e: EstadoLibreta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val a = e.abierto.coerceIn(0f, 1f)
        val gordo = (kotlin.math.sqrt(e.hojas.coerceAtLeast(1).toFloat()) / 14f).coerceIn(0.07f, 1f)
        val s = e.semilla
        fun c(x: Color, luz: Float = 0f) = lc(x, oscuro, luz)
        // La pared de arenisca, con sus hiladas, la banda de colores y una columna de signos pintados.
        val estera0 = h * 0.5f
        drawRect(c(ARENISCA, 0.15f), Offset.Zero, Size(w, estera0 + 4 * u))
        var hy = h * 0.2f
        while (hy < estera0) { drawLine(c(ARENISCA_OSC, 0.15f).copy(alpha = 0.5f), Offset(0f, hy), Offset(w, hy), 0.8f * u); hy += h * 0.1f }
        for (k in 0 until 8) { val x = w * (0.05f + 0.125f * k) + (if ((k / 1) % 2 == 0) 0f else 10 * u); drawLine(c(ARENISCA_OSC, 0.15f).copy(alpha = 0.4f), Offset(x, h * 0.2f + h * 0.1f * (k % 3)), Offset(x, h * 0.3f + h * 0.1f * (k % 3)), 0.8f * u) }
        drawRect(c(ARENISCA_LUZ, 0.15f), Offset.Zero, Size(w, h * 0.12f))
        bandaLib(Rect(0f, h * 0.12f, w, h * 0.155f), oscuro, 0.15f)
        for (k in 0 until 12) {
            val x = w * (0.04f + 0.083f * k)
            drawPath(Path().apply { moveTo(x, h * 0.11f); lineTo(x + w * 0.03f, h * 0.11f); lineTo(x + w * 0.015f, h * 0.03f); close() }, c(if (k % 2 == 0) VERDE else LAPIS, 0.15f).copy(alpha = 0.8f))
        }
        val col = Rect(w * 0.8f, h * 0.18f, w * 0.88f, estera0 - 4 * u)
        drawRect(c(LAPIS, 0.15f), col.topLeft, col.size, style = Stroke(1 * u))
        for (k in 0 until 4) signo(k + s, Offset(col.center.x, col.top + col.height * (0.14f + 0.24f * k)), col.width * 0.75f, c(if (k % 2 == 0) LAPIS else ROJO, 0.15f))
        // La lámpara en su pie, a la izquierda, y su resplandor (el único degradado).
        val mecha = Offset(w * 0.075f, h * 0.27f)
        mancha(mecha, if (oscuro) w * 0.9f else w * 0.3f, AMARILLO.copy(alpha = if (oscuro) 0.42f else 0.26f))
        val barro = c(BARRO, 0.9f)
        drawRect(barro, Offset(mecha.x - 3 * u, h * 0.32f), Size(6 * u, h * 0.3f))
        drawPath(Path().apply { moveTo(mecha.x - 12 * u, h * 0.62f); lineTo(mecha.x + 12 * u, h * 0.62f); lineTo(mecha.x + 5 * u, h * 0.56f); lineTo(mecha.x - 5 * u, h * 0.56f); close() }, barro)
        drawPath(Path().apply { moveTo(mecha.x - 14 * u, h * 0.3f); lineTo(mecha.x + 14 * u, h * 0.3f); quadraticTo(mecha.x + 10 * u, h * 0.335f, mecha.x, h * 0.335f); quadraticTo(mecha.x - 10 * u, h * 0.335f, mecha.x - 14 * u, h * 0.3f); close() }, barro)
        drawRect(c(ROJO, 0.9f), Offset(mecha.x - 13 * u, h * 0.305f), Size(26 * u, 1.6f * u))
        drawPath(Path().apply { moveTo(mecha.x - 4 * u, h * 0.3f); quadraticTo(mecha.x - 5 * u, mecha.y, mecha.x, mecha.y - 12 * u); quadraticTo(mecha.x + 5 * u, mecha.y, mecha.x + 4 * u, h * 0.3f); close() }, AMARILLO)
        drawCircle(Color.White, 1.6f * u, Offset(mecha.x, h * 0.29f))
        // La estera de junco en perspectiva: el tejido en espiga y las orillas atadas.
        val estera = Path().apply { moveTo(w * 0.1f, estera0); lineTo(w * 0.9f, estera0); lineTo(w * 1.06f, h * 0.98f); lineTo(-w * 0.06f, h * 0.98f); close() }
        drawRect(c(HONDO, 0.3f), Offset(0f, estera0), Size(w, h - estera0))
        drawPath(estera, c(Color(0xFFC9AE6E), 0.5f))
        val junco = c(Color(0xFF8E7440), 0.5f).copy(alpha = 0.55f)
        clipPath(estera) {
            for (k in 0..24) {
                val f = k / 24f
                val xa = w * 0.1f + w * 0.8f * f; val xb = -w * 0.06f + w * 1.12f * f
                drawLine(junco, Offset(xa, estera0), Offset(xb, h * 0.98f), 0.8f * u)
            }
            var yy = estera0; var fila = 0
            while (yy < h) {
                val paso = (5 + 9 * (yy - estera0) / (h - estera0)) * u
                for (k in 0..24) {
                    val f = (k + 0.5f) / 24f
                    val t0 = (yy - estera0) / (h * 0.98f - estera0)
                    val x = (w * 0.1f + w * 0.8f * f) * (1 - t0) + (-w * 0.06f + w * 1.12f * f) * t0
                    val d = (if ((k + fila) % 2 == 0) 1 else -1) * paso * 0.4f
                    drawLine(junco, Offset(x - d, yy), Offset(x + d, yy + paso), 0.7f * u)
                }
                yy += paso; fila++
            }
            drawRect(Color.Black.copy(alpha = 0.12f), Offset(0f, estera0), Size(w, h * 0.04f))
        }
        drawPath(estera, c(Color(0xFF6E5528), 0.5f), style = Stroke(3 * u))
        // La paleta del escriba, el jarrito del agua y una bola de pigmento, al fondo a la derecha.
        val pal = Rect(w * 0.64f, h * 0.56f, w * 0.95f, h * 0.6f)
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(pal.left + 2 * u, pal.top + 3 * u), pal.size)
        drawRect(c(Color(0xFFD8C090), 0.4f), pal.topLeft, pal.size)
        drawRect(c(Color(0xFFF0E0B4), 0.4f), pal.topLeft, Size(pal.width, 1.6f * u))
        drawOval(c(NEGRO, 0.4f), Offset(pal.left + 5 * u, pal.top + 2.5f * u), Size(9 * u, pal.height - 5 * u))
        drawOval(c(ROJO, 0.4f), Offset(pal.left + 16 * u, pal.top + 2.5f * u), Size(9 * u, pal.height - 5 * u))
        drawRect(c(HONDO, 0.4f), Offset(pal.left + 30 * u, pal.center.y - 1.6f * u), Size(pal.width - 36 * u, 3.2f * u))
        drawLine(c(Color(0xFFB59A5A), 0.4f), Offset(pal.left + 32 * u, pal.center.y - 0.6f * u), Offset(pal.right + 10 * u, pal.center.y - 3 * u), 1.6f * u, StrokeCap.Round)
        drawLine(c(Color(0xFF9A7E44), 0.4f), Offset(pal.left + 34 * u, pal.center.y + 0.8f * u), Offset(pal.right + 6 * u, pal.center.y + 2 * u), 1.4f * u, StrokeCap.Round)
        val jarro = Offset(w * 0.93f, h * 0.53f)
        drawPath(Path().apply { moveTo(jarro.x - 5 * u, jarro.y - 18 * u); lineTo(jarro.x + 5 * u, jarro.y - 18 * u); lineTo(jarro.x + 4 * u, jarro.y - 14 * u)
            quadraticTo(jarro.x + 13 * u, jarro.y - 8 * u, jarro.x + 6 * u, jarro.y); lineTo(jarro.x - 6 * u, jarro.y); quadraticTo(jarro.x - 13 * u, jarro.y - 8 * u, jarro.x - 4 * u, jarro.y - 14 * u); close() }, c(BARRO, 0.3f))
        drawLine(c(LAPIS, 0.3f), Offset(jarro.x - 9 * u, jarro.y - 9 * u), Offset(jarro.x + 9 * u, jarro.y - 9 * u), 1.6f * u)
        drawCircle(c(LAPIS, 0.3f), 4 * u, Offset(w * 0.6f, h * 0.575f))
        // El rollo: rueda hacia delante al abrirse y deja tendida la hoja escrita.
        val diam = h * (0.12f + 0.13f * gordo) * (1f - 0.18f * a)
        val x0 = w * 0.13f; val x1 = w * 0.6f
        val atras = h * 0.55f
        val yRollo = atras + diam / 2 + (h * 0.84f - diam / 2 - atras) * a
        val papiro = c(PAPIROS[(s and 0x7fffffff) % PAPIROS.size], 0.55f)
        if (a > 0.01f) {
            val hoja = Rect(x0, atras, x1, yRollo)
            drawRect(Color.Black.copy(alpha = 0.2f), Offset(hoja.left + 3 * u, hoja.top + 3 * u), hoja.size)
            drawRect(papiro, hoja.topLeft, hoja.size)
            clipRect(hoja.left, hoja.top, hoja.right, hoja.bottom) {
                var fx = hoja.left + 3 * u
                while (fx < hoja.right) { drawLine(Color(0x185A4020), Offset(fx, hoja.top), Offset(fx, hoja.bottom), 0.6f * u); fx += 4.4f * u }
                val tinta = c(NEGRO, 0.55f).copy(alpha = 0.8f)
                val anchoCol = 16 * u
                var cx = hoja.right - 12 * u; var k = 0
                while (cx > hoja.left + 6 * u) {
                    drawLine(tinta.copy(alpha = 0.4f), Offset(cx - anchoCol / 2 - 2 * u, hoja.top + 4 * u), Offset(cx - anchoCol / 2 - 2 * u, hoja.bottom), 0.6f * u)
                    var cy = hoja.top + 10 * u; var j = 0
                    while (cy < hoja.bottom + 10 * u) {
                        signo(s + k * 5 + j, Offset(cx, cy), anchoCol * 0.85f, if (k == 0 && j == 0) c(ROJO, 0.55f) else tinta)
                        cy += anchoCol * 1.05f; j++
                    }
                    cx -= anchoCol + 5 * u; k++
                }
            }
        }
        val elipse = diam * 0.34f
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(x0, yRollo + diam * 0.32f), Size(x1 - x0 + 6 * u, diam * 0.3f))
        drawRect(papiro, Offset(x0, yRollo - diam / 2), Size(x1 - x0, diam))
        drawRect(Color.White.copy(alpha = 0.22f), Offset(x0, yRollo - diam * 0.32f), Size(x1 - x0, diam * 0.14f))
        drawRect(Color.Black.copy(alpha = 0.14f), Offset(x0, yRollo + diam * 0.12f), Size(x1 - x0, diam * 0.18f))
        drawRect(Color.Black.copy(alpha = 0.24f), Offset(x0, yRollo + diam * 0.3f), Size(x1 - x0, diam * 0.2f))
        var fx = x0 + 4 * u
        while (fx < x1) { drawLine(Color(0x1A5A4020), Offset(fx, yRollo - diam / 2), Offset(fx, yRollo + diam / 2), 0.6f * u); fx += 4.4f * u }
        drawOval(mezcla(papiro, Color.Black, 0.25f), Offset(x0 - elipse / 2, yRollo - diam / 2), Size(elipse, diam))
        drawOval(mezcla(papiro, Color.White, 0.08f), Offset(x1 - elipse / 2, yRollo - diam / 2), Size(elipse, diam))
        val vueltas = (3 + e.hojas / 20).coerceAtMost(10)
        for (k in 1..vueltas) {
            val f = k / (vueltas + 1f)
            drawOval(mezcla(papiro, Color.Black, 0.3f), Offset(x1 - elipse / 2 * (1 - f), yRollo - diam / 2 * (1 - f)), Size(elipse * (1 - f), diam * (1 - f)), style = Stroke(0.7f * u))
        }
        // El cordón de lino, con su terrón de barro sellado; al abrir, suelto sobre la estera.
        val lino = c(LINO, 0.55f)
        val xh = x0 + (x1 - x0) * 0.7f
        if (a < 0.15f) {
            listOf(-3f, 3f).forEach { o -> drawLine(lino, Offset(xh + o * u, yRollo - diam / 2 - 1 * u), Offset(xh + o * u, yRollo + diam / 2 + 1 * u), 1.8f * u) }
            drawOval(c(Color(0xFF7A5232), 0.55f), Offset(xh - 9 * u, yRollo + diam / 2 - 7 * u), Size(18 * u, 13 * u))
            drawOval(Color.Black.copy(alpha = 0.3f), Offset(xh - 5 * u, yRollo + diam / 2 - 4 * u), Size(10 * u, 7 * u), style = Stroke(1 * u))
        } else {
            drawPath(Path().apply { moveTo(xh, yRollo + diam / 2); cubicTo(xh + 20 * u, yRollo + diam / 2 + 6 * u, xh + 10 * u, h * 0.95f, w * 0.66f, h * 0.9f) }, lino, style = Stroke(1.6f * u))
        }
        // La etiqueta de madera atada al cordón: el nombre en un cartucho de lapislázuli y la cuenta a tinta.
        val et = Rect(w * 0.6f, h * 0.68f, w * 0.97f, h * 0.95f)
        if (a < 0.15f) drawPath(Path().apply { moveTo(xh + 3 * u, yRollo + diam / 2); quadraticTo(et.left - 4 * u, et.top - 8 * u, et.left + 8 * u, et.top + 8 * u) }, lino, style = Stroke(1.4f * u))
        rotate(-3f + 2f * a, et.center) {
            val madera = c(Color(0xFFD9B98A), 0.7f)
            val tabla = Path().apply { moveTo(et.left, et.top + 8 * u); quadraticTo(et.left, et.top, et.left + 8 * u, et.top); lineTo(et.right, et.top); lineTo(et.right, et.bottom); lineTo(et.left, et.bottom); close() }
            translate(2 * u, 3 * u) { drawPath(tabla, Color.Black.copy(alpha = 0.3f)) }
            drawPath(tabla, madera)
            vetas(et, Color.Black.copy(alpha = 0.12f), s + 2, densidad = 0.5f)
            drawPath(tabla, c(HONDO, 0.7f), style = Stroke(1 * u))
            drawCircle(c(HONDO, 0.7f), 2.4f * u, Offset(et.left + 8 * u, et.top + 8 * u))
            val car = Rect(et.left + 15 * u, et.top + 5 * u, et.right - 6 * u, et.top + et.height * 0.6f)
            drawRoundRect(c(LAPIS, 0.6f), car.topLeft, car.size, CornerRadius(car.height / 2), style = Stroke(1.8f * u))
            drawLine(c(LAPIS, 0.6f), Offset(car.right + 2.5f * u, car.top + 2 * u), Offset(car.right + 2.5f * u, car.bottom - 2 * u), 2 * u)
            textoEn(medidor, e.nombre, car.deflate(5 * u), letra, legibleAnt(madera, c(LAPIS, 0.3f), ARENISCA_LUZ), 14f, peso = FontWeight.Bold, minimo = 7f)
            textoEn(medidor, e.cuenta, Rect(et.left + 8 * u, car.bottom + 1 * u, et.right - 6 * u, et.bottom - 4 * u), letra,
                legibleAnt(madera, c(NEGRO, 0.3f), ARENISCA_LUZ), 10.5f, minimo = 6.5f)
        }
    }

    override fun DrawScope.pestana(e: EstadoPestana, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val s = e.semilla
        // Una plaquita de lino con el día dentro de un cartucho; arriba, la franja pintada del color del ánimo.
        val lino = lc(if ((s and 1) == 0) LINO else PAPIROS[(s and 0x7fffffff) % PAPIROS.size], oscuro, 0.35f)
        val r = Rect(w * 0.05f, h * 0.04f, w * 0.95f, h * 0.97f)
        drawRect(Color.Black.copy(alpha = 0.28f), Offset(r.left + 1.5f * u, r.top + 2 * u), r.size)
        drawRect(lino, r.topLeft, r.size)
        var y = r.top + 3 * u
        while (y < r.bottom) { drawLine(Color(0x145A4020), Offset(r.left, y), Offset(r.right, y), 0.5f * u); y += 3 * u }
        val franja = Rect(r.left, r.top, r.right, r.top + 8 * u)
        if (e.tono != null) drawRect(e.tono, franja.topLeft, franja.size)
        else { var x = r.left; var k = 0; while (x < r.right) { drawRect(lc(listOf(LAPIS, ROJO, VERDE)[k % 3], oscuro, 0.35f).copy(alpha = 0.35f), Offset(x, franja.top), Size(minOf(4 * u, r.right - x), franja.height)); x += 5 * u; k++ } }
        drawLine(lc(NEGRO, oscuro, 0.35f), Offset(r.left, franja.bottom), Offset(r.right, franja.bottom), 0.8f * u)
        val lapis = if (oscuro) Color(0xFF8FA8E8) else LAPIS
        val car = Rect(r.left + 4 * u, franja.bottom + 3 * u, r.right - 4 * u, r.bottom - 12 * u)
        drawRoundRect(lapis, car.topLeft, car.size, CornerRadius(car.width / 2), style = Stroke(1.5f * u))
        drawLine(lapis, Offset(car.left + 3 * u, car.bottom + 2.5f * u), Offset(car.right - 3 * u, car.bottom + 2.5f * u), 1.8f * u)
        textoEn(medidor, e.dia, car.deflate(2 * u), letra, if (oscuro) ARENISCA_LUZ else NEGRO, 19f, peso = FontWeight.Bold, minimo = 9f)
        textoEn(medidor, e.semana, Rect(r.left, car.bottom + 3 * u, r.right, r.bottom), letra, if (oscuro) mezcla(ROJO, Color.White, 0.45f) else ROJO, 8.5f, minimo = 6f)
        drawRect(lc(LINO_OSC, oscuro, 0.35f), r.topLeft, r.size, style = Stroke(0.8f * u))
    }

    override fun DrawScope.rotuloMes(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // Una banda de lino entre dos bandas de colores, con un ankh a cada lado.
        val lino = lc(LINO, oscuro, 0.35f)
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(3 * u, 4 * u), Size(w - 4 * u, h - 5 * u))
        drawRect(lino, Offset(1 * u, 2 * u), Size(w - 4 * u, h - 5 * u))
        bandaLib(Rect(1 * u, 2 * u, w - 3 * u, 6.5f * u), oscuro, 0.35f)
        bandaLib(Rect(1 * u, h - 7.5f * u, w - 3 * u, h - 3 * u), oscuro, 0.35f)
        val tinta = legibleAnt(lino, lc(NEGRO, oscuro, 0.2f), ARENISCA_LUZ)
        val lapis = legibleAnt(lino, lc(LAPIS, oscuro, 0.2f), Color(0xFF8FA8E8))
        signo(0, Offset(w * 0.06f, h * 0.52f), h * 0.42f, lapis)
        signo(0, Offset(w * 0.94f - 2 * u, h * 0.52f), h * 0.42f, lapis)
        textoEn(medidor, texto, Rect(w * 0.12f, 6.5f * u, w * 0.88f, h - 7.5f * u), letra, tinta, 15f, peso = FontWeight.Bold, espaciado = 0.5f, minimo = 8f)
    }

    // ---------------------------------------------------------------- las cartas

    private val PAPIRO_CARTA = Color(0xFFE6D29A)
    private val PAPIRO_DORSO = Color(0xFFCDB57A)
    private val FIBRA = Color(0xFF8A6E3A)
    private val BARRO_SELLO = Color(0xFF6E4A2E)
    private val TERRACOTA = Color(0xFFB0643A)
    private val TERRACOTA_OSC = Color(0xFF7E4220)
    private val TERRACOTA_LUZ = Color(0xFFCD8656)

    /** El color de las cartas con su luz: de día, tal cual; en modo oscuro, hundido en la penumbra del templo. */
    private fun cc(x: Color, oscuro: Boolean): Color = if (oscuro) mezcla(x, Color(0xFF110D08), 0.55f) else x

    /** Las fibras del papiro en [r]: las de una capa a lo largo y las de la otra a lo alto, apenas marcadas. */
    private fun DrawScope.fibrasPapiro(r: Rect, oscuro: Boolean) {
        val u = density
        var y = r.top + 1.5f * u
        while (y < r.bottom) { drawLine(cc(FIBRA, oscuro).copy(alpha = 0.12f), Offset(r.left, y), Offset(r.right, y), 0.7f * u); y += 2.6f * u }
        var x = r.left + 2 * u
        while (x < r.right) { drawLine(cc(FIBRA, oscuro).copy(alpha = 0.07f), Offset(x, r.top), Offset(x, r.bottom), 0.7f * u); x += 4 * u }
    }

    /**
     * El sello de Egipto, centrado en [c]: un terrón de barro del Nilo apretado sobre el nudo del lino, con el escarabajo del
     * sello hundido dentro de un óvalo y su borde de puntos.
     */
    private fun DrawScope.selloEgipto(c: Offset, r: Float, oscuro: Boolean, semilla: Int) {
        fun k(x: Color) = if (oscuro) mezcla(x, Color(0xFF110D08), 0.3f) else x
        val terron = discoAnt(c, r, semilla, 14, 0.16f)
        translate(r * 0.07f, r * 0.12f) { drawPath(terron, Color.Black.copy(alpha = 0.32f)) }
        drawPath(terron, k(BARRO_SELLO))
        val hondo = k(mezcla(BARRO_SELLO, Color.Black, 0.4f))
        val claro = k(mezcla(BARRO_SELLO, Color(0xFFE6D2A0), 0.45f))
        drawOval(hondo.copy(alpha = 0.8f), Offset(c.x - r * 0.52f, c.y - r * 0.66f), Size(r * 1.04f, r * 1.32f), style = Stroke(r * 0.07f))
        for (j in 0 until 14) {
            val a = (j * Math.PI / 7).toFloat()
            drawCircle(hondo.copy(alpha = 0.7f), r * 0.045f, Offset(c.x + r * 0.66f * cos(a) * 0.85f, c.y + r * 0.8f * sin(a)))
        }
        signo(3, c, r * 0.95f, claro)
        drawCircle(Color.White.copy(alpha = 0.16f), r * 0.16f, c - Offset(r * 0.4f, r * 0.42f))
    }

    /**
     * La carta de Egipto: la hoja de papiro doblada en paquete, como se mandaban las cartas, con las fibras a la vista. La
     * solapa recta lleva en su orilla la banda de rectángulos de colores y, pintados, el ojo udjat y el ankh; al pie, la
     * dirección a tinta negra. Las de al futuro se atan con un hilo de lino ([EstadoCarta.cordon]) que da dos vueltas por la
     * cintura del paquete; sobre el nudo, el terrón de barro con el escarabajo. Al abrirse, el barro se parte, la solapa se
     * levanta mostrando el dorso del papiro y la hoja sube. Un degradado: el brillo del sello.
     */
    override fun DrawScope.sobre(e: EstadoCarta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val q = e.cuerpo
        val W = q.width; val H = q.height; val cx = q.center.x
        val ab = e.abrir.coerceIn(0f, 1f)
        val s = e.semilla
        fun c(x: Color) = cc(x, oscuro)
        val caja = Rect(q.left + W * 0.03f, q.top + H * 0.06f, q.right - W * 0.03f, q.bottom - H * 0.05f)
        // La hoja de atrás del paquete, con su sombra.
        drawRect(Color.Black.copy(alpha = 0.28f), caja.topLeft + Offset(2.5f * u, 4 * u), caja.size)
        drawRect(c(PAPIRO_DORSO), caja.topLeft, caja.size)
        val boca = caja.top + H * 0.1f
        if (ab > 0f) drawRect(c(Color(0xFF2A1E10)).copy(alpha = ab), Offset(caja.left + 3 * u, caja.top + 2 * u), Size(caja.width - 6 * u, boca - caja.top))
        // La solapa: una tira recta doblada hacia abajo; al abrirse se levanta y queda de pie detrás, por el dorso.
        val kk = 1f - 2f * ab
        val bordeCerrado = q.top + H * 0.44f
        val bordeY = caja.top + (bordeCerrado - caja.top) * kk
        val solapa = Rect(caja.left, minOf(caja.top, bordeY), caja.right, maxOf(caja.top, bordeY))
        if (kk < 0f) {
            drawRect(c(PAPIRO_DORSO), solapa.topLeft, solapa.size)
            clipRect(solapa.left, solapa.top, solapa.right, solapa.bottom) { fibrasPapiro(solapa, oscuro) }
            drawRect(c(FIBRA).copy(alpha = 0.6f), solapa.topLeft, solapa.size, style = Stroke(1 * u))
        }
        if (ab > 0.3f || e.subir > 0f) hojaCartaAnt(q, e.subir, e.hoja, e.renglones, oscuro, 0.11f, 0.89f, fibras = true)
        // El frente del paquete, con sus fibras y los dobleces.
        val frente = Rect(caja.left, boca, caja.right, caja.bottom)
        drawRect(c(PAPIRO_CARTA), frente.topLeft, frente.size)
        clipRect(frente.left, frente.top, frente.right, frente.bottom) {
            fibrasPapiro(frente, oscuro)
            drawLine(c(FIBRA).copy(alpha = 0.3f), Offset(frente.left + frente.width * 0.33f, frente.top), Offset(frente.left + frente.width * 0.33f, frente.bottom), 0.8f * u)
            drawLine(c(FIBRA).copy(alpha = 0.3f), Offset(frente.left + frente.width * 0.67f, frente.top), Offset(frente.left + frente.width * 0.67f, frente.bottom), 0.8f * u)
        }
        drawRect(c(FIBRA).copy(alpha = 0.7f), frente.topLeft, frente.size, style = Stroke(1 * u))
        drawLine(c(Color(0xFF5A4420)), Offset(frente.left, boca), Offset(frente.right, boca), 1.3f * u)
        // La dirección, a tinta negra; si no la hay (en grande), una línea de signos.
        val tinta = legibleAnt(c(PAPIRO_CARTA), c(NEGRO), PAPIRO_CARTA)
        val alto1 = q.top + H * 0.62f
        if (e.linea1.isBlank() && e.linea2.isBlank()) {
            for (j in 0 until 4) signo(j, Offset(cx + (j - 1.5f) * W * 0.14f, q.top + H * 0.76f), H * 0.16f, c(if (j % 2 == 0) LAPIS else ROJO).copy(alpha = 0.8f))
        }
        lineasCartaAnt(medidor, e.linea1, e.linea2, Rect(caja.left + W * 0.06f, alto1, caja.right - W * 0.06f, q.top + H * 0.79f),
            Rect(caja.left + W * 0.08f, q.top + H * 0.78f, caja.right - W * 0.08f, q.top + H * 0.9f), Letra.messiri, tinta, 12f, 9.5f)
        // La solapa cerrada (o levantándose): el papiro con su banda de colores en la orilla, el udjat y el ankh.
        if (kk >= 0f) {
            drawRect(Color.Black.copy(alpha = 0.2f), Offset(solapa.left, solapa.bottom), Size(solapa.width, 2.5f * u))
            drawRect(c(PAPIRO_CARTA), solapa.topLeft, solapa.size)
            clipRect(solapa.left, solapa.top, solapa.right, solapa.bottom) {
                fibrasPapiro(solapa, oscuro)
                val banda = minOf(H * 0.07f, 8 * u)
                if (solapa.height > banda * 1.5f) {
                    bandaColores(Rect(solapa.left + 2 * u, solapa.bottom - banda - 2 * u, solapa.right - 2 * u, solapa.bottom - 2 * u), oscuro, false)
                    val mid = solapa.top + (solapa.height - banda) * 0.5f
                    val vis = (1f - ab * 3f).coerceIn(0f, 1f)
                    if (vis > 0f) {
                        signo(2, Offset(caja.left + W * 0.2f, mid), H * 0.2f, c(LAPIS).copy(alpha = vis))
                        signo(0, Offset(caja.right - W * 0.2f, mid), H * 0.22f, c(ROJO).copy(alpha = vis))
                    }
                }
            }
            drawRect(c(FIBRA).copy(alpha = 0.7f), solapa.topLeft, solapa.size, style = Stroke(1 * u))
        }
        // El lino: dos vueltas a la cintura del paquete, a la altura de la orilla de la solapa.
        val rS = minOf(H * 0.15f, W * 0.1f)
        val cS = Offset(cx, bordeCerrado)
        if (e.cordon > 0f) listOf(-0.28f, 0.28f).forEachIndexed { i, dy ->
            val y = cS.y + rS * dy
            val av = ((e.cordon - i * 0.4f) / 0.6f).coerceIn(0f, 1f)
            val ruta = listOf(Offset(caja.left - W * 0.012f, y + 2 * u), Offset(cx - rS, y), Offset(cx + rS, y), Offset(caja.right + W * 0.012f, y - 2 * u))
            trazoParcialAnt(ruta.map { it + Offset(0f, 1.2f * u) }, av, Color.Black.copy(alpha = 0.28f), 2.4f * u)
            trazoParcialAnt(ruta, av, c(LINO), 2 * u)
            trazoParcialAnt(ruta, av, c(LINO_OSC), 0.6f * u)
        }
        selloCartaAnt(cS, rS, e.brillo, e.romper.coerceIn(0f, 1f), e.escalaSello, e.alfaSello, AMARILLO) { selloEgipto(cS, rS, oscuro, s) }
    }

    override val cajaPrevia: Rect = Rect(0.17f, 0.175f, 0.83f, 0.345f)

    override val altoBuzon: Dp = 210.dp

    /** Un rollo de papiro de pie que asoma de la boca de una tinaja, con la boca del rollo en [o]: listo, atado y sellado, o desenrollándose. */
    private fun DrawScope.rolloTinaja(o: Offset, d: Float, largo: Float, giro: Float, estado: Int, oscuro: Boolean, semilla: Int) {
        val u = density
        rotate(giro, Offset(o.x, o.y + largo)) {
            val papiro = cc(PAPIROS[semilla % PAPIROS.size], oscuro)
            if (estado == 2) {
                // La hoja que se desenrolla y cuelga por fuera, con renglones de hierático.
                val hoja = Rect(o.x, o.y + d * 0.3f, o.x + d * 2.2f, o.y + largo * 0.85f)
                drawRect(Color.Black.copy(alpha = 0.2f), hoja.topLeft + Offset(1.2f * u, 1.2f * u), hoja.size)
                drawRect(papiro, hoja.topLeft, hoja.size)
                for (j in 0 until 5) drawLine(cc(NEGRO, oscuro).copy(alpha = 0.45f), Offset(hoja.left + 3 * u, hoja.top + (5 + 5 * j) * u), Offset(hoja.right - (2 + 4 * (j % 2)) * u, hoja.top + (5 + 5 * j) * u), 0.8f * u)
                drawLine(cc(ROJO, oscuro), Offset(hoja.left + 3 * u, hoja.top + 3 * u), Offset(hoja.left + 10 * u, hoja.top + 3 * u), 1 * u)
            }
            drawRect(papiro, Offset(o.x - d / 2, o.y), Size(d, largo))
            drawRect(Color.Black.copy(alpha = 0.16f), Offset(o.x + d * 0.2f, o.y), Size(d * 0.3f, largo))
            drawOval(cc(PAPIRO_OSC, oscuro), Offset(o.x - d / 2, o.y - d * 0.22f), Size(d, d * 0.44f))
            drawArc(cc(FIBRA, oscuro), 0f, 300f, false, Offset(o.x - d * 0.3f, o.y - d * 0.12f), Size(d * 0.6f, d * 0.24f), style = Stroke(0.7f * u))
            if (estado != 2) {
                val y = o.y + largo * 0.3f
                if (estado == 1) listOf(-2f, 2f).forEach { dy -> drawLine(cc(LINO, oscuro), Offset(o.x - d / 2 - 1 * u, y + dy * u), Offset(o.x + d / 2 + 1 * u, y + dy * u), 1.3f * u) }
                drawPath(discoAnt(Offset(o.x, y), d * 0.42f, semilla, 10, 0.15f), cc(BARRO_SELLO, oscuro))
                signo(3, Offset(o.x, y), d * 0.4f, cc(mezcla(BARRO_SELLO, Color(0xFFE6D2A0), 0.4f), oscuro))
            }
        }
    }

    /**
     * El buzón de Egipto: tres tinajas del archivo de la Casa de la Correspondencia, de barro rojo con su banda de lotos
     * pintada, de pie en sus aros contra la pared de arenisca con el friso jeker y la banda de colores. De la boca de cada una
     * asoman los rollos: a la izquierda las cartas que ya se pueden abrir, con el sello de barro brillando; en medio las atadas
     * con lino y selladas; a la derecha las abiertas, con la hoja desenrollada colgando por fuera. En la panza de cada tinaja,
     * en un recuadro encalado, el nombre y la cuenta dentro de un cartucho de lapislázuli. En modo oscuro el cuarto se apaga.
     * Un degradado: el brillo de las listas.
     */
    override fun DrawScope.buzon(e: EstadoBuzon, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = cc(x, oscuro)
        // La pared: el friso jeker arriba, la arenisca, la banda de colores y el piso de losas.
        drawRect(c(ARENISCA))
        jeker(Rect(0f, 0f, w, h * 0.13f), c(VERDE), c(ROJO))
        drawRect(c(ARENISCA_OSC), Offset(0f, h * 0.13f), Size(w, 2 * u))
        bandaColores(Rect(0f, h * 0.7f, w, h * 0.76f), oscuro, false)
        drawRect(c(HONDO), Offset(0f, h * 0.88f), Size(w, h * 0.12f))
        var x = 0f
        while (x < w) { drawLine(c(ARENISCA_OSC), Offset(x, h * 0.88f), Offset(x, h), 1 * u); x += 30 * u }
        // Entre las tinajas, lotos pintados en la pared.
        listOf(w * 0.34f, w * 0.66f).forEach { lx ->
            val b = Offset(lx, h * 0.6f)
            drawLine(c(VERDE), b, Offset(lx, h * 0.36f), 1.4f * u)
            drawPath(Path().apply { moveTo(lx, h * 0.36f); lineTo(lx - 8 * u, h * 0.26f); lineTo(lx - 3 * u, h * 0.3f); lineTo(lx, h * 0.24f); lineTo(lx + 3 * u, h * 0.3f); lineTo(lx + 8 * u, h * 0.26f); close() }, c(LAPIS))
            drawPath(Path().apply { moveTo(lx, h * 0.5f); quadraticTo(lx - 10 * u, h * 0.46f, lx - 12 * u, h * 0.4f); quadraticTo(lx - 4 * u, h * 0.42f, lx, h * 0.5f); close() }, c(VERDE))
        }
        val zonas = listOf(w * 0.17f, w * 0.5f, w * 0.83f)
        val cuentas = listOf(e.listas, e.selladas, e.abiertas)
        val nombres = listOf("para abrir", "selladas", "abiertas")
        val ancho = minOf(w * 0.24f, 84 * u)
        val boca = h * 0.34f
        for (g in 0 until 3) {
            val cx = zonas[g]
            val n = minOf(cuentas[g], 3)
            if (g == 0 && n > 0) mancha(Offset(cx, boca - 12 * u), ancho * 0.9f, AMARILLO.copy(alpha = if (oscuro) 0.55f else 0.45f))
            // Los rollos, detrás del labio de la tinaja.
            for (i in 0 until n) {
                val dx = (i - (n - 1) / 2f) * 11 * u
                rolloTinaja(Offset(cx + dx - (if (g == 2) 6 * u else 0f), boca - (26 + 6 * ((i + 1) % 2)) * u), 10 * u, 40 * u, dx / u * 1.3f, g, oscuro, 7 * g + i)
            }
            // La tinaja: labio, cuello, hombros anchos y la panza que se angosta hasta el pie, en su aro.
            val tinaja = Path().apply {
                moveTo(cx - ancho * 0.24f, boca)
                lineTo(cx - ancho * 0.2f, boca + h * 0.05f)
                cubicTo(cx - ancho * 0.62f, boca + h * 0.08f, cx - ancho * 0.56f, h * 0.72f, cx - ancho * 0.14f, h * 0.86f)
                lineTo(cx + ancho * 0.14f, h * 0.86f)
                cubicTo(cx + ancho * 0.56f, h * 0.72f, cx + ancho * 0.62f, boca + h * 0.08f, cx + ancho * 0.2f, boca + h * 0.05f)
                lineTo(cx + ancho * 0.24f, boca); close()
            }
            translate(3 * u, 3 * u) { drawPath(tinaja, Color.Black.copy(alpha = 0.28f)) }
            drawPath(tinaja, c(TERRACOTA))
            clipPath(tinaja) {
                drawRect(c(TERRACOTA_OSC).copy(alpha = 0.5f), Offset(cx + ancho * 0.18f, boca), Size(ancho, h))
                drawRect(c(TERRACOTA_LUZ).copy(alpha = 0.45f), Offset(cx - ancho * 0.42f, boca), Size(ancho * 0.12f, h))
                // La banda de pétalos de loto en los hombros.
                val yb = boca + h * 0.1f
                drawRect(c(LAPIS), Offset(cx - ancho, yb), Size(ancho * 2, 2 * u))
                var px = cx - ancho * 0.6f
                while (px < cx + ancho * 0.6f) {
                    drawPath(Path().apply { moveTo(px, yb + 2 * u); quadraticTo(px + 3 * u, yb + 10 * u, px + 6 * u, yb + 2 * u); close() }, c(if (((px - cx) / u).toInt() / 6 % 2 == 0) VERDE else LAPIS))
                    px += 6 * u
                }
                drawRect(c(LAPIS), Offset(cx - ancho, yb + 11 * u), Size(ancho * 2, 1.4f * u))
            }
            drawPath(tinaja, c(TERRACOTA_OSC), style = Stroke(1 * u))
            drawRoundRect(c(TERRACOTA_LUZ), Offset(cx - ancho * 0.28f, boca - 3 * u), Size(ancho * 0.56f, 5 * u), CornerRadius(2.5f * u))
            drawRoundRect(c(HONDO), Offset(cx - ancho * 0.26f, h * 0.85f), Size(ancho * 0.52f, 6 * u), CornerRadius(3 * u))
            // El recuadro encalado con el nombre y el cartucho con la cuenta.
            val panel = Rect(cx - ancho * 0.36f, boca + h * 0.17f, cx + ancho * 0.36f, boca + h * 0.46f)
            drawRoundRect(c(LINO), panel.topLeft, panel.size, CornerRadius(2 * u))
            val tinta = legibleAnt(c(LINO), c(NEGRO), LINO)
            textoEn(medidor, nombres[g], Rect(panel.left + 2 * u, panel.top + 1 * u, panel.right - 2 * u, panel.top + panel.height * 0.36f), Letra.messiri, tinta, 9.5f, minimo = 6f)
            val cartucho = Rect(panel.left + panel.width * 0.14f, panel.top + panel.height * 0.38f, panel.right - panel.width * 0.14f, panel.bottom - panel.height * 0.08f)
            drawRoundRect(c(LAPIS), cartucho.topLeft, cartucho.size, CornerRadius(cartucho.height / 2), style = Stroke(1.6f * u))
            drawLine(c(LAPIS), Offset(cartucho.right + 2 * u, cartucho.top + 2 * u), Offset(cartucho.right + 2 * u, cartucho.bottom - 2 * u), 1.6f * u)
            textoEn(medidor, cuentas[g].toString(), Rect(cartucho.left + 3 * u, cartucho.top + 1 * u, cartucho.right - 3 * u, cartucho.bottom - 1 * u), Letra.messiri, tinta, 15f, peso = FontWeight.Bold, minimo = 7f)
            if (g == 0 && n > 0) for (i in 0 until n) drawPath(estrella(Offset(cx + (i - (n - 1) / 2f) * 11 * u + 7 * u, boca - 30 * u), 3.4f * u, 4, 0.25f, 0f), AMARILLO)
        }
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
 * una lunita en lugar del jade. De noche sale la luna en el vano y sube el humo del copal. La tapa es ese códice grande y de
 * frente: la piel de jaguar con sus rosetas y cuentas de jade, la etiqueta de estuco con la fecha, el día en barras y puntos
 * y las palabras, el cordón con su cuenta (de noche, la luna) y los pliegues por el canto; al abrirse, la tapa gira y deja
 * ver las páginas del biombo con sus signos de días. Distinto del Ideario, que es el muro pintado con la greca escalonada,
 * el talud-tablero y las petacas de palma.
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

    // ---------------------------------------------------------------- la tapa

    override val altoTapa: Dp = 188.dp
    override val cajaTapa = Rect(0.14f, 0.335f, 0.76f, 0.7f)
    override val letraTapa: FontFamily = LetraExtra.codice
    override val tamTapa = 14f
    override val lineasTapa = 3
    override val centradoTapa = true

    /** El estuco de la etiqueta, donde va el extracto: de noche lo alumbra la luna, así que se hunde menos. */
    private fun estucoTapa(oscuro: Boolean, noche: Boolean): Color {
        var r = ESTUCO
        if (oscuro) r = mezcla(r, Color(0xFF100C08), 0.36f)
        if (noche) r = mezcla(r, NOCHE, 0.3f)
        return r
    }

    override fun tintaTapa(oscuro: Boolean, noche: Boolean): Color = legibleAnt(estucoTapa(oscuro, noche), NEGRO, ESTUCO)
    override fun palabrasTapa(n: Int): String = palabrasAnt(n)

    /** Un signo de día pintado en una página del códice: el cuadro con su filete rojo y, dentro, una figura según [k]. */
    private fun DrawScope.signoDia(r: Rect, k: Int, oscuro: Boolean, noche: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, noche)
        drawRoundRect(c(CINABRIO), r.topLeft, r.size, CornerRadius(r.width * 0.2f), style = Stroke(1.1f * u))
        val o = r.center
        val m = r.width * 0.32f
        when (k % 4) {
            0 -> { drawCircle(c(JADE), m, o); drawCircle(c(NEGRO), m * 0.35f, o) }
            1 -> {
                drawRect(c(JAGUARES[0]), Offset(o.x - m, o.y - m * 0.7f), Size(m * 2, m * 1.4f))
                for (j in 0 until 3) drawCircle(c(NEGRO), m * 0.18f, Offset(o.x - m * 0.6f + j * m * 0.6f, o.y))
            }
            2 -> {
                val p = Path().apply { moveTo(o.x - m, o.y + m); lineTo(o.x - m, o.y); lineTo(o.x, o.y); lineTo(o.x, o.y - m); lineTo(o.x + m, o.y - m) }
                drawPath(p, c(NEGRO), style = Stroke(1.6f * u))
                drawCircle(c(CINABRIO), m * 0.3f, Offset(o.x + m * 0.4f, o.y + m * 0.5f))
            }
            else -> {
                drawOval(c(NEGRO), Offset(o.x - m, o.y - m * 0.5f), Size(m * 2, m))
                drawCircle(c(ESTUCO), m * 0.3f, Offset(o.x - m * 0.3f, o.y))
                drawCircle(c(CINABRIO), m * 0.22f, Offset(o.x + m * 0.45f, o.y))
            }
        }
    }

    /** La tapa, recortada a su lienzo: lo que se aparta al abrir y la luz de noche no se salen de su lugar. */
    override fun DrawScope.tapa(e: EstadoTapa, medidor: TextMeasurer, oscuro: Boolean) = clipRect { dibujarTapa(e, medidor, oscuro) }

    private fun DrawScope.dibujarTapa(e: EstadoTapa, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, e.noche)
        val a = e.abierto.coerceIn(0f, 1f)
        val sem = e.semilla and 0x7fffffff
        val piel = c(JAGUARES[sem % JAGUARES.size])
        val estuco = estucoTapa(oscuro, e.noche)
        val tapa = Rect(w * 0.025f, h * 0.04f, w * 0.9f, h * 0.96f)
        // Las páginas plegadas en biombo por el canto de la derecha: más pliegues cuanto más escribiste.
        val canto = Rect(tapa.right - 2 * u, h * 0.07f, w * 0.975f, h * 0.93f)
        val pliegues = 4 + ((e.palabras / 800f).coerceIn(0f, 1f) * 6).toInt()
        drawRect(c(ESTUCO), canto.topLeft, canto.size)
        for (j in 0 until pliegues) {
            val x = canto.left + canto.width * (j + 0.5f) / pliegues
            drawLine(c(CINABRIO).copy(alpha = 0.55f), Offset(x, canto.top), Offset(x, canto.bottom), 0.7f * u)
            if (j % 2 == 1) drawRect(Color.Black.copy(alpha = 0.1f), Offset(x, canto.top), Size(canto.width / pliegues, canto.height))
        }
        val zigzag = Path().apply {
            moveTo(canto.left, canto.top)
            for (j in 0..pliegues) lineTo(canto.left + canto.width * j / pliegues, canto.top + (if (j % 2 == 0) 0f else 2 * u))
        }
        drawPath(zigzag, c(CALIZA_OSC), style = Stroke(0.8f * u))
        drawRect(c(CALIZA_OSC), canto.topLeft, canto.size, style = Stroke(0.6f * u))

        // Lo que se ve al abrir: las primeras páginas del biombo, de estuco, con sus registros rojos, signos de días y cifras.
        if (a > 0.01f) {
            val paginas = 4
            val ancho = (canto.right - tapa.left) / paginas
            for (p in 0 until paginas) {
                val x0 = tapa.left + ancho * p
                val adentro = if (p % 2 == 1) 3 * u else 0f
                val pag = Rect(x0, tapa.top + adentro, x0 + ancho, tapa.bottom - adentro)
                drawRect(c(ESTUCO), pag.topLeft, pag.size)
                if (p % 2 == 1) drawRect(Color.Black.copy(alpha = 0.08f), pag.topLeft, pag.size)
                drawLine(c(CALIZA_OSC), Offset(pag.right, pag.top), Offset(pag.right, pag.bottom), 0.8f * u)
                listOf(0.33f, 0.66f).forEach { f -> drawLine(c(CINABRIO).copy(alpha = 0.7f), Offset(pag.left + 4 * u, pag.top + pag.height * f), Offset(pag.right - 4 * u, pag.top + pag.height * f), 1f * u) }
                for (fila in 0 until 3) {
                    val y0 = pag.top + pag.height * (fila / 3f) + pag.height * 0.06f
                    val lado0 = minOf(ancho * 0.36f, pag.height * 0.22f)
                    signoDia(Rect(pag.left + ancho * 0.12f, y0, pag.left + ancho * 0.12f + lado0, y0 + lado0), p * 3 + fila, oscuro, e.noche)
                    numeroMaya(1 + (azar(e.semilla, p * 3 + fila) * 18).toInt(), Rect(pag.left + ancho * 0.58f, y0, pag.right - ancho * 0.1f, y0 + lado0), c(NEGRO), c(CINABRIO))
                }
            }
        }

        // La tapa de piel de jaguar gira sobre el canto izquierdo y deja ver las páginas.
        withTransform({ scale(1f - 0.93f * a, 1f, Offset(tapa.left, tapa.center.y)) }) {
            drawRect(Color.Black.copy(alpha = 0.25f), Offset(tapa.left + 2 * u, tapa.top + 3 * u), tapa.size)
            drawRoundRect(piel, tapa.topLeft, tapa.size, CornerRadius(3 * u))
            drawRect(Color.White.copy(alpha = 0.1f), tapa.topLeft, Size(tapa.width * 0.05f, tapa.height))
            drawRect(Color.Black.copy(alpha = 0.16f), Offset(tapa.left, tapa.bottom - tapa.height * 0.06f), Size(tapa.width, tapa.height * 0.06f))
            // Las rosetas: anillos rotos en tres arcos con su centro.
            // Las rosetas: anillos rotos en tres arcos con su centro, sembradas por los márgenes que deja la etiqueta.
            val margen = 0.12f
            for (j in 0 until 40) {
                val lado = j % 4
                val f = azar(e.semilla, 10 + j)
                val g = margen * azar(e.semilla, 70 + j)
                val (fx, fy) = when (lado) { 0 -> f to g; 1 -> f to 1f - g; 2 -> g * 0.8f to f; else -> 1f - g * 0.6f to f }
                val rc = Offset(tapa.left + tapa.width * fx, tapa.top + tapa.height * fy)
                val rr = (3.2f + 2.6f * azar(e.semilla, 130 + j)) * u
                val giro = 360f * azar(e.semilla, 190 + j)
                drawCircle(mezcla(piel, c(ROSETA), 0.2f), rr * 0.7f, rc)
                for (s in 0 until 3) {
                    drawArc(c(ROSETA).copy(alpha = 0.85f), giro + s * 120f, 78f, false, Offset(rc.x - rr, rc.y - rr), Size(rr * 2, rr * 2), style = Stroke(1.7f * u, cap = StrokeCap.Round))
                }
                drawCircle(c(ROSETA).copy(alpha = 0.55f), rr * 0.3f, rc)
            }
            drawRoundRect(c(BARRO_OSC), tapa.topLeft, tapa.size, CornerRadius(3 * u), style = Stroke(1.4f * u))
            // Las cuentas de jade en las esquinas.
            listOf(Offset(tapa.left + 6 * u, tapa.top + 6 * u), Offset(tapa.right - 6 * u, tapa.top + 6 * u),
                Offset(tapa.left + 6 * u, tapa.bottom - 6 * u), Offset(tapa.right - 6 * u, tapa.bottom - 6 * u)).forEach { p ->
                drawCircle(c(JADE), 3.2f * u, p)
                drawCircle(Color.White.copy(alpha = 0.35f), 1 * u, Offset(p.x - 1 * u, p.y - 1 * u))
            }
            // La etiqueta de estuco con su filete rojo: la fecha, el extracto, el día en barras y puntos y las palabras.
            val etiqueta = Rect(w * 0.1f, h * 0.11f, w * 0.8f, h * 0.89f)
            drawRect(Color.Black.copy(alpha = 0.22f), Offset(etiqueta.left + 1.5f * u, etiqueta.top + 2 * u), etiqueta.size)
            drawRect(estuco, etiqueta.topLeft, etiqueta.size)
            drawRect(c(CINABRIO), etiqueta.topLeft, etiqueta.size, style = Stroke(1.4f * u))
            val filete = etiqueta.deflate(3 * u)
            drawRect(c(CINABRIO).copy(alpha = 0.6f), filete.topLeft, filete.size, style = Stroke(0.6f * u))
            // En las esquinas de la etiqueta, la greca escalonada en pequeño.
            listOf(filete.topLeft to Offset(1f, 1f), Offset(filete.right, filete.top) to Offset(-1f, 1f),
                Offset(filete.left, filete.bottom) to Offset(1f, -1f), filete.bottomRight to Offset(-1f, -1f)).forEach { (o, s) ->
                val g = 3.2f * u
                val esc = Path().apply {
                    moveTo(o.x + s.x * 2 * u, o.y + s.y * (2 * u + 3 * g)); lineTo(o.x + s.x * 2 * u, o.y + s.y * (2 * u + 2 * g))
                    lineTo(o.x + s.x * (2 * u + g), o.y + s.y * (2 * u + 2 * g)); lineTo(o.x + s.x * (2 * u + g), o.y + s.y * (2 * u + g))
                    lineTo(o.x + s.x * (2 * u + 2 * g), o.y + s.y * (2 * u + g)); lineTo(o.x + s.x * (2 * u + 2 * g), o.y + s.y * 2 * u)
                    lineTo(o.x + s.x * (2 * u + 3 * g), o.y + s.y * 2 * u)
                }
                drawPath(esc, c(CINABRIO).copy(alpha = 0.75f), style = Stroke(1f * u))
            }
            val tinta = legibleAnt(estuco, c(NEGRO), ESTUCO)
            val zona = Rect(w * 0.16f, h * 0.135f, w * 0.74f, h * 0.275f)
            val fechas = renglonesFechaAnt(e.fecha, 30)
            if (fechas.size == 1) {
                textoEn(medidor, fechas[0], zona, letra, tinta, 15f, peso = FontWeight.SemiBold, minimo = 7.5f)
            } else {
                val corte = zona.top + zona.height * 0.56f
                textoEn(medidor, fechas[0], Rect(zona.left, zona.top, zona.right, corte), letra, tinta, 14f, peso = FontWeight.SemiBold, minimo = 7.5f)
                textoEn(medidor, fechas[1], Rect(zona.left, corte, zona.right, zona.bottom), letra, tinta.copy(alpha = 0.85f), 11f, minimo = 7f)
            }
            drawLine(c(CINABRIO).copy(alpha = 0.7f), Offset(filete.left + 14 * u, h * 0.3f), Offset(filete.right - 14 * u, h * 0.3f), 0.9f * u)
            drawLine(c(CINABRIO).copy(alpha = 0.7f), Offset(filete.left + 14 * u, h * 0.725f), Offset(filete.right - 14 * u, h * 0.725f), 0.9f * u)
            // El número del día (el primero de la fecha) en la cuenta maya, y las palabras.
            val numero = Regex("\\d+").find(e.fecha)?.value?.toIntOrNull()?.coerceIn(0, 399) ?: 0
            numeroMaya(numero, Rect(w * 0.14f, h * 0.74f, w * 0.25f, h * 0.86f), tinta, c(CINABRIO))
            if (e.palabras > 0) {
                textoEn(medidor, palabrasTapa(e.palabras), Rect(w * 0.28f, h * 0.745f, w * 0.74f, h * 0.86f), letra, tinta.copy(alpha = 0.8f), 11f, minimo = 7f)
            }
            // El cordón de henequén que ciñe el códice de arriba abajo, con su cuenta de jade (de noche, una lunita).
            val xc = w * 0.85f
            listOf(-1.4f, 1.4f).forEach { d -> drawLine(c(CUERDA), Offset(xc + d * u, tapa.top - 1 * u), Offset(xc + d * u, tapa.bottom + 1 * u), 1.4f * u) }
            var yc = tapa.top
            while (yc < tapa.bottom) { drawLine(c(CUERDA_OSC), Offset(xc - 2 * u, yc), Offset(xc + 2 * u, yc + 2.4f * u), 0.6f * u); yc += 3.2f * u }
            val cuenta = Offset(xc, h * 0.5f)
            val rc = 5.5f * u
            if (e.noche) {
                drawCircle(LUNA, rc, cuenta)
                drawCircle(piel, rc * 0.8f, Offset(cuenta.x + rc * 0.45f, cuenta.y - rc * 0.3f))
                for (j in 0 until 3) drawCircle(LUNA.copy(alpha = 0.9f), 1.1f * u, Offset(xc + (j - 1) * 5 * u, cuenta.y - rc * (2.4f + (j % 2) * 0.8f)))
            } else {
                drawCircle(c(JADE), rc, cuenta)
                drawCircle(mezcla(c(JADE), Color.Black, 0.3f), rc, cuenta, style = Stroke(0.8f * u))
                drawCircle(Color.White.copy(alpha = 0.35f), rc * 0.35f, Offset(cuenta.x - rc * 0.3f, cuenta.y - rc * 0.3f))
            }
            drawLine(c(CUERDA), Offset(xc, cuenta.y + rc), Offset(xc + 3 * u, cuenta.y + rc + 9 * u), 1.2f * u)
            drawLine(c(CUERDA), Offset(xc, cuenta.y + rc), Offset(xc - 2.5f * u, cuenta.y + rc + 10 * u), 1.2f * u)
            // Al girar, la tapa se oscurece.
            drawRect(Color.Black.copy(alpha = 0.45f * a), tapa.topLeft, tapa.size)
        }
    }

    // ---------------------------------------------------------------- la página y el escritor

    override val altoEncabezado: Dp = 120.dp

    /**
     * La greca escalonada (xicalcoliuhqui) a lo largo de [r]: cada motivo sube en escalones y remata en un gancho que se
     * enrolla en cuadro, y el siguiente baja; en [color] y de grosor [g] (dp).
     */
    private fun DrawScope.grecaEscalonada(r: Rect, color: Color, g: Float) {
        val u = density
        val alto = r.height
        val paso = alto * 2.2f
        val e = alto / 3f
        val p = Path()
        var x = r.left
        p.moveTo(x, r.bottom)
        while (x + paso <= r.right + 0.5f) {
            // Tres escalones que suben.
            p.lineTo(x, r.bottom); p.lineTo(x + e * 0.6f, r.bottom); p.lineTo(x + e * 0.6f, r.bottom - e)
            p.lineTo(x + e * 1.2f, r.bottom - e); p.lineTo(x + e * 1.2f, r.bottom - 2 * e)
            p.lineTo(x + e * 1.8f, r.bottom - 2 * e); p.lineTo(x + e * 1.8f, r.top)
            // El gancho que se enrolla.
            p.lineTo(x + e * 3.6f, r.top); p.lineTo(x + e * 3.6f, r.top + e * 1.8f); p.lineTo(x + e * 2.6f, r.top + e * 1.8f)
            p.lineTo(x + e * 2.6f, r.top + e * 0.9f)
            p.moveTo(x + e * 3.6f, r.bottom); p.lineTo(x + paso, r.bottom)
            x += paso
        }
        drawPath(p, color, style = Stroke(g * u))
        drawLine(color, Offset(r.left, r.bottom), Offset(r.right, r.bottom), g * u)
    }

    /** La luna del códice de noche: el disco con el conejo que los mayas veían en ella, centrado en [c] y de radio [rr]. */
    private fun DrawScope.lunaConejo(c: Offset, rr: Float, luna: Color, conejo: Color) {
        drawCircle(luna, rr, c)
        drawOval(conejo, Offset(c.x - rr * 0.4f, c.y - rr * 0.05f), Size(rr * 0.7f, rr * 0.5f))
        drawCircle(conejo, rr * 0.2f, Offset(c.x + rr * 0.32f, c.y - rr * 0.1f))
        drawOval(conejo, Offset(c.x + rr * 0.25f, c.y - rr * 0.68f), Size(rr * 0.14f, rr * 0.5f))
        drawOval(conejo, Offset(c.x + rr * 0.42f, c.y - rr * 0.6f), Size(rr * 0.13f, rr * 0.45f))
    }

    /**
     * El encabezado: un registro de la página del códice, con el marco rojo y la guía roja que lo divide. A la izquierda, el
     * signo del día en su cartucho redondeado (de noche, la luna con su conejo) y, debajo, el número del día del año escrito
     * con barras y puntos en dos niveles de veinte; a la derecha, el rótulo en versalitas, la guía, la fecha (la cuenta del
     * tzolk'in, el haab y la cuenta larga) y el detalle; abajo, la greca escalonada.
     */
    override fun DrawScope.encabezado(e: EstadoEncabezado, p: Pagina, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val marco = Rect(2 * u, 3 * u, w - 2 * u, h - 3 * u)
        val estuco = if (p.oscura) mezcla(p.color, p.tinta, 0.07f) else tonoAnt(ESTUCO, p, oscuro)
        drawRect(Color.Black.copy(alpha = if (p.oscura) 0.3f else 0.1f), Offset(marco.left + 1.5f * u, marco.top + 2 * u), marco.size)
        drawRect(estuco, marco.topLeft, marco.size)
        if (e.noche) clipRect(marco.left, marco.top, marco.right, marco.bottom) { mancha(Offset(marco.left, marco.bottom), w * 0.6f, BRASA.copy(alpha = 0.14f)) }
        val rojo = p.acento
        drawRect(rojo, marco.topLeft, marco.size, style = Stroke(1.6f * u))
        val corte = marco.left + 66 * u
        drawLine(rojo, Offset(corte, marco.top), Offset(corte, marco.bottom - 16 * u), 1.2f * u)
        drawLine(rojo, Offset(marco.left, marco.bottom - 16 * u), Offset(marco.right, marco.bottom - 16 * u), 1.2f * u)
        grecaEscalonada(Rect(marco.left + 6 * u, marco.bottom - 13 * u, marco.right - 6 * u, marco.bottom - 4 * u), rojo.copy(alpha = 0.75f), 1.1f)
        // El signo del día (o la luna) y el número maya del día del año.
        val celda = Rect(marco.left + 13 * u, marco.top + 8 * u, corte - 13 * u, marco.top + 48 * u)
        val jade = if (p.oscura) p.detalle else tonoAnt(JADE, p, oscuro)
        val negro = if (p.oscura) p.tinta else NEGRO
        if (e.noche) {
            drawRoundRect(tonoAnt(CIELO_NOCHE, p, oscuro), celda.topLeft, celda.size, CornerRadius(celda.width * 0.2f))
            lunaConejo(celda.center, celda.width * 0.32f, LUNA, mezcla(CIELO_NOCHE, LUNA, 0.35f))
            drawRoundRect(rojo, celda.topLeft, celda.size, CornerRadius(celda.width * 0.2f), style = Stroke(1.2f * u))
        } else {
            drawRoundRect(rojo, celda.topLeft, celda.size, CornerRadius(celda.width * 0.2f), style = Stroke(1.2f * u))
            val o = celda.center
            val m = celda.width * 0.3f
            drawCircle(jade, m, o)
            drawCircle(estuco, m * 0.6f, o)
            drawCircle(negro, m * 0.3f, o)
            for (k in 0 until 4) {
                val a = Math.toRadians(45.0 + 90.0 * k)
                drawCircle(rojo, 2 * u, Offset(o.x + m * 1.45f * cos(a).toFloat(), o.y + m * 1.45f * sin(a).toFloat()))
            }
        }
        numeroMaya(e.numero, Rect(celda.left + 2 * u, celda.bottom + 4 * u, celda.right - 2 * u, marco.bottom - 19 * u), negro, rojo)
        val izq = corte + 8 * u
        val der = marco.right - 8 * u
        textoEn(medidor, e.rotulo, Rect(izq, marco.top + 5 * u, der, marco.top + 25 * u), LetraExtra.codice, rojo, 14.5f, espaciado = 1f, minimo = 8f)
        drawLine(rojo.copy(alpha = 0.6f), Offset(izq, marco.top + 27 * u), Offset(der, marco.top + 27 * u), 0.8f * u)
        fechaEncAnt(medidor, e.fecha, Rect(izq, marco.top + 29 * u, der, marco.top + 70 * u), LetraExtra.amate, p.tinta, 22f, 20, FontWeight.Bold)
        textoEn(medidor, e.detalle, Rect(izq, marco.top + 71 * u, der, marco.bottom - 18 * u), LetraExtra.amate, p.tenue, 10.5f, minimo = 7f)
    }

    /**
     * La página de estuco del códice plegado: el pliegue de biombo en los dos bordes, el doble marco rojo, una cuenta de
     * jade en cada esquina, la greca escalonada al pie y un signo de día muy tenue abajo a la derecha; de noche, la luna con
     * su conejo abajo a la derecha. En modo oscuro, el estuco en la penumbra y la luz del brasero de copal desde abajo a la
     * izquierda.
     */
    override fun DrawScope.pagina(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        drawRect(p.tinta.copy(alpha = 0.06f), Offset.Zero, Size(5 * u, h))
        drawRect(p.tinta.copy(alpha = 0.06f), Offset(w - 5 * u, 0f), Size(5 * u, h))
        drawRect(p.acento.copy(alpha = 0.45f), Offset(10 * u, 10 * u), Size(w - 20 * u, h - 20 * u), style = Stroke(1.6f * u))
        drawRect(p.acento.copy(alpha = 0.22f), Offset(14 * u, 14 * u), Size(w - 28 * u, h - 28 * u), style = Stroke(0.8f * u))
        listOf(Offset(10 * u, 10 * u), Offset(w - 10 * u, 10 * u), Offset(10 * u, h - 10 * u), Offset(w - 10 * u, h - 10 * u)).forEach {
            drawCircle(p.detalle.copy(alpha = 0.55f), 4 * u, it)
            drawCircle(p.color, 1.4f * u, it)
        }
        grecaEscalonada(Rect(20 * u, h - 32 * u, w - 20 * u, h - 21 * u), p.acento.copy(alpha = 0.38f), 1.2f)
        val s = Offset(w - 46 * u, h - 66 * u)
        drawRoundRect(p.acento.copy(alpha = 0.16f), Offset(s.x - 16 * u, s.y - 16 * u), Size(32 * u, 32 * u), CornerRadius(6 * u), style = Stroke(1.2f * u))
        drawCircle(p.detalle.copy(alpha = 0.14f), 8 * u, s)
        drawCircle(p.tinta.copy(alpha = 0.12f), 3 * u, s)
        if (noche) lunaConejo(Offset(w - 46 * u, h - 120 * u), 13 * u, p.acento.copy(alpha = 0.3f), p.color.copy(alpha = 0.6f))
        if (oscuro && !p.oscura) penumbraAnt(Offset(-w * 0.03f, h * 1.01f), Color(0xFFF0B062), 0.24f)
    }

    /** La barra: una franja de estuco (un degradado, más claro arriba) con la greca escalonada roja y su guía de jade al pie. */
    override fun DrawScope.barra(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val estuco = if (p.oscura) mezcla(p.color, p.tinta, 0.07f) else tonoAnt(ESTUCO, p, oscuro)
        drawRect(Brush.verticalGradient(listOf(mezcla(estuco, Color.White, 0.3f), estuco, mezcla(estuco, CALIZA_OSC, 0.25f)), 0f, h))
        grecaEscalonada(Rect(4 * u, h - 11 * u, w - 4 * u, h - 4 * u), p.acento.copy(alpha = 0.5f), 1f)
        drawLine((if (p.oscura) p.detalle else tonoAnt(JADE, p, oscuro)).copy(alpha = 0.6f), Offset(0f, h - 1.5f * u), Offset(w, h - 1.5f * u), 1.6f * u)
    }

    /**
     * El botón de guardar: una placa de jade pulido (un degradado, con su brillo arriba), con el borde más claro, una cuenta
     * de cinabrio en cada punta y su sombra plana.
     */
    override fun DrawScope.botonGuardar(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val r = Rect(2 * u, 2 * u, w - 3 * u, h - 4 * u)
        val jade = mezcla(JADE, Color.Black, if (oscuro) 0.3f else 0.18f)
        drawRoundRect(Color.Black.copy(alpha = 0.3f), Offset(r.left + 1.5f * u, r.top + 2.5f * u), r.size, CornerRadius(8 * u))
        drawRoundRect(Brush.verticalGradient(listOf(mezcla(jade, Color(0xFF8FD0A8), 0.3f), jade, mezcla(jade, Color.Black, 0.3f)), r.top, r.bottom), r.topLeft, r.size, CornerRadius(8 * u))
        drawRoundRect(Color(0xFF9FDCB8).copy(alpha = 0.5f), Offset(r.left + 2 * u, r.top + 2 * u), Size(r.width - 4 * u, r.height - 4 * u), CornerRadius(6 * u), style = Stroke(0.9f * u))
        listOf(r.left + 8 * u, r.right - 8 * u).forEach { x ->
            drawCircle(CINABRIO, 2.6f * u, Offset(x, r.center.y))
            drawCircle(ESTUCO.copy(alpha = 0.6f), 0.9f * u, Offset(x - 0.8f * u, r.center.y - 0.8f * u))
        }
    }

    override fun tintaBoton(p: Pagina, oscuro: Boolean, noche: Boolean): Color = ESTUCO

    // ---------------------------------------------------------------- la libreta de momentos

    override val altoLibreta: Dp = 256.dp

    /**
     * El color de la libreta con su luz: de día, tal cual; en modo oscuro, hundido en la penumbra del cuarto salvo donde llegan
     * las brasas del brasero ([luz], de 0 lejos a 1 junto a ellas, que además lo entibian).
     */
    private fun lc(x: Color, oscuro: Boolean, luz: Float = 0f): Color =
        if (!oscuro) x else mezcla(mezcla(x, Color(0xFF0B0806), 0.7f - 0.42f * luz), BRASA, 0.14f * luz)

    /** Una rosa de la piel de jaguar: cinco manchas en corro alrededor de un centro más oscuro. */
    private fun DrawScope.rosetaLib(c0: Offset, r: Float, piel: Color, mancha: Color, semilla: Int) {
        for (k in 0 until 5) {
            val ang = k * 1.2566f + azar(semilla, k) * 0.5f
            drawCircle(mancha, r * 0.33f, Offset(c0.x + r * cos(ang), c0.y + r * sin(ang)))
        }
        drawCircle(mezcla(piel, mancha, 0.25f), r * 0.5f, c0)
    }

    /**
     * La libreta de momentos: el códice en biombo de pie sobre el petate. La tira de amate estucada está doblada en zigzag
     * entre dos tapas forradas de piel de jaguar; por el canto se ven los pliegues (más cuanto más escribiste) y lo ciñe un
     * cordel de henequén con su cuenta de jade. En la tapa, la etiqueta de estuco con filete rojo, con el nombre y la cuenta.
     * Delante, la jícara con granos de cacao, la concha con el rojo de cinabrio y los pinceles; a la derecha, el brasero de
     * tres patas con brasas de copal y su humo. Al abrirse, el cordel se suelta, la tapa se corre a la izquierda y el biombo
     * se despliega: aparecen las páginas pintadas, cada una con su marco rojo, sus números de barras y puntos y sus signos.
     * En modo oscuro el cuarto se apaga y alumbra el brasero. Un degradado: el resplandor de las brasas.
     */
    override fun DrawScope.libreta(e: EstadoLibreta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val a = e.abierto.coerceIn(0f, 1f)
        val gordo = (kotlin.math.sqrt(e.hojas.coerceAtLeast(1).toFloat()) / 14f).coerceIn(0.07f, 1f)
        val s = e.semilla
        fun c(x: Color, luz: Float = 0f) = lc(x, oscuro, luz)
        // La pared estucada de sillares, con su banda roja; el piso de tierra apisonada.
        val petate0 = h * 0.62f
        drawRect(c(CALIZA, 0.15f), Offset.Zero, Size(w, petate0))
        var hy = h * 0.08f; var fila = 0
        while (hy < h * 0.5f) {
            drawLine(c(CALIZA_OSC, 0.15f).copy(alpha = 0.6f), Offset(0f, hy), Offset(w, hy), 0.9f * u)
            var x = if (fila % 2 == 0) w * 0.08f else w * 0.2f
            while (x < w) { drawLine(c(CALIZA_OSC, 0.15f).copy(alpha = 0.6f), Offset(x, hy), Offset(x, hy + h * 0.1f), 0.9f * u); x += w * 0.24f }
            hy += h * 0.1f; fila++
        }
        drawRect(c(CINABRIO, 0.15f), Offset(0f, h * 0.48f), Size(w, petate0 - h * 0.48f))
        drawRect(c(CALIZA_LUZ, 0.15f), Offset(0f, h * 0.48f), Size(w, 2 * u))
        drawRect(c(TIERRA, 0.3f), Offset(0f, petate0), Size(w, h - petate0))
        // El resplandor de las brasas (el único degradado).
        val brasero = Offset(w * 0.91f, h * 0.8f)
        mancha(Offset(brasero.x, brasero.y - h * 0.12f), if (oscuro) w * 0.95f else w * 0.3f, BRASA.copy(alpha = if (oscuro) 0.4f else 0.22f))
        // El petate de palma tejido en petatillo, visto en perspectiva.
        val petate = Path().apply { moveTo(w * 0.06f, petate0); lineTo(w * 0.94f, petate0); lineTo(w * 1.04f, h * 0.98f); lineTo(-w * 0.04f, h * 0.98f); close() }
        val palma = c(Color(0xFFD6BC80), 0.5f); val palmaOsc = c(Color(0xFFB0935A), 0.5f)
        drawPath(petate, palma)
        clipPath(petate) {
            var yy = petate0; var f = 0
            while (yy < h) {
                val paso = (5 + 7 * (yy - petate0) / (h - petate0)) * u
                val t0 = (yy - petate0) / (h * 0.98f - petate0)
                val izq = w * 0.06f * (1 - t0) - w * 0.04f * t0; val der = w * 0.94f * (1 - t0) + w * 1.04f * t0
                val n = 30
                for (k in 0 until n) if ((k + f) % 2 == 0) {
                    val xa = izq + (der - izq) * k / n
                    drawRect(palmaOsc, Offset(xa, yy), Size((der - izq) / n, paso))
                }
                drawLine(Color.Black.copy(alpha = 0.12f), Offset(0f, yy), Offset(w, yy), 0.5f * u)
                yy += paso; f++
            }
        }
        drawPath(petate, c(Color(0xFF7A6234), 0.5f), style = Stroke(2.4f * u))
        // El biombo: la tapa se corre a la izquierda y las páginas se despliegan en zigzag.
        val anchoP = w * 0.36f; val altoP = h * 0.56f
        val pie = h * 0.83f
        val x0 = w * 0.25f - w * 0.21f * a
        val tope = pie - altoP
        drawOval(Color.Black.copy(alpha = 0.28f), Offset(x0 - 4 * u, pie - 5 * u), Size(anchoP + anchoP * 1.5f * a + 30 * u, 12 * u))
        val estuco = c(ESTUCO, 0.5f)
        val rojo = c(CINABRIO, 0.5f)
        val negro = c(NEGRO, 0.5f)
        val paneles = 3
        var px = x0 + anchoP
        for (k in 0 until paneles) {
            val pw = anchoP * 0.45f * a
            if (pw < 1f) break
            val haciaAtras = k % 2 == 0
            val baja = 7 * u * a
            // Cada página, un poco en perspectiva: la que va hacia atrás se encoge arriba y abajo hacia su orilla derecha.
            val pag = Path().apply {
                if (haciaAtras) { moveTo(px, tope); lineTo(px + pw, tope + baja); lineTo(px + pw, pie - baja); lineTo(px, pie) }
                else { moveTo(px, tope + baja); lineTo(px + pw, tope); lineTo(px + pw, pie); lineTo(px, pie - baja) }
                close()
            }
            drawPath(pag, if (haciaAtras) estuco else mezcla(estuco, Color.Black, 0.12f))
            clipPath(pag) {
                val r = Rect(px + 4 * u, tope + baja + 4 * u, px + pw - 4 * u, pie - baja - 4 * u)
                drawRect(rojo, r.topLeft, r.size, style = Stroke(1.2f * u))
                drawLine(rojo, Offset(r.left, r.center.y), Offset(r.right, r.center.y), 1 * u)
                if (r.width > 10 * u) {
                    numeroMaya(((s + k * 7) and 0x7fffffff) % 19 + 1, Rect(r.left + 3 * u, r.top + 3 * u, r.right - 3 * u, r.top + r.height * 0.22f), negro, rojo)
                    val g = Offset(r.center.x, r.top + r.height * 0.36f); val m = minOf(r.width, r.height) * 0.16f
                    when (((s + k) and 0x7fffffff) % 3) {
                        0 -> { drawCircle(c(JADE, 0.5f), m, g); drawCircle(negro, m * 0.4f, g) }
                        1 -> { drawRect(c(JAGUARES[0], 0.5f), Offset(g.x - m, g.y - m * 0.7f), Size(m * 2, m * 1.4f)); for (j in 0 until 3) drawCircle(negro, m * 0.18f, Offset(g.x - m * 0.6f + j * m * 0.6f, g.y)) }
                        else -> { drawOval(negro, Offset(g.x - m, g.y - m * 0.5f), Size(m * 2, m)); drawCircle(estuco, m * 0.3f, Offset(g.x - m * 0.3f, g.y)) }
                    }
                    // Abajo, una figura de pie: el tocado, la cara de perfil y el cuerpo, en rojo y negro.
                    val fx = r.center.x; val fy = r.bottom - r.height * 0.06f; val fa = r.height * 0.38f
                    drawRect(rojo, Offset(fx - fa * 0.14f, fy - fa * 0.55f), Size(fa * 0.28f, fa * 0.55f))
                    drawCircle(c(JAGUARES[1], 0.5f), fa * 0.14f, Offset(fx, fy - fa * 0.7f))
                    drawPath(Path().apply { moveTo(fx - fa * 0.2f, fy - fa * 0.82f); lineTo(fx, fy - fa); lineTo(fx + fa * 0.25f, fy - fa * 0.84f); close() }, c(JADE, 0.5f))
                    drawLine(negro, Offset(fx - fa * 0.14f, fy), Offset(fx + fa * 0.14f, fy), 1.4f * u)
                }
            }
            drawPath(pag, c(CALIZA_OSC, 0.5f), style = Stroke(0.8f * u))
            px += pw
        }
        // El canto con sus pliegues, que se ve mientras el biombo sigue cerrado.
        val pliegues = (2 + e.hojas / 8).coerceAtMost(18)
        val canto = (4 + 24 * gordo) * u * (1f - a)
        if (canto > 1f) {
            for (k in 0 until pliegues) {
                val x = x0 + anchoP + k * canto / pliegues
                drawRect(if (k % 2 == 0) estuco else mezcla(estuco, Color.Black, 0.14f), Offset(x, tope + 3 * u + k * 0.3f * u), Size(canto / pliegues + 0.5f, altoP - 6 * u - k * 0.6f * u))
                drawLine(rojo.copy(alpha = 0.6f), Offset(x, tope + 3 * u), Offset(x, pie - 3 * u), 0.6f * u)
            }
            drawRect(c(JAGUARES[2], 0.5f), Offset(x0 + anchoP + canto - 2 * u, tope + 2 * u), Size(3 * u, altoP - 4 * u))
        }
        // La tapa de piel de jaguar, con sus rosas y un ribete de cuero.
        val tapa = Rect(x0, tope, x0 + anchoP, pie)
        val jaguar = c(JAGUARES[(s and 0x7fffffff) % JAGUARES.size], 0.5f)
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(tapa.left + 3 * u, tapa.top + 4 * u), tapa.size)
        drawRect(jaguar, tapa.topLeft, tapa.size)
        clipRect(tapa.left, tapa.top, tapa.right, tapa.bottom) {
            drawRect(Color.White.copy(alpha = 0.1f), tapa.topLeft, Size(tapa.width, tapa.height * 0.1f))
            drawRect(Color.Black.copy(alpha = 0.12f), Offset(tapa.right - tapa.width * 0.12f, tapa.top), Size(tapa.width * 0.12f, tapa.height))
            for (k in 0 until 22) {
                val p = Offset(tapa.left + tapa.width * azar(s, 50 + k), tapa.top + tapa.height * azar(s, 80 + k))
                rosetaLib(p, (3.5f + 2.5f * azar(s, 110 + k)) * u, jaguar, c(ROSETA, 0.5f).copy(alpha = 0.8f), s + k)
            }
        }
        drawRect(c(VIGA_OSC, 0.5f), tapa.topLeft, tapa.size, style = Stroke(2 * u))
        // La etiqueta de estuco con su filete rojo: el nombre y la cuenta.
        val et = Rect(tapa.left + tapa.width * 0.07f, tapa.top + tapa.height * 0.12f, tapa.right - tapa.width * 0.07f, tapa.top + tapa.height * 0.56f)
        val est = c(ESTUCO, 0.75f)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(et.left + 1.5f * u, et.top + 2 * u), et.size)
        drawRect(est, et.topLeft, et.size)
        drawRect(c(CINABRIO, 0.75f), et.deflate(3 * u).topLeft, et.deflate(3 * u).size, style = Stroke(1.4f * u))
        for (k in 0 until 4) drawCircle(c(CINABRIO, 0.75f), 1.6f * u, listOf(et.topLeft, Offset(et.right, et.top), Offset(et.left, et.bottom), et.bottomRight)[k] + Offset(if (k % 2 == 0) 6 * u else -6 * u, if (k < 2) 6 * u else -6 * u))
        textoEn(medidor, e.nombre, Rect(et.left + 9 * u, et.top + et.height * 0.1f, et.right - 9 * u, et.top + et.height * 0.5f), letra,
            legibleAnt(est, c(NEGRO, 0.3f), ESTUCO), 14f, peso = FontWeight.Bold, minimo = 7f)
        // La cuenta, en dos renglones si no cabe en uno.
        val cuenta = renglonesFechaAnt(e.cuenta, 20)
        val zona = Rect(et.left + 9 * u, et.top + et.height * 0.5f, et.right - 9 * u, et.bottom - et.height * 0.1f)
        cuenta.forEachIndexed { k, t ->
            val alto = zona.height / cuenta.size
            textoEn(medidor, t, Rect(zona.left, zona.top + alto * k, zona.right, zona.top + alto * (k + 1)), letra, legibleAnt(est, c(CINABRIO, 0.3f), ESTUCO), 10f, minimo = 6.5f)
        }
        // El cordel de henequén con su cuenta de jade: ceñido mientras está cerrado; al abrir, suelto en el petate.
        val henequen = c(CUERDA, 0.5f)
        val yc = tapa.top + tapa.height * 0.72f
        if (a < 0.12f) {
            listOf(-3f, 3f).forEach { o -> drawLine(henequen, Offset(tapa.left - 3 * u, yc + o * u), Offset(tapa.right + canto + 3 * u, yc + o * u), 2 * u) }
            drawLine(henequen, Offset(tapa.center.x, yc), Offset(tapa.center.x + 6 * u, yc + 22 * u), 1.6f * u)
            drawCircle(c(JADE, 0.6f), 5.5f * u, Offset(tapa.center.x + 6 * u, yc + 24 * u))
            drawCircle(Color.White.copy(alpha = 0.35f), 1.6f * u, Offset(tapa.center.x + 4.5f * u, yc + 22.5f * u))
        } else {
            val p0 = Offset(tapa.left + tapa.width * 0.3f, pie + 2 * u)
            drawPath(Path().apply { moveTo(p0.x, p0.y); cubicTo(p0.x + 30 * u, pie + 18 * u, p0.x + 50 * u, pie - 2 * u, p0.x + 80 * u, pie + 12 * u) }, henequen, style = Stroke(2 * u))
            drawCircle(c(JADE, 0.6f), 5.5f * u, Offset(p0.x + 80 * u, pie + 12 * u))
        }
        // Delante, a la izquierda: la jícara con cacao, la concha con cinabrio y los pinceles.
        val jx = w * 0.1f; val jy = h * 0.9f
        drawOval(Color.Black.copy(alpha = 0.25f), Offset(jx - 18 * u, jy + 2 * u), Size(38 * u, 7 * u))
        drawPath(Path().apply { moveTo(jx - 18 * u, jy - 8 * u); lineTo(jx + 18 * u, jy - 8 * u); quadraticTo(jx + 16 * u, jy + 6 * u, jx, jy + 6 * u); quadraticTo(jx - 16 * u, jy + 6 * u, jx - 18 * u, jy - 8 * u); close() }, c(BARRO_OSC, 0.4f))
        drawOval(c(BARRO, 0.4f), Offset(jx - 18 * u, jy - 11 * u), Size(36 * u, 7 * u))
        for (k in 0 until 7) drawOval(c(Color(0xFF5A3420), 0.4f), Offset(jx - 13 * u + k * 3.6f * u, jy - 10 * u + (k % 2) * 1.5f * u), Size(4.4f * u, 3 * u))
        drawLine(rojo, Offset(jx - 16 * u, jy - 3 * u), Offset(jx + 16 * u, jy - 3 * u), 1 * u)
        val cx = w * 0.26f; val cy = h * 0.93f
        drawPath(Path().apply { moveTo(cx - 12 * u, cy); quadraticTo(cx, cy - 14 * u, cx + 12 * u, cy); quadraticTo(cx, cy + 5 * u, cx - 12 * u, cy); close() }, c(Color(0xFFEDE3D0), 0.4f))
        drawOval(rojo, Offset(cx - 6 * u, cy - 5 * u), Size(12 * u, 5 * u))
        listOf(0f, 5f).forEach { o -> drawLine(c(VIGA, 0.4f), Offset(w * 0.3f + o * u, h * 0.975f), Offset(w * 0.42f + o * u, h * 0.9f), 1.8f * u, StrokeCap.Round); drawCircle(negro, 1.8f * u, Offset(w * 0.42f + o * u, h * 0.9f)) }
        // El brasero de copal, a la derecha: tres patas, el cuenco con su filete rojo, las brasas y el humo.
        val alto = h * 0.17f
        val boca = brasero.y - alto * 0.7f
        for (j in 0 until 3) {
            val x0h = brasero.x + (j - 1) * alto * 0.12f
            drawPath(Path().apply { moveTo(x0h, boca - 2 * u); cubicTo(x0h - alto * 0.5f, boca - alto * 0.9f, brasero.x + alto * 0.5f, boca - alto * 1.5f, brasero.x - alto * 0.1f + j * alto * 0.2f, boca - alto * (2.4f + 0.3f * j)) },
                c(COPAL, 0.7f).copy(alpha = 0.45f - 0.1f * j), style = Stroke((4 - j) * u, cap = StrokeCap.Round))
        }
        for (sg in listOf(-1f, 0f, 1f)) drawLine(c(BARRO_OSC, 0.9f), Offset(brasero.x + sg * alto * 0.25f, brasero.y), Offset(brasero.x + sg * alto * 0.16f, brasero.y - alto * 0.3f), 2.6f * u, StrokeCap.Round)
        drawPath(Path().apply { moveTo(brasero.x - alto * 0.45f, boca); lineTo(brasero.x + alto * 0.45f, boca); quadraticTo(brasero.x + alto * 0.4f, brasero.y - alto * 0.25f, brasero.x, brasero.y - alto * 0.25f)
            quadraticTo(brasero.x - alto * 0.4f, brasero.y - alto * 0.25f, brasero.x - alto * 0.45f, boca); close() }, c(BARRO, 0.9f))
        drawLine(c(CINABRIO, 0.9f), Offset(brasero.x - alto * 0.4f, boca + alto * 0.08f), Offset(brasero.x + alto * 0.4f, boca + alto * 0.08f), 1.4f * u)
        for (j in -1..1) drawCircle(c(NEGRO, 0.9f), 1.2f * u, Offset(brasero.x + j * alto * 0.16f, boca + alto * 0.2f))
        drawOval(c(BARRO_OSC, 0.9f), Offset(brasero.x - alto * 0.47f, boca - 3 * u), Size(alto * 0.94f, 6 * u))
        for (j in 0 until 6) drawCircle(if (j % 2 == 0) BRASA else Color(0xFFF4C76E), (1.4f + azar(s, 300 + j)) * u, Offset(brasero.x + (j - 2.5f) * alto * 0.12f, boca - 1 * u))
    }

    override fun DrawScope.pestana(e: EstadoPestana, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val s = e.semilla
        // Una página de estuco con su doble filete rojo; el color del ánimo, en una cuenta redonda (chalchihuite) al pie.
        val est = lc(if ((s and 1) == 0) ESTUCO else AMATE, oscuro, 0.3f)
        val r = Rect(w * 0.05f, h * 0.03f, w * 0.95f, h * 0.97f)
        drawRect(Color.Black.copy(alpha = 0.28f), Offset(r.left + 1.5f * u, r.top + 2 * u), r.size)
        drawRect(est, r.topLeft, r.size)
        val rojo = if (oscuro) mezcla(CINABRIO, Color.White, 0.3f) else CINABRIO
        drawRect(rojo, r.deflate(2.5f * u).topLeft, r.deflate(2.5f * u).size, style = Stroke(1.2f * u))
        drawRect(rojo.copy(alpha = 0.5f), r.deflate(5 * u).topLeft, r.deflate(5 * u).size, style = Stroke(0.6f * u))
        val tinta = if (oscuro) ESTUCO else NEGRO
        textoEn(medidor, e.dia, Rect(r.left + 4 * u, r.top + 5 * u, r.right - 4 * u, r.top + r.height * 0.55f), letra, tinta, 19f, peso = FontWeight.Bold, minimo = 9f)
        textoEn(medidor, e.semana, Rect(r.left + 4 * u, r.top + r.height * 0.52f, r.right - 4 * u, r.top + r.height * 0.7f), letra, rojo, 9f, minimo = 6f)
        val p = Offset(r.center.x, r.bottom - 10 * u)
        if (e.tono != null) {
            drawCircle(Color.Black.copy(alpha = 0.3f), 6.4f * u, p + Offset(0.8f * u, 1 * u))
            drawCircle(e.tono, 6 * u, p)
            drawCircle(est, 2 * u, p)
            drawCircle(Color.Black.copy(alpha = 0.3f), 6 * u, p, style = Stroke(0.7f * u))
        } else {
            for (j in -1..1) drawCircle(tinta.copy(alpha = 0.6f), 1.5f * u, Offset(p.x + j * 5 * u, p.y))
        }
    }

    override fun DrawScope.rotuloMes(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // Una tira de amate con el marco rojo y una hilera de puntos y barras a cada lado.
        val amate = lc(AMATE, oscuro, 0.35f)
        val r = Rect(2 * u, 3 * u, w - 3 * u, h - 3 * u)
        val forma = contornoIrregular(r, texto.hashCode(), 80, 0.03f)
        translate(2 * u, 2 * u) { drawPath(forma, Color.Black.copy(alpha = 0.28f)) }
        drawPath(forma, amate)
        clipPath(forma) { for (k in 0 until 30) drawLine(lc(CUERDA_OSC, oscuro, 0.35f).copy(alpha = 0.3f), Offset(w * azar(k, 1), 0f), Offset(w * azar(k, 2), h), 0.6f * u) }
        val rojo = legibleAnt(amate, lc(CINABRIO, oscuro, 0.2f), mezcla(CINABRIO, Color.White, 0.4f))
        val tinta = legibleAnt(amate, lc(NEGRO, oscuro, 0.2f), ESTUCO)
        drawRect(rojo, r.deflate(3 * u).topLeft, r.deflate(3 * u).size, style = Stroke(1.2f * u))
        listOf(r.left + 10 * u, r.right - 46 * u).forEach { x0 ->
            drawRoundRect(rojo, Offset(x0, h * 0.56f), Size(36 * u, 3.4f * u), CornerRadius(1.5f * u))
            for (j in 0 until 3) drawCircle(rojo, 2.2f * u, Offset(x0 + 8 * u + j * 10 * u, h * 0.38f))
        }
        textoEn(medidor, texto, Rect(r.left + 52 * u, 0f, r.right - 52 * u, h), letra, tinta, 15f, peso = FontWeight.Bold, minimo = 8f)
    }

    // ---------------------------------------------------------------- las cartas

    private val AMATE_CARTA = Color(0xFFD9C29A)
    private val AMATE_OSC = Color(0xFF9C8058)
    private val AMATE_FIBRA = Color(0xFF7A6040)
    private val HENEQUEN = Color(0xFFC9AE70)
    private val HENEQUEN_OSC = Color(0xFF7E6A3C)
    private val TEJIDO = Color(0xFFC4A266)
    private val TEJIDO_OSC = Color(0xFF8E6E3A)
    private val ORO_MESO = Color(0xFFF2C46A)

    /** El color de las cartas con su luz: de día, tal cual; en modo oscuro, hundido en la penumbra de la cámara. */
    private fun cc(x: Color, oscuro: Boolean): Color = if (oscuro) mezcla(x, Color(0xFF100C08), 0.55f) else x

    /** Las fibras del amate en [r]: la red de corteza machacada, hilos ondulados que se cruzan. */
    private fun DrawScope.fibrasAmate(r: Rect, color: Color, semilla: Int) {
        val u = density
        for (j in 0 until 14) {
            val y = r.top + r.height * azar(semilla, j)
            drawPath(Path().apply {
                moveTo(r.left, y)
                quadraticTo(r.left + r.width * 0.3f, y + (azar(semilla, j + 20) - 0.5f) * 10 * u, r.left + r.width * 0.6f, y + (azar(semilla, j + 40) - 0.5f) * 6 * u)
                quadraticTo(r.left + r.width * 0.85f, y + (azar(semilla, j + 60) - 0.5f) * 10 * u, r.right, y)
            }, color, style = Stroke(0.7f * u))
        }
    }

    /**
     * El sello de Mesoamérica, centrado en [c]: barro pintado con cinabrio, de borde escalonado, con un aro de estuco y, en
     * medio, el cartucho de un día: el cuadro redondeado con el signo y su número de barras y puntos encima.
     */
    private fun DrawScope.selloMeso(c: Offset, r: Float, oscuro: Boolean) {
        fun k(x: Color) = if (oscuro) mezcla(x, Color(0xFF100C08), 0.3f) else x
        val escalon = Path().apply {
            val n = 12
            for (j in 0 until n * 2) {
                val rr = if (j % 2 == 0) r else r * 0.88f
                val a0 = (j * Math.PI / n).toFloat(); val a1 = ((j + 1) * Math.PI / n).toFloat()
                if (j == 0) moveTo(c.x + rr * cos(a0), c.y + rr * sin(a0)) else lineTo(c.x + rr * cos(a0), c.y + rr * sin(a0))
                lineTo(c.x + rr * cos(a1), c.y + rr * sin(a1))
            }
            close()
        }
        translate(r * 0.07f, r * 0.12f) { drawPath(escalon, Color.Black.copy(alpha = 0.32f)) }
        drawPath(escalon, k(CINABRIO))
        drawPath(escalon, k(mezcla(CINABRIO, Color.Black, 0.35f)), style = Stroke(r * 0.05f))
        val estuco = k(ESTUCO)
        drawCircle(estuco, r * 0.7f, c, style = Stroke(r * 0.06f))
        val cuadro = Rect(c.x - r * 0.4f, c.y - r * 0.28f, c.x + r * 0.4f, c.y + r * 0.46f)
        drawRoundRect(estuco, cuadro.topLeft, cuadro.size, CornerRadius(r * 0.14f))
        drawRoundRect(k(NEGRO), cuadro.topLeft, cuadro.size, CornerRadius(r * 0.14f), style = Stroke(r * 0.05f))
        // El signo del día: una cara de ojos redondos y la boca en «T» (ik', el viento).
        drawCircle(k(NEGRO), r * 0.06f, Offset(c.x - r * 0.15f, c.y - r * 0.02f))
        drawCircle(k(NEGRO), r * 0.06f, Offset(c.x + r * 0.15f, c.y - r * 0.02f))
        drawLine(k(NEGRO), Offset(c.x - r * 0.16f, c.y + r * 0.16f), Offset(c.x + r * 0.16f, c.y + r * 0.16f), r * 0.07f)
        drawLine(k(NEGRO), Offset(c.x, c.y + r * 0.16f), Offset(c.x, c.y + r * 0.34f), r * 0.07f)
        // El numeral encima: dos puntos y una barra.
        drawCircle(estuco, r * 0.07f, Offset(c.x - r * 0.12f, c.y - r * 0.5f))
        drawCircle(estuco, r * 0.07f, Offset(c.x + r * 0.12f, c.y - r * 0.5f))
        drawRoundRect(estuco, Offset(c.x - r * 0.3f, c.y - r * 0.4f), Size(r * 0.6f, r * 0.08f), CornerRadius(r * 0.04f))
        drawCircle(Color.White.copy(alpha = 0.18f), r * 0.12f, c - Offset(r * 0.5f, r * 0.5f))
    }

    /** La solapa escalonada: baja desde los dos lados en escalones de pirámide hasta [puntaY], en el centro. */
    private fun solapaEscalonada(l: Float, r: Float, arriba: Float, puntaY: Float): Path = Path().apply {
        val cx = (l + r) / 2
        val pasos = 4
        moveTo(l, arriba)
        for (j in 1..pasos) {
            val y = arriba + (puntaY - arriba) * j / pasos
            val x = l + (cx - l) * (j - 1) / pasos * 1.05f
            lineTo(x, y); lineTo(l + (cx - l) * j / pasos, y)
        }
        for (j in pasos downTo 1) {
            val y = arriba + (puntaY - arriba) * j / pasos
            lineTo(r - (r - cx) * j / pasos, y); lineTo(r - (r - cx) * (j - 1) / pasos * 1.05f, y)
        }
        lineTo(r, arriba); close()
    }

    /**
     * La carta de Mesoamérica: un envoltorio de papel amate, con sus fibras de corteza, cerrado con una solapa escalonada como
     * el perfil de una pirámide, con la greca escalonada pintada en cinabrio; al frente, una etiqueta de estuco con filete rojo
     * con a quién va y las fechas. Las de al futuro se atan en cruz con henequén ([EstadoCarta.cordon]), con una cuenta de jade
     * colgando; en el cruce, el sello de barro con cinabrio y el cartucho del día. Al abrirse, el barro se parte, la solapa se
     * levanta y deja ver el envés del amate, y sube la hoja con su marco rojo de códice. Un degradado: el brillo del sello.
     */
    override fun DrawScope.sobre(e: EstadoCarta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val q = e.cuerpo
        val W = q.width; val H = q.height; val cx = q.center.x
        val ab = e.abrir.coerceIn(0f, 1f)
        val s = e.semilla
        fun c(x: Color) = cc(x, oscuro)
        val caja = Rect(q.left + W * 0.03f, q.top + H * 0.06f, q.right - W * 0.03f, q.bottom - H * 0.05f)
        drawRect(Color.Black.copy(alpha = 0.28f), caja.topLeft + Offset(2.5f * u, 4 * u), caja.size)
        drawRect(c(AMATE_OSC), caja.topLeft, caja.size)
        val boca = caja.top + H * 0.1f
        if (ab > 0f) drawRect(c(Color(0xFF261A0E)).copy(alpha = ab), Offset(caja.left + 3 * u, caja.top + 2 * u), Size(caja.width - 6 * u, boca - caja.top))
        val kk = 1f - 2f * ab
        val puntaCerrada = q.top + H * 0.46f
        val puntaY = caja.top + (puntaCerrada - caja.top) * kk
        val solapa = solapaEscalonada(caja.left, caja.right, caja.top, puntaY)
        if (kk < 0f) {
            drawPath(solapa, c(AMATE_OSC))
            clipPath(solapa) { fibrasAmate(Rect(caja.left, puntaY, caja.right, caja.top), c(AMATE_FIBRA).copy(alpha = 0.4f), s + 5) }
            drawPath(solapa, c(AMATE_FIBRA), style = Stroke(1 * u))
        }
        if (ab > 0.3f || e.subir > 0f) hojaCartaAnt(q, e.subir, e.hoja, e.renglones, oscuro, 0.12f, 0.88f, margen = c(CINABRIO).copy(alpha = 0.55f))
        // El frente del envoltorio.
        val frente = Rect(caja.left, boca, caja.right, caja.bottom)
        drawRect(c(AMATE_CARTA), frente.topLeft, frente.size)
        clipRect(frente.left, frente.top, frente.right, frente.bottom) {
            fibrasAmate(frente, c(AMATE_FIBRA).copy(alpha = 0.3f), s)
            grecaEscalonada(Rect(frente.left + 4 * u, frente.bottom - H * 0.09f, frente.right - 4 * u, frente.bottom - H * 0.025f), c(CINABRIO).copy(alpha = 0.75f), 1.1f)
        }
        drawRect(c(AMATE_FIBRA), frente.topLeft, frente.size, style = Stroke(1 * u))
        drawLine(c(AMATE_FIBRA), Offset(frente.left, boca), Offset(frente.right, boca), 1.3f * u)
        val rS = minOf(H * 0.14f, W * 0.1f)
        val cS = Offset(cx, puntaCerrada)
        // El henequén en cruz: la vuelta de arriba abajo (se esconde bajo la etiqueta) y la de lado a lado.
        val etiqueta = Rect(caja.left + W * 0.08f, q.top + H * 0.6f, caja.right - W * 0.08f, q.top + H * 0.85f)
        if (e.cordon > 0f) {
            val v = listOf(Offset(cx, caja.top - 2 * u), Offset(cx, caja.bottom + 2 * u))
            trazoParcialAnt(v.map { it + Offset(1.2f * u, 0f) }, (e.cordon / 0.5f).coerceIn(0f, 1f), Color.Black.copy(alpha = 0.4f), 3f * u)
            trazoParcialAnt(v, (e.cordon / 0.5f).coerceIn(0f, 1f), c(HENEQUEN), 2.2f * u)
        }
        // La etiqueta de estuco con su filete rojo, con a quién va y las fechas.
        val estuco = c(ESTUCO)
        drawRect(Color.Black.copy(alpha = 0.25f), etiqueta.topLeft + Offset(1.2f * u, 1.6f * u), etiqueta.size)
        drawRect(estuco, etiqueta.topLeft, etiqueta.size)
        drawRect(c(CINABRIO), Offset(etiqueta.left + 2 * u, etiqueta.top + 2 * u), Size(etiqueta.width - 4 * u, etiqueta.height - 4 * u), style = Stroke(1.2f * u))
        val tinta = legibleAnt(estuco, c(NEGRO), ESTUCO)
        if (e.linea1.isBlank() && e.linea2.isBlank()) numeroMaya(13, Rect(etiqueta.center.x - etiqueta.height * 0.7f, etiqueta.top + 4 * u, etiqueta.center.x + etiqueta.height * 0.7f, etiqueta.bottom - 4 * u), c(NEGRO), c(CINABRIO))
        val corte = etiqueta.top + etiqueta.height * 0.58f
        lineasCartaAnt(medidor, e.linea1, e.linea2, Rect(etiqueta.left + 5 * u, etiqueta.top + 2.5f * u, etiqueta.right - 5 * u, corte),
            Rect(etiqueta.left + 6 * u, corte - 1 * u, etiqueta.right - 6 * u, etiqueta.bottom - 3 * u), LetraExtra.codice, tinta, 12f, 9.5f)
        // La solapa cerrada (o levantándose): el amate con la greca escalonada en cinabrio y dos rosetas de puntos.
        if (kk >= 0f) {
            translate(0f, 2 * u) { drawPath(solapa, Color.Black.copy(alpha = 0.2f)) }
            drawPath(solapa, c(AMATE_CARTA))
            clipPath(solapa) {
                fibrasAmate(Rect(caja.left, caja.top, caja.right, puntaY), c(AMATE_FIBRA).copy(alpha = 0.35f), s + 9)
                val banda = minOf(H * 0.08f, 9 * u)
                if (puntaY - caja.top > banda * 2) grecaEscalonada(Rect(caja.left + 4 * u, caja.top + 3 * u, caja.right - 4 * u, caja.top + 3 * u + banda), c(CINABRIO).copy(alpha = 0.8f), 1.1f)
                if (ab < 0.25f) listOf(caja.left + W * 0.16f, caja.right - W * 0.16f).forEach { x ->
                    val o = Offset(x, caja.top + (puntaY - caja.top) * 0.36f + banda * 0.5f)
                    drawCircle(c(JADE), H * 0.03f, o)
                    for (j in 0 until 6) { val a = (j * Math.PI / 3).toFloat(); drawCircle(c(CINABRIO), H * 0.014f, Offset(o.x + H * 0.06f * cos(a), o.y + H * 0.06f * sin(a))) }
                }
            }
            drawPath(solapa, c(AMATE_FIBRA), style = Stroke(1 * u))
        }
        if (e.cordon > 0f) {
            // La vuelta de lado a lado, encima de la solapa, y la parte de arriba de la vuelta vertical.
            val y = cS.y
            trazoParcialAnt(listOf(Offset(cx, caja.top - 2 * u), Offset(cx, y)), (e.cordon / 0.5f).coerceIn(0f, 1f), c(HENEQUEN), 2.2f * u)
            val av = ((e.cordon - 0.5f) / 0.5f).coerceIn(0f, 1f)
            val h1 = listOf(Offset(caja.left - W * 0.012f, y + 2 * u), Offset(cx, y), Offset(caja.right + W * 0.012f, y - 2 * u))
            trazoParcialAnt(h1.map { it + Offset(0f, 1.2f * u) }, av, Color.Black.copy(alpha = 0.4f), 3f * u)
            trazoParcialAnt(h1, av, c(HENEQUEN), 2.2f * u)
            trazoParcialAnt(h1, av, c(HENEQUEN_OSC), 0.6f * u)
            if (e.cordon > 0.95f) {
                // La cuenta de jade, colgando de un cabo del nudo.
                drawLine(c(HENEQUEN), Offset(cx - rS * 1.6f, y), Offset(cx - rS * 1.85f, y + rS * 0.32f), 1.4f * u)
                drawCircle(c(JADE), rS * 0.22f, Offset(cx - rS * 1.9f, y + rS * 0.5f))
                drawCircle(Color.White.copy(alpha = 0.3f), rS * 0.07f, Offset(cx - rS * 1.96f, y + rS * 0.44f))
            }
        }
        selloCartaAnt(cS, rS, e.brillo, e.romper.coerceIn(0f, 1f), e.escalaSello, e.alfaSello, ORO_MESO) { selloMeso(cS, rS, oscuro) }
    }

    override val cajaPrevia: Rect = Rect(0.17f, 0.175f, 0.83f, 0.345f)

    override val altoBuzon: Dp = 210.dp

    /** Un envoltorio de amate chico, de pie en el chiquihuite, con el centro de su canto de abajo en [o]: listo, atado o abierto. */
    private fun DrawScope.envoltorioChico(o: Offset, w: Float, h: Float, giro: Float, estado: Int, oscuro: Boolean, semilla: Int) {
        val u = density
        rotate(giro, o) {
            val r = Rect(o.x - w / 2, o.y - h, o.x + w / 2, o.y)
            if (estado == 2) {
                val hoja = Rect(r.left + w * 0.12f, r.top - h * 0.4f, r.right - w * 0.12f, r.top + h * 0.4f)
                drawRect(cc(ESTUCO, oscuro), hoja.topLeft, hoja.size)
                drawRect(cc(CINABRIO, oscuro), Offset(hoja.left + 1.5f * u, hoja.top + 1.5f * u), Size(hoja.width - 3 * u, hoja.height - 3 * u), style = Stroke(0.8f * u))
                numeroMaya(3 + semilla % 9, Rect(hoja.left + 3 * u, hoja.top + 3 * u, hoja.right - 3 * u, hoja.top + hoja.height * 0.5f), cc(NEGRO, oscuro), cc(CINABRIO, oscuro))
            }
            drawRect(Color.Black.copy(alpha = 0.25f), r.topLeft + Offset(1.2f * u, 1.2f * u), r.size)
            drawRect(cc(AMATE_CARTA, oscuro), r.topLeft, r.size)
            drawRect(cc(AMATE_FIBRA, oscuro), r.topLeft, r.size, style = Stroke(0.6f * u))
            if (estado != 2) {
                drawPath(solapaEscalonada(r.left, r.right, r.top, r.top + h * 0.45f), cc(mezcla(AMATE_CARTA, Color.White, 0.15f), oscuro))
                drawPath(solapaEscalonada(r.left, r.right, r.top, r.top + h * 0.45f), cc(AMATE_FIBRA, oscuro), style = Stroke(0.6f * u))
                if (estado == 1) {
                    drawLine(cc(HENEQUEN, oscuro), Offset(o.x, r.top - 1 * u), Offset(o.x, r.bottom), 1.2f * u)
                    drawLine(cc(HENEQUEN, oscuro), Offset(r.left - 1 * u, r.top + h * 0.45f), Offset(r.right + 1 * u, r.top + h * 0.45f), 1.2f * u)
                }
                drawCircle(cc(CINABRIO, oscuro), w * 0.17f, Offset(o.x, r.top + h * 0.45f))
                drawCircle(cc(ESTUCO, oscuro), w * 0.08f, Offset(o.x, r.top + h * 0.45f), style = Stroke(0.7f * u))
            }
        }
    }

    /**
     * El buzón de Mesoamérica: la plataforma de tres gradas estucadas al pie del muro de sillares, con la banda roja de la
     * greca escalonada arriba. En la grada alta, tres chiquihuites de palma tejida en petatillo con los envoltorios de amate:
     * a la izquierda los que ya se pueden abrir, con el sello brillando; en medio los atados en cruz con henequén; a la derecha
     * los abiertos, con su hoja de códice afuera. En el peralte de la grada, bajo cada canasto, una tablilla de estuco con el
     * nombre y la cuenta en cifras y en barras y puntos. En modo oscuro la cámara se apaga. Un degradado: el brillo de las listas.
     */
    override fun DrawScope.buzon(e: EstadoBuzon, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = cc(x, oscuro)
        // El muro de sillares y la banda roja con la greca.
        drawRect(c(CALIZA))
        var y = h * 0.16f; var fila = 0
        while (y < h * 0.62f) {
            drawLine(c(CALIZA_OSC), Offset(0f, y), Offset(w, y), 1 * u)
            var x = if (fila % 2 == 0) 0f else 22 * u
            while (x < w) { drawLine(c(CALIZA_OSC), Offset(x, y), Offset(x, y + 18 * u), 1 * u); x += 44 * u }
            y += 18 * u; fila++
        }
        drawRect(c(CINABRIO), Offset(0f, 0f), Size(w, h * 0.15f))
        grecaEscalonada(Rect(4 * u, h * 0.03f, w - 4 * u, h * 0.12f), c(ESTUCO), 1.4f)
        drawRect(c(mezcla(CINABRIO, Color.Black, 0.3f)), Offset(0f, h * 0.15f), Size(w, 2 * u))
        // Las tres gradas.
        val gradas = listOf(h * 0.62f, h * 0.8f, h * 0.92f)
        gradas.forEachIndexed { i, gy ->
            drawRect(c(mezcla(ESTUCO, CINABRIO, 0.12f + 0.06f * i)), Offset(0f, gy), Size(w, h - gy))
            drawRect(c(ESTUCO), Offset(0f, gy), Size(w, 3 * u))
            drawRect(Color.Black.copy(alpha = 0.18f), Offset(0f, gy + 3 * u), Size(w, 2 * u))
        }
        val zonas = listOf(w * 0.18f, w * 0.5f, w * 0.82f)
        val cuentas = listOf(e.listas, e.selladas, e.abiertas)
        val nombres = listOf("para abrir", "atadas", "abiertas")
        val anchoC = minOf(w * 0.27f, 96 * u)
        val boca = h * 0.46f
        for (g in 0 until 3) {
            val cx = zonas[g]
            val n = minOf(cuentas[g], 3)
            if (g == 0 && n > 0) mancha(Offset(cx, boca - 8 * u), anchoC * 0.8f, ORO_MESO.copy(alpha = if (oscuro) 0.6f else 0.5f))
            for (i in 0 until n) {
                val dx = (i - (n - 1) / 2f) * 20 * u
                envoltorioChico(Offset(cx + dx, boca + 12 * u), 22 * u, 40 * u, dx / u * 0.6f, g, oscuro, 11 * g + i)
            }
            if (g == 0 && n > 0) for (i in 0 until n) drawPath(estrella(Offset(cx + (i - (n - 1) / 2f) * 20 * u + 8 * u, boca - 28 * u), 3.4f * u, 4, 0.25f, 0f), ORO_MESO)
            // El chiquihuite: bajo y ancho, tejido en petatillo.
            val canasto = Path().apply {
                moveTo(cx - anchoC / 2, boca)
                lineTo(cx + anchoC / 2, boca)
                cubicTo(cx + anchoC * 0.5f, boca + (gradas[0] - boca) * 0.7f, cx + anchoC * 0.38f, gradas[0], cx + anchoC * 0.3f, gradas[0])
                lineTo(cx - anchoC * 0.3f, gradas[0])
                cubicTo(cx - anchoC * 0.38f, gradas[0], cx - anchoC * 0.5f, boca + (gradas[0] - boca) * 0.7f, cx - anchoC / 2, boca)
                close()
            }
            translate(2.5f * u, 2.5f * u) { drawPath(canasto, Color.Black.copy(alpha = 0.25f)) }
            drawPath(canasto, c(TEJIDO))
            clipPath(canasto) {
                val paso = 5 * u
                var yy = boca; var f = 0
                while (yy < gradas[0]) {
                    var xx = cx - anchoC / 2 + (if (f % 2 == 0) 0f else paso)
                    while (xx < cx + anchoC / 2) { drawRect(c(TEJIDO_OSC), Offset(xx, yy), Size(paso, paso)); xx += paso * 2 }
                    yy += paso; f++
                }
                drawRect(Color.Black.copy(alpha = 0.16f), Offset(cx + anchoC * 0.2f, boca), Size(anchoC, h))
            }
            drawRoundRect(c(TEJIDO_OSC), Offset(cx - anchoC / 2 - 2 * u, boca - 3 * u), Size(anchoC + 4 * u, 5 * u), CornerRadius(2.5f * u))
            // La tablilla de estuco en el peralte de la grada.
            val tab = Rect(cx - anchoC * 0.48f, gradas[0] + 6 * u, cx + anchoC * 0.48f, gradas[1] + (gradas[2] - gradas[1]) * 0.75f)
            drawRect(Color.Black.copy(alpha = 0.25f), tab.topLeft + Offset(1.5f * u, 1.5f * u), tab.size)
            drawRect(c(ESTUCO), tab.topLeft, tab.size)
            drawRect(c(CINABRIO), Offset(tab.left + 2 * u, tab.top + 2 * u), Size(tab.width - 4 * u, tab.height - 4 * u), style = Stroke(1.2f * u))
            val tinta = legibleAnt(c(ESTUCO), c(NEGRO), ESTUCO)
            textoEn(medidor, nombres[g], Rect(tab.left + 4 * u, tab.top + 3 * u, tab.right - 4 * u, tab.top + tab.height * 0.42f), LetraExtra.codice, tinta, 10f, minimo = 6f)
            val mitad = tab.left + tab.width * 0.52f
            textoEn(medidor, cuentas[g].toString(), Rect(tab.left + 4 * u, tab.top + tab.height * 0.4f, mitad, tab.bottom - 3 * u), LetraExtra.codice, tinta, 16f, peso = FontWeight.Bold, minimo = 7f)
            numeroMaya(cuentas[g].coerceAtMost(399), Rect(mitad + 2 * u, tab.top + tab.height * 0.42f, tab.right - 6 * u, tab.bottom - 4 * u), tinta, c(CINABRIO))
        }
    }
}


// =====================================================================
//  Ayudantes de las tapas de la Antigüedad
// =====================================================================

/** De dos tintas, la que mejor se lee sobre [fondo] (la de más contraste). */
private fun legibleAnt(fondo: Color, oscura: Color, clara: Color): Color {
    val f = fondo.luminance()
    val conOscura = (maxOf(f, oscura.luminance()) + 0.05f) / (minOf(f, oscura.luminance()) + 0.05f)
    val conClara = (maxOf(f, clara.luminance()) + 0.05f) / (minOf(f, clara.luminance()) + 0.05f)
    return if (conOscura >= conClara) oscura else clara
}

/** La fecha en uno o dos renglones: si pasa de [largo] letras, se parte por el « · » más cercano a la mitad. */
private fun renglonesFechaAnt(f: String, largo: Int): List<String> {
    if (f.length <= largo) return listOf(f)
    val cortes = Regex(" · ").findAll(f).map { it.range.first }.toList()
    if (cortes.isEmpty()) return listOf(f)
    val k = cortes.minBy { kotlin.math.abs(it - f.length / 2) }
    return listOf(f.substring(0, k), f.substring(k + 3))
}

/** «1 palabra», «N palabras». */
private fun palabrasAnt(n: Int): String = if (n == 1) "1 palabra" else "$n palabras"

// =====================================================================
//  Ayudantes de la página abierta de la Antigüedad
// =====================================================================

/**
 * Un color del tema puesto sobre el papel [p]: tal cual de día; en modo oscuro (papel claro), hundido como el papel en su
 * penumbra; sobre un papel oscuro (el de un sueño), casi fundido con él para que la tinta clara siga leyéndose.
 */
private fun tonoAnt(c: Color, p: Pagina, oscuro: Boolean): Color = when {
    p.oscura -> mezcla(c, p.color, 0.6f)
    oscuro -> mezcla(c, Color(0xFF0E0A07), 0.24f)
    else -> c
}

/**
 * La fecha del encabezado en [r]: en un renglón si cabe en [largo] letras; si no, partida por el « · » más cercano a la mitad,
 * el primer renglón grande y el segundo menor (o, si no tiene « · », en dos renglones parejos partidos por un espacio).
 */
private fun DrawScope.fechaEncAnt(medidor: TextMeasurer, f: String, r: Rect, letra: FontFamily, color: Color, tam: Float, largo: Int,
                                  peso: FontWeight = FontWeight.Normal) {
    val renglones = renglonesFechaAnt(f, largo)
    val espacio = f.indices.filter { f[it] == ' ' }.minByOrNull { kotlin.math.abs(it - f.length / 2) }
    if (renglones.size == 1 && f.length > largo + 4 && espacio != null) {
        // Larga y sin « · »: dos renglones parejos, partidos por el espacio más cercano a la mitad.
        val corte = r.top + r.height * 0.5f
        textoEn(medidor, f.substring(0, espacio), Rect(r.left, r.top, r.right, corte), letra, color, tam * 0.78f, peso = peso, minimo = 9f)
        textoEn(medidor, f.substring(espacio + 1), Rect(r.left, corte, r.right, r.bottom), letra, color, tam * 0.78f, peso = peso, minimo = 9f)
    } else if (renglones.size == 1) {
        textoEn(medidor, renglones[0], r, letra, color, tam, peso = peso, minimo = 9f)
    } else {
        val corte = r.top + r.height * 0.6f
        textoEn(medidor, renglones[0], Rect(r.left, r.top, r.right, corte), letra, color, tam * 0.84f, peso = peso, minimo = 9f)
        textoEn(medidor, renglones[1], Rect(r.left, corte, r.right, r.bottom), letra, color.copy(alpha = color.alpha * 0.85f), tam * 0.6f, minimo = 8f)
    }
}

/**
 * La penumbra del modo oscuro sobre un papel claro: un velo parejo del 12 %, las orillas que se hunden (una elipse que deja
 * claro el centro, donde va el texto, y llega a cerca del 60 % en las esquinas) y la luz cálida que llega desde [foco] (la
 * lucerna, la vela, el fuego) con fuerza [fuerza]. Dos degradados.
 */
private fun DrawScope.penumbraAnt(foco: Offset, luz: Color, fuerza: Float) {
    val w = size.width; val h = size.height
    drawRect(Color.Black.copy(alpha = 0.12f))
    val centro = Offset(w * 0.5f + (foco.x - w * 0.5f) * 0.12f, h * 0.45f + (foco.y - h * 0.45f) * 0.12f)
    clipRect {
        withTransform({ scale(1f, h / w, centro) }) {
            drawRect(Brush.radialGradient(0f to Color.Transparent, 0.42f to Color.Black.copy(alpha = 0.06f), 0.72f to Color.Black.copy(alpha = 0.3f),
                1f to Color.Black.copy(alpha = 0.55f), center = centro, radius = w * 0.72f), Offset(-w, centro.y - h), Size(3 * w, 2 * h))
        }
    }
    drawRect(Brush.radialGradient(0f to luz.copy(alpha = fuerza), 0.4f to luz.copy(alpha = fuerza * 0.4f), 1f to Color.Transparent,
        center = foco, radius = maxOf(w, h) * 0.62f))
}


// =====================================================================
//  Ayudantes de las cartas de la Antigüedad
// =====================================================================

/**
 * La hoja de la carta que sale de su objeto, del papel [hoja] con sus [renglones]: va de [izq] a [der] (fracciones del ancho
 * de [cuerpo]) y sube con [subir] hasta el 72 % del alto del cuerpo (a 0.5, la de la rejilla, asoma por encima del cuerpo);
 * la tapa el frente que se dibuja después. [borde] (una semilla) le da un contorno irregular (piel raspada, pergamino);
 * [fibras], el cruce de las fibras del papiro; [margen], un filete a la izquierda. En modo oscuro se apaga solo un poco:
 * encima van las primeras palabras, con la tinta del tema. Devuelve dónde quedó la hoja.
 */
private fun DrawScope.hojaCartaAnt(cuerpo: Rect, subir: Float, hoja: Color, renglones: Color, oscuro: Boolean, izq: Float = 0.12f, der: Float = 0.88f,
                                   borde: Int? = null, fibras: Boolean = false, margen: Color? = null): Rect {
    val u = density
    val H = cuerpo.height; val W = cuerpo.width
    val arriba = cuerpo.top + H * 0.07f - H * 0.72f * subir.coerceIn(0f, 1f)
    val r = Rect(cuerpo.left + W * izq, arriba, cuerpo.left + W * der, arriba + H * 0.84f)
    val papel = if (oscuro) mezcla(hoja, Color.Black, 0.1f) else hoja
    val forma = if (borde != null) contornoIrregular(r, borde, 34, 0.012f) else Path().apply { addRect(r) }
    translate(1.5f * u, 2f * u) { drawPath(forma, Color.Black.copy(alpha = 0.24f)) }
    drawPath(forma, papel)
    clipPath(forma) {
        if (fibras) {
            var y = r.top + 2 * u
            while (y < r.bottom) { drawLine(Color(0xFF8A6E3A).copy(alpha = 0.1f), Offset(r.left, y), Offset(r.right, y), 0.7f * u); y += 2.6f * u }
            var x = r.left + 3 * u
            while (x < r.right) { drawLine(Color(0xFF8A6E3A).copy(alpha = 0.06f), Offset(x, r.top), Offset(x, r.bottom), 0.7f * u); x += 3.4f * u }
        }
        margen?.let { drawLine(it, Offset(r.left + r.width * 0.06f, r.top), Offset(r.left + r.width * 0.06f, r.bottom), 0.9f * u) }
        val paso = maxOf(H * 0.075f, 6 * u)
        var y = r.top + H * 0.1f
        var i = 0
        while (y < r.bottom - 2 * u && i < 12) {
            drawLine(renglones, Offset(r.left + r.width * 0.1f, y), Offset(r.right - r.width * (if (i % 4 == 3) 0.35f else 0.08f), y), 1.1f * u, StrokeCap.Round)
            y += paso; i++
        }
    }
    return r
}

/** Un disco de contorno irregular (arcilla aplastada, cera, barro), de [n] puntos, cada uno hundido hasta [hondo] del radio. */
private fun discoAnt(c: Offset, r: Float, semilla: Int, n: Int = 20, hondo: Float = 0.08f): Path = Path().apply {
    for (k in 0 until n) {
        val a = (2 * Math.PI * k / n).toFloat()
        val rr = r * (1f - hondo * azar(semilla, k))
        val p = Offset(c.x + rr * cos(a), c.y + rr * sin(a))
        if (k == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
    }
    close()
}

/**
 * El sello de una carta en [c] (radio [r]). Con [brillo], el resplandor de la que ya se puede abrir (un degradado, del color
 * [luz]) y cuatro destellos; con [romper], se parte por una grieta en zigzag y las dos mitades se apartan y giran; [escala]
 * y [alfa] son el sello que cae al sellar (grande y transparente, hasta posarse). [dibujar] pinta el sello entero, centrado
 * en [c].
 */
private fun DrawScope.selloCartaAnt(c: Offset, r: Float, brillo: Float, romper: Float, escala: Float, alfa: Float, luz: Color,
                                    dibujar: DrawScope.() -> Unit) {
    if (alfa <= 0.01f) return
    if (brillo > 0f) {
        drawCircle(Brush.radialGradient(listOf(luz.copy(alpha = 0.8f * brillo), luz.copy(alpha = 0.3f * brillo), Color.Transparent), c, r * 3f), r * 3f, c)
        for (j in 0 until 4) {
            val a = (j * Math.PI / 2 + Math.PI / 4).toFloat()
            val p = Offset(c.x + r * 1.55f * cos(a), c.y + r * 1.4f * sin(a))
            drawPath(estrella(p, r * (0.2f + 0.18f * brillo), 4, 0.25f, giro = 0f), luz.copy(alpha = brillo))
        }
    }
    val pintar: DrawScope.() -> Unit = {
        if (romper <= 0f) withTransform({ scale(escala, escala, c) }) { dibujar() }
        else {
            val d = r * 0.42f * romper
            for (lado in listOf(-1f, 1f)) {
                val mitad = Path().apply {
                    moveTo(c.x + lado * r * 3f, c.y - r * 3f); lineTo(c.x, c.y - r * 3f)
                    for (j in 0..6) lineTo(c.x + (if (j % 2 == 0) -1f else 1f) * r * 0.16f, c.y - r * 1.2f + j * r * 0.4f)
                    lineTo(c.x, c.y + r * 3f); lineTo(c.x + lado * r * 3f, c.y + r * 3f); close()
                }
                withTransform({ translate(lado * d, r * 0.12f * romper); rotate(lado * 18f * romper, c) }) { clipPath(mitad) { dibujar() } }
            }
        }
    }
    if (alfa < 0.999f) {
        val m = r * 4f * maxOf(escala, 1f)
        drawContext.canvas.saveLayer(Rect(c.x - m, c.y - m, c.x + m, c.y + m), Paint().apply { this.alpha = alfa })
        pintar()
        drawContext.canvas.restore()
    } else pintar()
}

/** Un trazo de [puntos] que se dibuja solo hasta [avance] (0 a 1) de su largo: el cordón que se va atando. */
private fun DrawScope.trazoParcialAnt(puntos: List<Offset>, avance: Float, color: Color, grosor: Float) {
    if (avance <= 0f || puntos.size < 2) return
    val largos = puntos.zipWithNext { a, b -> (b - a).getDistance() }
    var queda = largos.sum() * avance.coerceIn(0f, 1f)
    val p = Path().apply { moveTo(puntos[0].x, puntos[0].y) }
    for (i in largos.indices) {
        if (queda <= 0f) break
        val f = minOf(1f, queda / largos[i].coerceAtLeast(0.01f))
        val q = puntos[i] + (puntos[i + 1] - puntos[i]) * f
        p.lineTo(q.x, q.y); queda -= largos[i]
    }
    drawPath(p, color, style = Stroke(grosor, cap = StrokeCap.Round, join = androidx.compose.ui.graphics.StrokeJoin.Round))
}

/** Los dos renglones del sobre: [l1] (a quién va) más grande en [r1] y [l2] (las fechas) en [r2], si no vienen vacíos. */
private fun DrawScope.lineasCartaAnt(medidor: TextMeasurer, l1: String, l2: String, r1: Rect, r2: Rect, letra: FontFamily, tinta: Color,
                                     tam1: Float = 12f, tam2: Float = 9.5f, peso1: FontWeight = FontWeight.Bold) {
    textoEn(medidor, l1, r1, letra, tinta, tam1, peso = peso1, minimo = 7f)
    textoEn(medidor, l2, r2, letra, tinta.copy(alpha = tinta.alpha * 0.9f), tam2, minimo = 7f)
}

/** Un número en números romanos (para las cuentas del buzón de Roma); el cero, «nulla». */
private fun romanoAnt(n: Int): String {
    if (n <= 0) return "nulla"
    if (n >= 4000) return n.toString()
    val v = listOf(1000 to "M", 900 to "CM", 500 to "D", 400 to "CD", 100 to "C", 90 to "XC", 50 to "L", 40 to "XL", 10 to "X", 9 to "IX", 5 to "V", 4 to "IV", 1 to "I")
    var x = n
    val s = StringBuilder()
    for ((k, l) in v) while (x >= k) { s.append(l); x -= k }
    return s.toString()
}
