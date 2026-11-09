package io.github.ruddy2003.vestigium.ui.vida

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
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import io.github.ruddy2003.vestigium.ui.ideario.arcoApuntado
import io.github.ruddy2003.vestigium.ui.ideario.arcoMedioPunto
import io.github.ruddy2003.vestigium.ui.ideario.engrane
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import java.util.Random
import kotlin.math.cos
import kotlin.math.sin

/**
 * Orden: Vida en la sala capitular de acero de la Orden, con el acero, el carmesí y el latón de sus logros y su Ideario:
 * planchas remachadas, el engrane de la Orden, estandartes de cola en punta y franjas de peligro.
 * - Social: la mesa redonda del consejo, con su aro de latón y los respaldos altos; a los lados, dos armaduras con el visor
 *   encendido y la espada, delante de sus estandartes carmesí; arriba, la cornisa con su friso de engranes.
 * - Mapas: la mesa táctica. El marco de acero de esquinas cortadas con cantoneras de latón remachadas, el holograma azul con
 *   su retícula y sus anillos de alcance, los cuatro proyectores en las esquinas y la franja de peligro al pie.
 * - Otras vidas: los armeros. Un nicho de arco apuntado por medalla, con su fondo carmesí, y entre los nichos las espadas
 *   en su soporte; abajo, la repisa de acero con su filete de latón.
 * - Íconos: la ciudadela con su torre del homenaje y el engrane sobre la puerta, la cuchilla que raspa la placa, las
 *   insignias hexagonales.
 * - Notas: el panel de placas de blindaje remachadas con su orla de latón y el escudo de la Orden; las notas son placas de
 *   acero de esquinas cortadas, remachadas, con la franja de su tipo.
 */
internal object VidaOrden : DisenoVida {
    private val ACERO = Color(0xFF3C424A)
    private val ACERO_LUZ = Color(0xFF6A727C)
    private val ACERO_OSC = Color(0xFF262A30)
    private val ACERO_CLARO = Color(0xFFA9B1BA)
    private val LATON = Color(0xFFB8933E)
    private val LATON_LUZ = Color(0xFFE0C070)
    private val CARMESI = Color(0xFF9E1B22)
    private val CARMESI_OSC = Color(0xFF6A1016)
    private val AMARILLO = Color(0xFFE0B83A)
    private val NEGRO = Color(0xFF101214)
    private val HOLO = Color(0xFF8FB3C8)
    private val PLANO = Color(0xFF1E3A5C)

    /** La placa de la nota: acero cepillado claro de día, acero pavonado de noche. */
    private val PLACA = Color(0xFFD9DDE2)
    private val PLACA_NOCHE = Color(0xFF2A2F36)

    private fun DrawScope.u() = size.minDimension / 100f

