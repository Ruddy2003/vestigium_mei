package io.github.ruddy2003.vestigium.ui.vida

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import java.util.Random
import kotlin.math.cos
import kotlin.math.sin

/**
 * Art déco: los muebles de Vida y de Diario de los años veinte, de la misma casa que su estante de transatlántico, su
 * casillero de laca de la recepción del hotel y su salón de baile de logros. Laca negra, oro, marfil, jade y cromo; abanicos de
 * rayos, remates escalonados, esquinas cortadas en chaflán, chevrones y las tres líneas de la velocidad.
 * - Social: el salón de un trasatlántico: el sol de rayos del techo, las pilastras de laca escalonadas a los lados con sus
 *   estrías de oro y su aplique de abanico (de noche, encendido), dos butacas de concha, la mesa redonda de laca sobre su pie
 *   escalonado y la cenefa de chevrones dorados al pie.
 * - Mapas: la sala de cartas del barco: el marco de laca de esquinas cortadas con su doble filete de oro y sus escalones en las
 *   esquinas, la carta marfil con las líneas de rumbo y la rosa de los vientos déco de ocho puntas, y las tres líneas de cromo.
 * - Otras vidas: las vitrinas escalonadas de un rascacielos: un nicho de remate escalonado por medalla (el del centro, más alto)
 *   con su filete de oro y un abanico arriba, entre pilastras de laca, sobre el zócalo de laca con las líneas de cromo.
 * - Íconos: el rascacielos con su corona de arcos escalonados y su aguja; la placa de laca raspada; tres abanicos como
 *   marcadores.
 * - Notas: el panel lacado de esquinas cortadas, con su filete de oro, sus estrías, el abanico arriba y los chevrones abajo;
 *   las notas son tarjetas de invitación marfil de esquinas cortadas con su doble ribete dorado, un adorno escalonado del
 *   color del tipo y, si están fijadas, un broche de abanico dorado.
 */
internal object VidaDeco : DisenoVida {
    private val LACA = Color(0xFF16171A)
    private val LACA_LUZ = Color(0xFF34363C)
    private val CREMA = Color(0xFFF0E6CF)
    private val JADE = Color(0xFF1F6B5C)
    private val CROMO = Color(0xFFC9CED3)
    private val CROMO_OSC = Color(0xFF7D858C)
    private val CORAL = Color(0xFFB5523B)
    private val LUZ = Color(0xFFFFE2A0)
    private val TINTA = Color(0xFF16130E)

    private fun DrawScope.u() = size.minDimension / 100f
    private fun mezcla(a: Color, b: Color, f: Float): Color = lerp(a, b, f.coerceIn(0f, 1f))

    /** El oro de los filetes: oro viejo de día (para que se vea sobre el marfil) y oro vivo de noche. */
    private fun oro(oscuro: Boolean): Color = if (oscuro) Color(0xFFD9B44A) else Color(0xFFA9842C)

    /** La laca negra sobre la tarjeta: de día, negra; de noche, apenas más clara que el fondo para que se distinga. */
    private fun laca(e: EstadoMueble, oscuro: Boolean): Color = if (oscuro) mezcla(e.fondo, LACA_LUZ, 0.45f) else LACA

    // ---------------------------------------------------------------- piezas

    /** Un abanico de rayos: medio sol abierto hacia arriba (o hacia abajo, con [abajo]), con sus gajos alternados. */
    private fun DrawScope.abanico(c: Offset, r: Float, a: Color, b: Color, gajos: Int = 10, abajo: Boolean = false) {
        val n = gajos.coerceIn(2, 24)
        val inicio = if (abajo) 0f else 180f
        for (k in 0 until n) drawArc(if (k % 2 == 0) a else b, inicio + k * 180f / n, 180f / n, true, Offset(c.x - r, c.y - r), Size(2 * r, 2 * r))
    }

