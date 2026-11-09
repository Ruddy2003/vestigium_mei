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
import io.github.ruddy2003.vestigium.ui.Vapor
import io.github.ruddy2003.vestigium.ui.ideario.arcoMedioPunto
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.remacheVapor
import java.util.Random
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Vapor victoriano: Vida en el ferrocarril y en el gabinete de patentes, con el latón, el hierro, la caoba, el cardenillo, el
 * ladrillo y la luz del gas de alumbrado de su salón de logros y su Ideario.
 * - Social: el salón de un vagón. La mesa de caoba con sus tazas, los asientos de terciopelo a los lados, las ventanillas de
 *   arco con su cortina, las lámparas de gas (encendidas de noche) y el portaequipajes de latón arriba.
 * - Mapas: la mesa del cartógrafo. El tablero de caoba con su moldura de latón, el plano azul de patente con su cuadrícula
 *   y sus trazos de construcción, el engrane pisapapeles, el tintero con la pluma y el compás de puntas de latón.
 * - Otras vidas: la vitrina del gabinete de curiosidades. Una urna de arco con marco de latón por medalla, con su fondo de
 *   terciopelo verde y el brillo del vidrio, engranes en las enjutas y la cajonera de caoba abajo.
 * - Íconos: la estación de hierro y vidrio con su reloj, la lima que raspa el plano, los remaches unidos por la vía.
 * - Notas: el tablero de horarios de la estación, verde, con su reloj y sus rayas; las notas son billetes de cartón y
 *   telegramas clavados con tachuelas de latón.
 */
internal object VidaVapor : DisenoVida {
    private val HIERRO = Color(0xFF2E3230)
    private val HIERRO_LUZ = Color(0xFF59605C)
    private val LATON = Color(0xFFC9A04E)
    private val LATON_LUZ = Color(0xFFE6C77E)
    private val LATON_OSC = Color(0xFF8A6A2C)
    private val CAOBA = Color(0xFF5A2A1A)
    private val CAOBA_OSC = Color(0xFF2E140C)
    private val CARA = Color(0xFFEDE3C8)
    private val ROJO = Color(0xFF9A2A22)
    private val TERCIOPELO = Color(0xFF7A2420)
    private val GAS = Color(0xFFF2C46A)
    private val CIELO = Color(0xFFBFD3D6)
    private val CIELO_NOCHE = Color(0xFF1C2638)
    private val PLANO = Color(0xFF2C4F7C)
    private val CARDENILLO = Color(0xFF2F6B5E)
    private val VERDE_TABLERO = Color(0xFF2F4A3A)
    private val VERDE_FELPA = Color(0xFF2F5A4C)

    /** El cartón de los billetes y el papel de los telegramas; de noche, a la luz del gas. */
    private val CARTON = Color(0xFFF1E6CC)
    private val CARTON_NOCHE = Color(0xFF2E2820)
    private val TELEGRAMA = Color(0xFFF2E2B0)
    private val TELEGRAMA_NOCHE = Color(0xFF33291C)

    private fun DrawScope.u() = size.minDimension / 100f

    /** El color en modo oscuro: más hundido, como en su salón de logros. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0D0B09), 0.36f) else c

    /** Los centros de las cinco medallas de Otras vidas, como los reparte la fila de la pantalla (10 dp de margen, ≈ 50 dp cada una). */
    private fun DrawScope.centrosMedallas(): List<Float> {
        val pad = 10f * density; val iw = 50f * density
        val gap = ((size.width - 2 * pad - 5 * iw) / 6f).coerceAtLeast(0f)
        return List(5) { k -> pad + gap * (k + 1) + iw * (k + 0.5f) }
    }

    /** Una lámpara de gas de pared: la placa, el brazo, el globo de vidrio y, encendida, su halo en capas planas. */
    private fun DrawScope.lamparaGas(c: Offset, u: Float, laton: Color, globo: Color, halo: Color?, lado: Float) {
        if (halo != null) for (k in 0 until 3) drawCircle(halo, (13f - k * 4f) * u, c)
        val pared = Offset(c.x - lado * 4f * u, c.y + 3f * u)
        drawRoundRect(laton, Offset(pared.x - 0.8f * u, pared.y - 2f * u), Size(1.6f * u, 4f * u), CornerRadius(0.8f * u))
        drawPath(Path().apply { moveTo(pared.x, pared.y); quadraticTo(c.x - lado * 1f * u, pared.y + 1f * u, c.x, c.y + 2.4f * u) }, laton, style = Stroke(0.6f * u))
        drawRect(laton, Offset(c.x - 1.2f * u, c.y + 1.6f * u), Size(2.4f * u, 1f * u))
        drawCircle(globo, 2.4f * u, c)
        drawCircle(laton, 2.4f * u, c, style = Stroke(0.35f * u))
        drawRect(laton, Offset(c.x - 0.6f * u, c.y - 3.4f * u), Size(1.2f * u, 1f * u))
    }

    // ---------------------------------------------------------------- Social

