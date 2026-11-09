package io.github.ruddy2003.vestigium.ui.vida

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
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
import androidx.compose.ui.graphics.lerp
import kotlin.math.cos
import kotlin.math.sin

/**
 * Ciudad neón: los niveles bajos de una megaciudad, siempre de noche, con la misma casa que su puesto de fideos del Ideario
 * y su máquina expendedora de la biblioteca: ladrillo y acero oscuros y mojados, madera de puesto, lona carmesí, charcos que
 * reflejan, y la luz de los letreros (el magenta del acento, el ámbar del secundario, el verde de sodio y el cian), cada
 * tubo con su halo y su alma blanca. Cada nicho es una superficie oscura del tono de la superficie del esquema.
 * - Social: el puesto de fideos: la viga con su filo de neón, la cortina noren con el emblema del tazón, los postes de madera
 *   con sus abrazaderas, dos farolillos de papel, letreros verticales de neón en la calle; la pared de azulejos del fondo
 *   (el nicho) y la barra de acero con dos tazones humeantes, platos y palillos, su filo magenta, el frente de tablillas,
 *   los taburetes y el charco.
 * - Mapas: la mesa del callejón: el marco de acero remachado con las escuadras cian y el vidrio oscuro con su rejilla
 *   (el nicho), el canto con franjas de peligro y su luz de bajo, las patas, el proyector holográfico; alrededor, el muro de
 *   ladrillo con la tubería y el medidor, los cables y la lámpara ámbar que cuelga.
 * - Otras vidas: la máquina expendedora: la marquesina con sus focos y cuatro íconos de neón, los tubos magenta a los lados
 *   del cristal, cinco compartimentos iguales (el nicho) con su luz y su repisa, la trampilla y el panel de pago.
 * - Íconos: el torii entre las torres con sus ventanas encendidas y su reflejo; la tarjeta holográfica con chip que se
 *   rasca con una moneda; tres chinchetas de neón sobre la rejilla del suelo mojado.
 * - Notas: el tablón del callejón bajo la lámpara de sodio, en su marco de acero con un tubo magenta y calcomanías; cada
 *   nota es un ticket térmico con el borde dentado, la franja impresa del tipo y una cinta (o, si está fijada, una
 *   chincheta de neón y un holograma); el recordatorio lleva su código de barras.
 */
internal object VidaNeon : DisenoVida {
    private val NOCHE = Color(0xFF120C17)
    private val NOCHE_OSC = Color(0xFF09060D)
    private val ACERO = Color(0xFF2C2733)
    private val ACERO_LUZ = Color(0xFF4A4454)
    private val ACERO_BRILLO = Color(0xFF7A7286)
    private val ACERO_OSC = Color(0xFF19151F)
    private val CROMO = Color(0xFFB9B4C2)
    private val CARCASA = Color(0xFF261D33)
    private val LADRILLO = listOf(Color(0xFF3A2230), Color(0xFF33202C), Color(0xFF43283A), Color(0xFF2E1C29))
    private val MORTERO = Color(0xFF17101A)
    private val MADERA = Color(0xFF3E2620)
    private val MADERA_LUZ = Color(0xFF5C3A2C)
    private val MADERA_OSC = Color(0xFF261611)
    private val LONA = Color(0xFF7A1E3A)
    private val LONA_LUZ = Color(0xFF983050)
    private val LONA_OSC = Color(0xFF4E1226)
    private val FAROL = Color(0xFFE0443A)
    private val SODIO = Color(0xFF8CFF5A)
    private val CIAN = Color(0xFF3DE0FF)
    private val LLUVIA = Color(0xFFB4CCFF)
    private val BERMELLON = Color(0xFFC23A2A)
    private val BERMELLON_OSC = Color(0xFF7A2216)
    private val LACA = Color(0xFF1E0B0E)
    private val LACA_ROJA = Color(0xFF8E2626)
    private val CALDO = Color(0xFFB87A2E)
    /** Los fondos de los nichos: casi la superficie del esquema. */
    private val AZULEJO = Color(0xFF1D1424)
    private val AZULEJO_JUNTA = Color(0xFF130C18)
    private val VIDRIO = Color(0xFF160F1E)
    private val INTERIOR = Color(0xFF181021)
    private val TABLON = Color(0xFF1E1627)
    /** El papel térmico de los tickets y la tinta con que se escribe en ellos. */
    private val TICKET = Color(0xFFF2EEE6)
    private val TICKET_SOMBRA = Color(0xFFD9D1C6)
    private val TINTA = Color(0xFF2A1A2E)

    override fun area(m: MuebleVida): Rect = when (m) {
        MuebleVida.SOCIAL -> Rect(0.13f, 0.2f, 0.87f, 0.73f)
        MuebleVida.MAPAS -> Rect(0.1f, 0.155f, 0.9f, 0.735f)
        MuebleVida.OTRAS_VIDAS -> Rect(0.07f, 0.275f, 0.93f, 0.855f)
        MuebleVida.NOTAS -> Rect(0.075f, 0.13f, 0.925f, 0.87f)
    }

    // ---------------------------------------------------------------- ayudas

    private fun mez(a: Color, b: Color, f: Float): Color = lerp(a, b, f.coerceIn(0f, 1f))

    /** Un número estable de 0 a 1 (las irregularidades de cada cosa, siempre las mismas). */
    private fun azar(semilla: Int, k: Int): Float {
        var x = semilla * 374761393 + k * 668265263
        x = (x xor (x ushr 13)) * 1274126177
        x = x xor (x ushr 16)
        return (x and 0x7fffffff) / 2147483647f
    }

    /** El nicho de [m] en píxeles del lienzo. */
    private fun DrawScope.nicho(m: MuebleVida): Rect {
        val a = area(m)
        return Rect(size.width * a.left, size.height * a.top, size.width * a.right, size.height * a.bottom)
    }

    /** Una mancha radial suave (el resplandor de una luz, el vapor). */
    private fun DrawScope.mancha(c: Offset, r: Float, color: Color) {
        val rr = r.coerceAtLeast(1f)
        drawCircle(Brush.radialGradient(listOf(color, color.copy(alpha = 0f)), c, rr), rr, c)
    }

