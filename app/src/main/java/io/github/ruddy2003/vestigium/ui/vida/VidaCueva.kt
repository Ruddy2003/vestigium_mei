package io.github.ruddy2003.vestigium.ui.vida

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.sin

/**
 * Prehistoria: lo hondo de la cueva, con los materiales de su armario del Ideario, su biblioteca y su sala de los bisontes:
 * la roca con estratos, poros y grietas, la caliza lisa, el ocre rojo y amarillo, el carbón, el hueso, la piel curtida, la
 * madera con corteza atada con tendón, el sílex y el fuego. Los «muebles» son de piedra, hueso, piel, madera y fuego.
 * - Social: la pared del clan. El techo con estalactitas; en la roca, manos en negativo sopladas con ocre, un bisonte y
 *   rayas de cuenta; al centro, el panel de caliza lisa (el nicho) orlado de puntos de ocre, donde el clan pinta a su gente;
 *   abajo, el suelo de tierra con el fogón encendido, las piedras de sentarse con sus pieles, la lámpara de grasa y la leña.
 * - Mapas: la gran laja apoyada sobre dos piedras, vista desde arriba, con la piel estirada y clavada con punzones de hueso
 *   (el nicho); a la izquierda, la hoja de sílex, el punzón, los carbones y las conchas con ocre rojo y amarillo; a la
 *   derecha, la lámpara de grasa encendida, el mortero con su majadera y el pincel de pelo.
 * - Otras vidas: cinco huecos tallados en la roca (el nicho), separados por columnas de calcita; arriba, el friso con un
 *   bisonte, manos y puntos; abajo, la repisa de piedra con muescas de cuenta, el collar de dientes y cuentas, y el suelo.
 * - Íconos: un dolmen con una mano pintada, la laja con el mapa de ocre rascada con un raspador de sílex, y tres cantos
 *   pintados sobre una tira de piel, unidos por un rastro de puntos.
 * - Notas: el bastidor de troncos con corteza atado con tendón, la piel estirada y cosida a él (el nicho) y los dientes y
 *   cuentas colgados del tronco de arriba. Cada nota es una laja de caliza desportillada, colgada de una clavija de hueso;
 *   su tipo se ve en la esquina (la mano soplada, la luna con sus puntos, la punta de sílex o las muescas con su cordel) y,
 *   si está fijada, lleva una franja de ocre rojo y un diente colgado de la clavija.
 */
internal object VidaCueva : DisenoVida {
    private val ROCA = Color(0xFFB8A386)
    private val ROCA_LUZ = Color(0xFFDCCDB2)
    private val ROCA_OSC = Color(0xFF7A6650)
    private val ROCA_HONDA = Color(0xFF2F241B)
    private val CALIZA = Color(0xFFEDE2CC)
    private val OCRE = Color(0xFFA2401F)
    private val OCRE_AMARILLO = Color(0xFFC98A2E)
    private val CARBON = Color(0xFF221A15)
    private val BRASA = Color(0xFFE8804F)
    private val FUEGO = Color(0xFFF4C76E)
    private val FUEGO_LUZ = Color(0xFFFFF0C4)
    private val HUESO = Color(0xFFEDE3CB)
    private val HUESO_OSC = Color(0xFFB9A882)
    private val PIEL = Color(0xFFC49A6C)
    private val PIEL_CLARA = Color(0xFFE6CFAE)
    private val PIEL_OSC = Color(0xFF8A6440)
    private val PELO = Color(0xFF6B4A2E)
    private val MADERA = Color(0xFF6E4E30)
    private val MADERA_LUZ = Color(0xFF9A7448)
    private val CORTEZA = Color(0xFF4A3524)
    private val TENDON = Color(0xFFD8C49A)
    private val SILEX = Color(0xFF6F6358)
    private val TIERRA = Color(0xFF6E5A45)
    private val TIERRA_OSC = Color(0xFF4A3B2D)
    private val NOCHE = Color(0xFF1E150E)

    // ---------------------------------------------------------------- Ayudas

    /** Un número estable de 0 a 1: cada piedra y cada grieta tienen sus irregularidades, siempre las mismas. */
    private fun azar(semilla: Int, k: Int): Float {
        var x = semilla * 374761393 + k * 668265263
        x = (x xor (x ushr 13)) * 1274126177
        x = x xor (x ushr 16)
        return (x and 0x7fffffff) / 2147483647f
    }

    private fun mezcla(a: Color, b: Color, f: Float): Color = lerp(a, b, f.coerceIn(0f, 1f))

    /** De noche la cueva solo la alumbra el fuego: la roca, la tierra y la madera se hunden en la sombra. */
    private fun t(c: Color, oscuro: Boolean, cuanto: Float = 0.58f): Color = if (oscuro) mezcla(c, NOCHE, cuanto) else c

    /** El color sólido de un nicho: el material llevado a la luminancia de la tarjeta. */
    private fun nicho(e: EstadoMueble, oscuro: Boolean, material: Color): Color =
        if (oscuro) mezcla(e.fondo, material, 0.12f) else mezcla(e.fondo, material, 0.42f)

    private fun DrawScope.caja(r: Rect) = Rect(size.width * r.left, size.height * r.top, size.width * r.right, size.height * r.bottom)

    private fun Rect.crece(d: Float) = Rect(left - d, top - d, right + d, bottom + d)

    /** Una mancha radial suave (la luz del fuego, el ocre soplado). */
    private fun DrawScope.halo(c: Offset, r: Float, color: Color) =
        drawCircle(Brush.radialGradient(listOf(color, color.copy(alpha = 0f)), c, r.coerceAtLeast(1f)), r, c)

    /** La pared de roca: más oscura arriba y abajo, con manchas de luz y de sombra, estratos, grietas y poros. */
    private fun DrawScope.pared(r: Rect, oscuro: Boolean, semilla: Int, u: Float) {
        drawRect(Brush.verticalGradient(listOf(t(ROCA_OSC, oscuro), t(ROCA, oscuro), t(ROCA_LUZ, oscuro), t(ROCA, oscuro), t(ROCA_OSC, oscuro)), r.top, r.bottom), r.topLeft, r.size)
        clipRect(r.left, r.top, r.right, r.bottom) {
            val n = (r.width * r.height / (140f * u * u)).toInt().coerceIn(16, 90)
            for (k in 0 until n) {
                val c = Offset(r.left + r.width * azar(semilla + 11, k), r.top + r.height * azar(semilla + 12, k))
                halo(c, (4f + 10f * azar(semilla + 13, k)) * u, (if (k % 3 == 0) t(ROCA_LUZ, oscuro) else t(ROCA_OSC, oscuro)).copy(alpha = 0.3f))
            }
            // Los estratos: ondas horizontales de la caliza.
            var y = r.top + 4f * u; var k = 0
            while (y < r.bottom) {
                val p = Path(); val amp = (0.4f + 0.8f * azar(semilla + 20, k)) * u; val fase = azar(semilla + 21, k) * 6.28f
                for (s in 0..20) {
                    val x = r.left + r.width * s / 20
                    val yy = y + amp * sin(fase + s * 0.7f)
                    if (s == 0) p.moveTo(x, yy) else p.lineTo(x, yy)
                }
                drawPath(p, t(ROCA_OSC, oscuro).copy(alpha = 0.35f), style = Stroke(0.35f * u))
                drawPath(p.apply { translate(Offset(0f, 0.45f * u)) }, t(ROCA_LUZ, oscuro).copy(alpha = 0.3f), style = Stroke(0.25f * u))
                y += (7f + 5f * azar(semilla + 22, k)) * u; k++
            }
            // Las grietas.
            for (g in 0 until 4) {
                val p = Path(); var x = r.left + r.width * azar(semilla + 31, g); var yy = r.top + r.height * 0.2f * azar(semilla + 32, g)
                p.moveTo(x, yy)
                for (s in 0 until 7) { x += (azar(semilla + 33 + g, s) - 0.5f) * 6f * u; yy += (3f + 4f * azar(semilla + 34 + g, s)) * u; p.lineTo(x, yy) }
                drawPath(p, t(ROCA_HONDA, oscuro, 0.3f).copy(alpha = 0.45f), style = Stroke(0.35f * u, cap = StrokeCap.Round))
            }
            // Los poros.
            for (j in 0 until n * 2) {
                val c = Offset(r.left + r.width * azar(semilla + 41, j), r.top + r.height * azar(semilla + 42, j))
                drawCircle(t(ROCA_HONDA, oscuro, 0.3f).copy(alpha = 0.25f), (0.2f + 0.3f * azar(semilla + 43, j)) * u, c)
            }
        }
    }

    /** El techo de la cueva: un borde de roca honda, irregular, con estalactitas que cuelgan y gotean. */
    private fun DrawScope.techo(w: Float, alto: Float, oscuro: Boolean, semilla: Int, u: Float, cuantas: Int) {
        val borde = Path().apply {
            moveTo(0f, 0f); lineTo(w, 0f)
            for (k in 14 downTo 0) lineTo(w * k / 14, alto * (0.35f + 0.25f * azar(semilla + 50, k)))
            close()
        }
        drawPath(borde, Brush.verticalGradient(listOf(t(ROCA_HONDA, oscuro, 0.3f), t(ROCA_OSC, oscuro)), 0f, alto * 0.6f))
        drawPath(borde, t(ROCA_LUZ, oscuro).copy(alpha = 0.35f), style = Stroke(0.4f * u))
        for (k in 0 until cuantas) {
            val x = w * (k + 0.2f + 0.6f * azar(semilla + 51, k)) / cuantas
            val y0 = alto * 0.35f
            val largo = alto * (0.35f + 0.6f * azar(semilla + 52, k))
            val ancho = (2.2f + 2.4f * azar(semilla + 53, k)) * u
            val e = Path().apply { moveTo(x - ancho / 2, y0); quadraticTo(x - ancho * 0.12f, y0 + largo * 0.6f, x, y0 + largo); quadraticTo(x + ancho * 0.12f, y0 + largo * 0.6f, x + ancho / 2, y0); close() }
            drawPath(e, Brush.horizontalGradient(listOf(t(ROCA_OSC, oscuro), t(ROCA_LUZ, oscuro), t(ROCA_OSC, oscuro)), x - ancho / 2, x + ancho / 2))
            if (k % 2 == 0) drawCircle(t(ROCA_LUZ, oscuro).copy(alpha = 0.8f), 0.45f * u, Offset(x, y0 + largo + 1.2f * u))
        }
    }

