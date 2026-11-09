package io.github.ruddy2003.vestigium.ui.vida

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
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
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp
import java.util.Random
import kotlin.math.cos
import kotlin.math.sin

/**
 * Helénico: la casa griega, con los mismos materiales que su biblioteca, su Ideario y su stoa de logros: mármol, terracota
 * con barniz negro, bronce, la greca azul del Egeo y el sol de Vergina.
 * - Social: el banquete del simposio. Las paredes de estuco ocre del andrón a los lados; en cada una, una kliné con su
 *   colchón del color de acento, su fulcro y la trapeza con un kílix delante, y colgadas de la pared la lira y la corona de
 *   hojas. Arriba corre la greca; abajo, el piso de guijarros y, al centro, la crátera donde se mezcla el vino.
 * - Mapas: la mesa del geógrafo. El pínax de bronce (la tabla de bronce donde se grababa el mundo) bajo el mapa, con su
 *   borde, sus remaches y la greca; un rollo atado arriba, el compás abajo a la derecha y unas dracmas a la izquierda.
 * - Otras vidas: un friso dórico. Cada medalla va en una metopa hundida; entre ellas, los triglifos pintados de azul, con
 *   la cornisa y la greca arriba y el arquitrabe con las gotas abajo.
 * - Íconos: el Partenón, una dracma que raspa un óstrakon y tres sellos de cera con el sol.
 * - Notas: un muro de estuco con sus hiladas, la greca arriba y el zócalo azul; cada nota es un óstrakon (un tiesto de
 *   vasija) con su borde roto, el barniz negro en un canto y un clavo de bronce si está fijada.
 */
internal object VidaHeleno : DisenoVida {
    private val MARMOL = Color(0xFFEEE8DA)
    private val MARMOL_LUZ = Color(0xFFFAF6EC)
    private val MARMOL_OSC = Color(0xFFCBC1AB)
    private val MARMOL_SOMBRA = Color(0xFF9E947E)
    private val TERRACOTA = Color(0xFFC2652F)
    private val TERRA_OSC = Color(0xFF8A4220)
    private val TERRA_LUZ = Color(0xFFDE8A52)
    private val BARNIZ = Color(0xFF1A1410)
    private val BRONCE = Color(0xFFB07A3A)
    private val BRONCE_OSC = Color(0xFF6E4A20)
    private val ORO = Color(0xFFC9A54A)
    private val EGEO = Color(0xFF2E5E8E)
    private val MURO = Color(0xFFD9B97E)
    private val MURO_OSC = Color(0xFFA9874E)
    private val MADERA = Color(0xFF7A5232)
    private val MADERA_OSC = Color(0xFF4A3020)
    private val PAPIRO = Color(0xFFE6D3A3)
    private val VINO = Color(0xFF5A1A22)
    private val HOJA = Color(0xFF4E6B2E)
    private val NOCHE = Color(0xFF0F0C0A)

    /** El óstrakon: barro claro de día; de noche, barro cocido oscuro (con la tinta clara de [tintaNota]). */
    private val BARRO_CLARO = Color(0xFFE9CFA9)
    private val BARRO_OSCURO = Color(0xFF4A2C1E)

    private fun DrawScope.u() = size.minDimension / 100f
    private fun mez(a: Color, b: Color, f: Float): Color = lerp(a, b, f.coerceIn(0f, 1f))
    /** El material de noche, más hundido. */
    private fun n(c: Color, oscuro: Boolean): Color = if (oscuro) mez(c, NOCHE, 0.5f) else c
    /** El material sobre la superficie de la tarjeta: [k] es cuánto se ve (0, nada; 1, el material pleno). */
    private fun v(fondo: Color, c: Color, k: Float, oscuro: Boolean): Color = mez(fondo, n(c, oscuro), if (oscuro) minOf(k * 1.5f, 1f) else k)

