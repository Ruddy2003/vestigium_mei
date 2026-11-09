package io.github.ruddy2003.vestigium.ui.vida

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
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
import java.util.Random
import kotlin.math.cos
import kotlin.math.sin

/**
 * Refugio: Vida bajo tierra, con los materiales de su armario de taquillas y de su mesa del supervisor: chapa pintada de verde
 * azulado con remaches, muro de bloques crema, tuberías de acero y de cobre con sus bridas, cinta de aviso amarilla y negra,
 * cromo, fórmica de los cincuenta, la puerta engranada de la bóveda y el átomo. De noche, la penumbra verde con las luces
 * encendidas: pilotos ámbar, el fósforo verde y la lámpara.
 * - Social: el comedor común visto desde arriba. El piso de losetas crema y verde azulado con las franjas de seguridad, la
 *   mesa de fórmica con su canto cromado acanalado (el grafo va en el tablero), los dos bancos de vinilo capitoneado y los
 *   taburetes rojos de cromo, y las coladeras del piso.
 * - Mapas: la mesa del supervisor en perspectiva, como la de su libreta. El muro de bloques con la franja amarilla, la lámpara
 *   colgante y el reloj; en la mesa de linóleo, el secante con su marco de cuero pespunteado (el mapa va encima), la radio de
 *   onda corta con su antena, el contador Geiger con su sonda, el termo y la taza humeante; el canto cromado con su tirador,
 *   las patas y la franja de aviso del piso.
 * - Otras vidas: las taquillas de los recuerdos. Las tuberías del techo con su válvula y su manómetro, la sirena, la corona con
 *   la placa amarilla del refugio y sus rejillas; cinco casilleros iguales de chapa perforada con su número; las bisagras y el
 *   candado de los costados, la repisa remachada y el zócalo.
 * - Íconos: la puerta engranada de la bóveda en su búnker, el mapa en la tablilla con la capa que se raspa con una chapa de
 *   botella, y el plano con marcadores de colores y la ruta.
 * - Notas: el tablón de avisos de chapa perforada con el sol atómico de la corona, los pilotos y la cinta de aviso de la
 *   repisa. Las notas son avisos con su franja de color según el tipo y su línea de corte; las fijadas llevan un imán de
 *   estrella y las demás un trozo de cinta.
 */
internal object VidaRefugio : DisenoVida {
    private val PINTURA = Color(0xFF3F6B70)
    private val PINTURA_LUZ = Color(0xFF5F8C90)
    private val PINTURA_OSC = Color(0xFF284A4E)
    private val MURO = Color(0xFFD9CFAF)
    private val CREMA = Color(0xFFF0E6C8)
    private val TUBO = Color(0xFF8A9294)
    private val TUBO_OSC = Color(0xFF4E5557)
    private val CROMO = Color(0xFFD5DADD)
    private val COBRE = Color(0xFFB0703F)
    private val AMARILLO = Color(0xFFE5B436)
    private val NARANJA = Color(0xFFD9572B)
    private val ROJO = Color(0xFFC8392B)
    private val LINOLEO = Color(0xFF5E7F72)
    private val PISO = Color(0xFF55574F)
    private val NEGRO = Color(0xFF141716)
    private val AMBAR = Color(0xFFFFB23F)
    private val FOSFORO = Color(0xFF7DFF8C)
    private val AVISO = Color(0xFFF6EEDA)
    private val AVISO_NOCHE = Color(0xFF1C2A20)
    private val NOCHE = Color(0xFF0A0F0F)

    private fun DrawScope.u() = size.minDimension / 100f

    private fun mezcla(a: Color, b: Color, f: Float): Color = lerp(a, b, f.coerceIn(0f, 1f))

    /** El color en oscuro: hundido en la penumbra del refugio, como en su armario de taquillas. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, NOCHE, 0.42f) else c

    /** El fondo del nicho: el material apenas mezclado con el fondo del esquema, para que se lea lo de encima. */
    private fun nicho(fondo: Color, material: Color, oscuro: Boolean): Color = mezcla(fondo, material, if (oscuro) 0.16f else 0.3f)

    private fun DrawScope.enLienzo(a: Rect) = Rect(size.width * a.left, size.height * a.top, size.width * a.right, size.height * a.bottom)

    override fun area(m: MuebleVida): Rect = when (m) {
        MuebleVida.SOCIAL -> Rect(0.17f, 0.25f, 0.83f, 0.77f)
        MuebleVida.MAPAS -> Rect(0.21f, 0.22f, 0.79f, 0.79f)
        MuebleVida.OTRAS_VIDAS -> Rect(0.06f, 0.31f, 0.94f, 0.86f)
        MuebleVida.NOTAS -> Rect(0.07f, 0.16f, 0.93f, 0.89f)
    }

    // ---------------------------------------------------------------- Ayudas de dibujo

    private fun DrawScope.sombra(r: Rect, alcance: Float, fuerza: Float, radio: Float = 0f) {
        for (k in 4 downTo 1) {
            val e = alcance * k / 4f
            drawRoundRect(Color.Black.copy(alpha = fuerza / 4f), Offset(r.left - e * 0.3f, r.top + e * 0.5f), Size(r.width + e * 0.6f, r.height + e * 0.3f), CornerRadius(radio + e))
        }
    }

    private fun DrawScope.bisel(r: Rect, luz: Color, sombra: Color, ancho: Float) {
        drawRect(luz, r.topLeft, Size(r.width, ancho))
        drawRect(luz.copy(alpha = luz.alpha * 0.7f), r.topLeft, Size(ancho, r.height))
        drawRect(sombra, Offset(r.left, r.bottom - ancho), Size(r.width, ancho))
        drawRect(sombra.copy(alpha = sombra.alpha * 0.8f), Offset(r.right - ancho, r.top), Size(ancho, r.height))
    }

    /** Una chapa pintada con su degradado de luz y su bisel. */
    private fun DrawScope.chapa(r: Rect, base: Color, oscuro: Boolean, u: Float) {
        drawRect(Brush.verticalGradient(listOf(mezcla(base, Color.White, 0.14f), base, mezcla(base, Color.Black, 0.2f)), r.top, r.bottom), r.topLeft, r.size)
        bisel(r, Color.White.copy(alpha = if (oscuro) 0.1f else 0.25f), Color.Black.copy(alpha = 0.35f), 0.5f * u)
    }

    private fun DrawScope.remache(c: Offset, r: Float, base: Color, oscuro: Boolean) {
        drawCircle(Color.Black.copy(alpha = 0.3f), r, Offset(c.x + r * 0.25f, c.y + r * 0.3f))
        drawCircle(mezcla(base, Color.White, 0.2f), r, c)
        drawCircle(Color.White.copy(alpha = if (oscuro) 0.15f else 0.45f), r * 0.4f, Offset(c.x - r * 0.3f, c.y - r * 0.3f))
    }

    /** Una luz encendida: el halo suave y el foco con su brillo. */
    private fun DrawScope.piloto(c: Offset, r: Float, color: Color, encendido: Boolean, oscuro: Boolean) {
        if (encendido) drawCircle(Brush.radialGradient(listOf(color.copy(alpha = if (oscuro) 0.6f else 0.35f), Color.Transparent), c, r * 4f), r * 4f, c)
        drawCircle(Color.Black.copy(alpha = 0.5f), r * 1.35f, c)
        drawCircle(if (encendido) color else t(mezcla(color, Color.Black, 0.6f), oscuro), r, c)
        drawCircle(Color.White.copy(alpha = if (encendido) 0.7f else 0.2f), r * 0.35f, Offset(c.x - r * 0.3f, c.y - r * 0.3f))
    }

    /** Una franja de aviso amarilla y negra en diagonal, dentro de [r]. */
    private fun DrawScope.franjaAviso(r: Rect, paso: Float, oscuro: Boolean) {
        drawRect(t(AMARILLO, oscuro), r.topLeft, r.size)
        if (paso < 1f) return
        clipRect(r.left, r.top, r.right, r.bottom) {
            var x = r.left - r.height; var k = 0
            while (x < r.right && k < 200) {
                val p = Path().apply { moveTo(x, r.bottom); lineTo(x + paso * 0.5f, r.bottom); lineTo(x + paso * 0.5f + r.height, r.top); lineTo(x + r.height, r.top); close() }
                drawPath(p, t(NEGRO, oscuro))
                x += paso; k++
            }
        }
    }

