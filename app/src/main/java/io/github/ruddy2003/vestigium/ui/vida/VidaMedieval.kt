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
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import kotlin.math.cos
import kotlin.math.sin

/**
 * Scriptorium: el monasterio del copista, con los materiales de su armario del Ideario y de su biblioteca: roble con veta y
 * herrajes de hierro, pergamino, tinta ferrogálica, rúbrica bermellón, lapislázuli, pan de oro y el piso de baldosas
 * vidriadas en damero.
 * - Social: el refectorio visto desde arriba. La mesa larga de roble con su canto moldurado, el mantel de lino (el nicho)
 *   orlado de bermellón con rombos de lapislázuli, las escudillas, las cucharas y los panes de cada comensal, las jarras,
 *   los candeleros encendidos en las esquinas, los bancos a lo largo y las dos sillas de respaldo con su cojín.
 * - Mapas: el escritorio del copista: el tablero de roble con su canto, las escuadras de hierro en las esquinas, el
 *   pergamino del mapa (el nicho) con su doble filete y sus pesos de plomo; alrededor, la regla de boj, el compás de latón,
 *   la pluma, los cuernos de tinta negra y roja, el cortaplumas, la vela con su halo y las conchas de pigmento; abajo, el
 *   faldón con su arquería ciega y la cerradura del cajón, y las patas torneadas sobre el piso.
 * - Otras vidas: el armarium: frontón con crochets y medallón de lapislázuli, cornisa con arquería ciega bermellón y azul,
 *   cinco hornacinas de medio punto forradas (el nicho) con sus columnillas, la repisa, el zócalo con sus tiradores y el
 *   piso de baldosas.
 * - Íconos: la fachada de una catedral, el pergamino con la esquina raspada y el lunellum, y tres sellos de cera sobre un
 *   camino de tinta.
 * - Notas: el tablón del capítulo: crestería, cornisa con medallones de cuadrifolio, montantes de roble con flejes de
 *   hierro y apliques con vela, el fondo de tablas de tilo (el nicho) y la repisa. Cada nota es una hoja de pergamino de
 *   borde barbado, pautada en rojo y clavada; su tipo se ve en la esquina (el punto de oro, el trébol azul, el calderón rojo
 *   o la cinta de registro con la esquina doblada) y, si está fijada, lleva un clavo dorado y un sello de lacre pendiente.
 */
internal object VidaMedieval : DisenoVida {
    private val ROBLE = Color(0xFF6E4C2C)
    private val ROBLE_LUZ = Color(0xFF94704A)
    private val ROBLE_OSC = Color(0xFF3B2715)
    private val HIERRO = Color(0xFF26231F)
    private val HIERRO_LUZ = Color(0xFF7A736A)
    private val PERGAMINO = Color(0xFFF1E6C8)
    private val PERG_OSC = Color(0xFFD6C397)
    private val BERMELLON = Color(0xFF9E1B1B)
    private val LAPIS = Color(0xFF1F3F8F)
    private val ORO = Color(0xFFC09A3A)
    private val ORO_LUZ = Color(0xFFE6C875)
    private val TINTA = Color(0xFF2A1B10)
    private val BARRO = Color(0xFF9A4E2E)
    private val OCRE = Color(0xFFC9A464)
    private val CUERNO = Color(0xFF8A6A40)
    private val BOJ = Color(0xFFD7B878)
    private val LATON = Color(0xFFC79F45)
    private val PLOMO = Color(0xFF5C5B5E)
    private val ACERO = Color(0xFFB9BCC0)
    private val LINO = Color(0xFFEFE7D2)
    private val TILO = Color(0xFFDCC59B)
    private val CERA = Color(0xFFF2E3B6)
    private val VELA = Color(0xFFF2C46B)
    private val NOCHE = Color(0xFF0E0A06)

    // ---------------------------------------------------------------- Ayudas

    /** Un número estable de 0 a 1: cada cosa tiene sus irregularidades, siempre las mismas. */
    private fun azar(semilla: Int, k: Int): Float {
        var x = semilla * 374761393 + k * 668265263
        x = (x xor (x ushr 13)) * 1274126177
        x = x xor (x ushr 16)
        return (x and 0x7fffffff) / 2147483647f
    }

    private fun mezcla(a: Color, b: Color, f: Float): Color = lerp(a, b, f.coerceIn(0f, 1f))

    /** El material a la luz del día o de noche: de noche, la madera y la piedra en penumbra de verdad. */
    private fun t(c: Color, oscuro: Boolean, cuanto: Float = 0.45f): Color = if (oscuro) mezcla(c, NOCHE, cuanto) else c

    /** El color sólido de un nicho: el material, llevado hasta la luminancia de la tarjeta. */
    private fun nicho(e: EstadoMueble, oscuro: Boolean, material: Color): Color =
        if (oscuro) mezcla(e.fondo, material, 0.12f) else mezcla(e.fondo, material, 0.45f)

    /** Un rectángulo dado en fracciones del lienzo, en píxeles. */
    private fun DrawScope.caja(r: Rect) = Rect(size.width * r.left, size.height * r.top, size.width * r.right, size.height * r.bottom)

    private fun Rect.crece(d: Float) = Rect(left - d, top - d, right + d, bottom + d)

    /** Vetas de madera: líneas onduladas a lo largo, separadas ≈ [paso], de opacidad y grosor variables. */
    private fun DrawScope.vetas(r: Rect, color: Color, semilla: Int, paso: Float, vertical: Boolean = false, grosor: Float = 1f) {
        val largo = if (vertical) r.height else r.width
        val ancho = if (vertical) r.width else r.height
        val n = (ancho / paso).toInt().coerceIn(2, 60)
        clipRect(r.left, r.top, r.right, r.bottom) {
            for (k in 0 until n) {
                val base = ancho * (k + azar(semilla, k)) / n
                val amp = paso * (0.15f + 0.45f * azar(semilla, k + 100))
                val fase = azar(semilla, k + 200) * 6.28f
                val ondas = 1.2f + 2.2f * azar(semilla, k + 300)
                val p = Path()
                for (s in 0..16) {
                    val a = largo * s / 16
                    val v = base + amp * sin(fase + ondas * 6.28f * s / 16)
                    if (s == 0) p.moveTo(if (vertical) r.left + v else r.left + a, if (vertical) r.top + a else r.top + v)
                    else p.lineTo(if (vertical) r.left + v else r.left + a, if (vertical) r.top + a else r.top + v)
                }
                drawPath(p, color.copy(alpha = color.alpha * (0.3f + 0.7f * azar(semilla, k + 400))), style = Stroke(grosor * (0.5f + azar(semilla, k + 500))))
            }
        }
    }

