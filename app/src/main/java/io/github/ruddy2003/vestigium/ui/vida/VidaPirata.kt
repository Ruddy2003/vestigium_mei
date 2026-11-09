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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.lerp
import java.util.Random
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

/**
 * Pirata: Vida en el camarote del capitán, con el roble oscuro, la brea, la lona, el cabo, el latón y el pergamino húmedo
 * de su Ideario (la bodega de los cofres), sus estantes (la estantería del camarote) y su salón de logros.
 * - Social: el camarote de popa en la vista de los juegos de mesa (la pared de frente, la cubierta desde arriba). El bao con
 *   sus curvas, las cuatro ventanas de popa con el mar (de noche, la luna, su reflejo y las estrellas), el farol que cuelga
 *   del bao (encendido de noche, con su halo), el banco corrido con sus cojines; en la cubierta calafateada, la mesa del
 *   capitán con su barandilla de mar y sus cantoneras, y la carta náutica extendida (el nicho); las sillas de marinero, los dos
 *   sillones de cuero claveteado en las cabeceras, el barril, la pirámide de balas, el cofre y la adujada de cabo.
 * - Mapas: la mesa de derrota. El tablero de roble con su barandilla de mar, la carta (el nicho) y, alrededor, el sextante,
 *   el compás de puntas, el tintero con su pluma, la brújula en su caja, la ampolleta, la carta enrollada y el catalejo; el canto con su cabo y sus
 *   argollas, el faldón con dos cajones de cartas y las patas torneadas trincadas a la cubierta.
 * - Otras vidas: el armario de trofeos del capitán. El timón sobre la cornisa de roble con su moldura de cabo y sus
 *   tachuelas, dos faroles en las esquinas, cinco hornacinas de arco forradas de lona (el nicho, partido en cinco), las
 *   pilastras con su cabo torcido, capitel y basa de latón y la cajonera con sus tiradores.
 * - Íconos: el faro sobre las rocas con su haz, el mapa del tesoro que un doblón rasca, los gallardetes en las islas.
 * - Notas: el mamparo del camarote. La tabla de nombre con la rosa de los vientos y dos faroles, el marco de roble con
 *   sus cantoneras de latón y su cabo, el paño de vela cosido (el nicho) y la repisa con el catalejo. Las notas son
 *   pergamino de borde roto, hoja de bitácora, retazo de carta náutica y etiqueta de lona, clavadas con un clavo de forja;
 *   las fijadas, con tachuela de latón y su sello de lacre con el ancla.
 */
internal object VidaPirata : DisenoVida {
    private val ROBLE = Color(0xFF5A3D28)
    private val ROBLE_LUZ = Color(0xFF7C583B)
    private val ROBLE_OSC = Color(0xFF2E1E14)
    private val VETA = Color(0xFF1C120A)
    private val TECA = Color(0xFF8E6E4A)
    private val TECA_OSC = Color(0xFF5E4630)
    private val BREA = Color(0xFF1E1A16)
    private val LATON = Color(0xFFC09A48)
    private val LATON_LUZ = Color(0xFFE6CC86)
    private val LATON_OSC = Color(0xFF7E6230)
    private val HIERRO = Color(0xFF2A2826)
    private val HIERRO_LUZ = Color(0xFF6A665E)
    private val PERGAMINO = Color(0xFFE6DCC0)
    private val PERGAMINO_OSC = Color(0xFFB89E70)
    private val LONA = Color(0xFFDCCFAE)
    private val CABO = Color(0xFFBCA474)
    private val CABO_OSC = Color(0xFF7E6A44)
    private val LACRE = Color(0xFF8E2A20)
    private val LLAMA = Color(0xFFF7C45A)
    private val LLAMA_CENTRO = Color(0xFFFFF2C4)
    private val CRISTAL = Color(0xFFEFE6C8)
    private val CIELO = Color(0xFFAFCBDD)
    private val CIELO_ALTO = Color(0xFF8DB2CC)
    private val NUBE = Color(0xFFEFF3F4)
    private val MAR = Color(0xFF3C6E8E)
    private val MAR_HONDO = Color(0xFF244E6A)
    private val ESPUMA = Color(0xFFE6EEF0)
    private val CIELO_NOCHE = Color(0xFF141C34)
    private val CIELO_NOCHE_BAJO = Color(0xFF243052)
    private val MAR_NOCHE = Color(0xFF0C1828)
    private val LUNA = Color(0xFFEDE8CF)
    private val VERDE_COFRE = Color(0xFF2E4A3A)
    private val CUERO = Color(0xFF6A3A24)
    private val PANO_ROJO = Color(0xFF7A2A22)
    private val ROCA = Color(0xFF6E665A)
    private val ORO = Color(0xFFD8B04A)
    private val NOCHE = Color(0xFF0C0A08)

    /** Los papeles de las notas, de día y a la luz del farol. */
    private val P_PERGAMINO = Color(0xFFEADFC2)
    private val P_BITACORA = Color(0xFFF2EAD6)
    private val P_CARTA = Color(0xFFDDE3CE)
    private val P_LONA = Color(0xFFE4D6B0)
    private val P_PERGAMINO_N = Color(0xFF3A3122)
    private val P_BITACORA_N = Color(0xFF35302A)
    private val P_CARTA_N = Color(0xFF2C3229)
    private val P_LONA_N = Color(0xFF3B3323)

    // ---------------------------------------------------------------------
    //  Ayudas
    // ---------------------------------------------------------------------

    private fun DrawScope.u() = size.minDimension / 100f

    private fun mezcla(a: Color, b: Color, f: Float) = lerp(a, b, f.coerceIn(0f, 1f))