    /** Una estrella de [puntas] puntas; [hondo] es el radio de los valles respecto al de las puntas. */
    private fun estrella(c: Offset, r: Float, puntas: Int, hondo: Float): Path = Path().apply {
        for (k in 0 until puntas * 2) {
            val a = -Math.PI / 2 + Math.PI * k / puntas
            val rr = if (k % 2 == 0) r else r * hondo
            val x = c.x + rr * cos(a).toFloat(); val y = c.y + rr * sin(a).toFloat()
            if (k == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
    }

    /** El sol de Vergina: la estrella de dieciséis rayos con su disco. */
    private fun DrawScope.solVergina(c: Offset, r: Float, color: Color) {
        drawPath(estrella(c, r, 16, 0.5f), color)
        drawCircle(color, r * 0.3f, c)
    }

    /** Una greca (meandro) que corre a lo largo de [r], recortada a él. */
    private fun DrawScope.greca(r: Rect, color: Color, grosor: Float) {
        val a = r.height
        if (a <= 0f || r.width <= 0f) return
        val p = Path()
        var x = r.left
        p.moveTo(x, r.bottom)
        var k = 0
        while (x < r.right && k < 240) {
            p.lineTo(x, r.top); p.lineTo(x + a * 0.8f, r.top); p.lineTo(x + a * 0.8f, r.top + a * 0.6f); p.lineTo(x + a * 0.4f, r.top + a * 0.6f)
            p.lineTo(x + a * 0.4f, r.top + a * 0.3f); p.moveTo(x + a * 0.4f, r.bottom); p.lineTo(x + a * 1.2f, r.bottom)
            x += a * 1.2f; k++
        }
        clipRect(r.left, r.top - grosor, r.right, r.bottom + grosor) { drawPath(p, color, style = Stroke(grosor)) }
    }

    /** El contorno de un tiesto dentro de [r]: el perímetro con muescas, cada lado empujado hacia adentro hasta [hondo] (fracción del lado menor). */
    private fun tiesto(r: Rect, semilla: Int, hondo: Float): Path {
        val rnd = Random(semilla.toLong() * 7919L + 13L)
        val m = minOf(r.width, r.height) * hondo
        val p = Path()
        val puntos = 14
        for (k in 0 until puntos) {
            val t = k / puntos.toFloat()
            val adentro = m * (0.15f + 0.85f * rnd.nextFloat())
            val (x, y) = when {
                t < 0.25f -> (r.left + r.width * (t / 0.25f)) to (r.top + adentro)
                t < 0.5f -> (r.right - adentro) to (r.top + r.height * ((t - 0.25f) / 0.25f))
                t < 0.75f -> (r.right - r.width * ((t - 0.5f) / 0.25f)) to (r.bottom - adentro)
                else -> (r.left + adentro) to (r.bottom - r.height * ((t - 0.75f) / 0.25f))
            }
            if (k == 0) p.moveTo(x, y) else p.lineTo(x, y)
        }
        p.close()
        return p
    }

    /** Los centros de las cinco medallas de Otras vidas (una fila repartida con 10 dp de margen) y la distancia entre ellas. */
    private fun DrawScope.centrosMedallas(): Pair<List<Float>, Float> {
        val margen = 10.dp.toPx(); val ancho = 52.dp.toPx(); val n = 5
        val hueco = ((size.width - 2 * margen - n * ancho) / (n + 1)).coerceAtLeast(0f)
        return List(n) { i -> margen + hueco * (i + 1) + ancho * (i + 0.5f) } to (hueco + ancho)
    }

    // ---------------------------------------------------------------- Social

    override fun DrawScope.muebleSocial(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        fun m(c: Color, k: Float) = v(e.fondo, c, k, oscuro)
        fun s(c: Color, k: Float) = mez(e.fondo, c, k)
        val lado = minOf(w * 0.11f, 22f * u)
        val suelo = h - 5f * u

        // Las paredes de estuco del andrón, que se desvanecen hacia el centro.
        drawRect(Brush.horizontalGradient(listOf(m(MURO, 0.42f), m(MURO, 0.3f), m(MURO, 0f)), 0f, lado * 1.7f), Offset.Zero, Size(lado * 1.7f, h))
        drawRect(Brush.horizontalGradient(listOf(m(MURO, 0f), m(MURO, 0.3f), m(MURO, 0.42f)), w - lado * 1.7f, w), Offset(w - lado * 1.7f, 0f), Size(lado * 1.7f, h))
        // La franja pintada del zócalo del andrón, a los lados: azul del Egeo con su filete.
        drawRect(m(EGEO, 0.22f), Offset(0f, h * 0.56f), Size(lado * 1.1f, 2.2f * u))
        drawRect(m(EGEO, 0.22f), Offset(w - lado * 1.1f, h * 0.56f), Size(lado * 1.1f, 2.2f * u))

        // El piso de guijarros del centro, apenas: un óvalo tenue con su orla de puntos.
        val piso = Rect(w * 0.15f, h * 0.15f, w * 0.85f, h * 0.88f)
        drawOval(m(MARMOL_OSC, 0.10f), piso.topLeft, piso.size)
        drawOval(m(MARMOL_SOMBRA, 0.32f), piso.topLeft, piso.size,
            style = Stroke(1.1f * u, cap = StrokeCap.Round, pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.1f, 2.8f * u))))

        // El friso de arriba con la greca azul.
        val friso = 7f * u
        drawRect(m(MARMOL, 0.45f), Offset.Zero, Size(w, friso))
        drawLine(m(MARMOL_SOMBRA, 0.35f), Offset(0f, 1.1f * u), Offset(w, 1.1f * u), 0.5f * u)
        drawLine(m(MARMOL_SOMBRA, 0.6f), Offset(0f, friso), Offset(w, friso), 0.6f * u)
        greca(Rect(0f, 2.1f * u, w, friso - 1.1f * u), s(e.secundario, 0.6f), 0.55f * u)

        // El piso de abajo: la franja de guijarros claros y oscuros.
        drawRect(m(MARMOL_OSC, 0.38f), Offset(0f, suelo), Size(w, h - suelo))
        drawLine(m(MARMOL_SOMBRA, 0.6f), Offset(0f, suelo), Offset(w, suelo), 0.6f * u)
        val paso = 3f * u
        val nG = (w / paso).toInt().coerceIn(0, 200)
        for (k in 0 until nG) drawCircle(if (k % 2 == 0) m(BARNIZ, 0.30f) else m(MARMOL_LUZ, 0.65f), 0.75f * u, Offset(paso * (k + 0.5f), suelo + 2.5f * u))

        // Una kliné a cada lado, con la cabecera junto a la pared y su trapeza delante.
        val madera = m(MADERA, 0.6f); val tela = s(e.acento, 0.42f); val cojin = s(e.secundario, 0.4f)
        val bronce = m(BRONCE, 0.7f); val raya = s(e.acento, 0.65f)
        val yK = suelo - 5f * u
        kline(1.5f * u, lado, yK, true, u, madera, tela, cojin, bronce, raya)
        kline(w - lado, w - 1.5f * u, yK, false, u, madera, tela, cojin, bronce, raya)
        trapeza(lado * 0.62f, yK + 1.8f * u, (lado - 1.5f * u) * 0.55f, u, m(MADERA_OSC, 0.6f), m(BARNIZ, 0.55f))
        trapeza(w - lado * 0.62f, yK + 1.8f * u, (lado - 1.5f * u) * 0.55f, u, m(MADERA_OSC, 0.6f), m(BARNIZ, 0.55f))

        // Colgadas de la pared: la lira a la izquierda y la corona de hojas a la derecha.
        val clavo = m(BRONCE_OSC, 0.6f)
        val g = 1.5f * u
        lira(Offset(lado * 0.52f, h * 0.33f), g, m(TERRA_OSC, 0.7f), m(MADERA, 0.75f), m(MARMOL_SOMBRA, 0.7f), clavo)
        corona(Offset(w - lado * 0.52f, h * 0.33f), g, m(HOJA, 0.75f), s(e.acento, 0.6f), clavo)

        // La crátera en el centro, sobre el piso.
        val cx = w / 2; val yb = suelo
        val crater = Path().apply {
            moveTo(cx - 2.2f * u, yb - 2.6f * u)
            quadraticTo(cx - 5.4f * u, yb - 4.6f * u, cx - 5.8f * u, yb - 10f * u)
            lineTo(cx + 5.8f * u, yb - 10f * u)
            quadraticTo(cx + 5.4f * u, yb - 4.6f * u, cx + 2.2f * u, yb - 2.6f * u)
            close()
        }
        drawRect(m(TERRA_OSC, 0.6f), Offset(cx - 2.6f * u, yb - 1f * u), Size(5.2f * u, 1f * u))
        drawRect(m(BARNIZ, 0.55f), Offset(cx - 0.9f * u, yb - 2.7f * u), Size(1.8f * u, 1.7f * u))
        drawPath(crater, m(TERRACOTA, 0.62f))
        clipPath(crater) {
            drawRect(m(BARNIZ, 0.62f), Offset(cx - 7f * u, yb - 10f * u), Size(14f * u, 2f * u))
            drawRect(m(BARNIZ, 0.62f), Offset(cx - 7f * u, yb - 4.6f * u), Size(14f * u, 2.4f * u))
            greca(Rect(cx - 6f * u, yb - 7.4f * u, cx + 6f * u, yb - 6.2f * u), m(BARNIZ, 0.62f), 0.35f * u)
        }
        drawOval(m(VINO, 0.7f), Offset(cx - 5.2f * u, yb - 10.6f * u), Size(10.4f * u, 1.3f * u))
        drawArc(m(TERRA_OSC, 0.62f), 90f, 180f, false, Offset(cx - 6.6f * u, yb - 6.9f * u), Size(2.6f * u, 2.8f * u), style = Stroke(0.8f * u))
        drawArc(m(TERRA_OSC, 0.62f), -90f, 180f, false, Offset(cx + 4f * u, yb - 6.9f * u), Size(2.6f * u, 2.8f * u), style = Stroke(0.8f * u))
    }