    /** Una tabla de madera con veta, luz arriba y sombra abajo. */
    private fun DrawScope.tabla(r: Rect, base: Color, oscuro: Boolean, semilla: Int, u: Float, vertical: Boolean = false) {
        drawRect(t(base, oscuro), r.topLeft, r.size)
        vetas(r, t(ROBLE_OSC, oscuro).copy(alpha = 0.45f), semilla, 1.3f * u, vertical, 0.35f * u)
        drawRect(Color.White.copy(alpha = if (oscuro) 0.06f else 0.16f), r.topLeft, Size(r.width, 0.35f * u))
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(r.left, r.bottom - 0.35f * u), Size(r.width, 0.35f * u))
    }

    /** Una mancha radial suave (la luz de la vela). */
    private fun DrawScope.halo(c: Offset, r: Float, color: Color) =
        drawCircle(Brush.radialGradient(listOf(color, color.copy(alpha = 0f)), c, r.coerceAtLeast(1f)), r, c)

    /** Un remache o un clavo de cabeza redonda, con su brillo. */
    private fun DrawScope.clavo(c: Offset, r: Float, oscuro: Boolean, metal: Color = HIERRO, luz: Color = HIERRO_LUZ) {
        drawCircle(Color.Black.copy(alpha = 0.35f), r, c + Offset(r * 0.35f, r * 0.45f))
        drawCircle(t(metal, oscuro, 0.3f), r, c)
        drawCircle(t(luz, oscuro, 0.3f), r * 0.42f, c - Offset(r * 0.3f, r * 0.3f))
    }

    /** El piso de baldosas vidriadas, rojas y ocres, en damero. */
    private fun DrawScope.damero(r: Rect, lado: Float, oscuro: Boolean) {
        clipRect(r.left, r.top, r.right, r.bottom) {
            var y = r.top; var f = 0
            while (y < r.bottom) {
                var x = r.left; var k = 0
                while (x < r.right) {
                    val c = if ((k + f) % 2 == 0) BARRO else OCRE
                    drawRect(t(mezcla(c, Color.Black, 0.06f * azar(f, k)), oscuro, 0.55f), Offset(x, y), Size(lado + 0.5f, lado + 0.5f))
                    drawRect(Color.White.copy(alpha = if (oscuro) 0.03f else 0.1f), Offset(x + lado * 0.12f, y + lado * 0.12f), Size(lado * 0.35f, lado * 0.12f))
                    x += lado; k++
                }
                y += lado; f++
            }
        }
    }

    /** Un contorno de pergamino de borde barbado dentro de [r]. */
    private fun barbado(r: Rect, semilla: Int, puntos: Int, hondo: Float): Path = Path().apply {
        for (k in 0 until puntos) {
            val q = k / puntos.toFloat()
            val (x, y) = when {
                q < 0.25f -> r.left + r.width * (q / 0.25f) to r.top
                q < 0.5f -> r.right to r.top + r.height * ((q - 0.25f) / 0.25f)
                q < 0.75f -> r.right - r.width * ((q - 0.5f) / 0.25f) to r.bottom
                else -> r.left to r.bottom - r.height * ((q - 0.75f) / 0.25f)
            }
            val adentro = minOf(r.width, r.height) * hondo * (0.25f + 0.75f * azar(semilla, k)) * (if (k % 4 == 0) 1.8f else 1f)
            val px = x + (if (x <= r.left + 0.5f) adentro else if (x >= r.right - 0.5f) -adentro else 0f)
            val py = y + (if (y <= r.top + 0.5f) adentro else if (y >= r.bottom - 0.5f) -adentro else 0f)
            if (k == 0) moveTo(px, py) else lineTo(px, py)
        }
        close()
    }

    /** Un arco de medio punto: [r] con la parte de arriba redonda. */
    private fun medioPunto(r: Rect): Path = Path().apply {
        val rad = r.width / 2
        moveTo(r.left, r.bottom); lineTo(r.left, r.top + rad)
        arcTo(Rect(r.left, r.top, r.right, r.top + 2 * rad), 180f, 180f, false)
        lineTo(r.right, r.bottom); close()
    }

    /** Una estrella de [puntas] puntas. */
    private fun estrella(c: Offset, r: Float, puntas: Int, hondo: Float): Path = Path().apply {
        for (k in 0 until puntas * 2) {
            val a = Math.toRadians(-90.0 + k * 180.0 / puntas)
            val rr = if (k % 2 == 0) r else r * hondo
            val x = c.x + rr * cos(a).toFloat(); val y = c.y + rr * sin(a).toFloat()
            if (k == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
    }

    /** El cuadrifolio: cuatro lóbulos alrededor de un centro. */
    private fun DrawScope.cuadrifolio(c: Offset, r: Float, color: Color, centro: Color) {
        listOf(Offset(0f, -1f), Offset(1f, 0f), Offset(0f, 1f), Offset(-1f, 0f)).forEach { d -> drawCircle(color, r * 0.5f, c + d * (r * 0.5f)) }
        drawCircle(centro, r * 0.28f, c)
    }

    /** Un rombo centrado en [c]. */
    private fun rombo(c: Offset, a: Float, b: Float) = Path().apply { moveTo(c.x, c.y - b); lineTo(c.x + a, c.y); lineTo(c.x, c.y + b); lineTo(c.x - a, c.y); close() }

    /** Una llama de vela vista de frente, con su halo. */
    private fun DrawScope.llama(base: Offset, alto: Float, oscuro: Boolean) {
        halo(base - Offset(0f, alto * 0.5f), alto * 3.2f, VELA.copy(alpha = if (oscuro) 0.42f else 0.3f))
        val p = Path().apply {
            moveTo(base.x, base.y - alto)
            quadraticTo(base.x + alto * 0.42f, base.y - alto * 0.3f, base.x, base.y)
            quadraticTo(base.x - alto * 0.42f, base.y - alto * 0.3f, base.x, base.y - alto)
            close()
        }
        drawPath(p, Color(0xFFF5A62E))
        drawOval(Color(0xFFFFF3C4), Offset(base.x - alto * 0.12f, base.y - alto * 0.55f), Size(alto * 0.24f, alto * 0.45f))
    }

    // ---------------------------------------------------------------- Nichos

    override fun area(m: MuebleVida): Rect = when (m) {
        MuebleVida.SOCIAL -> Rect(0.11f, 0.17f, 0.89f, 0.83f)
        MuebleVida.MAPAS -> Rect(0.11f, 0.13f, 0.89f, 0.78f)
        MuebleVida.OTRAS_VIDAS -> Rect(0.035f, 0.21f, 0.965f, 0.80f)
        MuebleVida.NOTAS -> Rect(0.06f, 0.13f, 0.94f, 0.90f)
    }

    // ---------------------------------------------------------------- Social

    override fun DrawScope.muebleSocial(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val n = caja(area(MuebleVida.SOCIAL))
        // El piso del refectorio, de baldosas en damero, más oscuro hacia los bordes.
        damero(Rect(0f, 0f, w, h), 5.2f * u, oscuro)
        drawRect(Brush.radialGradient(listOf(Color.Transparent, Color.Black.copy(alpha = if (oscuro) 0.45f else 0.25f)), Offset(w / 2, h / 2), w * 0.62f))
        // Los bancos corridos a lo largo de la mesa, con los travesaños de sus patas asomando.
        listOf(Rect(w * 0.15f, h * 0.008f, w * 0.85f, h * 0.058f), Rect(w * 0.15f, h * 0.942f, w * 0.85f, h * 0.992f)).forEachIndexed { i, b ->
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(b.left + 0.6f * u, b.top + 0.8f * u), b.size)
            listOf(b.left + b.width * 0.06f, b.right - b.width * 0.06f - 1.6f * u).forEach { x ->
                drawRect(t(ROBLE_OSC, oscuro), Offset(x, b.top - 0.7f * u), Size(1.6f * u, b.height + 1.4f * u))
            }
            tabla(b, ROBLE_LUZ, oscuro, 30 + i, u)
            drawRect(t(ROBLE_OSC, oscuro), b.topLeft, b.size, style = Stroke(0.35f * u))
        }
        // Las dos sillas de respaldo en las cabeceras, con su cojín.
        listOf(true, false).forEach { izq ->
            val s = if (izq) Rect(w * 0.006f, h * 0.37f, w * 0.054f, h * 0.63f) else Rect(w * 0.946f, h * 0.37f, w * 0.994f, h * 0.63f)
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(s.left + 0.6f * u, s.top + 0.8f * u), s.size)
            tabla(s, ROBLE, oscuro, if (izq) 41 else 42, u, vertical = true)
            val resp = if (izq) Rect(s.left, s.top - 1f * u, s.left + s.width * 0.34f, s.bottom + 1f * u) else Rect(s.right - s.width * 0.34f, s.top - 1f * u, s.right, s.bottom + 1f * u)
            tabla(resp, ROBLE_OSC, oscuro, if (izq) 43 else 44, u, vertical = true)
            listOf(resp.top, resp.bottom).forEach { y -> clavo(Offset(resp.center.x, y), resp.width * 0.42f, oscuro, ORO, ORO_LUZ) }
            val coj = Rect(if (izq) resp.right + 0.5f * u else s.left + 0.6f * u, s.top + 0.9f * u, if (izq) s.right - 0.6f * u else resp.left - 0.5f * u, s.bottom - 0.9f * u)
            drawRoundRect(t(if (izq) BERMELLON else LAPIS, oscuro, 0.35f), coj.topLeft, coj.size, CornerRadius(1.2f * u))
            drawRoundRect(Color.White.copy(alpha = 0.14f), Offset(coj.left + 0.5f * u, coj.top + 0.5f * u), Size(coj.width * 0.4f, coj.height - 1f * u), CornerRadius(1f * u))
            drawRoundRect(t(ORO, oscuro, 0.3f), coj.topLeft, coj.size, CornerRadius(1.2f * u), style = Stroke(0.3f * u))
            listOf(coj.topLeft, Offset(coj.right, coj.top), Offset(coj.left, coj.bottom), coj.bottomRight).forEach { c -> drawCircle(t(ORO, oscuro, 0.3f), 0.55f * u, c) }
            drawCircle(t(ORO_LUZ, oscuro, 0.3f), 0.45f * u, coj.center)
        }
        // La mesa larga: sombra, tablero de tablas a lo largo y el canto con su moldura.
        val mesa = Rect(w * 0.062f, h * 0.075f, w * 0.938f, h * 0.925f)
        drawRoundRect(Color.Black.copy(alpha = 0.35f), Offset(mesa.left + 1f * u, mesa.top + 1.4f * u), mesa.size, CornerRadius(1.2f * u))
        drawRoundRect(t(ROBLE_OSC, oscuro), mesa.topLeft, mesa.size, CornerRadius(1.2f * u))
        val tablero = mesa.crece(-0.7f * u)
        val tablas = 5
        for (k in 0 until tablas) {
            val r = Rect(tablero.left, tablero.top + tablero.height * k / tablas, tablero.right, tablero.top + tablero.height * (k + 1) / tablas)
            drawRect(t(mezcla(ROBLE, ROBLE_LUZ, 0.15f + 0.3f * azar(e.semilla, k)), oscuro), r.topLeft, r.size)
            vetas(r, t(ROBLE_OSC, oscuro).copy(alpha = 0.5f), e.semilla * 7 + k, 1.2f * u, grosor = 0.3f * u)
            drawLine(Color.Black.copy(alpha = 0.45f), Offset(r.left, r.top), Offset(r.right, r.top), 0.3f * u)
        }
        drawRect(t(ROBLE_LUZ, oscuro).copy(alpha = 0.7f), tablero.topLeft, tablero.size, style = Stroke(0.45f * u))
        drawRect(Color.Black.copy(alpha = 0.3f), tablero.crece(-0.9f * u).topLeft, tablero.crece(-0.9f * u).size, style = Stroke(0.25f * u))
        // El mantel de lino con su orla bermellón de rombos azules; su campo liso es el nicho.
        val b = 1.5f * u
        val pano = n.crece(b + 0.9f * u)
        drawRect(Color.Black.copy(alpha = 0.22f), Offset(pano.left + 0.4f * u, pano.top + 0.6f * u), pano.size)
        drawRect(if (oscuro) mezcla(e.fondo, LINO, 0.2f) else mezcla(LINO, e.fondo, 0.3f), pano.topLeft, pano.size)
        val orla = n.crece(b)
        drawRect(t(BERMELLON, oscuro, 0.35f), orla.topLeft, orla.size)
        val paso = 3.6f * u
        fun rombos(a: Offset, z: Offset) {
            val largo = (z - a).getDistance(); val k = (largo / paso).toInt()
            for (i in 0..k) {
                val c = a + (z - a) * (i / k.toFloat())
                drawPath(rombo(c, b * 0.42f, b * 0.42f), t(LAPIS, oscuro, 0.3f))
                drawCircle(t(ORO_LUZ, oscuro, 0.3f), b * 0.12f, c)
            }
        }
        val m = b / 2
        rombos(Offset(orla.left + m, orla.top + m), Offset(orla.right - m, orla.top + m))
        rombos(Offset(orla.left + m, orla.bottom - m), Offset(orla.right - m, orla.bottom - m))
        rombos(Offset(orla.left + m, orla.top + m), Offset(orla.left + m, orla.bottom - m))
        rombos(Offset(orla.right - m, orla.top + m), Offset(orla.right - m, orla.bottom - m))
        drawRect(nicho(e, oscuro, LINO), n.topLeft, n.size)
        drawRect(t(ORO, oscuro, 0.3f), n.topLeft, n.size, style = Stroke(0.3f * u))
        // La trama del lino y los dobleces del mantel, apenas.
        clipRect(n.left, n.top, n.right, n.bottom) {
            var y = n.top
            while (y < n.bottom) { drawLine(Color.Black.copy(alpha = 0.025f), Offset(n.left, y), Offset(n.right, y), 0.25f * u); y += 1.3f * u }
            drawLine(Color.Black.copy(alpha = 0.05f), Offset(n.center.x, n.top), Offset(n.center.x, n.bottom), 0.5f * u)
            drawLine(Color.White.copy(alpha = if (oscuro) 0.03f else 0.4f), Offset(n.center.x + 0.5f * u, n.top), Offset(n.center.x + 0.5f * u, n.bottom), 0.4f * u)
            drawLine(Color.Black.copy(alpha = 0.04f), Offset(n.left, n.center.y), Offset(n.right, n.center.y), 0.5f * u)
        }
        // Los cubiertos de cada comensal en el canto de la mesa: la escudilla, la cuchara y el pan.
        val yArriba = (tablero.top + pano.top) / 2; val yAbajo = (tablero.bottom + pano.bottom) / 2
        val hueco = minOf(pano.top - tablero.top, tablero.bottom - pano.bottom)
        val rr = minOf(2f * u, hueco * 0.32f)
        listOf(0.22f, 0.38f, 0.54f, 0.70f).forEachIndexed { i, fx ->
            listOf(yArriba, yAbajo).forEachIndexed { j, y ->
                val x = w * (fx + 0.04f * j)
                // Escudilla de madera torneada.
                drawCircle(Color.Black.copy(alpha = 0.3f), rr, Offset(x + 0.3f * u, y + 0.4f * u))
                drawCircle(t(ROBLE_LUZ, oscuro), rr, Offset(x, y))
                drawCircle(t(ROBLE_OSC, oscuro), rr * 0.68f, Offset(x, y))
                drawCircle(t(mezcla(ROBLE_LUZ, OCRE, 0.4f), oscuro), rr * 0.55f, Offset(x, y))
                // La cuchara de cuerno.
                drawOval(t(CUERNO, oscuro), Offset(x + rr * 1.5f, y - rr * 0.38f), Size(rr * 0.9f, rr * 0.76f))
                drawLine(t(CUERNO, oscuro), Offset(x + rr * 2.3f, y), Offset(x + rr * 3.6f, y), 0.3f * u, StrokeCap.Round)
                // El pan redondo, con su corte en cruz.
                if ((i + j) % 2 == 0) {
                    val pc = Offset(x - rr * 2.3f, y)
                    drawCircle(t(Color(0xFFC08A4A), oscuro), rr * 0.85f, pc)
                    drawCircle(Color.White.copy(alpha = 0.18f), rr * 0.4f, pc - Offset(rr * 0.25f, rr * 0.25f))
                    drawLine(t(Color(0xFF8A5A2A), oscuro), pc - Offset(rr * 0.5f, 0f), pc + Offset(rr * 0.5f, 0f), 0.2f * u)
                    drawLine(t(Color(0xFF8A5A2A), oscuro), pc - Offset(0f, rr * 0.5f), pc + Offset(0f, rr * 0.5f), 0.2f * u)
                }
            }
        }
        // En las cabeceras, una jarra de peltre y una fuente con panes.
        listOf(tablero.left to pano.left, pano.right to tablero.right).forEachIndexed { i, (a, z) ->
            val x = (a + z) / 2; val r = minOf(1.5f * u, (z - a) * 0.36f)
            val jarra = Offset(x, h * 0.42f)
            drawCircle(Color.Black.copy(alpha = 0.3f), r, jarra + Offset(0.3f * u, 0.5f * u))
            drawLine(t(PLOMO, oscuro, 0.3f), jarra, jarra + Offset(0f, r * 1.7f), 0.5f * u, StrokeCap.Round)
            drawCircle(t(Color(0xFF9B9A9C), oscuro, 0.3f), r, jarra)
            drawCircle(t(PLOMO, oscuro, 0.3f), r * 0.62f, jarra)
            drawCircle(Color.White.copy(alpha = 0.35f), r * 0.25f, jarra - Offset(r * 0.35f, r * 0.35f))
            val fuente = Offset(x, h * 0.6f)
            drawCircle(Color.Black.copy(alpha = 0.3f), r, fuente + Offset(0.3f * u, 0.5f * u))
            drawCircle(t(if (i == 0) Color(0xFF9B9A9C) else ROBLE_LUZ, oscuro, 0.3f), r, fuente)
            drawCircle(t(Color(0xFFC08A4A), oscuro), r * 0.45f, fuente + Offset(-r * 0.2f, -r * 0.15f))
            drawCircle(t(Color(0xFFB07A3A), oscuro), r * 0.4f, fuente + Offset(r * 0.25f, r * 0.2f))
        }
        // Los candeleros de latón en las cuatro esquinas, encendidos.
        listOf((tablero.left + pano.left) / 2, (pano.right + tablero.right) / 2).forEach { x ->
            listOf((tablero.top + pano.top) / 2, (pano.bottom + tablero.bottom) / 2).forEach { y ->
                val c = Offset(x, y)
                halo(c, 9f * u, VELA.copy(alpha = if (oscuro) 0.4f else 0.28f))
                drawCircle(Color.Black.copy(alpha = 0.3f), rr * 1.05f, c + Offset(0.3f * u, 0.4f * u))
                drawCircle(t(LATON, oscuro, 0.25f), rr * 1.05f, c)
                drawCircle(t(mezcla(LATON, Color.Black, 0.3f), oscuro, 0.25f), rr * 1.05f, c, style = Stroke(0.25f * u))
                drawCircle(CERA, rr * 0.5f, c)
                drawCircle(Color(0xFFFFE6A0), rr * 0.28f, c)
                drawCircle(Color(0xFFF5A62E), rr * 0.12f, c)
            }
        }
    }

    // ---------------------------------------------------------------- Mapas

    override fun DrawScope.muebleMapas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val n = caja(area(MuebleVida.MAPAS))
        // El piso del scriptorium, en penumbra detrás del escritorio.
        damero(Rect(0f, 0f, w, h), 5f * u, oscuro)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.3f))
        // Las patas torneadas, con su sombra en el piso.
        val tope = h * 0.86f; val faldon = h * 0.935f
        listOf(w * 0.055f, w * 0.945f - 3.4f * u).forEach { x ->
            drawOval(Color.Black.copy(alpha = 0.4f), Offset(x - 1.5f * u, h - 1.6f * u), Size(6.4f * u, 2.2f * u))
            drawRect(t(ROBLE, oscuro), Offset(x, faldon), Size(3.4f * u, h - faldon))
            listOf(0.25f, 0.6f).forEach { f ->
                drawRoundRect(t(ROBLE_LUZ, oscuro), Offset(x - 0.6f * u, faldon + (h - faldon) * f), Size(4.6f * u, 1.3f * u), CornerRadius(0.6f * u))
            }
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(x + 2.3f * u, faldon), Size(1.1f * u, h - faldon))
        }
        // El faldón: una tabla con su arquería ciega tallada y la cerradura del cajón al centro.
        val fal = Rect(w * 0.035f, tope, w * 0.965f, faldon)
        tabla(fal, ROBLE, oscuro, 61, u)
        val na = 14
        val aw = fal.width / na
        for (k in 0 until na) {
            val r = Rect(fal.left + aw * k + aw * 0.18f, fal.top + fal.height * 0.25f, fal.left + aw * (k + 1) - aw * 0.18f, fal.bottom - fal.height * 0.15f)
            if (k == na / 2 - 1 || k == na / 2) continue
            drawPath(medioPunto(r), t(ROBLE_OSC, oscuro))
            drawPath(medioPunto(r.translate(0.25f * u, 0.25f * u)), Color.White.copy(alpha = if (oscuro) 0.04f else 0.1f), style = Stroke(0.25f * u))
        }
        val cer = Rect(fal.center.x - 3.4f * u, fal.top + fal.height * 0.12f, fal.center.x + 3.4f * u, fal.bottom - fal.height * 0.12f)
        drawRect(t(HIERRO, oscuro, 0.3f), cer.topLeft, cer.size)
        drawRect(t(HIERRO_LUZ, oscuro, 0.3f), cer.topLeft, cer.size, style = Stroke(0.25f * u))
        drawCircle(Color.Black, 0.55f * u, Offset(cer.center.x, cer.center.y - 0.3f * u))
        drawRect(Color.Black, Offset(cer.center.x - 0.25f * u, cer.center.y), Size(0.5f * u, 1.1f * u))
        listOf(cer.left + 0.8f * u, cer.right - 0.8f * u).forEach { x -> clavo(Offset(x, cer.center.y), 0.45f * u, oscuro) }
        // El tablero del escritorio con su canto moldurado: el borde oscuro, la moldura clara y las tablas a lo largo.
        val top = Rect(w * 0.015f, h * 0.015f, w * 0.985f, tope)
        drawRect(Color.Black.copy(alpha = 0.4f), Offset(top.left, top.bottom), Size(top.width, 1f * u))
        drawRoundRect(t(ROBLE_OSC, oscuro), top.topLeft, top.size, CornerRadius(1f * u))
        val canto = top.crece(-0.6f * u)
        drawRoundRect(t(ROBLE_LUZ, oscuro), canto.topLeft, canto.size, CornerRadius(0.8f * u))
        val campo = canto.crece(-1.1f * u)
        for (k in 0 until 4) {
            val r = Rect(campo.left, campo.top + campo.height * k / 4, campo.right, campo.top + campo.height * (k + 1) / 4)
            drawRect(t(mezcla(ROBLE, ROBLE_LUZ, 0.1f + 0.3f * azar(e.semilla, k)), oscuro), r.topLeft, r.size)
            vetas(r, t(ROBLE_OSC, oscuro).copy(alpha = 0.5f), e.semilla * 5 + k, 1.2f * u, grosor = 0.3f * u)
            drawLine(Color.Black.copy(alpha = 0.45f), Offset(r.left, r.top), Offset(r.right, r.top), 0.3f * u)
        }
        drawRect(Color.Black.copy(alpha = 0.35f), campo.topLeft, campo.size, style = Stroke(0.3f * u))
        // Las escuadras de hierro de las esquinas, remachadas.
        val ee = 6f * u; val eg = 1.5f * u
        listOf(campo.topLeft to Offset(1f, 1f), Offset(campo.right, campo.top) to Offset(-1f, 1f), Offset(campo.left, campo.bottom) to Offset(1f, -1f), campo.bottomRight to Offset(-1f, -1f)).forEach { (c, d) ->
            val x0 = if (d.x > 0) c.x else c.x - ee; val y0 = if (d.y > 0) c.y else c.y - eg
            drawRect(t(HIERRO, oscuro, 0.3f), Offset(x0, y0), Size(ee, eg))
            drawRect(t(HIERRO, oscuro, 0.3f), Offset(if (d.x > 0) c.x else c.x - eg, if (d.y > 0) c.y else c.y - ee), Size(eg, ee))
            clavo(c + Offset(d.x * eg * 0.5f, d.y * eg * 0.5f), 0.45f * u, oscuro)
            clavo(c + Offset(d.x * (ee - eg * 0.6f), d.y * eg * 0.5f), 0.45f * u, oscuro)
            clavo(c + Offset(d.x * eg * 0.5f, d.y * (ee - eg * 0.6f)), 0.45f * u, oscuro)
        }
        // El pergamino del mapa, de borde barbado, con su doble filete rojo y azul; el campo es el nicho.
        val perg = n.crece(2.6f * u)
        val forma = barbado(perg, e.semilla, 36, 0.012f)
        translate(0.6f * u, 0.9f * u) { drawPath(forma, Color.Black.copy(alpha = 0.35f)) }
        val colorPerg = nicho(e, oscuro, PERGAMINO)
        drawPath(forma, colorPerg)
        drawPath(forma, t(PERG_OSC, oscuro, 0.3f).copy(alpha = 0.7f), style = Stroke(0.6f * u))
        drawRect(t(BERMELLON, oscuro, 0.3f), n.crece(1.5f * u).topLeft, n.crece(1.5f * u).size, style = Stroke(0.35f * u))
        drawRect(t(LAPIS, oscuro, 0.3f), n.crece(0.9f * u).topLeft, n.crece(0.9f * u).size, style = Stroke(0.25f * u))
        drawRect(colorPerg, n.topLeft, n.size)
        // Los pesos de plomo en las esquinas del pergamino.
        listOf(perg.topLeft, Offset(perg.right, perg.top), Offset(perg.left, perg.bottom), perg.bottomRight).forEach { c ->
            val p = c + Offset(if (c.x < w / 2) 1.1f * u else -1.1f * u, if (c.y < h / 2) 1.1f * u else -1.1f * u)
            clavo(p, 1.25f * u, oscuro, PLOMO, Color(0xFFB4B3B8))
        }
        // Arriba, sobre el tablero: la pluma, la regla de boj y el compás de latón.
        val ys = (campo.top + perg.top) / 2
        val alto = perg.top - campo.top
        val regla = Rect(w * 0.3f, ys - alto * 0.18f, w * 0.58f, ys + alto * 0.18f)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(regla.left + 0.4f * u, regla.top + 0.5f * u), regla.size)
        drawRect(t(BOJ, oscuro), regla.topLeft, regla.size)
        drawRect(Color.White.copy(alpha = 0.2f), regla.topLeft, Size(regla.width, regla.height * 0.3f))
        var x = regla.left + 1f * u; var k = 0
        while (x < regla.right - 0.5f * u) {
            drawLine(t(TINTA, oscuro, 0.2f), Offset(x, regla.bottom), Offset(x, regla.bottom - regla.height * (if (k % 4 == 0) 0.6f else 0.3f)), 0.15f * u)
            x += 1.1f * u; k++
        }
        drawRect(t(ROBLE_OSC, oscuro), regla.topLeft, regla.size, style = Stroke(0.2f * u))
        val bisagra = Offset(w * 0.64f, ys)
        listOf(-1f, 1f).forEach { s ->
            val punta = Offset(w * 0.78f, ys + s * alto * 0.3f)
            drawLine(Color.Black.copy(alpha = 0.3f), bisagra + Offset(0.3f * u, 0.4f * u), punta + Offset(0.3f * u, 0.4f * u), 0.7f * u, StrokeCap.Round)
            drawLine(t(LATON, oscuro, 0.25f), bisagra, punta, 0.65f * u, StrokeCap.Round)
            drawLine(t(ACERO, oscuro, 0.25f), punta - (punta - bisagra) * 0.12f, punta, 0.35f * u, StrokeCap.Round)
        }
        drawCircle(t(LATON, oscuro, 0.25f), 1f * u, bisagra)
        drawCircle(t(mezcla(LATON, Color.Black, 0.4f), oscuro, 0.25f), 0.4f * u, bisagra)
        pluma(Offset(w * 0.1f, ys + alto * 0.3f), Offset(w * 0.26f, ys - alto * 0.32f), oscuro, u)
        // A la izquierda, los cuernos de tinta negra y roja, y el cortaplumas.
        val xl = (campo.left + perg.left) / 2
        val anchoL = perg.left - campo.left
        listOf(Triple(h * 0.36f, TINTA, 1f), Triple(h * 0.52f, BERMELLON, 0.8f)).forEach { (y, tinta, f) ->
            val r = minOf(2.4f * u, anchoL * 0.34f) * f
            val c = Offset(xl, y)
            drawCircle(Color.Black.copy(alpha = 0.35f), r, c + Offset(0.4f * u, 0.6f * u))
            drawCircle(t(CUERNO, oscuro), r, c)
            drawCircle(t(mezcla(CUERNO, Color.White, 0.25f), oscuro), r, c, style = Stroke(0.35f * u))
            drawCircle(t(LATON, oscuro, 0.25f), r * 0.75f, c, style = Stroke(0.3f * u))
            drawCircle(t(mezcla(tinta, Color.Black, 0.2f), oscuro, 0.2f), r * 0.55f, c)
            drawCircle(Color.White.copy(alpha = 0.35f), r * 0.15f, c - Offset(r * 0.2f, r * 0.2f))
        }
        val cp = Rect(xl - 0.6f * u, h * 0.62f, xl + 0.6f * u, h * 0.8f)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(cp.left + 0.3f * u, cp.top + 0.4f * u), cp.size)
        drawRect(t(ROBLE_OSC, oscuro), Offset(cp.left - 0.2f * u, cp.top + cp.height * 0.45f), Size(cp.width + 0.4f * u, cp.height * 0.55f))
        drawPath(Path().apply { moveTo(cp.left, cp.top + cp.height * 0.45f); lineTo(cp.left, cp.top + cp.height * 0.08f); quadraticTo(cp.left, cp.top, cp.right, cp.top); lineTo(cp.right, cp.top + cp.height * 0.45f); close() }, t(ACERO, oscuro, 0.3f))
        drawLine(t(LATON, oscuro, 0.25f), Offset(cp.left - 0.3f * u, cp.top + cp.height * 0.45f), Offset(cp.right + 0.3f * u, cp.top + cp.height * 0.45f), 0.35f * u)
        // A la derecha, la vela en su palmatoria, encendida, y las conchas con los pigmentos.
        val xr = (perg.right + campo.right) / 2
        val anchoR = campo.right - perg.right
        val vela = Offset(xr, h * 0.34f)
        val rp = minOf(2.6f * u, anchoR * 0.38f)
        drawCircle(Color.Black.copy(alpha = 0.35f), rp, vela + Offset(0.4f * u, 0.6f * u))
        drawCircle(t(LATON, oscuro, 0.25f), rp, vela)
        drawCircle(t(mezcla(LATON, Color.Black, 0.35f), oscuro, 0.25f), rp, vela, style = Stroke(0.3f * u))
        drawRect(CERA, Offset(vela.x - rp * 0.32f, vela.y - rp * 1.9f), Size(rp * 0.64f, rp * 1.9f))
        drawRect(Color.Black.copy(alpha = 0.12f), Offset(vela.x + rp * 0.1f, vela.y - rp * 1.9f), Size(rp * 0.22f, rp * 1.9f))
        drawOval(Color(0xFFFFF6DA), Offset(vela.x - rp * 0.32f, vela.y - rp * 2.05f), Size(rp * 0.64f, rp * 0.3f))
        llama(Offset(vela.x, vela.y - rp * 2f), rp * 1.1f, oscuro)
        listOf(Triple(0.56f, BERMELLON, 1f), Triple(0.68f, LAPIS, 0.9f), Triple(0.78f, ORO, 0.75f)).forEach { (fy, pig, f) ->
            val c = Offset(xr, h * fy); val r = minOf(2.2f * u, anchoR * 0.32f) * f
            drawOval(Color.Black.copy(alpha = 0.3f), Offset(c.x - r + 0.3f * u, c.y - r * 0.8f + 0.5f * u), Size(r * 2, r * 1.6f))
            drawOval(t(Color(0xFFE8DCC4), oscuro), Offset(c.x - r, c.y - r * 0.8f), Size(r * 2, r * 1.6f))
            drawOval(t(pig, oscuro, 0.25f), Offset(c.x - r * 0.7f, c.y - r * 0.5f), Size(r * 1.4f, r * 1.0f))
            drawOval(Color.White.copy(alpha = 0.25f), Offset(c.x - r * 0.5f, c.y - r * 0.4f), Size(r * 0.5f, r * 0.3f))
        }
    }

    /** Una pluma de ganso tendida de [a] (el cañón) a [b] (la punta de las barbas). */
    private fun DrawScope.pluma(a: Offset, b: Offset, oscuro: Boolean, u: Float) {
        val d = b - a; val largo = d.getDistance(); val dir = d / largo; val nor = Offset(-dir.y, dir.x)
        val ini = a + d * 0.3f
        val p = Path().apply {
            moveTo(ini.x, ini.y)
            val c1 = a + d * 0.55f + nor * (largo * 0.16f)
            quadraticTo(c1.x, c1.y, b.x, b.y)
            val c3 = a + d * 0.6f - nor * (largo * 0.1f)
            quadraticTo(c3.x, c3.y, ini.x, ini.y)
            close()
        }
        translate(0.4f * u, 0.6f * u) { drawPath(p, Color.Black.copy(alpha = 0.3f)) }
        drawPath(p, t(Color(0xFFF4EEE0), oscuro, 0.35f))
        for (k in 1..6) {
            val q = a + d * (0.32f + 0.1f * k)
            drawLine(t(PERG_OSC, oscuro, 0.3f), q, q + nor * (largo * 0.07f) + dir * (largo * 0.05f), 0.15f * u)
        }
        drawLine(t(Color(0xFFCBBF9F), oscuro, 0.3f), a, b, 0.35f * u, StrokeCap.Round)
        drawLine(t(TINTA, oscuro, 0.1f), a, a + d * 0.06f, 0.4f * u, StrokeCap.Round)
    }

    // ---------------------------------------------------------------- Otras vidas

    override fun DrawScope.muebleOtrasVidas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val n = caja(area(MuebleVida.OTRAS_VIDAS))
        // El piso de baldosas bajo el armario.
        val piso = h * 0.93f
        damero(Rect(0f, piso, w, h), (h - piso) / 2f, oscuro)
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(0f, piso), Size(w, 1.4f * u))
        // El cuerpo de roble, de tablas verticales.
        val cuerpo = Rect(w * 0.012f, h * 0.14f, w * 0.988f, piso)
        drawRect(t(ROBLE, oscuro), cuerpo.topLeft, cuerpo.size)
        vetas(cuerpo, t(ROBLE_OSC, oscuro).copy(alpha = 0.4f), 3, 1.6f * u, vertical = true, grosor = 0.4f * u)
        // Los montantes con sus fajas de hierro remachadas.
        listOf(cuerpo.left + (n.left - cuerpo.left) * 0.18f, n.right + (cuerpo.right - n.right) * 0.18f).forEach { x ->
            val bw = (n.left - cuerpo.left) * 0.64f
            drawRect(t(HIERRO, oscuro, 0.3f), Offset(x, cuerpo.top + 1f * u), Size(bw, cuerpo.height - 2f * u))
            drawRect(t(HIERRO_LUZ, oscuro, 0.3f).copy(alpha = 0.5f), Offset(x, cuerpo.top + 1f * u), Size(bw * 0.25f, cuerpo.height - 2f * u))
            var y = cuerpo.top + 4f * u
            while (y < cuerpo.bottom - 2f * u) { clavo(Offset(x + bw / 2, y), bw * 0.2f, oscuro); y += 7f * u }
        }
        // Las cinco hornacinas de medio punto, forradas: el nicho; con su arquivolta, sus columnillas y un cuadrifolio en cada enjuta.
        val col = n.width / 5f
        val ph = 1.1f * u
        val forro = nicho(e, oscuro, PERGAMINO)
        drawRect(t(ROBLE_OSC, oscuro), n.topLeft, n.size)
        for (i in 0 until 5) {
            val r = Rect(n.left + col * i + ph, n.top + 1.4f * u, n.left + col * (i + 1) - ph, n.bottom)
            val arco = medioPunto(r)
            drawPath(medioPunto(r.crece(0.9f * u)), t(ROBLE_LUZ, oscuro))
            drawPath(medioPunto(r.crece(0.9f * u)), t(ROBLE_OSC, oscuro), style = Stroke(0.35f * u))
            drawPath(arco, forro)
            clipPath(arco) {
                drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.12f), Color.Transparent), r.top, r.top + r.width * 0.7f), r.topLeft, r.size)
                drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.2f else 0.08f), Color.Transparent), r.left, r.left + 2.5f * u), r.topLeft, r.size)
                halo(Offset(r.center.x, r.top + r.width * 0.45f), r.width * 0.7f, VELA.copy(alpha = if (oscuro) 0.08f else 0.1f))
            }
        }
        for (i in 0..5) {
            val x = n.left + col * i
            val arranque = n.top + 1.4f * u + col / 2 - ph
            if (i in 1..4) cuadrifolio(Offset(x, n.top + 1.4f * u), 1.5f * u, t(ORO, oscuro, 0.3f), t(LAPIS, oscuro, 0.3f))
            if (i == 0 || i == 5) continue
            val cw = 1.9f * u
            drawRect(t(ROBLE_LUZ, oscuro), Offset(x - cw / 2, arranque), Size(cw, n.bottom - arranque))
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(x + cw * 0.15f, arranque), Size(cw * 0.35f, n.bottom - arranque))
            drawRect(Color.White.copy(alpha = if (oscuro) 0.05f else 0.18f), Offset(x - cw * 0.4f, arranque), Size(cw * 0.25f, n.bottom - arranque))
            drawPath(Path().apply { moveTo(x - cw * 1.1f, arranque - 1.4f * u); lineTo(x + cw * 1.1f, arranque - 1.4f * u); lineTo(x + cw * 0.5f, arranque + 0.4f * u); lineTo(x - cw * 0.5f, arranque + 0.4f * u); close() }, t(ROBLE_LUZ, oscuro))
            drawRect(t(ORO, oscuro, 0.3f), Offset(x - cw * 1.2f, arranque - 1.9f * u), Size(cw * 2.4f, 0.6f * u))
            drawRect(t(ROBLE_LUZ, oscuro), Offset(x - cw * 0.9f, n.bottom - 1.2f * u), Size(cw * 1.8f, 1.2f * u))
        }
        // La repisa bajo las hornacinas y el zócalo con un tirador de hierro por compartimento.
        val repisa = Rect(cuerpo.left - 0.6f * u, n.bottom, cuerpo.right + 0.6f * u, n.bottom + 2.2f * u)
        tabla(repisa, ROBLE_LUZ, oscuro, 9, u)
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(repisa.left, repisa.bottom), Size(repisa.width, 0.8f * u))
        val zoc = Rect(n.left, repisa.bottom + 1.5f * u, n.right, piso - 1.2f * u)
        for (i in 0 until 5) {
            val r = Rect(zoc.left + col * i + 0.8f * u, zoc.top, zoc.left + col * (i + 1) - 0.8f * u, zoc.bottom)
            drawRect(t(ROBLE_OSC, oscuro), r.topLeft, r.size)
            drawRect(t(ROBLE, oscuro), r.crece(-0.5f * u).topLeft, r.crece(-0.5f * u).size)
            vetas(r.crece(-0.5f * u), t(ROBLE_OSC, oscuro).copy(alpha = 0.4f), 70 + i, 1.2f * u, grosor = 0.3f * u)
            drawRect(Color.White.copy(alpha = if (oscuro) 0.04f else 0.12f), Offset(r.left + 0.5f * u, r.top + 0.5f * u), Size(r.width - 1f * u, 0.3f * u))
            val c = r.center
            drawRect(t(HIERRO, oscuro, 0.3f), Offset(c.x - 1.4f * u, c.y - 0.9f * u), Size(2.8f * u, 1.8f * u))
            drawCircle(t(HIERRO_LUZ, oscuro, 0.3f), 1.1f * u, Offset(c.x, c.y + 0.6f * u), style = Stroke(0.35f * u))
        }
        // La cornisa: molduras oscuras, la arquería ciega pintada de bermellón y lapislázuli, y los dentículos.
        val cornisa = Rect(0f, h * 0.075f, w, n.top - 1.2f * u)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(cornisa.left, cornisa.bottom), Size(cornisa.width, 1.2f * u))
        tabla(cornisa, ROBLE_LUZ, oscuro, 12, u)
        drawRect(t(ROBLE_OSC, oscuro), Offset(0f, cornisa.top), Size(w, 1.1f * u))
        drawRect(t(ROBLE_OSC, oscuro), Offset(0f, cornisa.bottom - 1.3f * u), Size(w, 1.3f * u))
        val na = (w / (11f * u)).toInt().coerceAtLeast(9) or 1
        val aw = w * 0.92f / na
        for (j in 0 until na) {
            val r = Rect(w * 0.04f + aw * j + aw * 0.16f, cornisa.top + 1.8f * u, w * 0.04f + aw * (j + 1) - aw * 0.16f, cornisa.bottom - 2f * u)
            drawPath(medioPunto(r), t(if (j % 2 == 0) BERMELLON else LAPIS, oscuro, 0.3f))
            drawPath(medioPunto(r), t(ROBLE_OSC, oscuro), style = Stroke(0.4f * u))
            drawCircle(t(ORO_LUZ, oscuro, 0.3f), 0.5f * u, Offset(r.center.x, r.top + r.height * 0.62f))
        }
        var x = 0.5f * u
        while (x < w) { drawRect(t(ROBLE_OSC, oscuro), Offset(x, cornisa.bottom), Size(1.2f * u, 1.2f * u)); x += 2.4f * u }
        // El frontón bajo, con crochets en las vertientes, el remate dorado y el medallón de lapislázuli con su estrella.
        val izq = Offset(w * 0.2f, cornisa.top); val der = Offset(w * 0.8f, cornisa.top); val cima = Offset(w / 2, 1.2f * u)
        val fronton = Path().apply { moveTo(izq.x, izq.y); lineTo(cima.x, cima.y); lineTo(der.x, der.y); close() }
        drawPath(fronton, t(ROBLE, oscuro))
        clipPath(fronton) { vetas(Rect(izq.x, cima.y, der.x, izq.y), t(ROBLE_OSC, oscuro).copy(alpha = 0.4f), 7, 1.2f * u, grosor = 0.3f * u) }
        drawPath(fronton, t(ROBLE_OSC, oscuro), style = Stroke(0.8f * u))
        for (j in 1..4) {
            val f = j / 5f
            listOf(izq, der).forEach { a ->
                val p = a + (cima - a) * f
                drawCircle(t(ROBLE_LUZ, oscuro), 1.1f * u, Offset(p.x, p.y - 1f * u))
                drawCircle(t(ROBLE_OSC, oscuro), 1.1f * u, Offset(p.x, p.y - 1f * u), style = Stroke(0.3f * u))
            }
        }
        val rm = (cornisa.top - cima.y) * 0.32f
        val med = Offset(w / 2, cornisa.top - rm - 0.6f * u)
        drawCircle(t(LAPIS, oscuro, 0.3f), rm, med)
        drawPath(estrella(med, rm * 0.72f, 8, 0.45f), t(ORO_LUZ, oscuro, 0.3f))
        drawCircle(t(ORO, oscuro, 0.3f), rm, med, style = Stroke(0.5f * u))
        drawCircle(t(ORO, oscuro, 0.3f), 1.2f * u, cima)
        listOf(1.4f * u, w - 1.4f * u).forEach { xx -> drawCircle(t(ORO, oscuro, 0.3f), 1.3f * u, Offset(xx, cornisa.top - 0.8f * u)) }
    }

    // ---------------------------------------------------------------- Íconos de mapa

    override fun DrawScope.iconoMapa(id: String, e: EstadoIcono, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 108f
        val c = Offset(w / 2, h / 2)
        when (id) {
            "unesco" -> {
                // La fachada de una catedral: dos torres con su aguja de pizarra, el hastial, el rosetón y la portada.
                val piedra = t(Color(0xFFD9CCAE), oscuro, 0.3f); val sombra = t(Color(0xFFB4A584), oscuro, 0.3f); val linea = t(Color(0xFF5E513D), oscuro, 0.3f)
                val pizarra = t(mezcla(e.c2, Color(0xFF3C4250), 0.5f), oscuro, 0.2f)
                val suelo = c.y + 40 * u
                drawOval(Color.Black.copy(alpha = 0.18f), Offset(c.x - 44 * u, suelo - 3 * u), Size(88 * u, 6 * u))
                listOf(-1f, 1f).forEach { s ->
                    val tx = c.x + s * 26 * u
                    val torre = Rect(tx - 10 * u, c.y - 18 * u, tx + 10 * u, suelo)
                    drawRect(piedra, torre.topLeft, torre.size)
                    drawRect(sombra, Offset(if (s > 0) torre.right - 5 * u else torre.left, torre.top), Size(5 * u, torre.height))
                    drawRect(linea, torre.topLeft, torre.size, style = Stroke(1.2f * u))
                    drawPath(Path().apply { moveTo(torre.left - 1.5f * u, torre.top); lineTo(tx, c.y - 46 * u); lineTo(torre.right + 1.5f * u, torre.top); close() }, pizarra)
                    drawPath(Path().apply { moveTo(tx, c.y - 46 * u); lineTo(torre.right + 1.5f * u, torre.top); lineTo(tx + s * 3f * u, torre.top); close() }, Color.Black.copy(alpha = 0.18f))
                    drawCircle(t(ORO, oscuro, 0.2f), 1.8f * u, Offset(tx, c.y - 47 * u))
                    listOf(c.y - 10 * u, c.y + 8 * u).forEach { y ->
                        val v = Rect(tx - 3 * u, y, tx + 3 * u, y + 11 * u)
                        drawPath(medioPunto(v), t(Color(0xFF2B231B), oscuro, 0.1f))
                    }
                }
                val nave = Rect(c.x - 16 * u, c.y - 8 * u, c.x + 16 * u, suelo)
                drawRect(piedra, nave.topLeft, nave.size)
                drawPath(Path().apply { moveTo(nave.left, nave.top); lineTo(c.x, c.y - 26 * u); lineTo(nave.right, nave.top); close() }, piedra)
                drawPath(Path().apply { moveTo(nave.left, nave.top); lineTo(c.x, c.y - 26 * u); lineTo(nave.right, nave.top); close() }, linea, style = Stroke(1.2f * u))
                drawRect(linea, nave.topLeft, nave.size, style = Stroke(1.2f * u))
                // El rosetón: vidrio del acento y del secundario con su tracería.
                val ro = Offset(c.x, c.y + 2 * u); val rr = 9 * u
                drawCircle(t(e.c2, oscuro, 0.1f), rr, ro)
                for (k in 0 until 8) {
                    val a = k * Math.PI / 4
                    drawCircle(t(e.c1, oscuro, 0.1f), rr * 0.3f, ro + Offset(cos(a).toFloat(), sin(a).toFloat()) * (rr * 0.6f))
                    drawLine(linea, ro, ro + Offset(cos(a).toFloat(), sin(a).toFloat()) * rr, 0.6f * u)
                }
                drawCircle(t(ORO, oscuro, 0.2f), rr * 0.25f, ro)
                drawCircle(linea, rr, ro, style = Stroke(1.6f * u))
                // La portada abocinada, con su tímpano.
                val pt = Rect(c.x - 7 * u, c.y + 16 * u, c.x + 7 * u, suelo)
                drawPath(medioPunto(pt.crece(2.4f * u)), sombra)
                drawPath(medioPunto(pt), t(Color(0xFF2B231B), oscuro, 0.1f))
                drawPath(Path().apply { arcTo(Rect(pt.left, pt.top, pt.right, pt.top + pt.width), 180f, 180f, true); close() }, t(e.c1, oscuro, 0.15f))
                drawLine(t(ORO, oscuro, 0.2f), Offset(c.x, c.y - 26 * u), Offset(c.x, c.y - 33 * u), 1.2f * u)
                drawLine(t(ORO, oscuro, 0.2f), Offset(c.x - 2.5f * u, c.y - 30.5f * u), Offset(c.x + 2.5f * u, c.y - 30.5f * u), 1.2f * u)
            }
            "rascar" -> {
                // El pergamino del mapa, con la esquina de arriba raspada (asoman los colores) y el lunellum que la raspa.
                val r = Rect(c.x - 50 * u, c.y - 34 * u, c.x + 34 * u, c.y + 36 * u)
                val forma = barbado(r, 7, 28, 0.02f)
                translate(1.5f * u, 2f * u) { drawPath(forma, Color.Black.copy(alpha = 0.25f)) }
                drawPath(forma, t(PERGAMINO, oscuro, 0.3f))
                drawPath(forma, t(PERG_OSC, oscuro, 0.3f), style = Stroke(1.4f * u))
                drawRect(t(BERMELLON, oscuro, 0.2f), r.crece(-5 * u).topLeft, r.crece(-5 * u).size, style = Stroke(0.9f * u))
                // Las costas, a pluma.
                val costa = Path().apply { moveTo(r.left + 9 * u, r.top + 40 * u); cubicTo(r.left + 22 * u, r.top + 30 * u, r.left + 30 * u, r.top + 52 * u, r.left + 46 * u, r.top + 46 * u); cubicTo(r.left + 58 * u, r.top + 42 * u, r.left + 62 * u, r.top + 58 * u, r.left + 76 * u, r.top + 56 * u) }
                drawPath(costa, t(TINTA, oscuro, 0.1f).copy(alpha = 0.6f), style = Stroke(1f * u))
                // La parte raspada: el acento y el secundario bajo la capa, con los surcos del raspado.
                val raspa = Path().apply { moveTo(r.right - 40 * u, r.top + 6 * u); lineTo(r.right - 6 * u, r.top + 6 * u); lineTo(r.right - 6 * u, r.top + 34 * u); quadraticTo(r.right - 24 * u, r.top + 30 * u, r.right - 40 * u, r.top + 6 * u); close() }
                clipPath(raspa) {
                    drawRect(t(e.c1, oscuro, 0.15f), Offset(r.right - 42 * u, r.top), Size(42 * u, 40 * u))
                    drawCircle(t(e.c2, oscuro, 0.15f), 10 * u, Offset(r.right - 14 * u, r.top + 24 * u))
                    for (k in 0 until 7) drawLine(Color.White.copy(alpha = 0.35f), Offset(r.right - 42 * u + k * 6 * u, r.top + 4 * u), Offset(r.right - 30 * u + k * 6 * u, r.top + 38 * u), 0.8f * u)
                }
                // El lunellum: la cuchilla de media luna con su mango de madera.
                val lc = Offset(r.right + 6 * u, r.top + 30 * u)
                rotate(-30f, lc) {
                    drawRect(Color.Black.copy(alpha = 0.25f), Offset(lc.x - 2.5f * u + 1.5f * u, lc.y - 2 * u), Size(5 * u, 22 * u))
                    drawRoundRect(t(ROBLE, oscuro), Offset(lc.x - 2.5f * u, lc.y - 2 * u), Size(5 * u, 22 * u), CornerRadius(2 * u))
                    drawRect(t(ROBLE_LUZ, oscuro), Offset(lc.x - 2.5f * u, lc.y - 2 * u), Size(1.5f * u, 22 * u))
                    drawRect(t(LATON, oscuro, 0.2f), Offset(lc.x - 3 * u, lc.y - 3.5f * u), Size(6 * u, 2.5f * u))
                    drawArc(t(ACERO, oscuro, 0.2f), 180f, 180f, true, Offset(lc.x - 13 * u, lc.y - 13 * u), Size(26 * u, 20 * u))
                    drawArc(Color.White.copy(alpha = 0.6f), 190f, 160f, false, Offset(lc.x - 12 * u, lc.y - 12 * u), Size(24 * u, 18 * u), style = Stroke(1f * u))
                    drawArc(t(Color(0xFF6E7277), oscuro, 0.2f), 180f, 180f, true, Offset(lc.x - 13 * u, lc.y - 13 * u), Size(26 * u, 20 * u), style = Stroke(0.8f * u))
                }
            }
            else -> {
                // Tus lugares: tres sellos de cera sobre una tira de pergamino, unidos por un camino de tinta roja.
                val r = Rect(c.x - 62 * u, c.y - 22 * u, c.x + 62 * u, c.y + 26 * u)
                val forma = barbado(r, 11, 30, 0.03f)
                translate(1.5f * u, 2f * u) { drawPath(forma, Color.Black.copy(alpha = 0.22f)) }
                drawPath(forma, t(PERGAMINO, oscuro, 0.3f))
                drawPath(forma, t(PERG_OSC, oscuro, 0.3f), style = Stroke(1.2f * u))
                val ps = listOf(Offset(c.x - 42 * u, c.y + 6 * u), Offset(c.x, c.y - 4 * u), Offset(c.x + 42 * u, c.y + 8 * u))
                val camino = Path().apply { moveTo(ps[0].x, ps[0].y); quadraticTo(c.x - 22 * u, c.y - 18 * u, ps[1].x, ps[1].y); quadraticTo(c.x + 22 * u, c.y + 22 * u, ps[2].x, ps[2].y) }
                drawPath(camino, t(BERMELLON, oscuro, 0.2f), style = Stroke(1.2f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(3 * u, 3 * u))))
                listOf(e.c1, e.c2, ORO).forEachIndexed { i, cera ->
                    val p = ps[i]; val rs = 12 * u
                    // Las cintas que cuelgan del sello.
                    drawPath(Path().apply { moveTo(p.x - 3 * u, p.y + 6 * u); lineTo(p.x - 7 * u, p.y + 18 * u); lineTo(p.x - 3 * u, p.y + 15 * u); lineTo(p.x, p.y + 19 * u); close() }, t(if (i == 1) BERMELLON else LAPIS, oscuro, 0.2f))
                    drawPath(Path().apply { moveTo(p.x + 3 * u, p.y + 6 * u); lineTo(p.x + 7 * u, p.y + 18 * u); lineTo(p.x + 3 * u, p.y + 15 * u); lineTo(p.x, p.y + 19 * u); close() }, t(if (i == 1) BERMELLON else LAPIS, oscuro, 0.2f))
                    drawCircle(Color.Black.copy(alpha = 0.3f), rs, p + Offset(1.2f * u, 1.6f * u))
                    val ondas = Path().apply {
                        for (k in 0 until 24) {
                            val a = k * Math.PI * 2 / 24; val rr = if (k % 2 == 0) rs else rs * 0.9f
                            val q = p + Offset(cos(a).toFloat(), sin(a).toFloat()) * rr
                            if (k == 0) moveTo(q.x, q.y) else lineTo(q.x, q.y)
                        }
                        close()
                    }
                    drawPath(ondas, t(cera, oscuro, 0.15f))
                    drawCircle(mezcla(t(cera, oscuro, 0.15f), Color.Black, 0.3f), rs * 0.66f, p, style = Stroke(1.2f * u))
                    cuadrifolio(p, rs * 0.85f, mezcla(t(cera, oscuro, 0.15f), Color.Black, 0.3f), mezcla(t(cera, oscuro, 0.15f), Color.White, 0.25f))
                    drawCircle(Color.White.copy(alpha = 0.3f), rs * 0.22f, p - Offset(rs * 0.45f, rs * 0.45f))
                }
            }
        }
    }

    // ---------------------------------------------------------------- Notas

    override fun DrawScope.tableroNotas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val n = caja(area(MuebleVida.NOTAS))
        // El cuerpo del tablón, de roble.
        val cuerpo = Rect(w * 0.01f, h * 0.09f, w * 0.99f, h * 0.975f)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(cuerpo.left + 0.8f * u, cuerpo.top + 1f * u), cuerpo.size)
        drawRect(t(ROBLE, oscuro), cuerpo.topLeft, cuerpo.size)
        vetas(cuerpo, t(ROBLE_OSC, oscuro).copy(alpha = 0.4f), 5, 1.4f * u, vertical = true, grosor = 0.35f * u)
        // El fondo de tablas de tilo, hundido: el nicho.
        val fondo = nicho(e, oscuro, TILO)
        drawRect(t(ROBLE_OSC, oscuro), n.crece(0.9f * u).topLeft, n.crece(0.9f * u).size)
        drawRect(fondo, n.topLeft, n.size)
        val tablas = 7
        for (k in 1 until tablas) {
            val x = n.left + n.width * k / tablas
            drawLine(Color.Black.copy(alpha = if (oscuro) 0.25f else 0.08f), Offset(x, n.top), Offset(x, n.bottom), 0.3f * u)
            drawLine(Color.White.copy(alpha = if (oscuro) 0.03f else 0.35f), Offset(x + 0.3f * u, n.top), Offset(x + 0.3f * u, n.bottom), 0.25f * u)
        }
        vetas(n, (if (oscuro) Color.Black else ROBLE_LUZ).copy(alpha = if (oscuro) 0.12f else 0.08f), 17, 2.2f * u, vertical = true, grosor = 0.25f * u)
        clipRect(n.left, n.top, n.right, n.bottom) {
            drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.35f else 0.14f), Color.Transparent), n.top, n.top + 4f * u), n.topLeft, Size(n.width, 4f * u))
            drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.25f else 0.1f), Color.Transparent), n.left, n.left + 3f * u), n.topLeft, Size(3f * u, n.height))
        }
        // Las escuadras de hierro alrededor del fondo.
        val ee = 7f * u; val eg = 1.3f * u
        listOf(n.topLeft to Offset(1f, 1f), Offset(n.right, n.top) to Offset(-1f, 1f), Offset(n.left, n.bottom) to Offset(1f, -1f), n.bottomRight to Offset(-1f, -1f)).forEach { (c, d) ->
            val o = c - Offset(d.x * 0.9f * u, d.y * 0.9f * u)
            drawRect(t(HIERRO, oscuro, 0.3f), Offset(if (d.x > 0) o.x - eg else o.x - ee + eg, if (d.y > 0) o.y - eg else o.y), Size(ee, eg))
            drawRect(t(HIERRO, oscuro, 0.3f), Offset(if (d.x > 0) o.x - eg else o.x, if (d.y > 0) o.y - eg else o.y - ee + eg), Size(eg, ee))
        }
        // Los montantes: fajas de hierro remachadas y un aplique con su vela encendida a cada lado.
        listOf(cuerpo.left to n.left, n.right to cuerpo.right).forEach { (a, z) ->
            val bw = (z - a) * 0.36f; val x = (a + z) / 2 - bw / 2
            drawRect(t(HIERRO, oscuro, 0.3f), Offset(x, cuerpo.top + 1f * u), Size(bw, cuerpo.height - 2f * u))
            drawRect(t(HIERRO_LUZ, oscuro, 0.3f).copy(alpha = 0.45f), Offset(x, cuerpo.top + 1f * u), Size(bw * 0.25f, cuerpo.height - 2f * u))
            var y = cuerpo.top + 3f * u
            while (y < cuerpo.bottom - 2f * u) { clavo(Offset(x + bw / 2, y), bw * 0.22f, oscuro); y += 6f * u }
            val cx = (a + z) / 2; val cy = h * 0.6f
            drawPath(Path().apply { moveTo(cx - 2f * u, cy + 3.5f * u); lineTo(cx + 2f * u, cy + 3.5f * u); lineTo(cx + 1.2f * u, cy + 1f * u); lineTo(cx - 1.2f * u, cy + 1f * u); close() }, t(HIERRO, oscuro, 0.2f))
            drawRect(t(HIERRO, oscuro, 0.2f), Offset(cx - 0.4f * u, cy + 3.5f * u), Size(0.8f * u, 4f * u))
            drawRect(CERA, Offset(cx - 0.9f * u, cy - 4f * u), Size(1.8f * u, 5f * u))
            drawRect(Color.Black.copy(alpha = 0.12f), Offset(cx + 0.2f * u, cy - 4f * u), Size(0.7f * u, 5f * u))
            llama(Offset(cx, cy - 4f * u), 2.6f * u, oscuro)
        }
        // La cornisa: molduras, el friso de medallones con cuadrifolios rojos y azules, y los dentículos.
        val cornisa = Rect(0f, h * 0.035f, w, n.top - 2.2f * u)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(0f, cornisa.bottom), Size(w, 1.2f * u))
        tabla(cornisa, ROBLE_LUZ, oscuro, 21, u)
        drawRect(t(ROBLE_OSC, oscuro), Offset(0f, cornisa.top), Size(w, 0.9f * u))
        drawRect(t(ROBLE_OSC, oscuro), Offset(0f, cornisa.bottom - 1f * u), Size(w, 1f * u))
        val friso = Rect(w * 0.04f, cornisa.top + 1.6f * u, w * 0.96f, cornisa.bottom - 1.8f * u)
        drawRect(t(ROBLE_OSC, oscuro), friso.topLeft, friso.size)
        val nm = (friso.width / (5.5f * u)).toInt() or 1
        val pm = friso.width / nm
        for (j in 0 until nm) {
            val c = Offset(friso.left + pm * (j + 0.5f), friso.center.y)
            val r = friso.height * 0.42f
            drawCircle(t(ORO, oscuro, 0.3f), r, c)
            cuadrifolio(c, r * 0.82f, t(if (j % 2 == 0) BERMELLON else LAPIS, oscuro, 0.3f), t(ORO_LUZ, oscuro, 0.3f))
        }
        var x = 0.4f * u
        while (x < w) { drawRect(t(ROBLE_OSC, oscuro), Offset(x, cornisa.bottom), Size(1.1f * u, 1.1f * u)); x += 2.2f * u }
        // La crestería de tréboles sobre la cornisa.
        val nt = (w / (4.4f * u)).toInt()
        val pt = w / nt
        for (j in 0 until nt) {
            val c = Offset(pt * (j + 0.5f), cornisa.top - 0.4f * u)
            listOf(Offset(-0.8f, 0f), Offset(0.8f, 0f), Offset(0f, -0.9f)).forEach { d -> drawCircle(t(ROBLE_LUZ, oscuro), 0.75f * u, c + d * u) }
            drawCircle(t(ROBLE_OSC, oscuro), 0.3f * u, c + Offset(0f, -0.3f * u))
        }
        // La repisa de abajo, que sobresale, con su sombra.
        val repisa = Rect(0f, n.bottom + 1.8f * u, w, n.bottom + 4.2f * u)
        tabla(repisa, ROBLE_LUZ, oscuro, 23, u)
        drawRect(Color.Black.copy(alpha = 0.4f), Offset(0f, repisa.bottom), Size(w, 0.9f * u))
        listOf(w * 0.08f, w * 0.92f).forEach { cx ->
            drawPath(Path().apply { moveTo(cx - 2f * u, repisa.bottom); lineTo(cx + 2f * u, repisa.bottom); lineTo(cx + 0.8f * u, cuerpo.bottom); lineTo(cx - 0.8f * u, cuerpo.bottom); close() }, t(ROBLE_OSC, oscuro))
        }
    }

    override fun DrawScope.notaTablero(e: EstadoNota, oscuro: Boolean) {
        val r = e.rect; val w = r.width; val h = r.height; val s = minOf(w, h) / 100f
        // La hoja de pergamino, de borde barbado, con su sombra en las tablas.
        val hoja = Rect(r.left + w * 0.02f, r.top + h * 0.02f, r.right - w * 0.02f, r.bottom - h * 0.02f)
        val forma = barbado(hoja, 31 + e.indice * 7, 26, 0.014f)
        translate(1.2f * s, 1.8f * s) { drawPath(forma, Color.Black.copy(alpha = if (oscuro) 0.5f else 0.28f)) }
        val papel = if (oscuro) mezcla(PERGAMINO, NOCHE, 0.38f) else PERGAMINO
        drawPath(forma, papel)
        clipPath(forma) {
            // El lado del pelo, más amarillo hacia los bordes.
            val b = 6f * s
            listOf(Rect(hoja.left, hoja.top, hoja.right, hoja.top + b), Rect(hoja.left, hoja.bottom - b, hoja.right, hoja.bottom)).forEach { q -> drawRect(PERG_OSC.copy(alpha = 0.3f), q.topLeft, q.size) }
            listOf(Rect(hoja.left, hoja.top, hoja.left + b, hoja.bottom), Rect(hoja.right - b, hoja.top, hoja.right, hoja.bottom)).forEach { q -> drawRect(PERG_OSC.copy(alpha = 0.22f), q.topLeft, q.size) }
        }
        drawPath(forma, mezcla(PERG_OSC, Color.Black, if (oscuro) 0.45f else 0.15f), style = Stroke(0.8f * s))
        // El pautado: la doble raya roja del margen y los picados.
        drawLine(BERMELLON.copy(alpha = 0.45f), Offset(r.left + w * 0.075f, r.top + h * 0.1f), Offset(r.left + w * 0.075f, r.bottom - h * 0.08f), 0.6f * s)
        drawLine(BERMELLON.copy(alpha = 0.3f), Offset(r.left + w * 0.095f, r.top + h * 0.1f), Offset(r.left + w * 0.095f, r.bottom - h * 0.08f), 0.4f * s)
        var y = r.top + h * 0.18f
        while (y < r.bottom - h * 0.1f) { drawCircle(TINTA.copy(alpha = 0.3f), 0.55f * s, Offset(r.right - w * 0.06f, y)); y += h * 0.08f }
        // El detalle del tipo, en la esquina de arriba a la derecha.
        val esq = Offset(r.right - w * 0.13f, r.top + h * 0.085f)
        val ka = if (oscuro) 0.2f else 0f
        when (e.tipo) {
            "idea" -> {
                // Un punto de oro iluminado, con su aro bermellón.
                drawCircle(mezcla(BERMELLON, NOCHE, ka), 4.2f * s, esq)
                drawCircle(mezcla(ORO_LUZ, NOCHE, ka), 3.2f * s, esq)
                drawPath(estrella(esq, 2.6f * s, 6, 0.45f), mezcla(ORO, NOCHE, ka))
                drawCircle(Color.White.copy(alpha = 0.5f), 0.8f * s, esq - Offset(1.2f * s, 1.2f * s))
            }
            "pensamiento" -> {
                // El trébol azul.
                for (k in 0 until 3) {
                    val a = Math.toRadians(-90.0 + k * 120.0)
                    drawCircle(mezcla(LAPIS, NOCHE, ka), 2.1f * s, esq + Offset(cos(a).toFloat(), sin(a).toFloat()) * (2.1f * s))
                }
                drawLine(mezcla(LAPIS, NOCHE, ka), esq, esq + Offset(0f, 5.5f * s), 0.9f * s, StrokeCap.Round)
            }
            "proposito" -> {
                // El calderón rojo de los rubricadores.
                drawCircle(mezcla(BERMELLON, NOCHE, ka), 2.4f * s, Offset(esq.x - 1.6f * s, esq.y - 1f * s))
                drawLine(mezcla(BERMELLON, NOCHE, ka), Offset(esq.x + 0.4f * s, esq.y - 3.4f * s), Offset(esq.x + 0.4f * s, esq.y + 4.4f * s), 1f * s)
                drawLine(mezcla(BERMELLON, NOCHE, ka), Offset(esq.x + 2.6f * s, esq.y - 3.4f * s), Offset(esq.x + 2.6f * s, esq.y + 4.4f * s), 1f * s)
                drawLine(mezcla(BERMELLON, NOCHE, ka), Offset(esq.x - 1.6f * s, esq.y - 3.4f * s), Offset(esq.x + 3.2f * s, esq.y - 3.4f * s), 1f * s)
            }
            else -> {
                // La cinta de registro roja que cuelga por el margen y la esquina de abajo doblada.
                val cx = r.right - w * 0.05f
                drawRect(Color.Black.copy(alpha = 0.25f), Offset(cx - 1.4f * s + 0.6f * s, r.top), Size(2.8f * s, h * 0.4f))
                drawRect(mezcla(BERMELLON, NOCHE, ka), Offset(cx - 1.4f * s, r.top), Size(2.8f * s, h * 0.4f))
                drawPath(Path().apply { moveTo(cx - 1.4f * s, r.top + h * 0.4f); lineTo(cx, r.top + h * 0.37f); lineTo(cx + 1.4f * s, r.top + h * 0.4f); lineTo(cx + 1.4f * s, r.top + h * 0.36f); lineTo(cx - 1.4f * s, r.top + h * 0.36f); close() }, papel)
                val d = 9f * s
                val ex = hoja.right; val ey = hoja.bottom
                drawPath(Path().apply { moveTo(ex - d, ey); lineTo(ex, ey - d); lineTo(ex, ey); close() }, mezcla(TILO, NOCHE, if (oscuro) 0.85f else 0.1f))
                drawPath(Path().apply { moveTo(ex - d, ey); lineTo(ex, ey - d); lineTo(ex - d, ey - d); close() }, mezcla(PERG_OSC, NOCHE, if (oscuro) 0.35f else 0f))
                drawLine(Color.Black.copy(alpha = 0.25f), Offset(ex - d, ey), Offset(ex, ey - d), 0.5f * s)
            }
        }
        // El clavo de arriba: de hierro; si la nota está fijada, de cabeza dorada, con un sello de lacre pendiente abajo.
        val clavoC = Offset(r.center.x, r.top + h * 0.075f)
        if (e.fijada) {
            val rs = 5.2f * s
            val sc = Offset(r.left + w * 0.2f, r.bottom - h * 0.085f)
            listOf(-1f, 1f).forEach { k ->
                drawPath(Path().apply { moveTo(sc.x + k * 1.2f * s, sc.y); lineTo(sc.x + k * 4.5f * s, sc.y + rs * 1.4f); lineTo(sc.x + k * 2.2f * s, sc.y + rs * 1.2f); lineTo(sc.x + k * 0.2f * s, sc.y + rs * 1.5f); close() }, LAPIS)
            }
            drawCircle(Color.Black.copy(alpha = 0.3f), rs, sc + Offset(0.8f * s, 1f * s))
            drawCircle(mezcla(e.acento, BERMELLON, 0.6f), rs, sc)
            drawCircle(mezcla(BERMELLON, Color.Black, 0.35f), rs * 0.7f, sc, style = Stroke(0.8f * s))
            drawPath(estrella(sc, rs * 0.5f, 6, 0.5f), mezcla(BERMELLON, Color.Black, 0.35f))
            drawCircle(Color.White.copy(alpha = 0.35f), rs * 0.22f, sc - Offset(rs * 0.4f, rs * 0.4f))
            halo(clavoC, 6f * s, ORO_LUZ.copy(alpha = 0.5f))
            clavo(clavoC, 2.6f * s, oscuro = false, metal = ORO, luz = Color(0xFFFFF0B8))
        } else clavo(clavoC, 2.2f * s, oscuro = false)
    }

    /** La tinta ferrogálica de las notas: el pergamino es claro de día y, de noche, se ve a la luz de la vela. */
    override fun tintaNota(oscuro: Boolean): Color = TINTA
}
