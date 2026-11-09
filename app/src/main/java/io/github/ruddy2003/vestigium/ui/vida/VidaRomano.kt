package io.github.ruddy2003.vestigium.ui.vida

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ClipOp
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
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * Romano: la domus de mármol, pórfido y oro, con el nogal de sus muebles (la misma casa que su biblioteca, su Ideario y su
 * Hall: el armario con frontón, la pared pompeyana, las columnas acanaladas y el bronce).
 * - Social: el triclinio de frente: el entablamento con antefijas, dentículos y friso de pórfido con roleos de oro; dos
 *   columnas jónicas de mármol; la pared roja pompeyana con el gran panel pintado (crema de día, negro de noche) donde va
 *   el grafo; el lecho tapizado con sus cojines, sus respaldos de bronce y su colgadura; dos candelabros encendidos y el
 *   piso de mosaico blanco y negro.
 * - Mapas: la mesa de cidro del agrimensor vista desde arriba, con su canto moldurado y la taracea de boj; el pergamino
 *   sujeto con cuatro pesas de bronce; alrededor, el rollo, el compás, el tintero con el cálamo, la regla, la escuadra con
 *   la plomada y la lucerna encendida; abajo, el faldón con apliques de bronce y las patas de león.
 * - Otras vidas: el armario de las hornacinas: frontón con corona de laurel y acroteras, cornisa con dentículos, cinco
 *   hornacinas de medio punto con su venera, separadas por columnillas de pórfido, entre pilastras de mármol, y el podio con
 *   tableros de pórfido.
 * - Íconos: el templo hexástilo en su podio, la tablilla dorada que raspa un denario de plata y los miliarios de una vía.
 * - Notas: el album de la domus: el tablón encalado (negro de noche) en un marco de nogal con cornisa y repisa; cada nota
 *   es una tablilla de cera con su marco de haya y, según el tipo, una bulla de oro, una ramita de laurel, una tesela de
 *   pórfido o un sello de lacre; la fijada lleva el clavo de bronce y su cinta.
 */
internal object VidaRomano : DisenoVida {
    private val MARMOL = Color(0xFFEFEAE0)
    private val MARMOL_SOMBRA = Color(0xFFC9C0B0)
    private val MARMOL_VETA = Color(0xFF9C9284)
    private val PORFIDO = Color(0xFF6E2232)
    private val PORFIDO_LUZ = Color(0xFF94394A)
    private val PORFIDO_OSC = Color(0xFF431420)
    private val POMPEYANO = Color(0xFF9A3222)
    private val POMPEYANO_OSC = Color(0xFF6E2116)
    private val NEGRO = Color(0xFF1E1714)
    private val NOGAL = Color(0xFF5E3C22)
    private val NOGAL_LUZ = Color(0xFF8C6340)
    private val NOGAL_OSC = Color(0xFF331F10)
    private val CIDRO = Color(0xFFA9683A)
    private val CIDRO_LUZ = Color(0xFFC88A55)
    private val CIDRO_OSC = Color(0xFF6E3C1C)
    private val HAYA = Color(0xFFC79A62)
    private val HAYA_LUZ = Color(0xFFE0BC86)
    private val HAYA_OSC = Color(0xFF8A6232)
    private val BOJ = Color(0xFFE8CF96)
    private val ORO = Color(0xFFD4AF37)
    private val ORO_LUZ = Color(0xFFF2D77E)
    private val ORO_OSC = Color(0xFF8E6A1F)
    private val BRONCE = Color(0xFFB98B4A)
    private val BRONCE_LUZ = Color(0xFFE6C27F)
    private val BRONCE_OSC = Color(0xFF6E4E22)
    private val PLATA = Color(0xFFD5D6D2)
    private val PLATA_OSC = Color(0xFF8E908A)
    private val LINO = Color(0xFFEDE4CC)
    private val PERGAMINO = Color(0xFFF1E7D0)
    private val CERA_CLARA = Color(0xFFE8D9B0)
    private val CERA_NEGRA = Color(0xFF2B221B)
    private val LAUREL = Color(0xFF5E7A3A)
    private val LAUREL_LUZ = Color(0xFF86A055)
    private val LACRE = Color(0xFFA3172E)
    private val LLAMA = Color(0xFFFFC95E)
    private val LUZ = Color(0xFFFFD98A)
    private val BASALTO = Color(0xFF55524E)
    private val NOCHE = Color(0xFF0D0B0A)
    /** El panel pintado (y el tablón encalado) donde va el contenido: crema de día, negro pompeyano de noche. */
    private val PANEL_DIA = Color(0xFFF2ECDF)
    private val PANEL_NOCHE = Color(0xFF1D1A18)

    private fun mez(a: Color, b: Color, f: Float): Color = lerp(a, b, f.coerceIn(0f, 1f))

    /** Un número estable de 0 a 1 (las irregularidades de cada cosa, siempre las mismas). */
    private fun azar(semilla: Int, k: Int): Float {
        var x = semilla * 374761393 + k * 668265263
        x = (x xor (x ushr 13)) * 1274126177
        x = x xor (x ushr 16)
        return (x and 0x7fffffff) / 2147483647f
    }

    /** Un material a la luz del modo: de noche, el mismo material en penumbra. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mez(c, NOCHE, 0.42f) else c

    /** La unidad de dibujo: una centésima del lado corto del lienzo. */
    private fun DrawScope.un(): Float = size.minDimension / 100f

    /** El nicho de un mueble, en px. */
    private fun DrawScope.nichoPx(m: MuebleVida): Rect {
        val a = area(m)
        return Rect(size.width * a.left, size.height * a.top, size.width * a.right, size.height * a.bottom)
    }

    override fun area(m: MuebleVida): Rect = when (m) {
        MuebleVida.SOCIAL -> Rect(0.135f, 0.165f, 0.865f, 0.76f)
        MuebleVida.MAPAS -> Rect(0.105f, 0.14f, 0.895f, 0.77f)
        MuebleVida.OTRAS_VIDAS -> Rect(0.045f, 0.25f, 0.955f, 0.80f)
        MuebleVida.NOTAS -> Rect(0.07f, 0.125f, 0.93f, 0.885f)
    }

    // ================================================================ Ayudas de dibujo

    /** Una mancha radial suave (luz, humedad, pigmento). */
    private fun DrawScope.mancha(c: Offset, r: Float, color: Color) {
        if (r < 0.5f) return
        drawCircle(Brush.radialGradient(listOf(color, color.copy(alpha = 0f)), c, r), r, c)
    }

    /** Vetas de madera en un rectángulo: líneas onduladas a lo largo, con su grosor y su tono variables. */
    private fun DrawScope.vetas(r: Rect, color: Color, semilla: Int, paso: Float, vertical: Boolean = false, ondulacion: Float = 1f) {
        val largo = if (vertical) r.height else r.width
        val ancho = if (vertical) r.width else r.height
        val n = (ancho / paso).toInt().coerceIn(2, 60)
        clipRect(r.left, r.top, r.right, r.bottom) {
            for (k in 0 until n) {
                val base = ancho * (k + azar(semilla, k)) / n
                val amp = (0.2f + 0.6f * azar(semilla, k + 100)) * paso * ondulacion
                val fase = azar(semilla, k + 200) * 6.28f
                val ondas = 1f + 2f * azar(semilla, k + 300)
                val p = Path()
                for (s in 0..16) {
                    val a = largo * s / 16f
                    val v = base + amp * sin(fase + ondas * 6.28f * s / 16f)
                    val x = if (vertical) r.left + v else r.left + a
                    val y = if (vertical) r.top + a else r.top + v
                    if (s == 0) p.moveTo(x, y) else p.lineTo(x, y)
                }
                drawPath(p, color.copy(alpha = color.alpha * (0.3f + 0.7f * azar(semilla, k + 400))), style = Stroke(paso * (0.12f + 0.18f * azar(semilla, k + 500))))
            }
        }
    }

    /** Vetas de mármol: unas pocas grietas finas y quebradas que cruzan la pieza a lo largo, en diagonal suave. */
    private fun DrawScope.vetasMarmol(r: Rect, color: Color, semilla: Int, n: Int, grosor: Float) {
        val vertical = r.height > r.width
        val largo = if (vertical) r.height else r.width
        val ancho = if (vertical) r.width else r.height
        clipRect(r.left, r.top, r.right, r.bottom) {
            for (k in 0 until n) {
                val p = Path()
                var a = -largo * 0.05f + largo * 0.3f * azar(semilla, k * 7)
                var b = ancho * (0.15f + 0.7f * azar(semilla, k * 7 + 1))
                val deriva = (azar(semilla, k * 7 + 2) - 0.5f) * ancho * 0.5f
                val tramo = largo * (0.35f + 0.35f * azar(semilla, k * 7 + 3))
                fun punto(x: Float, y: Float) = if (vertical) Offset(r.left + y, r.top + x) else Offset(r.left + x, r.top + y)
                val p0 = punto(a, b); p.moveTo(p0.x, p0.y)
                val pasos = 7
                for (s in 1..pasos) {
                    val na = a + tramo / pasos
                    val nb = (b + deriva / pasos + (azar(semilla, k * 13 + s) - 0.5f) * ancho * 0.22f).coerceIn(0f, ancho)
                    val q = punto(na, nb); p.lineTo(q.x, q.y)
                    a = na; b = nb
                }
                drawPath(p, color.copy(alpha = color.alpha * (0.45f + 0.55f * azar(semilla, k + 90))), style = Stroke(grosor * (0.6f + azar(semilla, k + 60))))
            }
        }
    }

    /** Un bloque de mármol con luz arriba, sombra abajo y sus vetas. */
    private fun DrawScope.marmol(r: Rect, oscuro: Boolean, semilla: Int, vetas: Int = 2) {
        val u = un()
        drawRect(Brush.verticalGradient(listOf(t(mez(MARMOL, Color.White, 0.4f), oscuro), t(MARMOL, oscuro), t(MARMOL_SOMBRA, oscuro)), r.top, r.bottom), r.topLeft, r.size)
        if (vetas > 0) vetasMarmol(r, t(MARMOL_VETA, oscuro).copy(alpha = 0.55f), semilla, vetas, 0.22f * u)
    }

    /** Una moldura horizontal: banda con su luz arriba y su sombra abajo. */
    private fun DrawScope.moldura(r: Rect, claro: Color, medio: Color, oscuro: Color) {
        drawRect(Brush.verticalGradient(listOf(claro, medio, oscuro), r.top, r.bottom), r.topLeft, r.size)
        drawLine(Color.White.copy(alpha = 0.25f), r.topLeft, Offset(r.right, r.top), maxOf(1f, r.height * 0.08f))
    }

    /** Dentículos: la fila de dados bajo la cornisa. */
    private fun DrawScope.denticulos(r: Rect, fondo: Color, dado: Color, luz: Color) {
        drawRect(fondo, r.topLeft, r.size)
        val paso = r.height * 1.25f
        val n = (r.width / paso).toInt().coerceAtLeast(1)
        val p = r.width / n
        for (k in 0 until n) {
            val x = r.left + k * p + p * 0.18f
            drawRect(dado, Offset(x, r.top), Size(p * 0.64f, r.height * 0.82f))
            drawRect(luz, Offset(x, r.top), Size(p * 0.16f, r.height * 0.82f))
        }
    }

    /** Una acrotera en forma de palmeta: un abanico de hojas sobre una voluta. */
    private fun DrawScope.palmeta(o: Offset, r: Float, color: Color, sombra: Color) {
        for (k in -3..3) {
            val ang = Math.toRadians(-90.0 + k * 21.0)
            val p = Offset(o.x + r * cos(ang).toFloat(), o.y + r * sin(ang).toFloat())
            drawLine(sombra, Offset(o.x + r * 0.05f, o.y + r * 0.05f), Offset(p.x + r * 0.05f, p.y + r * 0.05f), r * 0.2f, StrokeCap.Round)
            drawLine(color, o, p, r * 0.18f, StrokeCap.Round)
        }
        drawArc(sombra, 180f, 180f, false, Offset(o.x - r * 0.42f, o.y - r * 0.1f), Size(r * 0.84f, r * 0.5f), style = Stroke(r * 0.14f))
        drawCircle(color, r * 0.24f, o)
        drawCircle(Color.White.copy(alpha = 0.35f), r * 0.09f, Offset(o.x - r * 0.07f, o.y - r * 0.07f))
    }

    /** Una corona de laurel: dos ramas de hojas en círculo, abiertas arriba, con la cinta abajo. */
    private fun DrawScope.laurel(c: Offset, r: Float, hoja: Color, cinta: Color) {
        for (lado in listOf(-1f, 1f)) for (k in 0 until 7) {
            val ang = if (lado > 0) 100f + 22f * k else 80f - 22f * k
            val rad = Math.toRadians(ang.toDouble())
            val p = Offset(c.x + r * cos(rad).toFloat(), c.y + r * sin(rad).toFloat())
            rotate(ang + 90f + 18f * lado, p) {
                drawOval(mez(hoja, Color.Black, 0.25f), Offset(p.x - r * 0.3f, p.y - r * 0.1f), Size(r * 0.6f, r * 0.24f))
                drawOval(hoja, Offset(p.x - r * 0.28f, p.y - r * 0.12f), Size(r * 0.54f, r * 0.2f))
            }
        }
        drawLine(cinta, Offset(c.x - r * 0.15f, c.y + r * 1.0f), Offset(c.x - r * 0.45f, c.y + r * 1.45f), r * 0.13f, StrokeCap.Round)
        drawLine(cinta, Offset(c.x + r * 0.15f, c.y + r * 1.0f), Offset(c.x + r * 0.45f, c.y + r * 1.45f), r * 0.13f, StrokeCap.Round)
        drawCircle(cinta, r * 0.13f, Offset(c.x, c.y + r * 0.98f))
    }

    /** Una ramita de laurel: el tallo y sus hojas alternas. */
    private fun DrawScope.ramita(a: Offset, b: Offset, hoja: Color, n: Int) {
        val u = un()
        drawLine(mez(hoja, Color.Black, 0.3f), a, b, 0.35f * u, StrokeCap.Round)
        val ang = Math.toDegrees(atan2((b.y - a.y).toDouble(), (b.x - a.x).toDouble())).toFloat()
        val largo = (b - a).getDistance()
        for (k in 0 until n) {
            val f = (k + 0.6f) / (n + 0.4f)
            val p = Offset(a.x + (b.x - a.x) * f, a.y + (b.y - a.y) * f)
            val lado = if (k % 2 == 0) 1f else -1f
            rotate(ang + 40f * lado, p) {
                drawOval(hoja, Offset(p.x, p.y - largo * 0.045f), Size(largo * 0.26f, largo * 0.09f))
            }
        }
    }