    /** Una kliné de frente: las patas torneadas, el bastidor, el colchón, el cojín y el fulcro curvo de la cabecera. */
    private fun DrawScope.kline(x0: Float, x1: Float, y: Float, cabeceraIzq: Boolean, u: Float, madera: Color, tela: Color, cojin: Color, bronce: Color, raya: Color) {
        val pata = 5f * u
        listOf(x0 + 1.6f * u, x1 - 1.6f * u).forEach { x ->
            drawLine(madera, Offset(x, y), Offset(x, y + pata), 1.2f * u, StrokeCap.Round)
            drawCircle(madera, 0.85f * u, Offset(x, y + pata * 0.42f))
        }
        drawRect(madera, Offset(x0, y - 1.2f * u), Size(x1 - x0, 1.2f * u))
        drawRoundRect(tela, Offset(x0 + 0.4f * u, y - 3.6f * u), Size(x1 - x0 - 0.8f * u, 2.5f * u), CornerRadius(1.2f * u))
        drawLine(raya, Offset(x0 + 1.2f * u, y - 2.3f * u), Offset(x1 - 1.2f * u, y - 2.3f * u), 0.35f * u)
        val sg = if (cabeceraIzq) -1f else 1f
        val xc = if (cabeceraIzq) x0 + 0.8f * u else x1 - 0.8f * u
        val fulcro = Path().apply { moveTo(xc - sg * 2.2f * u, y - 1.2f * u); quadraticTo(xc + sg * 0.4f * u, y - 3f * u, xc + sg * 0.5f * u, y - 6.4f * u) }
        drawPath(fulcro, madera, style = Stroke(1.2f * u, cap = StrokeCap.Round))
        drawCircle(bronce, 0.9f * u, Offset(xc + sg * 0.5f * u, y - 6.4f * u))
        drawRoundRect(cojin, Offset(if (cabeceraIzq) x0 + 1.4f * u else x1 - 5.4f * u, y - 5.7f * u), Size(4f * u, 2.3f * u), CornerRadius(1.1f * u))
    }