    /** Un engrane de dientes cuadrados alrededor de [c]. */
    private fun engrane(c: Offset, r: Float, dientes: Int): Path = Path().apply {
        val n = dientes * 4
        for (k in 0 until n) {
            val a = 2 * Math.PI * k / n
            val rr = if ((k / 2) % 2 == 0) r else r * 0.82f
            val p = Offset(c.x + rr * cos(a).toFloat(), c.y + rr * sin(a).toFloat())
            if (k == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
        }
        close()
    }

    private fun estrella(c: Offset, r: Float, puntas: Int = 5, hondo: Float = 0.45f): Path = Path().apply {
        for (k in 0 until puntas * 2) {
            val a = Math.toRadians(-90.0 + k * 180.0 / puntas)
            val rr = if (k % 2 == 0) r else r * hondo
            val p = Offset(c.x + rr * cos(a).toFloat(), c.y + rr * sin(a).toFloat())
            if (k == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
        }
        close()
    }

    /** El átomo: tres órbitas y el núcleo. */
    private fun DrawScope.atomo(c: Offset, r: Float, orbita: Color, nucleo: Color, grosor: Float) {
        listOf(0f, 60f, 120f).forEach { a -> rotate(a, c) { drawOval(orbita, Offset(c.x - r, c.y - r * 0.36f), Size(r * 2, r * 0.72f), style = Stroke(grosor)) } }
        drawCircle(nucleo, r * 0.2f, c)
    }

    /** Una carátula de manómetro: el aro, la cara, las marcas y la aguja. */
    private fun DrawScope.manometro(c: Offset, r: Float, oscuro: Boolean, valor: Float = 0.62f) {
        drawCircle(Color.Black.copy(alpha = 0.3f), r, Offset(c.x + r * 0.12f, c.y + r * 0.15f))
        drawCircle(t(TUBO_OSC, oscuro), r, c)
        drawCircle(t(CREMA, oscuro), r * 0.8f, c)
        for (k in 0..8) {
            val a = Math.toRadians(135.0 + k * 33.75)
            drawLine(t(NEGRO, oscuro), Offset(c.x + r * 0.62f * cos(a).toFloat(), c.y + r * 0.62f * sin(a).toFloat()), Offset(c.x + r * 0.76f * cos(a).toFloat(), c.y + r * 0.76f * sin(a).toFloat()), r * 0.07f)
        }
        drawArc(t(ROJO, oscuro), 0f, 45f, false, Offset(c.x - r * 0.68f, c.y - r * 0.68f), Size(r * 1.36f, r * 1.36f), style = Stroke(r * 0.12f))
        val a = Math.toRadians(135.0 + 270.0 * valor)
        drawLine(t(ROJO, oscuro), c, Offset(c.x + r * 0.62f * cos(a).toFloat(), c.y + r * 0.62f * sin(a).toFloat()), r * 0.1f, StrokeCap.Round)
        drawCircle(t(NEGRO, oscuro), r * 0.12f, c)
    }

    /** Una tubería horizontal con su volumen, sus bridas y las abrazaderas al techo. */
    private fun DrawScope.tubo(y: Float, grosor: Float, x0: Float, x1: Float, base: Color, bridas: List<Float>, oscuro: Boolean) {
        drawRect(Color.Black.copy(alpha = 0.18f), Offset(x0, y - grosor / 2 + grosor * 0.35f), Size(x1 - x0, grosor))
        drawRect(Brush.verticalGradient(listOf(mezcla(base, Color.White, 0.3f), base, mezcla(base, Color.Black, 0.35f)), y - grosor / 2, y + grosor / 2), Offset(x0, y - grosor / 2), Size(x1 - x0, grosor))
        bridas.forEach { x ->
            drawRect(Brush.verticalGradient(listOf(t(TUBO, oscuro), t(TUBO_OSC, oscuro)), y - grosor * 0.75f, y + grosor * 0.75f), Offset(x - grosor * 0.35f, y - grosor * 0.75f), Size(grosor * 0.7f, grosor * 1.5f))
        }
    }

    // ---------------------------------------------------------------- Social: el comedor común

    override fun DrawScope.muebleSocial(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        fun c(x: Color) = t(x, oscuro)
        val n = enLienzo(area(MuebleVida.SOCIAL))

        // El piso de losetas en damero, crema y verde azulado, con sus juntas.
        val clara = if (oscuro) mezcla(CREMA, NOCHE, 0.78f) else CREMA
        val oscura = if (oscuro) mezcla(PINTURA, NOCHE, 0.62f) else mezcla(CREMA, PINTURA, 0.32f)
        drawRect(clara)
        val lado = 7f * u
        val damero = Path(); val juntas = Path()
        var j = 0; var y = -lado * 0.3f
        while (y < h && j < 40) {
            var i = 0; var x = -lado * 0.5f
            while (x < w && i < 80) { if ((i + j) % 2 == 0) damero.addRect(Rect(x, y, x + lado, y + lado)); x += lado; i++ }
            juntas.moveTo(0f, y); juntas.lineTo(w, y)
            y += lado; j++
        }
        var xj = -lado * 0.5f
        while (xj < w) { juntas.moveTo(xj, 0f); juntas.lineTo(xj, h); xj += lado }
        drawPath(damero, oscura)
        drawPath(juntas, Color.Black.copy(alpha = if (oscuro) 0.2f else 0.08f), style = Stroke(0.25f * u))
        // Las franjas de seguridad pintadas en el piso, a los lados.
        franjaAviso(Rect(0f, 0f, 2.6f * u, h), 3.2f * u, oscuro)
        franjaAviso(Rect(w - 2.6f * u, 0f, w, h), 3.2f * u, oscuro)
        // Las coladeras de las esquinas.
        listOf(Offset(n.left * 0.55f, h * 0.12f), Offset(w - n.left * 0.55f, h * 0.88f)).forEach { p ->
            val r = 3.4f * u
            drawCircle(c(TUBO_OSC), r, p)
            drawCircle(c(TUBO), r, p, style = Stroke(0.6f * u))
            clipRect(p.x - r, p.y - r, p.x + r, p.y + r) { for (k in -2..2) drawLine(c(NEGRO), Offset(p.x + k * 1.2f * u, p.y - r), Offset(p.x + k * 1.2f * u, p.y + r), 0.5f * u) }
        }

        // Los bancos de vinilo capitoneado con su marco de cromo, arriba y abajo de la mesa.
        val bancoA = 0.11f * h
        listOf(Rect(n.left + 3f * u, h * 0.07f, n.right - 3f * u, h * 0.07f + bancoA), Rect(n.left + 3f * u, h * 0.93f - bancoA, n.right - 3f * u, h * 0.93f)).forEachIndexed { k, b ->
            sombra(b, 2f * u, if (oscuro) 0.5f else 0.3f, 2f * u)
            drawRoundRect(Brush.verticalGradient(listOf(c(CROMO), c(TUBO)), b.top, b.bottom), b.topLeft, b.size, CornerRadius(2f * u))
            val cojin = Rect(b.left + 1f * u, b.top + 1f * u, b.right - 1f * u, b.bottom - 1f * u)
            val luzArriba = k == 0
            drawRoundRect(Brush.verticalGradient(if (luzArriba) listOf(c(PINTURA_LUZ), c(PINTURA), c(PINTURA_OSC)) else listOf(c(PINTURA_OSC), c(PINTURA), c(PINTURA_LUZ)), cojin.top, cojin.bottom), cojin.topLeft, cojin.size, CornerRadius(1.5f * u))
            val partes = 4
            val pw = cojin.width / partes
            for (p in 0 until partes) {
                val x0 = cojin.left + pw * p
                if (p > 0) {
                    drawLine(Color.Black.copy(alpha = 0.4f), Offset(x0, cojin.top + 0.5f * u), Offset(x0, cojin.bottom - 0.5f * u), 0.5f * u)
                    drawLine(Color.White.copy(alpha = 0.15f), Offset(x0 + 0.5f * u, cojin.top + 0.5f * u), Offset(x0 + 0.5f * u, cojin.bottom - 0.5f * u), 0.3f * u)
                }
                listOf(0.33f, 0.67f).forEach { f ->
                    val bc = Offset(x0 + pw * f, cojin.center.y)
                    drawCircle(Color.Black.copy(alpha = 0.35f), 0.75f * u, bc)
                    drawCircle(c(PINTURA_LUZ), 0.45f * u, Offset(bc.x - 0.15f * u, bc.y - 0.15f * u))
                }
            }
            drawRoundRect(Color.White.copy(alpha = if (oscuro) 0.06f else 0.18f), Offset(cojin.left + 1f * u, if (luzArriba) cojin.top + 0.6f * u else cojin.bottom - 1.4f * u), Size(cojin.width - 2f * u, 0.8f * u), CornerRadius(0.4f * u))
        }

        // Los taburetes de cromo con su asiento de vinilo rojo, dos a cada lado de la mesa.
        val tr = minOf(6.5f * u, n.left * 0.36f)
        listOf(0.37f, 0.65f).forEach { fy ->
            listOf(n.left * 0.52f, w - n.left * 0.52f).forEach { x ->
                val p = Offset(x, h * fy)
                drawCircle(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.25f), tr * 1.1f, Offset(p.x + 0.8f * u, p.y + 1.4f * u))
                drawCircle(Brush.linearGradient(listOf(c(CROMO), c(TUBO_OSC)), Offset(p.x - tr, p.y - tr), Offset(p.x + tr, p.y + tr)), tr, p)
                drawCircle(Brush.radialGradient(listOf(mezcla(c(ROJO), Color.White, 0.3f), c(ROJO), mezcla(c(ROJO), Color.Black, 0.35f)), Offset(p.x - tr * 0.3f, p.y - tr * 0.35f), tr), tr * 0.8f, p)
                drawCircle(Color.Black.copy(alpha = 0.25f), tr * 0.55f, p, style = Stroke(0.3f * u))
                drawCircle(mezcla(c(ROJO), Color.Black, 0.2f), tr * 0.12f, p)
                drawArc(Color.White.copy(alpha = if (oscuro) 0.2f else 0.5f), 200f, 60f, false, Offset(p.x - tr * 0.62f, p.y - tr * 0.62f), Size(tr * 1.24f, tr * 1.24f), style = Stroke(0.5f * u, cap = StrokeCap.Round))
            }
        }

        // La mesa de fórmica: la sombra, el canto cromado acanalado y el tablero.
        val canto = Rect(n.left - 2.2f * u, n.top - 2.2f * u, n.right + 2.2f * u, n.bottom + 2.2f * u)
        sombra(canto, 3f * u, if (oscuro) 0.55f else 0.35f, 2.4f * u)
        drawRoundRect(Brush.verticalGradient(listOf(c(CROMO), c(TUBO), c(CROMO), c(TUBO_OSC)), canto.top, canto.bottom), canto.topLeft, canto.size, CornerRadius(2.4f * u))
        for (k in 1..3) drawRoundRect(Color.Black.copy(alpha = 0.16f), Offset(canto.left + k * 0.5f * u, canto.top + k * 0.5f * u), Size(canto.width - k * 1f * u, canto.height - k * 1f * u), CornerRadius(2.4f * u - k * 0.5f * u), style = Stroke(0.15f * u))
        drawRect(nicho(e.fondo, CREMA, oscuro), n.topLeft, n.size)
        // El motivo de bumeranes y estrellitas de la fórmica, apenas visible.
        val rnd = Random(e.semilla.toLong())
        val motivo = (if (oscuro) PINTURA_LUZ else PINTURA).copy(alpha = if (oscuro) 0.07f else 0.07f)
        clipRect(n.left, n.top, n.right, n.bottom) {
            for (k in 0 until 18) {
                val p = Offset(n.left + n.width * rnd.nextFloat(), n.top + n.height * rnd.nextFloat())
                val g = rnd.nextFloat() * 360f
                rotate(g, p) {
                    val bum = Path().apply { moveTo(p.x - 3f * u, p.y + 1f * u); quadraticTo(p.x, p.y - 2.4f * u, p.x + 3f * u, p.y + 1f * u); quadraticTo(p.x, p.y - 0.8f * u, p.x - 3f * u, p.y + 1f * u); close() }
                    drawPath(bum, motivo)
                }
                drawCircle(motivo, 0.4f * u, Offset(p.x + 4f * u, p.y + 3f * u))
            }
        }
        drawRect(Color.Black.copy(alpha = 0.25f), n.topLeft, n.size, style = Stroke(0.3f * u))
    }

    // ---------------------------------------------------------------- Mapas: la mesa del supervisor

    override fun DrawScope.muebleMapas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        fun c(x: Color) = t(x, oscuro)
        val n = enLienzo(area(MuebleVida.MAPAS))
        val yB = h * 0.185f; val yF = h * 0.85f

        // El muro de bloques crema, con su franja amarilla y el zócalo verde azulado, que sigue detrás de la mesa.
        drawRect(c(PINTURA_OSC), Offset(0f, h * 0.135f), Size(w, h * 0.865f))
        drawRect(if (oscuro) mezcla(MURO, NOCHE, 0.62f) else MURO, Offset.Zero, Size(w, yB))
        val juntas = Path()
        val ba = h * 0.068f; val bl = w * 0.16f
        var y = 0f; var k = 0
        while (y < yB && k < 10) {
            juntas.moveTo(0f, y); juntas.lineTo(w, y)
            var x = if (k % 2 == 0) bl * 0.25f else bl * 0.75f
            while (x < w) { juntas.moveTo(x, y); juntas.lineTo(x, minOf(y + ba, yB)); x += bl }
            y += ba; k++
        }
        drawPath(juntas, Color.Black.copy(alpha = if (oscuro) 0.25f else 0.12f), style = Stroke(0.35f * u))
        drawRect(c(PINTURA), Offset(0f, h * 0.135f), Size(w, yB - h * 0.135f))
        drawRect(c(AMARILLO), Offset(0f, h * 0.128f), Size(w, 0.9f * u))
        drawLine(Color.White.copy(alpha = if (oscuro) 0.08f else 0.25f), Offset(0f, h * 0.137f), Offset(w, h * 0.137f), 0.3f * u)
        // La rejilla de ventilación y el reloj del muro.
        val rej = Rect(w * 0.27f, h * 0.025f, w * 0.38f, h * 0.095f)
        drawRect(c(TUBO_OSC), rej.topLeft, rej.size)
        for (i in 0 until 5) drawLine(c(NEGRO), Offset(rej.left + 0.8f * u, rej.top + (1f + i * 0.95f) * u), Offset(rej.right - 0.8f * u, rej.top + (1f + i * 0.95f) * u), 0.5f * u)
        val rl = Offset(w * 0.88f, h * 0.065f); val rr = 4.4f * u
        drawCircle(Color.Black.copy(alpha = 0.25f), rr, Offset(rl.x + 0.5f * u, rl.y + 0.6f * u))
        drawCircle(c(TUBO_OSC), rr, rl)
        drawCircle(c(CREMA), rr * 0.82f, rl)
        for (i in 0 until 12) { val a = i * Math.PI / 6; drawLine(c(NEGRO), Offset(rl.x + rr * 0.62f * cos(a).toFloat(), rl.y + rr * 0.62f * sin(a).toFloat()), Offset(rl.x + rr * 0.74f * cos(a).toFloat(), rl.y + rr * 0.74f * sin(a).toFloat()), 0.3f * u) }
        drawLine(c(NEGRO), rl, Offset(rl.x - rr * 0.3f, rl.y - rr * 0.38f), 0.5f * u, StrokeCap.Round)
        drawLine(c(NEGRO), rl, Offset(rl.x + rr * 0.55f, rl.y - rr * 0.1f), 0.4f * u, StrokeCap.Round)
        drawLine(c(ROJO), rl, Offset(rl.x + rr * 0.1f, rl.y + rr * 0.6f), 0.2f * u)

        // La lámpara colgante: el cable, la pantalla esmaltada, el foco y su cono de luz hasta la mesa.
        val lx = w / 2
        val luz = if (oscuro) Color(0xFFFFD88A) else Color(0xFFFFF6D6)
        val cono = Path().apply { moveTo(lx - 5f * u, h * 0.09f); lineTo(lx + 5f * u, h * 0.09f); lineTo(lx + w * 0.3f, n.top); lineTo(lx - w * 0.3f, n.top); close() }
        drawPath(cono, Brush.verticalGradient(listOf(luz.copy(alpha = if (oscuro) 0.3f else 0.45f), luz.copy(alpha = 0f)), h * 0.09f, n.top))
        drawLine(c(NEGRO), Offset(lx, 0f), Offset(lx, h * 0.04f), 0.5f * u)
        val pant = Path().apply { moveTo(lx - 2f * u, h * 0.04f); lineTo(lx + 2f * u, h * 0.04f); lineTo(lx + 7f * u, h * 0.09f); lineTo(lx - 7f * u, h * 0.09f); close() }
        drawPath(pant, Brush.horizontalGradient(listOf(c(PINTURA_OSC), c(PINTURA_LUZ), c(PINTURA_OSC)), lx - 7f * u, lx + 7f * u))
        drawRect(c(CROMO), Offset(lx - 1.2f * u, h * 0.03f), Size(2.4f * u, h * 0.012f))
        if (oscuro) drawCircle(Brush.radialGradient(listOf(luz.copy(alpha = 0.6f), Color.Transparent), Offset(lx, h * 0.095f), 8f * u), 8f * u, Offset(lx, h * 0.095f))
        drawOval(luz, Offset(lx - 3f * u, h * 0.085f), Size(6f * u, 1.8f * u))

        // La mesa en perspectiva: el linóleo moteado, con la luz de la lámpara.
        val tablero = Path().apply { moveTo(w * 0.07f, yB); lineTo(w * 0.93f, yB); lineTo(w * 0.99f, yF); lineTo(w * 0.01f, yF); close() }
        drawPath(tablero, Brush.verticalGradient(listOf(c(mezcla(LINOLEO, Color.White, 0.12f)), c(LINOLEO), c(mezcla(LINOLEO, Color.Black, 0.15f))), yB, yF))
        val rnd = Random(e.semilla.toLong())
        clipPath(tablero) {
            for (i in 0 until 70) drawCircle((if (i % 2 == 0) Color.White else Color.Black).copy(alpha = 0.05f), (0.4f + rnd.nextFloat() * 1.4f) * u, Offset(w * rnd.nextFloat(), yB + (yF - yB) * rnd.nextFloat()))
            drawOval(Brush.radialGradient(listOf(luz.copy(alpha = if (oscuro) 0.16f else 0.22f), Color.Transparent), Offset(lx, (yB + yF) / 2), w * 0.42f), Offset(lx - w * 0.42f, yB), Size(w * 0.84f, yF - yB))
        }
        drawLine(Color.White.copy(alpha = if (oscuro) 0.12f else 0.35f), Offset(w * 0.07f, yB), Offset(w * 0.93f, yB), 0.5f * u)

        // El secante: el marco de cuero pespunteado y el papel crema del mapa.
        val sec = Rect(n.left - 2.2f * u, n.top - 2.2f * u, n.right + 2.2f * u, n.bottom + 2.2f * u)
        sombra(sec, 1.6f * u, if (oscuro) 0.5f else 0.3f)
        val cuero = mezcla(e.secundario, Color(0xFF5A2E1A), 0.55f)
        drawRect(Brush.verticalGradient(listOf(c(cuero), c(mezcla(cuero, Color.Black, 0.25f))), sec.top, sec.bottom), sec.topLeft, sec.size)
        val pesp = Path()
        listOf(Offset(sec.left + 1.1f * u, sec.top + 1.1f * u) to Offset(sec.right - 1.1f * u, sec.top + 1.1f * u), Offset(sec.left + 1.1f * u, sec.bottom - 1.1f * u) to Offset(sec.right - 1.1f * u, sec.bottom - 1.1f * u),
            Offset(sec.left + 1.1f * u, sec.top + 1.1f * u) to Offset(sec.left + 1.1f * u, sec.bottom - 1.1f * u), Offset(sec.right - 1.1f * u, sec.top + 1.1f * u) to Offset(sec.right - 1.1f * u, sec.bottom - 1.1f * u)).forEach { (a, b) ->
            val largo = (b - a).getDistance(); val pasos = (largo / (1.4f * u)).toInt()
            for (s in 0 until pasos step 1) if (s % 2 == 0) {
                val f0 = s / pasos.toFloat(); val f1 = (s + 1) / pasos.toFloat()
                pesp.moveTo(a.x + (b.x - a.x) * f0, a.y + (b.y - a.y) * f0); pesp.lineTo(a.x + (b.x - a.x) * f1, a.y + (b.y - a.y) * f1)
            }
        }
        drawPath(pesp, c(CREMA).copy(alpha = 0.6f), style = Stroke(0.25f * u))
        drawRect(nicho(e.fondo, CREMA, oscuro), n.topLeft, n.size)
        drawRect(Color.Black.copy(alpha = 0.3f), n.topLeft, n.size, style = Stroke(0.3f * u))

        // A la izquierda, al fondo: la radio de onda corta con su dial, la rejilla del altavoz, las perillas y la antena.
        val izq = Rect(w * 0.035f, h * 0.215f, n.left - 2.8f * u, h * 0.5f)
        drawLine(c(TUBO), Offset(izq.right - 2f * u, izq.top), Offset(izq.right + 6f * u, 0.5f * u), 0.5f * u)
        drawCircle(c(TUBO), 0.6f * u, Offset(izq.right + 6f * u, 0.5f * u))
        sombra(izq, 1.6f * u, 0.4f, 1.2f * u)
        drawRoundRect(Brush.verticalGradient(listOf(c(Color(0xFF55604E)), c(Color(0xFF3B4436))), izq.top, izq.bottom), izq.topLeft, izq.size, CornerRadius(1.2f * u))
        drawRoundRect(Color.White.copy(alpha = if (oscuro) 0.06f else 0.18f), izq.topLeft, Size(izq.width, 0.7f * u), CornerRadius(0.4f * u))
        val dial = Rect(izq.left + izq.width * 0.1f, izq.top + izq.height * 0.1f, izq.right - izq.width * 0.1f, izq.top + izq.height * 0.34f)
        drawRect(c(TUBO_OSC), Offset(dial.left - 0.4f * u, dial.top - 0.4f * u), Size(dial.width + 0.8f * u, dial.height + 0.8f * u))
        drawRect(if (oscuro) mezcla(AMBAR, NOCHE, 0.25f) else CREMA, dial.topLeft, dial.size)
        if (oscuro) drawRect(Brush.radialGradient(listOf(AMBAR.copy(alpha = 0.5f), Color.Transparent), dial.center, dial.width), Offset(dial.left - 3f * u, dial.top - 3f * u), Size(dial.width + 6f * u, dial.height + 6f * u))
        var dx = dial.left + 0.8f * u; var dk = 0
        while (dx < dial.right - 0.5f * u && dk < 60) { drawLine(c(NEGRO).copy(alpha = 0.7f), Offset(dx, dial.top + 0.5f * u), Offset(dx, dial.top + (if (dk % 4 == 0) 1.8f else 1f) * u), 0.2f * u); dx += 0.9f * u; dk++ }
        drawLine(ROJO, Offset(dial.left + dial.width * 0.6f, dial.top + 0.3f * u), Offset(dial.left + dial.width * 0.6f, dial.bottom - 0.3f * u), 0.35f * u)
        val alt = Rect(izq.left + izq.width * 0.1f, izq.top + izq.height * 0.44f, izq.left + izq.width * 0.5f, izq.bottom - izq.height * 0.1f)
        var gy = alt.top + 0.8f * u
        while (gy < alt.bottom) { var gx = alt.left + 0.8f * u; while (gx < alt.right) { drawCircle(c(NEGRO), 0.35f * u, Offset(gx, gy)); gx += 1.3f * u }; gy += 1.3f * u }
        val pc = Offset(izq.left + izq.width * 0.74f, izq.top + izq.height * 0.6f); val pr = minOf(izq.width * 0.17f, 3.2f * u)
        drawCircle(c(NEGRO), pr, pc)
        drawCircle(c(Color(0xFF2E5E3E)), pr * 0.78f, pc)
        drawLine(FOSFORO.copy(alpha = if (oscuro) 0.9f else 0.7f), pc, Offset(pc.x + pr * 0.5f, pc.y - pr * 0.5f), 0.35f * u)
        listOf(0.62f, 0.86f).forEach { f -> drawCircle(c(NEGRO), 0.9f * u, Offset(izq.left + izq.width * f, izq.bottom - izq.height * 0.13f)); drawCircle(c(TUBO), 0.4f * u, Offset(izq.left + izq.width * f - 0.2f * u, izq.bottom - izq.height * 0.13f - 0.2f * u)) }
        piloto(Offset(izq.left + izq.width * 0.14f, izq.bottom - izq.height * 0.06f), 0.5f * u, ROJO, true, oscuro)

        // A la izquierda, al frente: el contador Geiger amarillo con su aguja, el asa y la sonda con su cable.
        val gg = Rect(w * 0.03f, h * 0.58f, n.left - 3.2f * u, h * 0.79f)
        sombra(gg, 1.6f * u, 0.4f, 1f * u)
        drawArc(c(NEGRO), 180f, 180f, false, Offset(gg.left + gg.width * 0.2f, gg.top - gg.height * 0.22f), Size(gg.width * 0.6f, gg.height * 0.44f), style = Stroke(1f * u))
        drawRoundRect(Brush.verticalGradient(listOf(c(mezcla(AMARILLO, Color.White, 0.2f)), c(AMARILLO), c(mezcla(AMARILLO, Color.Black, 0.25f))), gg.top, gg.bottom), gg.topLeft, gg.size, CornerRadius(1f * u))
        val med = Rect(gg.left + gg.width * 0.14f, gg.top + gg.height * 0.16f, gg.right - gg.width * 0.14f, gg.top + gg.height * 0.62f)
        drawRect(c(NEGRO), med.topLeft, med.size)
        drawRect(c(CREMA), Offset(med.left + 0.4f * u, med.top + 0.4f * u), Size(med.width - 0.8f * u, med.height - 0.8f * u))
        drawArc(c(NEGRO).copy(alpha = 0.6f), 200f, 140f, false, Offset(med.center.x - med.width * 0.36f, med.bottom - med.height * 0.4f), Size(med.width * 0.72f, med.height * 0.7f), style = Stroke(0.25f * u))
        drawLine(ROJO, Offset(med.center.x, med.bottom - 0.5f * u), Offset(med.center.x + med.width * 0.22f, med.top + med.height * 0.3f), 0.35f * u)
        listOf(0.3f, 0.7f).forEach { f -> drawCircle(c(NEGRO), 0.9f * u, Offset(gg.left + gg.width * f, gg.bottom - gg.height * 0.18f)) }
        val cable = Path().apply { moveTo(gg.right - 0.5f * u, gg.center.y); cubicTo(gg.right + 3f * u, gg.center.y + 2f * u, gg.right - 2f * u, gg.bottom + 3f * u, gg.right + 2f * u, gg.bottom + 2.5f * u) }
        drawPath(cable, c(NEGRO), style = Stroke(0.5f * u))
        drawRoundRect(c(TUBO_OSC), Offset(gg.left + gg.width * 0.1f, gg.bottom + 1.2f * u), Size(gg.width * 0.95f, 1.6f * u), CornerRadius(0.8f * u))
        drawRect(c(CROMO), Offset(gg.left + gg.width * 0.1f, gg.bottom + 1.2f * u), Size(gg.width * 0.35f, 1.6f * u))

        // A la derecha: el termo con su etiqueta y su tapa, y la taza de café humeante.
        val de = (n.right + w) / 2
        val tw = minOf(w - n.right - 6f * u, 9f * u)
        val te = Rect(de - tw / 2, h * 0.24f, de + tw / 2, h * 0.5f)
        sombra(te, 1.4f * u, 0.4f, tw / 2)
        drawRect(Brush.horizontalGradient(listOf(c(TUBO_OSC), c(CROMO), c(TUBO), c(TUBO_OSC)), te.left, te.right), Offset(te.left, te.top + 2f * u), Size(te.width, te.height - 2f * u))
        drawOval(c(TUBO_OSC), Offset(te.left, te.bottom - 1f * u), Size(te.width, 2f * u))
        drawRect(Brush.horizontalGradient(listOf(c(mezcla(PINTURA, Color.Black, 0.2f)), c(PINTURA_LUZ), c(PINTURA_OSC)), te.left, te.right), Offset(te.left, te.top), Size(te.width, 3f * u))
        drawOval(c(PINTURA_LUZ), Offset(te.left, te.top - 1f * u), Size(te.width, 2f * u))
        drawRect(c(AMARILLO), Offset(te.left, te.top + te.height * 0.45f), Size(te.width, te.height * 0.22f))
        drawRect(c(NEGRO), Offset(te.left + te.width * 0.2f, te.top + te.height * 0.52f), Size(te.width * 0.6f, te.height * 0.08f))
        val ta = Offset(de, h * 0.72f); val tr = minOf(tw * 0.48f, 4f * u)
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(ta.x - tr * 1.3f, ta.y + tr * 0.9f), Size(tr * 2.8f, tr * 0.7f))
        drawArc(c(CREMA), -70f, 140f, false, Offset(ta.x + tr * 0.5f, ta.y - tr * 0.6f), Size(tr * 1.1f, tr * 1.2f), style = Stroke(0.9f * u))
        drawRect(Brush.horizontalGradient(listOf(c(CREMA), Color.White.copy(alpha = if (oscuro) 0.6f else 1f), c(mezcla(CREMA, Color.Black, 0.15f))), ta.x - tr, ta.x + tr), Offset(ta.x - tr, ta.y - tr), Size(tr * 2, tr * 2))
        drawOval(c(mezcla(CREMA, Color.Black, 0.15f)), Offset(ta.x - tr, ta.y + tr - 0.6f * u), Size(tr * 2, 1.2f * u))
        drawRect(c(PINTURA), Offset(ta.x - tr, ta.y - tr * 0.3f), Size(tr * 2, tr * 0.35f))
        drawOval(c(Color(0xFF3A2416)), Offset(ta.x - tr, ta.y - tr - 0.6f * u), Size(tr * 2, 1.2f * u))
        drawOval(c(CREMA), Offset(ta.x - tr, ta.y - tr - 0.6f * u), Size(tr * 2, 1.2f * u), style = Stroke(0.3f * u))
        listOf(-0.4f, 0.3f).forEach { f ->
            val vapor = Path().apply { moveTo(ta.x + tr * f, ta.y - tr - 0.6f * u); cubicTo(ta.x + tr * f - 2f * u, ta.y - tr - 3f * u, ta.x + tr * f + 2f * u, ta.y - tr - 5f * u, ta.x + tr * f, ta.y - tr - 8f * u) }
            drawPath(vapor, Color.White.copy(alpha = if (oscuro) 0.25f else 0.6f), style = Stroke(0.45f * u, cap = StrokeCap.Round))
        }