    /** El suelo de tierra, con cantos y algún resto. */
    private fun DrawScope.suelo(r: Rect, oscuro: Boolean, semilla: Int, u: Float) {
        drawRect(Brush.verticalGradient(listOf(t(TIERRA, oscuro), t(TIERRA_OSC, oscuro)), r.top, r.bottom), r.topLeft, r.size)
        drawRect(Color.Black.copy(alpha = 0.3f), r.topLeft, Size(r.width, 0.4f * u))
        val n = (r.width / (5f * u)).toInt()
        for (k in 0 until n) {
            val c = Offset(r.left + r.width * azar(semilla + 61, k), r.top + r.height * (0.15f + 0.8f * azar(semilla + 62, k)))
            val rr = (0.5f + 1.2f * azar(semilla + 63, k)) * u
            drawOval(t(if (k % 2 == 0) ROCA_OSC else ROCA, oscuro), Offset(c.x - rr, c.y - rr * 0.6f), Size(2 * rr, 1.2f * rr))
            drawOval(t(ROCA_LUZ, oscuro).copy(alpha = 0.5f), Offset(c.x - rr * 0.6f, c.y - rr * 0.55f), Size(rr * 1.1f, rr * 0.4f))
        }
    }

    /** Una llama: el contorno de brasa, el cuerpo amarillo y el corazón claro. */
    private fun DrawScope.llama(base: Offset, ancho: Float, alto: Float, ladeo: Float = 0f) {
        fun forma(a: Float, h: Float) = Path().apply {
            moveTo(base.x - a, base.y)
            cubicTo(base.x - a * 1.2f, base.y - h * 0.45f, base.x + ladeo - a * 0.3f, base.y - h * 0.65f, base.x + ladeo, base.y - h)
            cubicTo(base.x + ladeo + a * 0.3f, base.y - h * 0.65f, base.x + a * 1.2f, base.y - h * 0.45f, base.x + a, base.y)
            close()
        }
        drawPath(forma(ancho, alto), Brush.verticalGradient(listOf(BRASA, OCRE), base.y - alto, base.y))
        drawPath(forma(ancho * 0.66f, alto * 0.72f), FUEGO)
        drawPath(forma(ancho * 0.32f, alto * 0.4f), FUEGO_LUZ)
    }

    /** El fogón: el anillo de piedras, los leños cruzados, las brasas y tres llamas. [s] es el radio del anillo. */
    private fun DrawScope.fogon(c: Offset, s: Float, oscuro: Boolean, u: Float) {
        drawOval(Color.Black.copy(alpha = 0.35f), Offset(c.x - s * 1.4f, c.y - s * 0.3f), Size(s * 2.8f, s * 0.9f))
        drawOval(CARBON, Offset(c.x - s, c.y - s * 0.3f), Size(2 * s, s * 0.6f))
        listOf(-25f, 25f).forEach { g ->
            rotate(g, c) {
                drawRoundRect(t(MADERA, oscuro, 0.3f), Offset(c.x - s * 0.9f, c.y - s * 0.12f), Size(s * 1.8f, s * 0.24f), CornerRadius(s * 0.12f))
                drawRect(CARBON, Offset(c.x - s * 0.25f, c.y - s * 0.12f), Size(s * 0.5f, s * 0.24f))
            }
        }
        for (k in 0 until 9) drawCircle(if (k % 2 == 0) BRASA else FUEGO, (0.35f + 0.4f * azar(71, k)) * u, Offset(c.x + (azar(72, k) - 0.5f) * s * 1.4f, c.y + (azar(73, k) - 0.5f) * s * 0.3f))
        llama(Offset(c.x - s * 0.35f, c.y), s * 0.32f, s * 1.3f, -s * 0.15f)
        llama(Offset(c.x + s * 0.38f, c.y), s * 0.3f, s * 1.1f, s * 0.15f)
        llama(Offset(c.x, c.y + s * 0.05f), s * 0.42f, s * 1.9f)
        // Las piedras del frente del anillo.
        for (k in 0 until 7) {
            val a = 0.25f + k * 0.45f
            val p = Offset(c.x + s * 1.05f * cos(a + 0.0f) * (if (k % 2 == 0) 1f else 0.98f) - 0f, c.y + s * 0.34f * sin(a))
            val q = Offset(c.x - s * 1.05f + (s * 2.1f) * k / 6f, p.y.coerceAtLeast(c.y + s * 0.12f))
            val rw = s * 0.32f; val rh = s * 0.2f
            drawOval(t(ROCA_OSC, oscuro, 0.4f), Offset(q.x - rw, q.y - rh), Size(2 * rw, 2 * rh))
            drawOval(t(ROCA_LUZ, oscuro, 0.35f).copy(alpha = 0.7f), Offset(q.x - rw * 0.6f, q.y - rh * 0.9f), Size(rw * 1.1f, rh * 0.7f))
        }
    }

    /** Una mano en negativo: el ocre soplado alrededor y la silueta de la mano en el color de la roca [roca]. */
    private fun DrawScope.manoNegativa(c: Offset, r: Float, giro: Float, roca: Color, ocre: Color, fuerza: Float = 0.55f) {
        halo(c, r * 1.8f, ocre.copy(alpha = fuerza))
        halo(c, r * 1.15f, ocre.copy(alpha = fuerza * 0.5f))
        rotate(giro, c) {
            drawOval(roca, Offset(c.x - r * 0.42f, c.y - r * 0.15f), Size(r * 0.84f, r * 0.8f))
            listOf(-0.32f to 0.55f, -0.12f to 0.72f, 0.08f to 0.75f, 0.27f to 0.62f).forEach { (dx, l) ->
                drawLine(roca, Offset(c.x + r * dx, c.y), Offset(c.x + r * dx * 1.15f, c.y - r * l), r * 0.17f, StrokeCap.Round)
            }
            drawLine(roca, Offset(c.x - r * 0.35f, c.y + r * 0.2f), Offset(c.x - r * 0.72f, c.y - r * 0.1f), r * 0.19f, StrokeCap.Round)
            drawRect(roca, Offset(c.x - r * 0.28f, c.y + r * 0.5f), Size(r * 0.56f, r * 0.6f))
        }
    }

    /** Un bisonte de perfil, de [ancho] de ancho con el centro en [c]; [lado] = 1 mira a la derecha, −1 a la izquierda. */
    private fun bisonte(c: Offset, ancho: Float, lado: Float): Path = Path().apply {
        val a = ancho
        fun p(x: Float, y: Float) = Offset(c.x + lado * x * a, c.y + y * a)
        fun m(x: Float, y: Float) { val q = p(x, y); moveTo(q.x, q.y) }
        fun l(x: Float, y: Float) { val q = p(x, y); lineTo(q.x, q.y) }
        fun q2(x1: Float, y1: Float, x2: Float, y2: Float) { val q = p(x1, y1); val r = p(x2, y2); quadraticTo(q.x, q.y, r.x, r.y) }
        fun c3(x1: Float, y1: Float, x2: Float, y2: Float, x3: Float, y3: Float) {
            val q = p(x1, y1); val r = p(x2, y2); val s = p(x3, y3); cubicTo(q.x, q.y, r.x, r.y, s.x, s.y)
        }
        m(-0.46f, -0.03f)
        c3(-0.3f, -0.12f, -0.06f, -0.13f, 0.08f, -0.22f)
        c3(0.18f, -0.29f, 0.3f, -0.26f, 0.35f, -0.13f)
        q2(0.45f, -0.11f, 0.5f, 0.0f)
        l(0.49f, 0.09f)
        q2(0.42f, 0.15f, 0.34f, 0.11f)
        l(0.31f, 0.22f); l(0.27f, 0.23f); l(0.24f, 0.11f)
        q2(0.0f, 0.13f, -0.24f, 0.08f)
        l(-0.27f, 0.22f); l(-0.32f, 0.22f); l(-0.37f, 0.06f)
        q2(-0.47f, 0.05f, -0.46f, -0.03f)
        close()
        m(0.37f, -0.13f); q2(0.41f, -0.22f, 0.47f, -0.2f); q2(0.42f, -0.17f, 0.4f, -0.11f); close()
        m(-0.46f, -0.02f); q2(-0.53f, 0.04f, -0.5f, 0.12f); q2(-0.49f, 0.04f, -0.45f, 0.0f); close()
    }

    /** Un bisonte pintado: el relleno de ocre y el contorno y la giba a carbón. */
    private fun DrawScope.bisontePintado(c: Offset, ancho: Float, lado: Float, oscuro: Boolean, u: Float) {
        val b = bisonte(c, ancho, lado)
        drawPath(b, mezcla(OCRE, OCRE_AMARILLO, 0.3f).copy(alpha = if (oscuro) 0.55f else 0.6f))
        clipPath(b) { drawCircle(CARBON.copy(alpha = 0.55f), ancho * 0.2f, Offset(c.x + lado * ancho * 0.24f, c.y - ancho * 0.2f)) }
        drawPath(b, CARBON.copy(alpha = if (oscuro) 0.7f else 0.8f), style = Stroke(0.45f * u, cap = StrokeCap.Round))
    }

    /** Una fila de puntos de ocre, pintados con el dedo. */
    private fun DrawScope.puntos(a: Offset, b: Offset, n: Int, r: Float, color: Color) {
        for (i in 0 until n) {
            val f = if (n == 1) 0.5f else i / (n - 1f)
            val p = a + (b - a) * f
            drawCircle(color, r * (0.8f + 0.4f * azar(n, i)), p + Offset(0f, if (i % 2 == 0) 0f else r * 0.6f))
        }
    }