    /** La trapeza: la mesita baja de tres patas que se ponía delante de la kliné, con un kílix encima. */
    private fun DrawScope.trapeza(cx: Float, y: Float, ancho: Float, u: Float, madera: Color, copa: Color) {
        drawRect(madera, Offset(cx - ancho / 2, y), Size(ancho, 0.8f * u))
        listOf(-0.4f, 0f, 0.4f).forEach { k -> drawLine(madera, Offset(cx + k * ancho, y + 0.8f * u), Offset(cx + k * ancho * 1.1f, y + 3.2f * u), 0.6f * u) }
        val c = Offset(cx + ancho * 0.12f, y)
        drawPath(Path().apply { moveTo(c.x - 2f * u, c.y - 1.7f * u); quadraticTo(c.x, c.y - 0.1f * u, c.x + 2f * u, c.y - 1.7f * u); close() }, copa)
        drawLine(copa, Offset(c.x, c.y - 1f * u), Offset(c.x, c.y), 0.5f * u)
        drawLine(copa, Offset(c.x - 0.8f * u, c.y), Offset(c.x + 0.8f * u, c.y), 0.4f * u)
    }

    /** La lira colgada de un clavo: la caja de caparazón, los dos brazos, el travesaño y las cuerdas. */
    private fun DrawScope.lira(c: Offset, u: Float, caja: Color, brazos: Color, cuerdas: Color, clavo: Color) {
        drawLine(cuerdas, Offset(c.x - 2.8f * u, c.y - 3.2f * u), Offset(c.x, c.y - 6.5f * u), 0.35f * u)
        drawLine(cuerdas, Offset(c.x + 2.8f * u, c.y - 3.2f * u), Offset(c.x, c.y - 6.5f * u), 0.35f * u)
        drawCircle(clavo, 0.6f * u, Offset(c.x, c.y - 6.5f * u))
        drawArc(caja, 0f, 180f, true, Offset(c.x - 3f * u, c.y + 1.2f * u), Size(6f * u, 4.4f * u))
        listOf(-1f, 1f).forEach { sg ->
            drawPath(Path().apply { moveTo(c.x + sg * 2.2f * u, c.y + 3.4f * u); quadraticTo(c.x + sg * 4.2f * u, c.y, c.x + sg * 2.8f * u, c.y - 3.6f * u) },
                brazos, style = Stroke(0.8f * u, cap = StrokeCap.Round))
        }
        drawLine(brazos, Offset(c.x - 3.3f * u, c.y - 2.8f * u), Offset(c.x + 3.3f * u, c.y - 2.8f * u), 0.7f * u, StrokeCap.Round)
        for (k in 0 until 4) {
            val x = c.x + (-1.2f + 0.8f * k) * u
            drawLine(cuerdas, Offset(x, c.y - 2.8f * u), Offset(x, c.y + 3.4f * u), 0.25f * u)
        }
    }

    /** La corona de hojas colgada, con sus dos cintas. */
    private fun DrawScope.corona(c: Offset, u: Float, hoja: Color, cinta: Color, clavo: Color) {
        drawCircle(clavo, 0.6f * u, Offset(c.x, c.y - 4.6f * u))
        drawLine(cinta, Offset(c.x - 0.6f * u, c.y + 3.2f * u), Offset(c.x - 1.6f * u, c.y + 7f * u), 0.7f * u, StrokeCap.Round)
        drawLine(cinta, Offset(c.x + 0.6f * u, c.y + 3.2f * u), Offset(c.x + 1.8f * u, c.y + 6.6f * u), 0.7f * u, StrokeCap.Round)
        for (k in 0 until 12) {
            val a = 2 * Math.PI * k / 12
            drawCircle(hoja, 0.95f * u, Offset(c.x + 3.4f * u * cos(a).toFloat(), c.y + 3.4f * u * sin(a).toFloat()))
        }
    }

    // ---------------------------------------------------------------- Mapas

    override fun DrawScope.muebleMapas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        fun m(c: Color, k: Float) = v(e.fondo, c, k, oscuro)
        fun s(c: Color, k: Float) = mez(e.fondo, c, k)
        val rnd = Random(e.semilla.toLong())

        // La mesa de madera, con unas vetas largas.
        drawRect(m(MADERA, 0.24f))
        for (k in 0 until 7) {
            val y = h * (k + 0.5f) / 7f + (rnd.nextFloat() - 0.5f) * 3f * u
            val amp = (0.6f + rnd.nextFloat()) * u
            drawPath(Path().apply { moveTo(0f, y); cubicTo(w * 0.3f, y - amp, w * 0.6f, y + amp, w, y - amp * 0.5f) }, m(MADERA_OSC, 0.22f), style = Stroke(0.5f * u))
        }

