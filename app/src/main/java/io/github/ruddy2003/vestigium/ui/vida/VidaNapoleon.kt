package io.github.ruddy2003.vestigium.ui.vida

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.lerp
import java.util.Random
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * Napoleón: los muebles de Vida y de Diario del Imperio (1804–1815), de la misma casa que su biblioteca de campaña, su
 * secrétaire de caoba y su galería imperial de logros. Caoba con bronces dorados, azul imperial, carmesí, cuero verde,
 * pergamino, lacre, abejas de oro y coronas de laurel.
 * - Social: la mesa del estado mayor: el tablero de caoba con su moldura y sus bronces, el mapa de campaña que cuelga por el
 *   frente, el bicornio con su escarapela y el catalejo en las puntas, dos sillas Imperio de patas de sable a los lados, el
 *   lambrequín carmesí con su fleco de oro arriba y la pared de seda con abejas, apenas marcada.
 * - Mapas: el escritorio de campaña: el marco de caoba, el cuero verde con su doble filete dorado y sus florones, los
 *   alfileres de cabeza azul, carmesí y oro clavados alrededor del mapa, el compás de puntas y el catalejo.
 * - Otras vidas: la vitrina Imperio: un nicho forrado de terciopelo azul por medalla entre columnas de caoba con capitel y
 *   basa de bronce, la cornisa con su perlado, una abeja de oro sobre cada nicho y el zócalo con sus rombos dorados.
 * - Íconos: el arco de triunfo; el pergamino raspado por un sable; tres abejas imperiales como marcadores.
 * - Notas: el tablero de despachos: el marco de caoba con sus rosetas, la seda azul sembrada de abejas y la corona de laurel
 *   arriba; las notas son despachos de pergamino con doble filete, prendidos con un alfiler de latón o, si están fijados,
 *   con el sello de lacre y sus cintas, y un detalle según el tipo (estrella, laurel, escarapela tricolor, esquina doblada).
 */
internal object VidaNapoleon : DisenoVida {
    private val CAOBA = Color(0xFF6E2F1E)
    private val CAOBA_LUZ = Color(0xFF8D4430)
    private val CAOBA_OSC = Color(0xFF4A1E12)
    private val ORO = Color(0xFFD9B54A)
    private val ORO_VIEJO = Color(0xFFB8892B)
    private val LATON = Color(0xFFC7A04F)
    private val AZUL = Color(0xFF1E3A8A)
    private val CARMESI = Color(0xFFA3172E)
    private val CUERO = Color(0xFF2E5A3A)
    private val PERGAMINO = Color(0xFFEDE2C4)
    private val LACRE = Color(0xFF9A1A1A)
    private val NEGRO = Color(0xFF1C1A20)
    private val BLANCO = Color(0xFFF6F2E8)
    private val TINTA = Color(0xFF14213D)

    private fun DrawScope.u() = size.minDimension / 100f
    private fun mezcla(a: Color, b: Color, f: Float): Color = lerp(a, b, f.coerceIn(0f, 1f))

    /** Un material del Imperio sobre la tarjeta: en claro, tal cual; en oscuro, hundido hacia el terciopelo azul del fondo. */
    private fun m(c: Color, e: EstadoMueble, oscuro: Boolean, hundido: Float = 0.4f): Color = if (oscuro) mezcla(c, e.fondo, hundido) else c

    /** El oro de los bronces: oro viejo de día (para que se vea sobre el papel claro) y oro vivo de noche. */
    private fun oro(oscuro: Boolean): Color = if (oscuro) ORO else ORO_VIEJO

    // ---------------------------------------------------------------- piezas

    /** Una abeja imperial vista desde arriba: las dos alas, el cuerpo y la cabeza. */
    private fun DrawScope.abeja(c: Offset, s: Float, color: Color) {
        drawOval(color, Offset(c.x - s * 0.36f, c.y - s * 0.32f), Size(s * 0.32f, s * 0.42f))
        drawOval(color, Offset(c.x + s * 0.04f, c.y - s * 0.32f), Size(s * 0.32f, s * 0.42f))
        drawOval(color, Offset(c.x - s * 0.12f, c.y - s * 0.3f), Size(s * 0.24f, s * 0.72f))
        drawCircle(color, s * 0.12f, Offset(c.x, c.y - s * 0.38f))
    }

    /** Una abeja de tres trazos, para sembrar una tela sin gastar. */
    private fun DrawScope.abejaChica(c: Offset, s: Float, color: Color) {
        drawOval(color, Offset(c.x - s * 0.36f, c.y - s * 0.3f), Size(s * 0.72f, s * 0.36f))
        drawOval(color, Offset(c.x - s * 0.12f, c.y - s * 0.3f), Size(s * 0.24f, s * 0.7f))
        drawCircle(color, s * 0.12f, Offset(c.x, c.y - s * 0.38f))
    }