    /** Muescas de cuenta grabadas en grupos de cinco: cuatro rayas y la quinta cruzada; con su filo de luz. */
    private fun DrawScope.muescas(o: Offset, n: Int, alto: Float, paso: Float, color: Color, luz: Color, u: Float) {
        var x = o.x
        for (i in 0 until n) {
            if (i % 5 == 4) {
                val x0 = x - paso * 4.2f
                drawLine(luz, Offset(x0 + 0.25f * u, o.y + alto * 0.35f + 0.25f * u), Offset(x - paso * 0.6f + 0.25f * u, o.y - alto * 0.35f + 0.25f * u), 0.4f * u, StrokeCap.Round)
                drawLine(color, Offset(x0, o.y + alto * 0.35f), Offset(x - paso * 0.6f, o.y - alto * 0.35f), 0.45f * u, StrokeCap.Round)
                x += paso * 0.8f
            } else {
                drawLine(luz, Offset(x + 0.25f * u, o.y - alto / 2), Offset(x + 0.25f * u, o.y + alto / 2), 0.4f * u, StrokeCap.Round)
                drawLine(color, Offset(x, o.y - alto / 2), Offset(x, o.y + alto / 2), 0.45f * u, StrokeCap.Round)
                x += paso
            }
        }
    }

    /** El contorno de una laja en [r]: el borde desportillado, siempre el mismo para cada [semilla]. */
    private fun laja(r: Rect, semilla: Int, hondo: Float = 0.05f, puntos: Int = 22): Path = Path().apply {
        for (k in 0 until puntos) {
            val q = k / puntos.toFloat()
            val (x, y) = when {
                q < 0.25f -> r.left + r.width * (q / 0.25f) to r.top
                q < 0.5f -> r.right to r.top + r.height * ((q - 0.25f) / 0.25f)
                q < 0.75f -> r.right - r.width * ((q - 0.5f) / 0.25f) to r.bottom
                else -> r.left to r.bottom - r.height * ((q - 0.75f) / 0.25f)
            }
            val adentro = minOf(r.width, r.height) * (0.015f + hondo * azar(semilla, k)) * (if (k % 4 == 0) 1.8f else 1f)
            val px = x + (if (x <= r.left + 0.5f) adentro else if (x >= r.right - 0.5f) -adentro else 0f)
            val py = y + (if (y <= r.top + 0.5f) adentro else if (y >= r.bottom - 0.5f) -adentro else 0f)
            if (k == 0) moveTo(px, py) else lineTo(px, py)
        }
        close()
    }