        // El canto de la mesa: el filo cromado, el frente con el cajón y su tirador; las patas y el piso con su franja de aviso.
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.55f), Color.Transparent), yF, h), Offset(0f, yF), Size(w, h - yF))
        drawRect(c(PISO), Offset(0f, yF), Size(w, h - yF))
        franjaAviso(Rect(0f, h * 0.965f, w, h), 3f * u, oscuro)
        val canto = Rect(w * 0.01f, yF, w * 0.99f, yF + h * 0.07f)
        chapa(canto, c(PINTURA), oscuro, u)
        drawRect(Brush.verticalGradient(listOf(c(CROMO), c(TUBO)), yF, yF + 1f * u), Offset(canto.left, yF), Size(canto.width, 1f * u))
        drawRoundRect(Brush.verticalGradient(listOf(c(CROMO), c(TUBO_OSC)), canto.center.y - 0.6f * u, canto.center.y + 1.2f * u), Offset(w / 2 - 6f * u, canto.center.y - 0.3f * u), Size(12f * u, 1.5f * u), CornerRadius(0.75f * u))
        listOf(canto.left + 2f * u, canto.right - 2f * u).forEach { remache(Offset(it, canto.center.y + 0.4f * u), 0.6f * u, c(TUBO), oscuro) }
        listOf(0.03f to 0.07f, 0.93f to 0.97f).forEach { (a, b) ->
            drawRect(Brush.horizontalGradient(listOf(c(PINTURA_OSC), c(mezcla(PINTURA_OSC, Color.Black, 0.4f))), w * a, w * b), Offset(w * a, canto.bottom), Size(w * (b - a), h * 0.965f - canto.bottom))
        }
    }

    // ---------------------------------------------------------------- Otras vidas: las taquillas de los recuerdos

    override fun DrawScope.muebleOtrasVidas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        fun c(x: Color) = t(x, oscuro)
        val n = enLienzo(area(MuebleVida.OTRAS_VIDAS))

        // El muro crema y las tuberías del techo, con sus bridas, la válvula roja y el manómetro.
        drawRect(if (oscuro) mezcla(MURO, NOCHE, 0.62f) else MURO, Offset.Zero, Size(w, h * 0.12f))
        tubo(h * 0.035f, 3f * u, 0f, w, c(TUBO), listOf(w * 0.12f, w * 0.38f, w * 0.64f, w * 0.9f), oscuro)
        tubo(h * 0.085f, 1.8f * u, 0f, w, c(COBRE), listOf(w * 0.25f, w * 0.75f), oscuro)
        val va = Offset(w * 0.16f, h * 0.085f)
        drawCircle(c(ROJO), 3f * u, va, style = Stroke(0.8f * u))
        for (i in 0 until 3) rotate(i * 60f, va) { drawLine(c(ROJO), Offset(va.x - 3f * u, va.y), Offset(va.x + 3f * u, va.y), 0.5f * u) }
        drawCircle(c(ROJO), 0.8f * u, va)
        manometro(Offset(w * 0.84f, h * 0.075f), 3.6f * u, oscuro)

        // El cuerpo de chapa verde azulada.
        val cuerpo = Rect(0f, h * 0.12f, w, h * 0.9f)
        drawRect(c(PINTURA), cuerpo.topLeft, cuerpo.size)
        // La corona: las rejillas y la placa amarilla del refugio (un triángulo y barras).
        val cor = Rect(0f, h * 0.12f, w, h * 0.28f)
        chapa(cor, c(PINTURA), oscuro, u)
        drawLine(Color.Black.copy(alpha = 0.35f), Offset(0f, cor.bottom), Offset(w, cor.bottom), 0.5f * u)
        val placa = Rect(w * 0.37f, cor.top + cor.height * 0.2f, w * 0.63f, cor.bottom - cor.height * 0.2f)
        listOf(w * 0.05f to placa.left - w * 0.04f, placa.right + w * 0.04f to w * 0.95f).forEach { (a, b) ->
            var x = a
            while (x < b - 0.8f * u) { drawRoundRect(c(PINTURA_OSC), Offset(x, placa.top), Size(0.9f * u, placa.height), CornerRadius(0.45f * u)); x += 2f * u }
        }
        drawRect(c(NEGRO), Offset(placa.left - 0.5f * u, placa.top - 0.5f * u), Size(placa.width + 1f * u, placa.height + 1f * u))
        drawRect(c(AMARILLO), placa.topLeft, placa.size)
        val tri = Path().apply { moveTo(placa.left + placa.width * 0.1f, placa.bottom - placa.height * 0.18f); lineTo(placa.left + placa.width * 0.2f, placa.top + placa.height * 0.18f); lineTo(placa.left + placa.width * 0.3f, placa.bottom - placa.height * 0.18f); close() }
        drawPath(tri, c(NEGRO))
        listOf(0.25f to 0.62f, 0.47f to 0.5f, 0.69f to 0.36f).forEach { (fy, fw) -> drawRect(c(NEGRO), Offset(placa.left + placa.width * 0.38f, placa.top + placa.height * fy - 0.35f * u), Size(placa.width * fw, 0.7f * u)) }
        // La sirena enjaulada sobre la corona.
        val si = Offset(w / 2, cor.top)
        if (oscuro) drawCircle(Brush.radialGradient(listOf(ROJO.copy(alpha = 0.55f), Color.Transparent), si, 12f * u), 12f * u, si)
        drawArc(Brush.radialGradient(listOf(mezcla(ROJO, Color.White, 0.4f), ROJO, mezcla(ROJO, Color.Black, 0.3f)), Offset(si.x - 1f * u, si.y - 3f * u), 5f * u), 180f, 180f, true, Offset(si.x - 3.6f * u, si.y - 3.6f * u), Size(7.2f * u, 7.2f * u))
        for (i in -1..1) drawLine(c(TUBO_OSC), Offset(si.x + i * 1.6f * u, si.y), Offset(si.x + i * 1.2f * u, si.y - 3.4f * u), 0.3f * u)
        drawRect(c(TUBO_OSC), Offset(si.x - 4.6f * u, si.y - 0.6f * u), Size(9.2f * u, 1.2f * u))

        // Los costados: bisagras y placa del número a la izquierda; el pasador con su candado a la derecha.
        listOf(0f to n.left, n.right to w).forEachIndexed { lado, (x0, x1) ->
            val an = x1 - x0; val cx = (x0 + x1) / 2
            drawRect(c(PINTURA_LUZ), Offset(if (lado == 0) x0 else x1 - 0.6f * u, cuerpo.top), Size(0.6f * u, n.bottom - cuerpo.top))
            drawRect(c(PINTURA_OSC), Offset(if (lado == 0) x1 - 0.8f * u else x0, cor.bottom), Size(0.8f * u, n.bottom - cor.bottom))
            for (j in 0 until 3) drawRoundRect(c(PINTURA_OSC), Offset(cx - an * 0.28f, h * 0.36f + j * 1.6f * u), Size(an * 0.56f, 0.8f * u), CornerRadius(0.4f * u))
            if (lado == 0) {
                listOf(h * 0.48f, h * 0.74f).forEach { y ->
                    drawRect(c(TUBO_OSC), Offset(x1 - 2.2f * u, y), Size(2.2f * u, 5f * u))
                    drawLine(c(TUBO), Offset(x1 - 1.1f * u, y), Offset(x1 - 1.1f * u, y + 5f * u), 0.4f * u)
                }
                drawRect(c(AMARILLO), Offset(cx - an * 0.32f, h * 0.6f), Size(an * 0.5f, 3f * u))
                drawRect(c(NEGRO), Offset(cx - an * 0.22f, h * 0.6f + 0.8f * u), Size(0.6f * u, 1.4f * u))
            } else {
                val ca = Offset(cx, h * 0.62f)
                drawRect(c(TUBO_OSC), Offset(x0, ca.y - 0.8f * u), Size(an * 0.6f, 1.6f * u))
                drawArc(c(TUBO), 180f, 180f, false, Offset(ca.x - 1.3f * u, ca.y - 0.4f * u), Size(2.6f * u, 3f * u), style = Stroke(0.6f * u))
                drawRoundRect(Brush.verticalGradient(listOf(mezcla(AMARILLO, Color.White, 0.25f), c(AMARILLO), c(mezcla(AMARILLO, Color.Black, 0.3f))), ca.y + 1f * u, ca.y + 5f * u), Offset(ca.x - 1.8f * u, ca.y + 1f * u), Size(3.6f * u, 3.6f * u), CornerRadius(0.6f * u))
                drawCircle(c(NEGRO), 0.4f * u, Offset(ca.x, ca.y + 2.6f * u))
            }
        }

        // El nicho: el fondo de chapa perforada pintada, en cinco casilleros con su número y montantes remachados.
        drawRect(Color.Black.copy(alpha = 0.45f), Offset(n.left - 0.6f * u, n.top - 0.6f * u), Size(n.width + 1.2f * u, n.height + 1.2f * u))
        drawRect(nicho(e.fondo, MURO, oscuro), n.topLeft, n.size)
        val agujero = Color.Black.copy(alpha = if (oscuro) 0.22f else 0.08f)
        var py = n.top + 3f * u; var fila = 0
        while (py < n.bottom - 1f * u && fila < 60) {
            var px = n.left + (if (fila % 2 == 0) 1.8f else 3.6f) * u
            while (px < n.right) { drawCircle(agujero, 0.32f * u, Offset(px, py)); px += 3.6f * u }
            py += 2.6f * u; fila++
        }
        val cw = n.width / 5f
        for (i in 0 until 5) {
            val r = Rect(n.left + cw * i, n.top, n.left + cw * (i + 1), n.bottom)
            drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.45f else 0.22f), Color.Transparent), r.top, r.top + 4.5f * u), r.topLeft, Size(r.width, 4.5f * u))
            // La plaquita del número: barras I, II, III, como en las taquillas de la biblioteca.
            val np = Rect(r.left + 1.6f * u, r.top + 1.4f * u, r.left + 1.6f * u + 4.4f * u, r.top + 4f * u)
            drawRect(c(AMARILLO), np.topLeft, np.size)
            for (b in 0..(i % 3)) drawRect(c(NEGRO), Offset(np.left + 0.8f * u + b * 1.1f * u, np.top + 0.5f * u), Size(0.55f * u, np.height - 1f * u))
            piloto(Offset(r.right - 2.4f * u, r.top + 2.6f * u), 0.55f * u, if (oscuro) (if (i == 2) e.secundario else e.acento) else (if (i == 2) AMBAR else FOSFORO), oscuro || i == 2, oscuro)
        }
        val mont = 1.4f * u
        for (i in 1 until 5) {
            val x = n.left + cw * i
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(x + mont / 2, n.top), Size(0.6f * u, n.height))
            drawRect(Brush.horizontalGradient(listOf(c(PINTURA_LUZ), c(PINTURA), c(PINTURA_OSC)), x - mont / 2, x + mont / 2), Offset(x - mont / 2, n.top), Size(mont, n.height))
        }

        // La repisa remachada, el zócalo con su rejilla y el piso con la franja amarilla.
        val rep = Rect(0f, n.bottom, w, h * 0.905f)
        chapa(rep, c(PINTURA), oscuro, u)
        var rx = w * 0.06f
        while (rx < w * 0.95f) { drawCircle(c(PINTURA_LUZ), 0.45f * u, Offset(rx, rep.center.y)); rx += w * 0.08f }
        val zo = Rect(0f, h * 0.905f, w, h * 0.965f)
        drawRect(c(PINTURA_OSC), zo.topLeft, zo.size)
        var zx = w * 0.3f
        while (zx < w * 0.7f) { drawRect(c(NEGRO), Offset(zx, zo.top + zo.height * 0.25f), Size(0.6f * u, zo.height * 0.5f)); zx += 1.4f * u }
        drawRect(c(PISO), Offset(0f, h * 0.965f), Size(w, h * 0.035f))
        drawRect(c(AMARILLO), Offset(0f, h * 0.975f), Size(w, 0.7f * u))
    }

    // ---------------------------------------------------------------- Íconos de los mapas

    override fun DrawScope.iconoMapa(id: String, e: EstadoIcono, oscuro: Boolean) {
        val w = size.width; val h = size.height
        val s = h / 70f // un dp del ícono (el lienzo mide ≈ 70 dp de alto)
        val c = Offset(w / 2, h / 2)
        fun k(x: Color) = t(x, oscuro)
        when (id) {
            "unesco" -> {
                // El búnker en la colina con la puerta engranada de la bóveda, su aro de aviso, la antena y su luz.
                val suelo = c.y + 27f * s
                val colina = Path().apply { moveTo(c.x - 48f * s, suelo); cubicTo(c.x - 34f * s, c.y - 22f * s, c.x + 34f * s, c.y - 22f * s, c.x + 48f * s, suelo); close() }
                drawPath(colina, Brush.verticalGradient(listOf(k(Color(0xFF8C8A5E)), k(Color(0xFF5E6A44))), c.y - 16f * s, suelo))
                drawLine(k(TUBO_OSC), Offset(c.x + 22f * s, c.y - 6f * s), Offset(c.x + 26f * s, c.y - 30f * s), 1.2f * s)
                piloto(Offset(c.x + 26f * s, c.y - 30f * s), 1.6f * s, ROJO, true, oscuro)
                val fach = Path().apply { moveTo(c.x - 26f * s, suelo); lineTo(c.x - 22f * s, c.y - 14f * s); lineTo(c.x + 22f * s, c.y - 14f * s); lineTo(c.x + 26f * s, suelo); close() }
                drawPath(fach, Brush.horizontalGradient(listOf(k(Color(0xFFB8B4A6)), k(Color(0xFF8E8B80))), c.x - 26f * s, c.x + 26f * s))
                drawRect(Color.Black.copy(alpha = 0.25f), Offset(c.x - 22f * s, c.y - 14f * s), Size(44f * s, 2f * s))
                val pc = Offset(c.x, c.y + 7f * s); val pr = 16f * s
                drawCircle(Color.Black.copy(alpha = 0.4f), pr * 1.15f, Offset(pc.x + 1f * s, pc.y + 1.5f * s))
                drawCircle(k(AMARILLO), pr * 1.12f, pc)
                for (i in 0 until 16) if (i % 2 == 0) drawArc(k(NEGRO), i * 22.5f, 22.5f, true, Offset(pc.x - pr * 1.12f, pc.y - pr * 1.12f), Size(pr * 2.24f, pr * 2.24f))
                drawCircle(k(TUBO_OSC), pr * 0.97f, pc)
                drawPath(engrane(pc, pr * 0.92f, 12), Brush.radialGradient(listOf(k(CROMO), k(TUBO)), Offset(pc.x - pr * 0.3f, pc.y - pr * 0.3f), pr * 1.2f))
                drawCircle(k(TUBO_OSC), pr * 0.52f, pc)
                drawCircle(k(TUBO), pr * 0.44f, pc)
                drawRect(k(AMARILLO), Offset(pc.x - pr * 0.24f, pc.y - pr * 0.1f), Size(pr * 0.48f, pr * 0.2f))
                for (i in 0 until 3) drawRect(k(NEGRO), Offset(pc.x - pr * 0.18f + i * pr * 0.13f, pc.y - pr * 0.07f), Size(pr * 0.07f, pr * 0.14f))
                franjaAviso(Rect(c.x - 26f * s, suelo - 2.5f * s, c.x + 26f * s, suelo), 3f * s, oscuro)
            }
            "rascar" -> {
                // La tablilla con su pinza cromada y el mapa; la capa gris se raspa con una chapa de botella.
                rotate(-5f, c) {
                    val tb = Rect(c.x - 30f * s, c.y - 28f * s, c.x + 30f * s, c.y + 28f * s)
                    drawRoundRect(Color.Black.copy(alpha = 0.3f), Offset(tb.left + 1.5f * s, tb.top + 2f * s), tb.size, CornerRadius(3f * s))
                    drawRoundRect(Brush.verticalGradient(listOf(k(PINTURA_LUZ), k(PINTURA_OSC)), tb.top, tb.bottom), tb.topLeft, tb.size, CornerRadius(3f * s))
                    val hoja = Rect(tb.left + 4f * s, tb.top + 7f * s, tb.right - 4f * s, tb.bottom - 4f * s)
                    drawRect(k(CREMA), hoja.topLeft, hoja.size)
                    val tierra = Path().apply {
                        moveTo(hoja.left + 3f * s, hoja.top + 8f * s); cubicTo(hoja.left + 14f * s, hoja.top + 2f * s, hoja.left + 24f * s, hoja.top + 12f * s, hoja.left + 34f * s, hoja.top + 6f * s)
                        cubicTo(hoja.right - 4f * s, hoja.top + 4f * s, hoja.right - 2f * s, hoja.top + 22f * s, hoja.right - 10f * s, hoja.top + 28f * s)
                        cubicTo(hoja.left + 26f * s, hoja.bottom - 4f * s, hoja.left + 8f * s, hoja.bottom - 10f * s, hoja.left + 3f * s, hoja.top + 8f * s); close()
                    }
                    drawPath(tierra, e.c1.copy(alpha = 0.3f)); drawPath(tierra, e.c1.copy(alpha = 0.8f), style = Stroke(0.8f * s))
                    // La capa de rascar sobre la mitad de abajo, con el tramo raspado.
                    val capa = Rect(hoja.left, hoja.top + 18f * s, hoja.right, hoja.bottom)
                    val raspado = Path().apply {
                        moveTo(capa.left + 6f * s, capa.top); lineTo(capa.left + 26f * s, capa.top); lineTo(capa.left + 30f * s, capa.top + 8f * s); lineTo(capa.left + 24f * s, capa.top + 12f * s)
                        lineTo(capa.left + 28f * s, capa.bottom); lineTo(capa.left + 10f * s, capa.bottom); lineTo(capa.left + 4f * s, capa.top + 9f * s); close()
                    }
                    clipPath(raspado, clipOp = ClipOp.Difference) {
                        drawRect(Brush.linearGradient(listOf(k(Color(0xFFB4B8BA)), k(Color(0xFF8E9396)), k(Color(0xFFB4B8BA))), capa.topLeft, capa.bottomRight), capa.topLeft, capa.size)
                    }
                    drawPath(raspado, k(TUBO_OSC).copy(alpha = 0.5f), style = Stroke(0.5f * s))
                    // La pinza cromada.
                    val pz = Rect(c.x - 11f * s, tb.top - 3f * s, c.x + 11f * s, tb.top + 7f * s)
                    drawRoundRect(Brush.verticalGradient(listOf(k(CROMO), k(TUBO_OSC)), pz.top, pz.bottom), pz.topLeft, pz.size, CornerRadius(2f * s))
                    drawCircle(k(TUBO_OSC), 2f * s, Offset(c.x, pz.top + 2.5f * s), style = Stroke(1f * s))
                }
                // La chapa de botella, roja, con su borde ondulado.
                val ch = Offset(c.x + 24f * s, c.y + 14f * s); val cr = 8f * s
                drawCircle(Color.Black.copy(alpha = 0.35f), cr, Offset(ch.x + 1f * s, ch.y + 1.5f * s))
                drawPath(engrane(ch, cr, 11), k(TUBO))
                drawCircle(Brush.radialGradient(listOf(mezcla(e.c2, Color.White, 0.35f), e.c2, mezcla(e.c2, Color.Black, 0.3f)), Offset(ch.x - 2f * s, ch.y - 2f * s), cr), cr * 0.72f, ch)
                drawPath(estrella(ch, cr * 0.42f), k(CREMA))
            }
            else -> {
                // El plano con el agua, las calles, la ruta punteada y tres marcadores de colores con su sombra.
                val pl = Rect(c.x - 42f * s, c.y - 24f * s, c.x + 42f * s, c.y + 26f * s)
                drawRect(Color.Black.copy(alpha = 0.25f), Offset(pl.left + 1.5f * s, pl.top + 2f * s), pl.size)
                drawRect(k(CREMA), pl.topLeft, pl.size)
                clipRect(pl.left, pl.top, pl.right, pl.bottom) {
                    val agua = Path().apply { moveTo(pl.left, pl.bottom - 14f * s); cubicTo(pl.left + 20f * s, pl.bottom - 22f * s, pl.left + 36f * s, pl.bottom - 4f * s, pl.left + 60f * s, pl.bottom); lineTo(pl.left, pl.bottom); close() }
                    drawPath(agua, k(PINTURA_LUZ).copy(alpha = 0.55f))
                    for (i in 1 until 8) drawLine(k(MURO), Offset(pl.left + i * 11f * s, pl.top), Offset(pl.left + i * 11f * s - 6f * s, pl.bottom), 1.2f * s)
                    for (j in 1 until 5) drawLine(k(MURO), Offset(pl.left, pl.top + j * 10f * s), Offset(pl.right, pl.top + j * 10f * s + 4f * s), 1.2f * s)
                }
                drawRect(k(PINTURA_OSC), pl.topLeft, pl.size, style = Stroke(1f * s))
                val ms = listOf(Offset(pl.left + 16f * s, pl.top + 26f * s) to e.c1, Offset(pl.left + 44f * s, pl.top + 14f * s) to e.c2, Offset(pl.right - 14f * s, pl.top + 30f * s) to k(AMARILLO))
                val ruta = Path().apply { moveTo(ms[0].first.x, ms[0].first.y); ms.drop(1).forEach { lineTo(it.first.x, it.first.y) } }
                drawPath(ruta, k(NEGRO).copy(alpha = 0.6f), style = Stroke(0.9f * s, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2f * s, 2f * s))))
                ms.forEach { (p, col) ->
                    drawOval(Color.Black.copy(alpha = 0.3f), Offset(p.x - 3f * s, p.y - 1f * s), Size(6f * s, 2f * s))
                    val gota = Path().apply { moveTo(p.x, p.y); cubicTo(p.x - 7f * s, p.y - 9f * s, p.x - 6f * s, p.y - 17f * s, p.x, p.y - 17f * s); cubicTo(p.x + 6f * s, p.y - 17f * s, p.x + 7f * s, p.y - 9f * s, p.x, p.y); close() }
                    drawPath(gota, Brush.linearGradient(listOf(mezcla(col, Color.White, 0.25f), col, mezcla(col, Color.Black, 0.3f)), Offset(p.x - 6f * s, p.y - 17f * s), Offset(p.x + 6f * s, p.y)))
                    drawPath(gota, Color.Black.copy(alpha = 0.3f), style = Stroke(0.4f * s))
                    drawCircle(k(CREMA), 2.4f * s, Offset(p.x, p.y - 11.5f * s))
                }
            }
        }
    }

    // ---------------------------------------------------------------- Notas: el tablón de avisos

    override fun DrawScope.tableroNotas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        fun c(x: Color) = t(x, oscuro)
        val n = enLienzo(area(MuebleVida.NOTAS))

        // El marco de chapa verde azulada, con el filo cromado por dentro, las rejillas y los tornillos.
        drawRect(c(PINTURA), Offset.Zero, Size(w, h * 0.93f))
        bisel(Rect(0f, 0f, w, h * 0.93f), Color.White.copy(alpha = if (oscuro) 0.08f else 0.22f), Color.Black.copy(alpha = 0.35f), 0.6f * u)
        listOf(n.left / 2, w - (w - n.right) / 2).forEach { x ->
            for (j in 0 until 4) drawRoundRect(c(PINTURA_OSC), Offset(x - 1.6f * u, h * 0.44f + j * 1.8f * u), Size(3.2f * u, 0.9f * u), CornerRadius(0.45f * u))
            remache(Offset(x, h * 0.22f), 0.8f * u, c(PINTURA), oscuro)
            remache(Offset(x, h * 0.84f), 0.8f * u, c(PINTURA), oscuro)
        }
        drawRect(Brush.verticalGradient(listOf(c(CROMO), c(TUBO_OSC)), n.top - 1.2f * u, n.bottom + 1.2f * u), Offset(n.left - 1.2f * u, n.top - 1.2f * u), Size(n.width + 2.4f * u, n.height + 2.4f * u))
        // El nicho: chapa perforada pintada, con sus agujeros en retícula y la sombra del marco.
        drawRect(nicho(e.fondo, MURO, oscuro), n.topLeft, n.size)
        val agujero = Color.Black.copy(alpha = if (oscuro) 0.25f else 0.1f)
        val paso = 3.4f * u
        var py = n.top + paso * 0.6f
        while (py < n.bottom) { var px = n.left + paso * 0.6f; while (px < n.right) { drawCircle(agujero, 0.36f * u, Offset(px, py)); px += paso }; py += paso }
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.35f else 0.16f), Color.Transparent), n.top, n.top + 2.5f * u), n.topLeft, Size(n.width, 2.5f * u))

        // La corona: el sol atómico en su placa crema y los pilotos ámbar.
        val cor = Rect(0f, 0f, w, h * 0.13f)
        chapa(cor, c(PINTURA), oscuro, u)
        drawLine(Color.Black.copy(alpha = 0.35f), Offset(0f, cor.bottom), Offset(w, cor.bottom), 0.5f * u)
        val sc = Offset(w / 2, cor.center.y)
        val pw = minOf(w * 0.2f, 26f * u); val ph = cor.height * 0.8f
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(sc.x - pw / 2 + 0.4f * u, sc.y - ph / 2 + 0.6f * u), Size(pw, ph))
        drawOval(c(CREMA), Offset(sc.x - pw / 2, sc.y - ph / 2), Size(pw, ph))
        drawOval(c(TUBO_OSC), Offset(sc.x - pw / 2, sc.y - ph / 2), Size(pw, ph), style = Stroke(0.5f * u))
        clipPath(Path().apply { addOval(Rect(sc.x - pw / 2, sc.y - ph / 2, sc.x + pw / 2, sc.y + ph / 2)) }) {
            for (i in 0 until 16) {
                val a = i * Math.PI / 8
                val rayo = Path().apply {
                    moveTo(sc.x, sc.y)
                    lineTo(sc.x + pw * cos(a - 0.09).toFloat(), sc.y + pw * sin(a - 0.09).toFloat())
                    lineTo(sc.x + pw * cos(a + 0.09).toFloat(), sc.y + pw * sin(a + 0.09).toFloat()); close()
                }
                drawPath(rayo, c(AMARILLO).copy(alpha = 0.75f))
            }
        }
        atomo(sc, ph * 0.36f, c(NARANJA), c(PINTURA_OSC), 0.5f * u)
        listOf(w * 0.08f, w * 0.13f, w * 0.87f, w * 0.92f).forEachIndexed { i, x -> piloto(Offset(x, cor.center.y), 0.9f * u, if (i % 2 == 0) AMBAR else FOSFORO, i != 1 || oscuro, oscuro) }

        // La repisa con el filo cromado y la cinta de aviso.
        val rep = Rect(0f, h * 0.93f, w, h)
        drawRect(c(PINTURA_OSC), rep.topLeft, rep.size)
        drawRect(Brush.verticalGradient(listOf(c(CROMO), c(TUBO_OSC)), rep.top, rep.top + 1.2f * u), rep.topLeft, Size(w, 1.2f * u))
        franjaAviso(Rect(0f, rep.top + 1.6f * u, w, h - 1f * u), 3f * u, oscuro)
    }

    override fun DrawScope.notaTablero(e: EstadoNota, oscuro: Boolean) {
        val r0 = e.rect
        val u = minOf(r0.width, r0.height) / 100f
        val r = Rect(r0.left + r0.width * 0.03f, r0.top + r0.height * 0.04f, r0.right - r0.width * 0.03f, r0.bottom - r0.height * 0.04f)
        val gir = if (e.fijada) 0f else ((e.indice * 41) % 5 - 2) * 0.5f
        val franja = when (e.tipo) {
            "pensamiento" -> NARANJA
            "proposito" -> AMARILLO
            "recordatorio" -> ROJO
            else -> PINTURA
        }
        val col = t(franja, oscuro)
        val hoja = if (oscuro) AVISO_NOCHE else AVISO
        val radio = if (e.tipo == "pensamiento") 4f * u else 1f * u
        rotate(gir, r.center) {
            drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.55f else 0.25f), Offset(r.left + 1.2f * u, r.top + 2f * u), r.size, CornerRadius(radio))
            val forma = Path().apply {
                if (e.tipo == "recordatorio") {
                    // La esquina de abajo doblada.
                    moveTo(r.left, r.top); lineTo(r.right, r.top); lineTo(r.right, r.bottom - r.width * 0.12f); lineTo(r.right - r.width * 0.12f, r.bottom); lineTo(r.left, r.bottom); close()
                } else addRoundRect(RoundRect(r, CornerRadius(radio)))
            }
            drawPath(forma, hoja)
            clipPath(forma) {
                // La franja de color del tipo, arriba, dentro del margen.
                drawRect(col, r.topLeft, Size(r.width, r.height * 0.1f))
                drawRect(Color.White.copy(alpha = 0.18f), r.topLeft, Size(r.width, r.height * 0.02f))
                // La línea de corte punteada, abajo.
                var x = r.left + 1.5f * u
                while (x < r.right - 1f * u) { drawCircle((if (oscuro) FOSFORO else NEGRO).copy(alpha = 0.22f), 0.35f * u, Offset(x, r.bottom - r.height * 0.07f)); x += 2.2f * u }
            }
            if (e.tipo == "recordatorio") {
                val pl = Path().apply { moveTo(r.right, r.bottom - r.width * 0.12f); lineTo(r.right - r.width * 0.12f, r.bottom - r.width * 0.12f); lineTo(r.right - r.width * 0.12f, r.bottom); close() }
                drawPath(pl, mezcla(hoja, Color.Black, 0.18f))
                // El relojito en la franja.
                val rc = Offset(r.right - r.width * 0.08f, r.top + r.height * 0.05f)
                drawCircle(t(CREMA, oscuro), r.height * 0.032f, rc)
                drawLine(col, rc, Offset(rc.x, rc.y - r.height * 0.022f), 0.4f * u)
                drawLine(col, rc, Offset(rc.x + r.height * 0.018f, rc.y), 0.4f * u)
            }
            when (e.tipo) {
                "idea" -> atomo(Offset(r.right - r.width * 0.08f, r.top + r.height * 0.05f), r.height * 0.035f, t(CREMA, oscuro), t(CREMA, oscuro), 0.3f * u)
                "proposito" -> {
                    val cb = Rect(r.left + r.width * 0.025f, r.bottom - r.height * 0.15f, r.left + r.width * 0.075f, r.bottom - r.height * 0.15f + r.width * 0.05f)
                    drawRect((if (oscuro) FOSFORO else NEGRO).copy(alpha = 0.6f), cb.topLeft, cb.size, style = Stroke(0.4f * u))
                    drawPath(Path().apply { moveTo(cb.left + cb.width * 0.15f, cb.center.y); lineTo(cb.center.x, cb.bottom - cb.height * 0.15f); lineTo(cb.right + cb.width * 0.2f, cb.top - cb.height * 0.2f) }, t(NARANJA, oscuro), style = Stroke(0.5f * u))
                }
                else -> {}
            }
            drawPath(forma, Color.Black.copy(alpha = 0.18f), style = Stroke(0.3f * u))
            if (e.fijada) {
                // El imán de estrella amarillo.
                val ic = Offset(r.center.x, r.top + r.height * 0.05f)
                drawPath(estrella(Offset(ic.x + 0.8f * u, ic.y + 1.4f * u), 5.4f * u), Color.Black.copy(alpha = 0.35f))
                drawPath(estrella(ic, 5.4f * u), Brush.radialGradient(listOf(mezcla(AMARILLO, Color.White, 0.4f), t(AMARILLO, oscuro), t(mezcla(AMARILLO, NARANJA, 0.5f), oscuro)), Offset(ic.x - 1.5f * u, ic.y - 1.5f * u), 6f * u))
                drawPath(estrella(ic, 5.4f * u), Color.Black.copy(alpha = 0.3f), style = Stroke(0.3f * u))
                drawCircle(Color.White.copy(alpha = 0.6f), 0.7f * u, Offset(ic.x - 1.2f * u, ic.y - 1.2f * u))
            } else {
                // Un trozo de cinta adhesiva, translúcido, arriba al centro.
                rotate(-4f, Offset(r.center.x, r.top)) {
                    val ci = Rect(r.center.x - r.width * 0.16f, r.top - r.height * 0.035f, r.center.x + r.width * 0.16f, r.top + r.height * 0.05f)
                    drawRect(Color(0xFFEFE6C4).copy(alpha = if (oscuro) 0.35f else 0.75f), ci.topLeft, ci.size)
                    drawRect(Color.Black.copy(alpha = 0.08f), ci.topLeft, ci.size, style = Stroke(0.25f * u))
                }
            }
        }
    }

    /** El texto de los avisos: tinta de cinta de máquina de día; fósforo verde en la penumbra. */
    override fun tintaNota(oscuro: Boolean): Color = if (oscuro) Color(0xFF9CF5B0) else Color(0xFF1F2A2A)
}