    /** Un rectángulo con las cuatro esquinas cortadas en chaflán de [ch]. */
    private fun chaflan(r: Rect, ch: Float): Path = Path().apply {
        moveTo(r.left + ch, r.top); lineTo(r.right - ch, r.top); lineTo(r.right, r.top + ch); lineTo(r.right, r.bottom - ch)
        lineTo(r.right - ch, r.bottom); lineTo(r.left + ch, r.bottom); lineTo(r.left, r.bottom - ch); lineTo(r.left, r.top + ch); close()
    }

    /** Un nicho de remate escalonado: el cuerpo de [r] y, arriba, [escalones] gradas que se estrechan hasta [cima]. */
    private fun escalonado(r: Rect, cima: Float, escalones: Int): Path = Path().apply {
        val n = escalones.coerceIn(1, 6)
        val subida = (r.top - cima) / n
        val entra = r.width * 0.12f
        moveTo(r.left, r.bottom); lineTo(r.left, r.top)
        for (k in 0 until n) { lineTo(r.left + entra * k, r.top - subida * k); lineTo(r.left + entra * (k + 1), r.top - subida * k); lineTo(r.left + entra * (k + 1), r.top - subida * (k + 1)) }
        for (k in n - 1 downTo 0) { lineTo(r.right - entra * (k + 1), r.top - subida * (k + 1)); lineTo(r.right - entra * (k + 1), r.top - subida * k); lineTo(r.right - entra * k, r.top - subida * k) }
        lineTo(r.right, r.top); lineTo(r.right, r.bottom); close()
    }

    /** Una cenefa de chevrones de [x0] a [x1] a la altura [y]: un zigzag de dientes de [paso]. */
    private fun DrawScope.chevrones(x0: Float, x1: Float, y: Float, paso: Float, alto: Float, color: Color, grosor: Float) {
        if (paso < 0.5f || x1 <= x0) return
        val n = ((x1 - x0) / paso).toInt().coerceIn(1, 200)
        val p = Path().apply {
            moveTo(x0, y)
            for (k in 0 until n) { lineTo(x0 + paso * (k + 0.5f), y - alto); lineTo(x0 + paso * (k + 1), y) }
        }
        drawPath(p, color, style = Stroke(grosor, join = StrokeJoin.Miter))
    }