    override fun DrawScope.muebleSocial(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        fun m(c: Color, k: Float) = mezcla(e.fondo, t(c, oscuro), k)
        val laton = m(LATON, if (oscuro) 0.8f else 0.85f)
        // Los paneles de caoba del vagón, tenues, con sus juntas y la moldura de latón a media altura.
        drawRect(m(CAOBA, if (oscuro) 0.2f else 0.1f), Offset.Zero, size)
        val juntas = Path()
        var x = 28f * u; var k = 0
        while (x < w - 20f * u && k < 40) { if (x > 22f * u) { juntas.moveTo(x, 5f * u); juntas.lineTo(x, h) }; x += 28f * u; k++ }
        drawPath(juntas, m(CAOBA_OSC, if (oscuro) 0.4f else 0.16f), style = Stroke(0.5f * u))
        drawLine(m(LATON, if (oscuro) 0.3f else 0.3f), Offset(0f, h * 0.62f), Offset(w, h * 0.62f), 0.5f * u)

        // El techo del vagón y el portaequipajes de latón con sus soportes.
        drawRect(m(CAOBA, if (oscuro) 0.75f else 0.7f), Offset.Zero, Size(w, 4f * u))
        drawLine(laton, Offset(0f, 4f * u), Offset(w, 4f * u), 0.6f * u)
        drawLine(laton, Offset(w * 0.08f, 7f * u), Offset(w * 0.92f, 7f * u), 0.7f * u, StrokeCap.Round)
        var xs = w * 0.08f; k = 0
        while (xs <= w * 0.92f + 0.5f && k < 60) { drawLine(laton, Offset(xs, 4f * u), Offset(xs, 7f * u), 0.4f * u); xs += 9f * u; k++ }

        // Las ventanillas de arco a cada lado, con el cielo (de noche, azul con estrellas), las rayas de la velocidad, el
        // marco de latón, el travesaño y la cortina recogida.
        listOf(false, true).forEach { derecha ->
            val x0 = if (derecha) w - 18f * u else 3f * u
            val v = Rect(x0, 9f * u, x0 + 15f * u, 48f * u)
            val arco = arcoMedioPunto(v)
            drawPath(arco, if (oscuro) mezcla(e.fondo, CIELO_NOCHE, 0.9f) else mezcla(e.fondo, CIELO, 0.75f))
            clipPath(arco) {
                if (oscuro) for (j in 0 until 5) drawCircle(Color.White.copy(alpha = 0.5f), 0.35f * u, Offset(v.left + v.width * ((j * 37 % 10) / 10f), v.top + v.height * ((j * 23 % 10) / 14f)))
                for (j in 0 until 3) {
                    val ly = v.top + v.height * (0.45f + j * 0.12f)
                    drawLine(Color.White.copy(alpha = if (oscuro) 0.12f else 0.5f), Offset(v.left + v.width * (0.1f + 0.2f * j), ly), Offset(v.left + v.width * (0.6f + 0.15f * j), ly), 0.4f * u, StrokeCap.Round)
                }
                // Las colinas que pasan.
                drawOval(m(CARDENILLO, if (oscuro) 0.5f else 0.45f), Offset(v.left - v.width * 0.3f, v.bottom - v.height * 0.12f), Size(v.width * 1.1f, v.height * 0.3f))
            }
            drawPath(arco, laton, style = Stroke(1f * u))
            drawLine(laton, Offset(v.left, v.top + v.height * 0.62f), Offset(v.right, v.top + v.height * 0.62f), 0.6f * u)
            val xc = if (derecha) v.right - 0.6f * u else v.left + 0.6f * u
            val cortina = Path().apply {
                moveTo(xc - (if (derecha) 3.4f else -3.4f) * u, v.top + 2f * u); lineTo(xc, v.top + 2f * u)
                lineTo(xc, v.bottom); quadraticTo(xc - (if (derecha) 1.5f else -1.5f) * u, v.top + v.height * 0.55f, xc - (if (derecha) 3.4f else -3.4f) * u, v.top + 2f * u); close()
            }
            drawPath(cortina, m(TERCIOPELO, if (oscuro) 0.8f else 0.85f))
            drawCircle(laton, 0.7f * u, Offset(xc - (if (derecha) 1.6f else -1.6f) * u, v.top + v.height * 0.58f))

            // La lámpara de gas bajo la ventanilla (encendida de noche).
            val lc = Offset(v.center.x, 56f * u)
            lamparaGas(lc, u, laton, if (oscuro) mezcla(e.fondo, GAS, 0.9f) else m(Color(0xFFF2EEE4), 0.9f), if (oscuro) GAS.copy(alpha = 0.06f) else GAS.copy(alpha = 0.08f), if (derecha) -1f else 1f)

            // El asiento de terciopelo, de lado, con su respaldo contra la pared y su base de caoba.
            val pared = if (derecha) w - 1f * u else 1f * u
            val dentro = if (derecha) -1f else 1f
            val resp = Rect(minOf(pared, pared + dentro * 3.5f * u), h * 0.66f, maxOf(pared, pared + dentro * 3.5f * u), h - 9f * u)
            drawRoundRect(m(TERCIOPELO, if (oscuro) 0.75f else 0.8f), resp.topLeft, resp.size, CornerRadius(1.2f * u))
            val asiento = Rect(minOf(pared, pared + dentro * 18f * u), h - 15f * u, maxOf(pared, pared + dentro * 18f * u), h - 10f * u)
            drawRoundRect(m(TERCIOPELO, if (oscuro) 0.75f else 0.8f), asiento.topLeft, asiento.size, CornerRadius(1.6f * u))
            for (j in 0 until 3) drawCircle(m(CAOBA_OSC, 0.6f), 0.35f * u, Offset(asiento.left + asiento.width * (0.3f + 0.25f * j), asiento.top + 1.4f * u))
            val base = Rect(minOf(pared + dentro * 3f * u, pared + dentro * 17f * u), asiento.bottom, maxOf(pared + dentro * 3f * u, pared + dentro * 17f * u), h - 3f * u)
            drawRect(m(CAOBA, if (oscuro) 0.75f else 0.8f), base.topLeft, base.size)
            drawLine(laton, Offset(base.left, base.top + 0.4f * u), Offset(base.right, base.top + 0.4f * u), 0.4f * u)
        }

        // La mesa de caoba con su canto de latón, sus dos patas torneadas y las dos tazas.
        val ym = h - 9f * u
        listOf(0.26f, 0.74f).forEach { f ->
            val lx = w * f
            drawRect(m(CAOBA, if (oscuro) 0.8f else 0.8f), Offset(lx - 0.8f * u, ym + 2.4f * u), Size(1.6f * u, h - 3f * u - ym - 2.4f * u))
            drawOval(m(CAOBA, 0.85f), Offset(lx - 1.6f * u, ym + 4.4f * u), Size(3.2f * u, 1.6f * u))
            drawRect(laton, Offset(lx - 1.4f * u, h - 4f * u), Size(2.8f * u, 1f * u))
        }
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.1f), Offset(w * 0.18f, ym + 2.4f * u), Size(w * 0.64f, 0.8f * u))
        drawRect(m(CAOBA, if (oscuro) 0.85f else 0.85f), Offset(w * 0.16f, ym), Size(w * 0.68f, 2.4f * u))
        drawLine(laton, Offset(w * 0.16f, ym + 2.2f * u), Offset(w * 0.84f, ym + 2.2f * u), 0.5f * u)
        drawLine(Color.White.copy(alpha = if (oscuro) 0.06f else 0.2f), Offset(w * 0.16f, ym + 0.3f * u), Offset(w * 0.84f, ym + 0.3f * u), 0.4f * u)
        listOf(0.4f, 0.6f).forEach { f ->
            val cx = w * f
            drawLine(m(CARA, if (oscuro) 0.6f else 0.95f), Offset(cx - 2.2f * u, ym - 0.3f * u), Offset(cx + 2.2f * u, ym - 0.3f * u), 0.6f * u, StrokeCap.Round)
            drawPath(Path().apply { moveTo(cx - 1.4f * u, ym - 2.6f * u); lineTo(cx + 1.4f * u, ym - 2.6f * u); lineTo(cx + 1f * u, ym - 0.6f * u); lineTo(cx - 1f * u, ym - 0.6f * u); close() }, m(CARA, if (oscuro) 0.6f else 0.95f))
            drawLine(m(ROJO, 0.8f), Offset(cx - 1.4f * u, ym - 2.4f * u), Offset(cx + 1.4f * u, ym - 2.4f * u), 0.3f * u)
            drawArc(m(CARA, if (oscuro) 0.6f else 0.95f), -90f, 180f, false, Offset(cx + 0.9f * u, ym - 2.3f * u), Size(1.4f * u, 1.4f * u), style = Stroke(0.35f * u))
        }
        // La alfombra del pasillo, con su punteado de latón.
        drawRect(m(ROJO, if (oscuro) 0.5f else 0.45f), Offset(0f, h - 3f * u), Size(w, 3f * u))
        val puntos = Path()
        var px = 2f * u; k = 0
        while (px < w && k < 200) { puntos.addOval(Rect(px - 0.3f * u, h - 1.8f * u, px + 0.3f * u, h - 1.2f * u)); px += 5f * u; k++ }
        drawPath(puntos, m(LATON, if (oscuro) 0.5f else 0.6f))
    }

    // ---------------------------------------------------------------- Mapas

    override fun DrawScope.muebleMapas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        fun m(c: Color, k: Float) = mezcla(e.fondo, t(c, oscuro), k)
        val laton = m(LATON, if (oscuro) 0.85f else 0.9f)
        // Las patas torneadas que asoman al pie, con sus regatones de latón.
        listOf(0.07f, 0.93f).forEach { f ->
            drawRect(m(CAOBA, 0.85f), Offset(w * f - 1.2f * u, h - 4f * u), Size(2.4f * u, 4f * u))
            drawRect(laton, Offset(w * f - 1.5f * u, h - 1.2f * u), Size(3f * u, 1.2f * u))
        }
        // El tablero de caoba, con sus vetas y la moldura de latón.
        val mesa = Rect(1f * u, 1f * u, w - 1f * u, h - 4f * u)
        drawRoundRect(m(CAOBA, if (oscuro) 0.7f else 0.75f), mesa.topLeft, mesa.size, CornerRadius(2f * u))
        val az = Random(e.semilla.toLong())
        val vetas = Path()
        repeat(9) {
            val y0 = mesa.top + mesa.height * az.nextFloat()
            vetas.moveTo(mesa.left, y0); vetas.cubicTo(w * 0.3f, y0 - 2f * u, w * 0.7f, y0 + 2f * u, mesa.right, y0 + (az.nextFloat() - 0.5f) * 3f * u)
        }
        drawPath(vetas, m(CAOBA_OSC, if (oscuro) 0.5f else 0.45f), style = Stroke(0.4f * u))
        drawRoundRect(laton, Offset(mesa.left + 2.4f * u, mesa.top + 2.2f * u), Size(mesa.width - 4.8f * u, mesa.height - 4.4f * u), CornerRadius(1f * u), style = Stroke(0.5f * u))
        // El plano azul de patente: el papel tenue, la cuadrícula y los trazos de construcción en las esquinas.
        val plano = Rect(4.5f * u, 4f * u, w - 4.5f * u, h - 7.5f * u)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.12f), Offset(plano.left + 0.6f * u, plano.top + 0.8f * u), plano.size)
        drawRect(if (oscuro) mezcla(e.fondo, PLANO, 0.4f) else mezcla(e.fondo, PLANO, 0.12f), plano.topLeft, plano.size)
        val linea = if (oscuro) Color.White.copy(alpha = 0.07f) else mezcla(e.fondo, PLANO, 0.22f)
        val trazo = if (oscuro) Color.White.copy(alpha = 0.2f) else mezcla(e.fondo, PLANO, 0.38f)
        clipRect(plano.left, plano.top, plano.right, plano.bottom) {
            val rejilla = Path()
            var x = plano.left + 6f * u; var k = 0
            while (x < plano.right && k < 100) { rejilla.moveTo(x, plano.top); rejilla.lineTo(x, plano.bottom); x += 6f * u; k++ }
            var y = plano.top + 6f * u; k = 0
            while (y < plano.bottom && k < 40) { rejilla.moveTo(plano.left, y); rejilla.lineTo(plano.right, y); y += 6f * u; k++ }
            drawPath(rejilla, linea, style = Stroke(0.3f * u))
            // El engrane de la patente, abajo a la izquierda, y el círculo con su cruz, arriba a la derecha.
            val ge = Offset(plano.left + 7f * u, plano.bottom - 6f * u)
            drawPath(Vapor.engrane(ge, 15f * u, 14, 0.84f), trazo, style = Stroke(0.5f * u))
            drawCircle(trazo, 6f * u, ge, style = Stroke(0.4f * u))
            val cc = Offset(plano.right - 9f * u, plano.top + 8f * u)
            drawCircle(trazo, 11f * u, cc, style = Stroke(0.4f * u))
            drawLine(trazo, Offset(cc.x - 14f * u, cc.y), Offset(cc.x + 14f * u, cc.y), 0.3f * u)
            drawLine(trazo, Offset(cc.x, cc.y - 14f * u), Offset(cc.x, cc.y + 14f * u), 0.3f * u)
        }
        drawRect(if (oscuro) Color.White.copy(alpha = 0.2f) else mezcla(e.fondo, PLANO, 0.5f), plano.topLeft, plano.size, style = Stroke(0.4f * u))
        // El engrane de latón que pisa el plano, arriba a la izquierda.
        val pg = Offset(plano.left + 5f * u, plano.top + 4.6f * u)
        drawCircle(Color.Black.copy(alpha = if (oscuro) 0.35f else 0.18f), 4.8f * u, Offset(pg.x + 0.6f * u, pg.y + 0.9f * u))
        drawPath(Vapor.engrane(pg, 4.6f * u, 10, 0.8f), laton)
        drawCircle(m(LATON_OSC, 0.9f), 1.8f * u, pg)
        remacheVapor(pg, 0.8f * u, m(LATON_LUZ, 0.95f))
        // El tintero de vidrio oscuro con su pluma, arriba a la derecha.
        val ti = Offset(plano.right - 5f * u, plano.top + 5f * u)
        drawRoundRect(m(HIERRO, if (oscuro) 0.85f else 0.9f), Offset(ti.x - 2.6f * u, ti.y - 1.6f * u), Size(5.2f * u, 4f * u), CornerRadius(0.8f * u))
        drawRect(laton, Offset(ti.x - 1.2f * u, ti.y - 2.6f * u), Size(2.4f * u, 1f * u))
        rotate(-30f, Offset(ti.x, ti.y - 2f * u)) {
            drawLine(m(HIERRO, 0.9f), Offset(ti.x, ti.y - 2f * u), Offset(ti.x, ti.y - 12f * u), 0.4f * u)
            drawOval(m(CARA, if (oscuro) 0.6f else 0.95f), Offset(ti.x - 1.2f * u, ti.y - 12f * u), Size(2.4f * u, 7f * u))
        }
        // El compás de puntas de latón, abierto, abajo a la derecha.
        val bis = Offset(w - 11f * u, h - 15f * u)
        val p1 = Offset(w - 27f * u, h - 5.5f * u); val p2 = Offset(w - 5f * u, h - 4.8f * u)
        listOf(p1, p2).forEach { p ->
            drawLine(Color.Black.copy(alpha = if (oscuro) 0.35f else 0.16f), Offset(bis.x + 0.5f * u, bis.y + 0.8f * u), Offset(p.x + 0.5f * u, p.y + 0.8f * u), 1f * u, StrokeCap.Round)
            drawLine(laton, bis, p, 0.9f * u, StrokeCap.Round)
            val dx = p.x - bis.x; val dy = p.y - bis.y; val l = hypot(dx, dy).coerceAtLeast(1f)
            drawLine(m(HIERRO, 0.9f), Offset(p.x - dx / l * 2f * u, p.y - dy / l * 2f * u), p, 0.5f * u, StrokeCap.Round)
        }
        drawCircle(laton, 1.6f * u, bis)
        remacheVapor(bis, 0.7f * u, m(LATON_OSC, 0.9f))
        drawLine(laton, Offset(bis.x, bis.y - 1.6f * u), Offset(bis.x, bis.y - 3.4f * u), 0.6f * u, StrokeCap.Round)
    }

    // ---------------------------------------------------------------- Otras vidas

    override fun DrawScope.muebleOtrasVidas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u(); val d = density
        fun m(c: Color, k: Float) = mezcla(e.fondo, t(c, oscuro), k)
        val laton = m(LATON, if (oscuro) 0.85f else 0.9f)
        val centros = centrosMedallas()
        val paso = (w - 20f * d) / 5f
        val my = h / 2 - 10f * d
        val estante = minOf(my + 46f * d, h - 11f * u)
        // El cuerpo de caoba de la vitrina, con unas vetas, y la cornisa con su moldura de latón y sus dentellones.
        drawRect(m(CAOBA, if (oscuro) 0.6f else 0.55f), Offset.Zero, size)
        val az = Random(e.semilla.toLong())
        val vetas = Path()
        repeat(6) { val y0 = h * az.nextFloat(); vetas.moveTo(0f, y0); vetas.cubicTo(w * 0.3f, y0 - 2f * u, w * 0.7f, y0 + 2f * u, w, y0) }
        drawPath(vetas, m(CAOBA_OSC, if (oscuro) 0.45f else 0.35f), style = Stroke(0.4f * u))
        drawRect(m(CAOBA_OSC, if (oscuro) 0.85f else 0.8f), Offset.Zero, Size(w, 4f * u))
        drawLine(laton, Offset(0f, 4f * u), Offset(w, 4f * u), 0.8f * u)
        val dientes = Path()
        var dx = 2f * u; var k = 0
        while (dx < w && k < 200) { dientes.addRect(Rect(dx, 1.2f * u, dx + 1.6f * u, 2.8f * u)); dx += 3.6f * u; k++ }
        drawPath(dientes, m(LATON, if (oscuro) 0.5f else 0.6f))
        // Las urnas de arco, una por medalla: el terciopelo verde (tenue, para que se lean la medalla y su año), el brillo del
        // vidrio y el marco de latón.
        val nw = minOf(paso * 0.82f, 58f * d)
        centros.forEach { cx ->
            val r = Rect(cx - nw / 2, 6.5f * u, cx + nw / 2, estante)
            val arco = arcoMedioPunto(r)
            drawPath(arco, mezcla(e.fondo, t(VERDE_FELPA, oscuro), if (oscuro) 0.35f else 0.14f))
            clipPath(arco) {
                drawRect(Color.Black.copy(alpha = if (oscuro) 0.25f else 0.08f), r.topLeft, Size(r.width, 3f * u))
                drawLine(Color.White.copy(alpha = if (oscuro) 0.08f else 0.4f), Offset(r.left + nw * 0.12f, r.top + nw * 0.5f), Offset(r.left + nw * 0.42f, r.top + nw * 0.1f), 0.7f * u)
                drawLine(Color.White.copy(alpha = if (oscuro) 0.05f else 0.25f), Offset(r.left + nw * 0.12f, r.top + nw * 0.66f), Offset(r.left + nw * 0.52f, r.top + nw * 0.12f), 0.4f * u)
            }
            drawPath(arco, laton, style = Stroke(1.1f * u))
            drawPath(arco, m(LATON_OSC, 0.8f), style = Stroke(0.35f * u))
        }
        // Los engranes de latón en las enjutas, entre los arcos.
        for (j in 1 until 5) {
            val x = (centros[j - 1] + centros[j]) / 2
            drawPath(Vapor.engrane(Offset(x, 9f * u), 2.6f * u, 8, 0.8f), laton)
            drawCircle(m(CAOBA_OSC, 0.9f), 0.9f * u, Offset(x, 9f * u))
        }
        // El estante de latón y la cajonera de caoba, con sus tiradores, y el zócalo con las patas de garra.
        drawRect(laton, Offset(0f, estante), Size(w, 1.2f * u))
        val caj = Rect(0f, estante + 1.2f * u, w, h - 4f * u)
        drawRect(m(CAOBA, if (oscuro) 0.75f else 0.7f), caj.topLeft, caj.size)
        centros.forEach { cx ->
            if (caj.height > 3f * u) {
                drawRect(m(CAOBA_OSC, 0.6f), Offset(cx - paso * 0.42f, caj.top + 1f * u), Size(paso * 0.84f, caj.height - 2f * u), style = Stroke(0.4f * u))
                drawCircle(laton, 0.9f * u, Offset(cx, caj.center.y))
            }
        }
        drawRect(m(CAOBA_OSC, if (oscuro) 0.9f else 0.85f), Offset(0f, h - 4f * u), Size(w, 4f * u))
        drawLine(laton, Offset(0f, h - 4f * u), Offset(w, h - 4f * u), 0.4f * u)
        listOf(3f * u, w - 3f * u).forEach { x -> drawCircle(laton, 1.6f * u, Offset(x, h - 1.6f * u)) }
    }

    // ---------------------------------------------------------------- Íconos de los mapas

    override fun DrawScope.iconoMapa(id: String, e: EstadoIcono, oscuro: Boolean) {
        val w = size.width; val h = size.height; val c = Offset(w / 2, h / 2); val r = minOf(w, h) * 0.4f
        val g = 1.6f * density
        when (id) {
            "unesco" -> {
                // La estación de hierro y vidrio: la nave de medio punto con sus costillas, el reloj y el andén.
                val nave = Rect(c.x - r * 1.2f, c.y - r * 0.95f, c.x + r * 1.2f, c.y + r * 0.72f)
                val arco = arcoMedioPunto(nave)
                drawPath(arco, e.c1.copy(alpha = 0.2f))
                val centro = Offset(c.x, nave.top + nave.width / 2)
                val rIn = nave.width / 2 - r * 0.24f
                for (k in 1..7) {
                    val a = Math.PI + k * Math.PI / 8
                    drawLine(e.c1, Offset(centro.x + rIn * cos(a).toFloat(), centro.y + rIn * sin(a).toFloat()),
                        Offset(centro.x + nave.width / 2 * cos(a).toFloat(), centro.y + nave.width / 2 * sin(a).toFloat()), g * 0.6f)
                }
                listOf(-0.62f, -0.31f, 0.31f, 0.62f).forEach { f ->
                    val dx = rIn * f
                    val yTop = centro.y - sqrt((rIn * rIn - dx * dx).coerceAtLeast(0f))
                    drawLine(e.c1, Offset(c.x + dx, yTop), Offset(c.x + dx, nave.bottom), g * 0.5f)
                }
                drawPath(arcoMedioPunto(Rect(c.x - rIn, centro.y - rIn, c.x + rIn, nave.bottom)), e.c1, style = Stroke(g * 0.7f))
                drawPath(arco, e.c1, style = Stroke(g * 1.4f))
                val reloj = Offset(c.x, c.y - r * 0.25f)
                drawCircle(e.fondo, r * 0.27f, reloj)
                drawCircle(e.c2, r * 0.27f, reloj, style = Stroke(g))
                drawLine(e.c2, reloj, Offset(reloj.x, reloj.y - r * 0.18f), g * 0.7f, StrokeCap.Round)
                drawLine(e.c2, reloj, Offset(reloj.x + r * 0.13f, reloj.y + r * 0.05f), g * 0.7f, StrokeCap.Round)
                drawLine(e.c2, Offset(c.x - r * 1.45f, nave.bottom + g * 0.6f), Offset(c.x + r * 1.45f, nave.bottom + g * 0.6f), g * 1.4f, StrokeCap.Round)
            }
            "rascar" -> {
                // El plano de patente con su capa para rascar; donde ya pasó la lima asoma el engrane dibujado.
                val plano = Rect(c.x - r * 1.4f, c.y - r * 0.75f, c.x + r * 0.95f, c.y + r * 0.8f)
                drawRect(e.c2.copy(alpha = 0.6f), plano.topLeft, plano.size)
                val raspado = Path().apply {
                    moveTo(plano.left, plano.top + plano.height * 0.15f)
                    lineTo(plano.left + plano.width * 0.58f, plano.top + plano.height * 0.1f)
                    lineTo(plano.left + plano.width * 0.68f, plano.top + plano.height * 0.35f)
                    lineTo(plano.left + plano.width * 0.6f, plano.top + plano.height * 0.6f)
                    lineTo(plano.left + plano.width * 0.7f, plano.bottom - plano.height * 0.1f)
                    lineTo(plano.left, plano.bottom - plano.height * 0.1f)
                    close()
                }
                drawPath(raspado, e.fondo)
                clipPath(raspado) {
                    for (k in 1..5) { val x = plano.left + plano.width * k / 6; drawLine(e.c2.copy(alpha = 0.45f), Offset(x, plano.top), Offset(x, plano.bottom), g * 0.4f) }
                    for (k in 1..3) { val y = plano.top + plano.height * k / 4; drawLine(e.c2.copy(alpha = 0.45f), Offset(plano.left, y), Offset(plano.right, y), g * 0.4f) }
                    val ec = Offset(plano.left + plano.width * 0.32f, plano.center.y)
                    drawPath(Vapor.engrane(ec, r * 0.48f, 12, 0.82f), e.c1, style = Stroke(g))
                    drawCircle(e.c1, r * 0.16f, ec, style = Stroke(g * 0.8f))
                }
                drawRect(e.c2, plano.topLeft, plano.size, style = Stroke(g * 0.8f))
                // La lima: la hoja picada y el mango de madera con su virola.
                val s = Offset(c.x + r * 0.9f, c.y + r * 0.05f)
                rotate(-32f, s) {
                    drawRect(e.tinta.copy(alpha = 0.55f), Offset(s.x - r * 0.85f, s.y - r * 0.09f), Size(r * 0.85f, r * 0.18f))
                    for (k in 0 until 7) {
                        val x = s.x - r * 0.8f + k * r * 0.11f
                        drawLine(e.fondo.copy(alpha = 0.7f), Offset(x, s.y + r * 0.09f), Offset(x + r * 0.08f, s.y - r * 0.09f), g * 0.35f)
                    }
                    drawRect(e.c2, Offset(s.x, s.y - r * 0.12f), Size(r * 0.12f, r * 0.24f))
                    drawRoundRect(e.c1, Offset(s.x + r * 0.12f, s.y - r * 0.13f), Size(r * 0.5f, r * 0.26f), CornerRadius(r * 0.1f))
                }
            }
            else -> {
                // Tus lugares: tres remaches unidos por una vía de ferrocarril, con sus traviesas.
                val pts = listOf(Offset(c.x - r * 1.05f, c.y + r * 0.35f), Offset(c.x + r * 0.05f, c.y - r * 0.4f), Offset(c.x + r * 1.05f, c.y + r * 0.3f))
                for (i in 0 until 2) {
                    val a = pts[i]; val b = pts[i + 1]
                    val dx = b.x - a.x; val dy = b.y - a.y; val l = hypot(dx, dy).coerceAtLeast(1f)
                    val nx = -dy / l * r * 0.18f; val ny = dx / l * r * 0.18f
                    val n = (l / (r * 0.2f)).toInt()
                    for (k in 1 until n) {
                        val f = k / n.toFloat(); val p = Offset(a.x + dx * f, a.y + dy * f)
                        drawLine(e.c2.copy(alpha = 0.8f), Offset(p.x - nx, p.y - ny), Offset(p.x + nx, p.y + ny), g * 0.6f)
                    }
                }
                val via = Path().apply { moveTo(pts[0].x, pts[0].y); lineTo(pts[1].x, pts[1].y); lineTo(pts[2].x, pts[2].y) }
                drawPath(via, e.c2, style = Stroke(r * 0.22f))
                drawPath(via, e.fondo, style = Stroke(r * 0.22f - g * 1.4f))
                pts.forEachIndexed { k, p ->
                    val rr = r * (if (k == 1) 0.3f else 0.25f)
                    drawCircle(Color.Black.copy(alpha = 0.2f), rr, Offset(p.x + rr * 0.15f, p.y + rr * 0.2f))
                    remacheVapor(p, rr, e.c1)
                    drawCircle(Color.Black.copy(alpha = 0.25f), rr, p, style = Stroke(g * 0.5f))
                }
            }
        }
    }

    // ---------------------------------------------------------------- Notas

    override fun DrawScope.tableroNotas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        fun m(c: Color, k: Float) = mezcla(e.fondo, t(c, oscuro), k)
        val laton = m(LATON, if (oscuro) 0.85f else 0.9f)
        // El marco de caoba con su filete de latón.
        drawRoundRect(m(CAOBA, if (oscuro) 0.75f else 0.85f), Offset.Zero, size, CornerRadius(2f * u))
        val marco = 3f * u
        val tablero = Rect(marco, marco, w - marco, h - marco)
        // El tablero de horarios pintado de verde, con sus rayas y las columnas entre las notas.
        drawRect(m(VERDE_TABLERO, if (oscuro) 0.7f else 0.85f), tablero.topLeft, tablero.size)
        val rayas = Path()
        var y = tablero.top + 8f * u; var k = 0
        while (y < tablero.bottom && k < 40) { rayas.moveTo(tablero.left, y); rayas.lineTo(tablero.right, y); y += 8f * u; k++ }
        drawPath(rayas, CARA.copy(alpha = if (oscuro) 0.06f else 0.1f), style = Stroke(0.3f * u))
        val mx = w * 0.06f; val hueco = w * 0.025f
        val cw = (w - 2 * mx - 2 * hueco) / 3f
        listOf(mx + cw + hueco / 2, mx + 2 * cw + hueco * 1.5f).forEach { x -> drawLine(CARA.copy(alpha = if (oscuro) 0.12f else 0.2f), Offset(x, tablero.top), Offset(x, tablero.bottom), 0.4f * u) }
        drawRect(laton, tablero.topLeft, tablero.size, style = Stroke(0.6f * u))
        // El reloj de la estación en el marco de arriba.
        val rc = Offset(w / 2, 4.6f * u); val rr = 4.4f * u
        drawCircle(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.2f), rr, Offset(rc.x + 0.4f * u, rc.y + 0.6f * u))
        drawCircle(laton, rr, rc)
        drawCircle(if (oscuro) mezcla(e.fondo, CARA, 0.8f) else mezcla(e.fondo, CARA, 0.97f), rr * 0.78f, rc)
        for (j in 0 until 12) {
            val a = j * Math.PI / 6
            drawLine(m(HIERRO, 0.9f), Offset(rc.x + rr * 0.6f * cos(a).toFloat(), rc.y + rr * 0.6f * sin(a).toFloat()), Offset(rc.x + rr * 0.72f * cos(a).toFloat(), rc.y + rr * 0.72f * sin(a).toFloat()), if (j % 3 == 0) 0.4f * u else 0.2f * u)
        }
        drawLine(m(HIERRO, 0.95f), rc, Offset(rc.x - rr * 0.1f, rc.y - rr * 0.5f), 0.4f * u, StrokeCap.Round)
        drawLine(m(HIERRO, 0.95f), rc, Offset(rc.x + rr * 0.35f, rc.y + rr * 0.1f), 0.3f * u, StrokeCap.Round)
        drawCircle(m(ROJO, 0.9f), 0.4f * u, rc)
        // La placa de latón del marco de abajo, con sus remaches.
        val pl = Rect(w / 2 - 8f * u, h - 2.6f * u, w / 2 + 8f * u, h - 0.4f * u)
        drawRect(laton, pl.topLeft, pl.size)
        remacheVapor(Offset(pl.left + 1.2f * u, pl.center.y), 0.5f * u, m(LATON_OSC, 0.9f))
        remacheVapor(Offset(pl.right - 1.2f * u, pl.center.y), 0.5f * u, m(LATON_OSC, 0.9f))
    }

    override fun DrawScope.notaTablero(e: EstadoNota, oscuro: Boolean) {
        val r = e.rect; val u = minOf(r.width, r.height) / 100f
        val telegrama = e.tipo == "idea"
        val papel = if (telegrama) (if (oscuro) TELEGRAMA_NOCHE else TELEGRAMA) else (if (oscuro) CARTON_NOCHE else CARTON)
        // La sombra, el cartón y su filo.
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.3f), Offset(r.left + 1.6f * u, r.top + 2.2f * u), r.size)
        drawRect(papel, r.topLeft, r.size)
        when (e.tipo) {
            // El telegrama: las dos rayas del encabezado impreso.
            "idea" -> {
                drawLine(t(ROJO, oscuro).copy(alpha = 0.7f), Offset(r.left + r.width * 0.08f, r.top + r.height * 0.06f), Offset(r.right - r.width * 0.08f, r.top + r.height * 0.06f), 1f * u)
                drawLine(t(ROJO, oscuro).copy(alpha = 0.4f), Offset(r.left + r.width * 0.08f, r.top + r.height * 0.1f), Offset(r.right - r.width * 0.08f, r.top + r.height * 0.1f), 0.5f * u)
            }
            // El billete de cartón: la franja de color de su clase y la línea de puntos para cortar el talón.
            "pensamiento", "proposito" -> {
                val franja = if (e.tipo == "pensamiento") CARDENILLO else ROJO
                drawRect(t(franja, oscuro), r.topLeft, Size(r.width * 0.06f, r.height))
                for (j in 0 until 7) drawCircle((if (oscuro) CARA else HIERRO).copy(alpha = 0.3f), 0.8f * u, Offset(r.left + r.width * 0.09f, r.top + r.height * (0.1f + j * 0.13f)))
                // El agujero del revisor, por donde se ve el verde del tablero.
                if (e.tipo == "proposito") drawCircle(t(VERDE_TABLERO, oscuro), 3f * u, Offset(r.right - 7f * u, r.top + 7f * u))
            }
            // La etiqueta de equipaje: el ojal de latón y su cordel.
            "recordatorio" -> {
                val o = Offset(r.left + 7f * u, r.top + 7f * u)
                drawLine(t(CARA, oscuro), o, Offset(r.left - 3f * u, r.top - 4f * u), 0.6f * u, StrokeCap.Round)
                drawCircle(t(LATON, oscuro), 3f * u, o, style = Stroke(1.4f * u))
                drawCircle(t(VERDE_TABLERO, oscuro), 1.6f * u, o)
            }
            else -> drawRect((if (oscuro) CARA else HIERRO).copy(alpha = 0.2f), Offset(r.left + 3f * u, r.top + 3f * u), Size(r.width - 6f * u, r.height - 6f * u), style = Stroke(0.4f * u))
        }
        drawRect(if (e.fijada) e.acento else LATON_OSC.copy(alpha = if (oscuro) 0.5f else 0.4f), r.topLeft, r.size, style = Stroke(if (e.fijada) 1.6f * u else 0.6f * u))
        // Las tachuelas de latón: una arriba al centro; fijada, dos en las esquinas y una más grande.
        if (e.fijada) {
            listOf(Offset(r.left + 6f * u, r.top + 5f * u), Offset(r.right - 6f * u, r.top + 5f * u)).forEach { p -> remacheVapor(p, 3f * u, t(LATON, oscuro)) }
            remacheVapor(Offset(r.center.x, r.top + 5f * u), 4.6f * u, t(LATON_LUZ, oscuro))
        } else remacheVapor(Offset(r.center.x, r.top + 5f * u), 3.6f * u, t(LATON, oscuro))
    }

    override fun tintaNota(oscuro: Boolean): Color = if (oscuro) Color(0xFFEADCC0) else Color(0xFF231B14)
}