        // El pínax de bronce bajo el mapa: tenue por dentro, con el borde grueso, la greca y los remaches.
        val p = Rect(3.5f * u, 2.8f * u, w - 3.5f * u, h - 2.8f * u)
        val placa = m(BRONCE, 0.16f)
        drawRoundRect(placa, p.topLeft, p.size, CornerRadius(1.5f * u))
        // Los meridianos y paralelos grabados, casi invisibles (teñidos a partir de la placa misma).
        val grabado = mez(placa, if (oscuro) Color.Black else BRONCE_OSC, if (oscuro) 0.12f else 0.18f)
        for (k in 1 until 6) drawLine(grabado, Offset(p.left + p.width * k / 6f, p.top + 4f * u), Offset(p.left + p.width * k / 6f, p.bottom - 4f * u), 0.4f * u)
        for (k in 1 until 4) drawLine(grabado, Offset(p.left + 4f * u, p.top + p.height * k / 4f), Offset(p.right - 4f * u, p.top + p.height * k / 4f), 0.4f * u)
        drawRoundRect(m(BRONCE, 0.62f), p.topLeft, p.size, CornerRadius(1.5f * u), style = Stroke(2.2f * u))
        drawRoundRect(m(BRONCE_OSC, 0.55f), Offset(p.left + 2f * u, p.top + 2f * u), Size(p.width - 4f * u, p.height - 4f * u), CornerRadius(1f * u), style = Stroke(0.5f * u))
        greca(Rect(p.left + 6f * u, p.top + 2.6f * u, p.right - 6f * u, p.top + 5.4f * u), s(e.secundario, 0.45f), 0.4f * u)
        greca(Rect(p.left + 6f * u, p.bottom - 5.4f * u, p.right - 6f * u, p.bottom - 2.6f * u), s(e.secundario, 0.45f), 0.4f * u)
        listOf(Offset(p.left, p.top), Offset(p.right, p.top), Offset(p.left, p.bottom), Offset(p.right, p.bottom)).forEach { o ->
            drawCircle(m(BRONCE_OSC, 0.7f), 1.3f * u, o)
            drawCircle(m(ORO, 0.6f), 0.6f * u, Offset(o.x - 0.3f * u, o.y - 0.3f * u))
        }

        // Arriba a la izquierda, un rollo de papiro atado con su cinta.
        val r0 = Rect(7f * u, 0.6f * u, minOf(32f * u, w * 0.2f), 4f * u)
        drawRoundRect(m(PAPIRO, 0.75f), r0.topLeft, r0.size, CornerRadius(r0.height / 2))
        drawRoundRect(m(MURO_OSC, 0.6f), r0.topLeft, r0.size, CornerRadius(r0.height / 2), style = Stroke(0.4f * u))
        drawOval(m(MURO_OSC, 0.6f), Offset(r0.right - 1.8f * u, r0.top), Size(1.8f * u, r0.height), style = Stroke(0.4f * u))
        drawLine(s(e.acento, 0.7f), Offset(r0.left + r0.width * 0.4f, r0.top - 0.3f * u), Offset(r0.left + r0.width * 0.4f, r0.bottom + 0.3f * u), 0.8f * u)

        // Abajo a la derecha, el compás de puntas del geógrafo.
        val bisagra = Offset(w - 6.5f * u, h - 17f * u)
        drawLine(m(BRONCE_OSC, 0.75f), bisagra, Offset(w - 10.5f * u, h - 1.6f * u), 0.9f * u, StrokeCap.Round)
        drawLine(m(BRONCE_OSC, 0.75f), bisagra, Offset(w - 2.4f * u, h - 3f * u), 0.9f * u, StrokeCap.Round)
        drawCircle(m(ORO, 0.75f), 1.2f * u, bisagra)