    /** Un tubo de neón: el halo ancho y tenue, el tubo del color y su alma blanca. */
    private fun DrawScope.neon(p: Path, color: Color, grosor: Float) {
        drawPath(p, color.copy(alpha = 0.16f), style = Stroke(grosor * 4.5f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawPath(p, color.copy(alpha = 0.38f), style = Stroke(grosor * 2.2f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawPath(p, color, style = Stroke(grosor, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawPath(p, Color.White.copy(alpha = 0.7f), style = Stroke(grosor * 0.38f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }

    private fun linea(a: Offset, b: Offset) = Path().apply { moveTo(a.x, a.y); lineTo(b.x, b.y) }

    /** Una pieza con volumen: la sombra abajo y a la derecha, la luz arriba y a la izquierda, y la cara. */
    private fun DrawScope.pieza(r: Rect, cara: Color, luz: Color, sombra: Color, g: Float, radio: Float = 0f) {
        val cr = CornerRadius(radio)
        drawRoundRect(sombra, r.topLeft, r.size, cr)
        drawRoundRect(luz, r.topLeft, Size(r.width - g, r.height - g), cr)
        drawRoundRect(cara, Offset(r.left + g, r.top + g), Size((r.width - 2 * g).coerceAtLeast(0f), (r.height - 2 * g).coerceAtLeast(0f)), CornerRadius((radio - g).coerceAtLeast(0f)))
    }

    /** Un remache: la cabeza de acero con su brillo y su sombra. */
    private fun DrawScope.remache(p: Offset, r: Float) {
        drawCircle(NOCHE_OSC.copy(alpha = 0.7f), r * 1.15f, Offset(p.x + r * 0.3f, p.y + r * 0.35f))
        drawCircle(Brush.radialGradient(listOf(CROMO, ACERO_BRILLO, ACERO), Offset(p.x - r * 0.35f, p.y - r * 0.35f), r * 1.6f), r, p)
    }

    /** Un muro de ladrillo oscuro: el mortero y las piezas en hiladas trabadas, cada una con su tono y su filo de luz. */
    private fun DrawScope.ladrillos(r: Rect, u: Float, semilla: Int) {
        clipRect(r.left, r.top, r.right, r.bottom) {
            drawRect(MORTERO, r.topLeft, r.size)
            val bh = 3.6f * u; val bw = 10f * u; val j = 0.45f * u
            var y = r.top; var fila = 0
            while (y < r.bottom && fila < 60) {
                var x = r.left - (if (fila % 2 == 1) bw / 2 else 0f); var k = 0
                while (x < r.right && k < 80) {
                    val c = LADRILLO[(azar(semilla + fila, k) * LADRILLO.size).toInt().coerceIn(0, LADRILLO.size - 1)]
                    drawRect(c, Offset(x + j, y + j), Size(bw - 2 * j, bh - 2 * j))
                    drawLine(mez(c, Color.White, 0.08f), Offset(x + j, y + j), Offset(x + bw - j, y + j), 0.3f * u)
                    x += bw; k++
                }
                y += bh; fila++
            }
        }
    }

    /** La lluvia: rayas finas inclinadas dentro de [r]. */
    private fun DrawScope.lluvia(r: Rect, u: Float, semilla: Int, n: Int) {
        for (i in 0 until n) {
            val x = r.left + r.width * azar(semilla, i); val y = r.top + r.height * azar(semilla + 1, i)
            val l = (2.5f + 3f * azar(semilla + 2, i)) * u
            drawLine(LLUVIA.copy(alpha = 0.18f + 0.15f * azar(semilla + 3, i)), Offset(x, y), Offset(x - l * 0.25f, y + l), 0.25f * u)
        }
    }

    /** Un charco: el agua oscura con el reflejo alargado de un color. */
    private fun DrawScope.charco(c: Offset, rx: Float, ry: Float, color: Color) {
        drawOval(NOCHE_OSC, Offset(c.x - rx, c.y - ry), Size(rx * 2, ry * 2))
        drawOval(Brush.horizontalGradient(listOf(color.copy(alpha = 0f), color.copy(alpha = 0.35f), color.copy(alpha = 0f)), c.x - rx, c.x + rx), Offset(c.x - rx * 0.8f, c.y - ry * 0.35f), Size(rx * 1.6f, ry * 0.7f))
    }

    // ---------------------------------------------------------------- Social: el puesto de fideos

    override fun DrawScope.muebleSocial(e: EstadoMueble, oscuro: Boolean) {
        if (size.minDimension < 1f) return
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val n = nicho(MuebleVida.SOCIAL)
        drawRect(NOCHE)
        // La calle a los lados: el ladrillo en penumbra y la lluvia.
        ladrillos(Rect(0f, 0f, w * 0.09f, h * 0.8f), u, 3)
        ladrillos(Rect(w * 0.91f, 0f, w, h * 0.8f), u, 4)
        drawRect(NOCHE.copy(alpha = 0.45f), Offset.Zero, Size(w, h * 0.8f))
        lluvia(Rect(0f, 0f, w * 0.085f, h * 0.8f), u, 11, 10)
        lluvia(Rect(w * 0.915f, 0f, w, h * 0.8f), u, 12, 10)

        // La pared de azulejos del fondo (el nicho y lo que la cortina deja ver).
        val muro = Rect(w * 0.12f, h * 0.06f, w * 0.88f, h * 0.75f)
        drawRect(AZULEJO, muro.topLeft, muro.size)
        val t = 3.8f * u
        var y = muro.top; var k = 0
        while (y < muro.bottom && k < 60) { drawLine(AZULEJO_JUNTA, Offset(muro.left, y), Offset(muro.right, y), 0.35f * u); y += t; k++ }
        var x = muro.left; k = 0
        while (x < muro.right && k < 120) { drawLine(AZULEJO_JUNTA, Offset(x, muro.top), Offset(x, muro.bottom), 0.35f * u); x += t; k++ }
        drawRect(Brush.verticalGradient(listOf(e.acento.copy(alpha = 0.1f), Color.Transparent), muro.top, muro.top + muro.height * 0.45f), muro.topLeft, Size(muro.width, muro.height * 0.45f))
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, NOCHE_OSC.copy(alpha = 0.5f)), n.bottom - 4f * u, muro.bottom), Offset(muro.left, n.bottom - 4f * u), Size(muro.width, muro.bottom - n.bottom + 4f * u))

        // Los postes de madera, con su veta y sus abrazaderas.
        listOf(Rect(w * 0.085f, 0f, w * 0.125f, h * 0.8f), Rect(w * 0.875f, 0f, w * 0.915f, h * 0.8f)).forEachIndexed { i, p ->
            drawRect(MADERA, p.topLeft, p.size)
            drawRect(MADERA_LUZ, p.topLeft, Size(p.width * 0.18f, p.height))
            drawRect(MADERA_OSC, Offset(p.right - p.width * 0.2f, p.top), Size(p.width * 0.2f, p.height))
            for (j in 0 until 3) {
                val gx = p.left + p.width * (0.32f + j * 0.16f)
                drawPath(Path().apply {
                    moveTo(gx, p.top)
                    cubicTo(gx + p.width * 0.08f, p.top + p.height * 0.3f, gx - p.width * 0.08f, p.top + p.height * 0.6f, gx + p.width * 0.04f * (j - 1), p.bottom)
                }, MADERA_OSC.copy(alpha = 0.7f), style = Stroke(0.3f * u))
            }
            listOf(0.42f, 0.66f).forEach { f ->
                val b = Rect(p.left - 0.3f * u, p.top + p.height * f, p.right + 0.3f * u, p.top + p.height * f + 2f * u)
                pieza(b, ACERO, ACERO_BRILLO, ACERO_OSC, 0.3f * u)
                remache(Offset(b.center.x, b.center.y), 0.55f * u)
            }
            // La placa del letrero vertical, colgada del poste hacia la calle.
            val lx = if (i == 0) w * 0.008f else w * 0.932f
            val le = Rect(lx, h * 0.3f, lx + w * 0.06f, h * 0.72f)
            val brazo = if (i == 0) le.right else le.left
            listOf(le.top + 1.6f * u, le.bottom - 1.6f * u).forEach { by -> drawLine(ACERO_LUZ, Offset(brazo, by), Offset(if (i == 0) p.left else p.right, by), 0.8f * u) }
            drawRect(NOCHE_OSC.copy(alpha = 0.6f), Offset(le.left + 0.6f * u, le.top + 0.8f * u), le.size)
            pieza(le, NOCHE_OSC, ACERO_LUZ, ACERO_OSC, 0.5f * u, 0.6f * u)
            val col = if (i == 0) e.secundario else CIAN
            val gw = le.width * 0.52f
            for (g in 0 until 3) glifo(Offset(le.center.x, le.top + le.height * (0.2f + g * 0.3f)), gw, g + i * 3, col, 0.55f * u)
        }

        // La viga, con su veta y el filo de neón magenta.
        val viga = Rect(w * 0.05f, 0f, w * 0.95f, h * 0.07f)
        drawRect(MADERA_OSC, viga.topLeft, viga.size)
        drawRect(MADERA, viga.topLeft, Size(viga.width, viga.height * 0.7f))
        for (j in 0 until 3) {
            val gy = viga.top + viga.height * (0.18f + j * 0.18f)
            drawPath(Path().apply { moveTo(viga.left, gy); cubicTo(w * 0.3f, gy + 0.8f * u, w * 0.6f, gy - 0.8f * u, viga.right, gy + 0.3f * u) }, MADERA_LUZ.copy(alpha = 0.45f), style = Stroke(0.3f * u))
        }
        neon(linea(Offset(viga.left + 1f * u, viga.bottom - 0.4f * u), Offset(viga.right - 1f * u, viga.bottom - 0.4f * u)), e.acento, 0.7f * u)

        // La cortina noren: cinco paños de lona con sus pliegues, la barra de madera y el emblema del tazón.
        val nr = Rect(w * 0.125f, viga.bottom + 0.8f * u, w * 0.875f, h * 0.185f)
        val paños = 5; val pw = nr.width / paños
        for (i in 0 until paños) {
            val p = Rect(nr.left + pw * i + 0.4f * u, nr.top, nr.left + pw * (i + 1) - 0.4f * u, nr.bottom)
            drawRect(LONA, p.topLeft, p.size)
            drawRect(Brush.horizontalGradient(listOf(LONA_LUZ.copy(alpha = 0.6f), Color.Transparent, LONA_OSC.copy(alpha = 0.7f), Color.Transparent, LONA_OSC.copy(alpha = 0.6f)), p.left, p.right), p.topLeft, p.size)
            drawRect(LONA_OSC, Offset(p.left, p.bottom - 1f * u), Size(p.width, 1f * u))
            drawRect(LONA_OSC, p.topLeft, Size(p.width, 1.8f * u))
        }
        drawLine(MADERA_LUZ, Offset(nr.left - 1f * u, nr.top + 0.9f * u), Offset(nr.right + 1f * u, nr.top + 0.9f * u), 1.1f * u, StrokeCap.Round)
        val em = Offset(w / 2, (nr.top + nr.bottom) / 2 + 0.6f * u); val er = nr.height * 0.36f
        drawCircle(TICKET.copy(alpha = 0.92f), er, em)
        drawPath(Path().apply { moveTo(em.x - er * 0.62f, em.y - er * 0.05f); lineTo(em.x + er * 0.62f, em.y - er * 0.05f); arcTo(Rect(em.x - er * 0.62f, em.y - er * 0.6f, em.x + er * 0.62f, em.y + er * 0.5f), 0f, 180f, false); close() }, LONA)
        drawLine(LONA, Offset(em.x + er * 0.05f, em.y - er * 0.65f), Offset(em.x + er * 0.55f, em.y - er * 0.18f), er * 0.12f, StrokeCap.Round)
        drawLine(LONA, Offset(em.x + er * 0.25f, em.y - er * 0.72f), Offset(em.x + er * 0.7f, em.y - er * 0.3f), er * 0.12f, StrokeCap.Round)

        // Los farolillos de papel rojo, con sus costillas, sus tapas y su halo.
        listOf(w * 0.045f, w * 0.955f).forEach { fx -> farolillo(Offset(fx, h * 0.19f), 3.4f * u, 4.8f * u, u) }

        // La barra: la cubierta de acero con lo que hay encima, el filo de neón, el frente de tablillas y los taburetes.
        val cub = Rect(w * 0.03f, h * 0.75f, w * 0.97f, h * 0.84f)
        drawRect(Brush.verticalGradient(listOf(ACERO, ACERO_LUZ), cub.top, cub.bottom), cub.topLeft, cub.size)
        var by = cub.top + 1f * u; k = 0
        while (by < cub.bottom && k < 20) { drawLine(ACERO_BRILLO.copy(alpha = 0.12f), Offset(cub.left, by), Offset(cub.right, by), 0.25f * u); by += 1.2f * u; k++ }
        drawLine(NOCHE_OSC, Offset(cub.left, cub.top), Offset(cub.right, cub.top), 0.5f * u)
        drawRect(e.acento.copy(alpha = 0.07f), cub.topLeft, cub.size)
        tazon(Offset(w * 0.24f, h * 0.79f), w * 0.07f, u, e, 1)
        tazon(Offset(w * 0.67f, h * 0.792f), w * 0.07f, u, e, 2)
        // Un plato de gyozas, el vasito de té y el platito de soya.
        val pl = Offset(w * 0.45f, h * 0.8f)
        drawOval(NOCHE_OSC.copy(alpha = 0.5f), Offset(pl.x - w * 0.05f, pl.y - 1.2f * u), Size(w * 0.1f, 3.6f * u))
        drawOval(TICKET, Offset(pl.x - w * 0.05f, pl.y - 1.8f * u), Size(w * 0.1f, 3.4f * u))
        drawOval(e.acento.copy(alpha = 0.6f), Offset(pl.x - w * 0.05f, pl.y - 1.8f * u), Size(w * 0.1f, 3.4f * u), style = Stroke(0.3f * u))
        for (j in 0 until 3) {
            val gx = pl.x - w * 0.025f + j * w * 0.022f
            drawOval(Color(0xFFE9C98A), Offset(gx - 1.6f * u, pl.y - 1.6f * u), Size(3.2f * u, 1.8f * u))
            drawLine(Color(0xFF9A6A30), Offset(gx - 1.2f * u, pl.y - 1.2f * u), Offset(gx + 1.2f * u, pl.y - 1.2f * u), 0.25f * u)
        }
        val vs = Offset(w * 0.84f, h * 0.795f)
        drawRect(Color(0xFF4A5A3A), Offset(vs.x - 1.4f * u, vs.y - 2.4f * u), Size(2.8f * u, 3.6f * u))
        drawOval(Color(0xFF6C7E52), Offset(vs.x - 1.4f * u, vs.y - 3f * u), Size(2.8f * u, 1.2f * u))
        drawOval(Color(0xFF8A9A40), Offset(vs.x - 1f * u, vs.y - 2.8f * u), Size(2f * u, 0.8f * u))
        drawOval(TICKET, Offset(w * 0.35f - 1.6f * u, h * 0.818f - 0.6f * u), Size(3.2f * u, 1.3f * u))
        drawOval(Color(0xFF3A1E14), Offset(w * 0.35f - 1.1f * u, h * 0.818f - 0.35f * u), Size(2.2f * u, 0.8f * u))
        // El filo de neón y el frente de tablillas con su zoclo de acero.
        neon(linea(Offset(cub.left, cub.bottom + 0.3f * u), Offset(cub.right, cub.bottom + 0.3f * u)), e.acento, 0.7f * u)
        val fr = Rect(cub.left, cub.bottom + 1.2f * u, cub.right, h * 0.955f)
        drawRect(MADERA_OSC, fr.topLeft, fr.size)
        val tb = 3.4f * u
        x = fr.left; k = 0
        while (x < fr.right && k < 80) {
            drawRect(if (k % 2 == 0) MADERA else mez(MADERA, MADERA_OSC, 0.4f), Offset(x + 0.25f * u, fr.top), Size(tb - 0.5f * u, fr.height))
            drawLine(MADERA_LUZ.copy(alpha = 0.5f), Offset(x + 0.4f * u, fr.top), Offset(x + 0.4f * u, fr.bottom), 0.25f * u)
            x += tb; k++
        }
        drawRect(Brush.verticalGradient(listOf(e.acento.copy(alpha = 0.18f), Color.Transparent), fr.top, fr.top + fr.height * 0.6f), fr.topLeft, Size(fr.width, fr.height * 0.6f))
        pieza(Rect(fr.left, fr.bottom - 1.6f * u, fr.right, fr.bottom), ACERO, ACERO_LUZ, ACERO_OSC, 0.3f * u)
        // El piso mojado, con el reflejo del filo y los charcos.
        drawRect(NOCHE_OSC, Offset(0f, fr.bottom), Size(w, h - fr.bottom))
        drawRect(Brush.verticalGradient(listOf(e.acento.copy(alpha = 0.16f), Color.Transparent), fr.bottom, h), Offset(0f, fr.bottom), Size(w, h - fr.bottom))
        charco(Offset(w * 0.3f, h * 0.985f), w * 0.1f, 0.9f * u, e.acento)
        charco(Offset(w * 0.74f, h * 0.982f), w * 0.07f, 0.8f * u, CIAN)
        listOf(0.2f, 0.4f, 0.6f, 0.8f).forEach { f -> taburete(Offset(w * f, h * 0.9f), 4.6f * u, u) }
    }

    /** Un signo de letrero, hecho de trazos de neón (no es una letra de ningún idioma: se lee como un letrero de lejos). */
    private fun DrawScope.glifo(c: Offset, s: Float, k: Int, color: Color, g: Float) {
        val h = s * 0.5f
        val p = Path()
        when (k % 6) {
            0 -> { p.moveTo(c.x - h, c.y - h * 0.7f); p.lineTo(c.x + h, c.y - h * 0.7f); p.moveTo(c.x, c.y - h); p.lineTo(c.x, c.y + h); p.moveTo(c.x - h * 0.8f, c.y + h * 0.2f); p.lineTo(c.x + h * 0.8f, c.y + h * 0.2f) }
            1 -> { p.moveTo(c.x - h * 0.8f, c.y - h); p.lineTo(c.x + h * 0.8f, c.y - h); p.lineTo(c.x - h * 0.2f, c.y + h * 0.1f); p.moveTo(c.x - h * 0.1f, c.y - h * 0.1f); p.lineTo(c.x + h * 0.7f, c.y + h) }
            2 -> { p.moveTo(c.x - h * 0.8f, c.y - h * 0.8f); p.lineTo(c.x + h * 0.8f, c.y - h * 0.8f); p.lineTo(c.x + h * 0.8f, c.y + h * 0.8f); p.lineTo(c.x - h * 0.8f, c.y + h * 0.8f); p.close(); p.moveTo(c.x - h * 0.8f, c.y); p.lineTo(c.x + h * 0.8f, c.y) }
            3 -> { p.moveTo(c.x - h, c.y - h * 0.6f); p.lineTo(c.x + h, c.y - h * 0.6f); p.moveTo(c.x - h * 0.3f, c.y - h); p.lineTo(c.x - h * 0.6f, c.y + h); p.moveTo(c.x + h * 0.4f, c.y - h * 0.2f); p.lineTo(c.x + h * 0.4f, c.y + h) }
            4 -> { p.moveTo(c.x - h, c.y - h); p.lineTo(c.x - h, c.y + h); p.moveTo(c.x - h, c.y - h * 0.2f); p.lineTo(c.x + h, c.y - h * 0.2f); p.moveTo(c.x + h * 0.6f, c.y - h); p.lineTo(c.x + h * 0.6f, c.y + h) }
            else -> { p.moveTo(c.x, c.y - h); p.lineTo(c.x, c.y + h * 0.2f); p.moveTo(c.x - h, c.y + h * 0.2f); p.lineTo(c.x + h, c.y + h * 0.2f); p.moveTo(c.x - h * 0.6f, c.y + h); p.lineTo(c.x + h * 0.6f, c.y + h) }
        }
        neon(p, color, g)
    }

    /** Un farolillo de papel: el cordón, las tapas negras, el papel rojo encendido con sus costillas y la borla. */
    private fun DrawScope.farolillo(c: Offset, rx: Float, ry: Float, u: Float) {
        mancha(c, ry * 3.4f, FAROL.copy(alpha = 0.4f))
        drawLine(ACERO_OSC, Offset(c.x, 0f), Offset(c.x, c.y - ry), 0.4f * u)
        val cuerpo = Rect(c.x - rx, c.y - ry, c.x + rx, c.y + ry)
        val ov = Path().apply { addOval(cuerpo) }
        drawPath(ov, Brush.radialGradient(listOf(Color(0xFFFFB08A), FAROL, Color(0xFF8A2018)), Offset(c.x - rx * 0.2f, c.y - ry * 0.1f), rx * 1.3f))
        clipPath(ov) {
            for (j in 1 until 6) {
                val y = cuerpo.top + cuerpo.height * j / 6f
                drawLine(Color(0xFF6A1410).copy(alpha = 0.55f), Offset(cuerpo.left, y), Offset(cuerpo.right, y), 0.3f * u)
            }
            drawLine(Color(0xFF6A1410).copy(alpha = 0.4f), Offset(c.x, cuerpo.top), Offset(c.x, cuerpo.bottom), 0.25f * u)
        }
        drawRect(NOCHE_OSC, Offset(c.x - rx * 0.55f, cuerpo.top - 0.9f * u), Size(rx * 1.1f, 1.4f * u))
        drawRect(NOCHE_OSC, Offset(c.x - rx * 0.55f, cuerpo.bottom - 0.5f * u), Size(rx * 1.1f, 1.4f * u))
        drawLine(FAROL, Offset(c.x, cuerpo.bottom + 0.9f * u), Offset(c.x, cuerpo.bottom + 3.2f * u), 0.5f * u)
        drawRect(Color(0xFFB8302A), Offset(c.x - 0.5f * u, cuerpo.bottom + 3.2f * u), Size(1f * u, 1.8f * u))
    }

    /** Un tazón de fideos visto desde la barra: el cuenco lacado, el caldo, los fideos, el huevo, el alga, los palillos y el vapor. */
    private fun DrawScope.tazon(c: Offset, rx: Float, u: Float, e: EstadoMueble, s: Int) {
        val ry = rx * 0.3f
        drawOval(NOCHE_OSC.copy(alpha = 0.55f), Offset(c.x - rx * 0.95f, c.y + ry * 0.4f), Size(rx * 1.9f, ry * 1.4f))
        // El cuerpo del cuenco, la boca y el caldo.
        drawPath(Path().apply {
            moveTo(c.x - rx, c.y); cubicTo(c.x - rx, c.y + ry * 2.2f, c.x + rx, c.y + ry * 2.2f, c.x + rx, c.y); close()
        }, Brush.horizontalGradient(listOf(LACA_ROJA, mez(LACA_ROJA, LACA, 0.6f)), c.x - rx, c.x + rx))
        drawOval(LACA, Offset(c.x - rx, c.y - ry), Size(rx * 2, ry * 2))
        drawOval(mez(LACA_ROJA, Color.White, 0.15f), Offset(c.x - rx, c.y - ry), Size(rx * 2, ry * 2), style = Stroke(0.35f * u))
        val ci = Rect(c.x - rx * 0.86f, c.y - ry * 0.78f, c.x + rx * 0.86f, c.y + ry * 0.78f)
        drawOval(CALDO, ci.topLeft, ci.size)
        clipPath(Path().apply { addOval(ci) }) {
            for (j in 0 until 5) {
                val yy = ci.top + ci.height * (0.25f + j * 0.13f)
                drawPath(Path().apply { moveTo(ci.left, yy); cubicTo(ci.left + ci.width * 0.3f, yy - ry * 0.3f, ci.left + ci.width * 0.6f, yy + ry * 0.3f, ci.right, yy - ry * 0.1f) },
                    Color(0xFFF1D79A).copy(alpha = 0.75f), style = Stroke(0.35f * u))
            }
            drawRect(Color(0xFF1E3A1E), Offset(ci.left + ci.width * 0.12f, ci.top), Size(ci.width * 0.16f, ci.height * 0.55f))
            drawOval(TICKET, Offset(ci.left + ci.width * 0.55f, ci.top + ci.height * 0.25f), Size(ci.width * 0.2f, ci.height * 0.42f))
            drawOval(Color(0xFFF0A020), Offset(ci.left + ci.width * 0.6f, ci.top + ci.height * 0.34f), Size(ci.width * 0.1f, ci.height * 0.24f))
            for (j in 0 until 6) drawCircle(SODIO.copy(alpha = 0.8f), 0.35f * u, Offset(ci.left + ci.width * (0.3f + 0.25f * azar(s, j)), ci.top + ci.height * (0.3f + 0.5f * azar(s + 9, j))))
            drawOval(Brush.radialGradient(listOf(Color.White.copy(alpha = 0.25f), Color.Transparent), Offset(ci.left + ci.width * 0.35f, ci.top + ci.height * 0.3f), ci.width * 0.3f), ci.topLeft, ci.size)
        }
        // Los palillos, cruzados sobre la boca.
        drawLine(mez(e.secundario, MADERA, 0.35f), Offset(c.x - rx * 0.3f, c.y - ry * 1.4f), Offset(c.x + rx * 1.25f, c.y + ry * 0.2f), 0.6f * u, StrokeCap.Round)
        drawLine(mez(e.secundario, MADERA, 0.15f), Offset(c.x - rx * 0.15f, c.y - ry * 1.6f), Offset(c.x + rx * 1.3f, c.y - ry * 0.3f), 0.6f * u, StrokeCap.Round)
        // El vapor, tenue, que sube.
        for (j in 0 until 3) {
            val vx = c.x - rx * 0.35f + j * rx * 0.35f
            drawPath(Path().apply { moveTo(vx, c.y - ry * 0.6f); quadraticTo(vx - 1.2f * u, c.y - ry * 1.6f, vx, c.y - ry * 2.4f); quadraticTo(vx + 1.2f * u, c.y - ry * 3f, vx, c.y - ry * 3.6f) },
                Color.White.copy(alpha = 0.16f), style = Stroke(0.6f * u, cap = StrokeCap.Round))
        }
    }

    /** Un taburete: el asiento de vinil con su aro cromado, el tubo y el aro para los pies. */
    private fun DrawScope.taburete(c: Offset, r: Float, u: Float) {
        drawLine(ACERO_OSC, Offset(c.x + 0.4f * u, c.y), Offset(c.x + 0.4f * u, size.height), 1f * u)
        drawLine(CROMO, Offset(c.x - 0.2f * u, c.y), Offset(c.x - 0.2f * u, size.height), 0.5f * u)
        drawOval(CROMO.copy(alpha = 0.8f), Offset(c.x - r * 0.55f, size.height - 2.2f * u), Size(r * 1.1f, 1.2f * u), style = Stroke(0.4f * u))
        drawRect(LONA_OSC, Offset(c.x - r, c.y - 0.2f * u), Size(r * 2, 1.6f * u))
        drawOval(LONA_OSC, Offset(c.x - r, c.y + 0.6f * u), Size(r * 2, 1.8f * u))
        drawOval(Brush.horizontalGradient(listOf(LONA_LUZ, LONA, LONA_OSC), c.x - r, c.x + r), Offset(c.x - r, c.y - 1f * u), Size(r * 2, 2f * u))
        drawOval(CROMO, Offset(c.x - r, c.y - 1f * u), Size(r * 2, 2f * u), style = Stroke(0.3f * u))
    }

    // ---------------------------------------------------------------- Mapas: la mesa del callejón

    override fun DrawScope.muebleMapas(e: EstadoMueble, oscuro: Boolean) {
        if (size.minDimension < 1f) return
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val n = nicho(MuebleVida.MAPAS)
        // El muro de ladrillo, más oscuro arriba, y la lluvia a los lados.
        ladrillos(Rect(0f, 0f, w, h * 0.97f), u, 21)
        drawRect(Brush.verticalGradient(listOf(NOCHE.copy(alpha = 0.75f), NOCHE.copy(alpha = 0.35f)), 0f, h), Offset.Zero, size)
        lluvia(Rect(0f, 0f, w * 0.06f, h * 0.95f), u, 31, 8)
        lluvia(Rect(w * 0.94f, 0f, w, h * 0.95f), u, 32, 8)
        // La tubería del lado izquierdo, con sus abrazaderas.
        val tx = w * 0.028f
        drawRect(Brush.horizontalGradient(listOf(ACERO_OSC, ACERO_BRILLO, ACERO, ACERO_OSC), tx - 1.3f * u, tx + 1.3f * u), Offset(tx - 1.3f * u, 0f), Size(2.6f * u, h * 0.97f))
        listOf(0.18f, 0.5f, 0.82f).forEach { f -> pieza(Rect(tx - 1.8f * u, h * f, tx + 1.8f * u, h * f + 1.6f * u), ACERO, ACERO_BRILLO, ACERO_OSC, 0.3f * u) }
        // El medidor de luz a la derecha, con su ventanita y sus cables.
        val med = Rect(w * 0.945f, h * 0.22f, w * 0.99f, h * 0.42f)
        drawLine(NOCHE_OSC, Offset(med.center.x - 0.6f * u, 0f), Offset(med.center.x - 0.6f * u, med.top), 0.6f * u)
        drawLine(NOCHE_OSC, Offset(med.center.x + 0.6f * u, 0f), Offset(med.center.x + 0.6f * u, med.top), 0.6f * u)
        pieza(med, ACERO, ACERO_BRILLO, ACERO_OSC, 0.4f * u, 0.5f * u)
        val mv = Offset(med.center.x, med.top + med.height * 0.38f)
        drawCircle(NOCHE_OSC, med.width * 0.32f, mv)
        mancha(mv, med.width * 0.5f, SODIO.copy(alpha = 0.25f))
        drawLine(SODIO, Offset(mv.x - med.width * 0.18f, mv.y), Offset(mv.x + med.width * 0.12f, mv.y - med.width * 0.1f), 0.35f * u)
        drawRect(e.secundario, Offset(med.left + med.width * 0.25f, med.bottom - med.height * 0.25f), Size(med.width * 0.5f, med.height * 0.08f))

        // Los cables que cruzan arriba y la lámpara ámbar que cuelga, con su halo.
        listOf(0.012f to 0.07f, 0.03f to 0.095f).forEach { (a, b) ->
            drawPath(Path().apply { moveTo(0f, h * a); quadraticTo(w * 0.5f, h * b * 2f, w, h * a * 1.5f) }, NOCHE_OSC, style = Stroke(0.6f * u))
        }
        val lc = Offset(w * 0.5f, h * 0.088f)
        mancha(Offset(lc.x, lc.y + 1f * u), 16f * u, e.secundario.copy(alpha = 0.4f))
        drawLine(NOCHE_OSC, Offset(lc.x, 0f), Offset(lc.x, h * 0.03f), 0.5f * u)
        drawPath(Path().apply { moveTo(lc.x - 1.6f * u, h * 0.03f); lineTo(lc.x + 1.6f * u, h * 0.03f); lineTo(lc.x + 5.2f * u, lc.y); lineTo(lc.x - 5.2f * u, lc.y); close() },
            Brush.horizontalGradient(listOf(ACERO_BRILLO, ACERO, ACERO_OSC), lc.x - 5.2f * u, lc.x + 5.2f * u))
        drawArc(e.secundario, 0f, 180f, true, Offset(lc.x - 2.6f * u, lc.y - 1.6f * u), Size(5.2f * u, 3.2f * u))
        drawArc(Color.White.copy(alpha = 0.8f), 0f, 180f, true, Offset(lc.x - 1.2f * u, lc.y - 0.8f * u), Size(2.4f * u, 1.6f * u))

        // El piso mojado, la sombra de la mesa y sus reflejos.
        val piso = h * 0.965f
        drawRect(NOCHE_OSC, Offset(0f, piso), Size(w, h - piso))
        charco(Offset(w * 0.5f, h * 0.983f), w * 0.36f, 1f * u, e.acento)
        charco(Offset(w * 0.5f, h * 0.983f), w * 0.05f, 0.6f * u, CIAN)
        // Las patas con su travesaño y el proyector holográfico debajo.
        listOf(w * 0.09f, w * 0.875f).forEach { x ->
            val p = Rect(x, h * 0.85f, x + w * 0.035f, piso)
            drawRect(ACERO_OSC, p.topLeft, p.size)
            drawRect(ACERO_LUZ, p.topLeft, Size(p.width * 0.45f, p.height))
            drawLine(ACERO_BRILLO, Offset(p.left + 0.3f * u, p.top), Offset(p.left + 0.3f * u, p.bottom), 0.3f * u)
            pieza(Rect(p.left - 1f * u, piso - 1f * u, p.right + 1f * u, piso + 0.3f * u), ACERO, ACERO_BRILLO, ACERO_OSC, 0.25f * u)
        }
        drawRect(ACERO_OSC, Offset(w * 0.125f, h * 0.925f), Size(w * 0.75f, 1.2f * u))
        drawLine(ACERO_LUZ, Offset(w * 0.125f, h * 0.925f), Offset(w * 0.875f, h * 0.925f), 0.3f * u)
        val pj = Rect(w * 0.42f, h * 0.875f, w * 0.58f, h * 0.935f)
        drawRect(Brush.verticalGradient(listOf(CIAN.copy(alpha = 0f), CIAN.copy(alpha = 0.22f)), h * 0.85f, pj.top), Offset(pj.center.x - 2f * u, h * 0.85f), Size(4f * u, pj.top - h * 0.85f))
        pieza(pj, ACERO_LUZ, ACERO_BRILLO, ACERO_OSC, 0.4f * u, 0.6f * u)
        for (j in 0 until 4) drawLine(NOCHE_OSC, Offset(pj.left + pj.width * (0.1f + j * 0.06f), pj.top + pj.height * 0.3f), Offset(pj.left + pj.width * (0.1f + j * 0.06f), pj.bottom - pj.height * 0.25f), 0.4f * u)
        val lente = Offset(pj.center.x + pj.width * 0.15f, pj.top + pj.height * 0.48f)
        mancha(lente, 5f * u, CIAN.copy(alpha = 0.5f))
        drawCircle(NOCHE_OSC, 1.7f * u, lente)
        drawCircle(CIAN, 1.1f * u, lente)
        drawCircle(Color.White.copy(alpha = 0.8f), 0.4f * u, Offset(lente.x - 0.3f * u, lente.y - 0.3f * u))
        drawCircle(SODIO, 0.4f * u, Offset(pj.right - pj.width * 0.12f, pj.top + pj.height * 0.3f))

        // La mesa: el marco de acero remachado, sus escuadras de neón cian y el vidrio con su rejilla (el nicho).
        val marco = Rect(w * 0.065f, h * 0.11f, w * 0.935f, h * 0.78f)
        drawRect(NOCHE_OSC.copy(alpha = 0.6f), Offset(marco.left + 0.6f * u, marco.top + 1f * u), marco.size)
        pieza(marco, ACERO, ACERO_BRILLO, ACERO_OSC, 0.5f * u, 0.8f * u)
        drawRect(Brush.horizontalGradient(listOf(Color.Transparent, e.secundario.copy(alpha = 0.3f), Color.Transparent), w * 0.3f, w * 0.7f), Offset(w * 0.3f, marco.top), Size(w * 0.4f, 0.5f * u))
        val rem = 6.5f * u
        var rx = marco.left + 3f * u; var k = 0
        while (rx < marco.right - 2f * u && k < 60) {
            remache(Offset(rx, (marco.top + n.top) / 2), 0.45f * u); remache(Offset(rx, (marco.bottom + n.bottom) / 2), 0.45f * u)
            rx += rem; k++
        }
        drawRect(NOCHE_OSC, Offset(n.left - 0.8f * u, n.top - 0.8f * u), Size(n.width + 1.6f * u, n.height + 1.6f * u))
        drawRect(VIDRIO, n.topLeft, n.size)
        clipRect(n.left, n.top, n.right, n.bottom) {
            var gx = n.left + 2.5f * u; k = 0
            while (gx < n.right && k < 100) { drawLine(CIAN.copy(alpha = 0.06f), Offset(gx, n.top), Offset(gx, n.bottom), 0.25f * u); gx += 5f * u; k++ }
            var gy = n.top + 2.5f * u; k = 0
            while (gy < n.bottom && k < 60) { drawLine(CIAN.copy(alpha = 0.06f), Offset(n.left, gy), Offset(n.right, gy), 0.25f * u); gy += 5f * u; k++ }
            drawRect(Brush.radialGradient(listOf(e.acento.copy(alpha = 0.06f), Color.Transparent), Offset(n.right, n.bottom), n.width * 0.5f), n.topLeft, n.size)
            drawRect(Brush.verticalGradient(listOf(NOCHE_OSC.copy(alpha = 0.6f), Color.Transparent), n.top, n.top + 2f * u), n.topLeft, Size(n.width, 2f * u))
        }
        val l = 4.5f * u; val d = 1.4f * u
        listOf(Offset(n.left - d, n.top - d) to Offset(1f, 1f), Offset(n.right + d, n.top - d) to Offset(-1f, 1f), Offset(n.left - d, n.bottom + d) to Offset(1f, -1f), Offset(n.right + d, n.bottom + d) to Offset(-1f, -1f)).forEach { (p, s) ->
            neon(Path().apply { moveTo(p.x + s.x * l, p.y); lineTo(p.x, p.y); lineTo(p.x, p.y + s.y * l) }, CIAN, 0.45f * u)
        }
        // El canto: la chapa gruesa con las franjas de peligro, los tornillos, los pilotos y la luz de bajo.
        val canto = Rect(w * 0.05f, h * 0.78f, w * 0.95f, h * 0.85f)
        pieza(canto, ACERO_OSC, ACERO_LUZ, NOCHE_OSC, 0.4f * u, 0.5f * u)
        val fr = Rect(w * 0.32f, canto.top + canto.height * 0.25f, w * 0.68f, canto.bottom - canto.height * 0.25f)
        drawRect(NOCHE_OSC, fr.topLeft, fr.size)
        clipRect(fr.left, fr.top, fr.right, fr.bottom) {
            var sx = fr.left - fr.height; k = 0
            while (sx < fr.right && k < 60) {
                drawPath(Path().apply { moveTo(sx, fr.bottom); lineTo(sx + fr.height, fr.top); lineTo(sx + fr.height * 1.8f, fr.top); lineTo(sx + fr.height * 0.8f, fr.bottom); close() }, mez(e.secundario, NOCHE, 0.25f))
                sx += fr.height * 1.6f; k++
            }
        }
        listOf(canto.left + 2f * u, canto.right - 2f * u, w * 0.29f, w * 0.71f).forEach { remache(Offset(it, canto.center.y), 0.7f * u) }
        listOf(SODIO, e.acento, CIAN).forEachIndexed { j, c ->
            val p = Offset(w * (0.8f + j * 0.035f), canto.center.y)
            mancha(p, 2.4f * u, c.copy(alpha = 0.45f)); drawCircle(c, 0.6f * u, p)
        }
        drawRect(NOCHE_OSC, Offset(w * 0.12f, canto.center.y - 0.5f * u), Size(w * 0.12f, 1f * u))
        neon(linea(Offset(canto.left + 2f * u, canto.bottom + 0.6f * u), Offset(canto.right - 2f * u, canto.bottom + 0.6f * u)), e.acento, 0.55f * u)
    }

    // ---------------------------------------------------------------- Otras vidas: la máquina expendedora

    override fun DrawScope.muebleOtrasVidas(e: EstadoMueble, oscuro: Boolean) {
        if (size.minDimension < 1f) return
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val n = nicho(MuebleVida.OTRAS_VIDAS)
        ladrillos(Rect(0f, 0f, w, h), u, 41)
        drawRect(NOCHE.copy(alpha = 0.7f), Offset.Zero, size)
        val piso = h * 0.965f
        drawRect(NOCHE_OSC, Offset(0f, piso), Size(w, h - piso))
        charco(Offset(w * 0.5f, h * 0.985f), w * 0.42f, 0.8f * u, e.acento)
        // El gabinete: la chapa con sus costados y sus rejillas.
        val gab = Rect(w * 0.012f, h * 0.015f, w * 0.988f, piso)
        drawRoundRect(NOCHE_OSC.copy(alpha = 0.7f), Offset(gab.left + 0.8f * u, gab.top + 1f * u), gab.size, CornerRadius(2f * u))
        pieza(gab, CARCASA, ACERO_LUZ, NOCHE_OSC, 0.6f * u, 2f * u)
        listOf(gab.left + 1.6f * u, gab.right - 1.6f * u).forEach { x ->
            for (j in 0 until 10) {
                val y = n.top + n.height * (0.08f + j * 0.095f)
                drawLine(NOCHE_OSC, Offset(x - 0.6f * u, y), Offset(x + 0.6f * u, y), 0.6f * u, StrokeCap.Round)
            }
        }
        // La marquesina: la caja negra con sus focos alrededor y cuatro íconos de neón.
        val mq = Rect(w * 0.045f, h * 0.045f, w * 0.955f, h * 0.225f)
        pieza(mq, NOCHE_OSC, ACERO_BRILLO, ACERO_OSC, 0.5f * u, 1.2f * u)
        drawRect(Brush.verticalGradient(listOf(e.acento.copy(alpha = 0.08f), Color.Transparent), mq.top, mq.bottom), Offset(mq.left + 0.6f * u, mq.top + 0.6f * u), Size(mq.width - 1.2f * u, mq.height - 1.2f * u))
        val paso = 3.4f * u
        var fx = mq.left + 2.2f * u; var k = 0
        while (fx < mq.right - 1.5f * u && k < 120) {
            val c = if (k % 2 == 0) e.secundario else e.acento
            listOf(mq.top + 1.6f * u, mq.bottom - 1.6f * u).forEach { y -> drawCircle(c.copy(alpha = 0.3f), 0.9f * u, Offset(fx, y)); drawCircle(c, 0.42f * u, Offset(fx, y)) }
            fx += paso; k++
        }
        val s = mq.height * 0.26f; val cy = mq.center.y
        val g = 0.55f * u
        // Una lata (cian), un rayo (magenta), una estrella (ámbar) y unas ondas (sodio).
        val c1 = Offset(w * 0.2f, cy)
        neon(Path().apply { addRoundRect(RoundRect(Rect(c1.x - s * 0.45f, c1.y - s * 0.75f, c1.x + s * 0.45f, c1.y + s * 0.85f), CornerRadius(s * 0.15f))); moveTo(c1.x - s * 0.45f, c1.y - s * 0.35f); lineTo(c1.x + s * 0.45f, c1.y - s * 0.35f) }, CIAN, g)
        val c2 = Offset(w * 0.4f, cy)
        neon(Path().apply { moveTo(c2.x + s * 0.25f, c2.y - s); lineTo(c2.x - s * 0.35f, c2.y + s * 0.1f); lineTo(c2.x + s * 0.3f, c2.y + s * 0.05f); lineTo(c2.x - s * 0.2f, c2.y + s) }, e.acento, g)
        val c3 = Offset(w * 0.6f, cy)
        neon(Path().apply {
            for (j in 0 until 10) {
                val a = Math.toRadians(-90.0 + j * 36.0); val r = if (j % 2 == 0) s else s * 0.42f
                val p = Offset(c3.x + (r * cos(a)).toFloat(), c3.y + (r * sin(a)).toFloat() + s * 0.08f)
                if (j == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
            }
            close()
        }, e.secundario, g)
        val c4 = Offset(w * 0.8f, cy)
        neon(Path().apply {
            for (j in 0 until 2) {
                val y = c4.y - s * 0.3f + j * s * 0.6f
                moveTo(c4.x - s, y); cubicTo(c4.x - s * 0.5f, y - s * 0.45f, c4.x, y + s * 0.45f, c4.x + s * 0.5f, y); quadraticTo(c4.x + s * 0.75f, y - s * 0.22f, c4.x + s, y)
            }
        }, SODIO, g)
        // El labio de acero bajo la marquesina.
        pieza(Rect(w * 0.03f, mq.bottom + 0.8f * u, w * 0.97f, n.top - 1.8f * u), ACERO, ACERO_BRILLO, ACERO_OSC, 0.3f * u)

        // El cristal (el nicho): cinco compartimentos con su luz de techo, su repisa y su espiral; los tubos a los lados.
        drawRect(NOCHE_OSC, Offset(n.left - 1f * u, n.top - 1f * u), Size(n.width + 2f * u, n.height + 2f * u))
        drawRect(INTERIOR, n.topLeft, n.size)
        val cw = n.width / 5
        for (i in 0 until 5) {
            val b = Rect(n.left + cw * i, n.top, n.left + cw * (i + 1), n.bottom)
            drawRect(Brush.verticalGradient(listOf(CIAN.copy(alpha = 0.1f), Color.Transparent), b.top, b.top + b.height * 0.5f), b.topLeft, Size(b.width, b.height * 0.5f))
            drawLine(CIAN.copy(alpha = 0.75f), Offset(b.left + b.width * 0.15f, b.top + 0.5f * u), Offset(b.right - b.width * 0.15f, b.top + 0.5f * u), 0.4f * u)
            val ry = b.bottom - 2.2f * u
            drawLine(CROMO.copy(alpha = 0.35f), Offset(b.left + b.width * 0.12f, ry - 1f * u), Offset(b.right - b.width * 0.12f, ry - 1f * u), 0.9f * u,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.4f * u, 0.8f * u)))
            drawRect(ACERO_LUZ, Offset(b.left, ry), Size(b.width, 0.7f * u))
            drawRect(ACERO_OSC, Offset(b.left, ry + 0.7f * u), Size(b.width, b.bottom - ry - 0.7f * u))
            drawRect(if (i % 2 == 0) e.secundario else SODIO, Offset(b.center.x - 1.4f * u, ry + 0.2f * u), Size(2.8f * u, 1f * u))
        }
        for (i in 1 until 5) {
            val x = n.left + cw * i
            drawRect(ACERO_OSC, Offset(x - 0.5f * u, n.top), Size(1f * u, n.height))
            drawLine(ACERO_LUZ, Offset(x - 0.5f * u, n.top), Offset(x - 0.5f * u, n.bottom), 0.25f * u)
        }
        clipRect(n.left, n.top, n.right, n.bottom) {
            listOf(0.18f to 0.06f, 0.62f to 0.035f).forEach { (f, a) ->
                drawPath(Path().apply { moveTo(n.left + n.width * f, n.top); lineTo(n.left + n.width * (f + 0.1f), n.top); lineTo(n.left + n.width * (f - 0.04f), n.bottom); lineTo(n.left + n.width * (f - 0.12f), n.bottom); close() }, Color.White.copy(alpha = a * 0.5f))
            }
        }
        listOf(n.left - 1.9f * u, n.right + 1.9f * u).forEach { x -> neon(linea(Offset(x, n.top + 1f * u), Offset(x, n.bottom - 1f * u)), e.acento, 0.55f * u) }

        // Abajo: la trampilla para sacar lo comprado y el panel de pago.
        val baj = Rect(w * 0.045f, n.bottom + 2.2f * u, w * 0.955f, gab.bottom - 1.4f * u)
        val tr = Rect(w * 0.08f, baj.top, w * 0.6f, baj.bottom)
        pieza(tr, NOCHE_OSC, ACERO_OSC, ACERO_BRILLO, 0.4f * u, 0.6f * u)
        pieza(Rect(tr.left + 1.2f * u, tr.top + 1f * u, tr.right - 1.2f * u, tr.bottom - 1.6f * u), ACERO, ACERO_BRILLO, ACERO_OSC, 0.3f * u, 0.4f * u)
        drawLine(CROMO, Offset(tr.center.x - tr.width * 0.12f, tr.top + 2f * u), Offset(tr.center.x + tr.width * 0.12f, tr.top + 2f * u), 0.8f * u, StrokeCap.Round)
        drawPath(Path().apply { moveTo(tr.right - 4f * u, tr.center.y - 0.6f * u); lineTo(tr.right - 2f * u, tr.center.y - 0.6f * u); lineTo(tr.right - 3f * u, tr.center.y + 0.8f * u); close() }, e.secundario)
        val pg = Rect(w * 0.64f, baj.top, w * 0.92f, baj.bottom)
        pieza(pg, ACERO, ACERO_BRILLO, ACERO_OSC, 0.3f * u, 0.5f * u)
        val ranura = Rect(pg.left + pg.width * 0.08f, pg.top + pg.height * 0.22f, pg.left + pg.width * 0.13f, pg.bottom - pg.height * 0.22f)
        drawRoundRect(CROMO, Offset(ranura.left - 0.4f * u, ranura.top - 0.4f * u), Size(ranura.width + 0.8f * u, ranura.height + 0.8f * u), CornerRadius(0.6f * u))
        drawRoundRect(NOCHE_OSC, ranura.topLeft, ranura.size, CornerRadius(0.4f * u))
        val lec = Rect(pg.left + pg.width * 0.2f, pg.top + pg.height * 0.3f, pg.left + pg.width * 0.42f, pg.bottom - pg.height * 0.3f)
        drawRect(NOCHE_OSC, lec.topLeft, lec.size)
        drawCircle(SODIO, 0.45f * u, Offset(lec.right - 0.9f * u, lec.center.y)); mancha(Offset(lec.right - 0.9f * u, lec.center.y), 2f * u, SODIO.copy(alpha = 0.4f))
        for (j in 0 until 6) {
            val kx = pg.left + pg.width * (0.5f + (j % 3) * 0.15f); val ky = pg.top + pg.height * (0.3f + (j / 3) * 0.38f)
            drawRect(NOCHE_OSC, Offset(kx, ky), Size(pg.width * 0.11f, pg.height * 0.26f))
            drawRect(e.secundario.copy(alpha = 0.7f), Offset(kx, ky), Size(pg.width * 0.11f, pg.height * 0.26f), style = Stroke(0.25f * u))
        }
    }

    // ---------------------------------------------------------------- Íconos de los mapas

    override fun DrawScope.iconoMapa(id: String, e: EstadoIcono, oscuro: Boolean) {
        if (size.minDimension < 1f) return
        val k = minOf(size.height / 70f, size.width / 160f)
        val c = Offset(size.width / 2, size.height / 2)
        when (id) {
            "unesco" -> torii(c, k, e)
            "rascar" -> tarjeta(c, k, e)
            else -> chinchetas(c, k, e)
        }
    }

    /** El Patrimonio: el torii bermellón con su viga negra y su neón, entre torres con ventanas encendidas, y su reflejo. */
    private fun DrawScope.torii(c: Offset, k: Float, e: EstadoIcono) {
        val suelo = c.y + 24f * k
        // Las torres de atrás, con sus ventanas y la baliza de la antena.
        listOf(Triple(-40f, 16f, 34f), Triple(-24f, 12f, 46f), Triple(20f, 14f, 50f), Triple(34f, 12f, 30f)).forEachIndexed { i, (dx, an, al) ->
            val t = Rect(c.x + dx * k - an * k / 2, suelo - al * k, c.x + dx * k + an * k / 2, suelo)
            drawRect(Color(0xFF2A1F38), t.topLeft, t.size)
            drawRect(Color(0xFF3A2C4C), t.topLeft, Size(t.width * 0.3f, t.height))
            for (fy in 0 until (al / 5).toInt()) for (fx in 0 until 3) {
                if (azar(i * 7 + fy, fx) < 0.45f) drawRect(if (azar(i + 3, fy * 3 + fx) < 0.6f) e.c2.copy(alpha = 0.85f) else CIAN.copy(alpha = 0.7f),
                    Offset(t.left + t.width * (0.15f + fx * 0.27f), t.top + 2.5f * k + fy * 5f * k), Size(t.width * 0.16f, 2f * k))
            }
        }
        drawLine(ACERO_LUZ, Offset(c.x + 20f * k, suelo - 50f * k), Offset(c.x + 20f * k, suelo - 58f * k), 0.8f * k)
        mancha(Offset(c.x + 20f * k, suelo - 58f * k), 3f * k, FAROL.copy(alpha = 0.6f)); drawCircle(FAROL, 0.9f * k, Offset(c.x + 20f * k, suelo - 58f * k))
        // El reflejo en el suelo mojado.
        drawRect(NOCHE_OSC, Offset(c.x - 48f * k, suelo), Size(96f * k, 10f * k))
        listOf(-11f, 11f).forEach { dx -> drawRect(BERMELLON.copy(alpha = 0.3f), Offset(c.x + dx * k - 1.8f * k, suelo), Size(3.6f * k, 8f * k)) }
        drawRect(e.c1.copy(alpha = 0.25f), Offset(c.x - 20f * k, suelo + 7f * k), Size(40f * k, 1f * k))
        // Los pilares, con su cara en sombra, sus basas negras y el travesaño.
        listOf(-11f, 11f).forEach { dx ->
            val p = Rect(c.x + dx * k - 2f * k, suelo - 36f * k, c.x + dx * k + 2f * k, suelo)
            drawRect(BERMELLON, p.topLeft, p.size)
            drawRect(BERMELLON_OSC, Offset(p.center.x + 0.4f * k, p.top), Size(p.width / 2 - 0.4f * k, p.height))
            drawRect(NOCHE_OSC, Offset(p.left - 0.8f * k, suelo - 4f * k), Size(p.width + 1.6f * k, 4f * k))
        }
        val nuki = Rect(c.x - 17f * k, suelo - 30f * k, c.x + 17f * k, suelo - 27f * k)
        drawRect(BERMELLON, nuki.topLeft, nuki.size); drawRect(BERMELLON_OSC, Offset(nuki.left, nuki.bottom - 1f * k), Size(nuki.width, 1f * k))
        drawRect(BERMELLON, Offset(c.x - 1.5f * k, suelo - 36f * k), Size(3f * k, 6f * k))
        drawRect(BERMELLON, Offset(c.x - 19f * k, suelo - 38f * k), Size(38f * k, 3f * k))
        val kasagi = Path().apply {
            moveTo(c.x - 24f * k, suelo - 44f * k); quadraticTo(c.x, suelo - 39f * k, c.x + 24f * k, suelo - 44f * k)
            lineTo(c.x + 22f * k, suelo - 40f * k); quadraticTo(c.x, suelo - 36.5f * k, c.x - 22f * k, suelo - 40f * k); close()
        }
        drawPath(kasagi, NOCHE_OSC)
        neon(Path().apply { moveTo(c.x - 24f * k, suelo - 44.5f * k); quadraticTo(c.x, suelo - 39.5f * k, c.x + 24f * k, suelo - 44.5f * k) }, e.c1, 1.1f * k)
    }

    /** El mapa para rascar: la tarjeta holográfica con su canto, el chip, la franja y la capa a medio rascar, y la moneda. */
    private fun DrawScope.tarjeta(c: Offset, k: Float, e: EstadoIcono) {
        val t = Rect(c.x - 36f * k, c.y - 22f * k, c.x + 32f * k, c.y + 20f * k)
        drawRoundRect(NOCHE_OSC.copy(alpha = 0.7f), Offset(t.left + 2f * k, t.top + 3f * k), t.size, CornerRadius(4f * k))
        drawRoundRect(ACERO_OSC, Offset(t.left, t.top + 1.6f * k), t.size, CornerRadius(4f * k))
        val forma = Path().apply { addRoundRect(RoundRect(t, CornerRadius(4f * k))) }
        drawPath(forma, Brush.linearGradient(listOf(Color(0xFF3A2440), Color(0xFF221830), Color(0xFF2E2050)), t.topLeft, t.bottomRight))
        clipPath(forma) {
            drawRect(Brush.linearGradient(listOf(e.c1.copy(alpha = 0f), e.c1.copy(alpha = 0.25f), CIAN.copy(alpha = 0.2f), e.c2.copy(alpha = 0f)), t.topLeft, Offset(t.right, t.top + t.height * 0.5f)), t.topLeft, t.size)
            drawRect(e.c1, Offset(t.left, t.top + 5f * k), Size(t.width, 2.4f * k))
        }
        drawRoundRect(Color.White.copy(alpha = 0.18f), t.topLeft, t.size, CornerRadius(4f * k), style = Stroke(0.6f * k))
        // El chip dorado con sus contactos.
        val ch = Rect(t.left + 6f * k, t.top + 11f * k, t.left + 16f * k, t.top + 19f * k)
        drawRoundRect(Brush.linearGradient(listOf(Color(0xFFF2D27A), Color(0xFFB8862E)), ch.topLeft, ch.bottomRight), ch.topLeft, ch.size, CornerRadius(1.4f * k))
        drawLine(Color(0xFF7A5418), Offset(ch.left, ch.center.y), Offset(ch.right, ch.center.y), 0.5f * k)
        drawLine(Color(0xFF7A5418), Offset(ch.center.x, ch.top), Offset(ch.center.x, ch.bottom), 0.5f * k)
        // La capa de plata por rascar, con la costa que ya asoma.
        val capa = Rect(t.left + 6f * k, t.top + 23f * k, t.right - 6f * k, t.bottom - 4f * k)
        drawRoundRect(NOCHE, capa.topLeft, capa.size, CornerRadius(1.4f * k))
        clipRect(capa.left, capa.top, capa.right, capa.bottom) {
            drawPath(Path().apply { moveTo(capa.left, capa.bottom); cubicTo(capa.left + capa.width * 0.2f, capa.top, capa.left + capa.width * 0.35f, capa.bottom, capa.left + capa.width * 0.5f, capa.top + capa.height * 0.3f); lineTo(capa.left + capa.width * 0.5f, capa.bottom); close() }, e.c1.copy(alpha = 0.55f))
            val lam = Path().apply {
                moveTo(capa.left + capa.width * 0.42f, capa.top); lineTo(capa.left + capa.width * 0.48f, capa.top + capa.height * 0.3f)
                lineTo(capa.left + capa.width * 0.4f, capa.top + capa.height * 0.55f); lineTo(capa.left + capa.width * 0.5f, capa.bottom)
                lineTo(capa.right, capa.bottom); lineTo(capa.right, capa.top); close()
            }
            drawPath(lam, Brush.linearGradient(listOf(Color(0xFF9C7AA0), Color(0xFFF2C6E4), Color(0xFF9C7AA0)), capa.topLeft, capa.bottomRight))
            var x = capa.left; var j = 0
            while (x < capa.right + capa.height && j < 40) { drawLine(Color.White.copy(alpha = 0.18f), Offset(x, capa.top), Offset(x - capa.height, capa.bottom), 0.4f * k); x += 2f * k; j++ }
        }
        // El holograma redondo.
        val hc = Offset(t.right - 9f * k, t.top + 15f * k)
        drawCircle(Brush.sweepGradient(listOf(CIAN, e.c1, e.c2, SODIO, CIAN), hc), 4.2f * k, hc)
        drawCircle(Color.White.copy(alpha = 0.35f), 4.2f * k, hc, style = Stroke(0.5f * k))
        // La moneda que rasca, de canto estriado, con su brillo.
        val mc = Offset(t.right + 1f * k, t.bottom - 1f * k)
        drawOval(NOCHE_OSC.copy(alpha = 0.6f), Offset(mc.x - 9f * k, mc.y - 4f * k), Size(18f * k, 11f * k))
        drawOval(mez(e.c2, NOCHE, 0.45f), Offset(mc.x - 9f * k, mc.y - 6f * k), Size(18f * k, 11f * k))
        drawOval(e.c2, Offset(mc.x - 9f * k, mc.y - 7.6f * k), Size(18f * k, 11f * k))
        drawOval(mez(e.c2, Color.White, 0.35f), Offset(mc.x - 6f * k, mc.y - 5.8f * k), Size(12f * k, 7.4f * k), style = Stroke(0.6f * k))
        drawOval(Color.White.copy(alpha = 0.5f), Offset(mc.x - 5f * k, mc.y - 6.4f * k), Size(5f * k, 2f * k))
    }

    /** Tus lugares: tres chinchetas de neón, con su brillo y su agujero, sobre la rejilla del suelo mojado que se pierde a lo lejos. */
    private fun DrawScope.chinchetas(c: Offset, k: Float, e: EstadoIcono) {
        val hz = c.y - 4f * k; val pie = c.y + 30f * k
        val suelo = Path().apply { moveTo(c.x - 26f * k, hz); lineTo(c.x + 26f * k, hz); lineTo(c.x + 64f * k, pie); lineTo(c.x - 64f * k, pie); close() }
        drawPath(suelo, Brush.verticalGradient(listOf(NOCHE_OSC, Color(0xFF1E1530)), hz, pie))
        clipPath(suelo) {
            for (j in -6..6) drawLine(CIAN.copy(alpha = 0.35f), Offset(c.x + j * 4.3f * k, hz), Offset(c.x + j * 10.6f * k, pie), 0.5f * k)
            var t = 0f; var j = 0
            while (j < 7) { val y = hz + (pie - hz) * t * t; drawLine(CIAN.copy(alpha = 0.3f), Offset(c.x - 70f * k, y), Offset(c.x + 70f * k, y), 0.5f * k); t += 1f / 6f; j++ }
        }
        listOf(Triple(Offset(c.x - 26f * k, c.y + 18f * k), e.c1, 1f), Triple(Offset(c.x + 2f * k, c.y + 8f * k), e.c2, 1.15f), Triple(Offset(c.x + 28f * k, c.y + 20f * k), SODIO, 0.95f)).forEach { (p, col, s) ->
            val r = 6.4f * k * s
            val cab = Offset(p.x, p.y - r * 2.4f)
            drawOval(col.copy(alpha = 0.35f), Offset(p.x - r * 1.2f, p.y - r * 0.25f), Size(r * 2.4f, r * 0.7f))
            mancha(cab, r * 2.8f, col.copy(alpha = 0.35f))
            val gota = Path().apply {
                moveTo(p.x, p.y)
                cubicTo(p.x - r * 0.4f, p.y - r * 0.8f, cab.x - r, cab.y + r * 0.7f, cab.x - r, cab.y)
                arcTo(Rect(cab.x - r, cab.y - r, cab.x + r, cab.y + r), 180f, 180f, false)
                cubicTo(cab.x + r, cab.y + r * 0.7f, p.x + r * 0.4f, p.y - r * 0.8f, p.x, p.y); close()
            }
            drawPath(gota, Brush.radialGradient(listOf(mez(col, Color.White, 0.55f), col, mez(col, NOCHE, 0.45f)), Offset(cab.x - r * 0.35f, cab.y - r * 0.35f), r * 2.2f))
            drawCircle(NOCHE_OSC, r * 0.38f, cab)
            drawCircle(Color.White.copy(alpha = 0.7f), r * 0.2f, Offset(cab.x - r * 0.5f, cab.y - r * 0.5f))
        }
    }

    // ---------------------------------------------------------------- Notas: el tablón del callejón

    override fun DrawScope.tableroNotas(e: EstadoMueble, oscuro: Boolean) {
        if (size.minDimension < 1f) return
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val n = nicho(MuebleVida.NOTAS)
        ladrillos(Rect(0f, 0f, w, h), u, 51)
        drawRect(Brush.verticalGradient(listOf(NOCHE.copy(alpha = 0.4f), NOCHE.copy(alpha = 0.75f)), 0f, h), Offset.Zero, size)
        // La lámpara de sodio que cuelga sobre el tablón, con su halo.
        mancha(Offset(w / 2, h * 0.05f), w * 0.42f, e.secundario.copy(alpha = 0.28f))
        val lam = Rect(w * 0.4f, 0f, w * 0.6f, h * 0.04f)
        drawPath(Path().apply { moveTo(lam.left + 2f * u, lam.top); lineTo(lam.right - 2f * u, lam.top); lineTo(lam.right, lam.bottom); lineTo(lam.left, lam.bottom); close() },
            Brush.verticalGradient(listOf(ACERO_OSC, ACERO_LUZ), lam.top, lam.bottom))
        drawRect(e.secundario, Offset(lam.left + 1.5f * u, lam.bottom - 0.2f * u), Size(lam.width - 3f * u, 0.9f * u))
        drawRect(Color.White.copy(alpha = 0.6f), Offset(lam.left + 3f * u, lam.bottom), Size(lam.width - 6f * u, 0.4f * u))
        // El marco de acero con sus pernos, el tubo magenta de arriba y las calcomanías.
        val marco = Rect(w * 0.04f, h * 0.085f, w * 0.96f, h * 0.905f)
        drawRect(NOCHE_OSC.copy(alpha = 0.6f), Offset(marco.left + 0.8f * u, marco.top + 1.2f * u), marco.size)
        pieza(marco, ACERO, ACERO_BRILLO, ACERO_OSC, 0.6f * u, 0.8f * u)
        drawRect(Brush.verticalGradient(listOf(e.secundario.copy(alpha = 0.2f), Color.Transparent), marco.top, n.top), marco.topLeft, Size(marco.width, n.top - marco.top))
        listOf(marco.topLeft, Offset(marco.right, marco.top), Offset(marco.left, marco.bottom), marco.bottomRight, Offset(marco.left, marco.center.y), Offset(marco.right, marco.center.y)).forEach { p ->
            remache(Offset(p.x + (if (p.x < w / 2) 2f else -2f) * u, p.y + (if (p.y < marco.center.y - 1f) 2f else if (p.y > marco.center.y + 1f) -2f else 0f) * u), 0.8f * u)
        }
        neon(linea(Offset(n.left + 4f * u, (marco.top + n.top) / 2), Offset(n.right - 4f * u, (marco.top + n.top) / 2)), e.acento, 0.6f * u)
        listOf(Triple(0.055f, 0.3f, e.acento), Triple(0.94f, 0.55f, CIAN), Triple(0.052f, 0.7f, SODIO), Triple(0.945f, 0.2f, e.secundario)).forEachIndexed { i, (fx, fy, c) ->
            val p = Offset(w * fx, h * fy)
            rotate(-15f + 30f * azar(61, i), p) {
                drawRoundRect(mez(c, NOCHE, 0.35f), Offset(p.x - 1.6f * u, p.y - 2.4f * u), Size(3.2f * u, 4.8f * u), CornerRadius(0.6f * u))
                drawLine(Color.White.copy(alpha = 0.35f), Offset(p.x - 0.8f * u, p.y - 1.2f * u), Offset(p.x + 0.8f * u, p.y - 1.2f * u), 0.3f * u)
            }
        }
        // El tablón (el nicho): la madera oscura con restos de volantes viejos, grapas y la luz de la lámpara arriba.
        drawRect(NOCHE_OSC, Offset(n.left - 0.8f * u, n.top - 0.8f * u), Size(n.width + 1.6f * u, n.height + 1.6f * u))
        drawRect(TABLON, n.topLeft, n.size)
        clipRect(n.left, n.top, n.right, n.bottom) {
            var y = n.top + 6f * u; var k = 0
            while (y < n.bottom && k < 30) { drawLine(NOCHE_OSC.copy(alpha = 0.35f), Offset(n.left, y), Offset(n.right, y), 0.25f * u); y += 9f * u; k++ }
            for (j in 0 until 7) {
                val p = Offset(n.left + n.width * azar(71, j), n.top + n.height * azar(72, j))
                rotate(-12f + 24f * azar(73, j), p) { drawRect(TICKET.copy(alpha = 0.035f), Offset(p.x - 7f * u, p.y - 5f * u), Size(14f * u, 10f * u)) }
            }
            for (j in 0 until 16) {
                val p = Offset(n.left + n.width * azar(81, j), n.top + n.height * azar(82, j))
                drawLine(CROMO.copy(alpha = 0.3f), p, Offset(p.x + 1.4f * u, p.y + 0.2f * u), 0.3f * u)
            }
            drawRect(Brush.verticalGradient(listOf(e.secundario.copy(alpha = 0.07f), Color.Transparent), n.top, n.top + n.height * 0.5f), n.topLeft, Size(n.width, n.height * 0.5f))
            drawRect(Brush.verticalGradient(listOf(NOCHE_OSC.copy(alpha = 0.6f), Color.Transparent), n.top, n.top + 2f * u), n.topLeft, Size(n.width, 2f * u))
        }
        // La canaleta de abajo, con sus gotas, y el charco que refleja el tubo.
        val can = Rect(w * 0.03f, h * 0.915f, w * 0.97f, h * 0.945f)
        pieza(can, ACERO_LUZ, ACERO_BRILLO, ACERO_OSC, 0.4f * u, 0.4f * u)
        listOf(0.22f, 0.57f, 0.81f).forEach { f -> drawOval(LLUVIA.copy(alpha = 0.45f), Offset(w * f - 0.4f * u, can.bottom + 0.4f * u), Size(0.8f * u, 1.3f * u)) }
        drawRect(NOCHE_OSC, Offset(0f, h * 0.96f), Size(w, h * 0.04f))
        charco(Offset(w * 0.5f, h * 0.98f), w * 0.38f, 0.7f * u, e.acento)
    }

    /** El color impreso de cada tipo de nota (la franja, la cinta y el signo). */
    private fun tinta(tipo: String): Color = when (tipo) {
        "idea" -> Color(0xFFD02A7E)
        "pensamiento" -> Color(0xFF1E8DB0)
        "proposito" -> Color(0xFF3E962A)
        "recordatorio" -> Color(0xFFD0870E)
        else -> Color(0xFF8A7A88)
    }

    override fun DrawScope.notaTablero(e: EstadoNota, oscuro: Boolean) {
        val r = e.rect
        if (r.width < 1f || r.height < 1f) return
        val k = minOf(r.width, r.height) / 100f
        val f = Rect(r.left + 2.5f * k, r.top + 4f * k, r.right - 2.5f * k, r.bottom - 1.5f * k)
        // El ticket térmico: el borde de abajo, dentado por el corte de la impresora.
        fun silueta(dx: Float, dy: Float) = Path().apply {
            moveTo(f.left + dx, f.top + dy); lineTo(f.right + dx, f.top + dy); lineTo(f.right + dx, f.bottom - 2.4f * k + dy)
            val dientes = 9; val dw = f.width / dientes
            for (j in 0 until dientes) {
                val x0 = f.right - dw * j
                lineTo(x0 - dw / 2 + dx, f.bottom + dy); lineTo(x0 - dw + dx, f.bottom - 2.4f * k + dy)
            }
            close()
        }
        val col = tinta(e.tipo)
        val papel = silueta(0f, 0f)
        if (e.fijada) drawPath(papel, e.acento.copy(alpha = 0.4f), style = Stroke(3.4f * k, join = StrokeJoin.Round))
        drawPath(silueta(1.4f * k, 2.4f * k), NOCHE_OSC.copy(alpha = 0.7f))
        drawPath(papel, TICKET)
        clipPath(papel) {
            drawRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.25f), Color.Transparent, TICKET_SOMBRA.copy(alpha = 0.6f)), f.top, f.bottom), f.topLeft, f.size)
            drawRect(col, f.topLeft, Size(2f * k, f.height))
            // La línea punteada del corte de arriba.
            drawLine(TINTA.copy(alpha = 0.3f), Offset(f.left + 5f * k, f.top + 7.5f * k), Offset(f.right - 4f * k, f.top + 7.5f * k), 0.6f * k,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.2f * k, 1.2f * k)))
            if (e.tipo == "recordatorio") {
                // El código de barras.
                var bx = f.left + 9f * k; var j = 0
                while (bx < f.left + f.width * 0.6f && j < 40) {
                    val bw = (0.5f + 1.3f * azar(e.indice + 90, j)) * k
                    drawRect(TINTA.copy(alpha = 0.8f), Offset(bx, f.bottom - 11f * k), Size(bw, 5.5f * k))
                    bx += bw + 0.9f * k; j++
                }
            } else {
                drawLine(TINTA.copy(alpha = 0.3f), Offset(f.left + 9f * k, f.bottom - 8f * k), Offset(f.left + f.width * 0.55f, f.bottom - 8f * k), 0.6f * k,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(2.4f * k, 1f * k)))
            }
        }
        // El signo del tipo, impreso arriba a la derecha.
        val gc = Offset(f.right - 7f * k, f.top + 4f * k); val gr = 2.2f * k; val g = 0.9f * k
        when (e.tipo) {
            "idea" -> {
                drawPath(Path().apply {
                    for (j in 0 until 8) {
                        val a = Math.toRadians(-90.0 + j * 45.0); val rr = if (j % 2 == 0) gr else gr * 0.35f
                        val p = Offset(gc.x + (rr * cos(a)).toFloat(), gc.y + (rr * sin(a)).toFloat())
                        if (j == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
                    }
                    close()
                }, col)
            }
            "pensamiento" -> drawPath(Path().apply { moveTo(gc.x - gr * 1.3f, gc.y); cubicTo(gc.x - gr * 0.5f, gc.y - gr * 1.2f, gc.x + gr * 0.5f, gc.y + gr * 1.2f, gc.x + gr * 1.3f, gc.y) }, col, style = Stroke(g, cap = StrokeCap.Round))
            "proposito" -> drawPath(Path().apply { moveTo(gc.x - gr, gc.y); lineTo(gc.x - gr * 0.2f, gc.y + gr * 0.8f); lineTo(gc.x + gr * 1.2f, gc.y - gr) }, col, style = Stroke(g, cap = StrokeCap.Round, join = StrokeJoin.Round))
            "recordatorio" -> {
                drawLine(col, Offset(gc.x, gc.y - gr), Offset(gc.x, gc.y + gr * 0.3f), g * 1.1f, StrokeCap.Round)
                drawCircle(col, g * 0.65f, Offset(gc.x, gc.y + gr * 0.95f))
            }
            else -> drawLine(col, Offset(gc.x - gr, gc.y), Offset(gc.x + gr, gc.y), g, StrokeCap.Round)
        }
        val sx = r.center.x
        if (e.fijada) {
            // La chincheta de neón, con su halo y su sombra, y el holograma en la esquina.
            val pc = Offset(sx, f.top + 2.5f * k); val pr = 4.4f * k
            mancha(pc, pr * 3f, e.acento.copy(alpha = 0.45f))
            drawCircle(NOCHE_OSC.copy(alpha = 0.6f), pr, Offset(pc.x + 1.2f * k, pc.y + 1.8f * k))
            drawCircle(Brush.radialGradient(listOf(mez(e.acento, Color.White, 0.6f), e.acento, mez(e.acento, NOCHE, 0.5f)), Offset(pc.x - pr * 0.35f, pc.y - pr * 0.35f), pr * 1.6f), pr, pc)
            drawCircle(Color.White.copy(alpha = 0.75f), pr * 0.28f, Offset(pc.x - pr * 0.38f, pc.y - pr * 0.38f))
            val hc = Offset(f.right - 7f * k, f.bottom - 9f * k)
            drawCircle(Brush.sweepGradient(listOf(CIAN, e.acento, Color(0xFFFFB238), SODIO, CIAN), hc), 3.6f * k, hc)
            drawCircle(Color.White.copy(alpha = 0.5f), 3.6f * k, hc, style = Stroke(0.4f * k))
        } else {
            // La cinta adhesiva, translúcida, del color del tipo.
            rotate(-4f + 8f * azar(e.indice, 5), Offset(sx, f.top)) {
                val cinta = Rect(sx - 11f * k, f.top - 3.4f * k, sx + 11f * k, f.top + 3.6f * k)
                drawRect(col.copy(alpha = 0.55f), cinta.topLeft, cinta.size)
                drawRect(Color.White.copy(alpha = 0.18f), cinta.topLeft, Size(cinta.width, cinta.height * 0.35f))
                drawLine(Color.White.copy(alpha = 0.25f), Offset(cinta.left, cinta.top), Offset(cinta.left, cinta.bottom), 0.5f * k,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.8f * k, 0.6f * k)))
                drawLine(Color.White.copy(alpha = 0.25f), Offset(cinta.right, cinta.top), Offset(cinta.right, cinta.bottom), 0.5f * k,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.8f * k, 0.6f * k)))
            }
        }
    }

    override fun tintaNota(oscuro: Boolean): Color = TINTA
}