    /** La tela sembrada de abejas en [r]: en tresbolillo, una cada [paso]. */
    private fun DrawScope.sembrado(r: Rect, paso: Float, s: Float, color: Color) {
        if (paso < 1f) return
        val nx = (r.width / paso).toInt().coerceIn(0, 40)
        val ny = (r.height / paso).toInt().coerceIn(0, 20)
        for (j in 0..ny) for (i in 0..nx) {
            val x = r.left + paso * (i + if (j % 2 == 0) 0.25f else 0.75f)
            val y = r.top + paso * (j + 0.5f)
            if (x < r.right - s * 0.4f && y < r.bottom - s * 0.4f) abejaChica(Offset(x, y), s, color)
        }
    }

    /** Una corona de laurel: dos ramas de hojas que se cierran arriba. */
    private fun DrawScope.laurel(c: Offset, r: Float, color: Color, grosor: Float) {
        listOf(-1f, 1f).forEach { s ->
            drawArc(color, if (s < 0) 100f else -80f, 160f, false, Offset(c.x - r, c.y - r), Size(2 * r, 2 * r), style = Stroke(grosor))
            for (k in 0 until 6) {
                val a = Math.toRadians(if (s < 0) 115.0 + k * 26.0 else 65.0 - k * 26.0)
                val p = Offset(c.x + r * cos(a).toFloat(), c.y + r * sin(a).toFloat())
                rotate(Math.toDegrees(a).toFloat() + 90f * s, p) { drawOval(color, Offset(p.x - r * 0.1f, p.y - r * 0.24f), Size(r * 0.2f, r * 0.42f)) }
            }
        }
    }