        // Abajo a la izquierda, tres dracmas con el sol.
        listOf(Offset(7f * u, h - 5.5f * u), Offset(10.5f * u, h - 3.6f * u), Offset(6f * u, h - 2.2f * u)).forEach { o ->
            drawCircle(m(ORO, 0.7f), 1.9f * u, o)
            drawCircle(m(BRONCE_OSC, 0.6f), 1.9f * u, o, style = Stroke(0.35f * u))
            solVergina(o, 1.1f * u, m(BRONCE_OSC, 0.55f))
        }
    }

    // ---------------------------------------------------------------- Otras vidas

    override fun DrawScope.muebleOtrasVidas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        fun m(c: Color, k: Float) = v(e.fondo, c, k, oscuro)
        fun s(c: Color, k: Float) = mez(e.fondo, c, k)
        val (centros, paso) = centrosMedallas()
        val nw = paso * 0.84f; val tw = paso - nw
        val geison = 7f * u; val frisoTop = 9f * u; val frisoBot = 86f * u

        // El friso de mármol, tenue.
        drawRect(m(MARMOL, 0.30f), Offset(0f, frisoTop), Size(w, frisoBot - frisoTop))

        // Las metopas: un nicho hundido para cada medalla, con su sombra arriba y a la izquierda.
        centros.forEach { cx ->
            val r = Rect(cx - nw / 2, frisoTop, cx + nw / 2, frisoBot)
            drawRect(m(MARMOL_OSC, 0.10f), r.topLeft, r.size)
            drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.28f else 0.10f), Color.Transparent), r.top, r.top + 6f * u), r.topLeft, Size(r.width, 6f * u))
            drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.2f else 0.07f), Color.Transparent), r.left, r.left + 3f * u), r.topLeft, Size(3f * u, r.height))
            drawRect(m(MARMOL_SOMBRA, 0.45f), r.topLeft, r.size, style = Stroke(0.5f * u))
        }

        // Los triglifos pintados de azul entre las metopas y en las puntas.
        val bordes = mutableListOf<Float>()
        bordes += centros.first() - nw / 2 - tw
        centros.zipWithNext().forEach { (a, b) -> bordes += a + nw / 2 + (b - a - nw - tw) / 2 }
        bordes += centros.last() + nw / 2
        val azul = s(e.secundario, if (oscuro) 0.38f else 0.32f)
        val surco = m(BARNIZ, 0.38f)
        bordes.forEach { x ->
            if (tw > 1.5f * u) {
                drawRect(azul, Offset(x, frisoTop), Size(tw, frisoBot - frisoTop))
                drawRect(m(MARMOL, 0.4f), Offset(x, frisoTop), Size(tw, 2.2f * u))
                listOf(1f / 3f, 2f / 3f).forEach { f -> drawLine(surco, Offset(x + tw * f, frisoTop + 3.5f * u), Offset(x + tw * f, frisoBot), maxOf(0.5f * u, tw * 0.09f)) }
                drawLine(surco, Offset(x + 0.4f * u, frisoTop + 3.5f * u), Offset(x + 0.4f * u, frisoBot), 0.4f * u)
                drawLine(surco, Offset(x + tw - 0.4f * u, frisoTop + 3.5f * u), Offset(x + tw - 0.4f * u, frisoBot), 0.4f * u)
                // Los mútulos bajo la cornisa y la régula con sus gotas bajo la tenia.
                drawRect(m(MARMOL_SOMBRA, 0.5f), Offset(x - 0.5f * u, geison), Size(tw + 1f * u, frisoTop - geison))
                drawRect(m(MARMOL_OSC, 0.55f), Offset(x, frisoBot + 2.4f * u), Size(tw, 1.4f * u))
                for (g in 0 until 4) drawCircle(m(MARMOL_SOMBRA, 0.55f), 0.55f * u, Offset(x + tw * (g + 0.5f) / 4f, frisoBot + 4.4f * u))
            }
        }

        // La cornisa con la greca y el arquitrabe con la tenia.
        drawRect(m(MARMOL, 0.5f), Offset.Zero, Size(w, geison))
        drawLine(m(MARMOL_SOMBRA, 0.6f), Offset(0f, geison), Offset(w, geison), 0.6f * u)
        greca(Rect(0f, 1.4f * u, w, geison - 1.3f * u), s(e.secundario, 0.6f), 0.5f * u)
        drawRect(m(MARMOL_LUZ, 0.55f), Offset(0f, frisoBot), Size(w, 2.4f * u))
        drawLine(m(MARMOL_SOMBRA, 0.55f), Offset(0f, frisoBot), Offset(w, frisoBot), 0.6f * u)
        drawRect(m(MARMOL, 0.42f), Offset(0f, frisoBot + 2.4f * u), Size(w, h - frisoBot - 2.4f * u))
        drawLine(m(MARMOL_SOMBRA, 0.4f), Offset(0f, h - 3f * u), Offset(w, h - 3f * u), 0.5f * u)
        // Una franja púrpura fina, como en las casas de Macedonia.
        drawLine(s(e.acento, 0.45f), Offset(0f, h - 1f * u), Offset(w, h - 1f * u), 0.8f * u)
    }

    // ---------------------------------------------------------------- Íconos de mapa

    override fun DrawScope.iconoMapa(id: String, e: EstadoIcono, oscuro: Boolean) {
        val c = Offset(size.width / 2, size.height / 2)
        val s = size.height * 0.4f
        val g = s * 0.07f
        when (id) {
            "unesco" -> {
                // El Partenón: las tres gradas, seis columnas, el arquitrabe, el friso y el frontón con sus acroterias.
                for (k in 0 until 3) {
                    val an = s * (2.9f + 0.25f * k)
                    drawRect(e.c2, Offset(c.x - an / 2, c.y + s * (0.68f + 0.1f * k)), Size(an, s * 0.08f))
                }
                for (k in 0 until 6) {
                    val x = c.x + s * (-1.15f + 0.46f * k)
                    drawRect(e.c1, Offset(x - s * 0.1f, c.y - s * 0.26f), Size(s * 0.2f, s * 0.94f))
                    drawRect(e.c1, Offset(x - s * 0.15f, c.y - s * 0.32f), Size(s * 0.3f, s * 0.07f))
                }
                drawRect(e.c1, Offset(c.x - s * 1.38f, c.y - s * 0.45f), Size(s * 2.76f, s * 0.12f))
                drawRect(e.c1, Offset(c.x - s * 1.38f, c.y - s * 0.6f), Size(s * 2.76f, s * 0.13f))
                for (k in 0 until 9) drawRect(e.fondo, Offset(c.x - s * 1.3f + s * 0.3f * k + s * 0.05f, c.y - s * 0.57f), Size(s * 0.13f, s * 0.07f))
                val fronton = Path().apply { moveTo(c.x - s * 1.48f, c.y - s * 0.62f); lineTo(c.x, c.y - s * 0.98f); lineTo(c.x + s * 1.48f, c.y - s * 0.62f); close() }
                drawPath(fronton, e.c1)
                drawPath(Path().apply { moveTo(c.x - s * 1.1f, c.y - s * 0.67f); lineTo(c.x, c.y - s * 0.9f); lineTo(c.x + s * 1.1f, c.y - s * 0.67f); close() }, e.fondo.copy(alpha = 0.55f))
                solVergina(Offset(c.x, c.y - s * 0.74f), s * 0.07f, e.c2)
                drawCircle(e.c2, s * 0.06f, Offset(c.x, c.y - s * 1.0f))
                drawCircle(e.c2, s * 0.06f, Offset(c.x - s * 1.48f, c.y - s * 0.66f))
                drawCircle(e.c2, s * 0.06f, Offset(c.x + s * 1.48f, c.y - s * 0.66f))
            }
            "rascar" -> {
                // Un óstrakon con una franja raspada y la dracma que raspa al final.
                val tiesto = Path().apply {
                    moveTo(c.x - s * 1.5f, c.y - s * 0.55f); lineTo(c.x - s * 0.45f, c.y - s * 0.92f); lineTo(c.x + s * 0.85f, c.y - s * 0.78f)
                    lineTo(c.x + s * 1.35f, c.y + s * 0.05f); lineTo(c.x + s * 0.8f, c.y + s * 0.85f); lineTo(c.x - s * 0.55f, c.y + s * 0.9f)
                    lineTo(c.x - s * 1.4f, c.y + s * 0.38f); close()
                }
                drawPath(tiesto, e.c2.copy(alpha = 0.32f))
                clipPath(tiesto) {
                    val raspa = Path().apply {
                        moveTo(c.x - s * 1.1f, c.y - s * 0.4f)
                        var x = c.x - s * 1.1f; var arriba = false; var k = 0
                        while (x < c.x + s * 0.45f && k < 20) { x += s * 0.22f; lineTo(x, if (arriba) c.y - s * 0.42f else c.y + s * 0.32f); arriba = !arriba; k++ }
                    }
                    drawPath(raspa, e.c1, style = Stroke(s * 0.2f, cap = StrokeCap.Round))
                }
                drawPath(tiesto, e.c2, style = Stroke(g))
                val moneda = Offset(c.x + s * 0.85f, c.y + s * 0.42f)
                drawCircle(e.fondo, s * 0.47f, moneda)
                drawCircle(e.c1, s * 0.42f, moneda)
                drawCircle(e.fondo, s * 0.33f, moneda, style = Stroke(g * 0.7f))
                solVergina(moneda, s * 0.24f, e.fondo)
            }
            else -> {
                // Tus lugares: tres sellos de cera con el sol, unidos por un camino de puntos.
                val pts = listOf(Offset(c.x - s * 1.1f, c.y + s * 0.38f), Offset(c.x, c.y - s * 0.45f), Offset(c.x + s * 1.05f, c.y + s * 0.25f))
                val camino = Path().apply {
                    moveTo(pts[0].x, pts[0].y); quadraticTo(c.x - s * 0.7f, c.y - s * 0.4f, pts[1].x, pts[1].y)
                    quadraticTo(c.x + s * 0.7f, c.y - s * 0.4f, pts[2].x, pts[2].y)
                }
                drawPath(camino, e.c2, style = Stroke(g, cap = StrokeCap.Round, pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.1f, g * 2.6f))))
                pts.forEachIndexed { i, o ->
                    val r = s * (0.4f - 0.04f * i)
                    drawPath(sello(o, r), e.c1)
                    drawCircle(e.fondo.copy(alpha = 0.55f), r * 0.68f, o, style = Stroke(g * 0.6f))
                    solVergina(o, r * 0.46f, e.fondo)
                }
            }
        }
    }

    /** Un sello de cera: un círculo con el borde ondulado de la cera aplastada. */
    private fun sello(c: Offset, r: Float): Path = Path().apply {
        val n = 18
        for (k in 0 until n) {
            val a = 2 * Math.PI * k / n
            val rr = if (k % 2 == 0) r else r * 0.9f
            val x = c.x + rr * cos(a).toFloat(); val y = c.y + rr * sin(a).toFloat()
            if (k == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
    }

    // ---------------------------------------------------------------- Notas

    override fun DrawScope.tableroNotas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        fun m(c: Color, k: Float) = v(e.fondo, c, k, oscuro)
        fun s(c: Color, k: Float) = mez(e.fondo, c, k)
        val rnd = Random(e.semilla.toLong())

        // El muro de estuco ocre con sus hiladas de sillares a soga.
        drawRect(m(MURO, 0.34f))
        val alto = 12f * u; val largo = 30f * u
        var y = 6f * u; var fila = 0
        while (y < h - 5f * u && fila < 20) {
            drawLine(m(MURO_OSC, 0.32f), Offset(0f, y), Offset(w, y), 0.5f * u)
            var x = if (fila % 2 == 0) largo * 0.5f else 0f; var k = 0
            while (x < w && k < 40) {
                drawLine(m(MURO_OSC, 0.28f), Offset(x, y), Offset(x, minOf(y + alto, h - 5f * u)), 0.5f * u)
                x += largo; k++
            }
            y += alto; fila++
        }
        // Unos tiestos sueltos al pie de los lados.
        repeat(4) { i ->
            val x = if (i % 2 == 0) w * 0.012f + rnd.nextFloat() * w * 0.02f else w * 0.955f + rnd.nextFloat() * w * 0.02f
            val yy = h * (0.25f + 0.5f * rnd.nextFloat())
            val r = Rect(x, yy, x + 4f * u, yy + 3f * u)
            drawPath(tiesto(r, e.semilla + i, 0.2f), m(if (i % 2 == 0) TERRACOTA else TERRA_LUZ, 0.5f))
        }
        // La cornisa con la greca arriba y el zócalo azul del Egeo con su filete púrpura abajo.
        drawRect(m(MARMOL, 0.55f), Offset.Zero, Size(w, 5.5f * u))
        drawLine(m(MARMOL_SOMBRA, 0.6f), Offset(0f, 5.5f * u), Offset(w, 5.5f * u), 0.5f * u)
        greca(Rect(0f, 1.1f * u, w, 4.4f * u), s(e.secundario, 0.6f), 0.45f * u)
        drawRect(m(EGEO, 0.4f), Offset(0f, h - 5f * u), Size(w, 5f * u))
        drawLine(s(e.acento, 0.6f), Offset(0f, h - 5.6f * u), Offset(w, h - 5.6f * u), 0.8f * u)
    }

    override fun DrawScope.notaTablero(e: EstadoNota, oscuro: Boolean) {
        val r = e.rect
        translate(r.left, r.top) {
            val w = r.width; val h = r.height; val u = minOf(w, h) / 100f
            val papel = if (oscuro) BARRO_OSCURO else BARRO_CLARO
            val forma = tiesto(Rect(0f, 0f, w, h), e.indice * 31 + 5, 0.07f)
            // La sombra del tiesto sobre el muro.
            translate(1.2f * u, 2.2f * u) { drawPath(forma, Color.Black.copy(alpha = if (oscuro) 0.4f else 0.18f)) }
            drawPath(forma, papel)
            clipPath(forma) {
                // El barro, apenas más oscuro hacia un canto, y el barniz negro de la cara de afuera en el canto de abajo.
                drawRect(Brush.linearGradient(listOf(Color.White.copy(alpha = if (oscuro) 0.03f else 0.10f), Color.Transparent, TERRA_OSC.copy(alpha = 0.07f)), Offset.Zero, Offset(w, h)))
                drawRect(if (oscuro) BARNIZ.copy(alpha = 0.75f) else BARNIZ.copy(alpha = 0.82f), Offset(0f, h * 0.9f), Size(w, h * 0.1f))
                drawLine(TERRA_LUZ.copy(alpha = 0.5f), Offset(0f, h * 0.9f), Offset(w, h * 0.9f), 0.8f * u)
            }
            val borde = when {
                e.fijada -> e.acento
                oscuro -> mez(papel, TERRA_LUZ, 0.45f)
                else -> mez(papel, TERRA_OSC, 0.55f)
            }
            drawPath(forma, borde, style = Stroke(if (e.fijada) 2f * u else 1.1f * u))

            // El detalle del tipo, en la esquina de arriba a la derecha.
            val o = Offset(w * 0.9f, h * 0.11f); val rr = h * 0.065f
            val vivo = { c: Color -> if (oscuro) mez(c, Color.White, 0.25f) else c }
            when (e.tipo) {
                "idea" -> solVergina(o, rr, vivo(ORO))
                "pensamiento" -> drawPath(Path().apply {
                    moveTo(o.x - rr, o.y + rr); lineTo(o.x - rr, o.y - rr); lineTo(o.x + rr, o.y - rr); lineTo(o.x + rr, o.y + rr * 0.35f)
                    lineTo(o.x - rr * 0.2f, o.y + rr * 0.35f); lineTo(o.x - rr * 0.2f, o.y - rr * 0.3f)
                }, vivo(EGEO), style = Stroke(rr * 0.3f))
                "proposito" -> {
                    drawLine(vivo(HOJA), Offset(o.x - rr, o.y + rr), Offset(o.x + rr, o.y - rr), rr * 0.2f, StrokeCap.Round)
                    drawOval(vivo(HOJA), Offset(o.x - rr * 0.9f, o.y - rr * 0.65f), Size(rr * 0.9f, rr * 0.5f))
                    drawOval(vivo(HOJA), Offset(o.x - rr * 0.1f, o.y + rr * 0.05f), Size(rr * 0.9f, rr * 0.5f))
                }
                "recordatorio" -> {
                    drawCircle(vivo(TERRACOTA), rr * 0.75f, o)
                    drawCircle(if (oscuro) MARMOL else BARNIZ, rr * 0.75f, o, style = Stroke(rr * 0.22f))
                }
                else -> {}
            }
            // Si está fijada, un clavo de bronce arriba al centro.
            if (e.fijada) {
                val cl = Offset(w * 0.5f, h * 0.085f)
                drawCircle(Color.Black.copy(alpha = 0.3f), h * 0.05f, Offset(cl.x + 0.6f * u, cl.y + 0.9f * u))
                drawCircle(BRONCE, h * 0.048f, cl)
                drawCircle(BRONCE_OSC, h * 0.048f, cl, style = Stroke(0.6f * u))
                drawCircle(Color.White.copy(alpha = 0.5f), h * 0.016f, Offset(cl.x - h * 0.014f, cl.y - h * 0.014f))
            }
        }
    }

    override fun tintaNota(oscuro: Boolean): Color = if (oscuro) Color(0xFFF3E6D0) else Color(0xFF1E1408)
}