    /** Una piel estirada alrededor de [r]: con las solapas de las patas en las esquinas, el cuello arriba y la cola abajo. */
    private fun piel(r: Rect, s: Float, semilla: Int): Path = Path().apply {
        val n = 64
        for (k in 0 until n) {
            val q = k / n.toFloat()
            val (p, nor) = when {
                q < 0.25f -> Offset(r.left + r.width * (q / 0.25f), r.top) to Offset(0f, -1f)
                q < 0.5f -> Offset(r.right, r.top + r.height * ((q - 0.25f) / 0.25f)) to Offset(1f, 0f)
                q < 0.75f -> Offset(r.right - r.width * ((q - 0.5f) / 0.25f), r.bottom) to Offset(0f, 1f)
                else -> Offset(r.left, r.bottom - r.height * ((q - 0.75f) / 0.25f)) to Offset(-1f, 0f)
            }
            fun cerca(c: Float, ancho: Float): Float { var d = abs(q - c); if (d > 0.5f) d = 1f - d; return exp(-(d / ancho) * (d / ancho)) }
            val esquina = listOf(0f, 0.25f, 0.5f, 0.75f).maxOf { cerca(it, 0.022f) }
            val medio = maxOf(cerca(0.125f, 0.03f), cerca(0.625f, 0.025f))
            val fuera = s * (0.2f + 0.25f * azar(semilla, k)) + s * 1.6f * esquina + s * 0.9f * medio
            val x = p.x + nor.x * fuera; val y = p.y + nor.y * fuera
            if (k == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
    }

    /** Un hueso: la caña y las dos cabezas. */
    private fun DrawScope.hueso(a: Offset, b: Offset, g: Float, oscuro: Boolean) {
        drawLine(Color.Black.copy(alpha = 0.3f), a + Offset(g * 0.3f, g * 0.4f), b + Offset(g * 0.3f, g * 0.4f), g, StrokeCap.Round)
        val d = (b - a); val l = d.getDistance().coerceAtLeast(0.01f); val p = Offset(-d.y / l, d.x / l)
        listOf(a, b).forEach { e ->
            drawCircle(t(HUESO_OSC, oscuro, 0.35f), g * 0.62f, e + p * (g * 0.4f))
            drawCircle(t(HUESO_OSC, oscuro, 0.35f), g * 0.62f, e - p * (g * 0.4f))
        }
        drawLine(t(HUESO, oscuro, 0.35f), a, b, g, StrokeCap.Round)
        listOf(a, b).forEach { e ->
            drawCircle(t(HUESO, oscuro, 0.35f), g * 0.52f, e + p * (g * 0.4f))
            drawCircle(t(HUESO, oscuro, 0.35f), g * 0.52f, e - p * (g * 0.4f))
        }
        drawLine(Color.White.copy(alpha = 0.35f), a + p * (g * 0.2f), b + p * (g * 0.2f), g * 0.25f, StrokeCap.Round)
    }

    /** Un tronco con corteza, de [r]; [horizontal] dice a lo largo de qué va. Con los anillos en las puntas si [cortes]. */
    private fun DrawScope.tronco(r: Rect, horizontal: Boolean, oscuro: Boolean, semilla: Int, u: Float, cortes: Boolean = true) {
        val g = if (horizontal) Brush.verticalGradient(listOf(t(MADERA_LUZ, oscuro, 0.5f), t(MADERA, oscuro, 0.5f), t(CORTEZA, oscuro, 0.5f)), r.top, r.bottom)
        else Brush.horizontalGradient(listOf(t(MADERA_LUZ, oscuro, 0.5f), t(MADERA, oscuro, 0.5f), t(CORTEZA, oscuro, 0.5f)), r.left, r.right)
        val radio = (if (horizontal) r.height else r.width) / 2
        drawRoundRect(g, r.topLeft, r.size, CornerRadius(radio * 0.5f))
        // La corteza: grietas a lo largo y algún nudo.
        clipRect(r.left, r.top, r.right, r.bottom) {
            val largo = if (horizontal) r.width else r.height
            val n = (largo / (3f * u)).toInt().coerceAtLeast(2)
            for (k in 0 until n) {
                val a = largo * azar(semilla, k); val l = (2f + 5f * azar(semilla + 1, k)) * u
                val o = radio * 2 * (0.15f + 0.7f * azar(semilla + 2, k))
                if (horizontal) drawLine(t(CORTEZA, oscuro, 0.4f).copy(alpha = 0.8f), Offset(r.left + a, r.top + o), Offset(r.left + a + l, r.top + o + (azar(semilla + 3, k) - 0.5f) * u), 0.35f * u, StrokeCap.Round)
                else drawLine(t(CORTEZA, oscuro, 0.4f).copy(alpha = 0.8f), Offset(r.left + o, r.top + a), Offset(r.left + o + (azar(semilla + 3, k) - 0.5f) * u, r.top + a + l), 0.35f * u, StrokeCap.Round)
            }
            for (k in 0 until 2) {
                val a = largo * (0.25f + 0.5f * azar(semilla + 9, k))
                val c = if (horizontal) Offset(r.left + a, r.center.y) else Offset(r.center.x, r.top + a)
                drawOval(t(CORTEZA, oscuro, 0.4f), Offset(c.x - radio * 0.4f, c.y - radio * 0.3f), Size(radio * 0.8f, radio * 0.6f))
                drawOval(t(MADERA_LUZ, oscuro, 0.4f).copy(alpha = 0.6f), Offset(c.x - radio * 0.2f, c.y - radio * 0.15f), Size(radio * 0.4f, radio * 0.3f))
            }
        }
        if (cortes) {
            val ends = if (horizontal) listOf(Offset(r.left + radio * 0.4f, r.center.y), Offset(r.right - radio * 0.4f, r.center.y)) else listOf(Offset(r.center.x, r.top + radio * 0.4f), Offset(r.center.x, r.bottom - radio * 0.4f))
            ends.forEach { c ->
                val sz = if (horizontal) Size(radio * 0.8f, radio * 2f) else Size(radio * 2f, radio * 0.8f)
                drawOval(t(Color(0xFFD9B88A), oscuro, 0.45f), Offset(c.x - sz.width / 2, c.y - sz.height / 2), sz)
                drawOval(t(MADERA, oscuro, 0.45f), Offset(c.x - sz.width * 0.3f, c.y - sz.height * 0.3f), Size(sz.width * 0.6f, sz.height * 0.6f), style = Stroke(0.25f * u))
                drawOval(t(CORTEZA, oscuro, 0.45f), Offset(c.x - sz.width / 2, c.y - sz.height / 2), sz, style = Stroke(0.4f * u))
            }
        }
    }

    /** Una atadura de tendón en cruz, en [c], de lado [s]. */
    private fun DrawScope.atadura(c: Offset, s: Float, oscuro: Boolean, u: Float) {
        for (k in -1..1) {
            val d = k * s * 0.24f
            drawLine(t(CORTEZA, oscuro, 0.3f), Offset(c.x - s / 2 + d, c.y - s / 2 - d) + Offset(0.2f * u, 0.3f * u), Offset(c.x + s / 2 + d, c.y + s / 2 - d) + Offset(0.2f * u, 0.3f * u), 0.9f * u)
            drawLine(t(TENDON, oscuro, 0.3f), Offset(c.x - s / 2 + d, c.y - s / 2 - d), Offset(c.x + s / 2 + d, c.y + s / 2 - d), 0.7f * u, StrokeCap.Round)
        }
        for (k in -1..1) {
            val d = k * s * 0.24f
            drawLine(t(CORTEZA, oscuro, 0.3f), Offset(c.x + s / 2 + d, c.y - s / 2 + d) + Offset(0.2f * u, 0.3f * u), Offset(c.x - s / 2 + d, c.y + s / 2 + d) + Offset(0.2f * u, 0.3f * u), 0.9f * u)
            drawLine(t(mezcla(TENDON, Color.White, 0.15f), oscuro, 0.3f), Offset(c.x + s / 2 + d, c.y - s / 2 + d), Offset(c.x - s / 2 + d, c.y + s / 2 + d), 0.7f * u, StrokeCap.Round)
        }
    }

    /** Una lámpara de grasa: la piedra ahuecada con su mango, la grasa y la llamita con su halo. */
    private fun DrawScope.lampara(c: Offset, s: Float, oscuro: Boolean, u: Float) {
        halo(c - Offset(0f, s * 0.8f), s * 5f, FUEGO.copy(alpha = if (oscuro) 0.45f else 0.3f))
        drawOval(Color.Black.copy(alpha = 0.35f), Offset(c.x - s * 1.2f, c.y + s * 0.1f), Size(s * 2.6f, s * 0.6f))
        drawRoundRect(t(ROCA_OSC, oscuro, 0.4f), Offset(c.x + s * 0.6f, c.y - s * 0.35f), Size(s * 1f, s * 0.5f), CornerRadius(s * 0.25f))
        drawArc(Brush.verticalGradient(listOf(t(ROCA_LUZ, oscuro, 0.4f), t(ROCA_OSC, oscuro, 0.4f)), c.y - s * 0.4f, c.y + s * 0.4f), 0f, 180f, true, Offset(c.x - s, c.y - s * 0.6f), Size(2 * s, s * 1.2f))
        drawOval(t(ROCA, oscuro, 0.4f), Offset(c.x - s, c.y - s * 0.3f), Size(2 * s, s * 0.6f))
        drawOval(t(Color(0xFFE8D8A8), oscuro, 0.3f), Offset(c.x - s * 0.75f, c.y - s * 0.2f), Size(s * 1.5f, s * 0.4f))
        llama(Offset(c.x - s * 0.2f, c.y - s * 0.05f), s * 0.25f, s * 1.1f)
    }

    /** Una concha abierta con pigmento: el abanico con sus costillas y la pasta de color adentro. */
    private fun DrawScope.concha(c: Offset, s: Float, pigmento: Color, giro: Float, oscuro: Boolean, u: Float) {
        rotate(giro, c) {
            drawOval(Color.Black.copy(alpha = 0.3f), Offset(c.x - s + 0.4f * u, c.y - s * 0.85f + 0.6f * u), Size(2 * s, 1.7f * s))
            drawOval(t(Color(0xFFE9DCC6), oscuro, 0.35f), Offset(c.x - s, c.y - s * 0.85f), Size(2 * s, 1.7f * s))
            for (k in 0 until 7) {
                val a = Math.toRadians(200.0 + k * 23.0)
                drawLine(t(HUESO_OSC, oscuro, 0.35f), Offset(c.x, c.y + s * 0.7f), Offset(c.x + s * cos(a).toFloat(), c.y + s * 0.85f * sin(a).toFloat()), 0.25f * u)
            }
            drawOval(t(pigmento, oscuro, 0.2f), Offset(c.x - s * 0.62f, c.y - s * 0.45f), Size(s * 1.24f, s * 0.95f))
            drawOval(Color.White.copy(alpha = 0.25f), Offset(c.x - s * 0.4f, c.y - s * 0.35f), Size(s * 0.5f, s * 0.25f))
            drawRect(t(Color(0xFFE9DCC6), oscuro, 0.35f), Offset(c.x - s * 0.25f, c.y + s * 0.65f), Size(s * 0.5f, s * 0.3f))
        }
    }

    /** Una hoja de sílex tallada: la forma de hoja de laurel, las facetas de la talla y el filo claro. */
    private fun DrawScope.silex(a: Offset, b: Offset, ancho: Float, oscuro: Boolean, u: Float, color: Color = SILEX) {
        val d = b - a; val l = d.getDistance().coerceAtLeast(0.01f); val p = Offset(-d.y / l, d.x / l)
        val m1 = a + d * 0.35f; val m2 = a + d * 0.7f
        val forma = Path().apply {
            moveTo(a.x, a.y)
            quadraticTo((m1 + p * ancho).x, (m1 + p * ancho).y, b.x, b.y)
            quadraticTo((m2 - p * ancho).x, (m2 - p * ancho).y, a.x, a.y)
            close()
        }
        translate(0.4f * u, 0.6f * u) { drawPath(forma, Color.Black.copy(alpha = 0.3f)) }
        drawPath(forma, Brush.linearGradient(listOf(t(mezcla(color, Color.White, 0.3f), oscuro, 0.35f), t(color, oscuro, 0.35f), t(mezcla(color, Color.Black, 0.3f), oscuro, 0.35f)), a + p * ancho, a - p * ancho))
        clipPath(forma) {
            for (k in 1..5) {
                val q = a + d * (k / 6f)
                drawLine(Color.Black.copy(alpha = 0.25f), q + p * ancho, q + d * 0.06f, 0.25f * u)
                drawLine(Color.White.copy(alpha = 0.2f), q - p * ancho, q + d * 0.06f, 0.2f * u)
            }
        }
        drawPath(forma, t(mezcla(color, Color.Black, 0.45f), oscuro, 0.35f), style = Stroke(0.25f * u))
    }

    /** Un canto rodado pintado con ocre: rayas o puntos según [k]. */
    private fun DrawScope.canto(c: Offset, s: Float, k: Int, ocre: Color, oscuro: Boolean, u: Float) {
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(c.x - s + 0.5f * u, c.y - s * 0.7f + 0.8f * u), Size(2 * s, 1.4f * s))
        drawOval(Brush.radialGradient(listOf(t(ROCA_LUZ, oscuro, 0.3f), t(ROCA, oscuro, 0.3f), t(ROCA_OSC, oscuro, 0.3f)), c - Offset(s * 0.3f, s * 0.3f), s * 1.4f), Offset(c.x - s, c.y - s * 0.7f), Size(2 * s, 1.4f * s))
        when (k % 3) {
            0 -> for (i in -1..1) drawLine(ocre, Offset(c.x + i * s * 0.4f, c.y - s * 0.4f), Offset(c.x + i * s * 0.4f, c.y + s * 0.4f), s * 0.18f, StrokeCap.Round)
            1 -> for (i in 0 until 5) { val a = i * 1.256f; drawCircle(ocre, s * 0.13f, c + Offset(cos(a), sin(a) * 0.7f) * (s * 0.5f)) };
            else -> { drawCircle(ocre, s * 0.3f, c); drawCircle(ocre, s * 0.5f, c, style = Stroke(s * 0.1f)) }
        }
        drawOval(Color.White.copy(alpha = 0.3f), Offset(c.x - s * 0.6f, c.y - s * 0.55f), Size(s * 0.6f, s * 0.25f))
    }

    // ---------------------------------------------------------------- Nichos

    override fun area(m: MuebleVida): Rect = when (m) {
        MuebleVida.SOCIAL -> Rect(0.15f, 0.18f, 0.85f, 0.78f)
        MuebleVida.MAPAS -> Rect(0.18f, 0.12f, 0.82f, 0.74f)
        MuebleVida.OTRAS_VIDAS -> Rect(0.05f, 0.25f, 0.95f, 0.79f)
        MuebleVida.NOTAS -> Rect(0.08f, 0.15f, 0.92f, 0.86f)
    }

    // ---------------------------------------------------------------- Social

    override fun DrawScope.muebleSocial(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val n = caja(area(MuebleVida.SOCIAL))
        val piso = h * 0.86f
        val roca = t(ROCA, oscuro)
        // La pared de roca y el techo con estalactitas.
        pared(Rect(0f, 0f, w, piso), oscuro, e.semilla, u)
        techo(w, h * 0.13f, oscuro, e.semilla, u, (w / (9f * u)).toInt())
        // Lo que el clan pintó alrededor del panel: manos sopladas, el bisonte, puntos y rayas de cuenta.
        val izq = n.left * 0.5f; val der = (w + n.right) / 2
        manoNegativa(Offset(izq, h * 0.3f), 5.4f * u, -18f, roca, OCRE, if (oscuro) 0.5f else 0.6f)
        manoNegativa(Offset(izq + 1.5f * u, h * 0.56f), 4.8f * u, 12f, roca, mezcla(OCRE, OCRE_AMARILLO, 0.5f), if (oscuro) 0.45f else 0.5f)
        puntos(Offset(izq - 3f * u, h * 0.72f), Offset(izq + 4f * u, h * 0.72f), 4, 0.8f * u, OCRE.copy(alpha = 0.8f))
        bisontePintado(Offset(der, h * 0.32f), minOf((w - n.right) * 0.92f, 20f * u), -1f, oscuro, u)
        manoNegativa(Offset(der, h * 0.56f), 4.8f * u, 20f, roca, OCRE, if (oscuro) 0.45f else 0.55f)
        muescas(Offset(der - 4f * u, h * 0.72f), 5, 3.4f * u, 1.6f * u, CARBON.copy(alpha = 0.7f), t(ROCA_LUZ, oscuro).copy(alpha = 0.5f), u)
        // La luz del fogón en la pared, antes del panel para que no lo tiña.
        val fuego = Offset(w * 0.5f, h * 0.955f)
        halo(fuego - Offset(0f, 6f * u), w * (if (oscuro) 0.45f else 0.32f), FUEGO.copy(alpha = if (oscuro) 0.35f else 0.18f))
        // El panel de caliza lisa, orlado de puntos de ocre; su campo es el nicho.
        val panel = laja(n.crece(3.6f * u), e.semilla + 3, 0.035f, 30)
        translate(0.8f * u, 1.2f * u) { drawPath(panel, Color.Black.copy(alpha = 0.35f)) }
        drawPath(panel, Brush.verticalGradient(listOf(t(mezcla(CALIZA, ROCA_LUZ, 0.4f), oscuro, 0.55f), t(mezcla(CALIZA, ROCA, 0.4f), oscuro, 0.55f)), n.top, n.bottom))
        drawPath(panel, t(ROCA_OSC, oscuro).copy(alpha = 0.8f), style = Stroke(0.45f * u))
        val orla = n.crece(1.9f * u)
        val oc = OCRE.copy(alpha = if (oscuro) 0.75f else 0.85f)
        val paso = 2.6f * u
        var x = orla.left; var k = 0
        while (x <= orla.right) { drawCircle(oc, (0.45f + 0.2f * azar(7, k)) * u, Offset(x, orla.top)); drawCircle(oc, (0.45f + 0.2f * azar(8, k)) * u, Offset(x, orla.bottom)); x += paso; k++ }
        var y = orla.top + paso
        while (y < orla.bottom) { drawCircle(oc, (0.45f + 0.2f * azar(9, k)) * u, Offset(orla.left, y)); drawCircle(oc, (0.45f + 0.2f * azar(10, k)) * u, Offset(orla.right, y)); y += paso; k++ }
        drawRect(nicho(e, oscuro, CALIZA), n.topLeft, n.size)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.12f), n.topLeft, n.size, style = Stroke(0.3f * u))
        // El suelo, el fogón, las piedras de sentarse con sus pieles, la lámpara y la leña.
        suelo(Rect(0f, piso, w, h), oscuro, e.semilla, u)
        listOf(w * 0.27f, w * 0.73f).forEachIndexed { i, cx ->
            val c = Offset(cx, h * 0.945f); val rw = 8f * u; val rh = 3.6f * u
            drawOval(Color.Black.copy(alpha = 0.35f), Offset(c.x - rw * 1.05f, c.y - rh * 0.2f), Size(rw * 2.1f, rh * 1.3f))
            drawOval(Brush.verticalGradient(listOf(t(ROCA_LUZ, oscuro, 0.45f), t(ROCA_OSC, oscuro, 0.45f)), c.y - rh, c.y + rh), Offset(c.x - rw, c.y - rh), Size(2 * rw, 2 * rh))
            // La piel echada encima, con su pelo.
            val p = Path().apply {
                moveTo(c.x - rw * 0.75f, c.y - rh * 0.5f); quadraticTo(c.x, c.y - rh * 1.35f, c.x + rw * 0.7f, c.y - rh * 0.55f)
                lineTo(c.x + rw * 0.8f, c.y + rh * 0.4f); lineTo(c.x + rw * 0.55f, c.y + rh * 0.3f); lineTo(c.x + rw * 0.4f, c.y + rh * 0.7f)
                quadraticTo(c.x, c.y + rh * 0.2f, c.x - rw * 0.45f, c.y + rh * 0.65f); lineTo(c.x - rw * 0.6f, c.y + rh * 0.3f); lineTo(c.x - rw * 0.85f, c.y + rh * 0.35f); close()
            }
            drawPath(p, t(if (i == 0) PELO else mezcla(PELO, PIEL, 0.5f), oscuro, 0.4f))
            clipPath(p) {
                for (j in 0 until 18) {
                    val q = Offset(c.x - rw + 2 * rw * azar(80 + i, j), c.y - rh + 2 * rh * azar(90 + i, j))
                    drawLine(t(mezcla(PELO, PIEL_CLARA, 0.5f), oscuro, 0.4f).copy(alpha = 0.6f), q, q + Offset(0.5f * u, 1.2f * u), 0.3f * u, StrokeCap.Round)
                }
            }
            drawPath(p, t(CORTEZA, oscuro, 0.4f), style = Stroke(0.3f * u))
        }
        fogon(fuego, 6.4f * u, oscuro, u)
        lampara(Offset(w * 0.075f, h * 0.95f), 2.6f * u, oscuro, u)
        // La leña apilada a la derecha y un hueso.
        listOf(0f, 1f, 2f).forEach { j ->
            val r = Rect(w * 0.88f, h * 0.955f - j * 2.4f * u - 2.4f * u, w * 0.88f + 14f * u, h * 0.955f - j * 2.4f * u)
            val rr = if (j == 2f) r.translate(3f * u, 0f) else r
            if (rr.right < w) tronco(rr, true, oscuro, 100 + j.toInt(), u)
        }
        hueso(Offset(w * 0.37f, h * 0.975f), Offset(w * 0.37f + 7f * u, h * 0.96f), 1f * u, oscuro)
    }

    // ---------------------------------------------------------------- Mapas

    override fun DrawScope.muebleMapas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val n = caja(area(MuebleVida.MAPAS))
        // La roca del fondo y el suelo.
        pared(Rect(0f, 0f, w, h * 0.88f), oscuro, e.semilla + 4, u)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.18f))
        suelo(Rect(0f, h * 0.88f, w, h), oscuro, e.semilla, u)
        // Las dos piedras que sostienen la laja.
        listOf(w * 0.14f, w * 0.86f).forEachIndexed { i, cx ->
            val r = Rect(cx - 9f * u, h * 0.84f, cx + 9f * u, h * 0.985f)
            drawOval(Color.Black.copy(alpha = 0.4f), Offset(r.left - 1f * u, r.bottom - 2f * u), Size(r.width + 2f * u, 3f * u))
            val p = laja(r, 120 + i, 0.08f, 14)
            drawPath(p, Brush.verticalGradient(listOf(t(ROCA_OSC, oscuro, 0.5f), t(ROCA_HONDA, oscuro, 0.3f)), r.top, r.bottom))
            drawPath(p, t(ROCA, oscuro, 0.5f).copy(alpha = 0.5f), style = Stroke(0.35f * u))
        }
        // La laja: el canto grueso, la cara de arriba con sus poros y manchas.
        val cara = Rect(w * 0.015f, h * 0.02f, w * 0.985f, h * 0.83f)
        val forma = laja(cara, e.semilla + 7, 0.02f, 36)
        translate(0f, 4f * u) { drawPath(forma, Brush.verticalGradient(listOf(t(ROCA_OSC, oscuro, 0.5f), t(ROCA_HONDA, oscuro, 0.35f)), cara.bottom - 4f * u, cara.bottom + 4f * u)) }
        translate(0.4f * u, 5.4f * u) { drawPath(forma, Color.Black.copy(alpha = 0.25f)) }
        translate(0f, 4f * u) { drawPath(forma, Brush.verticalGradient(listOf(t(ROCA, oscuro, 0.5f), t(ROCA_OSC, oscuro, 0.5f)), cara.top, cara.bottom + 4f * u)) }
        drawPath(forma, Brush.linearGradient(listOf(t(ROCA_LUZ, oscuro, 0.55f), t(mezcla(ROCA, ROCA_LUZ, 0.5f), oscuro, 0.55f), t(ROCA, oscuro, 0.55f)), cara.topLeft, cara.bottomRight))
        clipPath(forma) {
            for (k in 0 until 40) {
                val c = Offset(cara.left + cara.width * azar(e.semilla + 130, k), cara.top + cara.height * azar(e.semilla + 131, k))
                halo(c, (3f + 6f * azar(e.semilla + 132, k)) * u, (if (k % 2 == 0) t(ROCA_OSC, oscuro, 0.55f) else t(CALIZA, oscuro, 0.55f)).copy(alpha = 0.25f))
            }
            for (k in 0 until 90) drawCircle(t(ROCA_HONDA, oscuro, 0.3f).copy(alpha = 0.22f), (0.2f + 0.3f * azar(e.semilla + 133, k)) * u, Offset(cara.left + cara.width * azar(e.semilla + 134, k), cara.top + cara.height * azar(e.semilla + 135, k)))
        }
        drawPath(forma, t(ROCA_LUZ, oscuro, 0.5f).copy(alpha = 0.7f), style = Stroke(0.4f * u))
        // La piel estirada, clavada con punzones de hueso en las patas, el cuello y la cola; su campo es el nicho.
        val pr = n.crece(1.6f * u)
        val pf = piel(pr, 3.4f * u, e.semilla)
        translate(0.8f * u, 1.2f * u) { drawPath(pf, Color.Black.copy(alpha = 0.35f)) }
        drawPath(pf, Brush.radialGradient(listOf(t(PIEL, oscuro, 0.4f), t(PIEL_OSC, oscuro, 0.4f)), n.center, n.width * 0.7f))
        drawPath(pf, t(mezcla(PIEL_OSC, CORTEZA, 0.5f), oscuro, 0.4f), style = Stroke(0.45f * u))
        val colorPiel = nicho(e, oscuro, PIEL_CLARA)
        drawRect(colorPiel, n.topLeft, n.size)
        drawRect(t(PIEL_OSC, oscuro, 0.3f).copy(alpha = 0.5f), n.topLeft, n.size, style = Stroke(0.35f * u))
        val punzones = listOf(
            Offset(pr.left - 3.2f * u, pr.top - 3.2f * u) to 225f, Offset(pr.right + 3.2f * u, pr.top - 3.2f * u) to 315f,
            Offset(pr.left - 3.2f * u, pr.bottom + 3.2f * u) to 135f, Offset(pr.right + 3.2f * u, pr.bottom + 3.2f * u) to 45f,
            Offset(pr.center.x, pr.top - 3f * u) to 270f, Offset(pr.center.x, pr.bottom + 2.6f * u) to 90f,
        )
        punzones.forEach { (c, a) ->
            val d = Offset(cos(Math.toRadians(a.toDouble())).toFloat(), sin(Math.toRadians(a.toDouble())).toFloat())
            hueso(c - d * (1.6f * u), c + d * (2.4f * u), 0.9f * u, oscuro)
        }
        // A la izquierda: la hoja de sílex, el punzón, los carbones y las conchas con ocre.
        val lx = (cara.left + pr.left - 3.4f * u) / 2
        val lan = (pr.left - 3.4f * u - cara.left) * 0.5f
        silex(Offset(lx - lan * 0.35f, h * 0.08f), Offset(lx + lan * 0.4f, h * 0.36f), minOf(2.4f * u, lan * 0.35f), oscuro, u)
        hueso(Offset(lx - lan * 0.55f, h * 0.48f), Offset(lx + lan * 0.15f, h * 0.38f), 0.8f * u, oscuro)
        drawLine(t(HUESO, oscuro, 0.35f), Offset(lx + lan * 0.15f, h * 0.38f), Offset(lx + lan * 0.55f, h * 0.33f), 0.6f * u, StrokeCap.Round)
        listOf(0f, 1f).forEach { j ->
            val a = Offset(lx - lan * 0.45f + j * 1.8f * u, h * 0.5f + j * 1.2f * u)
            drawLine(Color.Black.copy(alpha = 0.3f), a + Offset(0.3f * u, 0.5f * u), a + Offset(lan * 0.8f, 2.2f * u) + Offset(0.3f * u, 0.5f * u), 1.1f * u, StrokeCap.Round)
            drawLine(CARBON, a, a + Offset(lan * 0.8f, 2.2f * u), 1.1f * u, StrokeCap.Round)
            drawLine(Color.White.copy(alpha = 0.15f), a - Offset(0f, 0.3f * u), a + Offset(lan * 0.8f, 1.9f * u), 0.3f * u, StrokeCap.Round)
        }
        concha(Offset(lx - lan * 0.2f, h * 0.67f), minOf(4.2f * u, lan * 0.55f), mezcla(OCRE, e.acento, 0.3f), -15f, oscuro, u)
        concha(Offset(lx + lan * 0.3f, h * 0.8f), minOf(3.6f * u, lan * 0.5f), OCRE_AMARILLO, 20f, oscuro, u)
        // A la derecha: la lámpara de grasa encendida, el mortero con su majadera y el pincel de pelo.
        val rx = (pr.right + 3.4f * u + cara.right) / 2
        val ran = (cara.right - pr.right - 3.4f * u) * 0.5f
        lampara(Offset(rx, h * 0.24f), minOf(4.6f * u, ran * 0.55f), oscuro, u)
        run {
            val c = Offset(rx, h * 0.52f); val r = minOf(5.2f * u, ran * 0.62f)
            drawCircle(Color.Black.copy(alpha = 0.35f), r, c + Offset(0.5f * u, 0.8f * u))
            drawCircle(Brush.radialGradient(listOf(t(ROCA_LUZ, oscuro, 0.45f), t(ROCA_OSC, oscuro, 0.45f)), c - Offset(r * 0.3f, r * 0.3f), r * 1.4f), r, c)
            drawCircle(t(ROCA_HONDA, oscuro, 0.3f), r * 0.66f, c)
            drawCircle(t(mezcla(OCRE, e.acento, 0.3f), oscuro, 0.2f), r * 0.55f, c + Offset(0f, r * 0.06f))
            for (k in 0 until 6) drawCircle(t(OCRE_AMARILLO, oscuro, 0.2f).copy(alpha = 0.7f), 0.3f * u, c + Offset((azar(140, k) - 0.5f) * r, (azar(141, k) - 0.5f) * r * 0.8f))
            val m0 = c + Offset(r * 0.2f, -r * 0.1f); val m1 = c + Offset(r * 1.1f, -r * 1.6f)
            drawLine(Color.Black.copy(alpha = 0.3f), m0 + Offset(0.4f * u, 0.6f * u), m1 + Offset(0.4f * u, 0.6f * u), 1.8f * u, StrokeCap.Round)
            drawLine(t(ROCA, oscuro, 0.45f), m0, m1, 1.8f * u, StrokeCap.Round)
            drawLine(t(ROCA_LUZ, oscuro, 0.45f), m0 - Offset(0.3f * u, 0.3f * u), m1 - Offset(0.3f * u, 0.3f * u), 0.5f * u, StrokeCap.Round)
        }
        run {
            val a = Offset(rx - ran * 0.3f, h * 0.83f); val b = Offset(rx + ran * 0.1f, h * 0.63f)
            drawLine(Color.Black.copy(alpha = 0.3f), a + Offset(0.3f * u, 0.5f * u), b + Offset(0.3f * u, 0.5f * u), 0.9f * u, StrokeCap.Round)
            drawLine(t(MADERA_LUZ, oscuro, 0.4f), a, b, 0.9f * u, StrokeCap.Round)
            val d = (b - a) / (b - a).getDistance()
            atadura(b - d * (0.6f * u), 1.4f * u, oscuro, u)
            for (k in -2..2) drawLine(t(PELO, oscuro, 0.35f), b, b + d * (4f * u) + Offset(-d.y, d.x) * (k * 0.5f * u), 0.4f * u, StrokeCap.Round)
            drawCircle(t(OCRE, oscuro, 0.2f), 0.7f * u, b + d * (4f * u))
        }
    }

    // ---------------------------------------------------------------- Otras vidas

    override fun DrawScope.muebleOtrasVidas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val n = caja(area(MuebleVida.OTRAS_VIDAS))
        val roca = t(ROCA, oscuro)
        pared(Rect(0f, 0f, w, h), oscuro, e.semilla + 2, u)
        techo(w, h * 0.12f, oscuro, e.semilla + 2, u, (w / (8f * u)).toInt())
        // El friso pintado: el bisonte al centro, manos y filas de puntos a los lados.
        val banda = h * 0.165f
        bisontePintado(Offset(w / 2, banda), 22f * u, 1f, oscuro, u)
        listOf(0.2f, 0.8f).forEachIndexed { i, f -> manoNegativa(Offset(w * f, banda), 3.8f * u, if (i == 0) -15f else 15f, roca, OCRE, if (oscuro) 0.45f else 0.55f) }
        puntos(Offset(w * 0.28f, banda + 1f * u), Offset(w * 0.4f, banda - 1f * u), 7, 0.75f * u, OCRE.copy(alpha = 0.8f))
        puntos(Offset(w * 0.6f, banda - 1f * u), Offset(w * 0.72f, banda + 1f * u), 7, 0.75f * u, OCRE.copy(alpha = 0.8f))
        muescas(Offset(w * 0.06f, banda), 5, 3f * u, 1.4f * u, CARBON.copy(alpha = 0.7f), t(ROCA_LUZ, oscuro).copy(alpha = 0.5f), u)
        muescas(Offset(w * 0.9f, banda), 5, 3f * u, 1.4f * u, CARBON.copy(alpha = 0.7f), t(ROCA_LUZ, oscuro).copy(alpha = 0.5f), u)
        // Los cinco huecos tallados en la roca, con su borde gastado y su sombra adentro.
        val cw = n.width / 5f
        val fondo = nicho(e, oscuro, CALIZA)
        for (i in 0 until 5) {
            val c = Rect(n.left + cw * i + 1.1f * u, n.top, n.left + cw * (i + 1) - 1.1f * u, n.bottom)
            val arco = c.width * 0.36f
            fun hueco(r: Rect) = Path().apply {
                moveTo(r.left, r.bottom); lineTo(r.left, r.top + arco)
                cubicTo(r.left, r.top + arco * 0.1f, r.left + r.width * 0.2f, r.top, r.center.x, r.top)
                cubicTo(r.right - r.width * 0.2f, r.top, r.right, r.top + arco * 0.1f, r.right, r.top + arco)
                lineTo(r.right, r.bottom); close()
            }
            drawPath(hueco(c.crece(0.9f * u)), t(ROCA_OSC, oscuro, 0.45f))
            drawPath(hueco(c.crece(0.9f * u)), t(ROCA_LUZ, oscuro).copy(alpha = 0.5f), style = Stroke(0.35f * u))
            val hc = hueco(c)
            drawPath(hc, fondo)
            clipPath(hc) {
                drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.14f), Color.Transparent), c.top, c.top + c.height * 0.3f), c.topLeft, Size(c.width, c.height * 0.3f))
                drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.2f else 0.08f), Color.Transparent), c.left, c.left + 2f * u), c.topLeft, Size(2f * u, c.height))
                // El piso del hueco: una laja apenas más clara.
                drawRect(t(mezcla(CALIZA, ROCA, 0.3f), oscuro, 0.6f), Offset(c.left, c.bottom - 1.4f * u), Size(c.width, 1.4f * u))
            }
            // Una gota de ocre en la clave de cada hueco.
            drawCircle(OCRE.copy(alpha = 0.85f), 0.7f * u, Offset(c.center.x, c.top - 2f * u))
        }
        // Las columnas de calcita entre los huecos, con sus nudos.
        for (i in 1..4) {
            val x = n.left + cw * i
            val col = Rect(x - 1f * u, n.top + 2f * u, x + 1f * u, n.bottom)
            drawRect(Brush.horizontalGradient(listOf(t(ROCA_LUZ, oscuro, 0.5f), t(CALIZA, oscuro, 0.5f), t(ROCA_OSC, oscuro, 0.5f)), col.left, col.right), col.topLeft, col.size)
            listOf(0.18f, 0.5f, 0.82f).forEach { f ->
                val y = col.top + col.height * f
                drawOval(t(mezcla(CALIZA, ROCA, 0.4f), oscuro, 0.5f), Offset(x - 1.4f * u, y - 0.8f * u), Size(2.8f * u, 1.6f * u))
                drawOval(Color.White.copy(alpha = if (oscuro) 0.06f else 0.25f), Offset(x - 1f * u, y - 0.7f * u), Size(1.4f * u, 0.5f * u))
            }
        }
        // La repisa de piedra con muescas de cuenta.
        val rep = Rect(w * 0.015f, n.bottom, w * 0.985f, n.bottom + 4.6f * u)
        val repf = laja(rep, e.semilla + 21, 0.06f, 30)
        translate(0f, 1.2f * u) { drawPath(repf, Color.Black.copy(alpha = 0.4f)) }
        drawPath(repf, Brush.verticalGradient(listOf(t(ROCA_LUZ, oscuro, 0.5f), t(ROCA, oscuro, 0.5f), t(ROCA_OSC, oscuro, 0.5f)), rep.top, rep.bottom))
        drawPath(repf, t(ROCA_OSC, oscuro, 0.4f), style = Stroke(0.3f * u))
        for (i in 0 until 5) {
            val cx = n.left + cw * (i + 0.5f)
            muescas(Offset(cx - 3.6f * u, rep.center.y), 5 + (i % 3), 2.4f * u, 1.3f * u, t(ROCA_HONDA, oscuro, 0.2f), t(ROCA_LUZ, oscuro, 0.3f).copy(alpha = 0.6f), u)
        }
        // El suelo y el collar de dientes y cuentas colgado del borde de la repisa.
        suelo(Rect(0f, rep.bottom, w, h), oscuro, e.semilla + 5, u)
        val ya = rep.bottom + 0.2f * u
        val cuerda = Path().apply { moveTo(w * 0.04f, ya); quadraticTo(w * 0.5f, ya + (h - ya) * 1.0f, w * 0.96f, ya) }
        drawPath(cuerda, t(TENDON, oscuro, 0.35f), style = Stroke(0.4f * u))
        val m = 21
        for (k in 1 until m) {
            val f = k / m.toFloat()
            val x = (1 - f) * (1 - f) * w * 0.04f + 2 * (1 - f) * f * w * 0.5f + f * f * w * 0.96f
            val y = (1 - f) * (1 - f) * ya + 2 * (1 - f) * f * (ya + (h - ya)) + f * f * ya
            if (k % 3 == 0) {
                val d = Path().apply { moveTo(x - 0.8f * u, y); quadraticTo(x - 0.5f * u, y + 2.4f * u, x + 0.2f * u, y + 3.4f * u); quadraticTo(x + 0.4f * u, y + 1.6f * u, x + 0.8f * u, y); close() }
                drawPath(d, t(HUESO, oscuro, 0.35f))
                drawPath(d, t(HUESO_OSC, oscuro, 0.35f), style = Stroke(0.2f * u))
            } else {
                drawCircle(Color.Black.copy(alpha = 0.3f), 0.85f * u, Offset(x + 0.2f * u, y + 0.3f * u))
                drawCircle(t(if (k % 3 == 1) mezcla(OCRE, e.acento, 0.3f) else OCRE_AMARILLO, oscuro, 0.25f), 0.85f * u, Offset(x, y))
                drawCircle(Color.White.copy(alpha = 0.35f), 0.25f * u, Offset(x - 0.3f * u, y - 0.3f * u))
            }
        }
    }

    // ---------------------------------------------------------------- Íconos

    override fun DrawScope.iconoMapa(id: String, e: EstadoIcono, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 108f
        val c = Offset(w / 2, h / 2)
        val linea = t(Color(0xFF3A2C20), oscuro, 0.1f)
        when (id) {
            "unesco" -> {
                // El dolmen: dos piedras de pie, la losa de cubierta encima y un menhir al lado, con una mano pintada.
                val suelo = c.y + 40 * u
                drawOval(Color.Black.copy(alpha = 0.2f), Offset(c.x - 54 * u, suelo - 4 * u), Size(108 * u, 8 * u))
                drawPath(Path().apply { moveTo(c.x - 50 * u, suelo); quadraticTo(c.x, suelo - 7 * u, c.x + 50 * u, suelo); close() }, t(mezcla(Color(0xFF6E7A3E), e.c2, 0.2f), oscuro, 0.3f))
                fun piedra(r: Rect, semilla: Int) {
                    val p = laja(r, semilla, 0.08f, 16)
                    drawPath(p, Brush.horizontalGradient(listOf(t(ROCA_LUZ, oscuro, 0.35f), t(ROCA, oscuro, 0.35f), t(ROCA_OSC, oscuro, 0.35f)), r.left, r.right))
                    drawPath(p, linea, style = Stroke(1.1f * u))
                }
                piedra(Rect(c.x + 30 * u, c.y - 18 * u, c.x + 44 * u, suelo), 3)
                piedra(Rect(c.x - 30 * u, c.y - 16 * u, c.x - 14 * u, suelo), 4)
                piedra(Rect(c.x + 10 * u, c.y - 16 * u, c.x + 26 * u, suelo), 5)
                drawRect(t(ROCA_HONDA, oscuro, 0.1f), Offset(c.x - 14 * u, c.y - 12 * u), Size(24 * u, suelo - c.y + 11 * u))
                val tapa = Rect(c.x - 40 * u, c.y - 32 * u, c.x + 34 * u, c.y - 14 * u)
                drawPath(laja(tapa.translate(2 * u, 3 * u), 6, 0.07f, 18), Color.Black.copy(alpha = 0.25f))
                piedra(tapa, 6)
                drawLine(Color.White.copy(alpha = 0.3f), Offset(tapa.left + 6 * u, tapa.top + 3 * u), Offset(tapa.right - 8 * u, tapa.top + 3 * u), 1 * u, StrokeCap.Round)
                manoNegativa(Offset(c.x - 22 * u, c.y + 8 * u), 7 * u, -10f, t(ROCA, oscuro, 0.35f), e.c1, 0.75f)
                puntos(Offset(c.x - 30 * u, tapa.center.y + 1 * u), Offset(c.x + 24 * u, tapa.center.y + 1 * u), 9, 1.3f * u, t(e.c1, oscuro, 0.1f))
                listOf(-46f, -40f, 48f).forEach { dx -> drawLine(t(Color(0xFF6E7A3E), oscuro, 0.3f), Offset(c.x + dx * u, suelo), Offset(c.x + dx * u + 2 * u, suelo - 7 * u), 1 * u, StrokeCap.Round) }
            }
            "rascar" -> {
                // La laja con el mapa pintado de ocre; el raspador de sílex quita la capa de una esquina y asoma el acento.
                val r = Rect(c.x - 46 * u, c.y - 38 * u, c.x + 30 * u, c.y + 38 * u)
                val f = laja(r, 9, 0.06f, 22)
                translate(2 * u, 3 * u) { drawPath(f, Color.Black.copy(alpha = 0.25f)) }
                drawPath(f, Brush.linearGradient(listOf(t(CALIZA, oscuro, 0.3f), t(mezcla(CALIZA, ROCA, 0.5f), oscuro, 0.3f)), r.topLeft, r.bottomRight))
                drawPath(f, linea, style = Stroke(1.1f * u))
                clipPath(f) {
                    drawPath(Path().apply { moveTo(c.x - 4 * u, r.top); cubicTo(c.x - 14 * u, c.y - 6 * u, c.x + 6 * u, c.y + 6 * u, c.x - 8 * u, r.bottom) }, t(CARBON, oscuro, 0.1f), style = Stroke(1.6f * u))
                    puntos(Offset(c.x - 34 * u, c.y + 20 * u), Offset(c.x + 18 * u, c.y + 10 * u), 9, 1.4f * u, t(OCRE, oscuro, 0.1f))
                    listOf(-30f, -22f).forEach { dx -> drawPath(Path().apply { moveTo(c.x + dx * u - 4 * u, c.y - 8 * u); lineTo(c.x + dx * u, c.y - 16 * u); lineTo(c.x + dx * u + 4 * u, c.y - 8 * u) }, t(CARBON, oscuro, 0.1f), style = Stroke(1.2f * u)) }
                    val esq = Path().apply { moveTo(r.right - 26 * u, r.top); lineTo(r.right, r.top); lineTo(r.right, r.top + 26 * u); quadraticTo(r.right - 16 * u, r.top + 16 * u, r.right - 26 * u, r.top); close() }
                    drawPath(esq, t(e.c1, oscuro, 0.1f))
                    clipPath(esq) { for (k in 0 until 7) drawLine(Color.White.copy(alpha = 0.3f), Offset(r.right - 28 * u + k * 4 * u, r.top), Offset(r.right - 14 * u + k * 4 * u, r.top + 26 * u), 0.7f * u) }
                }
                // El raspador de sílex, en la mano invisible, sobre la esquina.
                silex(Offset(c.x + 56 * u, c.y + 12 * u), Offset(c.x + 20 * u, c.y - 20 * u), 12 * u, oscuro, u, mezcla(mezcla(SILEX, ROCA_LUZ, 0.35f), e.c2, 0.1f))
                listOf(0f, 1f, 2f).forEach { k -> drawCircle(t(e.c1, oscuro, 0.1f).copy(alpha = 0.7f), 1.1f * u, Offset(c.x + (16 + 4 * k) * u, c.y - (2 + 5 * k) * u)) }
            }
            else -> {
                // Tus lugares: tres cantos pintados sobre una tira de piel, unidos por un rastro de puntos de ocre.
                val r = Rect(c.x - 52 * u, c.y - 26 * u, c.x + 52 * u, c.y + 30 * u)
                val pf = piel(r, 4.4f * u, 3)
                translate(2 * u, 3 * u) { drawPath(pf, Color.Black.copy(alpha = 0.22f)) }
                drawPath(pf, Brush.radialGradient(listOf(t(PIEL_CLARA, oscuro, 0.3f), t(PIEL, oscuro, 0.3f)), c, r.width * 0.6f))
                drawPath(pf, linea, style = Stroke(1f * u))
                val pts = listOf(Offset(c.x - 32 * u, c.y + 10 * u), Offset(c.x, c.y - 9 * u), Offset(c.x + 32 * u, c.y + 8 * u))
                for (i in 0 until 2) puntos(pts[i] + Offset(12 * u, 0f), pts[i + 1] - Offset(12 * u, 0f), 4, 1.5f * u, t(OCRE, oscuro, 0.1f))
                listOf(e.c1, mezcla(e.c2, OCRE_AMARILLO, 0.4f), CARBON).forEachIndexed { i, col -> canto(pts[i], 12 * u, i, t(col, oscuro, 0.05f), oscuro, u) }
            }
        }
    }

    // ---------------------------------------------------------------- Notas

    override fun DrawScope.tableroNotas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val n = caja(area(MuebleVida.NOTAS))
        pared(Rect(0f, 0f, w, h), oscuro, e.semilla + 6, u)
        drawRect(Brush.radialGradient(listOf(Color.Transparent, Color.Black.copy(alpha = if (oscuro) 0.4f else 0.18f)), Offset(w / 2, h / 2), w * 0.62f))
        // El suelo al pie del bastidor.
        suelo(Rect(0f, h * 0.94f, w, h), oscuro, e.semilla + 1, u)
        // La piel estirada (el nicho), con los agujeros del cosido en su borde.
        val pr = n.crece(1.4f * u)
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(pr.left + 0.8f * u, pr.top + 1.2f * u), pr.size)
        drawRect(t(PIEL, oscuro, 0.45f), pr.topLeft, pr.size)
        drawRect(t(PIEL_OSC, oscuro, 0.45f), pr.topLeft, pr.size, style = Stroke(0.5f * u))
        val fondo = nicho(e, oscuro, PIEL_CLARA)
        drawRect(fondo, n.topLeft, n.size)
        clipRect(n.left, n.top, n.right, n.bottom) {
            for (k in 0 until 26) {
                val c = Offset(n.left + n.width * azar(e.semilla + 150, k), n.top + n.height * azar(e.semilla + 151, k))
                halo(c, (5f + 8f * azar(e.semilla + 152, k)) * u, PIEL_OSC.copy(alpha = if (oscuro) 0.05f else 0.06f))
            }
        }
        // El bastidor: los dos postes, el tronco de arriba y el de abajo, con corteza.
        val pw = 4.4f * u
        val izq = Rect(w * 0.012f, h * 0.0f, w * 0.012f + pw, h * 0.97f)
        val der = Rect(w * 0.988f - pw, h * 0.0f, w * 0.988f, h * 0.97f)
        listOf(izq, der).forEachIndexed { i, p ->
            drawRect(Color.Black.copy(alpha = 0.35f), Offset(p.left + 1f * u, p.top), Size(p.width, p.height))
            tronco(p, false, oscuro, 160 + i, u, cortes = false)
        }
        val arriba = Rect(w * 0.004f, h * 0.035f, w * 0.996f, h * 0.035f + 5f * u)
        val abajo = Rect(w * 0.004f, n.bottom + 2.2f * u, w * 0.996f, n.bottom + 7f * u)
        listOf(arriba, abajo).forEachIndexed { i, r ->
            drawRect(Color.Black.copy(alpha = 0.35f), Offset(r.left, r.top + 1.2f * u), r.size)
            tronco(r, true, oscuro, 170 + i, u)
        }
        // El cosido: el tendón va en zigzag de los agujeros de la piel a los troncos.
        val ten = t(TENDON, oscuro, 0.35f)
        fun cosido(a: Offset, b: Offset) {
            drawLine(Color.Black.copy(alpha = 0.3f), a + Offset(0.2f * u, 0.3f * u), b + Offset(0.2f * u, 0.3f * u), 0.4f * u)
            drawLine(ten, a, b, 0.35f * u)
            drawCircle(t(PIEL_OSC, oscuro, 0.3f), 0.45f * u, a)
        }
        val paso = 6f * u
        var x = pr.left + paso * 0.5f; var k = 0
        while (x < pr.right) {
            cosido(Offset(x, pr.top + 0.7f * u), Offset(x + (if (k % 2 == 0) 1.5f else -1.5f) * u, arriba.bottom - 0.6f * u))
            cosido(Offset(x, pr.bottom - 0.7f * u), Offset(x + (if (k % 2 == 0) -1.5f else 1.5f) * u, abajo.top + 0.6f * u))
            x += paso; k++
        }
        var y = pr.top + paso * 0.5f; k = 0
        while (y < pr.bottom) {
            cosido(Offset(pr.left + 0.7f * u, y), Offset(izq.right - 0.6f * u, y + (if (k % 2 == 0) 1.5f else -1.5f) * u))
            cosido(Offset(pr.right - 0.7f * u, y), Offset(der.left + 0.6f * u, y + (if (k % 2 == 0) -1.5f else 1.5f) * u))
            y += paso; k++
        }
        // Las ataduras de tendón en las cuatro esquinas.
        listOf(Offset(izq.center.x, arriba.center.y), Offset(der.center.x, arriba.center.y), Offset(izq.center.x, abajo.center.y), Offset(der.center.x, abajo.center.y)).forEach { c -> atadura(c, 4.6f * u, oscuro, u) }
        // Del tronco de arriba cuelgan dientes y cuentas, y dos plumas en los extremos.
        listOf(0.24f, 0.5f, 0.76f).forEachIndexed { j, f ->
            val cx = w * f
            listOf(-2.4f, -1.2f, 0f, 1.2f, 2.4f).forEachIndexed { i, dx ->
                val p = Offset(cx + dx * u, arriba.bottom + 0.4f * u)
                if (i % 2 == 0) {
                    val d = Path().apply { moveTo(p.x - 0.5f * u, p.y); quadraticTo(p.x - 0.3f * u, p.y + 1.8f * u, p.x + 0.15f * u, p.y + 2.6f * u); quadraticTo(p.x + 0.3f * u, p.y + 1.2f * u, p.x + 0.5f * u, p.y); close() }
                    drawPath(d, t(HUESO, oscuro, 0.35f))
                } else drawCircle(t(if (j == 1) mezcla(OCRE, e.acento, 0.3f) else OCRE_AMARILLO, oscuro, 0.25f), 0.6f * u, p + Offset(0f, 0.5f * u))
            }
            atadura(Offset(cx, arriba.center.y), 2.6f * u, oscuro, u)
        }
        listOf(izq.right + 3f * u to -1f, der.left - 3f * u to 1f).forEach { (px, s) ->
            val base = Offset(px, arriba.bottom)
            rotate(s * 12f, base) {
                val pl = Rect(base.x - 1.1f * u, base.y + 0.5f * u, base.x + 1.1f * u, base.y + 8f * u)
                drawOval(t(mezcla(HUESO, PELO, 0.3f), oscuro, 0.35f), pl.topLeft, pl.size)
                drawOval(t(CARBON, oscuro, 0.2f), Offset(pl.left, pl.bottom - 2.6f * u), Size(pl.width, 2.6f * u))
                drawLine(t(HUESO_OSC, oscuro, 0.35f), Offset(base.x, base.y), Offset(base.x, pl.bottom), 0.25f * u)
            }
        }
        // Al pie: la lámpara de grasa y una concha con ocre.
        lampara(Offset(w * 0.2f, h * 0.975f), 2.2f * u, oscuro, u)
        concha(Offset(w * 0.8f, h * 0.975f), 1.8f * u, OCRE, 10f, oscuro, u)
    }

    override fun DrawScope.notaTablero(e: EstadoNota, oscuro: Boolean) {
        val r = e.rect; val w = r.width; val h = r.height; val s = minOf(w, h) / 100f
        // La laja de caliza, desportillada, colgada de su clavija, con su sombra en la piel.
        val hoja = Rect(r.left + w * 0.025f, r.top + h * 0.05f, r.right - w * 0.025f, r.bottom - h * 0.02f)
        val forma = laja(hoja, 61 + e.indice * 5, 0.035f, 26)
        translate(1.3f * s, 2.2f * s) { drawPath(forma, Color.Black.copy(alpha = if (oscuro) 0.55f else 0.32f)) }
        val piedra = if (oscuro) mezcla(CALIZA, ROCA_HONDA, 0.35f) else CALIZA
        drawPath(forma, piedra)
        clipPath(forma) {
            val b = 6f * s
            drawRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = if (oscuro) 0.06f else 0.35f), Color.Transparent), hoja.top, hoja.top + b), hoja.topLeft, Size(hoja.width, b))
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, ROCA_OSC.copy(alpha = 0.35f)), hoja.bottom - b, hoja.bottom), Offset(hoja.left, hoja.bottom - b), Size(hoja.width, b))
            drawRect(Brush.horizontalGradient(listOf(Color.Transparent, ROCA_OSC.copy(alpha = 0.25f)), hoja.right - b, hoja.right), Offset(hoja.right - b, hoja.top), Size(b, hoja.height))
            for (k in 0 until 22) drawCircle(ROCA_OSC.copy(alpha = 0.22f), (0.4f + 0.5f * azar(e.indice + 70, k)) * s, Offset(hoja.left + hoja.width * azar(e.indice + 71, k), hoja.top + hoja.height * azar(e.indice + 72, k)))
            if (e.fijada) drawRect(OCRE.copy(alpha = if (oscuro) 0.7f else 0.8f), Offset(hoja.left, hoja.top + h * 0.02f), Size(hoja.width, h * 0.06f))
        }
        drawPath(forma, mezcla(ROCA_OSC, Color.Black, if (oscuro) 0.4f else 0.1f), style = Stroke(0.9f * s))
        // Tres puntos de ocre en la esquina de abajo.
        puntos(Offset(r.left + w * 0.06f, r.bottom - h * 0.075f), Offset(r.left + w * 0.16f, r.bottom - h * 0.075f), 3, 1.1f * s, OCRE.copy(alpha = 0.85f))
        // El detalle del tipo, en la esquina de arriba a la derecha.
        val esq = Offset(r.right - w * 0.09f, r.top + h * 0.12f)
        when (e.tipo) {
            "idea" -> manoNegativa(esq, 4.4f * s, 12f, piedra, mezcla(OCRE, e.acento, 0.3f), 0.8f)
            "pensamiento" -> {
                // La luna creciente a carbón, con los puntos de sus noches.
                clipPath(Path().apply { addRect(Rect(esq.x - 6f * s, esq.y - 6f * s, esq.x + 6f * s, esq.y + 6f * s)) }) {
                    drawCircle(CARBON, 4.2f * s, esq)
                    drawCircle(piedra, 3.4f * s, esq + Offset(3f * s, -1.8f * s))
                }
                for (k in 0 until 3) drawCircle(OCRE, 0.7f * s, esq + Offset(-5.6f * s + k * 2.2f * s, 5.2f * s))
            }
            "proposito" -> {
                // La punta de sílex, con su filo.
                silex(esq + Offset(0f, 5f * s), esq + Offset(0f, -4.6f * s), 2.6f * s, false, s, OCRE_AMARILLO)
            }
            else -> {
                // Las muescas de cuenta y el cordel anudado que cuelga del borde.
                muescas(Offset(esq.x - 4.5f * s, esq.y), 5, 5f * s, 1.6f * s, CARBON.copy(alpha = 0.85f), Color.White.copy(alpha = 0.4f), s)
                val cx = r.right - w * 0.04f
                drawLine(PIEL_OSC, Offset(cx, r.top + h * 0.2f), Offset(cx + 0.6f * s, r.top + h * 0.48f), 0.8f * s, StrokeCap.Round)
                listOf(0.28f, 0.38f).forEach { f -> drawCircle(PIEL_OSC, 1f * s, Offset(cx + 0.3f * s, r.top + h * f)) }
            }
        }
        // La clavija de hueso con su cordel; si la nota está fijada, de ocre, con un diente colgado.
        val cl = Offset(r.center.x, r.top + h * 0.045f)
        drawLine(PIEL_OSC, cl, Offset(r.center.x - w * 0.06f, hoja.top + h * 0.02f), 0.6f * s)
        drawLine(PIEL_OSC, cl, Offset(r.center.x + w * 0.06f, hoja.top + h * 0.02f), 0.6f * s)
        drawCircle(Color.Black.copy(alpha = 0.25f), 0.9f * s, Offset(r.center.x - w * 0.06f, hoja.top + h * 0.02f))
        drawCircle(Color.Black.copy(alpha = 0.25f), 0.9f * s, Offset(r.center.x + w * 0.06f, hoja.top + h * 0.02f))
        hueso(cl - Offset(3.4f * s, 0f), cl + Offset(3.4f * s, 0f), 1.6f * s, oscuro = false)
        if (e.fijada) {
            drawLine(OCRE, cl - Offset(1.4f * s, 0f), cl + Offset(1.4f * s, 0f), 1.8f * s)
            val d = Path().apply { moveTo(cl.x - 1f * s, cl.y + 1f * s); quadraticTo(cl.x - 0.6f * s, cl.y + 4.4f * s, cl.x + 0.4f * s, cl.y + 6f * s); quadraticTo(cl.x + 0.7f * s, cl.y + 3f * s, cl.x + 1f * s, cl.y + 1f * s); close() }
            drawPath(d, HUESO)
            drawPath(d, HUESO_OSC, style = Stroke(0.4f * s))
        }
    }

    override fun tintaNota(oscuro: Boolean): Color = CARBON
}