    /** Una estrella de [puntas] puntas con centro [c] y radio [r]; [hondo] es el radio de los entrantes, en fracción de [r]. */
    private fun estrella(c: Offset, r: Float, puntas: Int, hondo: Float): Path = Path().apply {
        for (k in 0 until puntas * 2) {
            val a = Math.toRadians(-90.0 + k * 180.0 / puntas)
            val rr = if (k % 2 == 0) r else r * hondo
            val x = c.x + rr * cos(a).toFloat(); val y = c.y + rr * sin(a).toFloat()
            if (k == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
    }

    /** Un rombo dorado de centro [c]. */
    private fun DrawScope.rombo(c: Offset, a: Float, b: Float, color: Color) =
        drawPath(Path().apply { moveTo(c.x, c.y - b); lineTo(c.x + a, c.y); lineTo(c.x, c.y + b); lineTo(c.x - a, c.y); close() }, color)

    /** Una roseta de bronce: el disco, su anillo y el botón. */
    private fun DrawScope.roseta(c: Offset, r: Float, oro: Color) {
        drawCircle(oro, r, c)
        drawCircle(mezcla(oro, Color.Black, 0.35f), r * 0.62f, c, style = Stroke(r * 0.18f))
        drawCircle(mezcla(oro, Color.White, 0.35f), r * 0.28f, c)
    }

    /**
     * Una silla Imperio de perfil, mirando hacia [dir] (1 a la derecha, −1 a la izquierda): las patas de sable, el asiento
     * tapizado con su galón, el respaldo curvo con su panel y el copete dorado; [x] es el centro del asiento y [piso] el suelo.
     */
    private fun DrawScope.sillaImperio(x: Float, piso: Float, s: Float, dir: Float, madera: Color, tapiz: Color, oro: Color) {
        val asiento = piso - 12f * s
        val atras = x - dir * 5f * s; val frente = x + dir * 5f * s
        // Las patas de sable: la de adelante se abre hacia el frente y la de atrás hacia atrás.
        drawPath(Path().apply { moveTo(frente - dir * 0.6f * s, asiento + 1.6f * s); quadraticTo(frente, piso - 5f * s, frente + dir * 2f * s, piso) }, madera, style = Stroke(1.2f * s, cap = StrokeCap.Round))
        drawPath(Path().apply { moveTo(atras + dir * 0.6f * s, asiento + 1.6f * s); quadraticTo(atras, piso - 5f * s, atras - dir * 2f * s, piso) }, madera, style = Stroke(1.2f * s, cap = StrokeCap.Round))
        // El respaldo: el montante curvo hacia atrás, el panel tapizado y el copete.
        drawPath(Path().apply { moveTo(atras, asiento + 1.6f * s); quadraticTo(atras - dir * 0.4f * s, asiento - 8f * s, atras - dir * 2.2f * s, asiento - 15f * s) },
            madera, style = Stroke(1.3f * s, cap = StrokeCap.Round))
        drawRoundRect(tapiz, Offset(minOf(atras - dir * 1.4f * s, atras + dir * 0.3f * s) - 0.8f * s, asiento - 12.5f * s), Size(2.4f * s, 9f * s), CornerRadius(1f * s))
        drawLine(oro, Offset(atras - dir * 3.6f * s, asiento - 15.4f * s), Offset(atras - dir * 0.6f * s, asiento - 14.6f * s), 1f * s, StrokeCap.Round)
        // El asiento tapizado con su galón dorado.
        val x0 = minOf(atras, frente) - 0.6f * s; val x1 = maxOf(atras, frente) + 0.6f * s
        drawRoundRect(tapiz, Offset(x0, asiento - 1.2f * s), Size(x1 - x0, 2.8f * s), CornerRadius(1.2f * s))
        drawLine(oro, Offset(x0 + 0.6f * s, asiento + 1.4f * s), Offset(x1 - 0.6f * s, asiento + 1.4f * s), 0.5f * s)
    }

    /** El catalejo de latón, horizontal, del ocular en [a] al objetivo en [b] (los tubos se ensanchan hacia [b]). */
    private fun DrawScope.catalejo(a: Offset, b: Offset, s: Float, laton: Color, cuero: Color) {
        val largo = b.x - a.x
        val tramos = listOf(0f to 0.26f, 0.26f to 0.5f, 0.5f to 1f)
        tramos.forEachIndexed { i, (t0, t1) ->
            val alto = (1.1f + i * 0.5f) * s
            val x0 = a.x + largo * t0; val x1 = a.x + largo * t1
            drawRect(if (i == 2) cuero else laton, Offset(minOf(x0, x1), a.y - alto), Size(abs(x1 - x0), alto * 2))
            drawRect(laton, Offset(minOf(x0, x1) - 0.3f * s, a.y - alto - 0.2f * s), Size(0.9f * s, alto * 2 + 0.4f * s))
        }
        drawRect(laton, Offset(b.x - (if (largo > 0) 1.4f else -0.2f) * s, a.y - 2.4f * s), Size(1.2f * s, 4.8f * s))
    }

    // ---------------------------------------------------------------- Social

    override fun DrawScope.muebleSocial(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        if (u <= 0f) return
        val rnd = Random(e.semilla.toLong())
        val oro = oro(oscuro)
        // La pared de seda sembrada de abejas, apenas marcada: el centro queda tranquilo para el grafo.
        sembrado(Rect(0f, 6f * u, w, h), 22f * u, 3f * u, oro.copy(alpha = if (oscuro) 0.09f else 0.12f))
        // El lambrequín carmesí en festones, con su fleco de oro y las borlas entre festón y festón.
        val carmesi = m(CARMESI, e, oscuro, 0.3f)
        drawRect(carmesi, Offset.Zero, Size(w, 2f * u))
        drawLine(oro, Offset(0f, 2f * u), Offset(w, 2f * u), 0.4f * u)
        val anchoF = 16f * u
        val nf = (w / anchoF).toInt().coerceIn(1, 40) + 1
        val inicio = (w - nf * anchoF) / 2f
        for (k in 0 until nf) {
            val x = inicio + k * anchoF
            drawArc(carmesi, 0f, 180f, true, Offset(x, -1.6f * u), Size(anchoF, 7.2f * u))
            drawArc(oro, 10f, 160f, false, Offset(x + 0.5f * u, -1.6f * u), Size(anchoF - 1f * u, 7.2f * u), style = Stroke(0.5f * u))
            drawLine(oro, Offset(x, 2f * u), Offset(x, 5f * u), 0.4f * u)
            drawOval(oro, Offset(x - 0.8f * u, 4.6f * u), Size(1.6f * u, 2.4f * u))
        }
        // El parquet: una línea de zócalo.
        drawLine(m(CAOBA, e, oscuro, 0.5f).copy(alpha = 0.6f), Offset(0f, h - 1.2f * u), Offset(w, h - 1.2f * u), 0.8f * u)
        // Las sillas Imperio a cada lado, mirando a la mesa.
        val madera = m(CAOBA, e, oscuro, 0.3f)
        val tapiz = m(if (oscuro) CARMESI else CUERO, e, oscuro, 0.25f)
        sillaImperio(10.5f * u, h - 1.6f * u, u, 1f, madera, tapiz, oro)
        sillaImperio(w - 10.5f * u, h - 1.6f * u, u, -1f, madera, tapiz, oro)
        // La mesa del estado mayor: el tablero con su moldura, el faldón con sus bronces y las patas de columna.
        val top = h - 21f * u
        val x0 = 21f * u; val x1 = w - 21f * u
        drawRect(m(CAOBA_LUZ, e, oscuro, 0.4f).copy(alpha = 0.55f), Offset(x0 + 2f * u, top), Size(x1 - x0 - 4f * u, 1.6f * u))
        drawRect(madera, Offset(x0, top + 1.6f * u), Size(x1 - x0, 2.4f * u))
        drawLine(oro, Offset(x0, top + 4f * u), Offset(x1, top + 4f * u), 0.4f * u)
        val falda = Rect(x0 + 2f * u, top + 4f * u, x1 - 2f * u, top + 9.5f * u)
        drawRect(m(CAOBA_OSC, e, oscuro, 0.35f), falda.topLeft, falda.size)
        val nr = ((falda.width / (24f * u)).toInt()).coerceIn(1, 12)
        for (k in 0..nr) {
            val rx = falda.left + falda.width * k / nr
            if (k == 0 || k == nr) roseta(Offset(rx + (if (k == 0) 2.4f else -2.4f) * u, falda.center.y), 1.4f * u, oro)
            else rombo(Offset(rx, falda.center.y), 1.8f * u, 1.1f * u, oro.copy(alpha = 0.8f))
        }
        listOf(x0 + 4f * u, x1 - 4f * u).forEach { x ->
            drawPath(Path().apply { moveTo(x - 1.8f * u, falda.bottom); lineTo(x + 1.8f * u, falda.bottom); lineTo(x + 1.1f * u, h - 3f * u); lineTo(x - 1.1f * u, h - 3f * u); close() }, madera)
            drawRect(oro, Offset(x - 2.1f * u, falda.bottom), Size(4.2f * u, 0.9f * u))
            drawOval(oro, Offset(x - 1.8f * u, h - 3.2f * u), Size(3.6f * u, 1.8f * u))
        }
        // El mapa de campaña que cuelga por el frente de la mesa.
        val mapa = Rect(w * 0.38f, top + 0.4f * u, w * 0.62f, top + 8.2f * u)
        drawRect(m(PERGAMINO, e, oscuro, 0.5f), mapa.topLeft, mapa.size)
        drawRect(e.tinta.copy(alpha = 0.25f), mapa.topLeft, mapa.size, style = Stroke(0.3f * u))
        val rio = Path().apply {
            moveTo(mapa.left, mapa.top + mapa.height * 0.6f)
            cubicTo(mapa.left + mapa.width * 0.3f, mapa.top + mapa.height * 0.2f, mapa.left + mapa.width * 0.6f, mapa.bottom, mapa.right, mapa.top + mapa.height * 0.4f)
        }
        drawPath(rio, m(AZUL, e, oscuro, 0.1f).copy(alpha = 0.5f), style = Stroke(0.4f * u))
        for (k in 0 until 3) {
            val px = mapa.left + mapa.width * (0.25f + 0.25f * k) + (rnd.nextFloat() - 0.5f) * 2f * u
            drawRect(if (k == 1) carmesi else m(AZUL, e, oscuro, 0.1f), Offset(px, mapa.top + mapa.height * (0.3f + 0.15f * k)), Size(1.6f * u, 0.9f * u))
        }
        // El bicornio con su escarapela en una punta y el catalejo en la otra.
        val bx = x0 + 9f * u; val by = top + 0.2f * u
        val sombrero = Path().apply { moveTo(bx - 6.4f * u, by); quadraticTo(bx, by - 8f * u, bx + 6.4f * u, by); quadraticTo(bx, by - 2.2f * u, bx - 6.4f * u, by); close() }
        drawPath(sombrero, if (oscuro) mezcla(NEGRO, e.fondo, 0.15f) else NEGRO)
        drawPath(sombrero, oro.copy(alpha = if (oscuro) 0.8f else 0.5f), style = Stroke(0.35f * u, join = StrokeJoin.Round))
        val esc = Offset(bx + 1.2f * u, by - 3.4f * u)
        drawCircle(m(AZUL, e, oscuro, 0f), 1.4f * u, esc)
        drawCircle(BLANCO, 0.95f * u, esc)
        drawCircle(m(CARMESI, e, oscuro, 0f), 0.5f * u, esc)
        catalejo(Offset(x1 - 20f * u, top - 1.6f * u), Offset(x1 - 4f * u, top - 1.6f * u), u, m(LATON, e, oscuro, 0.2f), m(CAOBA_OSC, e, oscuro, 0.2f))
    }

    // ---------------------------------------------------------------- Mapas

    override fun DrawScope.muebleMapas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        if (u <= 0f) return
        val rnd = Random(e.semilla.toLong())
        val oro = oro(oscuro)
        // El escritorio de campaña: el marco de caoba y el cuero verde con su doble filete dorado.
        drawRect(m(CAOBA, e, oscuro, 0.35f).copy(alpha = if (oscuro) 0.9f else 0.8f), Offset.Zero, size)
        drawRect(m(CAOBA_LUZ, e, oscuro, 0.4f).copy(alpha = 0.6f), Offset(0f, 0f), Size(w, 0.8f * u))
        val cuero = Rect(3f * u, 3f * u, w - 3f * u, h - 3f * u)
        drawRect(mezcla(e.fondo, CUERO, if (oscuro) 0.4f else 0.3f), cuero.topLeft, cuero.size)
        drawRect(oro.copy(alpha = 0.8f), Offset(cuero.left + 1.2f * u, cuero.top + 1.2f * u), Size(cuero.width - 2.4f * u, cuero.height - 2.4f * u), style = Stroke(0.45f * u))
        drawRect(oro.copy(alpha = 0.55f), Offset(cuero.left + 2.2f * u, cuero.top + 2.2f * u), Size(cuero.width - 4.4f * u, cuero.height - 4.4f * u), style = Stroke(0.25f * u))
        listOf(Offset(cuero.left + 2.2f * u, cuero.top + 2.2f * u), Offset(cuero.right - 2.2f * u, cuero.top + 2.2f * u),
            Offset(cuero.left + 2.2f * u, cuero.bottom - 2.2f * u), Offset(cuero.right - 2.2f * u, cuero.bottom - 2.2f * u)).forEach { rombo(it, 1.6f * u, 1.6f * u, oro) }
        // Los alfileres de cabeza de color clavados alrededor del mapa.
        val cabezas = listOf(m(AZUL, e, oscuro, 0f), m(CARMESI, e, oscuro, 0f), oro)
        val sitios = listOf(Offset(9.5f * u, h * 0.32f), Offset(9.5f * u, h * 0.62f), Offset(w - 9.5f * u, h * 0.48f), Offset(w - 9.5f * u, h * 0.74f),
            Offset(w * 0.3f, h - 6.5f * u), Offset(w * 0.52f, 6.5f * u))
        sitios.forEachIndexed { i, p0 ->
            val p = Offset(p0.x + (rnd.nextFloat() - 0.5f) * 2f * u, p0.y + (rnd.nextFloat() - 0.5f) * 3f * u)
            drawLine(Color.Black.copy(alpha = 0.3f), p, Offset(p.x + 1.6f * u, p.y + 1.4f * u), 0.4f * u)
            drawLine(m(BLANCO, e, oscuro, 0.3f), p, Offset(p.x - 1.4f * u, p.y + 2f * u), 0.35f * u)
            drawCircle(cabezas[i % cabezas.size], 1.5f * u, p)
            drawCircle(Color.White.copy(alpha = 0.5f), 0.5f * u, Offset(p.x - 0.5f * u, p.y - 0.5f * u))
        }
        // El compás de puntas, arriba a la izquierda.
        val bisagra = Offset(8.2f * u, 5f * u)
        val laton = m(LATON, e, oscuro, 0.2f)
        drawLine(laton, bisagra, Offset(5.4f * u, 20f * u), 0.7f * u, StrokeCap.Round)
        drawLine(laton, bisagra, Offset(11f * u, 19.4f * u), 0.7f * u, StrokeCap.Round)
        drawCircle(laton, 1.3f * u, bisagra)
        drawCircle(m(CAOBA_OSC, e, oscuro, 0.2f), 0.5f * u, bisagra)
        // El catalejo, abajo a la derecha.
        catalejo(Offset(w * 0.66f, h - 5.6f * u), Offset(w - 6f * u, h - 5.6f * u), u, laton, m(CAOBA_OSC, e, oscuro, 0.2f))
    }

    // ---------------------------------------------------------------- Otras vidas

    override fun DrawScope.muebleOtrasVidas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        if (u <= 0f) return
        val oro = oro(oscuro)
        val caoba = m(CAOBA, e, oscuro, 0.35f)
        val paso = w * 0.92f / 5f
        val x0 = w * 0.02f; val x1 = w * 0.98f
        val nichoArriba = h * 0.19f; val nichoAbajo = h * 0.8f
        // El cuerpo de caoba de la vitrina.
        drawRect(caoba.copy(alpha = if (oscuro) 0.85f else 0.75f), Offset(x0, h * 0.03f), Size(x1 - x0, h * 0.94f))
        // La cornisa con su moldura clara y el perlado dorado.
        val cornisa = Rect(x0 - 0.8f * u, h * 0.03f, x1 + 0.8f * u, h * 0.11f)
        drawRect(m(CAOBA_OSC, e, oscuro, 0.3f), cornisa.topLeft, cornisa.size)
        drawRect(m(CAOBA_LUZ, e, oscuro, 0.35f), cornisa.topLeft, Size(cornisa.width, cornisa.height * 0.3f))
        val np = (cornisa.width / (3f * u)).toInt().coerceIn(1, 200)
        for (k in 0 until np) drawCircle(oro, 0.55f * u, Offset(cornisa.left + 1.5f * u + k * 3f * u, cornisa.top + cornisa.height * 0.66f))
        // Un nicho de terciopelo azul por medalla, con su filete de oro, su reflejo y una abeja de oro encima.
        val terciopelo = mezcla(e.fondo, AZUL, if (oscuro) 0.3f else 0.1f)
        repeat(5) { i ->
            val cx = w * 0.04f + paso * (i + 0.5f)
            val nw = paso * 0.86f
            val nicho = Rect(cx - nw / 2, nichoArriba, cx + nw / 2, nichoAbajo)
            drawRect(terciopelo, nicho.topLeft, nicho.size)
            drawRect(oro, nicho.topLeft, nicho.size, style = Stroke(0.5f * u))
            drawRect(oro.copy(alpha = 0.45f), Offset(nicho.left + 1.4f * u, nicho.top + 1.4f * u), Size(nicho.width - 2.8f * u, nicho.height - 2.8f * u), style = Stroke(0.25f * u))
            drawLine(Color.White.copy(alpha = if (oscuro) 0.08f else 0.5f), Offset(nicho.right - nw * 0.3f, nicho.top + 2f * u), Offset(nicho.right - 2f * u, nicho.top + nw * 0.3f), 0.8f * u, StrokeCap.Round)
            abeja(Offset(cx, (cornisa.bottom + nicho.top) / 2 + 0.6f * u), 4.6f * u, oro)
            rombo(Offset(cx, h * 0.885f), 3f * u, 1.6f * u, oro.copy(alpha = 0.85f))
        }
        // Las columnas entre los nichos, con su capitel y su basa de bronce.
        for (k in 0..5) {
            val cx = w * 0.04f + paso * k
            val cw = paso * 0.1f
            drawRect(m(CAOBA_LUZ, e, oscuro, 0.35f), Offset(cx - cw / 2, nichoArriba), Size(cw, nichoAbajo - nichoArriba))
            drawLine(Color.Black.copy(alpha = 0.25f), Offset(cx + cw / 2, nichoArriba), Offset(cx + cw / 2, nichoAbajo), 0.3f * u)
            drawRect(oro, Offset(cx - cw * 0.75f, nichoArriba - 0.4f * u), Size(cw * 1.5f, 1.6f * u))
            drawRect(oro, Offset(cx - cw * 0.75f, nichoAbajo - 1.6f * u), Size(cw * 1.5f, 1.6f * u))
        }
        // El zócalo con su filete dorado.
        drawLine(oro, Offset(x0, nichoAbajo + 1.4f * u), Offset(x1, nichoAbajo + 1.4f * u), 0.5f * u)
        drawLine(oro.copy(alpha = 0.6f), Offset(x0, h * 0.95f), Offset(x1, h * 0.95f), 0.35f * u)
    }

