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
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.lerp
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

/**
 * Abismo: los muebles de Vida y de Diario del batiscafo, de la misma nave que su Ideario de cajas estancas, su estantería de
 * bitácoras con el ojo de buey y su Hall de perlas: el casco por dentro (planchas color hueso con sus costuras y sus filas
 * de remaches), el acero azulado, el latón de los ojos de buey y los instrumentos, la teca de la mesa y las válvulas rojas.
 * De día, la zona iluminada: el agua azul con los haces del sol en los vidrios; de noche, la zona abisal: el casco en
 * penumbra, las lámparas de jaula encendidas y, afuera, solo la luz de los animales (el acento y el secundario del esquema).
 * - Social: el salón de observación: el gran ventanal (el nicho, el agua) en su marco de latón con la corona de pernos y
 *   su junta de goma; arriba, las dos tuberías con sus bridas y la rueda de la válvula; a la izquierda, el manómetro y la
 *   válvula roja; a la derecha, la lámpara de jaula y el tubo acústico; abajo, el banco de teca con su listón de latón y el
 *   piso de chapa estriada.
 * - Mapas: la mesa de cartas: la carta batimétrica (el nicho) sobre el tablero de teca con su moldura de latón y sus
 *   pesas en las esquinas; alrededor, la regla paralela, el farol, el compás de puntas, la bitácora de la brújula y el casco
 *   de buzo; abajo, el faldón con dos cajones, las patas torneadas con zapatas de latón y el piso.
 * - Otras vidas: la vitrina de perlas: cinco hornacinas de agua (el nicho) con su arco de latón y unas algas en los rincones,
 *   entre montantes de acero remachado; arriba, la cornisa de teca con el ojo de buey a la medusa y dos manómetros; a los
 *   lados, dos tubos de latón como columnas, con sus bridas; abajo, el zócalo de teca con las placas de latón.
 * - Íconos: el pecio en el fondo como patrimonio sumergido; el ojo de buey empañado de plata que raspa un doblón; tres boyas
 *   en la superficie, cada una con su cadena hasta el ancla.
 * - Notas: la pizarra de a bordo: la chapa pintada (el nicho) en su marco de teca con cantoneras de latón, bajo la lámpara de
 *   jaula, entre dos tubos con su válvula, con la repisa de la linterna, el reloj de buceo y el lápiz; cada nota es una
 *   pizarra de buceo con su regla, su asa de goma del color del tipo y su signo (las burbujas, la medusa, el ancla o el
 *   reloj), colgada de un cordón o, si está fijada, de un mosquetón de latón.
 */
internal object VidaAbismo : DisenoVida {
    // ---------------------------------------------------------------- materiales

    /** Los materiales que cambian entre la zona iluminada (claro) y la zona abisal (oscuro). */
    private class Pal(
        val casco: Color, val cascoLuz: Color, val cascoOsc: Color, val remache: Color,
        val acero: Color, val aceroLuz: Color, val aceroOsc: Color,
        val laton: Color, val latonLuz: Color, val latonOsc: Color,
        val teca: Color, val tecaLuz: Color, val tecaVeta: Color,
        val nicho: Color, val nichoHondo: Color, val nichoLinea: Color, val piso: Color,
        val oscuro: Boolean,
    )

    private val DIA = Pal(
        casco = Color(0xFFD8D2C0), cascoLuz = Color(0xFFEAE5D7), cascoOsc = Color(0xFFADA690), remache = Color(0xFF9A9480),
        acero = Color(0xFF5E7078), aceroLuz = Color(0xFF8A9CA6), aceroOsc = Color(0xFF34424A),
        laton = Color(0xFFC09A48), latonLuz = Color(0xFFE6C886), latonOsc = Color(0xFF7E6128),
        teca = Color(0xFF7A4E2C), tecaLuz = Color(0xFF9A6A3E), tecaVeta = Color(0xFF4E301A),
        nicho = Color(0xFFE4F1F5), nichoHondo = Color(0xFFD5E8EF), nichoLinea = Color(0xFFCFE3EA), piso = Color(0xFF4E5C62),
        oscuro = false,
    )
    private val ABISAL = Pal(
        casco = Color(0xFF3B3E3E), cascoLuz = Color(0xFF515554), cascoOsc = Color(0xFF26292A), remache = Color(0xFF5E6261),
        acero = Color(0xFF2C3940), aceroLuz = Color(0xFF465760), aceroOsc = Color(0xFF172126),
        laton = Color(0xFF9A7A36), latonLuz = Color(0xFFC4A262), latonOsc = Color(0xFF5A4418),
        teca = Color(0xFF4E301A), tecaLuz = Color(0xFF6A4426), tecaVeta = Color(0xFF2A180A),
        nicho = Color(0xFF06131C), nichoHondo = Color(0xFF030B12), nichoLinea = Color(0xFF0D202C), piso = Color(0xFF1A2226),
        oscuro = true,
    )

    private fun pal(oscuro: Boolean) = if (oscuro) ABISAL else DIA

    private val NEGRO = Color(0xFF040709)
    private val GOMA = Color(0xFF26292B)
    private val ROJO = Color(0xFFC7302A)
    private val ROJO_LUZ = Color(0xFFE8604E)
    private val ROJO_OSC = Color(0xFF7E1C18)
    private val COBRE = Color(0xFFC07A3A)
    private val COBRE_LUZ = Color(0xFFE8A868)
    private val COBRE_OSC = Color(0xFF7A4418)
    private val ESFERA = Color(0xFFF4F1E6)
    private val AGUA_SOL = Color(0xFF4FA3C8)
    private val AGUA = Color(0xFF1A6A94)
    private val AGUA_HONDA = Color(0xFF0A3A5C)
    private val ABISMO = Color(0xFF020A12)
    private val ABISMO_AZUL = Color(0xFF07203A)
    private val ARENA = Color(0xFFD8C08A)
    private val ARENA_OSC = Color(0xFFA88E58)
    private val LUZ_CABINA = Color(0xFFFFD98A)
    private val BIOLUZ = Color(0xFF5FF2E0)
    private val ALGA = Color(0xFF3E8A5A)
    private val CORAL = Color(0xFFE07A5A)
    private val PLATA = Color(0xFFD6DCE0)
    private val ORO = Color(0xFFE0B03A)
    private val ORO_LUZ = Color(0xFFF6DA86)
    private val ORO_OSC = Color(0xFF8C6418)
    private val AMARILLO = Color(0xFFE8C21E)
    /** La pizarra de buceo y la tinta del lápiz con que se escribe en ella. */
    private val PIZARRA = Color(0xFFF4F2EA)
    private val PIZARRA_NOCHE = Color(0xFFDDDAD0)
    private val TINTA = Color(0xFF0C2430)

    override fun area(m: MuebleVida): Rect = when (m) {
        MuebleVida.SOCIAL -> Rect(0.155f, 0.19f, 0.845f, 0.79f)
        MuebleVida.MAPAS -> Rect(0.175f, 0.2f, 0.825f, 0.71f)
        MuebleVida.OTRAS_VIDAS -> Rect(0.095f, 0.27f, 0.905f, 0.83f)
        MuebleVida.NOTAS -> Rect(0.095f, 0.145f, 0.905f, 0.855f)
    }

    // ---------------------------------------------------------------- ayudas

    /** Un número estable de 0 a 1 (las irregularidades de cada cosa, siempre las mismas). */
    private fun azar(semilla: Int, k: Int): Float {
        var x = semilla * 374761393 + k * 668265263
        x = (x xor (x ushr 13)) * 1274126177
        x = x xor (x ushr 16)
        return (x and 0x7fffffff) / 2147483647f
    }

    private fun mez(a: Color, b: Color, f: Float): Color = lerp(a, b, f.coerceIn(0f, 1f))

    private fun DrawScope.nicho(m: MuebleVida): Rect {
        val a = area(m)
        return Rect(size.width * a.left, size.height * a.top, size.width * a.right, size.height * a.bottom)
    }

    private fun DrawScope.halo(c: Offset, r: Float, color: Color) {
        val rr = r.coerceAtLeast(1f)
        drawCircle(Brush.radialGradient(listOf(color, color.copy(alpha = 0f)), c, rr), rr, c)
    }

    /** Una pieza con volumen: la sombra abajo y a la derecha, la luz arriba y a la izquierda, y la cara. */
    private fun DrawScope.pieza(r: Rect, cara: Color, luz: Color, sombra: Color, g: Float, radio: Float = 0f) {
        drawRoundRect(sombra, r.topLeft, r.size, CornerRadius(radio))
        drawRoundRect(luz, r.topLeft, Size((r.width - g).coerceAtLeast(0f), (r.height - g).coerceAtLeast(0f)), CornerRadius(radio))
        drawRoundRect(cara, Offset(r.left + g, r.top + g), Size((r.width - 2 * g).coerceAtLeast(0f), (r.height - 2 * g).coerceAtLeast(0f)), CornerRadius((radio - g).coerceAtLeast(0f)))
    }

    private fun DrawScope.sombra(r: Rect, u: Float, radio: Float = 0f, alfa: Float = 0.35f) {
        drawRoundRect(NEGRO.copy(alpha = alfa), Offset(r.left + 0.5f * u, r.top + 0.9f * u), r.size, CornerRadius(radio))
    }

    /** Un perno de latón: la cabeza redonda con su brillo y su sombra. */
    private fun DrawScope.perno(c: Offset, r: Float, p: Pal) {
        drawCircle(NEGRO.copy(alpha = 0.45f), r * 1.1f, Offset(c.x + r * 0.3f, c.y + r * 0.35f))
        drawCircle(Brush.radialGradient(listOf(p.latonLuz, p.laton, p.latonOsc), Offset(c.x - r * 0.35f, c.y - r * 0.35f), r * 1.6f), r, c)
    }

    /** Una fila de remaches del casco, de [a] a [b]. */
    private fun DrawScope.remaches(a: Offset, b: Offset, paso: Float, r: Float, p: Pal) {
        val l = hypot(b.x - a.x, b.y - a.y)
        val n = (l / paso).toInt().coerceIn(1, 200)
        val pts = (0..n).map { Offset(a.x + (b.x - a.x) * it / n, a.y + (b.y - a.y) * it / n) }
        drawPoints(pts.map { Offset(it.x + r * 0.3f, it.y + r * 0.35f) }, PointMode.Points, NEGRO.copy(alpha = 0.25f), r * 2f, StrokeCap.Round)
        drawPoints(pts, PointMode.Points, p.remache, r * 2f, StrokeCap.Round)
        drawPoints(pts.map { Offset(it.x - r * 0.3f, it.y - r * 0.3f) }, PointMode.Points, p.cascoLuz, r * 0.7f, StrokeCap.Round)
    }

    /** El casco por dentro: planchas con sus costuras, sus filas de remaches y la curva que oscurece los lados. */
    private fun DrawScope.casco(p: Pal, u: Float) {
        val w = size.width; val h = size.height
        drawRect(Brush.horizontalGradient(listOf(p.cascoOsc, p.casco, p.cascoLuz, p.casco, p.cascoOsc), 0f, w))
        val cols = (w / (40f * u)).toInt().coerceIn(2, 6)
        val pw = w / cols
        for (i in 1 until cols) {
            val x = pw * i
            drawLine(NEGRO.copy(alpha = 0.3f), Offset(x, 0f), Offset(x, h), 0.5f * u)
            drawLine(p.cascoLuz.copy(alpha = 0.6f), Offset(x + 0.5f * u, 0f), Offset(x + 0.5f * u, h), 0.3f * u)
            remaches(Offset(x - 1.6f * u, 2f * u), Offset(x - 1.6f * u, h - 2f * u), 4.5f * u, 0.45f * u, p)
        }
        listOf(h * 0.5f).forEach { y ->
            drawLine(NEGRO.copy(alpha = 0.3f), Offset(0f, y), Offset(w, y), 0.5f * u)
            drawLine(p.cascoLuz.copy(alpha = 0.6f), Offset(0f, y + 0.5f * u), Offset(w, y + 0.5f * u), 0.3f * u)
            remaches(Offset(2f * u, y - 1.6f * u), Offset(w - 2f * u, y - 1.6f * u), 4.5f * u, 0.45f * u, p)
        }
    }