    /** El color en modo oscuro, más hundido, como en sus logros. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF09090B), 0.35f) else c

    /** El latón: se hunde menos que el acero, para que siga brillando en oscuro. */
    private fun tl(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF09090B), 0.16f) else c

    /** Los centros de las cinco medallas de Otras vidas, como los reparte la fila de la pantalla (10 dp de margen, ≈ 50 dp cada una). */
    private fun DrawScope.centrosMedallas(): List<Float> {
        val pad = 10f * density; val iw = 50f * density
        val gap = ((size.width - 2 * pad - 5 * iw) / 6f).coerceAtLeast(0f)
        return List(5) { k -> pad + gap * (k + 1) + iw * (k + 0.5f) }
    }

    /** Un rectángulo con las cuatro esquinas cortadas en [ch]. */
    private fun ochavado(r: Rect, ch: Float): Path = Path().apply {
        moveTo(r.left + ch, r.top); lineTo(r.right - ch, r.top); lineTo(r.right, r.top + ch); lineTo(r.right, r.bottom - ch)
        lineTo(r.right - ch, r.bottom); lineTo(r.left + ch, r.bottom); lineTo(r.left, r.bottom - ch); lineTo(r.left, r.top + ch); close()
    }

    /** Un remache: la cabeza y su punto de luz arriba a la izquierda. */
    private fun DrawScope.remache(c: Offset, r: Float, cuerpo: Color, luz: Color) {
        drawCircle(cuerpo, r, c)
        drawCircle(luz, r * 0.42f, Offset(c.x - r * 0.3f, c.y - r * 0.3f))
    }

    /** Un polígono regular de [n] lados con centro [c] y radio [r], con un vértice arriba. */
    private fun poligono(c: Offset, r: Float, n: Int): Path = Path().apply {
        for (k in 0 until n) {
            val a = -Math.PI / 2 + k * 2 * Math.PI / n
            val x = c.x + r * cos(a).toFloat(); val y = c.y + r * sin(a).toFloat()
            if (k == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
    }

    /** Franjas de peligro amarillas y negras en [r] (un solo trazo para las negras). */
    private fun DrawScope.franjasPeligro(r: Rect, amarillo: Color, negro: Color) {
        drawRect(amarillo, r.topLeft, r.size)
        val paso = r.height * 2.2f
        if (paso < 0.5f) return
        val rayas = Path()
        var x = r.left - r.height; var k = 0
        while (x < r.right && k < 300) {
            rayas.moveTo(x, r.bottom); rayas.lineTo(x + r.height, r.top); rayas.lineTo(x + r.height + paso / 2, r.top); rayas.lineTo(x + paso / 2, r.bottom); rayas.close()
            x += paso; k++
        }
        clipRect(r.left, r.top, r.right, r.bottom) { drawPath(rayas, negro) }
    }

    /**
     * Una armadura de pie con la espada clavada delante: [cx] es su eje, [pie] la altura del suelo y [alto] su altura total.
     * El visor brilla en [visor].
     */
    private fun DrawScope.armadura(cx: Float, pie: Float, alto: Float, acero: Color, luz: Color, sombra: Color, laton: Color, visor: Color) {
        val a = alto / 100f
        // Las piernas, los escarpes y los quijotes.
        listOf(-1f, 1f).forEach { s ->
            drawRect(acero, Offset(cx + s * 5.5f * a - 3.5f * a, pie - 40f * a), Size(7f * a, 38f * a))
            drawLine(luz, Offset(cx + s * 5.5f * a - 2f * a, pie - 38f * a), Offset(cx + s * 5.5f * a - 2f * a, pie - 4f * a), 1f * a)
            drawCircle(luz, 2.6f * a, Offset(cx + s * 5.5f * a, pie - 22f * a))
            drawPath(Path().apply { moveTo(cx + s * 5.5f * a - 4f * a, pie); lineTo(cx + s * 5.5f * a - 3.5f * a, pie - 4f * a); lineTo(cx + s * 5.5f * a + 3.5f * a, pie - 4f * a); lineTo(cx + s * 5.5f * a + s * 6f * a, pie); close() }, sombra)
        }
        // Las escarcelas y el cinturón de latón.
        drawRect(acero, Offset(cx - 12f * a, pie - 50f * a), Size(24f * a, 11f * a))
        drawLine(sombra, Offset(cx - 12f * a, pie - 44.5f * a), Offset(cx + 12f * a, pie - 44.5f * a), 0.8f * a)
        drawRect(laton, Offset(cx - 12.5f * a, pie - 53f * a), Size(25f * a, 3.4f * a))
        // Los brazos.
        listOf(-1f, 1f).forEach { s -> drawRect(acero, Offset(cx + s * 16f * a - 3f * a, pie - 76f * a), Size(6f * a, 24f * a)) }
        // El peto, con su arista de luz y la cruz de la Orden en carmesí.
        val peto = Path().apply { moveTo(cx - 14f * a, pie - 80f * a); lineTo(cx + 14f * a, pie - 80f * a); lineTo(cx + 11f * a, pie - 53f * a); lineTo(cx - 11f * a, pie - 53f * a); close() }
        drawPath(peto, acero)
        drawLine(luz, Offset(cx, pie - 79f * a), Offset(cx, pie - 54f * a), 1.2f * a)
        drawPath(engrane(Offset(cx, pie - 68f * a), 5f * a, 8), laton)
        drawCircle(acero, 2f * a, Offset(cx, pie - 68f * a))
        // Las hombreras.
        listOf(-1f, 1f).forEach { s ->
            drawOval(acero, Offset(cx + s * 15f * a - 6f * a, pie - 84f * a), Size(12f * a, 9f * a))
            drawArc(luz, 200f, 140f, false, Offset(cx + s * 15f * a - 5f * a, pie - 83f * a), Size(10f * a, 7f * a), style = Stroke(1f * a))
        }
        // El yelmo cerrado, con la rendija del visor encendida y la cresta.
        drawRoundRect(acero, Offset(cx - 7f * a, pie - 98f * a), Size(14f * a, 17f * a), CornerRadius(5f * a))
        drawLine(luz, Offset(cx - 4f * a, pie - 96f * a), Offset(cx - 4f * a, pie - 84f * a), 1f * a)
        drawLine(visor.copy(alpha = 0.35f), Offset(cx - 5f * a, pie - 90f * a), Offset(cx + 5f * a, pie - 90f * a), 3f * a, StrokeCap.Round)
        drawLine(visor, Offset(cx - 4.5f * a, pie - 90f * a), Offset(cx + 4.5f * a, pie - 90f * a), 1.3f * a, StrokeCap.Round)
        drawLine(laton, Offset(cx, pie - 100f * a), Offset(cx, pie - 96f * a), 2f * a, StrokeCap.Round)
        // La espada clavada delante, con las manos sobre el pomo.
        drawLine(luz, Offset(cx, pie - 50f * a), Offset(cx, pie - 3f * a), 2.4f * a)
        drawLine(sombra, Offset(cx + 0.8f * a, pie - 50f * a), Offset(cx + 0.8f * a, pie - 3f * a), 0.6f * a)
        drawLine(laton, Offset(cx - 8f * a, pie - 51f * a), Offset(cx + 8f * a, pie - 51f * a), 2f * a, StrokeCap.Round)
        drawRect(sombra, Offset(cx - 1.4f * a, pie - 59f * a), Size(2.8f * a, 8f * a))
        drawCircle(laton, 2.4f * a, Offset(cx, pie - 60f * a))
    }

    // ---------------------------------------------------------------- Social

    override fun DrawScope.muebleSocial(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        fun m(c: Color, k: Float) = mezcla(e.fondo, t(c, oscuro), k)
        fun ml(c: Color, k: Float) = mezcla(e.fondo, tl(c, oscuro), k)
        // Las planchas del muro: juntas y remaches, apenas marcados.
        val junta = if (oscuro) m(ACERO_LUZ, 0.3f) else m(ACERO_OSC, 0.14f)
        val juntas = Path(); val remaches = Path()
        var x = 26f * u; var k = 0
        while (x < w && k < 40) {
            juntas.moveTo(x, 0f); juntas.lineTo(x, h)
            var y = 8f * u; var j = 0
            while (y < h && j < 30) { remaches.addOval(Rect(x + 1.2f * u, y, x + 2f * u, y + 0.8f * u)); y += 8f * u; j++ }
            x += 26f * u; k++
        }
        juntas.moveTo(0f, h * 0.5f); juntas.lineTo(w, h * 0.5f)
        drawPath(juntas, junta, style = Stroke(0.5f * u))
        drawPath(remaches, junta)

        // Los estandartes carmesí detrás de las armaduras, colgados de la cornisa, con su engrane bordado.
        listOf(12f * u, w - 12f * u).forEach { cx ->
            val an = 6f * u; val yb = 5f * u; val largo = h * 0.5f
            val est = Path().apply { moveTo(cx - an, yb); lineTo(cx + an, yb); lineTo(cx + an, yb + largo); lineTo(cx, yb + largo - 5f * u); lineTo(cx - an, yb + largo); close() }
            drawPath(est, m(CARMESI, if (oscuro) 0.75f else 0.8f))
            drawRect(m(CARMESI_OSC, if (oscuro) 0.6f else 0.55f), Offset(cx + an * 0.4f, yb), Size(an * 0.6f, largo - 4f * u))
            drawPath(est, ml(LATON, 0.75f), style = Stroke(0.5f * u))
            drawPath(engrane(Offset(cx, yb + largo * 0.32f), 3.4f * u, 8), ml(LATON_LUZ, 0.85f))
            drawCircle(m(CARMESI, 0.9f), 1.2f * u, Offset(cx, yb + largo * 0.32f))
        }

        // La mesa redonda del consejo, en perspectiva: el canto delantero, el tablero tenue con sus divisiones, el aro de latón.
        val mc = Offset(w / 2, h * 0.64f); val rx = w * 0.33f; val ry = h * 0.27f
        drawOval(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.08f), Offset(mc.x - rx, mc.y - ry + 5f * u), Size(2 * rx, 2 * ry))
        drawOval(m(ACERO, if (oscuro) 0.5f else 0.36f), Offset(mc.x - rx, mc.y - ry + 3f * u), Size(2 * rx, 2 * ry))
        drawOval(mezcla(e.fondo, t(ACERO, oscuro), if (oscuro) 0.12f else 0.07f), Offset(mc.x - rx, mc.y - ry), Size(2 * rx, 2 * ry))
        val divisiones = Path()
        for (j in 0 until 12) {
            val a = j * Math.PI / 6
            divisiones.moveTo(mc.x + rx * 0.62f * cos(a).toFloat(), mc.y + ry * 0.62f * sin(a).toFloat())
            divisiones.lineTo(mc.x + rx * cos(a).toFloat(), mc.y + ry * sin(a).toFloat())
        }
        drawPath(divisiones, m(ACERO_LUZ, if (oscuro) 0.25f else 0.16f), style = Stroke(0.4f * u))
        drawOval(ml(LATON, if (oscuro) 0.45f else 0.4f), Offset(mc.x - rx * 0.62f, mc.y - ry * 0.62f), Size(rx * 1.24f, ry * 1.24f), style = Stroke(0.6f * u))
        drawOval(m(ACERO_LUZ, if (oscuro) 0.55f else 0.5f), Offset(mc.x - rx, mc.y - ry), Size(2 * rx, 2 * ry), style = Stroke(0.7f * u))
        // Los respaldos altos del lado de acá de la mesa, de espaldas, con su remate en punta y el engrane de latón.
        listOf(25.0, 65.0, 115.0, 155.0).forEach { gr ->
            val a = Math.toRadians(gr)
            val px = mc.x + (rx + 1f * u) * cos(a).toFloat(); val py = mc.y + (ry + 2f * u) * sin(a).toFloat()
            val bw = 4.6f * u; val bh = 11f * u
            val resp = Path().apply { moveTo(px - bw, py + 3f * u); lineTo(px - bw, py - bh + 3f * u); lineTo(px, py - bh - 1f * u); lineTo(px + bw, py - bh + 3f * u); lineTo(px + bw, py + 3f * u); close() }
            drawPath(resp, m(ACERO, if (oscuro) 0.75f else 0.7f))
            drawPath(resp, m(ACERO_LUZ, if (oscuro) 0.6f else 0.55f), style = Stroke(0.4f * u))
            drawRect(m(CARMESI, if (oscuro) 0.7f else 0.75f), Offset(px - bw * 0.6f, py - bh * 0.5f), Size(bw * 1.2f, bh * 0.55f))
            drawPath(engrane(Offset(px, py - bh * 0.62f), 1.4f * u, 8), ml(LATON, 0.85f))
        }

        // Las armaduras a los lados, delante de sus estandartes.
        val acero = if (oscuro) m(ACERO_LUZ, 0.75f) else m(ACERO, 0.8f)
        val luz = if (oscuro) m(ACERO_CLARO, 0.75f) else m(ACERO_LUZ, 0.9f)
        val sombra = if (oscuro) m(ACERO, 0.9f) else m(ACERO_OSC, 0.9f)
        listOf(12f * u, w - 12f * u).forEach { cx -> armadura(cx, h - 2.5f * u, 60f * u, acero, luz, sombra, ml(LATON, 0.9f), e.acento) }

        // La cornisa con su friso de engranes y el canto de latón.
        drawRect(m(ACERO, if (oscuro) 0.7f else 0.6f), Offset.Zero, Size(w, 4.6f * u))
        drawLine(ml(LATON, 0.8f), Offset(0f, 4.4f * u), Offset(w, 4.4f * u), 0.5f * u)
        var xe = 7f * u; k = 0
        while (xe < w && k < 60) { drawPath(engrane(Offset(xe, 2.2f * u), 1.4f * u, 8), m(ACERO_OSC, if (oscuro) 0.9f else 0.7f)); xe += 14f * u; k++ }
        // El piso de chapa estriada.
        drawRect(m(ACERO, if (oscuro) 0.6f else 0.45f), Offset(0f, h - 2.5f * u), Size(w, 2.5f * u))
        drawLine(m(ACERO_LUZ, if (oscuro) 0.5f else 0.6f), Offset(0f, h - 2.4f * u), Offset(w, h - 2.4f * u), 0.4f * u)
    }

    // ---------------------------------------------------------------- Mapas

    override fun DrawScope.muebleMapas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        fun m(c: Color, k: Float) = mezcla(e.fondo, t(c, oscuro), k)
        fun ml(c: Color, k: Float) = mezcla(e.fondo, tl(c, oscuro), k)
        // La franja de peligro al pie de la mesa.
        franjasPeligro(Rect(w * 0.04f, h - 3f * u, w * 0.96f, h), m(AMARILLO, if (oscuro) 0.6f else 0.8f), m(NEGRO, 0.85f))
        // El marco de acero de esquinas cortadas, con su bisel.
        val marco = Rect(1f * u, 1f * u, w - 1f * u, h - 3f * u)
        val forma = ochavado(marco, 6f * u)
        drawPath(forma, m(ACERO, if (oscuro) 0.7f else 0.62f))
        drawPath(ochavado(Rect(marco.left + 0.8f * u, marco.top + 0.8f * u, marco.right - 0.8f * u, marco.bottom - 0.8f * u), 5.4f * u), m(ACERO_LUZ, if (oscuro) 0.5f else 0.6f), style = Stroke(0.5f * u))
        // El holograma: el fondo azul tenue, la retícula y los anillos de alcance (todo bajo el mapa).
        val holo = Rect(5f * u, 4f * u, w - 5f * u, h - 7f * u)
        val vidrio = ochavado(holo, 4f * u)
        drawPath(vidrio, if (oscuro) mezcla(e.fondo, Color(0xFF0B1424), 0.75f) else mezcla(e.fondo, PLANO, 0.08f))
        val linea = if (oscuro) HOLO.copy(alpha = 0.14f) else mezcla(e.fondo, PLANO, 0.2f)
        clipPath(vidrio) {
            val ret = Path()
            var x = holo.left + 8f * u; var k = 0
            while (x < holo.right && k < 80) { ret.moveTo(x, holo.top); ret.lineTo(x, holo.bottom); x += 8f * u; k++ }
            var y = holo.top + 8f * u; k = 0
            while (y < holo.bottom && k < 40) { ret.moveTo(holo.left, y); ret.lineTo(holo.right, y); y += 8f * u; k++ }
            drawPath(ret, linea, style = Stroke(0.4f * u))
            val c = holo.center
            for (j in 1..3) drawOval(linea, Offset(c.x - holo.width * 0.16f * j, c.y - holo.height * 0.17f * j), Size(holo.width * 0.32f * j, holo.height * 0.34f * j), style = Stroke(0.5f * u))
            drawLine(linea, Offset(c.x, holo.top), Offset(c.x, holo.bottom), 0.6f * u)
            drawLine(linea, Offset(holo.left, c.y), Offset(holo.right, c.y), 0.6f * u)
        }
        drawPath(vidrio, m(ACERO_OSC, if (oscuro) 0.9f else 0.7f), style = Stroke(0.8f * u))
        // Los cuatro proyectores en las esquinas del holograma, con su luz.
        listOf(Offset(holo.left + 4f * u, holo.top + 4f * u), Offset(holo.right - 4f * u, holo.top + 4f * u), Offset(holo.left + 4f * u, holo.bottom - 4f * u), Offset(holo.right - 4f * u, holo.bottom - 4f * u)).forEach { p ->
            drawCircle(HOLO.copy(alpha = if (oscuro) 0.16f else 0.22f), 3.6f * u, p)
            drawCircle(ml(LATON, 0.9f), 1.6f * u, p)
            drawCircle(if (oscuro) HOLO else mezcla(HOLO, PLANO, 0.4f), 0.7f * u, p)
        }
        // Las cantoneras de latón remachadas en las cuatro esquinas del marco, y los remaches del canto.
        listOf(
            listOf(Offset(marco.left, marco.top + 6f * u), Offset(marco.left + 6f * u, marco.top), Offset(marco.left + 12f * u, marco.top), Offset(marco.left, marco.top + 12f * u)),
            listOf(Offset(marco.right, marco.top + 6f * u), Offset(marco.right - 6f * u, marco.top), Offset(marco.right - 12f * u, marco.top), Offset(marco.right, marco.top + 12f * u)),
            listOf(Offset(marco.left, marco.bottom - 6f * u), Offset(marco.left + 6f * u, marco.bottom), Offset(marco.left + 12f * u, marco.bottom), Offset(marco.left, marco.bottom - 12f * u)),
            listOf(Offset(marco.right, marco.bottom - 6f * u), Offset(marco.right - 6f * u, marco.bottom), Offset(marco.right - 12f * u, marco.bottom), Offset(marco.right, marco.bottom - 12f * u)),
        ).forEach { q ->
            drawPath(Path().apply { moveTo(q[0].x, q[0].y); lineTo(q[1].x, q[1].y); lineTo(q[2].x, q[2].y); lineTo(q[3].x, q[3].y); close() }, ml(LATON, if (oscuro) 0.85f else 0.9f))
            val cc = Offset((q[0].x + q[1].x + q[2].x + q[3].x) / 4, (q[0].y + q[1].y + q[2].y + q[3].y) / 4)
            remache(cc, 0.9f * u, ml(LATON_LUZ, 0.9f), Color.White.copy(alpha = 0.5f))
        }
        var rx = marco.left + 18f * u; var j = 0
        while (rx < marco.right - 16f * u && j < 60) {
            remache(Offset(rx, marco.top + 1.9f * u), 0.6f * u, m(ACERO_LUZ, if (oscuro) 0.8f else 0.9f), Color.White.copy(alpha = if (oscuro) 0.15f else 0.4f))
            remache(Offset(rx, marco.bottom - 1.9f * u), 0.6f * u, m(ACERO_LUZ, if (oscuro) 0.8f else 0.9f), Color.White.copy(alpha = if (oscuro) 0.15f else 0.4f))
            rx += 12f * u; j++
        }
    }

    // ---------------------------------------------------------------- Otras vidas

    override fun DrawScope.muebleOtrasVidas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u(); val d = density
        fun m(c: Color, k: Float) = mezcla(e.fondo, t(c, oscuro), k)
        fun ml(c: Color, k: Float) = mezcla(e.fondo, tl(c, oscuro), k)
        val centros = centrosMedallas()
        val paso = (w - 20f * d) / 5f
        val my = h / 2 - 10f * d
        val estante = minOf(my + 46f * d, h - 9f * u)
        // El muro de acero del armero, con sus juntas.
        drawRect(m(ACERO, if (oscuro) 0.4f else 0.22f), Offset.Zero, size)
        val junta = if (oscuro) m(ACERO_LUZ, 0.35f) else m(ACERO_OSC, 0.25f)
        drawLine(junta, Offset(0f, h * 0.45f), Offset(w, h * 0.45f), 0.4f * u)
        // Los nichos de arco apuntado, con su fondo carmesí (tenue, para que se lean la medalla y su año).
        val nw = minOf(paso * 0.78f, 58f * d)
        centros.forEach { cx ->
            val r = Rect(cx - nw / 2, 3.5f * u, cx + nw / 2, estante)
            val arco = arcoApuntado(r, nw * 0.55f)
            drawPath(arco, mezcla(e.fondo, t(CARMESI_OSC, oscuro), if (oscuro) 0.35f else 0.12f))
            clipPath(arco) { drawRect(Color.Black.copy(alpha = if (oscuro) 0.25f else 0.08f), Offset(r.left, r.top), Size(r.width, nw * 0.5f)) }
            drawPath(arco, m(ACERO_LUZ, if (oscuro) 0.7f else 0.7f), style = Stroke(1.4f * u))
            drawPath(arco, ml(LATON, if (oscuro) 0.6f else 0.55f), style = Stroke(0.4f * u))
            drawPath(engrane(Offset(cx, r.top + nw * 0.2f), 1.8f * u, 8), ml(LATON, 0.85f))
        }
        // Las espadas en su soporte, entre los nichos.
        for (k in 0..5) {
            val x = when (k) { 0 -> centros[0] - paso / 2; 5 -> centros[4] + paso / 2; else -> (centros[k - 1] + centros[k]) / 2 }
            if (x < 3f * u || x > w - 3f * u) continue
            val top = 8f * u; val pie = estante - 1f * u
            drawRect(m(ACERO_OSC, if (oscuro) 0.9f else 0.6f), Offset(x - 2.4f * u, pie - 2.6f * u), Size(4.8f * u, 2.6f * u))
            drawLine(if (oscuro) m(ACERO_CLARO, 0.75f) else m(ACERO_LUZ, 0.95f), Offset(x, top + 9f * u), Offset(x, pie - 2.6f * u), 1.6f * u)
            drawLine(Color.White.copy(alpha = if (oscuro) 0.15f else 0.45f), Offset(x - 0.4f * u, top + 10f * u), Offset(x - 0.4f * u, pie - 4f * u), 0.4f * u)
            drawLine(ml(LATON, 0.9f), Offset(x - 3.4f * u, top + 8.5f * u), Offset(x + 3.4f * u, top + 8.5f * u), 1.2f * u, StrokeCap.Round)
            drawRect(m(CARMESI_OSC, if (oscuro) 0.8f else 0.85f), Offset(x - 0.7f * u, top + 2f * u), Size(1.4f * u, 6.5f * u))
            drawCircle(ml(LATON, 0.9f), 1.2f * u, Offset(x, top + 1.6f * u))
        }
        // La repisa de acero con su filete de latón, y el zócalo remachado.
        drawRect(m(ACERO, if (oscuro) 0.75f else 0.6f), Offset(0f, estante), Size(w, 3f * u))
        drawLine(ml(LATON, 0.85f), Offset(0f, estante + 0.3f * u), Offset(w, estante + 0.3f * u), 0.6f * u)
        drawRect(m(ACERO_OSC, if (oscuro) 0.8f else 0.55f), Offset(0f, estante + 3f * u), Size(w, h - estante - 3f * u))
        var rx = 5f * u; var j = 0
        while (rx < w && j < 80) { remache(Offset(rx, (estante + 3f * u + h) / 2), 0.7f * u, m(ACERO_LUZ, if (oscuro) 0.7f else 0.8f), Color.White.copy(alpha = if (oscuro) 0.12f else 0.35f)); rx += 10f * u; j++ }
    }

    // ---------------------------------------------------------------- Íconos de los mapas

    override fun DrawScope.iconoMapa(id: String, e: EstadoIcono, oscuro: Boolean) {
        val w = size.width; val h = size.height; val c = Offset(w / 2, h / 2); val r = minOf(w, h) * 0.4f
        val g = 1.6f * density
        when (id) {
            "unesco" -> {
                // La ciudadela: dos torres almenadas, las murallas, la torre del homenaje en punta y la puerta con el engrane.
                val suelo = c.y + r * 0.86f
                drawLine(e.c2, Offset(c.x - r * 1.45f, suelo), Offset(c.x + r * 1.45f, suelo), g * 1.3f, StrokeCap.Round)
                val silueta = Path().apply {
                    addRect(Rect(c.x - r * 1.05f, c.y + r * 0.05f, c.x + r * 1.05f, suelo))
                    listOf(-1f, 1f).forEach { s -> addRect(Rect(c.x + s * r * 1.18f - r * 0.18f, c.y - r * 0.35f, c.x + s * r * 1.18f + r * 0.18f, suelo)) }
                    addRect(Rect(c.x - r * 0.34f, c.y - r * 0.55f, c.x + r * 0.34f, suelo))
                    moveTo(c.x - r * 0.42f, c.y - r * 0.55f); lineTo(c.x, c.y - r * 1.02f); lineTo(c.x + r * 0.42f, c.y - r * 0.55f); close()
                }
                drawPath(silueta, e.c1)
                // Las almenas de las torres y de la muralla.
                listOf(-1f, 1f).forEach { s ->
                    val x0 = c.x + s * r * 1.18f
                    for (k in -1..1) drawRect(e.c1, Offset(x0 + k * r * 0.13f - r * 0.05f, c.y - r * 0.47f), Size(r * 0.1f, r * 0.12f))
                    drawRect(e.fondo, Offset(x0 - r * 0.04f, c.y - r * 0.15f), Size(r * 0.08f, r * 0.2f))
                }
                for (k in 0 until 3) listOf(-1f, 1f).forEach { s -> drawRect(e.c1, Offset(c.x + s * (r * 0.48f + k * r * 0.17f) - r * 0.05f, c.y - r * 0.06f), Size(r * 0.1f, r * 0.11f)) }
                drawPath(arcoMedioPunto(Rect(c.x - r * 0.18f, c.y + r * 0.3f, c.x + r * 0.18f, suelo)), e.fondo)
                drawPath(engrane(Offset(c.x, c.y - r * 0.12f), r * 0.17f, 8), e.c2)
                drawCircle(e.c1, r * 0.06f, Offset(c.x, c.y - r * 0.12f))
            }
            "rascar" -> {
                // La placa de acero con su capa para rascar; donde ya raspó la cuchilla asoma el engrane de la Orden.
                val placa = Rect(c.x - r * 1.4f, c.y - r * 0.75f, c.x + r * 0.95f, c.y + r * 0.8f)
                val forma = ochavado(placa, r * 0.22f)
                drawPath(forma, e.c2.copy(alpha = 0.6f))
                val raspado = Path().apply {
                    moveTo(placa.left + placa.width * 0.06f, placa.top + placa.height * 0.2f)
                    lineTo(placa.left + placa.width * 0.55f, placa.top + placa.height * 0.08f)
                    lineTo(placa.left + placa.width * 0.75f, placa.top + placa.height * 0.5f)
                    lineTo(placa.left + placa.width * 0.5f, placa.bottom - placa.height * 0.08f)
                    lineTo(placa.left + placa.width * 0.08f, placa.bottom - placa.height * 0.2f)
                    close()
                }
                drawPath(raspado, e.fondo)
                clipPath(raspado) {
                    val ec = Offset(placa.left + placa.width * 0.38f, placa.center.y)
                    drawPath(engrane(ec, r * 0.42f, 10), e.c1)
                    drawCircle(e.fondo, r * 0.16f, ec)
                }
                drawPath(forma, e.c2, style = Stroke(g))
                for (q in listOf(Offset(placa.left + r * 0.2f, placa.top + r * 0.2f), Offset(placa.right - r * 0.2f, placa.top + r * 0.2f), Offset(placa.left + r * 0.2f, placa.bottom - r * 0.2f), Offset(placa.right - r * 0.2f, placa.bottom - r * 0.2f)))
                    drawCircle(e.c2, r * 0.06f, q)
                // La cuchilla, inclinada: la hoja de filo recto y el mango carmesí.
                val s = Offset(c.x + r * 0.95f, c.y)
                rotate(-35f, s) {
                    val hoja = Path().apply { moveTo(s.x - r * 0.75f, s.y + r * 0.08f); lineTo(s.x - r * 0.55f, s.y - r * 0.14f); lineTo(s.x, s.y - r * 0.14f); lineTo(s.x, s.y + r * 0.08f); close() }
                    drawPath(hoja, e.tinta.copy(alpha = 0.55f))
                    drawLine(e.fondo, Offset(s.x - r * 0.7f, s.y + r * 0.06f), Offset(s.x, s.y + r * 0.06f), g * 0.5f)
                    drawRoundRect(e.c1, Offset(s.x, s.y - r * 0.16f), Size(r * 0.62f, r * 0.26f), CornerRadius(r * 0.06f))
                    drawCircle(e.c2, r * 0.05f, Offset(s.x + r * 0.45f, s.y - r * 0.03f))
                }
            }
            else -> {
                // Tus lugares: tres insignias hexagonales con su aro de latón y el galón, clavadas con su pasador.
                val pins = listOf(Offset(c.x - r * 0.8f, c.y + r * 0.2f), Offset(c.x + r * 0.1f, c.y - r * 0.3f), Offset(c.x + r * 0.9f, c.y + r * 0.3f))
                pins.forEachIndexed { k, p ->
                    val rr = r * (if (k == 1) 0.36f else 0.3f)
                    drawLine(e.c2, p, Offset(p.x, p.y + rr * 1.5f), g * 0.7f, StrokeCap.Round)
                    drawPath(poligono(p, rr, 6), e.c1)
                    drawPath(poligono(p, rr * 0.8f, 6), e.c2, style = Stroke(g * 0.6f))
                    drawPath(Path().apply { moveTo(p.x - rr * 0.42f, p.y - rr * 0.12f); lineTo(p.x, p.y + rr * 0.28f); lineTo(p.x + rr * 0.42f, p.y - rr * 0.12f) }, e.fondo, style = Stroke(g * 0.8f))
                }
            }
        }
    }

    // ---------------------------------------------------------------- Notas

    override fun DrawScope.tableroNotas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        fun m(c: Color, k: Float) = mezcla(e.fondo, t(c, oscuro), k)
        fun ml(c: Color, k: Float) = mezcla(e.fondo, tl(c, oscuro), k)
        // El panel de placas de blindaje: el acero, las juntas en cruz y los remaches.
        val panel = ochavado(Rect(0f, 0f, w, h), 4f * u)
        drawPath(panel, m(ACERO, if (oscuro) 0.6f else 0.68f))
        val junta = Path().apply { moveTo(w / 2, 0f); lineTo(w / 2, h); moveTo(0f, h / 2); lineTo(w, h / 2) }
        drawPath(junta, Color.Black.copy(alpha = if (oscuro) 0.4f else 0.25f), style = Stroke(0.6f * u))
        drawPath(Path().apply { moveTo(w / 2 + 0.6f * u, 0f); lineTo(w / 2 + 0.6f * u, h); moveTo(0f, h / 2 + 0.6f * u); lineTo(w, h / 2 + 0.6f * u) }, Color.White.copy(alpha = if (oscuro) 0.05f else 0.15f), style = Stroke(0.4f * u))
        val az = Random(e.semilla.toLong())
        repeat(6) { k ->
            val y = h * (0.1f + 0.8f * az.nextFloat())
            drawLine(Color.White.copy(alpha = if (oscuro) 0.03f else 0.07f), Offset(4f * u, y), Offset(w - 4f * u, y + (k - 3) * 0.3f * u), 0.4f * u)
        }
        var x = 5f * u; var k = 0
        while (x < w - 3f * u && k < 80) {
            listOf(2.4f * u, h / 2 - 2.2f * u, h / 2 + 2.6f * u, h - 2.4f * u).forEach { y -> remache(Offset(x, y), 0.6f * u, m(ACERO_LUZ, if (oscuro) 0.75f else 0.9f), Color.White.copy(alpha = if (oscuro) 0.12f else 0.35f)) }
            x += 9f * u; k++
        }
        // La orla de latón.
        drawPath(ochavado(Rect(1f * u, 1f * u, w - 1f * u, h - 1f * u), 3.4f * u), ml(LATON, if (oscuro) 0.75f else 0.85f), style = Stroke(0.7f * u))
        // El escudo de la Orden en el margen de arriba: el acero, la orla de latón y el engrane.
        val ec = Offset(w / 2, 4f * u); val ea = 6f * u; val eh = 7f * u
        val escudo = Path().apply {
            moveTo(ec.x - ea / 2, ec.y - eh / 2); lineTo(ec.x + ea / 2, ec.y - eh / 2); lineTo(ec.x + ea / 2, ec.y)
            quadraticTo(ec.x + ea / 2, ec.y + eh * 0.35f, ec.x, ec.y + eh / 2); quadraticTo(ec.x - ea / 2, ec.y + eh * 0.35f, ec.x - ea / 2, ec.y); close()
        }
        drawPath(escudo, m(CARMESI, if (oscuro) 0.8f else 0.9f))
        drawPath(escudo, ml(LATON, 0.9f), style = Stroke(0.5f * u))
        drawPath(engrane(Offset(ec.x, ec.y - 0.3f * u), 1.8f * u, 8), ml(LATON_LUZ, 0.95f))
        // La franja de peligro al pie.
        clipPath(panel) { franjasPeligro(Rect(0f, h - 1.6f * u, w, h), m(AMARILLO, if (oscuro) 0.6f else 0.8f), m(NEGRO, 0.85f)) }
    }

    override fun DrawScope.notaTablero(e: EstadoNota, oscuro: Boolean) {
        val r = e.rect; val u = minOf(r.width, r.height) / 100f
        val placa = if (oscuro) PLACA_NOCHE else PLACA
        val forma = ochavado(r, 8f * u)
        // La sombra, la placa y su bisel.
        drawPath(ochavado(Rect(r.left + 1.6f * u, r.top + 2.2f * u, r.right + 1.6f * u, r.bottom + 2.2f * u), 8f * u), Color.Black.copy(alpha = if (oscuro) 0.5f else 0.3f))
        drawPath(forma, placa)
        clipPath(forma) {
            // El cepillado del acero, muy tenue.
            for (k in 1..5) drawLine((if (oscuro) Color.White else Color.Black).copy(alpha = 0.03f), Offset(r.left, r.top + r.height * k / 6f), Offset(r.right, r.top + r.height * k / 6f + 0.6f * u), 0.6f * u)
            drawLine(Color.White.copy(alpha = if (oscuro) 0.12f else 0.7f), Offset(r.left, r.top + 0.6f * u), Offset(r.right, r.top + 0.6f * u), 1.2f * u)
            drawLine(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.18f), Offset(r.left, r.bottom - 0.6f * u), Offset(r.right, r.bottom - 0.6f * u), 1.2f * u)
            // La franja de su tipo en el margen izquierdo.
            val franja = Rect(r.left + r.width * 0.035f, r.top + r.height * 0.18f, r.left + r.width * 0.075f, r.bottom - r.height * 0.18f)
            when (e.tipo) {
                "idea" -> drawRect(e.acento, franja.topLeft, franja.size)
                "pensamiento" -> drawRect(t(ACERO_LUZ, oscuro), franja.topLeft, franja.size)
                "proposito" -> drawRect(tl(LATON, oscuro), franja.topLeft, franja.size)
                "recordatorio" -> {
                    drawRect(AMARILLO, franja.topLeft, franja.size)
                    var y = franja.top; var k = 0
                    while (y < franja.bottom && k < 30) {
                        drawPath(Path().apply { moveTo(franja.left, y + franja.width); lineTo(franja.right, y); lineTo(franja.right, y + franja.width * 0.8f); lineTo(franja.left, y + franja.width * 1.8f); close() }, NEGRO)
                        y += franja.width * 2f; k++
                    }
                }
                else -> {}
            }
        }
        // El canto y los cuatro remaches de las esquinas.
        drawPath(forma, if (e.fijada) tl(LATON, oscuro) else (if (oscuro) Color.Black.copy(alpha = 0.5f) else ACERO_LUZ.copy(alpha = 0.6f)), style = Stroke(if (e.fijada) 1.8f * u else 0.8f * u))
        listOf(Offset(r.left + 6f * u, r.top + 6f * u), Offset(r.right - 6f * u, r.top + 6f * u), Offset(r.left + 6f * u, r.bottom - 6f * u), Offset(r.right - 6f * u, r.bottom - 6f * u)).forEach { p ->
            remache(p, 2.2f * u, if (oscuro) ACERO_LUZ else ACERO_CLARO, Color.White.copy(alpha = if (oscuro) 0.3f else 0.8f))
            drawCircle(Color.Black.copy(alpha = 0.3f), 2.2f * u, p, style = Stroke(0.5f * u))
        }
        // Fijada: el engrane de latón de la Orden en la esquina de arriba.
        if (e.fijada) {
            val gc = Offset(r.right - 16f * u, r.top + 7f * u)
            drawPath(engrane(gc, 5f * u, 8), tl(LATON, oscuro))
            drawCircle(placa, 1.8f * u, gc)
        }
    }

    override fun tintaNota(oscuro: Boolean): Color = if (oscuro) Color(0xFFDDE2E7) else Color(0xFF15181B)
}