    /** Una estrella de [puntas] puntas con centro [c] y radio [r]; [hondo] es el radio de los entrantes, en fracción de [r]. */
    private fun estrella(c: Offset, r: Float, puntas: Int, hondo: Float, giro: Double = -90.0): Path = Path().apply {
        for (k in 0 until puntas * 2) {
            val a = Math.toRadians(giro + k * 180.0 / puntas)
            val rr = if (k % 2 == 0) r else r * hondo
            val x = c.x + rr * cos(a).toFloat(); val y = c.y + rr * sin(a).toFloat()
            if (k == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
    }

    /**
     * Una butaca de concha vista de frente: el respaldo redondo con sus acanaladuras de oro, el cojín y el zócalo; [c] es el
     * centro del asiento.
     */
    private fun DrawScope.butaca(c: Offset, s: Float, tapiz: Color, oro: Color, base: Color) {
        val ancho = 11f * s
        drawArc(tapiz, 180f, 180f, true, Offset(c.x - ancho / 2, c.y - 9f * s), Size(ancho, 16f * s))
        for (k in 1..3) drawLine(oro.copy(alpha = 0.6f), Offset(c.x, c.y - 1f * s), Offset(c.x + cos(Math.toRadians(180.0 + k * 45.0)).toFloat() * ancho * 0.42f,
            c.y - 1f * s + sin(Math.toRadians(180.0 + k * 45.0)).toFloat() * 7f * s), 0.35f * s)
        drawRect(tapiz, Offset(c.x - ancho / 2, c.y - 1f * s), Size(ancho, 5f * s))
        drawRect(mezcla(tapiz, Color.White, 0.15f), Offset(c.x - ancho / 2 + 1f * s, c.y - 1f * s), Size(ancho - 2f * s, 1.6f * s))
        drawLine(oro, Offset(c.x - ancho / 2, c.y + 4f * s), Offset(c.x + ancho / 2, c.y + 4f * s), 0.5f * s)
        drawRect(base, Offset(c.x - ancho * 0.4f, c.y + 4f * s), Size(ancho * 0.8f, 2.4f * s))
    }

    /** La rosa de los vientos déco: ocho puntas (las cuatro grandes en oro y jade), su anillo y su botón. */
    private fun DrawScope.rosa(c: Offset, r: Float, oro: Color, jade: Color, fondo: Color, grosor: Float) {
        drawCircle(oro.copy(alpha = 0.8f), r * 0.78f, c, style = Stroke(grosor))
        drawPath(estrella(c, r * 0.62f, 4, 0.22f, -45.0), jade)
        drawPath(estrella(c, r, 4, 0.2f), oro)
        listOf(0.0, 90.0, 180.0, 270.0).forEach { a ->
            val ang = Math.toRadians(a - 90.0)
            drawPath(Path().apply { moveTo(c.x, c.y); lineTo(c.x + r * cos(ang).toFloat(), c.y + r * sin(ang).toFloat()); lineTo(c.x + r * 0.2f * cos(ang + 0.785).toFloat(), c.y + r * 0.2f * sin(ang + 0.785).toFloat()); close() },
                mezcla(oro, Color.Black, 0.3f))
        }
        drawCircle(fondo, r * 0.12f, c)
        drawCircle(oro, r * 0.12f, c, style = Stroke(grosor * 0.8f))
    }

    // ---------------------------------------------------------------- Social

    override fun DrawScope.muebleSocial(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        if (u <= 0f) return
        val oro = oro(oscuro)
        val laca = laca(e, oscuro)
        // El sol de rayos del techo, arriba al centro, tenue: el centro queda tranquilo para el grafo.
        val sol = Offset(w / 2, 0f)
        val nr = 15
        for (k in 1 until nr) {
            val a = Math.toRadians(k * 180.0 / nr)
            val largo = (if (k % 2 == 0) 16f else 11f) * u
            drawLine(oro.copy(alpha = if (oscuro) 0.22f else 0.25f), Offset(sol.x + cos(a).toFloat() * 4.5f * u, sol.y + sin(a).toFloat() * 4.5f * u),
                Offset(sol.x + cos(a).toFloat() * largo, sol.y + sin(a).toFloat() * largo), 0.4f * u)
        }
        abanico(sol, 3.6f * u, oro.copy(alpha = 0.6f), oro.copy(alpha = 0.35f), 8, abajo = true)
        // Las pilastras de laca a los lados, que suben en tres escalones, con sus estrías de oro.
        listOf(0f, w).forEach { borde ->
            val s = if (borde == 0f) 1f else -1f
            val pasos = listOf(7.5f to 0f, 5.5f to 6f, 3.5f to 12f)
            pasos.forEach { (ancho, baja) ->
                val x = if (s > 0) borde else borde - ancho * u
                drawRect(laca, Offset(x, baja * u), Size(ancho * u, h - baja * u))
            }
            for (k in 1..2) {
                val x = borde + s * (k * 2.2f) * u
                drawLine(oro.copy(alpha = 0.7f), Offset(x, 14f * u), Offset(x, h - 4f * u), 0.35f * u)
            }
            drawLine(oro, Offset(borde + s * 7.5f * u, 0f), Offset(borde + s * 7.5f * u, h), 0.4f * u)
            // El aplique de abanico de vidrio esmerilado (de noche, encendido).
            val ap = Offset(borde + s * 14.5f * u, 22f * u)
            if (oscuro) drawCircle(Brush.radialGradient(listOf(LUZ.copy(alpha = 0.35f), LUZ.copy(alpha = 0f)), ap, 11f * u), 11f * u, ap)
            abanico(ap, 5.5f * u, mezcla(e.fondo, if (oscuro) LUZ else CREMA, if (oscuro) 0.75f else 1f), mezcla(e.fondo, LUZ, if (oscuro) 0.5f else 0.6f), 7)
            drawArc(oro, 180f, 180f, true, Offset(ap.x - 5.5f * u, ap.y - 5.5f * u), Size(11f * u, 11f * u), style = Stroke(0.4f * u))
            drawRect(oro, Offset(ap.x - 2.4f * u, ap.y), Size(4.8f * u, 1.2f * u))
            drawRect(laca, Offset(ap.x - 1.2f * u, ap.y + 1.2f * u), Size(2.4f * u, 2.6f * u))
        }
        // La cenefa de chevrones dorados al pie.
        drawLine(oro.copy(alpha = 0.7f), Offset(7.5f * u, h - 0.8f * u), Offset(w - 7.5f * u, h - 0.8f * u), 0.4f * u)
        chevrones(7.5f * u, w - 7.5f * u, h - 1.4f * u, 4f * u, 1.6f * u, oro.copy(alpha = 0.55f), 0.35f * u)
        // Las dos butacas de concha.
        val tapiz = if (oscuro) mezcla(e.fondo, JADE, 0.6f) else JADE.copy(alpha = 0.85f)
        butaca(Offset(16f * u, h - 9f * u), u, tapiz, oro, laca)
        butaca(Offset(w - 16f * u, h - 9f * u), u, tapiz, oro, laca)
        // La mesa redonda de laca: el tablero visto de frente con su canto de oro y el pie que baja en escalones.
        val mc = Offset(w / 2, h - 13f * u)
        val rx = minOf(w * 0.17f, 46f * u)
        drawOval(laca.copy(alpha = if (oscuro) 0.9f else 0.75f), Offset(mc.x - rx, mc.y - 2.2f * u), Size(rx * 2, 4.4f * u))
        drawOval(oro, Offset(mc.x - rx, mc.y - 2.2f * u), Size(rx * 2, 4.4f * u), style = Stroke(0.45f * u))
        drawLine(oro, Offset(mc.x - rx * 0.97f, mc.y + 0.6f * u), Offset(mc.x + rx * 0.97f, mc.y + 0.6f * u), 0.3f * u)
        drawRect(laca, Offset(mc.x - 2f * u, mc.y + 2.2f * u), Size(4f * u, 6f * u))
        drawRect(laca, Offset(mc.x - 5f * u, mc.y + 8.2f * u), Size(10f * u, 1.8f * u))
        drawRect(laca, Offset(mc.x - 8f * u, mc.y + 10f * u), Size(16f * u, 1.6f * u))
        drawLine(oro, Offset(mc.x - 5f * u, mc.y + 8.2f * u), Offset(mc.x + 5f * u, mc.y + 8.2f * u), 0.35f * u)
        drawLine(oro, Offset(mc.x - 8f * u, mc.y + 10f * u), Offset(mc.x + 8f * u, mc.y + 10f * u), 0.35f * u)
        // Sobre la mesa, dos copas de cóctel con su gota de coral.
        listOf(-0.6f, 0.62f).forEach { f ->
            val cx = mc.x + rx * f; val cy = mc.y - 1.4f * u
            drawPath(Path().apply { moveTo(cx - 2f * u, cy - 4.2f * u); lineTo(cx + 2f * u, cy - 4.2f * u); lineTo(cx, cy - 2f * u); close() }, mezcla(e.fondo, CROMO, 0.8f))
            drawLine(mezcla(e.fondo, CROMO_OSC, 0.9f), Offset(cx, cy - 2f * u), Offset(cx, cy), 0.35f * u)
            drawLine(mezcla(e.fondo, CROMO_OSC, 0.9f), Offset(cx - 1f * u, cy), Offset(cx + 1f * u, cy), 0.35f * u)
            drawCircle(CORAL, 0.6f * u, Offset(cx, cy - 3.6f * u))
        }
    }

    // ---------------------------------------------------------------- Mapas

    override fun DrawScope.muebleMapas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        if (u <= 0f) return
        val rnd = Random(e.semilla.toLong())
        val oro = oro(oscuro)
        val laca = laca(e, oscuro)
        // El marco de laca de esquinas cortadas y la carta marfil por dentro.
        drawPath(chaflan(Rect(0f, 0f, w, h), 6f * u), laca)
        val carta = Rect(3.4f * u, 3.4f * u, w - 3.4f * u, h - 3.4f * u)
        drawPath(chaflan(carta, 4.4f * u), if (oscuro) mezcla(e.fondo, CREMA, 0.08f) else mezcla(e.fondo, CREMA, 0.75f))
        // Las líneas de rumbo que salen de la rosa y cruzan la carta, muy tenues.
        val rc = Offset(13.5f * u, h - 13.5f * u)
        val giro = rnd.nextDouble() * 11.25
        for (k in 0 until 16) {
            val a = Math.toRadians(giro + k * 22.5)
            val largo = w * 1.2f
            drawLine(if (k % 2 == 0) oro.copy(alpha = 0.18f) else e.secundario.copy(alpha = 0.12f), rc,
                Offset(rc.x + cos(a).toFloat() * largo, rc.y + sin(a).toFloat() * largo), 0.3f * u)
        }
        // El doble filete de oro y los escalones de las esquinas.
        drawPath(chaflan(carta, 4.4f * u), oro, style = Stroke(0.6f * u, join = StrokeJoin.Miter))
        drawPath(chaflan(Rect(carta.left + 1.4f * u, carta.top + 1.4f * u, carta.right - 1.4f * u, carta.bottom - 1.4f * u), 3.8f * u), oro.copy(alpha = 0.55f),
            style = Stroke(0.3f * u, join = StrokeJoin.Miter))
        listOf(Offset(0f, 0f) to Offset(1f, 1f), Offset(w, 0f) to Offset(-1f, 1f), Offset(0f, h) to Offset(1f, -1f), Offset(w, h) to Offset(-1f, -1f)).forEach { (o, d) ->
            for (k in 1..2) {
                val t = (6f + k * 1.6f) * u
                drawLine(oro.copy(alpha = 0.8f), Offset(o.x + d.x * t, o.y + d.y * 0.6f * u), Offset(o.x + d.x * 0.6f * u, o.y + d.y * t), 0.35f * u)
            }
        }
        // La rosa de los vientos déco, abajo a la izquierda.
        drawCircle(if (oscuro) mezcla(e.fondo, CREMA, 0.08f) else mezcla(e.fondo, CREMA, 0.75f), 8f * u, rc)
        rosa(rc, 8f * u, oro, if (oscuro) mezcla(e.fondo, e.secundario, 0.8f) else JADE, if (oscuro) e.fondo else CREMA, 0.4f * u)
        // Las tres líneas de cromo de la velocidad, arriba a la derecha, sobre el marco.
        for (k in 0 until 3) {
            val y = 1f * u + k * 0.8f * u
            drawLine(mezcla(e.fondo, CROMO, if (oscuro) 0.6f else 0.9f), Offset(w * 0.62f + k * 3f * u, y), Offset(w - 9f * u, y), 0.35f * u)
        }
    }

    // ---------------------------------------------------------------- Otras vidas

    override fun DrawScope.muebleOtrasVidas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        if (u <= 0f) return
        val oro = oro(oscuro)
        val laca = laca(e, oscuro)
        val y = h * 0.8f
        val paso = w * 0.92f / 5f
        // Los nichos de remate escalonado, como la silueta de los rascacielos (el del centro, más alto).
        val interior = if (oscuro) mezcla(e.fondo, JADE, 0.18f) else mezcla(e.fondo, JADE, 0.06f)
        repeat(5) { i ->
            val cx = w * 0.04f + paso * (i + 0.5f)
            val nw = paso * 0.88f
            val alto = when (i) { 2 -> 0.03f; 1, 3 -> 0.08f; else -> 0.12f }
            val cuerpo = Rect(cx - nw / 2, h * (0.24f + alto * 0.5f), cx + nw / 2, y)
            val nicho = escalonado(cuerpo, h * alto, 3)
            drawPath(nicho, interior)
            drawPath(nicho, oro, style = Stroke(0.5f * u, join = StrokeJoin.Miter))
            drawLine(Color.White.copy(alpha = if (oscuro) 0.07f else 0.55f), Offset(cuerpo.left + nw * 0.15f, cuerpo.bottom - 3f * u), Offset(cuerpo.left + nw * 0.42f, cuerpo.top + 2f * u), 0.7f * u, StrokeCap.Round)
            // El abanico en lo alto de la grada de arriba.
            val cima = h * alto
            abanico(Offset(cx, cima + (cuerpo.top - cima) * 0.62f), minOf(nw * 0.18f, (cuerpo.top - cima) * 0.5f), oro.copy(alpha = 0.75f), oro.copy(alpha = 0.35f), 6)
        }
        // Las pilastras de laca entre los nichos, con su filete de oro.
        for (k in 0..5) {
            val cx = w * 0.04f + paso * k
            val cw = paso * 0.08f
            drawRect(laca, Offset(cx - cw / 2, h * 0.3f), Size(cw, y - h * 0.3f))
            drawLine(oro.copy(alpha = 0.8f), Offset(cx, h * 0.33f), Offset(cx, y - 1f * u), 0.3f * u)
        }
        // El zócalo de laca con su filete de oro y las tres líneas de cromo.
        drawRect(laca, Offset(w * 0.02f, y), Size(w * 0.96f, h * 0.08f))
        drawLine(oro, Offset(w * 0.02f, y), Offset(w * 0.98f, y), 0.6f * u)
        for (k in 0 until 3) {
            val ly = y + h * 0.11f + k * 1.4f * u
            drawLine(mezcla(e.fondo, CROMO, if (oscuro) 0.6f else 0.9f), Offset(w * (0.06f + k * 0.02f), ly), Offset(w * (0.94f - k * 0.02f), ly), 0.45f * u)
        }
    }