    /** La veta de la teca en [r]: líneas onduladas a lo largo, con algún nudo. */
    private fun DrawScope.veta(r: Rect, p: Pal, semilla: Int, u: Float, vertical: Boolean = false) {
        drawRect(Brush.verticalGradient(listOf(p.tecaLuz, p.teca), r.top, r.bottom), r.topLeft, r.size)
        clipRect(r.left, r.top, r.right, r.bottom) {
            val largo = if (vertical) r.height else r.width
            val ancho = if (vertical) r.width else r.height
            val n = (ancho / (1.6f * u)).toInt().coerceIn(2, 40)
            for (k in 0 until n) {
                val base = ancho * (k + azar(semilla, k)) / n
                val amp = (0.2f + 0.6f * azar(semilla, k + 100)) * u
                val fase = azar(semilla, k + 200) * 6.28f
                val ondas = 1f + 2f * azar(semilla, k + 300)
                val path = Path()
                for (s in 0..16) {
                    val a = largo * s / 16
                    val v = base + amp * sin(fase + ondas * 6.28f * s / 16)
                    val x = if (vertical) r.left + v else r.left + a
                    val y = if (vertical) r.top + a else r.top + v
                    if (s == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                drawPath(path, p.tecaVeta.copy(alpha = 0.25f + 0.4f * azar(semilla, k + 400)), style = Stroke((0.15f + 0.25f * azar(semilla, k + 500)) * u))
            }
            if (largo > 30f * u) {
                val nx = if (vertical) r.center.x else r.left + largo * (0.2f + 0.6f * azar(semilla, 900))
                val ny = if (vertical) r.top + largo * (0.2f + 0.6f * azar(semilla, 901)) else r.center.y
                drawOval(p.tecaVeta.copy(alpha = 0.6f), Offset(nx - 1.4f * u, ny - 0.6f * u), Size(2.8f * u, 1.2f * u))
                drawOval(p.tecaVeta.copy(alpha = 0.4f), Offset(nx - 2.4f * u, ny - 1.1f * u), Size(4.8f * u, 2.2f * u), style = Stroke(0.2f * u))
            }
        }
    }

    /** Una tabla de teca con su veta, su luz arriba y su sombra abajo. */
    private fun DrawScope.tabla(r: Rect, p: Pal, semilla: Int, u: Float, vertical: Boolean = false) {
        veta(r, p, semilla, u, vertical)
        drawLine(p.tecaLuz, Offset(r.left, r.top + 0.2f * u), Offset(r.right, r.top + 0.2f * u), 0.4f * u)
        drawLine(p.tecaVeta, Offset(r.left, r.bottom - 0.2f * u), Offset(r.right, r.bottom - 0.2f * u), 0.4f * u)
    }

    /** Un listón de latón: la cara con su brillo y su filo oscuro. */
    private fun DrawScope.liston(r: Rect, p: Pal, u: Float) {
        drawRect(Brush.verticalGradient(listOf(p.latonLuz, p.laton, p.latonOsc), r.top, r.bottom), r.topLeft, r.size)
        drawLine(Color.White.copy(alpha = 0.35f), Offset(r.left, r.top + r.height * 0.25f), Offset(r.right, r.top + r.height * 0.25f), r.height * 0.15f)
    }

    /** Una tubería con sus bridas: el tubo con su luz y su sombra y, cada tanto, la brida con sus pernos. */
    private fun DrawScope.tubo(a: Offset, b: Offset, g: Float, p: Pal, bridas: List<Float>) {
        val vertical = abs(b.x - a.x) < abs(b.y - a.y)
        val d = if (vertical) Offset(g * 0.25f, 0f) else Offset(0f, g * 0.3f)
        drawLine(NEGRO.copy(alpha = 0.3f), a + d * 2f, b + d * 2f, g, StrokeCap.Butt)
        if (vertical) drawRect(Brush.horizontalGradient(listOf(p.aceroLuz, p.acero, p.aceroOsc), a.x - g / 2, a.x + g / 2), Offset(a.x - g / 2, minOf(a.y, b.y)), Size(g, abs(b.y - a.y)))
        else drawRect(Brush.verticalGradient(listOf(p.aceroLuz, p.acero, p.aceroOsc), a.y - g / 2, a.y + g / 2), Offset(minOf(a.x, b.x), a.y - g / 2), Size(abs(b.x - a.x), g))
        bridas.forEach { f ->
            val c = Offset(a.x + (b.x - a.x) * f, a.y + (b.y - a.y) * f)
            val br = if (vertical) Rect(c.x - g * 0.85f, c.y - g * 0.3f, c.x + g * 0.85f, c.y + g * 0.3f) else Rect(c.x - g * 0.3f, c.y - g * 0.85f, c.x + g * 0.3f, c.y + g * 0.85f)
            pieza(br, p.acero, p.aceroLuz, p.aceroOsc, g * 0.1f, g * 0.1f)
            if (vertical) { drawCircle(p.latonOsc, g * 0.1f, Offset(br.left + g * 0.2f, c.y)); drawCircle(p.latonOsc, g * 0.1f, Offset(br.right - g * 0.2f, c.y)) }
            else { drawCircle(p.latonOsc, g * 0.1f, Offset(c.x, br.top + g * 0.2f)); drawCircle(p.latonOsc, g * 0.1f, Offset(c.x, br.bottom - g * 0.2f)) }
        }
    }

    /** La rueda de una válvula: el aro rojo con sus radios y el cubo. */
    private fun DrawScope.rueda(c: Offset, r: Float, p: Pal) {
        val rojo = if (p.oscuro) mez(ROJO, NEGRO, 0.3f) else ROJO
        drawCircle(NEGRO.copy(alpha = 0.3f), r, Offset(c.x + r * 0.08f, c.y + r * 0.12f), style = Stroke(r * 0.2f))
        for (j in 0 until 5) {
            val a = Math.toRadians(-90.0 + j * 72.0)
            drawLine(mez(rojo, NEGRO, 0.25f), c, Offset(c.x + (r * cos(a)).toFloat(), c.y + (r * sin(a)).toFloat()), r * 0.14f, StrokeCap.Round)
        }
        drawCircle(rojo, r, c, style = Stroke(r * 0.2f))
        drawArc(mez(rojo, Color.White, 0.4f), 200f, 80f, false, Offset(c.x - r, c.y - r), Size(r * 2, r * 2), style = Stroke(r * 0.07f, cap = StrokeCap.Round))
        drawCircle(Brush.radialGradient(listOf(p.latonLuz, p.latonOsc), Offset(c.x - r * 0.1f, c.y - r * 0.1f), r * 0.35f), r * 0.24f, c)
    }

    /** Un manómetro: el bisel de latón, la esfera blanca con su escala, la zona roja y la aguja. */
    private fun DrawScope.manometro(c: Offset, r: Float, p: Pal, semilla: Int) {
        drawCircle(NEGRO.copy(alpha = 0.35f), r * 1.25f, Offset(c.x + r * 0.1f, c.y + r * 0.15f))
        drawCircle(Brush.radialGradient(listOf(p.latonLuz, p.laton, p.latonOsc), Offset(c.x - r * 0.4f, c.y - r * 0.4f), r * 1.8f), r * 1.22f, c)
        drawCircle(p.latonOsc, r * 1.02f, c)
        drawCircle(if (p.oscuro) mez(ESFERA, NEGRO, 0.35f) else ESFERA, r, c)
        drawArc(ROJO.copy(alpha = 0.8f), 0f, 45f, false, Offset(c.x - r * 0.78f, c.y - r * 0.78f), Size(r * 1.56f, r * 1.56f), style = Stroke(r * 0.1f))
        for (t in 0 until 11) {
            val a = Math.toRadians(135.0 + t * 27.0)
            val l = if (t % 5 == 0) 0.62f else 0.72f
            drawLine(TINTA, Offset(c.x + (r * l * cos(a)).toFloat(), c.y + (r * l * sin(a)).toFloat()), Offset(c.x + (r * 0.86f * cos(a)).toFloat(), c.y + (r * 0.86f * sin(a)).toFloat()), r * 0.05f)
        }
        val a = Math.toRadians(150.0 + 220.0 * (0.3f + 0.5f * azar(semilla, 1)))
        drawLine(ROJO, c, Offset(c.x + (r * 0.74f * cos(a)).toFloat(), c.y + (r * 0.74f * sin(a)).toFloat()), r * 0.09f, StrokeCap.Round)
        drawCircle(p.latonOsc, r * 0.12f, c)
        drawArc(Color.White.copy(alpha = 0.45f), 200f, 60f, false, Offset(c.x - r * 0.8f, c.y - r * 0.8f), Size(r * 1.6f, r * 1.6f), style = Stroke(r * 0.07f, cap = StrokeCap.Round))
    }

    /** Lo que se ve por un vidrio: de día, el agua azul con los haces del sol y un pez; de noche, la medusa y el rape. */
    private fun DrawScope.agua(c: Offset, r: Float, p: Pal, e: EstadoMueble, semilla: Int) {
        val vidrio = Path().apply { addOval(Rect(c, r)) }
        clipPath(vidrio) {
            if (!p.oscuro) {
                drawCircle(Brush.verticalGradient(listOf(AGUA_SOL, AGUA, AGUA_HONDA), c.y - r, c.y + r), r, c)
                for (j in 0 until 3) {
                    val x = c.x - r * 0.6f + j * r * 0.5f
                    drawPath(Path().apply { moveTo(x, c.y - r); lineTo(x + r * 0.16f, c.y - r); lineTo(x + r * 0.5f, c.y + r); lineTo(x + r * 0.2f, c.y + r); close() }, Color.White.copy(alpha = 0.12f))
                }
                // Un pez de perfil y unas burbujas.
                val pc = Offset(c.x + r * 0.15f, c.y + r * 0.15f)
                drawOval(AGUA_HONDA, Offset(pc.x - r * 0.3f, pc.y - r * 0.12f), Size(r * 0.6f, r * 0.24f))
                drawPath(Path().apply { moveTo(pc.x + r * 0.26f, pc.y); lineTo(pc.x + r * 0.46f, pc.y - r * 0.14f); lineTo(pc.x + r * 0.46f, pc.y + r * 0.14f); close() }, AGUA_HONDA)
                for (j in 0 until 4) drawCircle(Color.White.copy(alpha = 0.6f), r * (0.04f + 0.02f * j), Offset(c.x - r * 0.4f + azar(semilla, j) * r * 0.1f, c.y + r * 0.4f - j * r * 0.25f), style = Stroke(r * 0.025f))
            } else {
                drawCircle(Brush.radialGradient(listOf(ABISMO_AZUL, ABISMO), Offset(c.x - r * 0.3f, c.y - r * 0.3f), r * 1.4f), r, c)
                for (j in 0 until 10) drawCircle(BIOLUZ.copy(alpha = 0.3f + 0.4f * azar(semilla, j)), r * 0.025f, Offset(c.x + (azar(semilla + 1, j) - 0.5f) * r * 1.8f, c.y + (azar(semilla + 2, j) - 0.5f) * r * 1.8f))
                // La medusa: la campana con su halo y sus tentáculos.
                val m = Offset(c.x + r * 0.22f, c.y - r * 0.22f); val mr = r * 0.26f
                val col = mez(e.secundario, Color.White, 0.25f)
                halo(m, mr * 2.4f, col.copy(alpha = 0.35f))
                drawArc(col, 180f, 180f, true, Offset(m.x - mr, m.y - mr * 0.8f), Size(mr * 2, mr * 1.6f))
                for (j in 0 until 4) {
                    val tx = m.x - mr * 0.6f + j * mr * 0.4f
                    drawPath(Path().apply { moveTo(tx, m.y); quadraticTo(tx + mr * 0.25f, m.y + mr * 0.7f, tx, m.y + mr * 1.4f) }, col.copy(alpha = 0.8f), style = Stroke(r * 0.025f))
                }
                // El rape: la silueta y su señuelo encendido.
                val a = Offset(c.x - r * 0.3f, c.y + r * 0.35f)
                drawOval(Color(0xFF0B1620), Offset(a.x - r * 0.25f, a.y - r * 0.15f), Size(r * 0.5f, r * 0.3f))
                drawPath(Path().apply { moveTo(a.x + r * 0.22f, a.y); lineTo(a.x + r * 0.42f, a.y - r * 0.12f); lineTo(a.x + r * 0.42f, a.y + r * 0.12f); close() }, Color(0xFF0B1620))
                drawPath(Path().apply { moveTo(a.x - r * 0.1f, a.y - r * 0.14f); quadraticTo(a.x - r * 0.2f, a.y - r * 0.4f, a.x - r * 0.36f, a.y - r * 0.3f) }, Color(0xFF1C2A34), style = Stroke(r * 0.025f))
                halo(Offset(a.x - r * 0.36f, a.y - r * 0.3f), r * 0.2f, e.acento.copy(alpha = 0.6f))
                drawCircle(e.acento, r * 0.04f, Offset(a.x - r * 0.36f, a.y - r * 0.3f))
            }
            drawArc(Color.White.copy(alpha = 0.22f), 200f, 55f, false, Offset(c.x - r * 0.8f, c.y - r * 0.8f), Size(r * 1.6f, r * 1.6f), style = Stroke(r * 0.07f, cap = StrokeCap.Round))
        }
    }

    /** Un ojo de buey de latón: la sombra, el aro con sus ocho pernos, la junta y el vidrio con el agua. */
    private fun DrawScope.ojoDeBuey(c: Offset, r: Float, p: Pal, e: EstadoMueble, semilla: Int) {
        drawCircle(NEGRO.copy(alpha = 0.35f), r * 1.34f, Offset(c.x + r * 0.07f, c.y + r * 0.1f))
        drawCircle(Brush.radialGradient(listOf(p.latonLuz, p.laton, p.latonOsc), Offset(c.x - r * 0.5f, c.y - r * 0.5f), r * 2.1f), r * 1.3f, c)
        for (j in 0 until 8) {
            val a = Math.toRadians(j * 45.0)
            drawCircle(p.latonOsc, r * 0.075f, Offset(c.x + (r * 1.15f * cos(a)).toFloat(), c.y + (r * 1.15f * sin(a)).toFloat()))
        }
        drawCircle(GOMA, r * 1.03f, c)
        agua(c, r, p, e, semilla)
    }

    /** La lámpara de jaula: el soporte, la cúpula de latón, el globo con su rejilla y, de noche, la luz encendida. */
    private fun DrawScope.lamparaJaula(c: Offset, r: Float, p: Pal) {
        val encendida = p.oscuro
        if (encendida) halo(c, r * 5f, LUZ_CABINA.copy(alpha = 0.22f))
        drawRect(p.latonOsc, Offset(c.x - r * 0.9f, c.y - r * 1.6f), Size(r * 1.8f, r * 0.45f))
        drawArc(Brush.horizontalGradient(listOf(p.latonLuz, p.latonOsc), c.x - r, c.x + r), 180f, 180f, true, Offset(c.x - r * 1.05f, c.y - r * 1.25f), Size(r * 2.1f, r * 0.9f))
        val globo = Rect(c.x - r * 0.85f, c.y - r * 0.85f, c.x + r * 0.85f, c.y + r)
        drawOval(if (encendida) LUZ_CABINA else Color(0xFFEFE8D6), globo.topLeft, globo.size)
        if (encendida) drawOval(Color.White.copy(alpha = 0.7f), Offset(c.x - r * 0.3f, c.y - r * 0.3f), Size(r * 0.6f, r * 0.7f))
        else drawOval(Color.White.copy(alpha = 0.6f), Offset(c.x - r * 0.55f, c.y - r * 0.6f), Size(r * 0.35f, r * 0.6f))
        for (j in -2..2) drawLine(p.latonOsc, Offset(c.x + j * r * 0.32f, globo.top + r * 0.05f), Offset(c.x + j * r * 0.32f, globo.bottom - r * 0.05f), r * 0.1f)
        drawLine(p.latonOsc, Offset(globo.left, c.y + r * 0.1f), Offset(globo.right, c.y + r * 0.1f), r * 0.12f)
        drawCircle(p.latonOsc, r * 0.2f, Offset(c.x, globo.bottom + r * 0.05f))
    }

    /** Un hueco de agua sólido (el fondo de un nicho): claro de día, casi negro de noche, con una textura muy suave. */
    private fun DrawScope.aguaNicho(forma: Path, n: Rect, p: Pal, u: Float, semilla: Int) {
        drawPath(forma, Brush.verticalGradient(listOf(p.nicho, p.nichoHondo), n.top, n.bottom))
        clipPath(forma) {
            if (!p.oscuro) {
                // Los haces del sol, apenas.
                for (j in 0 until 4) {
                    val x = n.left + n.width * (0.1f + 0.25f * j + 0.08f * azar(semilla, j))
                    drawPath(Path().apply { moveTo(x, n.top); lineTo(x + n.width * 0.05f, n.top); lineTo(x + n.width * 0.14f, n.bottom); lineTo(x + n.width * 0.08f, n.bottom); close() }, Color.White.copy(alpha = 0.28f))
                }
            } else {
                // La nieve marina, apenas.
                val pts = (0 until (n.width * n.height / (60f * u * u)).toInt().coerceIn(10, 200)).map { Offset(n.left + n.width * azar(semilla, it), n.top + n.height * azar(semilla + 1, it)) }
                drawPoints(pts, PointMode.Points, Color(0xFF8FB8C8).copy(alpha = 0.18f), 0.4f * u, StrokeCap.Round)
            }
            drawRect(Brush.verticalGradient(listOf(NEGRO.copy(alpha = if (p.oscuro) 0.45f else 0.1f), Color.Transparent), n.top, n.top + 2.4f * u), n.topLeft, Size(n.width, 2.4f * u))
        }
    }

    /** El piso de chapa estriada. */
    private fun DrawScope.chapaEstriada(r: Rect, p: Pal, u: Float) {
        drawRect(p.piso, r.topLeft, r.size)
        clipRect(r.left, r.top, r.right, r.bottom) {
            var y = r.top + 1.2f * u; var fila = 0
            while (y < r.bottom + 1f * u && fila < 40) {
                var x = r.left + (if (fila % 2 == 0) 0f else 2f * u); var j = 0
                while (x < r.right && j < 200) {
                    val s = if ((j + fila) % 2 == 0) 1f else -1f
                    drawLine(NEGRO.copy(alpha = 0.35f), Offset(x, y + 0.4f * u), Offset(x + 1.6f * u, y + 0.4f * u - s * 0.8f * u), 0.5f * u, StrokeCap.Round)
                    drawLine(Color.White.copy(alpha = if (p.oscuro) 0.08f else 0.2f), Offset(x, y), Offset(x + 1.6f * u, y - s * 0.8f * u), 0.35f * u, StrokeCap.Round)
                    x += 4f * u; j++
                }
                y += 2.6f * u; fila++
            }
        }
        drawLine(NEGRO.copy(alpha = 0.4f), Offset(r.left, r.top), Offset(r.right, r.top), 0.5f * u)
    }

    // ---------------------------------------------------------------- Social: el salón de observación

    override fun DrawScope.muebleSocial(e: EstadoMueble, oscuro: Boolean) {
        if (size.minDimension < 1f) return
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val p = pal(oscuro)
        val n = nicho(MuebleVida.SOCIAL)
        casco(p, u)

        // Las dos tuberías de arriba, con sus bridas, sus abrazaderas y la rueda de la válvula.
        tubo(Offset(0f, h * 0.035f), Offset(w, h * 0.035f), 2.6f * u, p, listOf(0.12f, 0.88f))
        tubo(Offset(0f, h * 0.085f), Offset(w, h * 0.085f), 2f * u, p, listOf(0.3f, 0.7f))
        listOf(0.22f, 0.78f).forEach { f -> drawRect(p.aceroOsc, Offset(w * f - 0.6f * u, h * 0.02f), Size(1.2f * u, h * 0.09f)) }
        drawRect(p.aceroOsc, Offset(w * 0.5f - 0.8f * u, h * 0.035f), Size(1.6f * u, h * 0.05f))
        rueda(Offset(w * 0.5f, h * 0.06f), 3.2f * u, p)

        // El ventanal (el nicho): la sombra, el marco de latón con su corona de pernos y la junta de goma.
        val g = 3.4f * u
        val fr = Rect(n.left - g, n.top - g, n.right + g, n.bottom + g)
        sombra(fr, u, 3f * u, 0.45f)
        drawRoundRect(Brush.linearGradient(listOf(p.latonLuz, p.laton, p.latonOsc), fr.topLeft, fr.bottomRight), fr.topLeft, fr.size, CornerRadius(3f * u))
        drawRoundRect(p.latonOsc, fr.topLeft, fr.size, CornerRadius(3f * u), style = Stroke(0.4f * u))
        drawRoundRect(p.latonLuz.copy(alpha = 0.7f), Offset(fr.left + 0.5f * u, fr.top + 0.5f * u), Size(fr.width - 1f * u, fr.height - 1f * u), CornerRadius(2.6f * u), style = Stroke(0.3f * u))
        val pernos = ArrayList<Offset>()
        val nx = (fr.width / (6f * u)).toInt().coerceAtLeast(4); val ny = (fr.height / (6f * u)).toInt().coerceAtLeast(3)
        for (i in 0..nx) { val x = fr.left + 2.2f * u + (fr.width - 4.4f * u) * i / nx; pernos.add(Offset(x, fr.top + g * 0.5f)); pernos.add(Offset(x, fr.bottom - g * 0.5f)) }
        for (j in 1 until ny) { val y = fr.top + 2.2f * u + (fr.height - 4.4f * u) * j / ny; pernos.add(Offset(fr.left + g * 0.5f, y)); pernos.add(Offset(fr.right - g * 0.5f, y)) }
        pernos.forEach { perno(it, 0.55f * u, p) }
        drawRoundRect(GOMA, Offset(n.left - 0.9f * u, n.top - 0.9f * u), Size(n.width + 1.8f * u, n.height + 1.8f * u), CornerRadius(1.6f * u))
        val forma = Path().apply { addRoundRect(RoundRect(n, CornerRadius(1.2f * u))) }
        aguaNicho(forma, n, p, u, e.semilla)
        // La placa de latón al pie del marco.
        val pl = Rect(w / 2 - 8f * u, fr.bottom - g * 0.8f, w / 2 + 8f * u, fr.bottom + 0.8f * u)
        pieza(pl, p.laton, p.latonLuz, p.latonOsc, 0.3f * u, 0.4f * u)
        listOf(pl.left + 1.2f * u, pl.right - 1.2f * u).forEach { x -> drawCircle(p.latonOsc, 0.35f * u, Offset(x, pl.center.y)) }
        drawLine(p.latonOsc.copy(alpha = 0.7f), Offset(pl.left + 3f * u, pl.center.y), Offset(pl.right - 3f * u, pl.center.y), 0.35f * u)

        // A la izquierda: el manómetro y la válvula roja sobre su tubo.
        val col = fr.left
        val cx = col * 0.5f
        manometro(Offset(cx, h * 0.33f), minOf(col * 0.3f, h * 0.11f), p, e.semilla)
        tubo(Offset(cx, h * 0.52f), Offset(cx, h * 0.84f), 2.2f * u, p, listOf(0.15f))
        rueda(Offset(cx, h * 0.66f), minOf(col * 0.26f, 4.2f * u), p)
        // A la derecha: la lámpara de jaula y el tubo acústico con su bocina.
        val dx = w - col * 0.5f
        drawRect(p.aceroOsc, Offset(dx - 0.5f * u, h * 0.11f), Size(1f * u, h * 0.1f))
        lamparaJaula(Offset(dx, h * 0.31f), minOf(col * 0.26f, 5f * u), p)
        tubo(Offset(dx + col * 0.18f, h * 0.48f), Offset(dx + col * 0.18f, h * 0.84f), 1.6f * u, p, emptyList())
        // La bocina del tubo acústico, con su tapa colgada de una cadenita.
        val bo = Offset(dx + col * 0.18f, h * 0.5f); val bs = minOf(1.5f * u, col * 0.035f)
        drawPath(Path().apply {
            moveTo(bo.x - 0.8f * bs, bo.y + 2f * bs); lineTo(bo.x - 4f * bs, bo.y - 2.6f * bs); lineTo(bo.x + 2.4f * bs, bo.y - 2.6f * bs); lineTo(bo.x + 0.8f * bs, bo.y + 2f * bs); close()
        }, Brush.horizontalGradient(listOf(p.latonLuz, p.laton, p.latonOsc), bo.x - 4f * bs, bo.x + 2.4f * bs))
        drawOval(p.latonOsc, Offset(bo.x - 4.2f * bs, bo.y - 3.3f * bs), Size(6.8f * bs, 1.5f * bs))
        drawOval(NEGRO.copy(alpha = 0.75f), Offset(bo.x - 3.5f * bs, bo.y - 3.05f * bs), Size(5.4f * bs, 1f * bs))
        drawLine(p.latonOsc, Offset(bo.x - 4.2f * bs, bo.y - 2.6f * bs), Offset(bo.x - 5f * bs, bo.y + 1.4f * bs), 0.3f * bs)
        drawCircle(Brush.radialGradient(listOf(p.latonLuz, p.latonOsc), Offset(bo.x - 5.4f * bs, bo.y + 2f * bs), 1.6f * bs), 1.2f * bs, Offset(bo.x - 5f * bs, bo.y + 2.4f * bs))

        // Abajo: el banco de teca con su listón de latón, y el piso de chapa estriada.
        val piso = h * 0.95f
        chapaEstriada(Rect(0f, piso, w, h), p, u)
        val banco = Rect(w * 0.025f, h * 0.865f, w * 0.975f, h * 0.9f)
        drawRect(NEGRO.copy(alpha = 0.3f), Offset(banco.left + 1f * u, banco.bottom), Size(banco.width - 2f * u, 1.4f * u))
        listOf(w * 0.08f, w * 0.5f, w * 0.92f).forEach { x ->
            val pata = Rect(x - 1.8f * u, banco.bottom, x + 1.8f * u, piso)
            pieza(pata, p.acero, p.aceroLuz, p.aceroOsc, 0.3f * u)
        }
        tabla(banco, p, e.semilla + 3, u)
        liston(Rect(banco.left, banco.top - 1f * u, banco.right, banco.top + 0.2f * u), p, u)
        remaches(Offset(banco.left + 2f * u, banco.top - 0.4f * u), Offset(banco.right - 2f * u, banco.top - 0.4f * u), 9f * u, 0.3f * u, p)
    }

    // ---------------------------------------------------------------- Mapas: la mesa de cartas

    override fun DrawScope.muebleMapas(e: EstadoMueble, oscuro: Boolean) {
        if (size.minDimension < 1f) return
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val p = pal(oscuro)
        val n = nicho(MuebleVida.MAPAS)
        casco(p, u)
        tubo(Offset(0f, h * 0.04f), Offset(w, h * 0.04f), 2.4f * u, p, listOf(0.15f, 0.5f, 0.85f))

        // El piso y las patas torneadas con sus zapatas de latón.
        val piso = h * 0.955f
        chapaEstriada(Rect(0f, piso, w, h), p, u)
        drawOval(NEGRO.copy(alpha = 0.3f), Offset(w * 0.05f, piso - 1.2f * u), Size(w * 0.9f, 3f * u))
        listOf(w * 0.09f, w * 0.91f).forEach { x ->
            val pr = Rect(x - 2f * u, h * 0.86f, x + 2f * u, piso - 1.6f * u)
            veta(pr, p, e.semilla + 11, u, vertical = true)
            listOf(0.2f, 0.6f).forEach { f ->
                val y = pr.top + pr.height * f
                drawOval(p.tecaLuz, Offset(x - 2.6f * u, y - 0.9f * u), Size(5.2f * u, 1.8f * u))
                drawOval(p.tecaVeta, Offset(x - 2.6f * u, y - 0.9f * u), Size(5.2f * u, 1.8f * u), style = Stroke(0.25f * u))
            }
            pieza(Rect(x - 2.6f * u, piso - 1.8f * u, x + 2.6f * u, piso + 0.3f * u), p.laton, p.latonLuz, p.latonOsc, 0.3f * u, 0.5f * u)
        }

        // El faldón con sus dos cajones y sus tiradores de latón.
        val fal = Rect(w * 0.035f, h * 0.79f, w * 0.965f, h * 0.865f)
        drawRect(NEGRO.copy(alpha = 0.3f), Offset(fal.left + 1f * u, fal.bottom), Size(fal.width - 2f * u, 1.2f * u))
        tabla(fal, p, e.semilla + 5, u)
        listOf(0.3f, 0.7f).forEach { f ->
            val c = Rect(w * f - w * 0.14f, fal.top + fal.height * 0.18f, w * f + w * 0.14f, fal.bottom - fal.height * 0.18f)
            drawRect(p.tecaVeta.copy(alpha = 0.7f), c.topLeft, c.size, style = Stroke(0.4f * u))
            drawLine(p.tecaLuz, Offset(c.left, c.bottom + 0.3f * u), Offset(c.right, c.bottom + 0.3f * u), 0.3f * u)
            val t = Offset(c.center.x, c.center.y)
            drawRoundRect(NEGRO.copy(alpha = 0.35f), Offset(t.x - 3f * u + 0.3f * u, t.y - 0.7f * u + 0.4f * u), Size(6f * u, 1.4f * u), CornerRadius(0.7f * u))
            drawRoundRect(Brush.verticalGradient(listOf(p.latonLuz, p.latonOsc), t.y - 0.7f * u, t.y + 0.7f * u), Offset(t.x - 3f * u, t.y - 0.7f * u), Size(6f * u, 1.4f * u), CornerRadius(0.7f * u))
            listOf(t.x - 3.6f * u, t.x + 3.6f * u).forEach { x -> perno(Offset(x, t.y), 0.5f * u, p) }
        }

        // El tablero de teca con su moldura de latón.
        val tab = Rect(w * 0.025f, h * 0.115f, w * 0.975f, h * 0.79f)
        sombra(tab, u, 1f * u, 0.4f)
        tabla(tab, p, e.semilla, u)
        // Las juntas entre las tablas del tablero.
        for (j in 1 until 6) {
            val x = tab.left + tab.width * j / 6
            drawLine(p.tecaVeta, Offset(x, tab.top), Offset(x, tab.bottom), 0.45f * u)
            drawLine(p.tecaLuz.copy(alpha = 0.6f), Offset(x + 0.45f * u, tab.top), Offset(x + 0.45f * u, tab.bottom), 0.25f * u)
        }
        val mol = 1.2f * u
        listOf(Rect(tab.left, tab.top, tab.right, tab.top + mol), Rect(tab.left, tab.bottom - mol, tab.right, tab.bottom)).forEach { liston(it, p, u) }
        listOf(Rect(tab.left, tab.top, tab.left + mol, tab.bottom), Rect(tab.right - mol, tab.top, tab.right, tab.bottom)).forEach { r ->
            drawRect(Brush.horizontalGradient(listOf(p.latonLuz, p.laton, p.latonOsc), r.left, r.right), r.topLeft, r.size)
        }
        listOf(tab.topLeft, Offset(tab.right, tab.top), Offset(tab.left, tab.bottom), tab.bottomRight).forEach { c ->
            val sx = if (c.x < w / 2) 1f else -1f; val sy = if (c.y < h / 2) 1f else -1f
            drawPath(Path().apply { moveTo(c.x, c.y); lineTo(c.x + sx * 5f * u, c.y); lineTo(c.x, c.y + sy * 5f * u); close() }, p.laton)
            perno(Offset(c.x + sx * 1.5f * u, c.y + sy * 1.5f * u), 0.5f * u, p)
        }

        // La carta batimétrica (el nicho): el papel con su sombra, sus isóbatas muy tenues y el filete del margen.
        drawRect(NEGRO.copy(alpha = 0.3f), Offset(n.left + 0.6f * u, n.top + 0.9f * u), n.size)
        val forma = Path().apply { addRect(n) }
        drawPath(forma, Brush.verticalGradient(listOf(p.nicho, p.nichoHondo), n.top, n.bottom))
        clipRect(n.left, n.top, n.right, n.bottom) {
            for (j in 0 until 4) {
                val c = Offset(n.left + n.width * (0.25f + 0.5f * azar(e.semilla, j)), n.top + n.height * (0.3f + 0.4f * azar(e.semilla + 1, j)))
                for (anillo in 1..3) drawOval(p.nichoLinea, Offset(c.x - anillo * 5f * u * 1.5f, c.y - anillo * 5f * u), Size(anillo * 15f * u, anillo * 10f * u), style = Stroke(0.25f * u))
            }
        }
        drawRect(mez(p.nichoLinea, NEGRO, 0.1f), Offset(n.left + 1f * u, n.top + 1f * u), Size(n.width - 2f * u, n.height - 2f * u), style = Stroke(0.25f * u))
        // Las pesas de latón en las esquinas de la carta.
        listOf(n.topLeft, Offset(n.right, n.top), Offset(n.left, n.bottom), n.bottomRight).forEach { c ->
            val o = Offset(c.x + (if (c.x < w / 2) -2.2f else 2.2f) * u, c.y + (if (c.y < h / 2) -2.2f else 2.2f) * u)
            drawCircle(NEGRO.copy(alpha = 0.4f), 2.2f * u, Offset(o.x + 0.4f * u, o.y + 0.6f * u))
            drawCircle(Brush.radialGradient(listOf(p.latonLuz, p.laton, p.latonOsc), Offset(o.x - 0.7f * u, o.y - 0.7f * u), 3f * u), 2.1f * u, o)
            drawCircle(p.latonOsc, 0.8f * u, o)
        }

        // Arriba, sobre la teca: la regla paralela con sus dos tablas y sus bisagras.
        val ry = (tab.top + mol + n.top - 2.4f * u) / 2
        val rh = (n.top - tab.top - mol - 4.4f * u) * 0.36f
        listOf(-1f, 1f).forEach { s ->
            val rr = Rect(w * 0.3f, ry + s * rh * 0.6f - rh / 2, w * 0.7f, ry + s * rh * 0.6f + rh / 2)
            drawRect(NEGRO.copy(alpha = 0.3f), Offset(rr.left + 0.4f * u, rr.top + 0.6f * u), rr.size)
            drawRect(Brush.verticalGradient(listOf(Color(0xFF2E3436), Color(0xFF15191A)), rr.top, rr.bottom), rr.topLeft, rr.size)
            var tx = rr.left + 1f * u; var j = 0
            while (tx < rr.right - 0.5f * u && j < 100) { drawLine(Color.White.copy(alpha = 0.6f), Offset(tx, rr.top), Offset(tx, rr.top + rr.height * (if (j % 5 == 0) 0.5f else 0.28f)), 0.18f * u); tx += 1.2f * u; j++ }
        }
        listOf(w * 0.38f, w * 0.62f).forEach { x -> drawLine(p.latonLuz, Offset(x, ry - rh * 0.6f), Offset(x, ry + rh * 0.6f), 0.6f * u, StrokeCap.Round) }

        // A la izquierda: el farol (encendido de noche) y el compás de puntas.
        val col = n.left - 2.4f * u - tab.left
        val ix = tab.left + mol + col * 0.5f
        val fc = Offset(ix, h * 0.34f); val fr = minOf(col * 0.2f, 4f * u)
        if (oscuro) halo(fc, fr * 5f, LUZ_CABINA.copy(alpha = 0.25f))
        drawOval(NEGRO.copy(alpha = 0.35f), Offset(fc.x - fr * 1.3f, fc.y + fr * 1.6f), Size(fr * 2.8f, fr * 0.7f))
        drawRect(p.latonOsc, Offset(fc.x - fr * 1.1f, fc.y + fr * 1.2f), Size(fr * 2.2f, fr * 0.6f))
        drawRoundRect(if (oscuro) LUZ_CABINA else Color(0xFFF2EAD2), Offset(fc.x - fr * 0.8f, fc.y - fr * 1.1f), Size(fr * 1.6f, fr * 2.3f), CornerRadius(fr * 0.6f))
        drawRoundRect(Color.White.copy(alpha = 0.5f), Offset(fc.x - fr * 0.55f, fc.y - fr * 0.8f), Size(fr * 0.3f, fr * 1.6f), CornerRadius(fr * 0.15f))
        drawCircle(if (oscuro) Color.White else Color(0xFFE8A040), fr * 0.22f, Offset(fc.x, fc.y + fr * 0.3f))
        listOf(-0.9f, 0.9f).forEach { s -> drawLine(p.latonOsc, Offset(fc.x + s * fr, fc.y - fr * 1.1f), Offset(fc.x + s * fr, fc.y + fr * 1.2f), fr * 0.2f) }
        drawPath(Path().apply { moveTo(fc.x - fr * 1.1f, fc.y - fr * 1.1f); lineTo(fc.x - fr * 0.4f, fc.y - fr * 1.7f); lineTo(fc.x + fr * 0.4f, fc.y - fr * 1.7f); lineTo(fc.x + fr * 1.1f, fc.y - fr * 1.1f); close() }, p.laton)
        drawArc(p.latonOsc, 180f, 180f, false, Offset(fc.x - fr * 0.5f, fc.y - fr * 2.4f), Size(fr * 1f, fr * 1.2f), style = Stroke(fr * 0.18f))
        // El compás de puntas: las dos patas de latón con su charnela.
        val cc = Offset(ix, h * 0.55f); val cl = minOf(h * 0.16f, col * 0.8f)
        listOf(-1f, 1f).forEach { s ->
            val pt = Offset(cc.x + s * cl * 0.28f, cc.y + cl)
            drawLine(NEGRO.copy(alpha = 0.3f), Offset(cc.x + 0.4f * u, cc.y + 0.6f * u), Offset(pt.x + 0.4f * u, pt.y + 0.6f * u), 0.9f * u, StrokeCap.Round)
            drawLine(p.laton, cc, pt, 0.8f * u, StrokeCap.Round)
            drawLine(p.latonLuz, Offset(cc.x - 0.2f * u, cc.y), Offset(pt.x - 0.2f * u, pt.y - 1.6f * u), 0.25f * u)
            drawLine(PLATA, Offset(pt.x - s * 0.15f * cl * 0.28f / cl, pt.y - 1.6f * u), pt, 0.5f * u, StrokeCap.Round)
        }
        drawCircle(p.latonOsc, 1.3f * u, cc); drawCircle(p.latonLuz, 0.6f * u, Offset(cc.x - 0.3f * u, cc.y - 0.3f * u))

        // A la derecha: la bitácora de la brújula y el casco de buzo.
        val dx = tab.right - mol - col * 0.5f
        val bc = Offset(dx, h * 0.3f); val br = minOf(col * 0.3f, 6f * u)
        drawCircle(NEGRO.copy(alpha = 0.35f), br * 1.2f, Offset(bc.x + 0.4f * u, bc.y + 0.6f * u))
        drawCircle(Brush.radialGradient(listOf(p.latonLuz, p.laton, p.latonOsc), Offset(bc.x - br * 0.4f, bc.y - br * 0.4f), br * 1.8f), br * 1.2f, bc)
        drawCircle(p.latonOsc, br * 0.98f, bc)
        drawCircle(if (oscuro) mez(ESFERA, NEGRO, 0.35f) else ESFERA, br * 0.92f, bc)
        for (j in 0 until 8) {
            val a = Math.toRadians(j * 45.0)
            drawLine(TINTA.copy(alpha = 0.6f), Offset(bc.x + (br * 0.7f * cos(a)).toFloat(), bc.y + (br * 0.7f * sin(a)).toFloat()), Offset(bc.x + (br * 0.86f * cos(a)).toFloat(), bc.y + (br * 0.86f * sin(a)).toFloat()), 0.2f * u)
        }
        drawPath(Path().apply { moveTo(bc.x, bc.y - br * 0.75f); lineTo(bc.x + br * 0.16f, bc.y); lineTo(bc.x - br * 0.16f, bc.y); close() }, ROJO)
        drawPath(Path().apply { moveTo(bc.x, bc.y + br * 0.75f); lineTo(bc.x + br * 0.16f, bc.y); lineTo(bc.x - br * 0.16f, bc.y); close() }, TINTA)
        drawCircle(p.latonLuz, br * 0.1f, bc)
        drawArc(Color.White.copy(alpha = 0.5f), 200f, 60f, false, Offset(bc.x - br * 0.8f, bc.y - br * 0.8f), Size(br * 1.6f, br * 1.6f), style = Stroke(0.35f * u, cap = StrokeCap.Round))
        cascoBuzo(Offset(dx, h * 0.6f), minOf(col * 0.32f, h * 0.085f), p)
    }

    /** El casco de buzo de cobre: la pechera atornillada, la cúpula, la mirilla de frente, las laterales y la válvula. */
    private fun DrawScope.cascoBuzo(c: Offset, r: Float, p: Pal) {
        val f = if (p.oscuro) 0.35f else 0f
        val cobre = mez(COBRE, NEGRO, f); val cobreLuz = mez(COBRE_LUZ, NEGRO, f); val cobreOsc = mez(COBRE_OSC, NEGRO, f)
        drawOval(NEGRO.copy(alpha = 0.35f), Offset(c.x - r * 1.4f, c.y + r * 1.05f), Size(r * 2.8f, r * 0.5f))
        // La pechera.
        val pe = Path().apply { moveTo(c.x - r * 0.75f, c.y + r * 0.55f); lineTo(c.x + r * 0.75f, c.y + r * 0.55f); lineTo(c.x + r * 1.3f, c.y + r * 1.2f); lineTo(c.x - r * 1.3f, c.y + r * 1.2f); close() }
        drawPath(pe, Brush.horizontalGradient(listOf(cobreLuz, cobre, cobreOsc), c.x - r * 1.3f, c.x + r * 1.3f))
        listOf(-0.9f, -0.3f, 0.3f, 0.9f).forEach { s -> drawCircle(p.latonLuz, r * 0.07f, Offset(c.x + s * r, c.y + r * 1.0f)) }
        // La cúpula.
        drawCircle(Brush.radialGradient(listOf(cobreLuz, cobre, cobreOsc), Offset(c.x - r * 0.4f, c.y - r * 0.45f), r * 1.5f), r, c)
        // Las mirillas laterales y la del frente, con sus aros y su rejilla.
        listOf(-1f, 1f).forEach { s ->
            val lc = Offset(c.x + s * r * 0.78f, c.y + r * 0.05f)
            drawOval(p.latonOsc, Offset(lc.x - r * 0.18f, lc.y - r * 0.28f), Size(r * 0.36f, r * 0.56f))
            drawOval(Color(0xFF0E1A22), Offset(lc.x - r * 0.12f, lc.y - r * 0.2f), Size(r * 0.24f, r * 0.4f))
        }
        val fc = Offset(c.x, c.y + r * 0.05f)
        drawCircle(Brush.radialGradient(listOf(p.latonLuz, p.laton, p.latonOsc), Offset(fc.x - r * 0.2f, fc.y - r * 0.2f), r * 0.7f), r * 0.5f, fc)
        drawCircle(Color(0xFF0E1A22), r * 0.37f, fc)
        drawLine(p.laton, Offset(fc.x - r * 0.37f, fc.y), Offset(fc.x + r * 0.37f, fc.y), r * 0.06f)
        drawLine(p.laton, Offset(fc.x, fc.y - r * 0.37f), Offset(fc.x, fc.y + r * 0.37f), r * 0.06f)
        drawArc(Color.White.copy(alpha = 0.45f), 200f, 60f, false, Offset(fc.x - r * 0.3f, fc.y - r * 0.3f), Size(r * 0.6f, r * 0.6f), style = Stroke(r * 0.06f))
        // La válvula de arriba.
        drawRect(p.latonOsc, Offset(c.x + r * 0.35f, c.y - r * 1.15f), Size(r * 0.2f, r * 0.3f))
        drawCircle(ROJO, r * 0.14f, Offset(c.x + r * 0.45f, c.y - r * 1.2f))
        drawArc(Color.White.copy(alpha = 0.35f), 200f, 50f, false, Offset(c.x - r * 0.85f, c.y - r * 0.85f), Size(r * 1.7f, r * 1.7f), style = Stroke(r * 0.08f, cap = StrokeCap.Round))
    }

    // ---------------------------------------------------------------- Otras vidas: la vitrina de perlas

    override fun DrawScope.muebleOtrasVidas(e: EstadoMueble, oscuro: Boolean) {
        if (size.minDimension < 1f) return
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val p = pal(oscuro)
        val n = nicho(MuebleVida.OTRAS_VIDAS)
        casco(p, u)
        val piso = h * 0.955f
        chapaEstriada(Rect(0f, piso, w, h), p, u)

        // El cuerpo de la vitrina: el acero remachado con su sombra.
        val cuerpo = Rect(w * 0.015f, h * 0.035f, w * 0.985f, h * 0.94f)
        sombra(cuerpo, u, 1f * u, 0.45f)
        pieza(cuerpo, p.acero, p.aceroLuz, p.aceroOsc, 0.5f * u, 1f * u)

        // La cornisa de teca con su listón y, debajo, el friso con el ojo de buey a la medusa y dos manómetros.
        val cor = Rect(cuerpo.left - 0.6f * u, cuerpo.top - 1.6f * u, cuerpo.right + 0.6f * u, cuerpo.top + 3.4f * u)
        sombra(cor, u)
        tabla(cor, p, e.semilla + 2, u)
        liston(Rect(cor.left, cor.bottom - 0.9f * u, cor.right, cor.bottom), p, u)
        val fri = Rect(cuerpo.left + 1.2f * u, cor.bottom + 0.6f * u, cuerpo.right - 1.2f * u, n.top - 2.6f * u)
        pieza(fri, p.casco, p.cascoLuz, p.cascoOsc, 0.4f * u, 0.4f * u)
        remaches(Offset(fri.left + 2f * u, fri.top + 1.4f * u), Offset(fri.right - 2f * u, fri.top + 1.4f * u), 4.5f * u, 0.35f * u, p)
        remaches(Offset(fri.left + 2f * u, fri.bottom - 1.4f * u), Offset(fri.right - 2f * u, fri.bottom - 1.4f * u), 4.5f * u, 0.35f * u, p)
        val oc = Offset(w / 2, fri.center.y); val or = fri.height * 0.34f
        ojoDeBuey(oc, or, p, e, e.semilla + 5)
        listOf(-1f, 1f).forEach { s ->
            manometro(Offset(w / 2 + s * w * 0.22f, fri.center.y), fri.height * 0.25f, p, e.semilla + s.toInt() + 3)
            tubo(Offset(w / 2 + s * (or * 1.45f), fri.center.y), Offset(w / 2 + s * (w * 0.22f - fri.height * 0.32f), fri.center.y), 1.4f * u, p, listOf(0.5f))
            lamparaJaula(Offset(w / 2 + s * w * 0.38f, fri.center.y + 0.4f * u), fri.height * 0.15f, p)
        }

        // Las cinco hornacinas (el nicho): el agua con su arco de latón y unas algas en los rincones.
        drawRect(p.aceroOsc, Offset(n.left - 1f * u, n.top - 1f * u), Size(n.width + 2f * u, n.height + 2f * u))
        val cw = n.width / 5
        for (i in 0 until 5) {
            val b = Rect(n.left + cw * i + 0.8f * u, n.top, n.left + cw * (i + 1) - 0.8f * u, n.bottom)
            val arco = minOf(b.width * 0.5f, b.height * 0.3f)
            val forma = Path().apply { addRoundRect(RoundRect(b, CornerRadius(arco, arco), CornerRadius(arco, arco), CornerRadius(0.6f * u), CornerRadius(0.6f * u))) }
            aguaNicho(forma, b, p, u, e.semilla + i)
            clipPath(forma) {
                // Las algas y el coral en los rincones de abajo, pequeños.
                val y0 = b.bottom
                listOf(b.left + 1.4f * u to 1f, b.right - 1.4f * u to -1f).forEachIndexed { j, (x, s) ->
                    val col = if ((i + j) % 2 == 0) mez(ALGA, NEGRO, if (oscuro) 0.45f else 0f) else mez(CORAL, NEGRO, if (oscuro) 0.45f else 0f)
                    for (t in 0 until 3) {
                        val tx = x + s * t * 1.1f * u
                        drawPath(Path().apply { moveTo(tx, y0); quadraticTo(tx + s * 1.4f * u, y0 - 2.5f * u, tx + s * 0.3f * u, y0 - (3.6f + t * 0.8f) * u) }, col, style = Stroke(0.55f * u, cap = StrokeCap.Round))
                    }
                }
                drawRect(Brush.verticalGradient(listOf(Color.Transparent, NEGRO.copy(alpha = if (oscuro) 0.3f else 0.06f)), b.bottom - 3f * u, b.bottom), Offset(b.left, b.bottom - 3f * u), Size(b.width, 3f * u))
            }
            drawPath(forma, p.latonOsc, style = Stroke(0.8f * u))
            drawPath(forma, p.latonLuz.copy(alpha = 0.7f), style = Stroke(0.25f * u))
        }
        // Los montantes de acero entre las hornacinas, con sus remaches.
        for (i in 0..5) {
            val x = n.left + cw * i
            val m = Rect(x - 0.9f * u, n.top - 1f * u, x + 0.9f * u, n.bottom + 1f * u)
            pieza(m, p.acero, p.aceroLuz, p.aceroOsc, 0.25f * u)
            remaches(Offset(x, n.top + 1.6f * u), Offset(x, n.bottom - 1.6f * u), 5f * u, 0.3f * u, p)
        }
        // Las dos columnas: tubos de latón con su brida arriba y abajo.
        listOf((cuerpo.left + n.left - 1f * u) / 2, (cuerpo.right + n.right + 1f * u) / 2).forEach { x ->
            val g = minOf((n.left - 1f * u - cuerpo.left) * 0.55f, 3f * u)
            drawRect(Brush.horizontalGradient(listOf(p.latonLuz, p.laton, p.latonOsc), x - g / 2, x + g / 2), Offset(x - g / 2, n.top - 1f * u), Size(g, n.height + 2f * u))
            listOf(n.top - 1f * u, n.bottom + 1f * u).forEach { y ->
                pieza(Rect(x - g * 0.85f, y - g * 0.4f, x + g * 0.85f, y + g * 0.4f), p.laton, p.latonLuz, p.latonOsc, 0.25f * u, 0.3f * u)
            }
            for (j in 1..3) drawLine(p.latonOsc, Offset(x - g / 2, n.top + n.height * j / 4), Offset(x + g / 2, n.top + n.height * j / 4), 0.4f * u)
        }

        // El zócalo de teca con una placa de latón por hornacina.
        val zoc = Rect(cuerpo.left + 1f * u, n.bottom + 2f * u, cuerpo.right - 1f * u, cuerpo.bottom - 0.8f * u)
        tabla(zoc, p, e.semilla + 7, u)
        liston(Rect(zoc.left, zoc.top, zoc.right, zoc.top + 0.8f * u), p, u)
        for (i in 0 until 5) {
            val cx = n.left + cw * (i + 0.5f)
            val pl = Rect(cx - cw * 0.24f, zoc.center.y - zoc.height * 0.2f, cx + cw * 0.24f, zoc.center.y + zoc.height * 0.3f)
            pieza(pl, p.laton, p.latonLuz, p.latonOsc, 0.25f * u, 0.4f * u)
            listOf(pl.left + 1f * u, pl.right - 1f * u).forEach { x -> drawCircle(p.latonOsc, 0.3f * u, Offset(x, pl.center.y)) }
        }
        listOf(w * 0.07f, w * 0.93f).forEach { x ->
            pieza(Rect(x - 3f * u, cuerpo.bottom - 0.4f * u, x + 3f * u, piso + 0.3f * u), p.laton, p.latonLuz, p.latonOsc, 0.3f * u, 0.5f * u)
        }
    }

    // ---------------------------------------------------------------- Íconos de los mapas

    override fun DrawScope.iconoMapa(id: String, e: EstadoIcono, oscuro: Boolean) {
        if (size.minDimension < 1f) return
        val k = minOf(size.height / 70f, size.width / 160f)
        val c = Offset(size.width / 2, size.height / 2)
        val p = pal(oscuro)
        when (id) {
            "unesco" -> pecio(c, k, e, p)
            "rascar" -> ojoRascar(c, k, e, p)
            else -> boyas(c, k, e, p)
        }
    }

    /** El pecio: el casco de madera escorado sobre la arena, con su mástil roto, sus portas, algas, coral y burbujas. */
    private fun DrawScope.pecio(c: Offset, k: Float, e: EstadoIcono, p: Pal) {
        // La arena del fondo.
        val arena = Path().apply { moveTo(c.x - 60f * k, c.y + 28f * k); quadraticTo(c.x - 20f * k, c.y + 16f * k, c.x + 10f * k, c.y + 20f * k); quadraticTo(c.x + 40f * k, c.y + 24f * k, c.x + 60f * k, c.y + 18f * k); lineTo(c.x + 60f * k, c.y + 30f * k); lineTo(c.x - 60f * k, c.y + 30f * k); close() }
        drawPath(arena, Brush.verticalGradient(listOf(ARENA, ARENA_OSC), c.y + 16f * k, c.y + 30f * k))
        rotate(-8f, Offset(c.x, c.y + 14f * k)) {
            // El mástil roto y su verga.
            drawLine(Color(0xFF5A3A1E), Offset(c.x - 4f * k, c.y + 4f * k), Offset(c.x - 2f * k, c.y - 26f * k), 2f * k, StrokeCap.Round)
            drawLine(Color(0xFF5A3A1E), Offset(c.x - 12f * k, c.y - 16f * k), Offset(c.x + 8f * k, c.y - 18f * k), 1.3f * k, StrokeCap.Round)
            drawPath(Path().apply { moveTo(c.x - 11f * k, c.y - 15f * k); quadraticTo(c.x - 4f * k, c.y - 8f * k, c.x - 10f * k, c.y - 2f * k) }, Color(0xFFD9CFB4).copy(alpha = 0.8f), style = Stroke(0.8f * k))
            // El casco.
            val casco = Path().apply {
                moveTo(c.x - 36f * k, c.y + 2f * k); lineTo(c.x + 30f * k, c.y + 2f * k); lineTo(c.x + 38f * k, c.y - 4f * k)
                quadraticTo(c.x + 30f * k, c.y + 18f * k, c.x + 14f * k, c.y + 20f * k); lineTo(c.x - 24f * k, c.y + 20f * k)
                quadraticTo(c.x - 34f * k, c.y + 14f * k, c.x - 36f * k, c.y + 2f * k); close()
            }
            drawPath(casco, Brush.verticalGradient(listOf(Color(0xFF8A5A30), Color(0xFF5A3A1E), Color(0xFF3A2410)), c.y, c.y + 20f * k))
            clipPath(casco) {
                for (j in 1..4) drawLine(Color(0xFF2A1A0A).copy(alpha = 0.6f), Offset(c.x - 40f * k, c.y + 2f * k + j * 4f * k), Offset(c.x + 40f * k, c.y + 2f * k + j * 4f * k), 0.5f * k)
                drawPath(Path().apply { moveTo(c.x + 8f * k, c.y + 8f * k); lineTo(c.x + 14f * k, c.y + 6f * k); lineTo(c.x + 16f * k, c.y + 14f * k); lineTo(c.x + 9f * k, c.y + 15f * k); close() }, Color(0xFF140C04))
            }
            listOf(-24f, -14f, -4f).forEach { dx ->
                drawCircle(p.latonLuz, 1.7f * k, Offset(c.x + dx * k, c.y + 8f * k))
                drawCircle(Color(0xFF140C04), 1.1f * k, Offset(c.x + dx * k, c.y + 8f * k))
            }
            drawLine(Color(0xFFA8784A), Offset(c.x - 36f * k, c.y + 2f * k), Offset(c.x + 38f * k, c.y - 4f * k), 0.8f * k)
        }
        // Algas, coral y burbujas.
        listOf(-46f, -40f, 44f).forEachIndexed { j, dx ->
            drawPath(Path().apply { moveTo(c.x + dx * k, c.y + 26f * k); quadraticTo(c.x + (dx + 4f) * k, c.y + 16f * k, c.x + (dx - 1f) * k, c.y + (8f - j * 2f) * k) }, ALGA, style = Stroke(1.4f * k, cap = StrokeCap.Round))
        }
        drawPath(Path().apply { moveTo(c.x + 34f * k, c.y + 22f * k); lineTo(c.x + 34f * k, c.y + 14f * k); moveTo(c.x + 34f * k, c.y + 18f * k); lineTo(c.x + 30f * k, c.y + 13f * k); moveTo(c.x + 34f * k, c.y + 17f * k); lineTo(c.x + 38f * k, c.y + 12f * k) }, CORAL, style = Stroke(1.4f * k, cap = StrokeCap.Round))
        for (j in 0 until 4) drawCircle(e.c1.copy(alpha = 0.8f), (1f + j * 0.35f) * k, Offset(c.x + (6f + j * 1.5f) * k, c.y - (8f + j * 6f) * k), style = Stroke(0.5f * k))
        // Un pez que pasa.
        val pz = Offset(c.x + 42f * k, c.y - 16f * k)
        drawOval(e.c2, Offset(pz.x - 5f * k, pz.y - 2f * k), Size(10f * k, 4f * k))
        drawPath(Path().apply { moveTo(pz.x + 4f * k, pz.y); lineTo(pz.x + 8f * k, pz.y - 2.6f * k); lineTo(pz.x + 8f * k, pz.y + 2.6f * k); close() }, e.c2)
        drawCircle(Color.White, 0.6f * k, Offset(pz.x - 3f * k, pz.y - 0.4f * k))
    }

    /** El ojo de buey para rascar: el agua a la izquierda, el vaho de plata a la derecha y el doblón que lo raspa. */
    private fun DrawScope.ojoRascar(c: Offset, k: Float, e: EstadoIcono, p: Pal) {
        val oc = Offset(c.x - 6f * k, c.y); val r = 22f * k
        drawCircle(NEGRO.copy(alpha = 0.35f), r * 1.3f, Offset(oc.x + 1.2f * k, oc.y + 1.8f * k))
        drawCircle(Brush.radialGradient(listOf(p.latonLuz, p.laton, p.latonOsc), Offset(oc.x - r * 0.5f, oc.y - r * 0.5f), r * 2f), r * 1.28f, oc)
        for (j in 0 until 8) {
            val a = Math.toRadians(j * 45.0 + 22.5)
            drawCircle(p.latonOsc, 1.2f * k, Offset(oc.x + (r * 1.14f * cos(a)).toFloat(), oc.y + (r * 1.14f * sin(a)).toFloat()))
        }
        drawCircle(GOMA, r * 1.03f, oc)
        clipPath(Path().apply { addOval(Rect(oc, r)) }) {
            drawCircle(Brush.verticalGradient(listOf(AGUA_SOL, AGUA, AGUA_HONDA), oc.y - r, oc.y + r), r, oc)
            drawPath(Path().apply { moveTo(oc.x - r, oc.y + r * 0.6f); quadraticTo(oc.x - r * 0.4f, oc.y + r * 0.4f, oc.x, oc.y + r * 0.7f); lineTo(oc.x, oc.y + r); lineTo(oc.x - r, oc.y + r); close() }, ARENA)
            drawPath(Path().apply { moveTo(oc.x - r * 0.6f, oc.y - r * 0.3f); quadraticTo(oc.x - r * 0.2f, oc.y - r * 0.5f, oc.x + r * 0.1f, oc.y - r * 0.2f) }, e.c1.copy(alpha = 0.9f), style = Stroke(0.8f * k, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.6f * k, 1.2f * k))))
            drawCircle(ROJO, 1.4f * k, Offset(oc.x + r * 0.1f, oc.y - r * 0.2f))
            val capa = Path().apply {
                moveTo(oc.x + r, oc.y - r); lineTo(oc.x + r * 0.15f, oc.y - r)
                for (j in 0..8) lineTo(oc.x + r * (0.15f + 0.12f * azar(9, j) - 0.06f * (j % 2)), oc.y - r + 2 * r * j / 8f)
                lineTo(oc.x + r, oc.y + r); close()
            }
            drawPath(capa, Brush.linearGradient(listOf(Color(0xFFE6EAEC), Color(0xFFADB6BC), Color(0xFFD4DADE)), Offset(oc.x, oc.y - r), Offset(oc.x + r, oc.y + r)))
            clipPath(capa) { for (j in 0 until 8) drawLine(Color.White.copy(alpha = 0.5f), Offset(oc.x + j * r * 0.18f - r * 0.2f, oc.y + r), Offset(oc.x + j * r * 0.18f, oc.y - r), 0.4f * k) }
            drawArc(Color.White.copy(alpha = 0.3f), 200f, 55f, false, Offset(oc.x - r * 0.8f, oc.y - r * 0.8f), Size(r * 1.6f, r * 1.6f), style = Stroke(1.2f * k, cap = StrokeCap.Round))
        }
        // El doblón: oro con su canto, la cruz de anclas en relieve.
        val m = Offset(oc.x + r * 1.3f, oc.y + r * 0.7f); val mr = 11f * k
        drawCircle(NEGRO.copy(alpha = 0.35f), mr, Offset(m.x + 1.2f * k, m.y + 1.8f * k))
        drawCircle(Brush.radialGradient(listOf(ORO_LUZ, ORO, ORO_OSC), Offset(m.x - mr * 0.4f, m.y - mr * 0.4f), mr * 1.6f), mr, m)
        drawCircle(ORO_OSC, mr * 0.82f, m, style = Stroke(0.6f * k, pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.8f * k, 0.6f * k))))
        ancla(m, mr * 0.55f, ORO_OSC, 0.9f * k)
        drawArc(Color.White.copy(alpha = 0.5f), 200f, 60f, false, Offset(m.x - mr * 0.9f, m.y - mr * 0.9f), Size(mr * 1.8f, mr * 1.8f), style = Stroke(0.8f * k, cap = StrokeCap.Round))
    }

    /** Un ancla: la caña, el cepo, la argolla y los brazos con sus uñas. */
    private fun DrawScope.ancla(c: Offset, r: Float, color: Color, g: Float) {
        drawLine(color, Offset(c.x, c.y - r * 0.7f), Offset(c.x, c.y + r * 0.9f), g, StrokeCap.Round)
        drawLine(color, Offset(c.x - r * 0.45f, c.y - r * 0.45f), Offset(c.x + r * 0.45f, c.y - r * 0.45f), g, StrokeCap.Round)
        drawCircle(color, r * 0.22f, Offset(c.x, c.y - r * 0.9f), style = Stroke(g * 0.8f))
        drawArc(color, 20f, 140f, false, Offset(c.x - r * 0.85f, c.y - r * 0.15f), Size(r * 1.7f, r * 1.1f), style = Stroke(g, cap = StrokeCap.Round))
        listOf(-1f, 1f).forEach { s -> drawLine(color, Offset(c.x + s * r * 0.8f, c.y + r * 0.35f), Offset(c.x + s * r * 0.95f, c.y + r * 0.1f), g, StrokeCap.Round) }
    }

    /** Tres boyas en la superficie, cada una con su cadena hasta el ancla en la arena. */
    private fun DrawScope.boyas(c: Offset, k: Float, e: EstadoIcono, p: Pal) {
        val sup = c.y - 10f * k
        // El agua bajo la superficie, la ola y el fondo de arena.
        val agua = Path().apply {
            moveTo(c.x - 62f * k, sup)
            for (j in 0..12) { val x = c.x - 62f * k + j * 124f * k / 12; quadraticTo(x - 5f * k, sup + if (j % 2 == 0) -1.8f * k else 1.8f * k, x, sup) }
            lineTo(c.x + 62f * k, c.y + 26f * k); quadraticTo(c.x, c.y + 31f * k, c.x - 62f * k, c.y + 26f * k); close()
        }
        drawPath(agua, Brush.verticalGradient(listOf(AGUA_SOL.copy(alpha = 0.9f), AGUA_HONDA), sup, c.y + 30f * k))
        drawPath(Path().apply { moveTo(c.x - 62f * k, c.y + 24f * k); quadraticTo(c.x - 10f * k, c.y + 18f * k, c.x + 62f * k, c.y + 23f * k); lineTo(c.x + 62f * k, c.y + 26f * k); quadraticTo(c.x, c.y + 31f * k, c.x - 62f * k, c.y + 26f * k); close() }, ARENA)
        drawLine(Color.White.copy(alpha = 0.7f), Offset(c.x - 60f * k, sup), Offset(c.x + 60f * k, sup), 0.6f * k)
        listOf(e.c1, e.c2, ROJO).forEachIndexed { j, col ->
            val bx = c.x + (j - 1) * 38f * k
            val fondo = c.y + 22f * k - (if (j == 1) 2.5f else 0f) * k
            // La cadena, eslabón a eslabón, y el ancla.
            var y = sup + 4f * k; var t = 0
            while (y < fondo - 4f * k && t < 20) {
                if (t % 2 == 0) drawOval(Color(0xFF5A6670), Offset(bx - 0.8f * k, y), Size(1.6f * k, 2.6f * k), style = Stroke(0.5f * k))
                else drawLine(Color(0xFF5A6670), Offset(bx, y), Offset(bx, y + 2.6f * k), 0.6f * k)
                y += 2.2f * k; t++
            }
            ancla(Offset(bx, fondo - 2f * k), 3.6f * k, Color(0xFF3A4650), 0.9f * k)
            // La boya: el cuerpo con sus franjas, la vara y la luz.
            val b = Rect(bx - 5f * k, sup - 7f * k, bx + 5f * k, sup + 4f * k)
            drawOval(NEGRO.copy(alpha = 0.25f), Offset(b.left, sup + 2f * k), Size(b.width, 2.4f * k))
            val cuerpo = Path().apply { addRoundRect(RoundRect(b, CornerRadius(5f * k, 5f * k))) }
            drawPath(cuerpo, Brush.horizontalGradient(listOf(mez(col, Color.White, 0.35f), col, mez(col, NEGRO, 0.35f)), b.left, b.right))
            clipPath(cuerpo) { drawRect(Color.White.copy(alpha = 0.9f), Offset(b.left, b.top + b.height * 0.4f), Size(b.width, b.height * 0.2f)) }
            drawLine(Color(0xFF3A4650), Offset(bx, b.top), Offset(bx, b.top - 7f * k), 0.8f * k)
            val lz = Offset(bx, b.top - 7.6f * k)
            halo(lz, 5f * k, col.copy(alpha = 0.45f))
            drawCircle(col, 1.4f * k, lz)
            drawCircle(Color.White.copy(alpha = 0.7f), 0.5f * k, Offset(lz.x - 0.4f * k, lz.y - 0.4f * k))
        }
    }

    // ---------------------------------------------------------------- Notas: la pizarra de a bordo

    override fun DrawScope.tableroNotas(e: EstadoMueble, oscuro: Boolean) {
        if (size.minDimension < 1f) return
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val p = pal(oscuro)
        val n = nicho(MuebleVida.NOTAS)
        casco(p, u)
        // La tubería de arriba con la lámpara de jaula colgada al centro.
        tubo(Offset(0f, h * 0.03f), Offset(w, h * 0.03f), 2.2f * u, p, listOf(0.2f, 0.8f))
        drawRect(p.aceroOsc, Offset(w / 2 - 0.5f * u, h * 0.03f), Size(1f * u, h * 0.04f))
        lamparaJaula(Offset(w / 2, h * 0.085f), 2.4f * u, p)

        // Los dos tubos verticales de los costados, con su válvula.
        val fr = Rect(n.left - 3f * u, n.top - 3f * u, n.right + 3f * u, n.bottom + 3f * u)
        listOf(fr.left * 0.5f, w - fr.left * 0.5f).forEachIndexed { j, x ->
            val g = minOf(2f * u, fr.left * 0.4f)
            tubo(Offset(x, 0f), Offset(x, h * 0.9f), g, p, listOf(0.2f, 0.75f))
            if (j == 0) rueda(Offset(x, h * 0.5f), minOf(fr.left * 0.38f, 3.2f * u), p)
            else manometro(Offset(x, h * 0.5f), minOf(fr.left * 0.3f, 2.6f * u), p, e.semilla)
        }

        // El marco de teca con sus cantoneras de latón, y la chapa pintada (el nicho).
        sombra(fr, u, 0.8f * u, 0.45f)
        listOf(Rect(fr.left, fr.top, fr.right, n.top), Rect(fr.left, n.bottom, fr.right, fr.bottom)).forEachIndexed { j, r -> tabla(r, p, e.semilla + j, u) }
        listOf(Rect(fr.left, n.top, n.left, n.bottom), Rect(n.right, n.top, fr.right, n.bottom)).forEachIndexed { j, r -> veta(r, p, e.semilla + 5 + j, u, vertical = true) }
        drawRect(p.tecaVeta, fr.topLeft, fr.size, style = Stroke(0.4f * u))
        drawRect(GOMA, Offset(n.left - 0.6f * u, n.top - 0.6f * u), Size(n.width + 1.2f * u, n.height + 1.2f * u))
        drawRect(Brush.verticalGradient(listOf(p.nicho, p.nichoHondo), n.top, n.bottom), n.topLeft, n.size)
        clipRect(n.left, n.top, n.right, n.bottom) {
            // Las costuras de la chapa y sus remaches, muy tenues.
            listOf(n.left + n.width / 3, n.left + n.width * 2 / 3).forEach { x -> drawLine(p.nichoLinea, Offset(x, n.top), Offset(x, n.bottom), 0.3f * u) }
            listOf(n.top + 1.4f * u, n.bottom - 1.4f * u).forEach { y ->
                val pts = ArrayList<Offset>(); var x = n.left + 2f * u
                while (x < n.right) { pts.add(Offset(x, y)); x += 4.5f * u }
                drawPoints(pts, PointMode.Points, p.nichoLinea, 0.8f * u, StrokeCap.Round)
            }
            drawRect(Brush.verticalGradient(listOf(NEGRO.copy(alpha = if (oscuro) 0.45f else 0.1f), Color.Transparent), n.top, n.top + 2.4f * u), n.topLeft, Size(n.width, 2.4f * u))
        }
        listOf(fr.topLeft, Offset(fr.right, fr.top), Offset(fr.left, fr.bottom), fr.bottomRight).forEach { c ->
            val sx = if (c.x < w / 2) 1f else -1f; val sy = if (c.y < h / 2) 1f else -1f
            val esq = Path().apply { moveTo(c.x, c.y); lineTo(c.x + sx * 7f * u, c.y); lineTo(c.x + sx * 7f * u, c.y + sy * 1.8f * u); lineTo(c.x + sx * 1.8f * u, c.y + sy * 1.8f * u); lineTo(c.x + sx * 1.8f * u, c.y + sy * 7f * u); lineTo(c.x, c.y + sy * 7f * u); close() }
            drawPath(esq, Brush.linearGradient(listOf(p.latonLuz, p.laton, p.latonOsc), Offset(c.x, c.y), Offset(c.x + sx * 7f * u, c.y + sy * 7f * u)))
            drawPath(esq, p.latonOsc, style = Stroke(0.25f * u))
            perno(Offset(c.x + sx * 0.9f * u, c.y + sy * 0.9f * u), 0.45f * u, p)
            perno(Offset(c.x + sx * 5.4f * u, c.y + sy * 0.9f * u), 0.4f * u, p)
            perno(Offset(c.x + sx * 0.9f * u, c.y + sy * 5.4f * u), 0.4f * u, p)
        }

        // La repisa de teca, y encima la linterna amarilla, el reloj de buceo y el lápiz.
        val rep = Rect(w * 0.025f, h * 0.905f, w * 0.975f, h * 0.94f)
        drawRect(NEGRO.copy(alpha = 0.3f), Offset(rep.left, rep.bottom), Size(rep.width, 1.2f * u))
        listOf(w * 0.18f, w * 0.82f).forEach { x ->
            drawPath(Path().apply { moveTo(x - 1.2f * u, rep.bottom); lineTo(x + 1.2f * u, rep.bottom); lineTo(x + 1.2f * u, h); lineTo(x - 1.2f * u, h * 0.975f); close() }, p.aceroOsc)
        }
        tabla(rep, p, e.semilla + 9, u)
        liston(Rect(rep.left, rep.top - 0.6f * u, rep.right, rep.top + 0.3f * u), p, u)
        // La linterna.
        val lx = w * 0.66f; val ly = rep.top - 1.6f * u
        if (oscuro) halo(Offset(lx + w * 0.11f, ly), 6f * u, LUZ_CABINA.copy(alpha = 0.3f))
        drawRoundRect(NEGRO.copy(alpha = 0.3f), Offset(lx + 0.3f * u, ly - 1f * u), Size(w * 0.11f, 3f * u), CornerRadius(1f * u))
        drawRoundRect(Brush.verticalGradient(listOf(mez(AMARILLO, Color.White, 0.3f), AMARILLO, mez(AMARILLO, NEGRO, 0.35f)), ly - 1.6f * u, ly + 1.6f * u), Offset(lx, ly - 1.6f * u), Size(w * 0.11f, 3.2f * u), CornerRadius(1f * u))
        drawRect(GOMA, Offset(lx + w * 0.03f, ly - 1.6f * u), Size(w * 0.012f, 3.2f * u))
        drawRect(GOMA, Offset(lx + w * 0.085f, ly - 2f * u), Size(w * 0.03f, 4f * u))
        drawOval(if (oscuro) LUZ_CABINA else Color(0xFFF4EED8), Offset(lx + w * 0.112f, ly - 1.6f * u), Size(0.9f * u, 3.2f * u))
        // El reloj de buceo: la correa negra, el bisel con su marca roja y la esfera.
        val rc = Offset(w * 0.2f, rep.top - 2.6f * u); val rr = 2.4f * u
        drawRect(GOMA, Offset(rc.x - rr * 2.6f, rc.y + rr * 0.5f), Size(rr * 5.2f, rr * 0.55f))
        drawCircle(NEGRO.copy(alpha = 0.3f), rr * 1.15f, Offset(rc.x + 0.3f * u, rc.y + 0.5f * u))
        drawCircle(Brush.radialGradient(listOf(PLATA, Color(0xFF7A848C)), Offset(rc.x - rr * 0.3f, rc.y - rr * 0.3f), rr * 1.5f), rr * 1.12f, rc)
        drawCircle(Color(0xFF14181A), rr * 0.9f, rc)
        drawArc(ROJO, -90f, 60f, false, Offset(rc.x - rr * 0.8f, rc.y - rr * 0.8f), Size(rr * 1.6f, rr * 1.6f), style = Stroke(rr * 0.16f))
        drawLine(BIOLUZ, rc, Offset(rc.x, rc.y - rr * 0.6f), 0.3f * u, StrokeCap.Round)
        drawLine(BIOLUZ, rc, Offset(rc.x + rr * 0.45f, rc.y + rr * 0.1f), 0.3f * u, StrokeCap.Round)
        // El lápiz.
        val px = w * 0.32f; val py = rep.top - 0.7f * u
        drawLine(NEGRO.copy(alpha = 0.3f), Offset(px + 0.3f * u, py + 0.5f * u), Offset(px + w * 0.14f + 0.3f * u, py + 0.5f * u), 1.4f * u)
        drawLine(AMARILLO, Offset(px, py), Offset(px + w * 0.12f, py), 1.4f * u)
        drawLine(mez(AMARILLO, Color.White, 0.4f), Offset(px, py - 0.35f * u), Offset(px + w * 0.12f, py - 0.35f * u), 0.35f * u)
        drawPath(Path().apply { moveTo(px + w * 0.12f, py - 0.7f * u); lineTo(px + w * 0.14f, py); lineTo(px + w * 0.12f, py + 0.7f * u); close() }, Color(0xFFE6C8A0))
        drawRect(ROJO, Offset(px - 1.2f * u, py - 0.7f * u), Size(1.2f * u, 1.4f * u))
    }

    /** El color del asa de goma y del signo de cada tipo de nota. */
    private fun colorTipo(tipo: String): Color = when (tipo) {
        "idea" -> Color(0xFFE0A81E)
        "pensamiento" -> Color(0xFF1E8FA8)
        "proposito" -> Color(0xFF3E9A62)
        "recordatorio" -> Color(0xFFC7302A)
        else -> Color(0xFF6E7A80)
    }

    override fun DrawScope.notaTablero(e: EstadoNota, oscuro: Boolean) {
        val r = e.rect
        if (r.width < 1f || r.height < 1f) return
        val k = minOf(r.width, r.height) / 100f
        val f = Rect(r.left + 2.4f * k, r.top + 4f * k, r.right - 2.4f * k, r.bottom - 2.6f * k)
        val rad = CornerRadius(3.4f * k)
        val papel = if (oscuro) PIZARRA_NOCHE else PIZARRA
        val col = colorTipo(e.tipo)
        val p = pal(oscuro)
        if (e.fijada) drawRoundRect(e.acento.copy(alpha = 0.45f), Offset(f.left - 1.2f * k, f.top - 1.2f * k), Size(f.width + 2.4f * k, f.height + 2.4f * k), CornerRadius(4.6f * k), style = Stroke(1.6f * k))
        drawRoundRect(NEGRO.copy(alpha = 0.35f), Offset(f.left + 1f * k, f.top + 2f * k), f.size, rad)
        val forma = Path().apply { addRoundRect(RoundRect(f, rad)) }
        drawPath(forma, papel)
        clipPath(forma) {
            // El asa de goma del color del tipo, arriba, con su agujero.
            val ah = f.height * 0.1f
            drawRect(Brush.verticalGradient(listOf(mez(col, Color.White, 0.2f), col, mez(col, NEGRO, 0.25f)), f.top, f.top + ah), f.topLeft, Size(f.width, ah))
            drawRoundRect(mez(col, NEGRO, 0.55f), Offset(f.center.x - f.width * 0.18f, f.top + ah * 0.3f), Size(f.width * 0.36f, ah * 0.4f), CornerRadius(ah * 0.2f))
            // La regla de la orilla izquierda.
            var y = f.top + ah + 2f * k; var j = 0
            while (y < f.bottom - 2f * k && j < 60) {
                drawLine(TINTA.copy(alpha = 0.45f), Offset(f.left, y), Offset(f.left + (if (j % 5 == 0) 4f else 2.2f) * k, y), 0.4f * k); y += 2.4f * k; j++
            }
            // La textura mate de la pizarra, el brillo y la sombra del pie.
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, NEGRO.copy(alpha = 0.07f)), f.top, f.bottom), f.topLeft, f.size)
            drawPath(Path().apply { moveTo(f.left, f.top + f.height * 0.6f); lineTo(f.left + f.width * 0.5f, f.top); lineTo(f.left + f.width * 0.62f, f.top); lineTo(f.left, f.top + f.height * 0.78f); close() }, Color.White.copy(alpha = 0.18f))
        }
        drawRoundRect(mez(papel, NEGRO, 0.28f), f.topLeft, f.size, rad, style = Stroke(0.5f * k))
        // El signo del tipo, abajo a la derecha.
        val g = Offset(f.right - 7f * k, f.bottom - 6.6f * k); val gr = 3.4f * k
        when (e.tipo) {
            "idea" -> listOf(Triple(-0.4f, 0.5f, 0.45f), Triple(0.3f, 0f, 0.35f), Triple(-0.1f, -0.6f, 0.25f)).forEach { (dx, dy, br) ->
                drawCircle(col, gr * br, Offset(g.x + dx * gr, g.y + dy * gr), style = Stroke(0.6f * k))
            }
            "pensamiento" -> {
                drawArc(col, 180f, 180f, true, Offset(g.x - gr, g.y - gr * 0.9f), Size(gr * 2, gr * 1.6f))
                for (t in 0 until 3) drawPath(Path().apply { val tx = g.x - gr * 0.6f + t * gr * 0.6f; moveTo(tx, g.y - gr * 0.1f); quadraticTo(tx + gr * 0.3f, g.y + gr * 0.5f, tx, g.y + gr) }, col, style = Stroke(0.5f * k))
            }
            "proposito" -> ancla(g, gr, col, 0.7f * k)
            "recordatorio" -> {
                drawCircle(col, gr, g, style = Stroke(0.8f * k))
                drawArc(col, -90f, 60f, false, Offset(g.x - gr * 0.7f, g.y - gr * 0.7f), Size(gr * 1.4f, gr * 1.4f), style = Stroke(0.8f * k))
                drawLine(col, g, Offset(g.x, g.y - gr * 0.55f), 0.6f * k, StrokeCap.Round)
                drawLine(col, g, Offset(g.x + gr * 0.4f, g.y + gr * 0.15f), 0.6f * k, StrokeCap.Round)
            }
            else -> drawLine(col, Offset(g.x - gr, g.y), Offset(g.x + gr, g.y), 0.7f * k, StrokeCap.Round)
        }
        // Lo que la sujeta: el cordón por el agujero del asa hasta su gancho, o el mosquetón de latón si está fijada.
        val ox = f.center.x; val oy = f.top + f.height * 0.05f
        if (e.fijada) {
            val m = Rect(ox - 3.6f * k, r.top - 1f * k, ox + 3.6f * k, oy + 3f * k)
            val mos = Path().apply { addRoundRect(RoundRect(m, CornerRadius(3.6f * k))) }
            drawPath(Path().apply { addPath(mos, Offset(0.6f * k, 1f * k)) }, NEGRO.copy(alpha = 0.35f), style = Stroke(1.4f * k))
            drawPath(mos, Brush.horizontalGradient(listOf(p.latonLuz, p.laton, p.latonOsc), m.left, m.right), style = Stroke(1.4f * k))
            drawLine(p.latonOsc, Offset(m.right, m.top + m.height * 0.3f), Offset(m.right - 1.4f * k, m.top + m.height * 0.62f), 0.9f * k, StrokeCap.Round)
            drawRect(p.latonLuz, Offset(m.left - 0.9f * k, m.center.y - 1.2f * k), Size(1.8f * k, 2.4f * k))
        } else {
            drawPath(Path().apply { moveTo(ox - 2f * k, oy); quadraticTo(ox - 1f * k, r.top - 2f * k, ox, r.top + 0.5f * k); quadraticTo(ox + 1f * k, r.top - 2f * k, ox + 2f * k, oy) }, Color(0xFF2E3A40), style = Stroke(0.7f * k, cap = StrokeCap.Round))
            drawCircle(NEGRO.copy(alpha = 0.35f), 1.8f * k, Offset(ox + 0.4f * k, r.top + 1.2f * k))
            drawCircle(Brush.radialGradient(listOf(p.latonLuz, p.laton, p.latonOsc), Offset(ox - 0.6f * k, r.top), 2.4f * k), 1.7f * k, Offset(ox, r.top + 0.8f * k))
        }
    }

    override fun tintaNota(oscuro: Boolean): Color = TINTA
}