    /** Una llama de lucerna con su halo, que no se mete en [fuera] (el nicho). */
    private fun DrawScope.llama(o: Offset, r: Float, oscuro: Boolean, fuera: Rect?) {
        val halo = { mancha(o, r * (if (oscuro) 6.5f else 4.2f), LUZ.copy(alpha = if (oscuro) 0.34f else 0.22f)) }
        if (fuera != null) clipRect(fuera.left, fuera.top, fuera.right, fuera.bottom, ClipOp.Difference) { halo() } else halo()
        val p = Path().apply {
            moveTo(o.x, o.y - r * 2.2f)
            quadraticTo(o.x + r * 1.0f, o.y - r * 0.4f, o.x, o.y + r * 0.6f)
            quadraticTo(o.x - r * 1.0f, o.y - r * 0.4f, o.x, o.y - r * 2.2f)
            close()
        }
        drawPath(p, LLAMA)
        drawOval(Color(0xFFFFF4D6), Offset(o.x - r * 0.3f, o.y - r * 0.6f), Size(r * 0.6f, r * 1.0f))
    }

    /**
     * Una columna jónica de mármol: plinto, basa ática con sus toros, fuste acanalado con su luz, y el capitel con equino,
     * volutas y ábaco.
     */
    private fun DrawScope.columnaJonica(x0: Float, x1: Float, yCapitel: Float, yBasa: Float, ySuelo: Float, oscuro: Boolean, semilla: Int) {
        val u = un()
        val ancho = x1 - x0
        val fuste = Rect(x0 + ancho * 0.14f, yCapitel + ancho * 0.42f, x1 - ancho * 0.14f, yBasa)
        // La sombra que la columna echa en la pared, a la derecha.
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.16f), Offset(fuste.right, fuste.top), Size(ancho * 0.18f, fuste.height))
        // El fuste: un cilindro de mármol con estrías.
        drawRect(Brush.horizontalGradient(listOf(t(MARMOL_SOMBRA, oscuro), t(mez(MARMOL, Color.White, 0.5f), oscuro), t(MARMOL, oscuro), t(MARMOL_SOMBRA, oscuro), t(mez(MARMOL_SOMBRA, MARMOL_VETA, 0.6f), oscuro)), fuste.left, fuste.right), fuste.topLeft, fuste.size)
        val estrias = 6
        for (k in 1 until estrias) {
            val x = fuste.left + fuste.width * k / estrias
            drawLine(t(MARMOL_VETA, oscuro).copy(alpha = 0.55f), Offset(x, fuste.top + 1.2f * u), Offset(x, fuste.bottom - 1.2f * u), 0.3f * u, StrokeCap.Round)
            drawLine(Color.White.copy(alpha = if (oscuro) 0.12f else 0.4f), Offset(x + 0.45f * u, fuste.top + 1.2f * u), Offset(x + 0.45f * u, fuste.bottom - 1.2f * u), 0.25f * u, StrokeCap.Round)
        }
        vetasMarmol(fuste, t(MARMOL_VETA, oscuro).copy(alpha = 0.3f), semilla, 1, 0.2f * u)
        // El collarino y el capitel: equino con ovas, volutas a los lados y el ábaco arriba.
        val eq = Rect(x0 + ancho * 0.08f, yCapitel + ancho * 0.18f, x1 - ancho * 0.08f, yCapitel + ancho * 0.42f)
        drawRect(Brush.verticalGradient(listOf(t(MARMOL, oscuro), t(MARMOL_SOMBRA, oscuro)), eq.top, eq.bottom), eq.topLeft, eq.size)
        for (k in 0 until 4) {
            val cx = eq.left + eq.width * (k + 0.5f) / 4f
            drawOval(t(MARMOL_VETA, oscuro).copy(alpha = 0.7f), Offset(cx - eq.width * 0.08f, eq.top + eq.height * 0.18f), Size(eq.width * 0.16f, eq.height * 0.62f), style = Stroke(0.3f * u))
        }
        listOf(x0 + ancho * 0.1f, x1 - ancho * 0.1f).forEach { cx ->
            val c = Offset(cx, yCapitel + ancho * 0.25f)
            val r = ancho * 0.17f
            drawCircle(t(MARMOL, oscuro), r, c)
            drawCircle(t(MARMOL_SOMBRA, oscuro), r, c, style = Stroke(0.35f * u))
            drawArc(t(MARMOL_VETA, oscuro), 0f, 270f, false, Offset(c.x - r * 0.6f, c.y - r * 0.6f), Size(r * 1.2f, r * 1.2f), style = Stroke(0.3f * u))
            drawCircle(t(MARMOL_VETA, oscuro), r * 0.18f, c)
        }
        moldura(Rect(x0 - ancho * 0.02f, yCapitel, x1 + ancho * 0.02f, yCapitel + ancho * 0.12f), t(mez(MARMOL, Color.White, 0.5f), oscuro), t(MARMOL, oscuro), t(MARMOL_SOMBRA, oscuro))
        drawRect(t(MARMOL_SOMBRA, oscuro), Offset(x0 + ancho * 0.12f, yCapitel + ancho * 0.12f), Size(ancho * 0.76f, ancho * 0.06f))
        // La basa ática: dos toros y la escocia, sobre el plinto.
        val plinto = Rect(x0 - ancho * 0.04f, ySuelo - (ySuelo - yBasa) * 0.42f, x1 + ancho * 0.04f, ySuelo)
        val toroAb = Rect(x0 + ancho * 0.02f, plinto.top - (ySuelo - yBasa) * 0.26f, x1 - ancho * 0.02f, plinto.top)
        val toroAr = Rect(x0 + ancho * 0.1f, yBasa, x1 - ancho * 0.1f, yBasa + (ySuelo - yBasa) * 0.2f)
        drawRect(t(MARMOL_SOMBRA, oscuro), Offset(x0 + ancho * 0.14f, toroAr.bottom), Size(ancho * 0.72f, toroAb.top - toroAr.bottom))
        listOf(toroAb, toroAr).forEach { r ->
            drawRoundRect(Brush.verticalGradient(listOf(t(mez(MARMOL, Color.White, 0.5f), oscuro), t(MARMOL, oscuro), t(MARMOL_VETA, oscuro)), r.top, r.bottom), r.topLeft, r.size, CornerRadius(r.height / 2))
        }
        moldura(plinto, t(MARMOL, oscuro), t(MARMOL, oscuro), t(MARMOL_SOMBRA, oscuro))
        drawLine(t(MARMOL_VETA, oscuro).copy(alpha = 0.6f), Offset(plinto.left, plinto.bottom), Offset(plinto.right, plinto.bottom), 0.4f * u)
    }

    /** Un roleo de oro: una línea ondulante con espirales que se enroscan arriba y abajo, a lo largo de una banda. */
    private fun DrawScope.roleo(r: Rect, color: Color) {
        val u = un()
        val paso = r.height * 2.2f
        val n = (r.width / paso).toInt().coerceAtLeast(2)
        val p = r.width / n
        val cy = r.center.y
        val a = r.height * 0.28f
        val linea = Path().apply {
            moveTo(r.left, cy)
            for (k in 0 until n) {
                val x = r.left + k * p
                quadraticTo(x + p * 0.25f, cy + (if (k % 2 == 0) -a else a) * 1.6f, x + p * 0.5f, cy)
                quadraticTo(x + p * 0.75f, cy - (if (k % 2 == 0) -a else a) * 1.6f, x + p, cy)
            }
        }
        drawPath(linea, color, style = Stroke(0.35f * u, cap = StrokeCap.Round))
        for (k in 0 until 2 * n) {
            val x = r.left + (k + 0.5f) * p / 2f
            val arriba = k % 2 == 0
            val c = Offset(x, cy + (if (arriba) -a else a) * 1.1f)
            drawArc(color, if (arriba) 0f else 180f, 270f, false, Offset(c.x - a * 0.7f, c.y - a * 0.7f), Size(a * 1.4f, a * 1.4f), style = Stroke(0.3f * u, cap = StrokeCap.Round))
            drawCircle(color, a * 0.22f, c)
        }
    }

    /** El entablamento completo: antefijas arriba, cornisa, dentículos, friso de pórfido con roleos y arquitrabe de dos fajas. */
    private fun DrawScope.entablamento(x0: Float, x1: Float, yTop: Float, yBottom: Float, oscuro: Boolean, e: EstadoMueble, antefijas: Boolean) {
        val u = un()
        val h = yBottom - yTop
        val yCor = yTop + h * (if (antefijas) 0.22f else 0f)
        val yDen = yCor + h * 0.2f
        val yFri = yDen + h * 0.14f
        val yArq = yFri + h * 0.27f
        // Sombra sobre la pared.
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.22f), Color.Transparent), yBottom, yBottom + 3.5f * u), Offset(x0, yBottom), Size(x1 - x0, 3.5f * u))
        // Arquitrabe: dos fajas de mármol.
        marmol(Rect(x0 + 1.5f * u, yArq, x1 - 1.5f * u, yBottom), oscuro, 31, 1)
        drawLine(t(MARMOL_VETA, oscuro), Offset(x0 + 1.5f * u, (yArq + yBottom) / 2), Offset(x1 - 1.5f * u, (yArq + yBottom) / 2), 0.3f * u)
        drawLine(t(MARMOL_VETA, oscuro), Offset(x0 + 1.5f * u, yBottom), Offset(x1 - 1.5f * u, yBottom), 0.4f * u)
        // El friso de pórfido, con filetes y roleos de oro y una roseta al centro.
        val friso = Rect(x0 + 1.5f * u, yFri, x1 - 1.5f * u, yArq)
        drawRect(Brush.verticalGradient(listOf(t(PORFIDO_LUZ, oscuro), t(PORFIDO, oscuro), t(PORFIDO_OSC, oscuro)), friso.top, friso.bottom), friso.topLeft, friso.size)
        for (k in 0 until (friso.width / (3f * u)).toInt()) drawCircle(t(mez(PORFIDO_LUZ, Color.White, 0.25f), oscuro).copy(alpha = 0.35f), 0.25f * u, Offset(friso.left + friso.width * azar(e.semilla + 3, k), friso.top + friso.height * azar(e.semilla + 4, k)))
        val oro = t(ORO, oscuro)
        val cx = (x0 + x1) / 2
        val rr = friso.height * 0.42f
        clipRect(friso.left, friso.top, cx - rr * 1.9f, friso.bottom) { roleo(Rect(friso.left, friso.top + friso.height * 0.15f, friso.right, friso.bottom - friso.height * 0.15f), oro) }
        clipRect(cx + rr * 1.9f, friso.top, friso.right, friso.bottom) { roleo(Rect(friso.left, friso.top + friso.height * 0.15f, friso.right, friso.bottom - friso.height * 0.15f), oro) }
        drawLine(oro, Offset(friso.left, friso.top + 0.4f * u), Offset(friso.right, friso.top + 0.4f * u), 0.35f * u)
        drawLine(oro, Offset(friso.left, friso.bottom - 0.4f * u), Offset(friso.right, friso.bottom - 0.4f * u), 0.35f * u)
        // La roseta: pétalos de oro con el botón del acento.
        for (k in 0 until 8) {
            val a = k * Math.PI / 4
            drawCircle(oro, rr * 0.38f, Offset(cx + rr * 0.6f * cos(a).toFloat(), friso.center.y + rr * 0.6f * sin(a).toFloat()))
        }
        drawCircle(t(ORO_OSC, oscuro), rr * 0.45f, friso.center.copy(x = cx))
        drawCircle(e.acento, rr * 0.3f, friso.center.copy(x = cx))
        // Dentículos y cornisa con su corona saliente.
        denticulos(Rect(x0 + 0.8f * u, yDen, x1 - 0.8f * u, yFri), t(MARMOL_VETA, oscuro), t(MARMOL, oscuro), Color.White.copy(alpha = if (oscuro) 0.08f else 0.4f))
        val cornisa = Rect(x0, yCor, x1, yDen)
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(x0, yDen), Size(x1 - x0, 0.6f * u))
        marmol(cornisa, oscuro, 37, 1)
        drawLine(Color.White.copy(alpha = if (oscuro) 0.15f else 0.6f), Offset(x0, yCor + 0.3f * u), Offset(x1, yCor + 0.3f * u), 0.5f * u)
        drawLine(t(MARMOL_VETA, oscuro), Offset(x0, yCor + cornisa.height * 0.55f), Offset(x1, yCor + cornisa.height * 0.55f), 0.3f * u)
        // Las antefijas: palmetas de mármol sobre el borde del tejado.
        if (antefijas) {
            val n = ((x1 - x0) / (h * 0.75f)).toInt().coerceAtLeast(3)
            for (k in 0..n) {
                val x = x0 + (x1 - x0) * k / n
                val r = h * 0.2f
                val o = Offset(x, yCor)
                val p = Path().apply {
                    moveTo(o.x - r * 0.75f, o.y); quadraticTo(o.x - r * 0.8f, o.y - r * 0.6f, o.x, o.y - r * 1.05f)
                    quadraticTo(o.x + r * 0.8f, o.y - r * 0.6f, o.x + r * 0.75f, o.y); close()
                }
                drawPath(p, t(MARMOL, oscuro))
                drawPath(p, t(MARMOL_VETA, oscuro), style = Stroke(0.3f * u))
                for (j in -2..2) {
                    val a = Math.toRadians(-90.0 + j * 28.0)
                    drawLine(t(ORO_OSC, oscuro), Offset(o.x, o.y - r * 0.1f), Offset(o.x + r * 0.7f * cos(a).toFloat(), o.y - r * 0.1f + r * 0.7f * sin(a).toFloat()), 0.3f * u, StrokeCap.Round)
                }
            }
        }
    }

    /** Un candelabro de bronce: trípode de patas de león, astil acanalado con sus nudos, el platillo y la lucerna encendida. */
    private fun DrawScope.candelabro(x: Float, ySuelo: Float, yPlato: Float, oscuro: Boolean, fuera: Rect) {
        val u = un()
        val alto = ySuelo - yPlato
        val b = t(BRONCE, oscuro); val bl = t(BRONCE_LUZ, oscuro); val bo = t(BRONCE_OSC, oscuro)
        drawOval(Color.Black.copy(alpha = 0.25f), Offset(x - alto * 0.16f, ySuelo - alto * 0.02f), Size(alto * 0.32f, alto * 0.06f))
        // Las tres patas.
        listOf(-1f, 0f, 1f).forEach { s ->
            val pie = Offset(x + s * alto * 0.12f, ySuelo)
            drawLine(bo, Offset(x, ySuelo - alto * 0.16f), pie, 1.1f * u, StrokeCap.Round)
            drawCircle(b, 0.8f * u, pie)
        }
        drawCircle(bl, 1.1f * u, Offset(x, ySuelo - alto * 0.17f))
        // El astil.
        drawRect(Brush.horizontalGradient(listOf(bo, bl, b, bo), x - 0.6f * u, x + 0.6f * u), Offset(x - 0.6f * u, yPlato), Size(1.2f * u, alto * 0.83f))
        listOf(0.35f, 0.62f).forEach { f -> drawOval(Brush.horizontalGradient(listOf(bo, bl, bo), x - 1.3f * u, x + 1.3f * u), Offset(x - 1.3f * u, yPlato + alto * f), Size(2.6f * u, 1.3f * u)) }
        // El platillo y la lucerna.
        drawOval(Brush.horizontalGradient(listOf(bo, bl, b, bo), x - 3.4f * u, x + 3.4f * u), Offset(x - 3.4f * u, yPlato - 0.6f * u), Size(6.8f * u, 1.6f * u))
        val luc = Path().apply {
            moveTo(x - 2.4f * u, yPlato - 0.4f * u); quadraticTo(x - 2.2f * u, yPlato - 2.4f * u, x, yPlato - 2.3f * u)
            quadraticTo(x + 2.4f * u, yPlato - 2.2f * u, x + 3.4f * u, yPlato - 1.6f * u); lineTo(x + 2.2f * u, yPlato - 0.4f * u); close()
        }
        drawPath(luc, Brush.verticalGradient(listOf(bl, b), yPlato - 2.4f * u, yPlato))
        llama(Offset(x + 3.1f * u, yPlato - 2.2f * u), 1.0f * u, oscuro, fuera)
    }

    // ================================================================ Social: el triclinio

    override fun DrawScope.muebleSocial(e: EstadoMueble, oscuro: Boolean) {
        if (size.minDimension < 1f) return
        val w = size.width; val h = size.height; val u = un()
        val n = nichoPx(MuebleVida.SOCIAL)
        val suelo = h * 0.87f
        val colAncho = w * 0.078f
        // La pared roja pompeyana, con sus manchas de fresco.
        val pared = Rect(w * 0.03f, h * 0.14f, w * 0.97f, suelo)
        drawRect(Brush.verticalGradient(listOf(t(POMPEYANO, oscuro), t(mez(POMPEYANO, POMPEYANO_OSC, 0.5f), oscuro)), pared.top, pared.bottom), pared.topLeft, pared.size)
        for (k in 0 until 26) mancha(Offset(pared.left + pared.width * azar(e.semilla, k), pared.top + pared.height * azar(e.semilla + 1, k)), (4f + 8f * azar(e.semilla + 2, k)) * u, (if (k % 3 == 0) Color.White else Color.Black).copy(alpha = 0.05f))
        // El zócalo negro con su mármol fingido y el filete de oro.
        val zoc = Rect(pared.left, n.bottom + 2.4f * u, pared.right, suelo)
        drawRect(t(NEGRO, oscuro), zoc.topLeft, zoc.size)
        for (k in 0 until 8) {
            val x = zoc.left + zoc.width * azar(e.semilla + 7, k)
            drawLine(Color.White.copy(alpha = 0.07f), Offset(x, zoc.top + zoc.height * 0.2f), Offset(x + 6 * u, zoc.bottom - zoc.height * 0.2f), 0.4f * u)
        }
        drawLine(t(ORO, oscuro), Offset(zoc.left, zoc.top + 0.4f * u), Offset(zoc.right, zoc.top + 0.4f * u), 0.4f * u)
        // Los candelabros pintados en las franjas, entre la columna y el panel.
        listOf((w * 0.03f + colAncho + n.left) / 2, (w * 0.97f - colAncho + n.right) / 2).forEach { x ->
            val oro = t(ORO, oscuro).copy(alpha = 0.75f)
            drawLine(oro, Offset(x, n.top + 4 * u), Offset(x, n.bottom - 2 * u), 0.3f * u)
            var y = n.top + 8 * u
            while (y < n.bottom - 6 * u) { drawOval(oro, Offset(x - 0.9f * u, y), Size(1.8f * u, 0.9f * u)); y += 11 * u }
            drawCircle(oro, 0.9f * u, Offset(x, n.top + 3 * u))
        }
        // El gran panel pintado (el nicho): filete negro, filete del acento y el fondo liso con su pátina de cal.
        drawRect(t(NEGRO, oscuro), Offset(n.left - 1.6f * u, n.top - 1.6f * u), Size(n.width + 3.2f * u, n.height + 3.2f * u))
        drawRect(if (oscuro) e.acento.copy(alpha = 0.8f) else t(ORO, oscuro), Offset(n.left - 0.9f * u, n.top - 0.9f * u), Size(n.width + 1.8f * u, n.height + 1.8f * u))
        val panel = if (oscuro) PANEL_NOCHE else PANEL_DIA
        drawRect(panel, n.topLeft, n.size)
        clipRect(n.left, n.top, n.right, n.bottom) {
            for (k in 0 until 18) mancha(Offset(n.left + n.width * azar(e.semilla + 11, k), n.top + n.height * azar(e.semilla + 12, k)), (6f + 10f * azar(e.semilla + 13, k)) * u, (if (oscuro) Color.White else Color(0xFF8A7A5A)).copy(alpha = 0.025f))
            drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.25f else 0.07f), Color.Transparent), n.top, n.top + 4 * u), n.topLeft, Size(n.width, 4 * u))
        }
        // Las columnas jónicas a los lados.
        columnaJonica(w * 0.03f, w * 0.03f + colAncho, h * 0.15f, h * 0.82f, h * 0.92f, oscuro, e.semilla)
        columnaJonica(w * 0.97f - colAncho, w * 0.97f, h * 0.15f, h * 0.82f, h * 0.92f, oscuro, e.semilla + 1)
        // El entablamento.
        entablamento(w * 0.005f, w * 0.995f, 0f, h * 0.15f, oscuro, e, true)
        // El piso de mosaico: teselas blancas con una greca de olas negra.
        val piso = Rect(0f, suelo, w, h)
        drawRect(t(mez(MARMOL, MARMOL_SOMBRA, 0.3f), oscuro), piso.topLeft, piso.size)
        val banda = Rect(0f, suelo + piso.height * 0.3f, w, suelo + piso.height * 0.78f)
        drawRect(t(NEGRO, oscuro), Offset(0f, banda.top - 0.5f * u), Size(w, 0.6f * u))
        drawRect(t(NEGRO, oscuro), Offset(0f, banda.bottom), Size(w, 0.6f * u))
        val ola = banda.height * 1.6f
        var ox = 0f
        while (ox < w) {
            val o = Path().apply {
                moveTo(ox, banda.bottom)
                quadraticTo(ox + ola * 0.1f, banda.top, ox + ola * 0.5f, banda.top + banda.height * 0.1f)
                quadraticTo(ox + ola * 0.75f, banda.top + banda.height * 0.25f, ox + ola * 0.55f, banda.center.y)
                quadraticTo(ox + ola * 0.42f, banda.center.y + banda.height * 0.12f, ox + ola * 0.38f, banda.center.y)
                quadraticTo(ox + ola * 0.5f, banda.bottom, ox + ola, banda.bottom); close()
            }
            drawPath(o, t(NEGRO, oscuro).copy(alpha = 0.85f))
            ox += ola
        }
        for (k in 0 until (w / (1.6f * u)).toInt()) drawLine(Color.Black.copy(alpha = 0.06f), Offset(k * 1.6f * u, suelo), Offset(k * 1.6f * u, h), 0.15f * u)
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.35f else 0.18f), Color.Transparent), suelo, suelo + 2.5f * u), Offset(0f, suelo), Size(w, 2.5f * u))
        // Los candelabros encendidos.
        candelabro(w * 0.145f, h * 0.985f, h * 0.835f, oscuro, n)
        candelabro(w * 0.855f, h * 0.985f, h * 0.835f, oscuro, n)
        // El lecho.
        lecho(Rect(w * 0.2f, n.bottom + 3.4f * u, w * 0.8f, h * 0.985f), e, oscuro)
    }

    /**
     * El lecho del triclinio, de frente: las patas torneadas de bronce, el bastidor de nogal con su taracea, el colchón con la
     * colgadura de pórfido (franjas de oro y flecos), tres cojines de lino con las rayas del secundario y los dos respaldos
     * (fulcra) de bronce con su medallón.
     */
    private fun DrawScope.lecho(r: Rect, e: EstadoMueble, oscuro: Boolean) {
        val u = un()
        val h = r.height
        val yColchon = r.top + h * 0.25f
        val yColg = r.top + h * 0.31f
        val yBastidor = r.top + h * 0.62f
        val yPata = r.top + h * 0.74f
        // La sombra en el piso.
        drawOval(Color.Black.copy(alpha = if (oscuro) 0.35f else 0.2f), Offset(r.left - 2 * u, r.bottom - h * 0.07f), Size(r.width + 4 * u, h * 0.12f))
        // Las patas torneadas: bolas y carretes de bronce.
        val b = t(BRONCE, oscuro); val bl = t(BRONCE_LUZ, oscuro); val bo = t(BRONCE_OSC, oscuro)
        listOf(r.left + r.width * 0.04f, r.left + r.width * 0.5f, r.right - r.width * 0.04f).forEach { x ->
            val alto = r.bottom - yPata
            val g = Brush.horizontalGradient(listOf(bo, bl, b, bo), x - 2.4f * u, x + 2.4f * u)
            drawRect(g, Offset(x - 0.9f * u, yPata), Size(1.8f * u, alto))
            drawRect(g, Offset(x - 2.4f * u, yPata), Size(4.8f * u, alto * 0.1f))
            drawOval(g, Offset(x - 2.2f * u, yPata + alto * 0.12f), Size(4.4f * u, alto * 0.3f))
            drawOval(g, Offset(x - 1.4f * u, yPata + alto * 0.48f), Size(2.8f * u, alto * 0.16f))
            drawOval(g, Offset(x - 1.8f * u, yPata + alto * 0.66f), Size(3.6f * u, alto * 0.2f))
            drawOval(g, Offset(x - 1.5f * u, r.bottom - alto * 0.12f), Size(3f * u, alto * 0.12f))
        }
        // El bastidor de nogal, con su veta y la taracea de oro.
        val bast = Rect(r.left - r.width * 0.01f, yBastidor, r.right + r.width * 0.01f, yPata)
        drawRect(Brush.verticalGradient(listOf(t(NOGAL_LUZ, oscuro), t(NOGAL, oscuro), t(NOGAL_OSC, oscuro)), bast.top, bast.bottom), bast.topLeft, bast.size)
        vetas(bast, t(NOGAL_OSC, oscuro).copy(alpha = 0.5f), e.semilla + 21, 0.9f * u)
        drawLine(t(ORO, oscuro), Offset(bast.left + 1 * u, bast.top + bast.height * 0.62f), Offset(bast.right - 1 * u, bast.top + bast.height * 0.62f), 0.35f * u)
        var x = bast.left + 4 * u
        while (x < bast.right - 3 * u) { drawCircle(t(ORO_LUZ, oscuro), 0.5f * u, Offset(x, bast.top + bast.height * 0.62f)); x += 5 * u }
        drawLine(Color.White.copy(alpha = 0.2f), Offset(bast.left, bast.top), Offset(bast.right, bast.top), 0.4f * u)
        // Los fulcra: respaldos curvos de bronce en las dos puntas, con su medallón.
        listOf(-1f, 1f).forEach { s ->
            val x0 = if (s < 0) r.left + r.width * 0.012f else r.right - r.width * 0.012f
            val p = Path().apply {
                moveTo(x0, yBastidor)
                cubicTo(x0 - s * 2.5f * u, r.top + h * 0.4f, x0 - s * 1.5f * u, r.top + h * 0.02f, x0 + s * 4.5f * u, r.top + h * 0.01f)
                cubicTo(x0 + s * 7.5f * u, r.top, x0 + s * 7.5f * u, r.top + h * 0.13f, x0 + s * 5f * u, r.top + h * 0.12f)
                cubicTo(x0 + s * 3f * u, r.top + h * 0.12f, x0 + s * 2.8f * u, r.top + h * 0.3f, x0 + s * 8.5f * u, yColchon)
                lineTo(x0 + s * 8.5f * u, yBastidor); close()
            }
            drawPath(p, Brush.horizontalGradient(listOf(bo, b, bl, b), x0 - 7 * u, x0 + 9 * u))
            drawPath(p, bo, style = Stroke(0.3f * u))
            drawCircle(bo, 1.2f * u, Offset(x0 + s * 4.6f * u, r.top + h * 0.065f))
            val med = Offset(x0 + s * 1.4f * u, r.top + h * 0.45f)
            drawCircle(bo, 2.4f * u, med)
            drawCircle(Brush.radialGradient(listOf(bl, b), Offset(med.x - 0.6f * u, med.y - 0.6f * u), 2.4f * u), 2.0f * u, med)
            drawCircle(e.secundario.copy(alpha = 0.85f), 0.8f * u, med)
        }
        // El colchón de lino, con la colgadura de pórfido encima: pliegues, franjas de oro y flecos.
        val colchon = Rect(r.left + r.width * 0.025f, yColchon, r.right - r.width * 0.025f, yBastidor + h * 0.04f)
        drawRoundRect(Brush.verticalGradient(listOf(t(mez(LINO, Color.White, 0.4f), oscuro), t(LINO, oscuro)), yColchon, yColg + h * 0.03f), Offset(colchon.left, yColchon), Size(colchon.width, yColg - yColchon + h * 0.06f), CornerRadius(h * 0.06f))
        val pl = 16
        val colg = Path().apply {
            moveTo(colchon.left, yColg)
            lineTo(colchon.right, yColg)
            lineTo(colchon.right, colchon.bottom)
            for (k in pl downTo 1) {
                val xx = colchon.left + colchon.width * k / pl
                quadraticTo(xx - colchon.width / pl * 0.5f, colchon.bottom + h * 0.07f, xx - colchon.width / pl, colchon.bottom)
            }
            close()
        }
        drawPath(colg, t(PORFIDO, oscuro))
        clipPath(colg) {
            for (k in 0 until pl) {
                val xx = colchon.left + colchon.width * k / pl
                drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.25f), Color.White.copy(alpha = 0.12f), Color.Black.copy(alpha = 0.04f)), xx, xx + colchon.width / pl), Offset(xx, yColg), Size(colchon.width / pl, colchon.bottom - yColg + h * 0.08f))
            }
            listOf(0.22f, 0.8f).forEach { f -> drawRect(t(ORO, oscuro), Offset(colchon.left, yColg + (colchon.bottom - yColg) * f), Size(colchon.width, 0.6f * u)) }
            drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.3f), Color.Transparent), yColg, yColg + 1.5f * u), Offset(colchon.left, yColg), Size(colchon.width, 1.5f * u))
        }
        for (k in 0 until pl) {
            val xx = colchon.left + colchon.width * (k + 0.5f) / pl
            for (j in -1..1) drawLine(t(ORO, oscuro), Offset(xx + j * 0.9f * u, colchon.bottom + h * 0.045f - abs(j) * h * 0.012f), Offset(xx + j * 0.9f * u, colchon.bottom + h * 0.09f - abs(j) * h * 0.012f), 0.3f * u)
        }
        // Los tres cojines rellenos, con sus dos rayas del secundario y la borla.
        for (k in 0 until 3) {
            val cx = colchon.left + colchon.width * (0.19f + 0.31f * k)
            val cw = colchon.width * 0.18f
            val c = Rect(cx - cw / 2, r.top + h * 0.04f, cx + cw / 2, yColchon + h * 0.05f)
            drawRoundRect(Color.Black.copy(alpha = 0.22f), Offset(c.left + 0.8f * u, c.top + 1.2f * u), c.size, CornerRadius(c.height * 0.5f))
            drawRoundRect(Brush.verticalGradient(listOf(t(mez(LINO, Color.White, 0.6f), oscuro), t(LINO, oscuro), t(mez(LINO, NOGAL_LUZ, 0.4f), oscuro)), c.top, c.bottom), c.topLeft, c.size, CornerRadius(c.height * 0.5f))
            listOf(0.36f, 0.6f).forEach { f -> drawLine(e.secundario.copy(alpha = if (oscuro) 0.65f else 0.8f), Offset(c.left + c.height * 0.25f, c.top + c.height * f), Offset(c.right - c.height * 0.25f, c.top + c.height * f), 0.55f * u, StrokeCap.Round) }
            drawOval(Color.White.copy(alpha = if (oscuro) 0.08f else 0.4f), Offset(c.left + c.width * 0.2f, c.top + c.height * 0.1f), Size(c.width * 0.45f, c.height * 0.2f))
            drawCircle(t(ORO, oscuro), 0.8f * u, Offset(c.center.x, c.bottom - 0.4f * u))
            drawLine(t(ORO, oscuro), Offset(c.center.x, c.bottom), Offset(c.center.x, c.bottom + 2.2f * u), 0.6f * u)
        }
    }

    // ================================================================ Mapas: la mesa del agrimensor

    override fun DrawScope.muebleMapas(e: EstadoMueble, oscuro: Boolean) {
        if (size.minDimension < 1f) return
        val w = size.width; val h = size.height; val u = un()
        val n = nichoPx(MuebleVida.MAPAS)
        val tab = Rect(w * 0.012f, h * 0.02f, w * 0.988f, h * 0.82f)
        val frente = Rect(w * 0.025f, tab.bottom, w * 0.975f, h * 0.9f)
        // Las patas de león (trapezóforos) de bronce, con su sombra en el piso.
        listOf(w * 0.075f, w * 0.5f, w * 0.925f).forEachIndexed { i, x -> pataLeon(x, frente.bottom, h * 0.995f, oscuro, i == 1) }
        // El faldón: el frente del tablero con su moldura, la veta y los apliques de bronce.
        drawRect(Brush.verticalGradient(listOf(t(CIDRO, oscuro), t(CIDRO_OSC, oscuro)), frente.top, frente.bottom), frente.topLeft, frente.size)
        vetas(frente, t(CIDRO_OSC, oscuro).copy(alpha = 0.6f), e.semilla + 5, 1.1f * u)
        drawRect(t(NOGAL_OSC, oscuro), Offset(frente.left, frente.bottom - 1.2f * u), Size(frente.width, 1.2f * u))
        // La moldura bajo el canto: un cimacio con su luz y su sombra.
        moldura(Rect(frente.left - 0.6f * u, frente.top, frente.right + 0.6f * u, frente.top + frente.height * 0.22f), t(CIDRO_LUZ, oscuro), t(CIDRO, oscuro), t(CIDRO_OSC, oscuro))
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(frente.left, frente.top + frente.height * 0.22f), Size(frente.width, 0.5f * u))
        drawLine(t(BOJ, oscuro).copy(alpha = 0.8f), Offset(frente.left + 1 * u, frente.top + frente.height * 0.34f), Offset(frente.right - 1 * u, frente.top + frente.height * 0.34f), 0.35f * u)
        drawLine(t(BOJ, oscuro).copy(alpha = 0.8f), Offset(frente.left + 1 * u, frente.bottom - frente.height * 0.2f), Offset(frente.right - 1 * u, frente.bottom - frente.height * 0.2f), 0.35f * u)
        listOf(0.25f, 0.75f).forEach { f -> roseta(Offset(frente.left + frente.width * f, frente.top + frente.height * 0.62f), frente.height * 0.24f, oscuro, e) }
        // El aplique central: una cabeza de león de bronce con su argolla.
        val ca = Offset(w * 0.5f, frente.top + frente.height * 0.55f)
        val rc = frente.height * 0.34f
        drawCircle(t(BRONCE_OSC, oscuro), rc * 1.05f, ca)
        drawCircle(Brush.radialGradient(listOf(t(BRONCE_LUZ, oscuro), t(BRONCE, oscuro)), Offset(ca.x - rc * 0.3f, ca.y - rc * 0.3f), rc), rc * 0.92f, ca)
        for (k in 0 until 12) { val a = k * Math.PI / 6; drawLine(t(BRONCE_OSC, oscuro), Offset(ca.x + rc * 0.55f * cos(a).toFloat(), ca.y + rc * 0.55f * sin(a).toFloat()), Offset(ca.x + rc * 0.88f * cos(a).toFloat(), ca.y + rc * 0.88f * sin(a).toFloat()), 0.3f * u) }
        drawCircle(t(BRONCE_OSC, oscuro), rc * 0.3f, ca)
        drawArc(t(BRONCE_LUZ, oscuro), 20f, 140f, false, Offset(ca.x - rc * 0.55f, ca.y - rc * 0.1f), Size(rc * 1.1f, rc * 1.2f), style = Stroke(0.5f * u))
        // El tablero de cidro visto desde arriba: canto moldurado, taracea de boj y la veta encendida de la madera.
        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.2f), Offset(tab.left + 0.8f * u, tab.top + 1.4f * u), tab.size, CornerRadius(2 * u))
        drawRoundRect(Brush.verticalGradient(listOf(t(CIDRO_LUZ, oscuro), t(CIDRO, oscuro), t(CIDRO_OSC, oscuro)), tab.top, tab.bottom), tab.topLeft, tab.size, CornerRadius(2 * u))
        val campo = Rect(tab.left + 3.2f * u, tab.top + 3.2f * u, tab.right - 3.2f * u, tab.bottom - 3.2f * u)
        drawRect(t(CIDRO, oscuro), campo.topLeft, campo.size)
        vetas(campo, t(CIDRO_OSC, oscuro).copy(alpha = 0.55f), e.semilla, 1.3f * u, ondulacion = 2.2f)
        vetas(campo, t(CIDRO_LUZ, oscuro).copy(alpha = 0.35f), e.semilla + 50, 2.4f * u, ondulacion = 2.6f)
        // Los «ojos» del cidro: nudos en remolino.
        listOf(Offset(0.2f, 0.3f), Offset(0.78f, 0.62f), Offset(0.55f, 0.2f)).forEachIndexed { i, f ->
            val c = Offset(campo.left + campo.width * f.x, campo.top + campo.height * f.y)
            for (j in 0 until 3) drawOval(t(CIDRO_OSC, oscuro).copy(alpha = 0.45f - j * 0.1f), Offset(c.x - (1.6f + j * 1.4f) * u, c.y - (0.8f + j * 0.8f) * u), Size((3.2f + j * 2.8f) * u, (1.6f + j * 1.6f) * u), style = Stroke(0.3f * u))
        }
        // El canto: luz arriba e izquierda, sombra abajo y derecha, y el filete de boj con puntos de ébano.
        drawRoundRect(Color.White.copy(alpha = if (oscuro) 0.08f else 0.22f), tab.topLeft, tab.size, CornerRadius(2 * u), style = Stroke(0.6f * u))
        drawRect(Color.Black.copy(alpha = 0.22f), Offset(campo.left, campo.top), Size(campo.width, 0.6f * u))
        val taracea = Rect(tab.left + 1.4f * u, tab.top + 1.4f * u, tab.right - 1.4f * u, tab.bottom - 1.4f * u)
        drawRect(t(BOJ, oscuro).copy(alpha = 0.9f), taracea.topLeft, taracea.size, style = Stroke(0.8f * u))
        var k = 0
        var tx = taracea.left + 3 * u
        while (tx < taracea.right - 2 * u) {
            drawCircle(t(NOGAL_OSC, oscuro), 0.32f * u, Offset(tx, taracea.top)); drawCircle(t(NOGAL_OSC, oscuro), 0.32f * u, Offset(tx, taracea.bottom)); tx += 3.5f * u; k++
        }
        var ty = taracea.top + 3 * u
        while (ty < taracea.bottom - 2 * u) {
            drawCircle(t(NOGAL_OSC, oscuro), 0.32f * u, Offset(taracea.left, ty)); drawCircle(t(NOGAL_OSC, oscuro), 0.32f * u, Offset(taracea.right, ty)); ty += 3.5f * u
        }
        // El pergamino (el nicho), algo más grande que él, con su sombra y el borde irregular.
        val m = 2.2f * u
        val perg = Path().apply {
            moveTo(n.left - m, n.top - m)
            val pasos = 18
            for (s in 1..pasos) lineTo(n.left - m + (n.width + 2 * m) * s / pasos, n.top - m - (azar(e.semilla + 70, s) - 0.3f) * 0.8f * u)
            for (s in 1..pasos / 2) lineTo(n.right + m + (azar(e.semilla + 71, s) - 0.3f) * 0.8f * u, n.top - m + (n.height + 2 * m) * s / (pasos / 2))
            for (s in 1..pasos) lineTo(n.right + m - (n.width + 2 * m) * s / pasos, n.bottom + m + (azar(e.semilla + 72, s) - 0.3f) * 0.8f * u)
            for (s in 1..pasos / 2) lineTo(n.left - m - (azar(e.semilla + 73, s) - 0.3f) * 0.8f * u, n.bottom + m - (n.height + 2 * m) * s / (pasos / 2))
            close()
        }
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.45f else 0.22f), Offset(n.left - m + 1.2f * u, n.top - m + 1.6f * u), Size(n.width + 2 * m, n.height + 2 * m))
        val pergCol = if (oscuro) Color(0xFF2A2520) else PERGAMINO
        drawPath(perg, mez(pergCol, if (oscuro) Color(0xFF4A3E30) else Color(0xFFD9C79E), 0.55f))
        drawRect(pergCol, n.topLeft, n.size)
        clipRect(n.left, n.top, n.right, n.bottom) {
            for (j in 0 until 14) mancha(Offset(n.left + n.width * azar(e.semilla + 80, j), n.top + n.height * azar(e.semilla + 81, j)), (5f + 9f * azar(e.semilla + 82, j)) * u, (if (oscuro) Color.White else Color(0xFFB59B62)).copy(alpha = 0.03f))
        }
        // Las cuatro pesas de bronce en las esquinas.
        listOf(Offset(n.left - m * 0.6f, n.top - m * 0.6f), Offset(n.right + m * 0.6f, n.top - m * 0.6f), Offset(n.left - m * 0.6f, n.bottom + m * 0.6f), Offset(n.right + m * 0.6f, n.bottom + m * 0.6f)).forEach { o -> pesa(o, 2.6f * u, oscuro, e) }
        // Los instrumentos alrededor.
        val arriba = (tab.top + 3.2f * u + n.top - m) / 2
        rollo(Rect(w * 0.12f, arriba - 3.0f * u, w * 0.4f, arriba + 3.0f * u), oscuro, e)
        compas(Offset(w * 0.5f, arriba - 0.6f * u), Offset(w * 0.67f, arriba + 1.6f * u), oscuro)
        tintero(Offset(w * 0.82f, arriba), 3.4f * u, oscuro)
        calamo(Offset(w * 0.86f, arriba + 2.4f * u), Offset(w * 0.75f, arriba + 3.0f * u), oscuro)
        val izq = (campo.left + n.left - m) / 2
        regla(Rect(izq - 1.6f * u, n.top + n.height * 0.08f, izq + 1.6f * u, n.top + n.height * 0.62f), oscuro)
        lucerna(Offset(izq, n.bottom - n.height * 0.12f), 3.0f * u, oscuro, n)
        val der = (campo.right + n.right + m) / 2
        escuadra(Offset(der - 2.4f * u, n.top + n.height * 0.12f), 9f * u, oscuro)
        plomada(Offset(der + 0.6f * u, n.top + n.height * 0.48f), Offset(der, n.top + n.height * 0.88f), oscuro)
    }

    /**
     * Una pata de león de bronce, de frente: la cabeza de león con su melena arriba, la pierna que se curva y se estrecha, y
     * la garra con sus dedos sobre una basa.
     */
    private fun DrawScope.pataLeon(x: Float, y0: Float, y1: Float, oscuro: Boolean, chica: Boolean) {
        val u = un()
        val alto = y1 - y0
        val a = if (chica) 2.4f * u else 3.2f * u
        val b = t(BRONCE, oscuro); val bl = t(BRONCE_LUZ, oscuro); val bo = t(BRONCE_OSC, oscuro)
        val brillo = Brush.horizontalGradient(listOf(bo, bl, b, bo), x - a * 1.5f, x + a * 1.5f)
        drawOval(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.22f), Offset(x - a * 2f, y1 - alto * 0.1f), Size(a * 4f, alto * 0.18f))
        // La pierna.
        val p = Path().apply {
            moveTo(x - a * 1.1f, y0 + alto * 0.25f); lineTo(x + a * 1.1f, y0 + alto * 0.25f)
            cubicTo(x + a * 0.4f, y0 + alto * 0.45f, x + a * 0.35f, y0 + alto * 0.6f, x + a * 0.6f, y1 - alto * 0.3f)
            lineTo(x - a * 0.6f, y1 - alto * 0.3f)
            cubicTo(x - a * 0.35f, y0 + alto * 0.6f, x - a * 0.4f, y0 + alto * 0.45f, x - a * 1.1f, y0 + alto * 0.25f); close()
        }
        drawPath(p, brillo)
        drawLine(bo.copy(alpha = 0.6f), Offset(x, y0 + alto * 0.3f), Offset(x, y1 - alto * 0.32f), 0.25f * u)
        // La garra: tres dedos redondos sobre la basa.
        val basa = Rect(x - a * 1.4f, y1 - alto * 0.1f, x + a * 1.4f, y1)
        drawRect(Brush.verticalGradient(listOf(bl, bo), basa.top, basa.bottom), basa.topLeft, basa.size)
        for (k in -1..1) {
            val c = Offset(x + k * a * 0.62f, y1 - alto * 0.19f)
            drawCircle(brillo, a * 0.42f, c)
            drawCircle(bo, a * 0.42f, c, style = Stroke(0.2f * u))
        }
        // La cabeza de león: la melena en roseta, la cara y los ojos.
        val cab = Offset(x, y0 + alto * 0.17f)
        val rc = a * 1.05f
        for (k in 0 until 10) {
            val ang = k * Math.PI / 5
            drawCircle(bo, rc * 0.42f, Offset(cab.x + rc * 0.72f * cos(ang).toFloat(), cab.y + rc * 0.72f * sin(ang).toFloat()))
        }
        drawCircle(Brush.radialGradient(listOf(bl, b, bo), Offset(cab.x - rc * 0.3f, cab.y - rc * 0.3f), rc * 1.2f), rc * 0.78f, cab)
        drawCircle(bo, rc * 0.12f, Offset(cab.x - rc * 0.3f, cab.y - rc * 0.12f))
        drawCircle(bo, rc * 0.12f, Offset(cab.x + rc * 0.3f, cab.y - rc * 0.12f))
        drawOval(bo, Offset(cab.x - rc * 0.2f, cab.y + rc * 0.18f), Size(rc * 0.4f, rc * 0.24f))
    }

    /** Una roseta de bronce de cuatro pétalos. */
    private fun DrawScope.roseta(c: Offset, r: Float, oscuro: Boolean, e: EstadoMueble) {
        for (k in 0 until 4) {
            val a = k * Math.PI / 2 + Math.PI / 4
            drawCircle(t(BRONCE, oscuro), r * 0.45f, Offset(c.x + r * 0.45f * cos(a).toFloat(), c.y + r * 0.45f * sin(a).toFloat()))
        }
        drawCircle(t(BRONCE_LUZ, oscuro), r * 0.35f, c)
        drawCircle(e.acento.copy(alpha = 0.9f), r * 0.16f, c)
    }

    /** Una pesa de bronce redonda, con su asa y su luz. */
    private fun DrawScope.pesa(c: Offset, r: Float, oscuro: Boolean, e: EstadoMueble) {
        val u = un()
        drawCircle(Color.Black.copy(alpha = 0.3f), r, Offset(c.x + 0.6f * u, c.y + 0.9f * u))
        drawCircle(Brush.radialGradient(listOf(t(BRONCE_LUZ, oscuro), t(BRONCE, oscuro), t(BRONCE_OSC, oscuro)), Offset(c.x - r * 0.35f, c.y - r * 0.35f), r * 1.3f), r, c)
        drawCircle(t(BRONCE_OSC, oscuro), r * 0.62f, c, style = Stroke(0.3f * u))
        drawCircle(e.secundario.copy(alpha = 0.55f), r * 0.28f, c)
    }

    /** Un rollo de papiro cerrado, acostado, con sus perillas de marfil y el título colgando. */
    private fun DrawScope.rollo(r: Rect, oscuro: Boolean, e: EstadoMueble) {
        val u = un()
        drawRoundRect(Color.Black.copy(alpha = 0.25f), Offset(r.left + 0.6f * u, r.top + 1 * u), r.size, CornerRadius(r.height / 2))
        drawRoundRect(Brush.verticalGradient(listOf(t(mez(PERGAMINO, Color.White, 0.4f), oscuro), t(PERGAMINO, oscuro), t(Color(0xFFB59B62), oscuro)), r.top, r.bottom), r.topLeft, r.size, CornerRadius(r.height / 2))
        for (k in 1..3) drawLine(t(Color(0xFFB59B62), oscuro).copy(alpha = 0.6f), Offset(r.left + r.width * k / 4f, r.top + 0.5f * u), Offset(r.left + r.width * k / 4f + 0.6f * u, r.bottom - 0.5f * u), 0.25f * u)
        drawRect(e.acento.copy(alpha = 0.85f), Offset(r.left + r.width * 0.42f, r.top), Size(1.2f * u, r.height))
        listOf(r.left, r.right).forEach { x ->
            drawCircle(t(Color(0xFFEDE3CC), oscuro), r.height * 0.42f, Offset(x, r.center.y))
            drawCircle(t(Color(0xFFB8A784), oscuro), r.height * 0.42f, Offset(x, r.center.y), style = Stroke(0.3f * u))
        }
        // El título (sittybos) colgando.
        val tag = Rect(r.left + r.width * 0.6f, r.bottom - 0.4f * u, r.left + r.width * 0.6f + 4.5f * u, r.bottom + 2.4f * u)
        drawRect(t(Color(0xFFD9C79E), oscuro), tag.topLeft, tag.size)
        drawRect(e.acento.copy(alpha = 0.7f), Offset(tag.left + 0.8f * u, tag.center.y - 0.2f * u), Size(tag.width - 1.6f * u, 0.4f * u))
    }

    /** Un compás de bronce abierto: dos brazos que se tocan en la charnela. */
    private fun DrawScope.compas(a: Offset, b: Offset, oscuro: Boolean) {
        val u = un()
        val bisagra = Offset((a.x + b.x) / 2, minOf(a.y, b.y) - 2.4f * u)
        listOf(a, b).forEach { p ->
            drawLine(Color.Black.copy(alpha = 0.25f), Offset(bisagra.x + 0.5f * u, bisagra.y + 0.8f * u), Offset(p.x + 0.5f * u, p.y + 0.8f * u), 1f * u, StrokeCap.Round)
            drawLine(t(BRONCE, oscuro), bisagra, p, 0.9f * u, StrokeCap.Round)
            drawLine(t(BRONCE_LUZ, oscuro), bisagra, p, 0.3f * u, StrokeCap.Round)
        }
        drawCircle(t(BRONCE_OSC, oscuro), 1.2f * u, bisagra)
        drawCircle(t(BRONCE_LUZ, oscuro), 0.5f * u, bisagra)
    }

    /** Un tintero de bronce visto desde arriba: el aro, la tapa y la boca negra. */
    private fun DrawScope.tintero(c: Offset, r: Float, oscuro: Boolean) {
        val u = un()
        drawCircle(Color.Black.copy(alpha = 0.3f), r, Offset(c.x + 0.6f * u, c.y + 1f * u))
        drawCircle(Brush.radialGradient(listOf(t(BRONCE_LUZ, oscuro), t(BRONCE, oscuro), t(BRONCE_OSC, oscuro)), Offset(c.x - r * 0.4f, c.y - r * 0.4f), r * 1.4f), r, c)
        drawCircle(t(BRONCE_OSC, oscuro), r * 0.7f, c, style = Stroke(0.3f * u))
        drawCircle(Color(0xFF100A06), r * 0.42f, c)
        drawCircle(Color.White.copy(alpha = 0.3f), r * 0.12f, Offset(c.x - r * 0.15f, c.y - r * 0.15f))
    }

    /** El cálamo: la caña cortada en punta, manchada de tinta. */
    private fun DrawScope.calamo(a: Offset, b: Offset, oscuro: Boolean) {
        val u = un()
        drawLine(Color.Black.copy(alpha = 0.25f), Offset(a.x + 0.4f * u, a.y + 0.7f * u), Offset(b.x + 0.4f * u, b.y + 0.7f * u), 0.9f * u, StrokeCap.Round)
        drawLine(t(Color(0xFFC9B27A), oscuro), a, b, 0.8f * u, StrokeCap.Round)
        val f = 0.18f
        drawLine(Color(0xFF1A120A), Offset(b.x + (a.x - b.x) * f, b.y + (a.y - b.y) * f), b, 0.7f * u, StrokeCap.Round)
        drawLine(t(Color(0xFF9A8250), oscuro), Offset(a.x + (b.x - a.x) * 0.45f, a.y + (b.y - a.y) * 0.45f), Offset(a.x + (b.x - a.x) * 0.47f, a.y + (b.y - a.y) * 0.47f), 1f * u)
    }

    /** Una regla de bronce con sus marcas de pies y pulgadas romanas. */
    private fun DrawScope.regla(r: Rect, oscuro: Boolean) {
        val u = un()
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(r.left + 0.6f * u, r.top + 0.9f * u), r.size)
        drawRect(Brush.horizontalGradient(listOf(t(BRONCE_LUZ, oscuro), t(BRONCE, oscuro), t(BRONCE_OSC, oscuro)), r.left, r.right), r.topLeft, r.size)
        val n = 16
        for (k in 1 until n) {
            val y = r.top + r.height * k / n
            drawLine(t(BRONCE_OSC, oscuro), Offset(r.left, y), Offset(r.left + r.width * (if (k % 4 == 0) 0.7f else 0.4f), y), 0.25f * u)
        }
        drawCircle(t(BRONCE_OSC, oscuro), 0.4f * u, Offset(r.center.x, r.top + 1.2f * u))
    }

    /** Una lucerna de bronce vista desde arriba: el cuerpo redondo, el pico y la llama con su halo. */
    private fun DrawScope.lucerna(c: Offset, r: Float, oscuro: Boolean, fuera: Rect) {
        val u = un()
        val pico = Offset(c.x, c.y - r * 1.5f)
        drawCircle(Color.Black.copy(alpha = 0.3f), r, Offset(c.x + 0.6f * u, c.y + 1 * u))
        val cuerpo = Path().apply {
            addOval(Rect(c.x - r, c.y - r, c.x + r, c.y + r))
            moveTo(c.x - r * 0.45f, c.y - r * 0.6f); lineTo(pico.x - r * 0.35f, pico.y); lineTo(pico.x + r * 0.35f, pico.y); lineTo(c.x + r * 0.45f, c.y - r * 0.6f); close()
        }
        drawPath(cuerpo, Brush.radialGradient(listOf(t(BRONCE_LUZ, oscuro), t(BRONCE, oscuro), t(BRONCE_OSC, oscuro)), Offset(c.x - r * 0.3f, c.y - r * 0.3f), r * 1.6f))
        drawCircle(t(BRONCE_OSC, oscuro), r * 0.55f, c, style = Stroke(0.3f * u))
        drawCircle(Color(0xFF1A120A), r * 0.16f, c)
        drawArc(t(BRONCE, oscuro), -40f, 80f, false, Offset(c.x + r * 0.7f, c.y - r * 0.4f), Size(r * 0.8f, r * 0.8f), style = Stroke(0.6f * u))
        llama(Offset(pico.x, pico.y + r * 0.1f), r * 0.32f, oscuro, fuera)
    }

    /** Una escuadra de bronce (norma). */
    private fun DrawScope.escuadra(o: Offset, l: Float, oscuro: Boolean) {
        val u = un()
        val g = 1.4f * u
        val p = Path().apply {
            moveTo(o.x, o.y); lineTo(o.x + g, o.y); lineTo(o.x + g, o.y + l - g); lineTo(o.x + l * 0.62f, o.y + l - g); lineTo(o.x + l * 0.62f, o.y + l); lineTo(o.x, o.y + l); close()
        }
        drawPath(p, Color.Black.copy(alpha = 0.25f), style = Stroke(0.9f * u))
        drawPath(p, Brush.linearGradient(listOf(t(BRONCE_LUZ, oscuro), t(BRONCE, oscuro)), o, Offset(o.x + l, o.y + l)))
        for (k in 1..4) drawLine(t(BRONCE_OSC, oscuro), Offset(o.x, o.y + l * k / 5f), Offset(o.x + g * 0.5f, o.y + l * k / 5f), 0.22f * u)
    }

    /** Una plomada (perpendiculum): el hilo y la pesa cónica de plomo. */
    private fun DrawScope.plomada(a: Offset, b: Offset, oscuro: Boolean) {
        val u = un()
        drawLine(t(Color(0xFFB8A784), oscuro), a, b, 0.25f * u)
        drawCircle(t(BRONCE, oscuro), 0.7f * u, a)
        val p = Path().apply { moveTo(b.x - 1.4f * u, b.y); lineTo(b.x + 1.4f * u, b.y); lineTo(b.x, b.y + 3.6f * u); close() }
        drawPath(p, Brush.horizontalGradient(listOf(t(Color(0xFF8E908A), oscuro), t(Color(0xFFD0D2CC), oscuro), t(Color(0xFF6A6C66), oscuro)), b.x - 1.4f * u, b.x + 1.4f * u))
    }

    // ================================================================ Otras vidas: las hornacinas

    override fun DrawScope.muebleOtrasVidas(e: EstadoMueble, oscuro: Boolean) {
        if (size.minDimension < 1f) return
        val w = size.width; val h = size.height; val u = un()
        val n = nichoPx(MuebleVida.OTRAS_VIDAS)
        val celda = n.width / 5f
        val interior = if (oscuro) PANEL_NOCHE else PANEL_DIA
        // El fondo de la vitrina y las cinco hornacinas, con su sombra de bóveda y la venera.
        drawRect(interior, n.topLeft, n.size)
        for (i in 0 until 5) {
            val c = Rect(n.left + celda * i, n.top, n.left + celda * (i + 1), n.bottom)
            val r = c.width / 2
            clipRect(c.left, c.top, c.right, c.bottom) {
                drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.1f), Color.Transparent, Color.Transparent, Color.Black.copy(alpha = if (oscuro) 0.3f else 0.12f)), c.left, c.right), c.topLeft, c.size)
                drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.35f else 0.12f), Color.Transparent), c.top, c.top + r * 1.3f), c.topLeft, Size(c.width, r * 1.3f))
                // La venera, tenue, en la cabeza de la hornacina.
                val vc = Offset(c.center.x, c.top + r)
                val vr = r * 0.78f
                val conch = if (oscuro) Color.White.copy(alpha = 0.06f) else Color(0xFF9C8A6A).copy(alpha = 0.16f)
                for (k in 0..8) {
                    val a = Math.toRadians(180.0 + k * 22.5)
                    drawLine(conch, Offset(vc.x + vr * 0.18f * cos(a).toFloat(), vc.y + vr * 0.18f * sin(a).toFloat()), Offset(vc.x + vr * cos(a).toFloat(), vc.y + vr * sin(a).toFloat()), 0.35f * u)
                }
                drawArc(conch, 180f, 180f, false, Offset(vc.x - vr, vc.y - vr), Size(vr * 2, vr * 2), style = Stroke(0.35f * u))
                // Las enjutas de mármol sobre el arco, con su archivolta y una roseta de oro.
                val enjuta = Path().apply {
                    moveTo(c.left, c.top); lineTo(c.right, c.top); lineTo(c.right, c.top + r)
                    arcTo(Rect(c.left, c.top, c.right, c.top + 2 * r), 0f, -180f, false); close()
                }
                drawPath(enjuta, Brush.verticalGradient(listOf(t(mez(MARMOL, Color.White, 0.3f), oscuro), t(MARMOL, oscuro)), c.top, c.top + r))
                drawArc(t(MARMOL_SOMBRA, oscuro), 180f, 180f, false, Offset(c.left + 0.4f * u, c.top + 0.4f * u), Size(c.width - 0.8f * u, 2 * r - 0.8f * u), style = Stroke(1.0f * u))
                drawArc(if (oscuro) e.acento.copy(alpha = 0.7f) else t(ORO_OSC, oscuro), 180f, 180f, false, Offset(c.left + 1.2f * u, c.top + 1.2f * u), Size(c.width - 2.4f * u, 2 * r - 2.4f * u), style = Stroke(0.35f * u))
                // La repisa de mármol al pie.
                val rep = Rect(c.left, c.bottom - 2.2f * u, c.right, c.bottom)
                drawRect(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.1f), Offset(rep.left, rep.top - 1.2f * u), Size(rep.width, 1.2f * u))
                marmol(rep, oscuro, 70 + i, 0)
            }
            listOf(c.left, c.right).forEach { x -> drawCircle(t(ORO, oscuro), 0.7f * u, Offset(x, c.top + 1.6f * u)) }
        }
        // Las columnillas de pórfido entre hornacinas, con basa y capitel de bronce dorado.
        val col = minOf(w * 0.012f, 1.9f * u)
        for (i in 1 until 5) {
            val x = n.left + celda * i
            val cap = n.top + celda * 0.5f
            drawRect(Brush.horizontalGradient(listOf(t(PORFIDO_OSC, oscuro), t(PORFIDO_LUZ, oscuro), t(PORFIDO, oscuro), t(PORFIDO_OSC, oscuro)), x - col / 2, x + col / 2), Offset(x - col / 2, cap), Size(col, n.bottom - cap))
            drawRect(Brush.horizontalGradient(listOf(t(ORO_OSC, oscuro), t(ORO_LUZ, oscuro), t(ORO_OSC, oscuro)), x - col, x + col), Offset(x - col * 0.9f, cap - 1.4f * u), Size(col * 1.8f, 1.4f * u))
            drawRect(Brush.horizontalGradient(listOf(t(ORO_OSC, oscuro), t(ORO_LUZ, oscuro), t(ORO_OSC, oscuro)), x - col, x + col), Offset(x - col * 0.9f, n.bottom - 3.4f * u), Size(col * 1.8f, 1.2f * u))
        }
        // Las pilastras de mármol de los costados, acanaladas, con capitel y basa.
        listOf(Rect(w * 0.006f, n.top - 1.5f * u, n.left, n.bottom + 1.5f * u), Rect(n.right, n.top - 1.5f * u, w * 0.994f, n.bottom + 1.5f * u)).forEachIndexed { i, r ->
            marmol(r, oscuro, 90 + i, 1)
            val f = Rect(r.left + r.width * 0.2f, r.top + 4 * u, r.right - r.width * 0.2f, r.bottom - 4 * u)
            for (k in 0..2) {
                val x = f.left + f.width * k / 2f
                drawLine(t(MARMOL_VETA, oscuro).copy(alpha = 0.6f), Offset(x, f.top), Offset(x, f.bottom), 0.35f * u, StrokeCap.Round)
                drawLine(Color.White.copy(alpha = if (oscuro) 0.1f else 0.5f), Offset(x + 0.4f * u, f.top), Offset(x + 0.4f * u, f.bottom), 0.25f * u, StrokeCap.Round)
            }
            moldura(Rect(r.left - 0.6f * u, r.top, r.right + 0.6f * u, r.top + 2.8f * u), t(mez(MARMOL, Color.White, 0.5f), oscuro), t(MARMOL, oscuro), t(MARMOL_SOMBRA, oscuro))
            moldura(Rect(r.left - 0.6f * u, r.bottom - 2.8f * u, r.right + 0.6f * u, r.bottom), t(MARMOL, oscuro), t(MARMOL_SOMBRA, oscuro), t(MARMOL_VETA, oscuro))
            drawLine(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.18f), Offset(if (i == 0) r.right else r.left, r.top), Offset(if (i == 0) r.right else r.left, r.bottom), 0.6f * u)
        }
        // La cornisa con dentículos y el frontón con la corona de laurel y las acroteras.
        val yEnt = n.top - 1.5f * u
        val arq = Rect(0f, yEnt - h * 0.055f, w, yEnt)
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.2f), Color.Transparent), yEnt, yEnt + 2.5f * u), Offset(n.left, yEnt), Size(n.width, 2.5f * u))
        val friso = Rect(w * 0.005f, arq.top, w * 0.995f, arq.bottom)
        drawRect(Brush.verticalGradient(listOf(t(PORFIDO_LUZ, oscuro), t(PORFIDO, oscuro), t(PORFIDO_OSC, oscuro)), friso.top, friso.bottom), friso.topLeft, friso.size)
        roleo(Rect(friso.left + 2 * u, friso.top + friso.height * 0.18f, friso.right - 2 * u, friso.bottom - friso.height * 0.18f), t(ORO, oscuro))
        drawLine(t(ORO, oscuro), Offset(friso.left, friso.bottom - 0.4f * u), Offset(friso.right, friso.bottom - 0.4f * u), 0.4f * u)
        val den = Rect(0f, arq.top - h * 0.03f, w, arq.top)
        denticulos(den, t(MARMOL_VETA, oscuro), t(MARMOL, oscuro), Color.White.copy(alpha = if (oscuro) 0.08f else 0.4f))
        val cornisa = Rect(0f, den.top - h * 0.035f, w, den.top)
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(0f, den.top), Size(w, 0.5f * u))
        marmol(cornisa, oscuro, 33, 1)
        drawLine(Color.White.copy(alpha = if (oscuro) 0.15f else 0.6f), Offset(0f, cornisa.top + 0.3f * u), Offset(w, cornisa.top + 0.3f * u), 0.5f * u)
        val apice = Offset(w / 2, h * 0.012f)
        val fronton = Path().apply { moveTo(w * 0.02f, cornisa.top); lineTo(apice.x, apice.y); lineTo(w * 0.98f, cornisa.top); close() }
        drawPath(fronton, Brush.verticalGradient(listOf(t(mez(MARMOL, Color.White, 0.3f), oscuro), t(MARMOL, oscuro)), apice.y, cornisa.top))
        val alto = cornisa.top - apice.y
        val timpano = Path().apply { moveTo(w * 0.09f, cornisa.top - 0.5f * u); lineTo(apice.x, apice.y + alto * 0.42f); lineTo(w * 0.91f, cornisa.top - 0.5f * u); close() }
        drawPath(timpano, t(PORFIDO, oscuro))
        drawPath(timpano, t(ORO, oscuro), style = Stroke(0.35f * u))
        drawPath(fronton, t(MARMOL_VETA, oscuro), style = Stroke(0.35f * u))
        laurel(Offset(apice.x, cornisa.top - alto * 0.34f), alto * 0.22f, t(ORO, oscuro), e.acento)
        listOf(Offset(w * 0.025f, cornisa.top), Offset(w * 0.975f, cornisa.top)).forEach { o -> palmeta(o, alto * 0.42f, t(ORO, oscuro), t(ORO_OSC, oscuro)) }
        palmeta(Offset(apice.x, apice.y + alto * 0.05f), alto * 0.3f, t(ORO, oscuro), t(ORO_OSC, oscuro))
        // El podio: la moldura de la repisa, los tableros de pórfido bajo cada hornacina y el plinto.
        val podio = Rect(0f, n.bottom + 1.5f * u, w, h)
        val rep = Rect(0f, podio.top, w, podio.top + h * 0.035f)
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(0f, rep.bottom), Size(w, 0.8f * u))
        marmol(rep, oscuro, 41, 1)
        drawLine(Color.White.copy(alpha = if (oscuro) 0.15f else 0.6f), Offset(0f, rep.top + 0.3f * u), Offset(w, rep.top + 0.3f * u), 0.5f * u)
        val cuerpo = Rect(w * 0.012f, rep.bottom, w * 0.988f, h - h * 0.045f)
        marmol(cuerpo, oscuro, 43, 3)
        for (i in 0 until 5) {
            val tb = Rect(n.left + celda * i + celda * 0.12f, cuerpo.top + cuerpo.height * 0.2f, n.left + celda * (i + 1) - celda * 0.12f, cuerpo.bottom - cuerpo.height * 0.2f)
            drawRect(Brush.verticalGradient(listOf(t(PORFIDO_LUZ, oscuro), t(PORFIDO, oscuro)), tb.top, tb.bottom), tb.topLeft, tb.size)
            for (k in 0 until 6) drawCircle(t(mez(PORFIDO_LUZ, Color.White, 0.3f), oscuro).copy(alpha = 0.4f), 0.2f * u, Offset(tb.left + tb.width * azar(e.semilla + i, k), tb.top + tb.height * azar(e.semilla + i + 9, k)))
            drawRect(t(ORO, oscuro), tb.topLeft, tb.size, style = Stroke(0.4f * u))
            drawRect(Color.Black.copy(alpha = 0.25f), Offset(tb.left, tb.top), Size(tb.width, 0.5f * u))
        }
        val plinto = Rect(0f, cuerpo.bottom, w, h)
        moldura(plinto, t(MARMOL, oscuro), t(MARMOL_SOMBRA, oscuro), t(MARMOL_VETA, oscuro))
    }

    // ================================================================ Íconos de los mapas

    override fun DrawScope.iconoMapa(id: String, e: EstadoIcono, oscuro: Boolean) {
        if (size.minDimension < 1f) return
        val w = size.width; val h = size.height
        val u = h / 100f
        val c = Offset(w / 2, h / 2)
        when (id) {
            "unesco" -> {
                // El templo hexástilo: podio con escalinata, seis columnas, arquitrabe, frontón con tímpano del acento y acroteras.
                val linea = if (oscuro) Color.Black.copy(alpha = 0.55f) else Color(0xFF7A7064)
                val a = 50f * u
                drawOval(Color.Black.copy(alpha = if (oscuro) 0.35f else 0.14f), Offset(c.x - a * 1.15f, h * 0.88f), Size(a * 2.3f, 7 * u))
                for (k in 0 until 3) {
                    val y = h * 0.78f + k * 3.4f * u
                    val ww = a * (1.62f + k * 0.12f)
                    val esc = Rect(c.x - ww / 2, y, c.x + ww / 2, y + 3.4f * u)
                    drawRect(Brush.verticalGradient(listOf(t(mez(MARMOL, Color.White, 0.5f), oscuro), t(MARMOL_SOMBRA, oscuro)), esc.top, esc.bottom), esc.topLeft, esc.size)
                    drawRect(linea, esc.topLeft, esc.size, style = Stroke(0.6f * u))
                }
                val yCap = h * 0.38f; val yBase = h * 0.78f
                val cols = 6
                val ancho = a * 1.4f
                drawRect(t(MARMOL_VETA, oscuro).copy(alpha = 0.6f), Offset(c.x - ancho / 2, yCap), Size(ancho, yBase - yCap))
                for (k in 0 until cols) {
                    val x = c.x - ancho / 2 + ancho * k / (cols - 1)
                    val cw = 5.2f * u
                    val f = Rect(x - cw / 2, yCap + 2.6f * u, x + cw / 2, yBase - 2.2f * u)
                    drawRect(Brush.horizontalGradient(listOf(t(MARMOL_SOMBRA, oscuro), t(mez(MARMOL, Color.White, 0.6f), oscuro), t(MARMOL, oscuro), t(MARMOL_VETA, oscuro)), f.left, f.right), f.topLeft, f.size)
                    drawLine(t(MARMOL_VETA, oscuro).copy(alpha = 0.7f), Offset(x - cw * 0.15f, f.top), Offset(x - cw * 0.15f, f.bottom), 0.5f * u)
                    drawLine(t(MARMOL_VETA, oscuro).copy(alpha = 0.7f), Offset(x + cw * 0.2f, f.top), Offset(x + cw * 0.2f, f.bottom), 0.5f * u)
                    drawRect(linea, f.topLeft, f.size, style = Stroke(0.5f * u))
                    val cap = Rect(x - cw * 0.8f, yCap, x + cw * 0.8f, yCap + 2.6f * u)
                    drawRect(t(MARMOL, oscuro), cap.topLeft, cap.size); drawRect(linea, cap.topLeft, cap.size, style = Stroke(0.5f * u))
                    val bas = Rect(x - cw * 0.75f, yBase - 2.2f * u, x + cw * 0.75f, yBase)
                    drawRect(t(MARMOL_SOMBRA, oscuro), bas.topLeft, bas.size); drawRect(linea, bas.topLeft, bas.size, style = Stroke(0.5f * u))
                }
                val arq = Rect(c.x - ancho / 2 - 6 * u, yCap - 8.5f * u, c.x + ancho / 2 + 6 * u, yCap)
                drawRect(Brush.verticalGradient(listOf(t(mez(MARMOL, Color.White, 0.5f), oscuro), t(MARMOL_SOMBRA, oscuro)), arq.top, arq.bottom), arq.topLeft, arq.size)
                drawRect(e.c1, Offset(arq.left + 2 * u, arq.top + 3.6f * u), Size(arq.width - 4 * u, 2.2f * u))
                drawRect(linea, arq.topLeft, arq.size, style = Stroke(0.6f * u))
                val cima = h * 0.07f
                val fr = Path().apply { moveTo(arq.left - 2 * u, arq.top); lineTo(c.x, cima); lineTo(arq.right + 2 * u, arq.top); close() }
                drawPath(fr, Brush.verticalGradient(listOf(t(mez(MARMOL, Color.White, 0.5f), oscuro), t(MARMOL, oscuro)), cima, arq.top))
                val ti = Path().apply { moveTo(arq.left + 6 * u, arq.top - 1.4f * u); lineTo(c.x, cima + 5f * u); lineTo(arq.right - 6 * u, arq.top - 1.4f * u); close() }
                drawPath(ti, e.c1)
                drawPath(fr, linea, style = Stroke(0.7f * u))
                laurel(Offset(c.x, arq.top - 6.5f * u), 3.2f * u, t(ORO, oscuro), e.c2)
                listOf(Offset(arq.left - 2 * u, arq.top), Offset(c.x, cima), Offset(arq.right + 2 * u, arq.top)).forEach { palmeta(it, 4.5f * u, t(ORO, oscuro), t(ORO_OSC, oscuro)) }
            }
            "rascar" -> {
                // La tablilla con su lámina de oro raspada (debajo asoma el mapa) y el denario de plata que la raspa.
                val r = Rect(c.x - 44 * u, h * 0.1f, c.x + 30 * u, h * 0.8f)
                drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.18f), Offset(r.left + 1.5f * u, r.top + 2.5f * u), r.size, CornerRadius(2.5f * u))
                drawRoundRect(Brush.verticalGradient(listOf(t(NOGAL_LUZ, oscuro), t(NOGAL, oscuro)), r.top, r.bottom), r.topLeft, r.size, CornerRadius(2.5f * u))
                vetas(r, t(NOGAL_OSC, oscuro).copy(alpha = 0.6f), 3, 2.2f * u)
                drawRoundRect(Color.White.copy(alpha = 0.18f), r.topLeft, r.size, CornerRadius(2.5f * u), style = Stroke(0.6f * u))
                val lam = Rect(r.left + 5 * u, r.top + 5 * u, r.right - 5 * u, r.bottom - 5 * u)
                drawRect(Color.Black.copy(alpha = 0.35f), lam.topLeft, lam.size)
                // El mapa de abajo: mar del secundario y tierras de pergamino.
                val mapa = Rect(lam.left + 0.6f * u, lam.top + 0.6f * u, lam.right - 0.6f * u, lam.bottom - 0.6f * u)
                drawRect(mez(e.c2, Color.White, if (oscuro) 0.05f else 0.3f), mapa.topLeft, mapa.size)
                val tierra = Path().apply {
                    moveTo(mapa.left + mapa.width * 0.3f, mapa.top); lineTo(mapa.left + mapa.width * 0.62f, mapa.top)
                    quadraticTo(mapa.left + mapa.width * 0.5f, mapa.top + mapa.height * 0.4f, mapa.left + mapa.width * 0.62f, mapa.top + mapa.height * 0.55f)
                    quadraticTo(mapa.left + mapa.width * 0.45f, mapa.top + mapa.height * 0.95f, mapa.left + mapa.width * 0.2f, mapa.bottom)
                    lineTo(mapa.left + mapa.width * 0.08f, mapa.bottom)
                    quadraticTo(mapa.left + mapa.width * 0.35f, mapa.top + mapa.height * 0.5f, mapa.left + mapa.width * 0.3f, mapa.top); close()
                }
                drawPath(tierra, t(PERGAMINO, oscuro))
                drawPath(tierra, mez(e.c2, Color.Black, 0.3f), style = Stroke(0.5f * u))
                drawCircle(e.c1, 1.6f * u, Offset(mapa.left + mapa.width * 0.38f, mapa.top + mapa.height * 0.45f))
                // La lámina de oro, con un camino raspado en diagonal y sus virutas.
                val raspado = Path().apply {
                    moveTo(lam.left + lam.width * 0.05f, lam.bottom); lineTo(lam.left + lam.width * 0.38f, lam.bottom)
                    lineTo(lam.left + lam.width * 0.78f, lam.top); lineTo(lam.left + lam.width * 0.45f, lam.top); close()
                }
                clipPath(raspado, ClipOp.Difference) {
                    drawRect(Brush.linearGradient(listOf(t(ORO_LUZ, oscuro), t(ORO, oscuro), t(ORO_OSC, oscuro), t(ORO, oscuro), t(ORO_LUZ, oscuro)), lam.topLeft, lam.bottomRight), lam.topLeft, lam.size)
                    for (k in 0 until 9) drawLine(Color.White.copy(alpha = 0.22f), Offset(lam.left + lam.width * k / 9f, lam.top), Offset(lam.left + lam.width * k / 9f - 6 * u, lam.bottom), 0.4f * u)
                }
                drawPath(raspado, t(ORO_OSC, oscuro), style = Stroke(0.7f * u))
                listOf(Offset(r.left + 2.4f * u, r.top + 2.4f * u), Offset(r.right - 2.4f * u, r.top + 2.4f * u), Offset(r.left + 2.4f * u, r.bottom - 2.4f * u), Offset(r.right - 2.4f * u, r.bottom - 2.4f * u)).forEach { drawCircle(t(BRONCE_LUZ, oscuro), 1.3f * u, it) }
                // El denario: plata con su canto, la gráfila y la cabeza laureada de perfil.
                val mr = 16 * u
                val mc = Offset(r.right + 4 * u, h * 0.78f)
                drawCircle(Color.Black.copy(alpha = 0.3f), mr, Offset(mc.x + 1.5f * u, mc.y + 2 * u))
                drawCircle(t(PLATA_OSC, oscuro), mr, mc)
                drawCircle(Brush.radialGradient(listOf(t(Color.White, oscuro), t(PLATA, oscuro), t(PLATA_OSC, oscuro)), Offset(mc.x - mr * 0.4f, mc.y - mr * 0.4f), mr * 1.5f), mr * 0.92f, mc)
                for (k in 0 until 26) { val ang = k * Math.PI * 2 / 26; drawCircle(t(PLATA_OSC, oscuro), 0.5f * u, Offset(mc.x + mr * 0.8f * cos(ang).toFloat(), mc.y + mr * 0.8f * sin(ang).toFloat())) }
                val cab = Path().apply {
                    moveTo(mc.x - mr * 0.15f, mc.y + mr * 0.55f)
                    quadraticTo(mc.x - mr * 0.5f, mc.y + mr * 0.1f, mc.x - mr * 0.2f, mc.y - mr * 0.4f)
                    quadraticTo(mc.x + mr * 0.15f, mc.y - mr * 0.62f, mc.x + mr * 0.32f, mc.y - mr * 0.2f)
                    lineTo(mc.x + mr * 0.42f, mc.y - mr * 0.02f); lineTo(mc.x + mr * 0.3f, mc.y + mr * 0.05f)
                    quadraticTo(mc.x + mr * 0.32f, mc.y + mr * 0.3f, mc.x + mr * 0.12f, mc.y + mr * 0.3f)
                    lineTo(mc.x + mr * 0.15f, mc.y + mr * 0.55f); close()
                }
                drawPath(cab, t(PLATA_OSC, oscuro))
                drawPath(cab, Color.White.copy(alpha = 0.35f), style = Stroke(0.4f * u))
                for (k in 0 until 5) drawOval(t(LAUREL, oscuro), Offset(mc.x - mr * 0.42f + k * mr * 0.13f, mc.y - mr * 0.44f + abs(k - 2) * mr * 0.05f), Size(mr * 0.15f, mr * 0.08f))
            }
            else -> {
                // Lugares: una vía de losas de basalto y tres miliarios a su vera, con su banda del acento y del secundario.
                val p0 = Offset(c.x - 66 * u, h * 0.9f); val p1 = Offset(c.x - 30 * u, h * 0.62f); val p2 = Offset(c.x + 12 * u, h * 0.98f); val p3 = Offset(c.x + 66 * u, h * 0.66f)
                fun bez(f: Float): Offset {
                    val g = 1 - f
                    return Offset(g * g * g * p0.x + 3 * g * g * f * p1.x + 3 * g * f * f * p2.x + f * f * f * p3.x, g * g * g * p0.y + 3 * g * g * f * p1.y + 3 * g * f * f * p2.y + f * f * f * p3.y)
                }
                val linea = if (oscuro) Color.Black.copy(alpha = 0.55f) else Color(0xFF7A7064)
                listOf(Triple(0.18f, 1f, 0), Triple(0.5f, 0.86f, 1), Triple(0.82f, 0.74f, 0)).forEach { (f, k, cual) ->
                    val pie = bez(f)
                    val x = pie.x; val y = pie.y - 4.5f * u
                    val a = 7.4f * u * k; val alto = 54f * u * k
                    drawOval(Color.Black.copy(alpha = 0.3f), Offset(x - a * 1.4f, y - a * 0.3f), Size(a * 3f, a * 0.8f))
                    val basa = Rect(x - a * 1.25f, y - a * 0.9f, x + a * 1.25f, y)
                    drawRect(Brush.verticalGradient(listOf(t(MARMOL, oscuro), t(MARMOL_SOMBRA, oscuro)), basa.top, basa.bottom), basa.topLeft, basa.size)
                    drawRect(linea, basa.topLeft, basa.size, style = Stroke(0.5f * u))
                    val fuste = Rect(x - a, y - alto, x + a, basa.top)
                    drawRect(Brush.horizontalGradient(listOf(t(MARMOL_SOMBRA, oscuro), t(mez(MARMOL, Color.White, 0.6f), oscuro), t(MARMOL, oscuro), t(MARMOL_VETA, oscuro)), fuste.left, fuste.right), fuste.topLeft, fuste.size)
                    drawLine(linea, fuste.topLeft, Offset(fuste.left, fuste.bottom), 0.5f * u)
                    drawLine(linea, Offset(fuste.right, fuste.top), fuste.bottomRight, 0.5f * u)
                    drawOval(Brush.horizontalGradient(listOf(t(MARMOL, oscuro), t(Color.White, oscuro)), fuste.left, fuste.right), Offset(fuste.left, fuste.top - a * 0.35f), Size(fuste.width, a * 0.7f))
                    drawOval(linea, Offset(fuste.left, fuste.top - a * 0.35f), Size(fuste.width, a * 0.7f), style = Stroke(0.5f * u))
                    drawRect(if (cual == 1) e.c2 else e.c1, Offset(fuste.left, fuste.top + alto * 0.16f), Size(fuste.width, a * 0.6f))
                    for (j in 0 until 3) drawLine(t(MARMOL_VETA, oscuro), Offset(x - a * 0.55f, fuste.top + alto * (0.4f + j * 0.12f)), Offset(x + a * 0.55f, fuste.top + alto * (0.4f + j * 0.12f)), 0.6f * u)
                }
                val via = Path().apply { moveTo(p0.x, p0.y); cubicTo(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y) }
                drawPath(via, Color.Black.copy(alpha = if (oscuro) 0.4f else 0.16f), style = Stroke(12 * u, cap = StrokeCap.Round))
                drawPath(via, t(BASALTO, oscuro), style = Stroke(10 * u, cap = StrokeCap.Round))
                drawPath(via, t(mez(BASALTO, Color.White, 0.3f), oscuro), style = Stroke(8 * u, cap = StrokeCap.Butt, pathEffect = PathEffect.dashPathEffect(floatArrayOf(4 * u, 1.4f * u))))
                drawPath(via, t(MARMOL_SOMBRA, oscuro), style = Stroke(0.8f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(3 * u, 3 * u))))
            }
        }
    }

    // ================================================================ Notas: el album y sus tablillas

    override fun DrawScope.tableroNotas(e: EstadoMueble, oscuro: Boolean) {
        if (size.minDimension < 1f) return
        val w = size.width; val h = size.height; val u = un()
        val n = nichoPx(MuebleVida.NOTAS)
        // El marco de nogal, con su veta y sus molduras.
        val marco = Rect(w * 0.012f, h * 0.095f, w * 0.988f, h * 0.915f)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.45f else 0.2f), Offset(marco.left + 1 * u, marco.top + 1.6f * u), marco.size)
        drawRect(t(NOGAL, oscuro), marco.topLeft, marco.size)
        drawRect(Brush.horizontalGradient(listOf(t(NOGAL_LUZ, oscuro), t(NOGAL, oscuro)), marco.left, n.left), marco.topLeft, Size(n.left - marco.left, marco.height))
        drawRect(Brush.horizontalGradient(listOf(t(NOGAL, oscuro), t(NOGAL_OSC, oscuro)), n.right, marco.right), Offset(n.right, marco.top), Size(marco.right - n.right, marco.height))
        drawRect(Brush.verticalGradient(listOf(t(NOGAL_LUZ, oscuro), t(NOGAL, oscuro)), marco.top, n.top), Offset(n.left, marco.top), Size(n.width, n.top - marco.top))
        drawRect(Brush.verticalGradient(listOf(t(NOGAL, oscuro), t(NOGAL_OSC, oscuro)), n.bottom, marco.bottom), Offset(n.left, n.bottom), Size(n.width, marco.bottom - n.bottom))
        listOf(Rect(marco.left, marco.top, n.left - 1.4f * u, marco.bottom), Rect(n.right + 1.4f * u, marco.top, marco.right, marco.bottom)).forEachIndexed { i, r -> vetas(r, t(NOGAL_OSC, oscuro).copy(alpha = 0.6f), e.semilla + i, 1.0f * u, vertical = true) }
        listOf(Rect(n.left, marco.top, n.right, n.top - 1.4f * u), Rect(n.left, n.bottom + 1.4f * u, n.right, marco.bottom)).forEachIndexed { i, r -> vetas(r, t(NOGAL_OSC, oscuro).copy(alpha = 0.6f), e.semilla + 5 + i, 1.0f * u) }
        // Los ingletes de las esquinas.
        listOf(Offset(marco.left, marco.top) to Offset(n.left, n.top), Offset(marco.right, marco.top) to Offset(n.right, n.top), Offset(marco.left, marco.bottom) to Offset(n.left, n.bottom), Offset(marco.right, marco.bottom) to Offset(n.right, n.bottom)).forEach { (a, b) -> drawLine(t(NOGAL_OSC, oscuro), a, b, 0.4f * u) }
        drawRect(Color.White.copy(alpha = if (oscuro) 0.08f else 0.2f), marco.topLeft, marco.size, style = Stroke(0.6f * u))
        // El rebaje dorado alrededor del tablón.
        drawRect(t(ORO_OSC, oscuro), Offset(n.left - 1.4f * u, n.top - 1.4f * u), Size(n.width + 2.8f * u, n.height + 2.8f * u))
        drawRect(t(ORO, oscuro), Offset(n.left - 0.9f * u, n.top - 0.9f * u), Size(n.width + 1.8f * u, n.height + 1.8f * u), style = Stroke(0.5f * u))
        // El tablón encalado (el nicho), con la sombra del marco y su pátina.
        drawRect(if (oscuro) PANEL_NOCHE else PANEL_DIA, n.topLeft, n.size)
        clipRect(n.left, n.top, n.right, n.bottom) {
            for (k in 0 until 22) mancha(Offset(n.left + n.width * azar(e.semilla + 20, k), n.top + n.height * azar(e.semilla + 21, k)), (6f + 10f * azar(e.semilla + 22, k)) * u, (if (oscuro) Color.White else Color(0xFF8A7A5A)).copy(alpha = 0.03f))
            drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.1f), Color.Transparent), n.top, n.top + 3 * u), n.topLeft, Size(n.width, 3 * u))
            drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.25f else 0.08f), Color.Transparent), n.left, n.left + 2.5f * u), n.topLeft, Size(2.5f * u, n.height))
        }
        // La cornisa de arriba con sus dentículos, y el frontoncillo central con la corona de laurel y sus acroteras.
        val den = Rect(w * 0.006f, marco.top - h * 0.022f, w * 0.994f, marco.top + 0.6f * u)
        denticulos(den, t(NOGAL_OSC, oscuro), t(NOGAL_LUZ, oscuro), Color.White.copy(alpha = if (oscuro) 0.06f else 0.18f))
        val tapa = Rect(0f, h * 0.048f, w, den.top)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(0f, tapa.bottom), Size(w, 0.5f * u))
        moldura(tapa, t(NOGAL_LUZ, oscuro), t(NOGAL, oscuro), t(NOGAL_OSC, oscuro))
        vetas(tapa, t(NOGAL_OSC, oscuro).copy(alpha = 0.5f), e.semilla + 30, 0.9f * u)
        drawLine(t(ORO, oscuro), Offset(0f, tapa.bottom - 0.6f * u), Offset(w, tapa.bottom - 0.6f * u), 0.35f * u)
        val fa = Offset(w / 2, 0.6f * u)
        val fl = w * 0.3f; val fr = w * 0.7f
        val fronton = Path().apply { moveTo(fl, tapa.top + 0.4f * u); lineTo(fa.x, fa.y); lineTo(fr, tapa.top + 0.4f * u); close() }
        drawPath(fronton, Brush.verticalGradient(listOf(t(NOGAL_LUZ, oscuro), t(NOGAL, oscuro)), fa.y, tapa.top))
        val alto = tapa.top - fa.y
        val timpano = Path().apply { moveTo(fl + alto * 1.6f, tapa.top); lineTo(fa.x, fa.y + alto * 0.42f); lineTo(fr - alto * 1.6f, tapa.top); close() }
        drawPath(timpano, t(PORFIDO, oscuro))
        drawPath(timpano, t(ORO, oscuro), style = Stroke(0.3f * u))
        drawPath(fronton, Color.White.copy(alpha = if (oscuro) 0.08f else 0.2f), style = Stroke(0.4f * u))
        laurel(Offset(fa.x, tapa.top - alto * 0.3f), alto * 0.2f, t(ORO, oscuro), e.acento)
        listOf(Offset(fl, tapa.top), Offset(fr, tapa.top)).forEach { palmeta(it, alto * 0.5f, t(ORO, oscuro), t(ORO_OSC, oscuro)) }
        listOf(Offset(w * 0.025f, tapa.top), Offset(w * 0.975f, tapa.top)).forEach { palmeta(it, alto * 0.45f, t(ORO, oscuro), t(ORO_OSC, oscuro)) }
        // La repisa de abajo, con el estilo de bronce, la paleta para alisar y el tintero.
        val rep = Rect(0f, marco.bottom - 1.2f * u, w, h * 0.985f)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.22f), Offset(w * 0.02f, rep.bottom), Size(w * 0.96f, h - rep.bottom))
        moldura(rep, t(NOGAL_LUZ, oscuro), t(NOGAL, oscuro), t(NOGAL_OSC, oscuro))
        vetas(rep, t(NOGAL_OSC, oscuro).copy(alpha = 0.5f), e.semilla + 31, 0.9f * u)
        drawLine(t(ORO, oscuro), Offset(0f, rep.top + 0.6f * u), Offset(w, rep.top + 0.6f * u), 0.35f * u)
        drawLine(Color.Black.copy(alpha = 0.3f), Offset(0f, rep.bottom - 1.6f * u), Offset(w, rep.bottom - 1.6f * u), 0.4f * u)
        val ye = rep.top + rep.height * 0.5f
        drawLine(Color.Black.copy(alpha = 0.3f), Offset(w * 0.62f, ye + 0.8f * u), Offset(w * 0.86f, ye + 0.5f * u), 1.2f * u, StrokeCap.Round)
        drawLine(t(BRONCE, oscuro), Offset(w * 0.62f, ye), Offset(w * 0.86f, ye - 0.3f * u), 1.0f * u, StrokeCap.Round)
        drawLine(t(BRONCE_LUZ, oscuro), Offset(w * 0.62f, ye - 0.3f * u), Offset(w * 0.86f, ye - 0.6f * u), 0.3f * u, StrokeCap.Round)
        val pal = Path().apply { moveTo(w * 0.86f, ye - 1.5f * u); lineTo(w * 0.885f, ye - 2.2f * u); lineTo(w * 0.885f, ye + 1.4f * u); lineTo(w * 0.86f, ye + 0.9f * u); close() }
        drawPath(pal, t(BRONCE, oscuro))
        listOf(w * 0.06f, w * 0.94f).forEach { x -> drawCircle(t(BRONCE_LUZ, oscuro), 0.9f * u, Offset(x, ye)) }
        // El tintero de bronce, de frente, apoyado en la repisa.
        val tx = w * 0.2f; val ty = rep.top + 0.6f * u
        val ta = 2.6f * u
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(tx - ta * 1.3f, ty - ta * 0.25f), Size(ta * 2.6f, ta * 0.6f))
        drawRect(Brush.horizontalGradient(listOf(t(BRONCE_OSC, oscuro), t(BRONCE_LUZ, oscuro), t(BRONCE, oscuro), t(BRONCE_OSC, oscuro)), tx - ta, tx + ta), Offset(tx - ta, ty - ta * 1.3f), Size(ta * 2, ta * 1.3f))
        drawOval(t(BRONCE_LUZ, oscuro), Offset(tx - ta, ty - ta * 1.55f), Size(ta * 2, ta * 0.5f))
        drawOval(Color(0xFF120A06), Offset(tx - ta * 0.45f, ty - ta * 1.45f), Size(ta * 0.9f, ta * 0.3f))
        drawLine(t(Color(0xFFC9B27A), oscuro), Offset(tx + ta * 0.1f, ty - ta * 1.35f), Offset(tx + ta * 1.6f, ty - ta * 2.3f), 0.6f * u, StrokeCap.Round)
    }

    override fun DrawScope.notaTablero(e: EstadoNota, oscuro: Boolean) {
        val r = e.rect
        if (r.width < 4f || r.height < 4f) return
        val s = minOf(r.width, r.height) / 100f
        // La sombra, dentro de su rectángulo.
        val tab = Rect(r.left, r.top, r.right - 2.2f * s, r.bottom - 3f * s)
        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.22f), Offset(tab.left + 2.2f * s, tab.top + 3f * s), tab.size, CornerRadius(3 * s))
        // El marco de haya con su veta (un tono por tablilla).
        val tono = azar(e.indice + 3, 1)
        val madera = mez(HAYA, HAYA_OSC, tono * 0.35f)
        drawRoundRect(Brush.verticalGradient(listOf(t(mez(madera, HAYA_LUZ, 0.4f), oscuro), t(madera, oscuro), t(mez(madera, HAYA_OSC, 0.5f), oscuro)), tab.top, tab.bottom), tab.topLeft, tab.size, CornerRadius(3 * s))
        val fx = tab.width * 0.075f; val fy = tab.height * 0.09f
        listOf(Rect(tab.left, tab.top, tab.right, tab.top + fy), Rect(tab.left, tab.bottom - fy, tab.right, tab.bottom)).forEachIndexed { i, b -> vetas(b, t(HAYA_OSC, oscuro).copy(alpha = 0.55f), e.indice * 7 + i, 1.2f * s) }
        listOf(Rect(tab.left, tab.top + fy, tab.left + fx, tab.bottom - fy), Rect(tab.right - fx, tab.top + fy, tab.right, tab.bottom - fy)).forEachIndexed { i, b -> vetas(b, t(HAYA_OSC, oscuro).copy(alpha = 0.55f), e.indice * 7 + 3 + i, 1.2f * s, vertical = true) }
        // La cera, rebajada: sombra arriba e izquierda.
        val cera = Rect(tab.left + fx, tab.top + fy, tab.right - fx, tab.bottom - fy)
        drawRect(if (oscuro) CERA_NEGRA else CERA_CLARA, cera.topLeft, cera.size)
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.16f), Color.Transparent), cera.top, cera.top + 3 * s), cera.topLeft, Size(cera.width, 3 * s))
        drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.12f), Color.Transparent), cera.left, cera.left + 2.5f * s), cera.topLeft, Size(2.5f * s, cera.height))
        drawRect(Color.White.copy(alpha = if (oscuro) 0.05f else 0.25f), Offset(cera.left, cera.bottom - 0.8f * s), Size(cera.width, 0.8f * s))
        // Lo que se alisó: dos rayas de estilo muy tenues abajo, fuera del texto.
        if (!oscuro) drawLine(Color(0xFFB59A70).copy(alpha = 0.35f), Offset(cera.left + cera.width * 0.15f, cera.bottom - 1.6f * s), Offset(cera.left + cera.width * 0.55f, cera.bottom - 1.8f * s), 0.4f * s)
        // Los agujeros del cordel del díptico, en el canto de la izquierda.
        listOf(0.3f, 0.7f).forEach { f -> drawCircle(t(HAYA_OSC, oscuro), 0.9f * s, Offset(tab.left + fx * 0.45f, tab.top + tab.height * f)) }
        // El detalle de cada tipo, arriba a la derecha, sobre el marco.
        val esq = Offset(tab.right - fx * 0.5f - 7 * s, tab.top + fy * 0.5f)
        when (e.tipo) {
            "idea" -> {
                // Una bulla de oro colgada de su cordón.
                drawLine(t(Color(0xFF7A4A28), oscuro), Offset(esq.x - 3 * s, tab.top), Offset(esq.x, esq.y + 2 * s), 0.6f * s)
                drawLine(t(Color(0xFF7A4A28), oscuro), Offset(esq.x + 3 * s, tab.top), Offset(esq.x, esq.y + 2 * s), 0.6f * s)
                val bc = Offset(esq.x, esq.y + 4.4f * s)
                drawCircle(Color.Black.copy(alpha = 0.3f), 5.2f * s, Offset(bc.x + 0.7f * s, bc.y + 1f * s))
                drawCircle(Brush.radialGradient(listOf(ORO_LUZ, ORO, ORO_OSC), Offset(bc.x - 1.8f * s, bc.y - 1.8f * s), 6.5f * s), 5.2f * s, bc)
                drawArc(ORO_OSC, 200f, 140f, false, Offset(bc.x - 3.4f * s, bc.y - 3.4f * s), Size(6.8f * s, 6.8f * s), style = Stroke(0.6f * s))
                drawRect(ORO_OSC, Offset(bc.x - 1.6f * s, bc.y - 6.4f * s), Size(3.2f * s, 1.6f * s))
            }
            "pensamiento" -> ramita(Offset(esq.x - 16 * s, esq.y + 3 * s), Offset(esq.x + 6 * s, esq.y - 3 * s), if (oscuro) LAUREL_LUZ else LAUREL, 7)
            "proposito" -> {
                // Una tesela de pórfido engastada en oro.
                val q = 4.6f * s
                rotate(45f, esq) {
                    drawRect(Color.Black.copy(alpha = 0.3f), Offset(esq.x - q + 0.8f * s, esq.y - q + 0.8f * s), Size(2 * q, 2 * q))
                    drawRect(ORO, Offset(esq.x - q, esq.y - q), Size(2 * q, 2 * q))
                    drawRect(Brush.linearGradient(listOf(PORFIDO_LUZ, PORFIDO_OSC), Offset(esq.x - q, esq.y - q), Offset(esq.x + q, esq.y + q)), Offset(esq.x - q * 0.72f, esq.y - q * 0.72f), Size(q * 1.44f, q * 1.44f))
                    drawRect(Color.White.copy(alpha = 0.3f), Offset(esq.x - q * 0.6f, esq.y - q * 0.6f), Size(q * 0.5f, q * 0.25f))
                }
            }
            else -> {
                // Un sello de lacre sobre el cordel, en la esquina de abajo.
                val sc = Offset(tab.right - fx * 0.6f - 5 * s, tab.bottom - fy * 0.5f)
                drawLine(t(Color(0xFF7A4A28), oscuro), Offset(sc.x - 9 * s, sc.y + 0.5f * s), Offset(sc.x + 5 * s, sc.y - 0.5f * s), 0.6f * s)
                for (k in 0 until 7) drawCircle(LACRE, 4.6f * s, Offset(sc.x + 0.8f * s * cos(k * 0.9f), sc.y + 0.8f * s * sin(k * 0.9f)))
                drawCircle(mez(LACRE, Color.Black, 0.3f), 2.9f * s, sc, style = Stroke(0.6f * s))
                drawCircle(Color.White.copy(alpha = 0.3f), 0.8f * s, Offset(sc.x - 1.2f * s, sc.y - 1.2f * s))
            }
        }
        // Fijada: el filete del acento en la cera y el clavo de bronce, con su cinta, arriba al centro.
        if (e.fijada) {
            drawRect(e.acento.copy(alpha = 0.85f), Offset(cera.left + 1.2f * s, cera.top + 1.2f * s), Size(cera.width - 2.4f * s, cera.height - 2.4f * s), style = Stroke(0.7f * s))
            val cc = Offset(tab.center.x, tab.top + fy * 0.5f)
            drawLine(e.acento, Offset(cc.x - 2 * s, cc.y), Offset(cc.x - 5.5f * s, cc.y + 6 * s), 1.4f * s, StrokeCap.Round)
            drawLine(e.acento, Offset(cc.x + 2 * s, cc.y), Offset(cc.x + 5.5f * s, cc.y + 6 * s), 1.4f * s, StrokeCap.Round)
            drawCircle(Color.Black.copy(alpha = 0.35f), 3.2f * s, Offset(cc.x + 0.6f * s, cc.y + 0.9f * s))
            drawCircle(Brush.radialGradient(listOf(BRONCE_LUZ, BRONCE, BRONCE_OSC), Offset(cc.x - 1 * s, cc.y - 1 * s), 4 * s), 3.2f * s, cc)
            drawCircle(Color.White.copy(alpha = 0.45f), 0.9f * s, Offset(cc.x - 1 * s, cc.y - 1 * s))
        }
    }

    override fun tintaNota(oscuro: Boolean): Color = if (oscuro) LINO else NEGRO
}