    // ---------------------------------------------------------------- Íconos de mapas

    override fun DrawScope.iconoMapa(id: String, e: EstadoIcono, oscuro: Boolean) {
        val w = size.width; val h = size.height; val c = Offset(w / 2, h / 2); val r = minOf(w, h) * 0.36f
        val g = 1.6f * density
        val trazo = Stroke(g, cap = StrokeCap.Round, join = StrokeJoin.Miter)
        when (id) {
            "unesco" -> {
                // El rascacielos: la base ancha, el fuste con sus pilastras, la corona de arcos escalonados y la aguja.
                val base = Rect(c.x - r * 0.75f, c.y + r * 0.4f, c.x + r * 0.75f, c.y + r * 0.95f)
                val fuste = Rect(c.x - r * 0.42f, c.y - r * 0.25f, c.x + r * 0.42f, c.y + r * 0.4f)
                listOf(base, fuste).forEach { b -> drawRect(e.c1.copy(alpha = 0.2f), b.topLeft, b.size); drawRect(e.c1, b.topLeft, b.size, style = trazo) }
                listOf(-0.5f, -0.2f, 0.2f, 0.5f).forEach { k -> drawLine(e.c2, Offset(c.x + k * r, base.top + r * 0.1f), Offset(c.x + k * r, base.bottom - r * 0.08f), g * 0.6f) }
                listOf(-0.22f, 0f, 0.22f).forEach { k -> drawLine(e.c2, Offset(c.x + k * r, fuste.top + r * 0.1f), Offset(c.x + k * r, fuste.bottom - r * 0.06f), g * 0.6f) }
                for (k in 0 until 3) {
                    val rr = r * (0.42f - k * 0.12f)
                    val cy = fuste.top - k * r * 0.17f
                    drawArc(e.c1.copy(alpha = 0.25f), 180f, 180f, true, Offset(c.x - rr, cy - rr), Size(rr * 2, rr * 2))
                    drawArc(e.c1, 180f, 180f, false, Offset(c.x - rr, cy - rr), Size(rr * 2, rr * 2), style = Stroke(g))
                    drawPath(Path().apply { moveTo(c.x - rr * 0.35f, cy - rr * 0.05f); lineTo(c.x, cy - rr * 0.7f); lineTo(c.x + rr * 0.35f, cy - rr * 0.05f); close() }, e.c2)
                }
                drawLine(e.c1, Offset(c.x, fuste.top - r * 0.62f), Offset(c.x, c.y - r * 1.12f), g, StrokeCap.Round)
                drawLine(e.c2, Offset(c.x - r * 1.2f, base.bottom), Offset(c.x + r * 1.2f, base.bottom), g * 1.2f, StrokeCap.Round)
            }
            "rascar" -> {
                // La placa de laca de esquinas cortadas, con su filete de oro y la esquina raspada en escalones.
                val placa = Rect(c.x - r * 1.45f, c.y - r * 0.85f, c.x + r * 1.45f, c.y + r * 0.85f)
                val ch = r * 0.28f
                drawPath(chaflan(placa, ch), if (oscuro) mezcla(e.fondo, LACA_LUZ, 0.5f) else LACA)
                val raspado = Path().apply {
                    moveTo(placa.right - placa.width * 0.5f, placa.bottom)
                    lineTo(placa.right - placa.width * 0.5f, placa.bottom - placa.height * 0.22f); lineTo(placa.right - placa.width * 0.36f, placa.bottom - placa.height * 0.22f)
                    lineTo(placa.right - placa.width * 0.36f, placa.bottom - placa.height * 0.48f); lineTo(placa.right - placa.width * 0.2f, placa.bottom - placa.height * 0.48f)
                    lineTo(placa.right - placa.width * 0.2f, placa.top + ch); lineTo(placa.right, placa.top + ch)
                    lineTo(placa.right, placa.bottom - ch); lineTo(placa.right - ch, placa.bottom); close()
                }
                drawPath(raspado, e.c1)
                drawPath(chaflan(placa, ch), e.c1, style = trazo)
                drawPath(chaflan(Rect(placa.left + r * 0.16f, placa.top + r * 0.16f, placa.right - r * 0.16f, placa.bottom - r * 0.16f), ch * 0.7f), e.c1.copy(alpha = 0.6f), style = Stroke(g * 0.5f))
                abanico(Offset(placa.left + placa.width * 0.28f, placa.bottom - r * 0.3f), r * 0.42f, e.c1, e.c2, 8)
            }
            else -> {
                // Tus lugares: tres abanicos de rayos sobre su alfiler.
                listOf(Offset(-0.95f, 0.15f) to 0.85f, Offset(0f, -0.2f) to 1.05f, Offset(0.95f, 0.05f) to 0.9f).forEach { (o, k) ->
                    val p = Offset(c.x + o.x * r, c.y + o.y * r)
                    val pie = Offset(p.x, p.y + r * 0.85f * k)
                    drawLine(e.c1, p, pie, g, StrokeCap.Round)
                    drawCircle(e.c1, r * 0.07f, pie)
                    abanico(p, r * 0.5f * k, e.c1, e.c2, 6)
                    drawArc(e.c1, 180f, 180f, false, Offset(p.x - r * 0.5f * k, p.y - r * 0.5f * k), Size(r * k, r * k), style = Stroke(g * 0.6f))
                }
            }
        }
    }