    // ---------------------------------------------------------------- Íconos de mapas

    override fun DrawScope.iconoMapa(id: String, e: EstadoIcono, oscuro: Boolean) {
        val w = size.width; val h = size.height; val c = Offset(w / 2, h / 2); val r = minOf(w, h) * 0.36f
        val g = 1.6f * density
        val trazo = Stroke(g, cap = StrokeCap.Round, join = StrokeJoin.Round)
        when (id) {
            "unesco" -> {
                // El arco de triunfo: el cuerpo, el ático, la cornisa, el gran arco, los relieves y el suelo.
                val cuerpo = Rect(c.x - r * 1.05f, c.y - r * 0.55f, c.x + r * 1.05f, c.y + r * 0.85f)
                val arco = Path().apply {
                    val a = Rect(c.x - r * 0.38f, c.y - r * 0.2f, c.x + r * 0.38f, c.y + r * 0.85f)
                    moveTo(a.left, a.bottom); lineTo(a.left, a.top + a.width / 2)
                    arcTo(Rect(a.left, a.top, a.right, a.top + a.width), 180f, 180f, false)
                    lineTo(a.right, a.bottom); close()
                }
                drawRect(e.c1.copy(alpha = 0.2f), cuerpo.topLeft, cuerpo.size)
                drawPath(arco, e.fondo)
                drawPath(arco, e.c1, style = trazo)
                drawRect(e.c1, cuerpo.topLeft, cuerpo.size, style = trazo)
                drawRect(e.c1, Offset(c.x - r * 1.18f, c.y - r * 0.62f), Size(r * 2.36f, r * 0.12f))
                val atico = Rect(c.x - r * 0.95f, c.y - r * 0.98f, c.x + r * 0.95f, c.y - r * 0.62f)
                drawRect(e.c2, atico.topLeft, atico.size)
                drawRect(e.c1, Offset(c.x - r * 1.05f, atico.top - r * 0.08f), Size(r * 2.1f, r * 0.08f))
                listOf(-0.72f, 0.72f).forEach { k ->
                    drawRect(e.c2, Offset(c.x + k * r - r * 0.17f, c.y - r * 0.32f), Size(r * 0.34f, r * 0.42f), style = Stroke(g * 0.7f))
                    drawRect(e.c1, Offset(c.x + k * r - r * 0.1f, c.y + r * 0.3f), Size(r * 0.2f, r * 0.55f), style = Stroke(g * 0.6f))
                }
                drawRect(e.c1, Offset(c.x - r * 0.08f, c.y - r * 0.24f), Size(r * 0.16f, r * 0.14f))
                drawLine(e.c2, Offset(c.x - r * 1.35f, c.y + r * 0.88f), Offset(c.x + r * 1.35f, c.y + r * 0.88f), g * 1.3f, StrokeCap.Round)
            }
            "rascar" -> {
                // El pergamino de campaña raspado por un sable: la franja descubierta y el sable curvo encima.
                val hojaP = Rect(c.x - r * 1.45f, c.y - r * 0.82f, c.x + r * 1.45f, c.y + r * 0.82f)
                drawRect(e.c2.copy(alpha = 0.22f), hojaP.topLeft, hojaP.size)
                val franja = Path().apply {
                    moveTo(hojaP.left + hojaP.width * 0.18f, hojaP.top); lineTo(hojaP.left + hojaP.width * 0.46f, hojaP.top)
                    lineTo(hojaP.right, hojaP.bottom - hojaP.height * 0.28f); lineTo(hojaP.right, hojaP.bottom)
                    lineTo(hojaP.right - hojaP.width * 0.12f, hojaP.bottom)
                    lineTo(hojaP.left + hojaP.width * 0.5f, hojaP.top + hojaP.height * 0.42f); lineTo(hojaP.left + hojaP.width * 0.42f, hojaP.top + hojaP.height * 0.5f)
                    lineTo(hojaP.left + hojaP.width * 0.3f, hojaP.top + hojaP.height * 0.18f); close()
                }
                drawPath(franja, e.c1)
                drawRect(e.c1, hojaP.topLeft, hojaP.size, style = trazo)
                // El sable: la hoja curva, la guarda y la empuñadura.
                val hojaS = Path().apply { moveTo(c.x - r * 1.05f, c.y - r * 0.95f); quadraticTo(c.x + r * 0.1f, c.y - r * 0.55f, c.x + r * 1.1f, c.y + r * 0.62f) }
                drawPath(hojaS, e.tinta, style = Stroke(g * 1.8f, cap = StrokeCap.Round))
                drawPath(hojaS, e.fondo, style = Stroke(g * 0.6f, cap = StrokeCap.Round))
                val pu = Offset(c.x + r * 1.1f, c.y + r * 0.62f)
                drawLine(e.c2, Offset(pu.x - r * 0.24f, pu.y + r * 0.2f), Offset(pu.x + r * 0.2f, pu.y - r * 0.2f), g * 1.6f, StrokeCap.Round)
                drawLine(e.c2, pu, Offset(pu.x + r * 0.3f, pu.y + r * 0.34f), g * 2f, StrokeCap.Round)
            }
            else -> {
                // Tus lugares: tres abejas imperiales, cada una sobre su punto.
                listOf(Offset(-0.95f, 0.15f) to 0.85f, Offset(0f, -0.15f) to 1.05f, Offset(0.95f, 0.1f) to 0.9f).forEach { (o, k) ->
                    val p = Offset(c.x + o.x * r, c.y + o.y * r)
                    drawCircle(e.c2, r * 0.08f, Offset(p.x, p.y + r * 0.62f * k))
                    abeja(p, r * 1.15f * k, e.c1)
                }
            }
        }
    }