    /** El color en modo oscuro: el camarote a la luz del farol, como sus estantes. */
    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, NOCHE, 0.36f) else c

    /** El latón se hunde menos: brilla a la luz del farol. */
    private fun tl(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, NOCHE, 0.18f) else c

    private fun azar(semilla: Int, k: Int): Float {
        var x = semilla * 374761393 + k * 668265263
        x = (x xor (x ushr 13)) * 1274126177
        x = x xor (x ushr 16)
        return (x and 0x7fffffff) / 2147483647f
    }

    private fun DrawScope.nicho(m: MuebleVida): Rect {
        val a = area(m)
        return Rect(size.width * a.left, size.height * a.top, size.width * a.right, size.height * a.bottom)
    }

    private fun DrawScope.sombraSuave(r: Rect, radio: Float, alcance: Float, fuerza: Float = 0.22f) {
        for (k in 4 downTo 1) {
            val e = alcance * k / 4f
            drawRoundRect(Color.Black.copy(alpha = fuerza / 4f), Offset(r.left - e * 0.3f, r.top + e * 0.6f), Size(r.width + e * 0.6f, r.height + e * 0.4f), CornerRadius(radio + e))
        }
    }

    private fun DrawScope.sombraInterior(r: Rect, ancho: Float, fuerza: Float, color: Color = Color.Black) {
        drawRect(Brush.verticalGradient(listOf(color.copy(alpha = fuerza), Color.Transparent), r.top, r.top + ancho), r.topLeft, Size(r.width, ancho))
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, color.copy(alpha = fuerza * 0.5f)), r.bottom - ancho, r.bottom), Offset(r.left, r.bottom - ancho), Size(r.width, ancho))
        drawRect(Brush.horizontalGradient(listOf(color.copy(alpha = fuerza * 0.7f), Color.Transparent), r.left, r.left + ancho), r.topLeft, Size(ancho, r.height))
        drawRect(Brush.horizontalGradient(listOf(Color.Transparent, color.copy(alpha = fuerza * 0.7f)), r.right - ancho, r.right), Offset(r.right - ancho, r.top), Size(ancho, r.height))
    }

    private fun DrawScope.halo(c: Offset, r: Float, color: Color, fuerza: Float) =
        drawCircle(Brush.radialGradient(listOf(color.copy(alpha = fuerza), color.copy(alpha = fuerza * 0.35f), Color.Transparent), c, r.coerceAtLeast(1f)), r, c)

    /** Una tachuela de latón (o un clavo de hierro) con su punto de luz. */
    private fun DrawScope.tachuela(p: Offset, r: Float, oscuro: Boolean, cuerpo: Color = LATON) {
        drawCircle(Color.Black.copy(alpha = 0.35f), r, Offset(p.x + r * 0.3f, p.y + r * 0.35f))
        drawCircle(tl(cuerpo, oscuro), r, p)
        drawCircle(Color.White.copy(alpha = if (oscuro) 0.22f else 0.45f), r * 0.42f, Offset(p.x - r * 0.3f, p.y - r * 0.3f))
    }

    /** Vetas de madera en [r]: líneas onduladas a lo largo. */
    private fun DrawScope.vetas(r: Rect, color: Color, semilla: Int, u: Float, vertical: Boolean = false, paso: Float = 1.3f) {
        val largo = if (vertical) r.height else r.width
        val ancho = if (vertical) r.width else r.height
        val n = (ancho / (paso * u)).toInt().coerceIn(2, 60)
        for (k in 0 until n) {
            val base = ancho * (k + azar(semilla, k)) / n
            val amp = (0.2f + 0.6f * azar(semilla, k + 100)) * u
            val fase = azar(semilla, k + 200) * 6.28f
            val ondas = 1f + 2f * azar(semilla, k + 300)
            val p = Path()
            for (s in 0..14) {
                val q = largo * s / 14f
                val v = base + amp * sin(fase + ondas * 6.28f * s / 14f)
                val x = if (vertical) r.left + v else r.left + q
                val y = if (vertical) r.top + q else r.top + v
                if (s == 0) p.moveTo(x, y) else p.lineTo(x, y)
            }
            drawPath(p, color.copy(alpha = color.alpha * (0.3f + 0.7f * azar(semilla, k + 400))), style = Stroke(u * (0.15f + 0.25f * azar(semilla, k + 500))))
        }
    }

    /** Una tabla de roble en [r], con su veta, un nudo y su bisel. */
    private fun DrawScope.tabla(r: Rect, oscuro: Boolean, u: Float, semilla: Int, vertical: Boolean = false, tono: Color = ROBLE, nudo: Boolean = false) {
        if (r.width <= 0f || r.height <= 0f) return
        drawRect(t(tono, oscuro), r.topLeft, r.size)
        clipRect(r.left, r.top, r.right, r.bottom) {
            vetas(r, t(VETA, oscuro).copy(alpha = 0.45f), semilla, u, vertical)
            if (nudo) {
                val c = Offset(r.left + r.width * (0.2f + 0.6f * azar(semilla, 7)), r.top + r.height * (0.3f + 0.4f * azar(semilla, 8)))
                val rr = minOf(r.width, r.height) * 0.16f
                drawOval(t(VETA, oscuro).copy(alpha = 0.5f), Offset(c.x - rr * (if (vertical) 0.6f else 1.3f), c.y - rr * (if (vertical) 1.3f else 0.6f)), Size(rr * (if (vertical) 1.2f else 2.6f), rr * (if (vertical) 2.6f else 1.2f)))
                drawOval(t(VETA, oscuro).copy(alpha = 0.35f), Offset(c.x - rr * (if (vertical) 1f else 2f), c.y - rr * (if (vertical) 2f else 1f)), Size(rr * (if (vertical) 2f else 4f), rr * (if (vertical) 4f else 2f)), style = Stroke(0.25f * u))
            }
        }
        drawRect(Color.White.copy(alpha = if (oscuro) 0.05f else 0.1f), r.topLeft, if (vertical) Size(0.4f * u, r.height) else Size(r.width, 0.4f * u))
        if (vertical) drawRect(Color.Black.copy(alpha = 0.3f), Offset(r.right - 0.4f * u, r.top), Size(0.4f * u, r.height))
        else drawRect(Color.Black.copy(alpha = 0.3f), Offset(r.left, r.bottom - 0.4f * u), Size(r.width, 0.4f * u))
    }

    /** La tablazón: tablas en hilera, con sus juntas calafateadas de brea y los tarugos de las cabezas. */
    private fun DrawScope.tablazon(r: Rect, oscuro: Boolean, u: Float, semilla: Int, ancho: Float, vertical: Boolean, tono: Color = ROBLE, tonoOsc: Color = ROBLE_OSC) {
        drawRect(t(tonoOsc, oscuro), r.topLeft, r.size)
        val rnd = Random(semilla.toLong())
        clipRect(r.left, r.top, r.right, r.bottom) {
            var p = if (vertical) r.left else r.top
            var fila = 0
            val fin = if (vertical) r.right else r.bottom
            val largoTotal = if (vertical) r.height else r.width
            while (p < fin) {
                var q = -largoTotal * 0.6f * azar(semilla, fila)
                while (q < largoTotal) {
                    val largo = largoTotal * (0.45f + 0.5f * rnd.nextFloat())
                    val tb = if (vertical) Rect(p + 0.2f * u, r.top + q, p + ancho - 0.2f * u, r.top + q + largo - 0.3f * u)
                    else Rect(r.left + q, p + 0.2f * u, r.left + q + largo - 0.3f * u, p + ancho - 0.2f * u)
                    drawRect(t(mezcla(tono, tonoOsc, 0.35f * rnd.nextFloat()), oscuro), tb.topLeft, tb.size)
                    // Los dos tarugos de la cabeza de la tabla.
                    val tar = t(mezcla(tono, Color.Black, 0.4f), oscuro)
                    if (vertical) {
                        drawCircle(tar, 0.35f * u, Offset(tb.left + ancho * 0.3f, tb.top + 1f * u)); drawCircle(tar, 0.35f * u, Offset(tb.right - ancho * 0.3f, tb.top + 1f * u))
                    } else {
                        drawCircle(tar, 0.35f * u, Offset(tb.left + 1f * u, tb.top + ancho * 0.3f)); drawCircle(tar, 0.35f * u, Offset(tb.left + 1f * u, tb.bottom - ancho * 0.3f))
                    }
                    q += largo
                }
                val franja = if (vertical) Rect(p, r.top, p + ancho, r.bottom) else Rect(r.left, p, r.right, p + ancho)
                vetas(franja, t(VETA, oscuro).copy(alpha = 0.35f), semilla + fila * 7, u, vertical, paso = 1.2f)
                // La junta de brea.
                if (vertical) drawRect(t(BREA, oscuro), Offset(p, r.top), Size(0.35f * u, r.height))
                else drawRect(t(BREA, oscuro), Offset(r.left, p), Size(r.width, 0.35f * u))
                p += ancho; fila++
            }
        }
    }

    /** Una moldura de latón horizontal en [r]. */
    private fun DrawScope.laton(r: Rect, oscuro: Boolean) =
        drawRect(Brush.verticalGradient(listOf(tl(LATON_LUZ, oscuro), tl(LATON, oscuro), tl(LATON_OSC, oscuro)), r.top, r.bottom), r.topLeft, r.size)

    /** Un cabo recto de [a] a [b], de grosor [g], con su torcido. */
    private fun DrawScope.cabo(a: Offset, b: Offset, g: Float, oscuro: Boolean, sombra: Boolean = true) {
        val l = hypot(b.x - a.x, b.y - a.y)
        if (l < 1f) return
        val d = Offset((b.x - a.x) / l, (b.y - a.y) / l)
        val nr = Offset(-d.y, d.x)
        if (sombra) drawLine(Color.Black.copy(alpha = 0.3f), Offset(a.x + g * 0.3f, a.y + g * 0.5f), Offset(b.x + g * 0.3f, b.y + g * 0.5f), g, StrokeCap.Round)
        drawLine(t(CABO_OSC, oscuro), a, b, g, StrokeCap.Round)
        drawLine(t(CABO, oscuro), a, b, g * 0.66f, StrokeCap.Round)
        val n = (l / (g * 0.75f)).toInt().coerceIn(1, 400)
        for (k in 0..n) {
            val p = Offset(a.x + d.x * l * k / n, a.y + d.y * l * k / n)
            drawLine(t(CABO_OSC, oscuro), Offset(p.x - nr.x * g * 0.42f - d.x * g * 0.22f, p.y - nr.y * g * 0.42f - d.y * g * 0.22f),
                Offset(p.x + nr.x * g * 0.42f + d.x * g * 0.22f, p.y + nr.y * g * 0.42f + d.y * g * 0.22f), g * 0.16f)
        }
    }

    /** Un cabo que cuelga en curva de [p0] a [p1] pasando cerca de [ctrl]. */
    private fun DrawScope.caboCurvo(p0: Offset, ctrl: Offset, p1: Offset, g: Float, oscuro: Boolean) {
        val n = 18
        var prev = p0
        for (k in 1..n) {
            val s = k / n.toFloat()
            val x = (1 - s) * (1 - s) * p0.x + 2 * (1 - s) * s * ctrl.x + s * s * p1.x
            val y = (1 - s) * (1 - s) * p0.y + 2 * (1 - s) * s * ctrl.y + s * s * p1.y
            val p = Offset(x, y)
            cabo(prev, p, g, oscuro, sombra = false)
            prev = p
        }
    }

    /** Un rectángulo bordeado de cabo, por fuera de [r]. */
    private fun DrawScope.marcoCabo(r: Rect, g: Float, oscuro: Boolean) {
        val m = g * 0.5f
        cabo(Offset(r.left - m, r.top - m), Offset(r.right + m, r.top - m), g, oscuro)
        cabo(Offset(r.left - m, r.bottom + m), Offset(r.right + m, r.bottom + m), g, oscuro)
        cabo(Offset(r.left - m, r.top - m), Offset(r.left - m, r.bottom + m), g, oscuro)
        cabo(Offset(r.right + m, r.top - m), Offset(r.right + m, r.bottom + m), g, oscuro)
    }

    /** Una cantonera de latón en L en la esquina [p], hacia dentro según [sx] y [sy]. */
    private fun DrawScope.cantonera(p: Offset, lado: Float, sx: Float, sy: Float, oscuro: Boolean, u: Float) {
        val path = Path().apply {
            moveTo(p.x, p.y); lineTo(p.x + sx * lado, p.y); lineTo(p.x + sx * lado * 0.75f, p.y + sy * lado * 0.32f)
            lineTo(p.x + sx * lado * 0.32f, p.y + sy * lado * 0.32f); lineTo(p.x + sx * lado * 0.32f, p.y + sy * lado * 0.75f); lineTo(p.x, p.y + sy * lado); close()
        }
        drawPath(Path().apply { addPath(path, Offset(0.4f * u, 0.6f * u)) }, Color.Black.copy(alpha = 0.3f))
        drawPath(path, Brush.linearGradient(listOf(tl(LATON_LUZ, oscuro), tl(LATON, oscuro), tl(LATON_OSC, oscuro)), p, Offset(p.x + sx * lado, p.y + sy * lado)))
        drawPath(path, tl(LATON_OSC, oscuro), style = Stroke(0.25f * u))
        drawCircle(tl(LATON_OSC, oscuro), lado * 0.07f, Offset(p.x + sx * lado * 0.16f, p.y + sy * lado * 0.16f))
    }

    /** Un farol de barco que cuelga por su argolla: el sombrerete, el vidrio con sus barrotes y la llama, con su halo. */
    private fun DrawScope.farol(c: Offset, alto: Float, oscuro: Boolean, u: Float, colgado: Float = 0f) {
        val k = alto / 10f
        halo(Offset(c.x, c.y + 0.5f * k), alto * (if (oscuro) 3.4f else 1.8f), LLAMA, if (oscuro) 0.42f else 0.14f)
        if (colgado > 0f) drawLine(t(HIERRO, oscuro), Offset(c.x, c.y - 5.6f * k - colgado), Offset(c.x, c.y - 5.6f * k), 0.5f * k)
        drawCircle(tl(LATON_OSC, oscuro), 0.9f * k, Offset(c.x, c.y - 5.6f * k), style = Stroke(0.4f * k))
        val cap = Path().apply { moveTo(c.x - 3.2f * k, c.y - 3.4f * k); lineTo(c.x - 1.2f * k, c.y - 5f * k); lineTo(c.x + 1.2f * k, c.y - 5f * k); lineTo(c.x + 3.2f * k, c.y - 3.4f * k); close() }
        drawPath(cap, Brush.horizontalGradient(listOf(tl(LATON_LUZ, oscuro), tl(LATON, oscuro), tl(LATON_OSC, oscuro)), c.x - 3.2f * k, c.x + 3.2f * k))
        val cuerpo = Rect(c.x - 2.6f * k, c.y - 3.4f * k, c.x + 2.6f * k, c.y + 3f * k)
        drawRect(if (oscuro) LLAMA.copy(alpha = 0.92f) else CRISTAL.copy(alpha = 0.95f), cuerpo.topLeft, cuerpo.size)
        if (oscuro) drawRect(Brush.radialGradient(listOf(LLAMA_CENTRO, LLAMA, mezcla(LLAMA, Color(0xFF9A5A1A), 0.5f)), cuerpo.center, cuerpo.height * 0.7f), cuerpo.topLeft, cuerpo.size)
        drawPath(Path().apply { moveTo(c.x, c.y + 1.4f * k); quadraticTo(c.x - 1.2f * k, c.y - 0.3f * k, c.x, c.y - 2f * k); quadraticTo(c.x + 1.2f * k, c.y - 0.3f * k, c.x, c.y + 1.4f * k) },
            if (oscuro) LLAMA_CENTRO else LLAMA)
        listOf(-2.6f, -0.9f, 0.9f, 2.6f).forEach { dx -> drawLine(tl(LATON_OSC, oscuro), Offset(c.x + dx * k, cuerpo.top), Offset(c.x + dx * k, cuerpo.bottom), 0.4f * k) }
        drawLine(Color.White.copy(alpha = if (oscuro) 0.3f else 0.6f), Offset(c.x - 1.8f * k, cuerpo.top + 0.6f * k), Offset(c.x - 1.8f * k, cuerpo.bottom - 1.2f * k), 0.35f * k)
        drawRect(Brush.horizontalGradient(listOf(tl(LATON_LUZ, oscuro), tl(LATON_OSC, oscuro)), c.x - 3f * k, c.x + 3f * k), Offset(c.x - 3f * k, cuerpo.bottom), Size(6f * k, 1.3f * k))
        drawPath(Path().apply { moveTo(c.x - 1.6f * k, cuerpo.bottom + 1.3f * k); lineTo(c.x + 1.6f * k, cuerpo.bottom + 1.3f * k); lineTo(c.x, cuerpo.bottom + 2.4f * k); close() }, tl(LATON_OSC, oscuro))
    }

    /** Una ventana de popa en [r]: el cielo y el mar (de noche, las estrellas y la luna), sus parteluces y el brillo del vidrio. */
    private fun DrawScope.ventana(r: Rect, oscuro: Boolean, u: Float, semilla: Int, luna: Boolean) {
        val horizonte = r.top + r.height * 0.52f
        clipRect(r.left, r.top, r.right, r.bottom) {
            if (!oscuro) {
                drawRect(Brush.verticalGradient(listOf(CIELO_ALTO, CIELO), r.top, horizonte), r.topLeft, Size(r.width, horizonte - r.top))
                val nx = r.left + r.width * (0.2f + 0.5f * azar(semilla, 1)); val ny = r.top + r.height * 0.2f
                drawOval(NUBE, Offset(nx, ny), Size(r.width * 0.36f, r.height * 0.1f))
                drawOval(NUBE, Offset(nx + r.width * 0.08f, ny - r.height * 0.05f), Size(r.width * 0.2f, r.height * 0.1f))
                drawRect(Brush.verticalGradient(listOf(MAR, MAR_HONDO), horizonte, r.bottom), Offset(r.left, horizonte), Size(r.width, r.bottom - horizonte))
                for (j in 0 until 4) {
                    val yy = horizonte + (r.bottom - horizonte) * (0.2f + 0.22f * j)
                    var xx = r.left + r.width * azar(semilla, 10 + j) * 0.3f
                    while (xx < r.right) { drawLine(ESPUMA.copy(alpha = 0.55f), Offset(xx, yy), Offset(xx + 1.6f * u, yy), 0.3f * u, StrokeCap.Round); xx += 4.2f * u }
                }
            } else {
                drawRect(Brush.verticalGradient(listOf(CIELO_NOCHE, CIELO_NOCHE_BAJO), r.top, horizonte), r.topLeft, Size(r.width, horizonte - r.top))
                for (j in 0 until 6) drawCircle(LUNA.copy(alpha = 0.5f + 0.4f * azar(semilla, 40 + j)), 0.22f * u, Offset(r.left + r.width * azar(semilla, 20 + j), r.top + (horizonte - r.top) * azar(semilla, 30 + j) * 0.9f))
                drawRect(MAR_NOCHE, Offset(r.left, horizonte), Size(r.width, r.bottom - horizonte))
                if (luna) {
                    val lc = Offset(r.left + r.width * 0.62f, r.top + r.height * 0.24f)
                    halo(lc, r.height * 0.3f, LUNA, 0.35f)
                    drawCircle(LUNA, r.height * 0.09f, lc)
                    drawCircle(CIELO_NOCHE, r.height * 0.08f, Offset(lc.x + r.height * 0.045f, lc.y - r.height * 0.02f))
                    for (j in 0 until 5) {
                        val yy = horizonte + (r.bottom - horizonte) * (0.12f + 0.18f * j)
                        val ww = r.width * (0.22f - 0.03f * j)
                        drawLine(LUNA.copy(alpha = 0.5f - 0.07f * j), Offset(lc.x - ww / 2, yy), Offset(lc.x + ww / 2, yy), 0.35f * u, StrokeCap.Round)
                    }
                } else {
                    for (j in 0 until 3) {
                        val yy = horizonte + (r.bottom - horizonte) * (0.25f + 0.25f * j)
                        drawLine(LUNA.copy(alpha = 0.12f), Offset(r.left + r.width * 0.1f, yy), Offset(r.left + r.width * 0.5f, yy), 0.3f * u)
                    }
                }
            }
            // Los parteluces y el brillo del vidrio.
            val barra = t(ROBLE_OSC, oscuro)
            drawLine(barra, Offset(r.center.x, r.top), Offset(r.center.x, r.bottom), 0.7f * u)
            drawLine(barra, Offset(r.left, r.top + r.height * 0.4f), Offset(r.right, r.top + r.height * 0.4f), 0.7f * u)
            drawLine(Color.White.copy(alpha = if (oscuro) 0.08f else 0.3f), Offset(r.left + r.width * 0.1f, r.bottom), Offset(r.left + r.width * 0.45f, r.top), 0.6f * u)
            drawLine(Color.White.copy(alpha = if (oscuro) 0.05f else 0.2f), Offset(r.left + r.width * 0.55f, r.bottom), Offset(r.left + r.width * 0.9f, r.top), 0.4f * u)
        }
        drawRect(t(ROBLE_OSC, oscuro), r.topLeft, r.size, style = Stroke(0.6f * u))
    }

    /** La rosa de los vientos: ocho puntas partidas en luz y sombra, sus anillos y la flor de lis del norte. */
    private fun DrawScope.rosa(c: Offset, r: Float, oscuro: Boolean, acento: Color, secundario: Color, u: Float, aros: Boolean = true) {
        if (aros) {
            drawCircle(t(PERGAMINO, oscuro), r * 1.02f, c)
            drawCircle(t(Color(0xFF3A2A18), oscuro), r * 1.02f, c, style = Stroke(r * 0.04f))
            drawCircle(t(Color(0xFF3A2A18), oscuro), r * 0.86f, c, style = Stroke(r * 0.02f))
            for (k in 0 until 32) {
                val a = Math.toRadians(k * 11.25)
                val r0 = r * (if (k % 4 == 0) 0.86f else 0.92f)
                drawLine(t(Color(0xFF3A2A18), oscuro), Offset(c.x + r0 * cos(a).toFloat(), c.y + r0 * sin(a).toFloat()), Offset(c.x + r * cos(a).toFloat(), c.y + r * sin(a).toFloat()), r * 0.025f)
            }
        }
        for (k in 0 until 8) {
            val principal = k % 2 == 0
            val largo = r * (if (principal) 0.82f else 0.55f)
            val anchoP = r * (if (principal) 0.13f else 0.1f)
            val a = Math.toRadians(-90.0 + k * 45.0)
            val p = Offset(c.x + largo * cos(a).toFloat(), c.y + largo * sin(a).toFloat())
            val izq = Offset(c.x + anchoP * cos(a - Math.PI / 2).toFloat(), c.y + anchoP * sin(a - Math.PI / 2).toFloat())
            val der = Offset(c.x + anchoP * cos(a + Math.PI / 2).toFloat(), c.y + anchoP * sin(a + Math.PI / 2).toFloat())
            val luz = if (principal) mezcla(acento, Color.White, 0.15f) else mezcla(secundario, Color.White, 0.2f)
            val sombra = if (principal) mezcla(acento, Color.Black, 0.35f) else mezcla(secundario, Color.Black, 0.3f)
            drawPath(Path().apply { moveTo(c.x, c.y); lineTo(izq.x, izq.y); lineTo(p.x, p.y); close() }, luz)
            drawPath(Path().apply { moveTo(c.x, c.y); lineTo(der.x, der.y); lineTo(p.x, p.y); close() }, sombra)
        }
        // La flor de lis del norte.
        val n = Offset(c.x, c.y - r * 0.82f)
        drawPath(Path().apply { moveTo(n.x, n.y - r * 0.16f); quadraticTo(n.x + r * 0.1f, n.y - r * 0.04f, n.x, n.y + r * 0.04f); quadraticTo(n.x - r * 0.1f, n.y - r * 0.04f, n.x, n.y - r * 0.16f) }, acento)
        drawCircle(tl(LATON, oscuro), r * 0.08f, c)
        drawCircle(tl(LATON_OSC, oscuro), r * 0.08f, c, style = Stroke(r * 0.02f))
    }

    /** El timón: la rueda de roble con sus tachuelas, el cubo de latón y ocho cabillas con sus puños. */
    private fun DrawScope.timon(c: Offset, r: Float, oscuro: Boolean, u: Float) {
        val madera = t(ROBLE_LUZ, oscuro)
        for (k in 0 until 8) {
            val a = Math.toRadians(-90.0 + k * 45.0)
            val p1 = Offset(c.x + r * 1.28f * cos(a).toFloat(), c.y + r * 1.28f * sin(a).toFloat())
            drawLine(Color.Black.copy(alpha = 0.35f), Offset(c.x + 0.5f * u, c.y + 0.8f * u), Offset(p1.x + 0.5f * u, p1.y + 0.8f * u), r * 0.11f, StrokeCap.Round)
            drawLine(madera, c, p1, r * 0.1f, StrokeCap.Round)
            val puno = Offset(c.x + r * 1.36f * cos(a).toFloat(), c.y + r * 1.36f * sin(a).toFloat())
            drawCircle(t(ROBLE, oscuro), r * 0.1f, puno)
            drawCircle(Color.White.copy(alpha = 0.15f), r * 0.04f, Offset(puno.x - r * 0.03f, puno.y - r * 0.03f))
        }
        drawCircle(Color.Black.copy(alpha = 0.35f), r, Offset(c.x + 0.6f * u, c.y + 0.9f * u), style = Stroke(r * 0.2f))
        drawCircle(t(ROBLE, oscuro), r, c, style = Stroke(r * 0.2f))
        drawCircle(t(ROBLE_LUZ, oscuro), r * 1.06f, c, style = Stroke(r * 0.05f))
        drawCircle(t(ROBLE_OSC, oscuro), r * 0.92f, c, style = Stroke(r * 0.04f))
        for (k in 0 until 8) {
            val a = Math.toRadians(-67.5 + k * 45.0)
            drawCircle(tl(LATON, oscuro), r * 0.04f, Offset(c.x + r * cos(a).toFloat(), c.y + r * sin(a).toFloat()))
        }
        drawCircle(Brush.radialGradient(listOf(tl(LATON_LUZ, oscuro), tl(LATON_OSC, oscuro)), Offset(c.x - r * 0.06f, c.y - r * 0.06f), r * 0.28f), r * 0.22f, c)
        drawCircle(t(ROBLE_OSC, oscuro), r * 0.08f, c)
    }

    /** El fondo sólido de la carta náutica (el nicho): pergamino húmedo, con sus rumbos y su filete, muy suaves. */
    private fun DrawScope.carta(a: Rect, e: EstadoMueble, oscuro: Boolean, u: Float) {
        val papel = if (oscuro) mezcla(e.fondo, Color(0xFF3A3020), 0.45f) else mezcla(e.fondo, PERGAMINO, 0.4f)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.22f), Offset(a.left + 0.4f * u, a.top + 0.7f * u), a.size)
        drawRect(papel, a.topLeft, a.size)
        clipRect(a.left, a.top, a.right, a.bottom) {
            val linea = (if (oscuro) Color(0xFFE6D6B0) else Color(0xFF5A4028)).copy(alpha = if (oscuro) 0.05f else 0.07f)
            listOf(Offset(a.left + a.width * 0.3f, a.top + a.height * 0.58f), Offset(a.left + a.width * 0.78f, a.top + a.height * 0.3f)).forEach { o ->
                for (k in 0 until 16) {
                    val ang = Math.toRadians(k * 22.5)
                    val l = a.width * 1.2f
                    drawLine(linea, o, Offset(o.x + l * cos(ang).toFloat(), o.y + l * sin(ang).toFloat()), 0.22f * u)
                }
            }
            // Las manchas de humedad en los bordes.
            val mancha = (if (oscuro) Color.Black else PERGAMINO_OSC).copy(alpha = if (oscuro) 0.12f else 0.16f)
            drawCircle(Brush.radialGradient(listOf(mancha, Color.Transparent), a.topLeft, a.height * 0.35f), a.height * 0.35f, a.topLeft)
            drawCircle(Brush.radialGradient(listOf(mancha, Color.Transparent), a.bottomRight, a.height * 0.4f), a.height * 0.4f, a.bottomRight)
        }
        drawRect(e.secundario.copy(alpha = if (oscuro) 0.35f else 0.4f), Offset(a.left + 1.2f * u, a.top + 1.2f * u), Size(a.width - 2.4f * u, a.height - 2.4f * u), style = Stroke(0.35f * u))
        drawRect(e.secundario.copy(alpha = if (oscuro) 0.22f else 0.25f), Offset(a.left + 1.9f * u, a.top + 1.9f * u), Size(a.width - 3.8f * u, a.height - 3.8f * u), style = Stroke(0.2f * u))
        sombraInterior(a, 2.4f * u, if (oscuro) 0.25f else 0.1f, if (oscuro) Color.Black else Color(0xFF6E5030))
    }

    // ---------------------------------------------------------------------
    //  Los nichos
    // ---------------------------------------------------------------------

    override fun area(m: MuebleVida): Rect = when (m) {
        MuebleVida.SOCIAL -> Rect(0.165f, 0.315f, 0.835f, 0.845f)
        MuebleVida.MAPAS -> Rect(0.17f, 0.085f, 0.83f, 0.72f)
        MuebleVida.OTRAS_VIDAS -> Rect(0.055f, 0.285f, 0.945f, 0.835f)
        MuebleVida.NOTAS -> Rect(0.08f, 0.17f, 0.92f, 0.875f)
    }

    // ---------------------------------------------------------------------
    //  Social: el camarote de popa
    // ---------------------------------------------------------------------

    /** Una silla de marinero vista desde arriba: el asiento de roble con su cojín de paño y el respaldo de husillos; mira hacia +y y gira [giro]. */
    private fun DrawScope.silla(c: Offset, ancho: Float, fondo: Float, giro: Float, pano: Color, oscuro: Boolean, u: Float, semilla: Int) {
        rotate(giro, c) {
            val asiento = Rect(c.x - ancho * 0.42f, c.y - fondo * 0.2f, c.x + ancho * 0.42f, c.y + fondo * 0.5f)
            drawRoundRect(Color.Black.copy(alpha = 0.38f), Offset(asiento.left + 0.7f * u, asiento.top + 1f * u), asiento.size, CornerRadius(1.4f * u))
            drawRoundRect(t(ROBLE, oscuro), asiento.topLeft, asiento.size, CornerRadius(1.4f * u))
            drawRoundRect(t(ROBLE_OSC, oscuro), asiento.topLeft, asiento.size, CornerRadius(1.4f * u), style = Stroke(0.3f * u))
            val cojin = Rect(asiento.left + 0.9f * u, asiento.top + 0.9f * u, asiento.right - 0.9f * u, asiento.bottom - 0.9f * u)
            drawRoundRect(t(pano, oscuro), cojin.topLeft, cojin.size, CornerRadius(1.1f * u))
            drawRoundRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.18f), Color.Transparent, Color.Black.copy(alpha = 0.25f)), cojin.top, cojin.bottom), cojin.topLeft, cojin.size, CornerRadius(1.1f * u))
            drawCircle(Color.Black.copy(alpha = 0.4f), 0.35f * u, cojin.center)
            // El respaldo: el travesaño curvo de roble y sus husillos.
            val resp = Rect(c.x - ancho * 0.5f, c.y - fondo * 0.5f, c.x + ancho * 0.5f, c.y + fondo * 0.1f)
            for (j in 0 until 4) {
                val x = c.x - ancho * 0.27f + ancho * 0.18f * j
                drawLine(t(ROBLE_OSC, oscuro), Offset(x, c.y - fondo * 0.36f), Offset(x, asiento.top + 0.3f * u), 0.7f * u)
            }
            drawArc(Color.Black.copy(alpha = 0.3f), 200f, 140f, false, Offset(resp.left + 0.6f * u, resp.top + 0.9f * u), resp.size, style = Stroke(1.5f * u, cap = StrokeCap.Round))
            drawArc(t(ROBLE_LUZ, oscuro), 200f, 140f, false, resp.topLeft, resp.size, style = Stroke(1.5f * u, cap = StrokeCap.Round))
            drawArc(Color.White.copy(alpha = if (oscuro) 0.06f else 0.2f), 215f, 60f, false, resp.topLeft, resp.size, style = Stroke(0.45f * u, cap = StrokeCap.Round))
            if (semilla % 3 == 0) drawCircle(tl(LATON, oscuro), 0.45f * u, Offset(c.x, resp.top))
        }
    }

    /** El sillón del capitán visto desde arriba: brazos y respaldo de roble, el cojín de cuero claveteado con latón. */
    private fun DrawScope.sillon(c: Offset, ancho: Float, fondo: Float, giro: Float, cuero: Color, oscuro: Boolean, u: Float) {
        rotate(giro, c) {
            val r = Rect(c.x - ancho / 2, c.y - fondo / 2, c.x + ancho / 2, c.y + fondo / 2)
            drawRoundRect(Color.Black.copy(alpha = 0.38f), Offset(r.left + 0.8f * u, r.top + 1.1f * u), r.size, CornerRadius(2f * u))
            drawRoundRect(t(ROBLE, oscuro), r.topLeft, r.size, CornerRadius(2f * u))
            clipPath(Path().apply { addRoundRect(RoundRect(r, CornerRadius(2f * u))) }) { vetas(r, t(VETA, oscuro).copy(alpha = 0.5f), 31, u) }
            val brazo = ancho * 0.16f
            val respaldo = fondo * 0.26f
            drawRoundRect(t(ROBLE_LUZ, oscuro), r.topLeft, Size(r.width, respaldo), CornerRadius(2f * u))
            drawRect(Color.Black.copy(alpha = 0.25f), Offset(r.left + brazo, r.top + respaldo - 0.4f * u), Size(r.width - 2 * brazo, 0.4f * u))
            listOf(r.left + brazo / 2, r.right - brazo / 2).forEach { bx ->
                drawCircle(t(ROBLE_LUZ, oscuro), brazo * 0.55f, Offset(bx, r.bottom - brazo * 0.55f))
                drawCircle(Color.White.copy(alpha = 0.12f), brazo * 0.25f, Offset(bx - brazo * 0.12f, r.bottom - brazo * 0.7f))
            }
            val cojin = Rect(r.left + brazo, r.top + respaldo, r.right - brazo, r.bottom - 0.6f * u)
            drawRoundRect(t(cuero, oscuro), cojin.topLeft, cojin.size, CornerRadius(1.4f * u))
            drawRoundRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.16f), Color.Black.copy(alpha = 0.24f)), cojin.top, cojin.bottom), cojin.topLeft, cojin.size, CornerRadius(1.4f * u))
            var x = cojin.left + 1f * u
            while (x < cojin.right - 0.5f * u) { drawCircle(tl(LATON, oscuro), 0.32f * u, Offset(x, cojin.top + 0.7f * u)); drawCircle(tl(LATON, oscuro), 0.32f * u, Offset(x, cojin.bottom - 0.7f * u)); x += 1.6f * u }
            drawCircle(Color.Black.copy(alpha = 0.35f), 0.4f * u, cojin.center)
        }
    }

    /** Un barril visto desde arriba: el aro de hierro, el canto de las duelas, la tapa de tablas y el tapón. */
    private fun DrawScope.barril(c: Offset, r: Float, oscuro: Boolean, u: Float) {
        drawCircle(Color.Black.copy(alpha = 0.38f), r, Offset(c.x + r * 0.1f, c.y + r * 0.14f))
        drawCircle(Brush.radialGradient(listOf(t(HIERRO_LUZ, oscuro), t(HIERRO, oscuro)), Offset(c.x - r * 0.3f, c.y - r * 0.3f), r * 1.2f), r, c)
        drawCircle(t(TECA_OSC, oscuro), r * 0.9f, c)
        for (k in 0 until 18) {
            val a = Math.toRadians(k * 20.0)
            drawLine(t(VETA, oscuro).copy(alpha = 0.6f), Offset(c.x + r * 0.76f * cos(a).toFloat(), c.y + r * 0.76f * sin(a).toFloat()), Offset(c.x + r * 0.9f * cos(a).toFloat(), c.y + r * 0.9f * sin(a).toFloat()), 0.25f * u)
        }
        val tapa = Path().apply { addOval(Rect(c.x - r * 0.76f, c.y - r * 0.76f, c.x + r * 0.76f, c.y + r * 0.76f)) }
        drawPath(tapa, t(TECA, oscuro))
        clipPath(tapa) {
            var y = c.y - r * 0.76f + r * 0.38f
            while (y < c.y + r * 0.76f) { drawLine(t(TECA_OSC, oscuro), Offset(c.x - r, y), Offset(c.x + r, y), 0.3f * u); y += r * 0.38f }
            vetas(Rect(c.x - r, c.y - r, c.x + r, c.y + r), t(VETA, oscuro).copy(alpha = 0.3f), 9, u, paso = 1.2f)
        }
        drawCircle(t(ROBLE_OSC, oscuro), r * 0.12f, Offset(c.x + r * 0.32f, c.y - r * 0.1f))
        drawCircle(t(TECA_OSC, oscuro), r * 0.12f, Offset(c.x + r * 0.32f, c.y - r * 0.1f), style = Stroke(0.3f * u))
        drawArc(Color.White.copy(alpha = if (oscuro) 0.08f else 0.22f), 190f, 80f, false, Offset(c.x - r * 0.94f, c.y - r * 0.94f), Size(r * 1.88f, r * 1.88f), style = Stroke(r * 0.07f))
    }

    /** La pirámide de balas de cañón vista desde arriba. */
    private fun DrawScope.balas(c: Offset, r: Float, oscuro: Boolean) {
        fun bala(p: Offset) {
            drawCircle(Color.Black.copy(alpha = 0.35f), r, Offset(p.x + r * 0.18f, p.y + r * 0.24f))
            drawCircle(Brush.radialGradient(listOf(t(HIERRO_LUZ, oscuro), t(HIERRO, oscuro), Color(0xFF141210)), Offset(p.x - r * 0.35f, p.y - r * 0.35f), r * 1.4f), r, p)
            drawCircle(Color.White.copy(alpha = if (oscuro) 0.12f else 0.25f), r * 0.22f, Offset(p.x - r * 0.4f, p.y - r * 0.4f))
        }
        for (i in -1..1) for (j in -1..1) bala(Offset(c.x + i * r * 2f, c.y + j * r * 2f))
        for (i in 0..1) for (j in 0..1) bala(Offset(c.x - r + i * r * 2f, c.y - r + j * r * 2f))
        bala(c)
    }

    /** Una adujada: el cabo enrollado en espiral, visto desde arriba, con su chicote. */
    private fun DrawScope.adujada(c: Offset, r: Float, oscuro: Boolean, u: Float) {
        drawCircle(Color.Black.copy(alpha = 0.32f), r, Offset(c.x + r * 0.08f, c.y + r * 0.12f))
        val g = r / 5.2f
        var rr = r - g / 2
        var k = 0
        while (rr > g * 0.9f) {
            drawCircle(t(CABO_OSC, oscuro), rr, c, style = Stroke(g))
            drawCircle(t(if (k % 2 == 0) CABO else mezcla(CABO, CABO_OSC, 0.25f), oscuro), rr, c, style = Stroke(g * 0.7f))
            drawCircle(t(CABO_OSC, oscuro), rr, c, style = Stroke(g * 0.7f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(g * 0.18f, g * 0.6f), k * g * 0.2f)))
            rr -= g; k++
        }
        drawCircle(t(TECA_OSC, oscuro), g * 0.8f, c)
        cabo(Offset(c.x + r * 0.9f, c.y + r * 0.3f), Offset(c.x + r * 1.25f, c.y + r * 0.95f), g * 0.85f, oscuro)
    }

    /** El cofre de marinero visto desde arriba: la tapa de tablas verdes, los flejes y cantoneras de latón, el candado. */
    private fun DrawScope.cofre(r: Rect, oscuro: Boolean, u: Float) {
        sombraSuave(r, 0.6f * u, 2f * u, if (oscuro) 0.5f else 0.3f)
        drawRect(t(VERDE_COFRE, oscuro), r.topLeft, r.size)
        var x = r.left + r.width / 4
        while (x < r.right - 1f) { drawLine(Color.Black.copy(alpha = 0.3f), Offset(x, r.top), Offset(x, r.bottom), 0.3f * u); x += r.width / 4 }
        drawRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.12f), Color.Transparent, Color.Black.copy(alpha = 0.2f)), r.top, r.bottom), r.topLeft, r.size)
        listOf(0.22f, 0.78f).forEach { f -> drawRect(tl(LATON, oscuro), Offset(r.left + r.width * f - 0.6f * u, r.top), Size(1.2f * u, r.height)) }
        val l = minOf(r.width, r.height) * 0.3f
        cantonera(r.topLeft, l, 1f, 1f, oscuro, u); cantonera(Offset(r.right, r.top), l, -1f, 1f, oscuro, u)
        cantonera(Offset(r.left, r.bottom), l, 1f, -1f, oscuro, u); cantonera(r.bottomRight, l, -1f, -1f, oscuro, u)
        val cand = Offset(r.center.x, r.bottom - 0.4f * u)
        drawRoundRect(tl(LATON_OSC, oscuro), Offset(cand.x - 1.2f * u, cand.y - 1.2f * u), Size(2.4f * u, 2.4f * u), CornerRadius(0.4f * u))
        drawCircle(t(HIERRO, oscuro), 0.35f * u, cand)
        drawRect(Color.Black.copy(alpha = 0.4f), r.topLeft, r.size, style = Stroke(0.3f * u))
    }

    override fun DrawScope.muebleSocial(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        if (w < 1f || h < 1f) return
        val a = nicho(MuebleVida.SOCIAL)
        val pared = h * 0.19f
        // La cubierta: tablones a lo largo, calafateados de brea.
        tablazon(Rect(0f, pared, w, h), oscuro, u, e.semilla, 3.6f * u, vertical = false, tono = TECA, tonoOsc = TECA_OSC)
        // La pared de popa: el bao con sus curvas, las ventanas entre montantes y el alféizar.
        val bao = Rect(0f, 0f, w, pared * 0.2f)
        tabla(bao, oscuro, u, e.semilla + 1, tono = ROBLE_OSC)
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(0f, bao.bottom), Size(w, 0.6f * u))
        val alto0 = bao.bottom; val alfeizar = pared * 0.84f
        drawRect(t(ROBLE, oscuro), Offset(0f, alto0), Size(w, alfeizar - alto0))
        val nv = 4
        val mont = w * 0.032f
        val margen = w * 0.035f
        val vw = (w - 2 * margen - (nv - 1) * mont) / nv
        for (i in 0 until nv) {
            val x = margen + i * (vw + mont)
            ventana(Rect(x, alto0 + 1.2f * u, x + vw, alfeizar - 0.6f * u), oscuro, u, e.semilla + i * 13, luna = i == 2)
        }
        // Los montantes de roble y las curvas bajo el bao.
        for (i in 0..nv) {
            val x0 = if (i == 0) 0f else margen + i * (vw + mont) - mont
            val x1 = if (i == 0) margen else if (i == nv) w else x0 + mont
            tabla(Rect(x0, alto0, x1, alfeizar), oscuro, u, e.semilla + 40 + i, vertical = true)
            listOf(-1f, 1f).forEach { s ->
                val bx = if (s < 0) x0 else x1
                if ((i == 0 && s < 0) || (i == nv && s > 0)) return@forEach
                drawPath(Path().apply { moveTo(bx, alto0); lineTo(bx - s * 2.6f * u, alto0); quadraticTo(bx - s * 0.4f * u, alto0 + 0.4f * u, bx, alto0 + 2.6f * u); close() }, t(ROBLE_LUZ, oscuro))
            }
        }
        val alf = Rect(0f, alfeizar, w, pared)
        drawRect(Brush.verticalGradient(listOf(t(ROBLE_LUZ, oscuro), t(ROBLE, oscuro), t(ROBLE_OSC, oscuro)), alf.top, alf.bottom), alf.topLeft, alf.size)
        laton(Rect(0f, alfeizar, w, alfeizar + 0.6f * u), oscuro)
        // El farol que cuelga del bao, delante del montante del centro.
        farol(Offset(w / 2 + mont * 0.0f, pared * 0.5f), pared * 0.36f, oscuro, u, colgado = pared * 0.12f)
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.45f), Color.Transparent), pared, pared + 3f * u), Offset(0f, pared), Size(w, 3f * u))
        // El banco corrido bajo las ventanas, con sus cojines de paño.
        val aro = 3.2f * u
        val banco = Rect(w * 0.025f, pared, w * 0.975f, pared + (a.top - aro - pared) * 0.62f)
        drawRect(Color.Black.copy(alpha = 0.32f), Offset(banco.left, banco.bottom), Size(banco.width, 1.2f * u))
        tabla(banco, oscuro, u, e.semilla + 60, tono = ROBLE)
        val nc = 5
        val cw = (banco.width - 2f * u) / nc
        for (i in 0 until nc) {
            val cr = Rect(banco.left + 1f * u + i * cw + 0.3f * u, banco.top + 0.5f * u, banco.left + 1f * u + (i + 1) * cw - 0.3f * u, banco.bottom - 1.2f * u)
            val pano = mezcla(PANO_ROJO, e.acento, 0.2f)
            drawRoundRect(t(pano, oscuro), cr.topLeft, cr.size, CornerRadius(1.2f * u))
            drawRoundRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.16f), Color.Transparent, Color.Black.copy(alpha = 0.25f)), cr.top, cr.bottom), cr.topLeft, cr.size, CornerRadius(1.2f * u))
            drawCircle(Color.Black.copy(alpha = 0.35f), 0.35f * u, cr.center)
            drawRoundRect(t(mezcla(pano, Color.Black, 0.3f), oscuro), cr.topLeft, cr.size, CornerRadius(1.2f * u), style = Stroke(0.25f * u))
        }
        // Las esquinas: la pirámide de balas y el cofre arriba; el barril y la adujada abajo.
        val esquina = a.left - aro
        val rb = minOf(esquina * 0.12f, 2.2f * u)
        balas(Offset(esquina * 0.42f, banco.bottom + rb * 3.2f + 1.4f * u), rb, oscuro)
        val cofreR = Rect(w - esquina * 0.86f, banco.bottom + 1.6f * u, w - esquina * 0.12f, banco.bottom + 1.6f * u + esquina * 0.46f)
        cofre(cofreR, oscuro, u)
        val rBarril = minOf(esquina * 0.36f, (h - a.bottom) * 0.42f)
        barril(Offset(esquina * 0.45f, h - rBarril - 1.4f * u), rBarril, oscuro, u)
        adujada(Offset(w - esquina * 0.48f, h - rBarril - 1.4f * u), rBarril * 0.95f, oscuro, u)
        // Las sillas del lado de abajo y los sillones de las cabeceras.
        val fondoS = minOf(h - (a.bottom + aro) - 2.2f * u, 9f * u)
        val anchoS = fondoS * 1.12f
        val n = ((a.width - 4f * u) / (anchoS * 1.9f)).toInt().coerceIn(3, 6)
        val panoS = mezcla(PANO_ROJO, e.acento, 0.2f)
        for (k in 0 until n) {
            val x = a.left + a.width * (k + 0.5f) / n
            silla(Offset(x, a.bottom + aro + 0.6f * u + fondoS * 0.5f), anchoS, fondoS, 180f, panoS, oscuro, u, e.semilla + k)
        }
        val fondoB = minOf(esquina - 1f * u, 13f * u)
        val cuero = mezcla(CUERO, e.acento, 0.15f)
        sillon(Offset(a.left - aro - fondoB * 0.5f, a.center.y + 2f * u), fondoB * 1.35f, fondoB, -90f, cuero, oscuro, u)
        sillon(Offset(a.right + aro + fondoB * 0.5f, a.center.y + 2f * u), fondoB * 1.35f, fondoB, 90f, cuero, oscuro, u)
        // La mesa del capitán: el tablero de roble, la barandilla de mar y las cantoneras.
        val fuera = Rect(a.left - aro, a.top - aro, a.right + aro, a.bottom + aro)
        sombraSuave(fuera, 2f * u, 4f * u, if (oscuro) 0.5f else 0.34f)
        drawRoundRect(t(ROBLE_OSC, oscuro), Offset(fuera.left - 0.5f * u, fuera.top - 0.5f * u), Size(fuera.width + 1f * u, fuera.height + 1f * u), CornerRadius(2.4f * u))
        drawRoundRect(t(ROBLE, oscuro), fuera.topLeft, fuera.size, CornerRadius(2f * u))
        clipPath(Path().apply { addRoundRect(RoundRect(fuera, CornerRadius(2f * u))) }) {
            vetas(fuera, t(VETA, oscuro).copy(alpha = 0.55f), e.semilla + 21, u)
        }
        val fid = Rect(fuera.left + 1.1f * u, fuera.top + 1.1f * u, fuera.right - 1.1f * u, fuera.bottom - 1.1f * u)
        drawRoundRect(Color.Black.copy(alpha = 0.3f), Offset(fid.left + 0.25f * u, fid.top + 0.35f * u), fid.size, CornerRadius(1.4f * u), style = Stroke(1.1f * u))
        drawRoundRect(t(ROBLE_LUZ, oscuro), fid.topLeft, fid.size, CornerRadius(1.4f * u), style = Stroke(1.1f * u))
        drawRoundRect(Color.White.copy(alpha = if (oscuro) 0.06f else 0.16f), Offset(fid.left - 0.3f * u, fid.top - 0.3f * u), fid.size, CornerRadius(1.4f * u), style = Stroke(0.3f * u))
        val lc = 5f * u
        cantonera(fuera.topLeft, lc, 1f, 1f, oscuro, u); cantonera(Offset(fuera.right, fuera.top), lc, -1f, 1f, oscuro, u)
        cantonera(Offset(fuera.left, fuera.bottom), lc, 1f, -1f, oscuro, u); cantonera(fuera.bottomRight, lc, -1f, -1f, oscuro, u)
        // La carta náutica (el nicho).
        carta(a, e, oscuro, u)
    }

    // ---------------------------------------------------------------------
    //  Mapas: la mesa de derrota
    // ---------------------------------------------------------------------

    /** Una pata torneada de roble en [r], con su anillo de hierro y el pie de bola. */
    private fun DrawScope.pata(r: Rect, oscuro: Boolean, u: Float) {
        val cx = r.center.x; val wd = r.width
        val madera = Brush.horizontalGradient(listOf(t(ROBLE_LUZ, oscuro), t(ROBLE, oscuro), t(ROBLE_OSC, oscuro)), r.left, r.right)
        drawRect(madera, Offset(cx - wd * 0.28f, r.top), Size(wd * 0.56f, r.height * 0.8f))
        drawOval(madera, Offset(r.left, r.top + r.height * 0.2f), Size(wd, r.height * 0.38f))
        drawRect(Brush.horizontalGradient(listOf(t(HIERRO_LUZ, oscuro), t(HIERRO, oscuro)), r.left, r.right), Offset(cx - wd * 0.34f, r.top + r.height * 0.1f), Size(wd * 0.68f, r.height * 0.07f))
        drawOval(madera, Offset(cx - wd * 0.42f, r.bottom - wd * 0.7f), Size(wd * 0.84f, wd * 0.7f))
    }

    /** El sextante, apoyado en la mesa: el armazón de latón, el limbo graduado, la alidada, el anteojo y el mango. */
    private fun DrawScope.sextante(p: Offset, radio: Float, oscuro: Boolean, u: Float, acento: Color) {
        val a0 = Math.toRadians(60.0); val a1 = Math.toRadians(120.0)
        val q0 = Offset(p.x + radio * cos(a0).toFloat(), p.y + radio * sin(a0).toFloat())
        val q1 = Offset(p.x + radio * cos(a1).toFloat(), p.y + radio * sin(a1).toFloat())
        val sombra = Color.Black.copy(alpha = 0.32f)
        val so = Offset(0.8f * u, 1.2f * u)
        drawLine(sombra, p + so, q0 + so, 1.4f * u); drawLine(sombra, p + so, q1 + so, 1.4f * u)
        drawArc(sombra, 58f, 64f, false, Offset(p.x - radio + so.x, p.y - radio + so.y), Size(radio * 2, radio * 2), style = Stroke(2.4f * u))
        val lat = tl(LATON, oscuro)
        drawLine(lat, p, q0, 1.3f * u, StrokeCap.Round); drawLine(lat, p, q1, 1.3f * u, StrokeCap.Round)
        // El arco interior calado.
        drawArc(lat, 60f, 60f, false, Offset(p.x - radio * 0.55f, p.y - radio * 0.55f), Size(radio * 1.1f, radio * 1.1f), style = Stroke(0.9f * u))
        drawArc(Brush.linearGradient(listOf(tl(LATON_LUZ, oscuro), tl(LATON_OSC, oscuro)), Offset(p.x - radio, p.y), Offset(p.x + radio, p.y + radio)), 58f, 64f, false,
            Offset(p.x - radio, p.y - radio), Size(radio * 2, radio * 2), style = Stroke(2.4f * u))
        for (k in 0..24) {
            val a = Math.toRadians(60.0 + k * 2.5)
            val r0 = radio - (if (k % 5 == 0) 1.1f else 0.6f) * u
            drawLine(tl(LATON_OSC, oscuro), Offset(p.x + r0 * cos(a).toFloat(), p.y + r0 * sin(a).toFloat()), Offset(p.x + (radio + 0.5f * u) * cos(a).toFloat(), p.y + (radio + 0.5f * u) * sin(a).toFloat()), 0.18f * u)
        }
        // La alidada con su nonio.
        val ai = Math.toRadians(98.0)
        val qa = Offset(p.x + (radio + 1.6f * u) * cos(ai).toFloat(), p.y + (radio + 1.6f * u) * sin(ai).toFloat())
        drawLine(tl(LATON_OSC, oscuro), p, qa, 1f * u, StrokeCap.Round)
        drawLine(tl(LATON_LUZ, oscuro), p, qa, 0.4f * u, StrokeCap.Round)
        drawCircle(t(HIERRO, oscuro), 1.2f * u, qa)
        // Los espejos y el anteojo.
        drawRect(t(Color(0xFF9AB4C0), oscuro), Offset(p.x - 1.4f * u, p.y - 0.9f * u), Size(2.8f * u, 1.8f * u))
        drawRect(tl(LATON_OSC, oscuro), Offset(p.x - 1.4f * u, p.y - 0.9f * u), Size(2.8f * u, 1.8f * u), style = Stroke(0.25f * u))
        val ma = Offset(p.x + radio * 0.38f, p.y + radio * 0.42f)
        drawRect(t(Color(0xFF9AB4C0), oscuro), Offset(ma.x - 1f * u, ma.y - 1.2f * u), Size(2f * u, 2.4f * u))
        val tel0 = Offset(p.x - radio * 0.28f, p.y + radio * 0.36f); val tel1 = Offset(p.x + radio * 0.18f, p.y + radio * 0.36f)
        drawLine(tl(LATON_OSC, oscuro), tel0, tel1, 2f * u, StrokeCap.Round)
        drawLine(tl(LATON_LUZ, oscuro), Offset(tel0.x, tel0.y - 0.4f * u), Offset(tel1.x, tel1.y - 0.4f * u), 0.5f * u)
        drawCircle(acento, 0.5f * u, tel0)
        drawCircle(lat, 1.1f * u, p)
        drawCircle(t(HIERRO, oscuro), 0.45f * u, p)
    }

    /** El compás de puntas: dos brazos de latón con sus puntas de acero, abiertos. */
    private fun DrawScope.compasPuntas(p: Offset, largo: Float, apertura: Float, giro: Float, oscuro: Boolean, u: Float) {
        rotate(giro, p) {
            listOf(-1f, 1f).forEach { s ->
                val a = Math.toRadians(90.0 + s * apertura / 2.0)
                val q = Offset(p.x + largo * cos(a).toFloat(), p.y + largo * sin(a).toFloat())
                val m = Offset(p.x + largo * 0.82f * cos(a).toFloat(), p.y + largo * 0.82f * sin(a).toFloat())
                drawLine(Color.Black.copy(alpha = 0.3f), Offset(p.x + 0.6f * u, p.y + 0.9f * u), Offset(q.x + 0.6f * u, q.y + 0.9f * u), 1f * u, StrokeCap.Round)
                drawLine(tl(LATON, oscuro), p, m, 1f * u, StrokeCap.Round)
                drawLine(tl(LATON_LUZ, oscuro), p, m, 0.35f * u, StrokeCap.Round)
                drawLine(t(HIERRO_LUZ, oscuro), m, q, 0.5f * u, StrokeCap.Round)
            }
            drawCircle(tl(LATON, oscuro), 1.3f * u, p)
            drawCircle(tl(LATON_OSC, oscuro), 0.5f * u, p)
        }
    }

    /** La brújula en su caja de roble: la caja, el aro de latón, la rosa y el brillo del cristal. */
    private fun DrawScope.brujula(c: Offset, r: Float, oscuro: Boolean, u: Float, acento: Color, secundario: Color) {
        val caja = Rect(c.x - r * 1.18f, c.y - r * 1.18f, c.x + r * 1.18f, c.y + r * 1.18f)
        sombraSuave(caja, 0.8f * u, 2.2f * u, if (oscuro) 0.5f else 0.3f)
        tabla(caja, oscuro, u, 71, tono = ROBLE_LUZ)
        drawRect(Color.Black.copy(alpha = 0.4f), caja.topLeft, caja.size, style = Stroke(0.35f * u))
        drawRect(t(ROBLE_OSC, oscuro), Offset(caja.left + 1f * u, caja.top + 1f * u), Size(caja.width - 2f * u, caja.height - 2f * u), style = Stroke(0.3f * u))
        listOf(caja.topLeft, Offset(caja.right, caja.top), Offset(caja.left, caja.bottom), caja.bottomRight).forEach { q ->
            tachuela(Offset(q.x + (if (q.x < c.x) 1.6f else -1.6f) * u, q.y + (if (q.y < c.y) 1.6f else -1.6f) * u), 0.55f * u, oscuro)
        }
        drawCircle(Color.Black.copy(alpha = 0.35f), r * 1.04f, Offset(c.x + 0.4f * u, c.y + 0.6f * u))
        drawCircle(Brush.linearGradient(listOf(tl(LATON_LUZ, oscuro), tl(LATON, oscuro), tl(LATON_OSC, oscuro)), Offset(c.x - r, c.y - r), Offset(c.x + r, c.y + r)), r * 1.04f, c)
        rosa(c, r * 0.86f, oscuro, acento, secundario, u)
        drawLine(t(HIERRO, oscuro), Offset(c.x, c.y - r * 0.9f), Offset(c.x, c.y - r * 0.7f), 0.4f * u)
        drawArc(Color.White.copy(alpha = if (oscuro) 0.14f else 0.4f), 200f, 70f, false, Offset(c.x - r * 0.72f, c.y - r * 0.72f), Size(r * 1.44f, r * 1.44f), style = Stroke(r * 0.07f, cap = StrokeCap.Round))
    }

    /** La ampolleta: dos ampollas de vidrio entre platos de roble, con sus tres columnillas y la arena. */
    private fun DrawScope.ampolleta(c: Offset, alto: Float, oscuro: Boolean, u: Float) {
        val k = alto / 20f
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(c.x - 6f * k, c.y + 9f * k), Size(14f * k, 3f * k))
        val vidrio = if (oscuro) Color(0xFF6E7A70).copy(alpha = 0.55f) else Color(0xFFE4ECE8).copy(alpha = 0.85f)
        val arriba = Path().apply { moveTo(c.x - 4.2f * k, c.y - 8.4f * k); cubicTo(c.x - 4.6f * k, c.y - 3f * k, c.x - 0.6f * k, c.y - 1.4f * k, c.x - 0.5f * k, c.y); lineTo(c.x + 0.5f * k, c.y); cubicTo(c.x + 0.6f * k, c.y - 1.4f * k, c.x + 4.6f * k, c.y - 3f * k, c.x + 4.2f * k, c.y - 8.4f * k); close() }
        val abajo = Path().apply { moveTo(c.x - 4.2f * k, c.y + 8.4f * k); cubicTo(c.x - 4.6f * k, c.y + 3f * k, c.x - 0.6f * k, c.y + 1.4f * k, c.x - 0.5f * k, c.y); lineTo(c.x + 0.5f * k, c.y); cubicTo(c.x + 0.6f * k, c.y + 1.4f * k, c.x + 4.6f * k, c.y + 3f * k, c.x + 4.2f * k, c.y + 8.4f * k); close() }
        drawPath(arriba, vidrio); drawPath(abajo, vidrio)
        val arena = t(Color(0xFFD8B878), oscuro)
        clipPath(abajo) { drawPath(Path().apply { moveTo(c.x - 5f * k, c.y + 8.4f * k); lineTo(c.x, c.y + 3.6f * k); lineTo(c.x + 5f * k, c.y + 8.4f * k); close() }, arena) }
        clipPath(arriba) { drawRect(arena, Offset(c.x - 5f * k, c.y - 3.4f * k), Size(10f * k, 3.4f * k)) }
        drawLine(arena, Offset(c.x, c.y), Offset(c.x, c.y + 4f * k), 0.25f * k)
        drawPath(arriba, Color.Black.copy(alpha = 0.3f), style = Stroke(0.3f * k)); drawPath(abajo, Color.Black.copy(alpha = 0.3f), style = Stroke(0.3f * k))
        drawLine(Color.White.copy(alpha = 0.5f), Offset(c.x - 3f * k, c.y - 7f * k), Offset(c.x - 2.6f * k, c.y - 4f * k), 0.5f * k, StrokeCap.Round)
        listOf(-5.6f, 5.6f).forEach { dx -> drawRect(Brush.horizontalGradient(listOf(t(ROBLE_LUZ, oscuro), t(ROBLE_OSC, oscuro)), c.x + dx * k - 0.6f * k, c.x + dx * k + 0.6f * k), Offset(c.x + dx * k - 0.6f * k, c.y - 8.6f * k), Size(1.2f * k, 17.2f * k)) }
        listOf(c.y - 10.4f * k, c.y + 8.4f * k).forEach { y ->
            drawRoundRect(Brush.verticalGradient(listOf(t(ROBLE_LUZ, oscuro), t(ROBLE_OSC, oscuro)), y, y + 2f * k), Offset(c.x - 7f * k, y), Size(14f * k, 2f * k), CornerRadius(0.6f * k))
        }
    }

    /** El catalejo de latón con su forro de cuero, tendido de [x0] a [x1] a la altura [y]. */
    private fun DrawScope.catalejo(x0: Float, x1: Float, y: Float, g: Float, oscuro: Boolean, u: Float) {
        val l = x1 - x0
        drawRoundRect(Color.Black.copy(alpha = 0.32f), Offset(x0 + 0.6f * u, y - g * 0.4f + 1f * u), Size(l, g * 0.9f), CornerRadius(g / 2))
        fun tubo(a: Float, b: Float, gg: Float, cuero: Boolean) {
            val brush = if (cuero) Brush.verticalGradient(listOf(t(Color(0xFF7A4E30), oscuro), t(Color(0xFF4A2C18), oscuro), t(Color(0xFF2A180C), oscuro)), y - gg / 2, y + gg / 2)
            else Brush.verticalGradient(listOf(tl(LATON_LUZ, oscuro), tl(LATON, oscuro), tl(LATON_OSC, oscuro)), y - gg / 2, y + gg / 2)
            drawRect(brush, Offset(a, y - gg / 2), Size(b - a, gg))
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(a, y - gg / 2), Size(b - a, gg), style = Stroke(0.2f * u))
        }
        tubo(x0, x0 + l * 0.08f, g * 0.5f, false)
        tubo(x0 + l * 0.08f, x0 + l * 0.26f, g * 0.62f, false)
        tubo(x0 + l * 0.26f, x0 + l * 0.44f, g * 0.76f, false)
        tubo(x0 + l * 0.44f, x0 + l * 0.7f, g * 0.88f, true)
        tubo(x0 + l * 0.7f, x0 + l * 0.92f, g * 0.88f, false)
        tubo(x0 + l * 0.92f, x1, g, false)
        // Los cosidos del forro y los anillos.
        var xs = x0 + l * 0.46f
        while (xs < x0 + l * 0.69f) { drawLine(Color.Black.copy(alpha = 0.35f), Offset(xs, y - g * 0.44f), Offset(xs + g * 0.2f, y + g * 0.44f), 0.2f * u); xs += g * 0.4f }
        listOf(0.26f, 0.44f, 0.92f).forEach { f -> drawRect(tl(LATON_OSC, oscuro), Offset(x0 + l * f - 0.3f * u, y - g * 0.46f), Size(0.6f * u, g * 0.92f)) }
        drawOval(t(Color(0xFF4A6A7A), oscuro), Offset(x1 - g * 0.16f, y - g * 0.42f), Size(g * 0.3f, g * 0.84f))
        drawOval(Color.White.copy(alpha = 0.4f), Offset(x1 - g * 0.1f, y - g * 0.3f), Size(g * 0.12f, g * 0.3f))
    }

    /** El tintero de vidrio oscuro con su tapa de latón y la pluma de ganso. */
    private fun DrawScope.tintero(c: Offset, alto: Float, oscuro: Boolean, u: Float) {
        val k = alto / 10f
        drawOval(Color.Black.copy(alpha = 0.32f), Offset(c.x - 4.6f * k, c.y + 3.4f * k), Size(10f * k, 2.4f * k))
        // La pluma, inclinada hacia atrás.
        val base = Offset(c.x + 0.6f * k, c.y - 3.6f * k)
        val punta = Offset(c.x + 7f * k, c.y - 13f * k)
        val pluma = Path().apply {
            moveTo(base.x, base.y); quadraticTo(base.x + 1.6f * k, base.y - 5f * k, punta.x, punta.y)
            quadraticTo(punta.x - 4.6f * k, punta.y + 2.4f * k, base.x + 1.2f * k, base.y - 3f * k); close()
        }
        drawPath(pluma, t(Color(0xFFF2ECDC), oscuro))
        drawPath(Path().apply { moveTo(base.x + 1.2f * k, base.y - 3f * k); quadraticTo(punta.x - 3.4f * k, punta.y + 3.6f * k, punta.x, punta.y); quadraticTo(punta.x - 2f * k, punta.y + 4.6f * k, base.x + 1.6f * k, base.y - 2f * k); close() }, Color.Black.copy(alpha = 0.18f))
        drawLine(t(Color(0xFFB8A880), oscuro), base, punta, 0.3f * k)
        // El frasco.
        val cuerpo = Rect(c.x - 4f * k, c.y - 2.6f * k, c.x + 4f * k, c.y + 4f * k)
        drawRoundRect(Brush.horizontalGradient(listOf(t(Color(0xFF34404A), oscuro), t(Color(0xFF151A20), oscuro)), cuerpo.left, cuerpo.right), cuerpo.topLeft, cuerpo.size, CornerRadius(1.4f * k))
        drawLine(Color.White.copy(alpha = if (oscuro) 0.15f else 0.35f), Offset(cuerpo.left + 1.2f * k, cuerpo.top + 1f * k), Offset(cuerpo.left + 1.2f * k, cuerpo.bottom - 1.2f * k), 0.5f * k)
        drawRect(Brush.horizontalGradient(listOf(tl(LATON_LUZ, oscuro), tl(LATON_OSC, oscuro)), c.x - 2.4f * k, c.x + 2.4f * k), Offset(c.x - 2.4f * k, c.y - 4f * k), Size(4.8f * k, 1.6f * k))
    }

    override fun DrawScope.muebleMapas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        if (w < 1f || h < 1f) return
        val a = nicho(MuebleVida.MAPAS)
        // Detrás: el mamparo de tablas, la cubierta y las argollas de la trinca.
        tablazon(Rect(0f, 0f, w, h * 0.9f), oscuro, u, e.semilla, 5.2f * u, vertical = true)
        tablazon(Rect(0f, h * 0.9f, w, h), oscuro, u, e.semilla + 3, 2.2f * u, vertical = false, tono = TECA, tonoOsc = TECA_OSC)
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.4f), Color.Transparent), h * 0.9f, h * 0.94f), Offset(0f, h * 0.9f), Size(w, h * 0.04f))
        val tablero = Rect(w * 0.015f, h * 0.02f, w * 0.985f, h * 0.775f)
        // Las patas trincadas a la cubierta y el faldón con sus dos cajones de cartas.
        listOf(w * 0.065f, w * 0.935f).forEachIndexed { i, x ->
            pata(Rect(x - 2.8f * u, h * 0.86f, x + 2.8f * u, h - 0.4f * u), oscuro, u)
            val arg = Offset(x + (if (i == 0) 6f else -6f) * u, h - 1.6f * u)
            drawCircle(t(HIERRO, oscuro), 1f * u, arg, style = Stroke(0.45f * u))
            cabo(Offset(x, h * 0.93f), arg, 0.7f * u, oscuro)
        }
        val faldon = Rect(w * 0.03f, h * 0.835f, w * 0.97f, h * 0.9f)
        tabla(faldon, oscuro, u, e.semilla + 4)
        listOf(0.3f, 0.7f).forEach { f ->
            val cx = w * f
            val cajon = Rect(cx - w * 0.16f, faldon.top + 0.6f * u, cx + w * 0.16f, faldon.bottom - 0.6f * u)
            tabla(cajon, oscuro, u, e.semilla + (f * 10).toInt(), tono = ROBLE_LUZ)
            drawRect(Color.Black.copy(alpha = 0.45f), cajon.topLeft, cajon.size, style = Stroke(0.35f * u))
            // El tirador de latón embutido.
            val ti = Rect(cx - 3f * u, cajon.center.y - 0.9f * u, cx + 3f * u, cajon.center.y + 0.9f * u)
            drawRoundRect(tl(LATON_OSC, oscuro), ti.topLeft, ti.size, CornerRadius(0.9f * u))
            drawRoundRect(t(ROBLE_OSC, oscuro), Offset(ti.left + 0.6f * u, ti.top + 0.4f * u), Size(ti.width - 1.2f * u, ti.height - 0.8f * u), CornerRadius(0.5f * u))
            drawLine(tl(LATON_LUZ, oscuro), Offset(ti.left + 0.6f * u, ti.top + 0.2f * u), Offset(ti.right - 0.6f * u, ti.top + 0.2f * u), 0.2f * u)
        }
        sombraSuave(tablero, 1f * u, 4f * u, if (oscuro) 0.5f else 0.32f)
        // El canto, con su cabo y sus argollas de latón.
        val canto = Rect(w * 0.005f, tablero.bottom, w * 0.995f, h * 0.84f)
        drawRect(Brush.verticalGradient(listOf(t(ROBLE_LUZ, oscuro), t(ROBLE, oscuro), t(ROBLE_OSC, oscuro)), canto.top, canto.bottom), canto.topLeft, canto.size)
        cabo(Offset(canto.left + 1f * u, canto.center.y), Offset(canto.right - 1f * u, canto.center.y), canto.height * 0.4f, oscuro, sombra = false)
        listOf(0.12f, 0.5f, 0.88f).forEach { f ->
            val p = Offset(w * f, canto.center.y)
            drawCircle(tl(LATON, oscuro), canto.height * 0.42f, p, style = Stroke(0.5f * u))
            drawCircle(tl(LATON_OSC, oscuro), canto.height * 0.18f, Offset(p.x, canto.top + canto.height * 0.1f))
        }
        // El tablero de roble con su barandilla de mar.
        tabla(tablero, oscuro, u, e.semilla + 21, tono = ROBLE, nudo = true)
        clipRect(tablero.left, tablero.top, tablero.right, tablero.bottom) { vetas(tablero, t(VETA, oscuro).copy(alpha = 0.3f), e.semilla + 22, u, paso = 2f) }
        val fid = Rect(tablero.left + 0.4f * u, tablero.top + 0.4f * u, tablero.right - 0.4f * u, tablero.bottom - 0.4f * u)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(fid.left + 0.3f * u, fid.top + 0.4f * u), fid.size, style = Stroke(1.2f * u))
        drawRect(t(ROBLE_LUZ, oscuro), fid.topLeft, fid.size, style = Stroke(1.2f * u))
        drawRect(Color.White.copy(alpha = if (oscuro) 0.06f else 0.16f), Offset(fid.left - 0.4f * u, fid.top - 0.4f * u), fid.size, style = Stroke(0.3f * u))
        val lc = 5f * u
        cantonera(tablero.topLeft, lc, 1f, 1f, oscuro, u); cantonera(Offset(tablero.right, tablero.top), lc, -1f, 1f, oscuro, u)
        cantonera(Offset(tablero.left, tablero.bottom), lc, 1f, -1f, oscuro, u); cantonera(tablero.bottomRight, lc, -1f, -1f, oscuro, u)
        // La carta (el nicho).
        carta(a, e, oscuro, u)
        // A la izquierda: el sextante y el compás de puntas.
        val izq = Rect(tablero.left + 2f * u, a.top, a.left - 1.6f * u, a.bottom)
        val rs = minOf(izq.width * 0.95f, izq.height * 0.42f)
        sextante(Offset(izq.center.x, izq.top + 1.6f * u), rs, oscuro, u, e.acento)
        compasPuntas(Offset(izq.center.x - izq.width * 0.18f, izq.top + rs + 6f * u), izq.height * 0.28f, 24f, -28f, oscuro, u)
        tintero(Offset(izq.center.x + izq.width * 0.05f, izq.bottom - izq.height * 0.1f), minOf(izq.height * 0.16f, izq.width * 0.38f), oscuro, u)
        // A la derecha: la brújula en su caja y la ampolleta.
        val der = Rect(a.right + 1.6f * u, a.top, tablero.right - 2f * u, a.bottom)
        val rb = minOf(der.width * 0.38f, der.height * 0.2f)
        brujula(Offset(der.center.x, der.top + rb * 1.3f), rb, oscuro, u, e.acento, e.secundario)
        ampolleta(Offset(der.center.x, der.bottom - der.height * 0.24f), minOf(der.height * 0.36f, der.width * 1.1f), oscuro, u)
        // Arriba, la carta enrollada con su cinta; abajo, el catalejo.
        val gr = (a.top - tablero.top) * 0.62f
        val ry = (tablero.top + a.top) / 2f + 0.3f * u
        val rx0 = a.left + a.width * 0.12f; val rx1 = a.left + a.width * 0.55f
        drawRoundRect(Color.Black.copy(alpha = 0.3f), Offset(rx0 + 0.5f * u, ry - gr / 2 + 0.8f * u), Size(rx1 - rx0, gr), CornerRadius(gr / 2))
        drawRoundRect(Brush.verticalGradient(listOf(t(PERGAMINO, oscuro), t(mezcla(PERGAMINO, PERGAMINO_OSC, 0.4f), oscuro), t(PERGAMINO_OSC, oscuro)), ry - gr / 2, ry + gr / 2), Offset(rx0, ry - gr / 2), Size(rx1 - rx0, gr), CornerRadius(gr / 2))
        drawOval(t(PERGAMINO_OSC, oscuro), Offset(rx1 - gr * 0.3f, ry - gr / 2), Size(gr * 0.6f, gr))
        drawOval(t(mezcla(PERGAMINO_OSC, Color.Black, 0.3f), oscuro), Offset(rx1 - gr * 0.12f, ry - gr * 0.2f), Size(gr * 0.24f, gr * 0.4f))
        drawRect(t(LACRE, oscuro), Offset(rx0 + (rx1 - rx0) * 0.4f, ry - gr / 2), Size(1f * u, gr))
        catalejo(a.left + a.width * 0.3f, a.left + a.width * 0.8f, (a.bottom + tablero.bottom) / 2f, (tablero.bottom - a.bottom) * 0.62f, oscuro, u)
    }

    // ---------------------------------------------------------------------
    //  Otras vidas: el armario de trofeos del capitán
    // ---------------------------------------------------------------------

    /** Una pilastra de roble con su cabo torcido, capitel y basa de latón. */
    private fun DrawScope.pilastra(r: Rect, oscuro: Boolean, u: Float, semilla: Int) {
        tabla(r, oscuro, u, semilla, vertical = true, tono = ROBLE)
        val cap = Rect(r.left - 0.6f * u, r.top, r.right + 0.6f * u, r.top + 2f * u)
        val bas = Rect(r.left - 0.6f * u, r.bottom - 2.2f * u, r.right + 0.6f * u, r.bottom)
        laton(cap, oscuro); laton(bas, oscuro)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(cap.left, cap.bottom), Size(cap.width, 0.4f * u))
        cabo(Offset(r.center.x, cap.bottom + 1f * u), Offset(r.center.x, bas.top - 1f * u), minOf(r.width * 0.5f, 1.6f * u), oscuro, sombra = false)
    }

    override fun DrawScope.muebleOtrasVidas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        if (w < 1f || h < 1f) return
        val a = nicho(MuebleVida.OTRAS_VIDAS)
        // El mamparo detrás de la cresta.
        tablazon(Rect(0f, 0f, w, a.top), oscuro, u, e.semilla, 5f * u, vertical = true)
        // La cornisa de roble con su moldura de cabo y sus tachuelas.
        val corn = Rect(0f, a.top - h * 0.115f, w, a.top - 1.6f * u)
        sombraSuave(corn, 0f, 2f * u, if (oscuro) 0.45f else 0.28f)
        tabla(Rect(corn.left, corn.top, corn.right, corn.top + corn.height * 0.32f), oscuro, u, e.semilla + 1, tono = ROBLE_LUZ)
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(0f, corn.top + corn.height * 0.32f), Size(w, 0.5f * u))
        val friso = Rect(corn.left + w * 0.012f, corn.top + corn.height * 0.32f, corn.right - w * 0.012f, corn.bottom)
        tabla(friso, oscuro, u, e.semilla + 2, tono = ROBLE)
        var tx = friso.left + 2.4f * u
        while (tx < friso.right - 1f * u) { tachuela(Offset(tx, friso.top + friso.height * 0.32f), 0.45f * u, oscuro); tx += 3.2f * u }
        cabo(Offset(friso.left, friso.bottom - 1f * u), Offset(friso.right, friso.bottom - 1f * u), 1.6f * u, oscuro)
        // El timón en la cresta y dos faroles en las esquinas.
        val rt = minOf(corn.top * 0.62f, w * 0.06f).coerceAtLeast(2f * u)
        timon(Offset(w / 2, corn.top + rt * 0.1f), rt, oscuro, u)
        listOf(w * 0.075f, w * 0.925f).forEach { x ->
            val alto = corn.top * 0.66f
            farol(Offset(x, corn.top - alto * 0.55f), alto, oscuro, u)
        }
        // El cuerpo: costados con pilastras, las cinco hornacinas forradas de lona y la cajonera.
        val base = Rect(0f, a.bottom + 1.6f * u, w, h)
        tabla(Rect(0f, a.top - 1.6f * u, w, base.top), oscuro, u, e.semilla + 4, vertical = true, tono = ROBLE_OSC)
        pilastra(Rect(w * 0.012f, a.top - 1.6f * u, a.left - 0.6f * u, a.bottom + 1.6f * u), oscuro, u, e.semilla + 5)
        pilastra(Rect(a.right + 0.6f * u, a.top - 1.6f * u, w - w * 0.012f, a.bottom + 1.6f * u), oscuro, u, e.semilla + 6)
        drawRect(t(ROBLE_OSC, oscuro), Offset(a.left - 0.6f * u, a.top - 1.6f * u), Size(a.width + 1.2f * u, a.height + 3.2f * u))
        drawRect(t(ROBLE, oscuro), a.topLeft, a.size)
        clipRect(a.left, a.top, a.right, a.bottom) { vetas(a, t(VETA, oscuro).copy(alpha = 0.45f), e.semilla + 7, u, vertical = true) }
        val forro = if (oscuro) mezcla(e.fondo, Color(0xFF3A3326), 0.45f) else mezcla(e.fondo, LONA, 0.42f)
        val col = a.width / 5f
        val sep = 1.4f * u
        for (i in 0 until 5) {
            val c = Rect(a.left + col * i + sep, a.top + 1.2f * u, a.left + col * (i + 1) - sep, a.bottom)
            val arco = minOf(c.width * 0.42f, c.height * 0.24f)
            val hueco = Path().apply {
                moveTo(c.left, c.bottom); lineTo(c.left, c.top + arco)
                cubicTo(c.left, c.top + arco * 0.1f, c.left + c.width * 0.18f, c.top, c.center.x, c.top)
                cubicTo(c.right - c.width * 0.18f, c.top, c.right, c.top + arco * 0.1f, c.right, c.top + arco)
                lineTo(c.right, c.bottom); close()
            }
            drawPath(hueco, forro)
            clipPath(hueco) {
                // La trama de la lona, muy suave, y la sombra del arco.
                val hilo = (if (oscuro) Color.White else Color(0xFF6E5A36)).copy(alpha = if (oscuro) 0.02f else 0.04f)
                var yy = c.top + 1f * u; while (yy < c.bottom) { drawLine(hilo, Offset(c.left, yy), Offset(c.right, yy), 0.2f * u); yy += 1.1f * u }
                drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.35f else 0.14f), Color.Transparent), c.top, c.top + arco * 1.6f), c.topLeft, Size(c.width, arco * 1.6f))
                drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.25f else 0.1f), Color.Transparent), c.left, c.left + 1.6f * u), c.topLeft, Size(1.6f * u, c.height))
                drawRect(Brush.horizontalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = if (oscuro) 0.25f else 0.1f)), c.right - 1.6f * u, c.right), Offset(c.right - 1.6f * u, c.top), Size(1.6f * u, c.height))
            }
            // El arco con su cabo y la clave de latón.
            val borde = Path().apply {
                moveTo(c.left, c.top + arco * 1.2f); lineTo(c.left, c.top + arco)
                cubicTo(c.left, c.top + arco * 0.1f, c.left + c.width * 0.18f, c.top, c.center.x, c.top)
                cubicTo(c.right - c.width * 0.18f, c.top, c.right, c.top + arco * 0.1f, c.right, c.top + arco); lineTo(c.right, c.top + arco * 1.2f)
            }
            drawPath(borde, t(CABO_OSC, oscuro), style = Stroke(1.1f * u))
            drawPath(borde, t(CABO, oscuro), style = Stroke(0.7f * u))
            drawPath(borde, t(CABO_OSC, oscuro), style = Stroke(0.7f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.25f * u, 0.7f * u))))
            drawPath(Path().apply { moveTo(c.center.x - 1f * u, c.top - 0.6f * u); lineTo(c.center.x + 1f * u, c.top - 0.6f * u); lineTo(c.center.x + 0.6f * u, c.top + 1.4f * u); lineTo(c.center.x - 0.6f * u, c.top + 1.4f * u); close() }, tl(LATON, oscuro))
            // El brillo del vidrio, sólo en la curva del arco.
            clipPath(hueco) {
                drawLine(Color.White.copy(alpha = if (oscuro) 0.08f else 0.4f), Offset(c.left + c.width * 0.12f, c.top + arco * 1.1f), Offset(c.left + c.width * 0.42f, c.top - 1f * u), 0.6f * u)
            }
        }
        // Los separadores: listones de roble con un fleje de latón.
        for (i in 1 until 5) {
            val x = a.left + col * i
            drawRect(Brush.horizontalGradient(listOf(t(ROBLE_LUZ, oscuro), t(ROBLE_OSC, oscuro)), x - sep, x + sep), Offset(x - sep, a.top), Size(sep * 2, a.height))
            drawRect(tl(LATON, oscuro), Offset(x - 0.25f * u, a.top + a.height * 0.1f), Size(0.5f * u, a.height * 0.8f))
            tachuela(Offset(x, a.top + 1.2f * u), 0.5f * u, oscuro)
        }
        // La cajonera: la repisa con su canto de latón, cinco cajones con tirador y el zócalo.
        drawRect(Brush.verticalGradient(listOf(t(ROBLE_LUZ, oscuro), t(ROBLE_OSC, oscuro)), a.bottom, base.top), Offset(0f, a.bottom), Size(w, base.top - a.bottom))
        laton(Rect(0f, a.bottom, w, a.bottom + 0.6f * u), oscuro)
        val zocalo = Rect(0f, h - (h - base.top) * 0.28f, w, h)
        tabla(Rect(0f, base.top, w, zocalo.top), oscuro, u, e.semilla + 9, tono = ROBLE)
        for (i in 0 until 5) {
            val cx = a.left + col * (i + 0.5f)
            val cj = Rect(cx - col * 0.42f, base.top + 1f * u, cx + col * 0.42f, zocalo.top - 1f * u)
            if (cj.height < 1f) continue
            tabla(cj, oscuro, u, e.semilla + 30 + i, tono = ROBLE_LUZ)
            drawRect(Color.Black.copy(alpha = 0.45f), cj.topLeft, cj.size, style = Stroke(0.3f * u))
            drawArc(tl(LATON, oscuro), 0f, 180f, false, Offset(cx - 2.2f * u, cj.center.y - 1.2f * u), Size(4.4f * u, 2.4f * u), style = Stroke(0.5f * u))
            listOf(cx - 2.2f * u, cx + 2.2f * u).forEach { x -> drawCircle(tl(LATON_OSC, oscuro), 0.55f * u, Offset(x, cj.center.y)) }
        }
        drawRect(Brush.verticalGradient(listOf(t(ROBLE_OSC, oscuro), t(BREA, oscuro)), zocalo.top, zocalo.bottom), zocalo.topLeft, zocalo.size)
        laton(Rect(0f, zocalo.top, w, zocalo.top + 0.5f * u), oscuro)
        listOf(w * 0.03f, w * 0.97f).forEach { x -> cantonera(Offset(x, zocalo.top), 3f * u, if (x < w / 2) 1f else -1f, -1f, oscuro, u) }
    }

    // ---------------------------------------------------------------------
    //  Íconos de «Tus mapas»
    // ---------------------------------------------------------------------

    override fun DrawScope.iconoMapa(id: String, e: EstadoIcono, oscuro: Boolean) {
        val w = size.width; val h = size.height
        if (w < 1f || h < 1f) return
        val k = h / 90f
        val u = k
        val c = Offset(w / 2, h / 2)
        val sombra = Color.Black.copy(alpha = if (oscuro) 0.45f else 0.22f)
        when (id) {
            "unesco" -> {
                // El faro sobre las rocas: la torre a franjas, la linterna con su haz, la galería y el mar.
                drawOval(sombra, Offset(c.x - 50f * k, c.y + 34f * k), Size(100f * k, 8f * k))
                val olas = Rect(c.x - 48f * k, c.y + 26f * k, c.x + 48f * k, c.y + 38f * k)
                drawRoundRect(Brush.verticalGradient(listOf(t(MAR, oscuro), t(MAR_HONDO, oscuro)), olas.top, olas.bottom), olas.topLeft, olas.size, CornerRadius(5f * k))
                for (j in 0 until 6) drawArc(t(ESPUMA, oscuro), 180f, 180f, false, Offset(olas.left + 4f * k + j * 15f * k, olas.top - 2.4f * k), Size(10f * k, 5f * k), style = Stroke(1.4f * k, cap = StrokeCap.Round))
                val roca = Path().apply {
                    moveTo(c.x - 34f * k, c.y + 32f * k); lineTo(c.x - 26f * k, c.y + 18f * k); lineTo(c.x - 14f * k, c.y + 14f * k); lineTo(c.x + 4f * k, c.y + 12f * k)
                    lineTo(c.x + 18f * k, c.y + 16f * k); lineTo(c.x + 30f * k, c.y + 24f * k); lineTo(c.x + 36f * k, c.y + 32f * k); close()
                }
                drawPath(roca, t(ROCA, oscuro))
                drawPath(Path().apply { moveTo(c.x + 4f * k, c.y + 12f * k); lineTo(c.x + 18f * k, c.y + 16f * k); lineTo(c.x + 30f * k, c.y + 24f * k); lineTo(c.x + 36f * k, c.y + 32f * k); lineTo(c.x + 6f * k, c.y + 32f * k); close() }, Color.Black.copy(alpha = 0.25f))
                // El haz de luz.
                val lam = Offset(c.x, c.y - 26f * k)
                listOf(-1f, 1f).forEach { s ->
                    drawPath(Path().apply { moveTo(lam.x, lam.y); lineTo(lam.x + s * 52f * k, lam.y - 12f * k); lineTo(lam.x + s * 52f * k, lam.y + 6f * k); close() },
                        Brush.horizontalGradient(if (s < 0) listOf(Color.Transparent, LLAMA.copy(alpha = if (oscuro) 0.55f else 0.4f)) else listOf(LLAMA.copy(alpha = if (oscuro) 0.55f else 0.4f), Color.Transparent), if (s < 0) lam.x - 52f * k else lam.x, if (s < 0) lam.x else lam.x + 52f * k))
                }
                // La torre a franjas.
                val torre = Path().apply { moveTo(c.x - 9f * k, c.y + 14f * k); lineTo(c.x - 6f * k, c.y - 18f * k); lineTo(c.x + 6f * k, c.y - 18f * k); lineTo(c.x + 9f * k, c.y + 14f * k); close() }
                drawPath(torre, t(Color(0xFFF2EEE4), oscuro))
                clipPath(torre) {
                    listOf(c.y + 4f * k, c.y - 10f * k).forEach { y -> drawRect(e.c1, Offset(c.x - 12f * k, y), Size(24f * k, 7f * k)) }
                    drawRect(Color.Black.copy(alpha = 0.18f), Offset(c.x, c.y - 20f * k), Size(12f * k, 36f * k))
                }
                drawRect(t(HIERRO, oscuro), Offset(c.x - 1.4f * k, c.y + 7f * k), Size(2.8f * k, 7f * k))
                // La galería, la linterna y el capitel.
                drawRect(t(HIERRO, oscuro), Offset(c.x - 9f * k, c.y - 20f * k), Size(18f * k, 2.4f * k))
                for (j in 0..4) drawLine(t(HIERRO, oscuro), Offset(c.x - 8f * k + j * 4f * k, c.y - 20f * k), Offset(c.x - 8f * k + j * 4f * k, c.y - 23f * k), 0.8f * k)
                drawRect(t(HIERRO, oscuro), Offset(c.x - 9f * k, c.y - 23.4f * k), Size(18f * k, 1f * k))
                halo(lam, 12f * k, LLAMA, if (oscuro) 0.7f else 0.45f)
                drawRect(LLAMA, Offset(c.x - 5f * k, c.y - 31f * k), Size(10f * k, 7.6f * k))
                drawLine(t(HIERRO, oscuro), Offset(c.x, c.y - 31f * k), Offset(c.x, c.y - 23.4f * k), 0.8f * k)
                drawPath(Path().apply { moveTo(c.x - 7f * k, c.y - 31f * k); lineTo(c.x, c.y - 38f * k); lineTo(c.x + 7f * k, c.y - 31f * k); close() }, e.c2)
                drawCircle(e.c2, 1.6f * k, Offset(c.x, c.y - 39f * k))
            }
            "rascar" -> {
                // El mapa del tesoro: el pergamino con sus rollos, la capa por rascar y un doblón que la raspa.
                val hoja = Rect(c.x - 44f * k, c.y - 30f * k, c.x + 44f * k, c.y + 30f * k)
                drawRect(sombra, Offset(hoja.left + 2f * k, hoja.top + 3f * k), hoja.size)
                drawRect(t(PERGAMINO, oscuro), hoja.topLeft, hoja.size)
                drawRect(Brush.radialGradient(listOf(Color.Transparent, t(PERGAMINO_OSC, oscuro).copy(alpha = 0.6f)), hoja.center, hoja.width * 0.62f), hoja.topLeft, hoja.size)
                listOf(hoja.left, hoja.right).forEach { x ->
                    drawRoundRect(Brush.horizontalGradient(listOf(t(PERGAMINO_OSC, oscuro), t(PERGAMINO, oscuro), t(PERGAMINO_OSC, oscuro)), x - 4f * k, x + 4f * k), Offset(x - 4f * k, hoja.top - 2f * k), Size(8f * k, hoja.height + 4f * k), CornerRadius(4f * k))
                }
                val tinta = t(Color(0xFF5A4028), oscuro)
                val isla = Path().apply {
                    moveTo(hoja.left + 10f * k, hoja.top + 20f * k); cubicTo(hoja.left + 18f * k, hoja.top + 6f * k, hoja.left + 36f * k, hoja.top + 10f * k, hoja.left + 38f * k, hoja.top + 24f * k)
                    cubicTo(hoja.left + 40f * k, hoja.top + 40f * k, hoja.left + 20f * k, hoja.top + 52f * k, hoja.left + 12f * k, hoja.top + 40f * k); close()
                }
                drawPath(isla, e.c2.copy(alpha = 0.25f)); drawPath(isla, tinta, style = Stroke(1.2f * k))
                drawPath(Path().apply { moveTo(hoja.left + 16f * k, hoja.top + 36f * k); quadraticTo(hoja.left + 22f * k, hoja.top + 20f * k, hoja.left + 30f * k, hoja.top + 24f * k) }, tinta, style = Stroke(1f * k, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2f * k, 2f * k))))
                val xm = Offset(hoja.left + 30f * k, hoja.top + 24f * k)
                drawLine(e.c1, Offset(xm.x - 3f * k, xm.y - 3f * k), Offset(xm.x + 3f * k, xm.y + 3f * k), 1.6f * k, StrokeCap.Round)
                drawLine(e.c1, Offset(xm.x + 3f * k, xm.y - 3f * k), Offset(xm.x - 3f * k, xm.y + 3f * k), 1.6f * k, StrokeCap.Round)
                val capa = Path().apply {
                    moveTo(hoja.right, hoja.top); lineTo(hoja.left + 46f * k, hoja.top)
                    var yy = hoja.top; var j = 0
                    while (yy < hoja.bottom) { lineTo(hoja.left + 44f * k - (yy - hoja.top) * 0.3f + (if (j % 2 == 0) 3f else -2f) * k, yy); yy += 4f * k; j++ }
                    lineTo(hoja.left + 28f * k, hoja.bottom); lineTo(hoja.right, hoja.bottom); close()
                }
                drawPath(capa, t(Color(0xFF8C8578), oscuro))
                clipPath(capa) { var xx = hoja.left; while (xx < hoja.right + hoja.height) { drawLine(Color.Black.copy(alpha = 0.12f), Offset(xx, hoja.top), Offset(xx - hoja.height, hoja.bottom), 0.7f * k); xx += 3f * k } }
                drawRoundRect(Brush.horizontalGradient(listOf(t(PERGAMINO_OSC, oscuro), t(PERGAMINO, oscuro), t(PERGAMINO_OSC, oscuro)), hoja.right - 4f * k, hoja.right + 4f * k), Offset(hoja.right - 4f * k, hoja.top - 2f * k), Size(8f * k, hoja.height + 4f * k), CornerRadius(4f * k))
                // El doblón: oro con su cruz, sus cuarteles y el canto.
                val mo = Offset(hoja.left + 52f * k, hoja.top + 40f * k)
                drawCircle(sombra, 13f * k, Offset(mo.x + 2f * k, mo.y + 3f * k))
                drawCircle(Brush.linearGradient(listOf(Color(0xFFF2D27A), ORO, Color(0xFF8A6420)), Offset(mo.x - 13f * k, mo.y - 13f * k), Offset(mo.x + 13f * k, mo.y + 13f * k)), 13f * k, mo)
                drawCircle(Color(0xFF8A6420), 10.6f * k, mo, style = Stroke(0.8f * k))
                drawCircle(Color(0xFF8A6420), 12.4f * k, mo, style = Stroke(0.6f * k, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1f * k, 1f * k))))
                drawLine(Color(0xFF7A5418), Offset(mo.x - 7f * k, mo.y), Offset(mo.x + 7f * k, mo.y), 1.6f * k)
                drawLine(Color(0xFF7A5418), Offset(mo.x, mo.y - 7f * k), Offset(mo.x, mo.y + 7f * k), 1.6f * k)
                listOf(Offset(-1f, -1f), Offset(1f, -1f), Offset(-1f, 1f), Offset(1f, 1f)).forEach { q -> drawCircle(Color(0xFF9A7428), 1.4f * k, Offset(mo.x + q.x * 4f * k, mo.y + q.y * 4f * k)) }
                drawArc(Color.White.copy(alpha = 0.5f), 200f, 60f, false, Offset(mo.x - 11f * k, mo.y - 11f * k), Size(22f * k, 22f * k), style = Stroke(1.2f * k))
                for (j in 0 until 5) drawRect(t(Color(0xFF8C8578), oscuro), Offset(mo.x - 17f * k - j * 2.6f * k, mo.y - 2f * k + (j % 2) * 4f * k), Size(1.6f * k, 1.6f * k))
            }
            else -> {
                // Tus lugares: tres islas en la carta, cada una con su gallardete en un asta de latón, y la derrota.
                val hoja = Rect(c.x - 50f * k, c.y - 26f * k, c.x + 50f * k, c.y + 34f * k)
                drawRect(sombra, Offset(hoja.left + 2f * k, hoja.top + 3f * k), hoja.size)
                drawRect(t(PERGAMINO, oscuro), hoja.topLeft, hoja.size)
                drawRect(e.c2.copy(alpha = 0.6f), Offset(hoja.left + 2.5f * k, hoja.top + 2.5f * k), Size(hoja.width - 5f * k, hoja.height - 5f * k), style = Stroke(0.8f * k))
                val pts = listOf(Offset(c.x - 32f * k, c.y + 20f * k), Offset(c.x, c.y + 6f * k), Offset(c.x + 32f * k, c.y + 18f * k))
                drawPath(Path().apply { moveTo(pts[0].x, pts[0].y); quadraticTo(c.x - 16f * k, c.y + 24f * k, pts[1].x, pts[1].y); quadraticTo(c.x + 16f * k, c.y - 4f * k, pts[2].x, pts[2].y) },
                    t(Color(0xFF5A4028), oscuro), style = Stroke(1.1f * k, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2.4f * k, 2f * k))))
                val colores = listOf(e.c1, e.c2, e.c1)
                pts.forEachIndexed { i, p ->
                    drawOval(t(Color(0xFFCFB98A), oscuro), Offset(p.x - 8f * k, p.y - 3f * k), Size(16f * k, 6f * k))
                    drawOval(t(Color(0xFF6E8A5A), oscuro), Offset(p.x - 6f * k, p.y - 3.6f * k), Size(12f * k, 4.6f * k))
                    val tope = Offset(p.x, p.y - 26f * k)
                    drawLine(sombra, Offset(p.x + 1f * k, p.y), Offset(p.x + 6f * k, p.y - 1f * k), 1.6f * k, StrokeCap.Round)
                    drawLine(tl(LATON_OSC, oscuro), p, tope, 1.6f * k)
                    drawLine(tl(LATON_LUZ, oscuro), Offset(p.x - 0.4f * k, p.y), Offset(tope.x - 0.4f * k, tope.y), 0.5f * k)
                    drawCircle(tl(LATON, oscuro), 1.6f * k, tope)
                    val ban = Path().apply { moveTo(tope.x + 0.8f * k, tope.y + 1.4f * k); quadraticTo(tope.x + 10f * k, tope.y + 2f * k, tope.x + 18f * k, tope.y + 5.6f * k); quadraticTo(tope.x + 10f * k, tope.y + 7f * k, tope.x + 0.8f * k, tope.y + 10f * k); close() }
                    drawPath(Path().apply { addPath(ban, Offset(1f * k, 1.4f * k)) }, sombra)
                    drawPath(ban, colores[i])
                    drawPath(Path().apply { moveTo(tope.x + 0.8f * k, tope.y + 5.4f * k); quadraticTo(tope.x + 10f * k, tope.y + 5.6f * k, tope.x + 18f * k, tope.y + 5.6f * k); quadraticTo(tope.x + 10f * k, tope.y + 7f * k, tope.x + 0.8f * k, tope.y + 10f * k); close() }, Color.Black.copy(alpha = 0.2f))
                }
            }
        }
    }

    // ---------------------------------------------------------------------
    //  Notas: el mamparo del camarote
    // ---------------------------------------------------------------------

    override fun DrawScope.tableroNotas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        if (w < 1f || h < 1f) return
        val a = nicho(MuebleVida.NOTAS)
        // El mamparo de tablas detrás de todo.
        tablazon(Rect(0f, 0f, w, h), oscuro, u, e.semilla, 4.6f * u, vertical = true)
        // El marco de roble.
        val marco = Rect(w * 0.012f, a.top - h * 0.062f, w * 0.988f, a.bottom + h * 0.035f)
        sombraSuave(marco, 0f, 3f * u, if (oscuro) 0.5f else 0.3f)
        tabla(Rect(marco.left, marco.top, a.left, marco.bottom), oscuro, u, e.semilla + 1, vertical = true, tono = ROBLE)
        tabla(Rect(a.right, marco.top, marco.right, marco.bottom), oscuro, u, e.semilla + 2, vertical = true, tono = ROBLE)
        tabla(Rect(a.left, marco.top, a.right, a.top), oscuro, u, e.semilla + 3, tono = ROBLE, nudo = true)
        tabla(Rect(a.left, a.bottom, a.right, marco.bottom), oscuro, u, e.semilla + 4, tono = ROBLE)
        drawRect(Color.Black.copy(alpha = 0.4f), marco.topLeft, marco.size, style = Stroke(0.4f * u))
        drawRect(Color.White.copy(alpha = if (oscuro) 0.05f else 0.12f), Offset(marco.left + 1.2f * u, marco.top + 1.2f * u), Size(marco.width - 2.4f * u, marco.height - 2.4f * u), style = Stroke(0.3f * u))
        val lc = 7f * u
        cantonera(marco.topLeft, lc, 1f, 1f, oscuro, u); cantonera(Offset(marco.right, marco.top), lc, -1f, 1f, oscuro, u)
        cantonera(Offset(marco.left, marco.bottom), lc, 1f, -1f, oscuro, u); cantonera(marco.bottomRight, lc, -1f, -1f, oscuro, u)
        // El paño de vela cosido (el nicho).
        val pano = if (oscuro) mezcla(e.fondo, Color(0xFF3A3326), 0.5f) else mezcla(e.fondo, LONA, 0.48f)
        drawRect(pano, a.topLeft, a.size)
        clipRect(a.left, a.top, a.right, a.bottom) {
            val hilo = (if (oscuro) Color.White else Color(0xFF6E5A36)).copy(alpha = if (oscuro) 0.018f else 0.035f)
            var x = a.left + 0.8f * u; while (x < a.right) { drawLine(hilo, Offset(x, a.top), Offset(x, a.bottom), 0.2f * u); x += 1.1f * u }
            var y = a.top + 0.8f * u; while (y < a.bottom) { drawLine(hilo, Offset(a.left, y), Offset(a.right, y), 0.2f * u); y += 1.1f * u }
            // Las costuras de los paños, con su doble puntada.
            val costura = (if (oscuro) Color.Black else Color(0xFF8A7650)).copy(alpha = if (oscuro) 0.25f else 0.22f)
            listOf(0.25f, 0.5f, 0.75f).forEach { f ->
                val cx = a.left + a.width * f
                drawLine(costura, Offset(cx, a.top), Offset(cx, a.bottom), 0.3f * u)
                drawLine(costura, Offset(cx + 1f * u, a.top), Offset(cx + 1f * u, a.bottom), 0.2f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.8f * u, 0.7f * u)))
            }
        }
        sombraInterior(a, 2.6f * u, if (oscuro) 0.35f else 0.16f)
        marcoCabo(a, 1.5f * u, oscuro)
        // La tabla de nombre arriba: el tablón tallado con sus volutas, la rosa de los vientos y dos faroles.
        val rr = minOf(marco.top * 0.62f, w * 0.075f).coerceAtLeast(2f * u)
        val tn = Rect(w * 0.22f, marco.top - rr * 0.95f, w * 0.78f, marco.top + 1.2f * u)
        val placa = Path().apply {
            moveTo(tn.left, tn.bottom); lineTo(tn.left, tn.top + tn.height * 0.45f)
            quadraticTo(tn.left - 4f * u, tn.top + tn.height * 0.3f, tn.left - 2f * u, tn.top + tn.height * 0.1f)
            quadraticTo(tn.left + 3f * u, tn.top, tn.left + 6f * u, tn.top + tn.height * 0.3f)
            lineTo(tn.right - 6f * u, tn.top + tn.height * 0.3f)
            quadraticTo(tn.right - 3f * u, tn.top, tn.right + 2f * u, tn.top + tn.height * 0.1f)
            quadraticTo(tn.right + 4f * u, tn.top + tn.height * 0.3f, tn.right, tn.top + tn.height * 0.45f)
            lineTo(tn.right, tn.bottom); close()
        }
        drawPath(Path().apply { addPath(placa, Offset(0.6f * u, 1f * u)) }, Color.Black.copy(alpha = 0.35f))
        drawPath(placa, t(ROBLE_LUZ, oscuro))
        clipPath(placa) { vetas(Rect(tn.left - 5f * u, tn.top, tn.right + 5f * u, tn.bottom), t(VETA, oscuro).copy(alpha = 0.45f), e.semilla + 11, u) }
        drawPath(placa, t(ROBLE_OSC, oscuro), style = Stroke(0.4f * u))
        drawLine(tl(LATON, oscuro), Offset(tn.left + 6f * u, tn.top + tn.height * 0.58f), Offset(w / 2 - rr * 1.25f, tn.top + tn.height * 0.58f), 0.5f * u)
        drawLine(tl(LATON, oscuro), Offset(w / 2 + rr * 1.25f, tn.top + tn.height * 0.58f), Offset(tn.right - 6f * u, tn.top + tn.height * 0.58f), 0.5f * u)
        caboCurvo(Offset(tn.left + 7f * u, tn.top + tn.height * 0.3f), Offset((tn.left + w / 2 - rr) / 2f, tn.bottom + 1f * u), Offset(w / 2 - rr * 1.1f, tn.top + tn.height * 0.3f), 1.1f * u, oscuro)
        caboCurvo(Offset(w / 2 + rr * 1.1f, tn.top + tn.height * 0.3f), Offset((tn.right + w / 2 + rr) / 2f, tn.bottom + 1f * u), Offset(tn.right - 7f * u, tn.top + tn.height * 0.3f), 1.1f * u, oscuro)
        val rc = Offset(w / 2, tn.top + tn.height * 0.52f)
        drawCircle(Color.Black.copy(alpha = 0.35f), rr * 1.04f, Offset(rc.x + 0.5f * u, rc.y + 0.9f * u))
        drawCircle(Brush.linearGradient(listOf(tl(LATON_LUZ, oscuro), tl(LATON, oscuro), tl(LATON_OSC, oscuro)), Offset(rc.x - rr, rc.y - rr), Offset(rc.x + rr, rc.y + rr)), rr * 1.06f, rc)
        rosa(rc, rr * 0.9f, oscuro, e.acento, e.secundario, u)
        listOf(w * 0.08f, w * 0.92f).forEach { x ->
            drawLine(t(HIERRO, oscuro), Offset(x, marco.top - rr * 0.95f), Offset(x + (if (x < w / 2) 3f else -3f) * u, marco.top - rr * 0.95f), 0.6f * u)
            farol(Offset(x + (if (x < w / 2) 3f else -3f) * u, marco.top - rr * 0.32f), rr * 1.1f, oscuro, u)
        }
        // La repisa de abajo con su canto de latón, su placa y el catalejo tendido.
        val repisa = Rect(0f, marco.bottom, w, h)
        drawRect(Brush.verticalGradient(listOf(t(ROBLE_LUZ, oscuro), t(ROBLE, oscuro), t(ROBLE_OSC, oscuro)), repisa.top, repisa.bottom), repisa.topLeft, repisa.size)
        clipRect(repisa.left, repisa.top, repisa.right, repisa.bottom) { vetas(repisa, t(VETA, oscuro).copy(alpha = 0.4f), e.semilla + 13, u) }
        laton(Rect(0f, repisa.top, w, repisa.top + 0.7f * u), oscuro)
        val rh = repisa.height
        if (rh > 3f * u) {
            catalejo(w * 0.08f, w * 0.42f, repisa.top + rh * 0.46f, rh * 0.42f, oscuro, u)
            adujada(Offset(w * 0.86f, repisa.top + rh * 0.5f), rh * 0.4f, oscuro, u)
            val pl = Rect(w * 0.5f, repisa.top + rh * 0.24f, w * 0.72f, repisa.bottom - rh * 0.24f)
            drawRect(Color.Black.copy(alpha = 0.4f), Offset(pl.left + 0.5f * u, pl.top + 0.7f * u), pl.size)
            drawRect(Brush.verticalGradient(listOf(tl(LATON_LUZ, oscuro), tl(LATON, oscuro), tl(LATON_OSC, oscuro)), pl.top, pl.bottom), pl.topLeft, pl.size)
            drawRect(tl(LATON_OSC, oscuro), Offset(pl.left + 0.8f * u, pl.top + 0.8f * u), Size(pl.width - 1.6f * u, pl.height - 1.6f * u), style = Stroke(0.3f * u))
            tachuela(Offset(pl.left + 1.8f * u, pl.center.y), 0.5f * u, oscuro, LATON_OSC)
            tachuela(Offset(pl.right - 1.8f * u, pl.center.y), 0.5f * u, oscuro, LATON_OSC)
        }
    }

    override fun DrawScope.notaTablero(e: EstadoNota, oscuro: Boolean) {
        val r0 = e.rect
        if (r0.width < 1f || r0.height < 1f) return
        val k = minOf(r0.width, r0.height) / 100f
        val r = Rect(r0.left + 2.5f * k, r0.top + 3f * k, r0.right - 2.5f * k, r0.bottom - 2f * k)
        val papel = when (e.tipo) {
            "idea" -> if (oscuro) P_PERGAMINO_N else P_PERGAMINO
            "proposito" -> if (oscuro) P_CARTA_N else P_CARTA
            "recordatorio" -> if (oscuro) P_LONA_N else P_LONA
            else -> if (oscuro) P_BITACORA_N else P_BITACORA
        }
        // La silueta: el pergamino con el borde roto, la etiqueta de lona con las esquinas cortadas, el resto recto.
        val forma = when (e.tipo) {
            "idea" -> Path().apply {
                moveTo(r.left, r.top)
                var x = r.left; var j = 0
                while (x < r.right) { lineTo(x, r.top + (if (j % 2 == 0) 0f else 1.2f * k) * azar(e.indice + 5, j)); x += 6f * k; j++ }
                lineTo(r.right, r.top)
                var y = r.top; while (y < r.bottom) { lineTo(r.right - 1.8f * k * azar(e.indice + 6, j), y); y += 5f * k; j++ }
                lineTo(r.right, r.bottom)
                x = r.right; while (x > r.left) { lineTo(x, r.bottom - 2.4f * k * azar(e.indice + 7, j)); x -= 4.5f * k; j++ }
                lineTo(r.left, r.bottom)
                y = r.bottom; while (y > r.top) { lineTo(r.left + 1.8f * k * azar(e.indice + 8, j), y); y -= 5f * k; j++ }
                close()
            }
            "recordatorio" -> Path().apply {
                val ch = 11f * k
                moveTo(r.left + ch, r.top); lineTo(r.right - ch, r.top); lineTo(r.right, r.top + ch); lineTo(r.right, r.bottom); lineTo(r.left, r.bottom); lineTo(r.left, r.top + ch); close()
            }
            else -> Path().apply { addRect(r) }
        }
        drawPath(Path().apply { addPath(forma, Offset(1.4f * k, 2.4f * k)) }, Color.Black.copy(alpha = if (oscuro) 0.5f else 0.24f))
        drawPath(forma, papel)
        val linea = if (oscuro) Color.White.copy(alpha = 0.14f) else Color(0xFF2A1C0F).copy(alpha = 0.2f)
        clipPath(forma) {
            // El borde tostado del papel viejo.
            drawRect(Brush.radialGradient(listOf(Color.Transparent, (if (oscuro) Color.Black else PERGAMINO_OSC).copy(alpha = if (oscuro) 0.3f else 0.3f)), r.center, maxOf(r.width, r.height) * 0.75f), r.topLeft, r.size)
            when (e.tipo) {
                "idea" -> {
                    // El pergamino: la X roja del tesoro y su derrota de puntos, en el margen de abajo.
                    val xm = Offset(r.right - 10f * k, r.bottom - 7f * k)
                    val roja = mezcla(LACRE, e.acento, 0.2f).copy(alpha = if (oscuro) 0.85f else 0.9f)
                    drawLine(linea.copy(alpha = linea.alpha * 1.5f), Offset(r.left + 8f * k, r.bottom - 7f * k), Offset(xm.x - 4f * k, xm.y), 0.6f * k, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.6f * k, 1.6f * k)))
                    drawLine(roja, Offset(xm.x - 2.4f * k, xm.y - 2.4f * k), Offset(xm.x + 2.4f * k, xm.y + 2.4f * k), 1f * k, StrokeCap.Round)
                    drawLine(roja, Offset(xm.x + 2.4f * k, xm.y - 2.4f * k), Offset(xm.x - 2.4f * k, xm.y + 2.4f * k), 1f * k, StrokeCap.Round)
                }
                "pensamiento" -> {
                    // La hoja de bitácora: la doble raya de la cabecera y las columnas de la tabla de horas, apenas.
                    drawLine(linea, Offset(r.left + 4f * k, r.top + 10f * k), Offset(r.right - 4f * k, r.top + 10f * k), 0.6f * k)
                    drawLine(linea, Offset(r.left + 4f * k, r.top + 12f * k), Offset(r.right - 4f * k, r.top + 12f * k), 0.3f * k)
                    drawLine(linea.copy(alpha = linea.alpha * 0.6f), Offset(r.left + 7f * k, r.top + 12f * k), Offset(r.left + 7f * k, r.bottom), 0.3f * k)
                    drawLine(linea, Offset(r.left + 4f * k, r.bottom - 7f * k), Offset(r.right - 4f * k, r.bottom - 7f * k), 0.3f * k)
                }
                "proposito" -> {
                    // El retazo de carta: un rumbo en diagonal y una rosa chica en la esquina.
                    drawLine(linea.copy(alpha = linea.alpha * 0.6f), Offset(r.left, r.bottom), Offset(r.right, r.top + r.height * 0.3f), 0.3f * k)
                    rosa(Offset(r.right - 8f * k, r.bottom - 8f * k), 5.2f * k, oscuro, e.acento, mezcla(e.acento, Color.Gray, 0.6f), k, aros = false)
                }
                "recordatorio" -> {
                    // La etiqueta de lona: la costura de borde y el ojal reforzado.
                    drawRect(linea, Offset(r.left + 2.5f * k, r.top + 13f * k), Size(r.width - 5f * k, r.height - 15.5f * k), style = Stroke(0.4f * k, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.4f * k, 1.2f * k))))
                }
                else -> {}
            }
        }
        // El clavo de forja; en las fijadas, la tachuela de latón y el sello de lacre con el ancla.
        val clavo = if (e.tipo == "recordatorio") Offset(r.center.x, r.top + 6f * k) else Offset(r.center.x, r.top + 5f * k)
        if (e.tipo == "recordatorio") {
            drawCircle(tl(LATON, oscuro), 3.6f * k, clavo, style = Stroke(1.2f * k))
            drawPath(Path().apply { moveTo(clavo.x, clavo.y); cubicTo(clavo.x + 6f * k, clavo.y - 7f * k, clavo.x + 12f * k, clavo.y - 2f * k, clavo.x + 16f * k, r0.top) }, t(CABO, oscuro), style = Stroke(0.9f * k))
        }
        drawCircle(Color.Black.copy(alpha = 0.35f), 2.8f * k, Offset(clavo.x + 1f * k, clavo.y + 1.4f * k))
        if (e.fijada) {
            drawCircle(Brush.radialGradient(listOf(tl(LATON_LUZ, oscuro), tl(LATON, oscuro), tl(LATON_OSC, oscuro)), Offset(clavo.x - 1f * k, clavo.y - 1f * k), 4f * k), 3.2f * k, clavo)
            drawCircle(Color.White.copy(alpha = 0.5f), 0.9f * k, Offset(clavo.x - 1f * k, clavo.y - 1f * k))
            val s = Offset(r.left + 10f * k, r.bottom - 8f * k)
            val lacre = Path().apply {
                for (j in 0 until 12) {
                    val ang = j / 12.0 * 2 * Math.PI
                    val rr = 5.4f * k * (0.88f + 0.18f * azar(e.indice + 3, j))
                    val p = Offset(s.x + rr * cos(ang).toFloat(), s.y + rr * sin(ang).toFloat())
                    if (j == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
                }
                close()
            }
            drawPath(Path().apply { addPath(lacre, Offset(0.6f * k, 0.9f * k)) }, Color.Black.copy(alpha = 0.3f))
            drawPath(lacre, mezcla(LACRE, e.acento, 0.12f))
            // El ancla grabada.
            val an = mezcla(LACRE, Color.Black, 0.45f)
            drawLine(an, Offset(s.x, s.y - 3f * k), Offset(s.x, s.y + 2.6f * k), 0.6f * k)
            drawLine(an, Offset(s.x - 1.6f * k, s.y - 1.6f * k), Offset(s.x + 1.6f * k, s.y - 1.6f * k), 0.5f * k)
            drawArc(an, 20f, 140f, false, Offset(s.x - 2.6f * k, s.y - 0.6f * k), Size(5.2f * k, 3.6f * k), style = Stroke(0.6f * k))
            drawCircle(Color.White.copy(alpha = 0.2f), 1f * k, Offset(s.x - 2.2f * k, s.y - 2.2f * k))
        } else {
            val cabeza = Path().apply {
                moveTo(clavo.x - 2.4f * k, clavo.y - 1.8f * k); lineTo(clavo.x + 2f * k, clavo.y - 2.4f * k); lineTo(clavo.x + 2.6f * k, clavo.y + 1.8f * k); lineTo(clavo.x - 1.8f * k, clavo.y + 2.4f * k); close()
            }
            drawPath(cabeza, Brush.linearGradient(listOf(t(HIERRO_LUZ, oscuro), t(HIERRO, oscuro)), Offset(clavo.x - 2.4f * k, clavo.y - 2.4f * k), Offset(clavo.x + 2.6f * k, clavo.y + 2.4f * k)))
            drawCircle(Color.White.copy(alpha = 0.25f), 0.7f * k, Offset(clavo.x - 0.8f * k, clavo.y - 0.8f * k))
            // Un rastro de óxido bajo el clavo.
            drawLine(Color(0xFF7A4A22).copy(alpha = if (oscuro) 0.25f else 0.3f), Offset(clavo.x + 0.4f * k, clavo.y + 2.6f * k), Offset(clavo.x + 0.8f * k, clavo.y + 6f * k), 0.8f * k, StrokeCap.Round)
        }
    }

    /** La tinta de las notas: sepia de bitácora sobre el pergamino; de noche, el papel a la luz del farol y la tinta clara. */
    override fun tintaNota(oscuro: Boolean): Color = if (oscuro) Color(0xFFEBDFC4) else Color(0xFF2A1C0F)
}