    // ---------------------------------------------------------------- Notas

    override fun DrawScope.tableroNotas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        if (u <= 0f) return
        val oro = oro(oscuro)
        // El panel lacado de esquinas cortadas, con sus estrías de oro muy tenues.
        val panel = Rect(0f, 0f, w, h)
        drawPath(chaflan(panel, 5f * u), laca(e, oscuro))
        val ne = (w / (12f * u)).toInt().coerceIn(1, 80)
        for (k in 1 until ne) drawLine(oro.copy(alpha = 0.10f), Offset(k * w / ne, 6f * u), Offset(k * w / ne, h - 6f * u), 0.3f * u)
        // El doble filete de oro.
        drawPath(chaflan(Rect(1.6f * u, 1.6f * u, w - 1.6f * u, h - 1.6f * u), 4.2f * u), oro, style = Stroke(0.5f * u, join = StrokeJoin.Miter))
        drawPath(chaflan(Rect(2.8f * u, 2.8f * u, w - 2.8f * u, h - 2.8f * u), 3.6f * u), oro.copy(alpha = 0.5f), style = Stroke(0.25f * u, join = StrokeJoin.Miter))
        // El abanico arriba al centro y los chevrones abajo.
        abanico(Offset(w / 2, 6.6f * u), 5f * u, oro.copy(alpha = 0.85f), oro.copy(alpha = 0.4f), 10)
        drawLine(oro, Offset(w / 2 - 9f * u, 6.6f * u), Offset(w / 2 + 9f * u, 6.6f * u), 0.4f * u)
        chevrones(w * 0.3f, w * 0.7f, h - 3.4f * u, 3f * u, 1.4f * u, oro.copy(alpha = 0.7f), 0.3f * u)
    }

    /** La cartulina de la invitación: marfil de día; de noche, laca parda (la tinta de [tintaNota] se lee en las dos). */
    private fun papel(oscuro: Boolean): Color = if (oscuro) Color(0xFF26211A) else Color(0xFFFBF5E6)

    override fun DrawScope.notaTablero(e: EstadoNota, oscuro: Boolean) {
        val r = e.rect
        val k = size.minDimension / 100f
        if (k <= 0f) return
        val papel = papel(oscuro)
        val oro = oro(oscuro)
        val hoja = Rect(r.left + 0.5f * k, r.top + 1f * k, r.right - 1.5f * k, r.bottom - 2f * k)
        val ch = hoja.height * 0.1f
        // La sombra, la tarjeta de esquinas cortadas y su doble ribete dorado.
        drawPath(chaflan(Rect(hoja.left + 1.2f * k, hoja.top + 1.8f * k, hoja.right + 1.2f * k, hoja.bottom + 1.8f * k), ch), Color.Black.copy(alpha = if (oscuro) 0.5f else 0.3f))
        drawPath(chaflan(hoja, ch), papel)
        val m1 = hoja.height * 0.04f; val m2 = hoja.height * 0.075f
        drawPath(chaflan(Rect(hoja.left + m1, hoja.top + m1, hoja.right - m1, hoja.bottom - m1), ch * 0.8f), oro, style = Stroke(1f * k, join = StrokeJoin.Miter))
        drawPath(chaflan(Rect(hoja.left + m2, hoja.top + m2, hoja.right - m2, hoja.bottom - m2), ch * 0.6f), oro.copy(alpha = 0.55f), style = Stroke(0.45f * k, join = StrokeJoin.Miter))
        // El adorno escalonado del tipo en las dos esquinas de abajo: oro, jade, coral o cromo.
        val color = when (e.tipo) {
            "idea" -> oro
            "proposito" -> if (oscuro) Color(0xFF5FB59A) else JADE
            "recordatorio" -> if (oscuro) Color(0xFFE48A6E) else CORAL
            else -> if (oscuro) CROMO else CROMO_OSC
        }
        val s = hoja.height * 0.05f
        listOf(hoja.left + m2 + 1f * k to 1f, hoja.right - m2 - 1f * k to -1f).forEach { (x, d) ->
            val yb = hoja.bottom - m2 - 1f * k
            for (p in 0 until 3) drawRect(color, Offset(if (d > 0) x else x - s * (3 - p), yb - s * (p + 1)), Size(s * (3 - p), s))
        }
        // El broche de abanico dorado si está fijada; si no, un rombo pequeño del color del tipo.
        val bc = Offset(hoja.center.x, hoja.top + hoja.height * 0.1f)
        if (e.fijada) {
            val br = hoja.height * 0.085f
            drawCircle(Color.Black.copy(alpha = 0.3f), br * 0.6f, Offset(bc.x + 0.6f * k, bc.y + 0.8f * k))
            abanico(bc, br, oro, mezcla(oro, Color.White, 0.35f), 8)
            drawRect(oro, Offset(bc.x - br, bc.y), Size(br * 2, br * 0.22f))
            drawCircle(if (oscuro) Color(0xFF5FB59A) else JADE, br * 0.24f, Offset(bc.x, bc.y + br * 0.1f))
        } else {
            drawPath(Path().apply { moveTo(bc.x, bc.y - s); lineTo(bc.x + s * 1.4f, bc.y); lineTo(bc.x, bc.y + s); lineTo(bc.x - s * 1.4f, bc.y); close() }, color)
        }
    }

    override fun tintaNota(oscuro: Boolean): Color = if (oscuro) Color(0xFFF0E6CC) else TINTA
}