    // ---------------------------------------------------------------- Notas

    override fun DrawScope.tableroNotas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        if (u <= 0f) return
        val oro = oro(oscuro)
        // El marco de caoba con su moldura clara.
        drawRect(m(CAOBA, e, oscuro, 0.35f), Offset.Zero, size)
        drawRect(m(CAOBA_LUZ, e, oscuro, 0.4f), Offset.Zero, Size(w, 1f * u))
        // La seda azul sembrada de abejas, con su filete de oro.
        val seda = Rect(3f * u, 3f * u, w - 3f * u, h - 3f * u)
        drawRect(mezcla(e.fondo, AZUL, if (oscuro) 0.3f else 0.14f), seda.topLeft, seda.size)
        sembrado(seda, 20f * u, 3.4f * u, oro.copy(alpha = if (oscuro) 0.22f else 0.3f))
        drawRect(oro, seda.topLeft, seda.size, style = Stroke(0.5f * u))
        drawRect(Color.Black.copy(alpha = 0.18f), seda.topLeft, Size(seda.width, 1f * u))
        // Las rosetas de bronce en las esquinas y la corona de laurel arriba, al centro.
        listOf(Offset(1.5f * u, 1.5f * u), Offset(w - 1.5f * u, 1.5f * u), Offset(1.5f * u, h - 1.5f * u), Offset(w - 1.5f * u, h - 1.5f * u)).forEach { roseta(it, 1.4f * u, oro) }
        val corona = Offset(w / 2, 4.6f * u)
        drawCircle(m(CAOBA, e, oscuro, 0.35f), 4.4f * u, corona)
        laurel(corona, 3.2f * u, oro, 0.35f * u)
        drawPath(estrella(corona, 1.3f * u, 5, 0.42f), oro)
    }

    /** El pergamino del despacho: claro de día; de noche, a la luz de la vela, pardo oscuro (la tinta se lee en los dos). */
    private fun papel(tipo: String, oscuro: Boolean): Color = when (tipo) {
        "recordatorio" -> if (oscuro) Color(0xFF362D25) else Color(0xFFF3E7CE)
        "proposito" -> if (oscuro) Color(0xFF2F2C2A) else Color(0xFFF5F0E2)
        else -> if (oscuro) Color(0xFF322C24) else Color(0xFFF6EEDA)
    }

    override fun DrawScope.notaTablero(e: EstadoNota, oscuro: Boolean) {
        val r = e.rect
        val k = size.minDimension / 100f
        if (k <= 0f) return
        val papel = papel(e.tipo, oscuro)
        val oro = oro(oscuro)
        val filete = if (oscuro) e.acento else AZUL
        val hoja = Rect(r.left + 0.5f * k, r.top + 1f * k, r.right - 2f * k, r.bottom - 2.5f * k)
        // La sombra, el pergamino y su doble filete (en el margen).
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.18f), Offset(hoja.left + 1.6f * k, hoja.top + 2.2f * k), hoja.size)
        drawRect(papel, hoja.topLeft, hoja.size)
        val m1 = hoja.height * 0.045f; val m2 = hoja.height * 0.075f
        drawRect(filete.copy(alpha = 0.75f), Offset(hoja.left + m1, hoja.top + m1), Size(hoja.width - 2 * m1, hoja.height - 2 * m1), style = Stroke(0.9f * k))
        drawRect(filete.copy(alpha = 0.45f), Offset(hoja.left + m2, hoja.top + m2), Size(hoja.width - 2 * m2, hoja.height - 2 * m2), style = Stroke(0.45f * k))
        // El detalle del tipo, arriba a la derecha: estrella, laurel, escarapela tricolor o la esquina doblada.
        val mc = Offset(hoja.right - hoja.width * 0.085f, hoja.top + hoja.height * 0.1f)
        val s = hoja.height * 0.055f
        when (e.tipo) {
            "idea" -> drawPath(estrella(mc, s * 1.3f, 5, 0.42f), oro)
            "proposito" -> {
                drawRect(if (oscuro) Color(0xFF6F8BFF) else AZUL, Offset(mc.x - s * 1.2f, mc.y - s), Size(s * 0.8f, s * 2f))
                drawRect(BLANCO, Offset(mc.x - s * 0.4f, mc.y - s), Size(s * 0.8f, s * 2f))
                drawRect(if (oscuro) Color(0xFFE05A6A) else CARMESI, Offset(mc.x + s * 0.4f, mc.y - s), Size(s * 0.8f, s * 2f))
                drawRect(e.acento.copy(alpha = 0.4f), Offset(mc.x - s * 1.2f, mc.y - s), Size(s * 2.4f, s * 2f), style = Stroke(0.4f * k))
            }
            "recordatorio" -> {
                val d = hoja.height * 0.2f
                drawPath(Path().apply { moveTo(hoja.right - d, hoja.top); lineTo(hoja.right, hoja.top + d); lineTo(hoja.right - d, hoja.top + d); close() }, mezcla(papel, Color.Black, 0.16f))
                drawPath(Path().apply { moveTo(hoja.right - d, hoja.top); lineTo(hoja.right, hoja.top); lineTo(hoja.right, hoja.top + d); close() }, if (oscuro) Color(0xFF1B2550) else Color(0xFFDCE3F5))
            }
            else -> {
                listOf(-1f, 1f).forEach { sg ->
                    rotate(sg * 35f, mc) { drawOval(oro, Offset(mc.x - s * 0.35f + sg * s * 0.5f, mc.y - s * 0.9f), Size(s * 0.7f, s * 1.6f)) }
                }
                drawLine(oro, Offset(mc.x, mc.y), Offset(mc.x, mc.y + s * 1.1f), 0.5f * k)
            }
        }
        // El sello de lacre con sus cintas si está fijado; si no, el alfiler de latón en la esquina.
        if (e.fijada) {
            val sc = Offset(hoja.center.x, hoja.top + hoja.height * 0.07f)
            val sr = hoja.height * 0.075f
            listOf(-1f, 1f).forEach { sg ->
                drawPath(Path().apply { moveTo(sc.x + sg * sr * 0.2f, sc.y); lineTo(sc.x + sg * sr * 1.9f, sc.y + sr * 0.3f); lineTo(sc.x + sg * sr * 1.6f, sc.y + sr * 0.9f); close() },
                    if (sg < 0) (if (oscuro) Color(0xFF6F8BFF) else AZUL) else (if (oscuro) Color(0xFFE05A6A) else CARMESI))
            }
            drawCircle(Color.Black.copy(alpha = 0.3f), sr, Offset(sc.x + 0.7f * k, sc.y + 0.9f * k))
            drawCircle(LACRE, sr, sc)
            drawCircle(mezcla(LACRE, Color.Black, 0.3f), sr * 0.62f, sc, style = Stroke(0.6f * k))
            drawCircle(mezcla(LACRE, Color.White, 0.3f), sr * 0.22f, Offset(sc.x - sr * 0.3f, sc.y - sr * 0.3f))
        } else {
            val pc = Offset(hoja.left + hoja.width * 0.06f, hoja.top + hoja.height * 0.07f)
            drawLine(Color.Black.copy(alpha = 0.35f), pc, Offset(pc.x + 2.4f * k, pc.y + 2.4f * k), 0.5f * k)
            drawCircle(LATON, hoja.height * 0.035f, pc)
        }
    }

    override fun tintaNota(oscuro: Boolean): Color = if (oscuro) Color(0xFFF1E9D2) else TINTA
}
