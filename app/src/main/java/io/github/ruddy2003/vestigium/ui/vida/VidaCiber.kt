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
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.lerp
import kotlin.math.cos
import kotlin.math.sin

/**
 * Terminal: la sala de cómputo de noche, siempre en oscuro, con el mismo equipo que su biblioteca de disquetes y su Hall:
 * carcasas de plástico beige envejecido, gabinetes de grafito con sus rieles perforados, tornillos, rejillas de ventilación,
 * pantallas de fósforo con sus líneas de barrido y pilotos verdes y ámbar con su halo. El cian del esquema (el acento) es el
 * fósforo; el secundario, sus reflejos. Cada nicho es una pantalla apagada o un tablero perforado, del tono de la superficie.
 * - Social: el bastidor de la red: arriba, el panel de parcheo con sus 24 puertos y los cables de colores que suben; en
 *   medio, el monitor de bastidor (el nicho es su pantalla) con su bisel beige, sus botones y su piloto; abajo, el switch con
 *   un par de luces por puerto; a los lados, los rieles con sus agujeros cuadrados y sus tornillos.
 * - Mapas: la mesa trazadora: la consola beige con la pantalla horizontal (el nicho), el puente del trazador con su carro y
 *   sus tres plumillas y los motores en los extremos, la perilla, el osciloscopio y los interruptores en los costados, el
 *   frente inclinado con su teclado y las patas de grafito sobre el piso técnico.
 * - Otras vidas: el gabinete de servidores con cinco bahías iguales (el nicho), cada una con su luz de techo y sus pilotos;
 *   arriba, la moldura beige, la pantallita de cristal líquido, la rejilla hexagonal y la cerradura; abajo, las manijas con
 *   su etiqueta, la rejilla y las ruedas.
 * - Íconos: la torre de radio con su antena parabólica y su caseta; el monitor de tubo con la capa que se rasca y deja ver
 *   la costa; la tarjeta de circuito con tres LED encendidos como marcadores.
 * - Notas: el tablero perforado de grafito bajo su lámpara de tubo, con la repisa beige y su cinta rotulada; cada nota es una
 *   tarjeta perforada con la esquina cortada, la franja del color del tipo con su signo, las perforaciones abajo y un imán
 *   (o, si está fijada, una pinza negra).
 */
internal object VidaCiber : DisenoVida {
    private val PARED = Color(0xFF0A1316)
    private val PARED_JUNTA = Color(0xFF050A0C)
    private val LOSETA = Color(0xFF121C20)
    private val CARCASA = Color(0xFF8A8472)
    private val CARCASA_LUZ = Color(0xFFA9A28D)
    private val CARCASA_SOMBRA = Color(0xFF656050)
    private val CARCASA_OSC = Color(0xFF3F3B31)
    private val GRAFITO = Color(0xFF262A2F)
    private val GRAFITO_LUZ = Color(0xFF41464D)
    private val GRAFITO_OSC = Color(0xFF15181B)
    private val NEGRO = Color(0xFF060809)
    private val METAL = Color(0xFF8D9399)
    private val METAL_LUZ = Color(0xFFC9CED3)
    private val METAL_OSC = Color(0xFF4A5056)
    /** El vidrio de las pantallas apagadas: casi la superficie del esquema. */
    private val PANTALLA = Color(0xFF071416)
    /** El tablero perforado de las notas. */
    private val TABLERO = Color(0xFF0C1619)
    private val PILOTO = Color(0xFF5CF06A)
    private val AMBAR = Color(0xFFFFB347)
    private val ROJO = Color(0xFFE0443A)
    private val TUBO = Color(0xFFDDF6FF)
    private val ORO = Color(0xFFB8963E)
    private val PCB = Color(0xFF0F3A36)
    private val PCB_OSC = Color(0xFF082220)
    /** La cartulina de las tarjetas perforadas, y la tinta con que se escribe en ellas. */
    private val FICHA = Color(0xFFE4DCC3)
    private val FICHA_SOMBRA = Color(0xFFBDB398)
    private val TINTA = Color(0xFF151E20)
    private val CABLES = listOf(Color(0xFF2E6FA8), Color(0xFFC9A23A), Color(0xFFA8382E), Color(0xFF3A8A4A), Color(0xFF8A8F96), Color(0xFF6A4FA0))

    override fun area(m: MuebleVida): Rect = when (m) {
        MuebleVida.SOCIAL -> Rect(0.12f, 0.19f, 0.88f, 0.775f)
        MuebleVida.MAPAS -> Rect(0.105f, 0.15f, 0.895f, 0.74f)
        MuebleVida.OTRAS_VIDAS -> Rect(0.06f, 0.2f, 0.94f, 0.82f)
        MuebleVida.NOTAS -> Rect(0.075f, 0.12f, 0.925f, 0.87f)
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

    /** El nicho de [m] en píxeles del lienzo. */
    private fun DrawScope.nicho(m: MuebleVida): Rect {
        val a = area(m)
        return Rect(size.width * a.left, size.height * a.top, size.width * a.right, size.height * a.bottom)
    }

    /** El halo de una luz: una mancha radial que se apaga hacia afuera. */
    private fun DrawScope.halo(c: Offset, r: Float, color: Color) {
        val rr = r.coerceAtLeast(1f)
        drawCircle(Brush.radialGradient(listOf(color, color.copy(alpha = 0f)), c, rr), rr, c)
    }

    /** Una pieza con volumen: la sombra abajo y a la derecha, la luz arriba y a la izquierda, y la cara. */
    private fun DrawScope.pieza(r: Rect, cara: Color, luz: Color, sombra: Color, g: Float, radio: Float = 0f) {
        val cr = CornerRadius(radio)
        drawRoundRect(sombra, r.topLeft, r.size, cr)
        drawRoundRect(luz, r.topLeft, Size(r.width - g, r.height - g), cr)
        drawRoundRect(cara, Offset(r.left + g, r.top + g), Size((r.width - 2 * g).coerceAtLeast(0f), (r.height - 2 * g).coerceAtLeast(0f)), CornerRadius((radio - g).coerceAtLeast(0f)))
    }

    /** Un tornillo de cabeza redonda con su ranura, su brillo y su sombra. */
    private fun DrawScope.tornillo(p: Offset, r: Float) {
        drawCircle(NEGRO.copy(alpha = 0.55f), r * 1.12f, Offset(p.x + r * 0.25f, p.y + r * 0.3f))
        drawCircle(Brush.radialGradient(listOf(METAL_LUZ, METAL, METAL_OSC), Offset(p.x - r * 0.35f, p.y - r * 0.35f), r * 1.6f), r, p)
        drawLine(METAL_OSC, Offset(p.x - r * 0.62f, p.y + r * 0.32f), Offset(p.x + r * 0.62f, p.y - r * 0.32f), r * 0.32f)
    }

    /** Un piloto: encendido, con su halo y su reflejo; apagado, el vidrio oscuro del color. */
    private fun DrawScope.piloto(p: Offset, r: Float, color: Color, encendido: Boolean) {
        if (encendido) {
            halo(p, r * 4.5f, color.copy(alpha = 0.32f))
            drawCircle(color, r, p)
            drawCircle(Color.White.copy(alpha = 0.65f), r * 0.38f, Offset(p.x - r * 0.3f, p.y - r * 0.3f))
        } else {
            drawCircle(mez(color, NEGRO, 0.78f), r, p)
            drawCircle(Color.White.copy(alpha = 0.12f), r * 0.35f, Offset(p.x - r * 0.3f, p.y - r * 0.3f))
        }
    }

    /** Ranuras de ventilación con su bisel: la ranura oscura y el filo de luz bajo ella. */
    private fun DrawScope.ranuras(r: Rect, n: Int, vertical: Boolean, g: Float, hueco: Color, filo: Color) {
        for (i in 0 until n) {
            val f = (i + 0.5f) / n
            if (vertical) {
                val x = r.left + r.width * f
                drawLine(filo, Offset(x + g * 0.55f, r.top + g * 0.4f), Offset(x + g * 0.55f, r.bottom + g * 0.4f), g * 0.4f, StrokeCap.Round)
                drawLine(hueco, Offset(x, r.top), Offset(x, r.bottom), g, StrokeCap.Round)
            } else {
                val y = r.top + r.height * f
                drawLine(filo, Offset(r.left + g * 0.3f, y + g * 0.55f), Offset(r.right + g * 0.3f, y + g * 0.55f), g * 0.4f, StrokeCap.Round)
                drawLine(hueco, Offset(r.left, y), Offset(r.right, y), g, StrokeCap.Round)
            }
        }
    }

    /**
     * Una pantalla apagada en su hueco del bisel: el hueco con su sombra arriba y su luz abajo, y el vidrio: sólido, del tono
     * de la superficie, con las líneas de barrido muy tenues, el borde de fósforo y un reflejo. Es el fondo de un nicho.
     */
    private fun DrawScope.pantalla(n: Rect, e: EstadoMueble, u: Float, borde: Color, luz: Color) {
        val r = 1.6f * u
        drawRoundRect(luz, Offset(n.left - r + 0.5f * u, n.top - r + 0.5f * u), Size(n.width + 2 * r, n.height + 2 * r), CornerRadius(2.4f * u))
        drawRoundRect(borde, Offset(n.left - r, n.top - r), Size(n.width + 2 * r - 0.5f * u, n.height + 2 * r - 0.5f * u), CornerRadius(2.4f * u))
        drawRoundRect(NEGRO, Offset(n.left - 0.6f * u, n.top - 0.6f * u), Size(n.width + 1.2f * u, n.height + 1.2f * u), CornerRadius(1.8f * u))
        val forma = Path().apply { addRoundRect(RoundRect(n, CornerRadius(1.4f * u))) }
        drawPath(forma, PANTALLA)
        clipPath(forma) {
            drawRect(Brush.radialGradient(listOf(e.acento.copy(alpha = 0.06f), e.acento.copy(alpha = 0f)), n.center, n.maxDimension * 0.6f), n.topLeft, n.size)
            // Las líneas de barrido.
            var y = n.top + 0.6f * u
            while (y < n.bottom) { drawLine(NEGRO.copy(alpha = 0.32f), Offset(n.left, y), Offset(n.right, y), 0.3f * u); y += 1.15f * u }
            // La viñeta del tubo y el reflejo del vidrio.
            drawRect(Brush.radialGradient(0f to Color.Transparent, 0.72f to Color.Transparent, 1f to NEGRO.copy(alpha = 0.45f), center = n.center, radius = n.maxDimension * 0.62f), n.topLeft, n.size)
            drawPath(Path().apply {
                moveTo(n.left, n.top + n.height * 0.42f); lineTo(n.left + n.width * 0.2f, n.top)
                lineTo(n.left + n.width * 0.28f, n.top); lineTo(n.left, n.top + n.height * 0.6f); close()
            }, Color.White.copy(alpha = 0.022f))
        }
        drawRoundRect(e.acento.copy(alpha = 0.16f), Offset(n.left + 0.9f * u, n.top + 0.9f * u), Size(n.width - 1.8f * u, n.height - 1.8f * u), CornerRadius(1f * u), style = Stroke(0.35f * u))
    }

    /** Un riel del bastidor: la chapa con sus filos y la columna de agujeros cuadrados, de tres en tres. */
    private fun DrawScope.riel(r: Rect, u: Float) {
        pieza(r, GRAFITO, GRAFITO_LUZ, GRAFITO_OSC, 0.45f * u)
        val x = r.center.x
        var y = r.top + 1.6f * u; var k = 0
        while (y < r.bottom - 1f * u && k < 200) {
            drawRect(NEGRO, Offset(x - 0.7f * u, y - 0.7f * u), Size(1.4f * u, 1.4f * u))
            drawLine(GRAFITO_LUZ, Offset(x - 0.7f * u, y + 0.85f * u), Offset(x + 0.7f * u, y + 0.85f * u), 0.25f * u)
            y += if (k % 3 == 2) 3.4f * u else 2.2f * u; k++
        }
    }

    /** La pared de la sala: los paneles con sus juntas. */
    private fun DrawScope.pared(u: Float) {
        drawRect(PARED)
        var x = 0f; var k = 0
        while (x < size.width && k < 40) {
            drawLine(PARED_JUNTA, Offset(x, 0f), Offset(x, size.height), 0.6f * u)
            drawLine(GRAFITO.copy(alpha = 0.35f), Offset(x + 0.6f * u, 0f), Offset(x + 0.6f * u, size.height), 0.3f * u)
            x += 22f * u; k++
        }
    }

    // ---------------------------------------------------------------- Social: el bastidor de la red

    override fun DrawScope.muebleSocial(e: EstadoMueble, oscuro: Boolean) {
        if (size.minDimension < 1f) return
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val n = nicho(MuebleVida.SOCIAL)
        pared(u)
        // El gabinete: el fondo negro y los dos rieles perforados.
        drawRect(GRAFITO_OSC, Offset(w * 0.012f, 0f), Size(w * 0.976f, h))
        riel(Rect(w * 0.012f, 0f, w * 0.07f, h), u)
        riel(Rect(w * 0.93f, 0f, w * 0.988f, h), u)

        // 1. El panel de parcheo: 24 puertos, la mitad con su cable de color que sube y se pierde arriba.
        val pp = Rect(w * 0.035f, h * 0.012f, w * 0.965f, h * 0.135f)
        drawRect(NEGRO.copy(alpha = 0.6f), Offset(pp.left, pp.top + 0.8f * u), pp.size)
        pieza(pp, GRAFITO, GRAFITO_LUZ, GRAFITO_OSC, 0.5f * u)
        tornillo(Offset(pp.left + 2.3f * u, pp.center.y), 1.1f * u)
        tornillo(Offset(pp.right - 2.3f * u, pp.center.y), 1.1f * u)
        val puertos = 24
        val x0 = w * 0.13f; val x1 = w * 0.87f; val paso = (x1 - x0) / puertos
        val py = pp.top + pp.height * 0.3f; val ph = pp.height * 0.4f
        // La tira rotulada bajo los puertos, con una marca cada cuatro.
        val tira = Rect(x0, py + ph + pp.height * 0.1f, x1, py + ph + pp.height * 0.24f)
        drawRect(CARCASA_SOMBRA, tira.topLeft, tira.size)
        for (j in 0..puertos step 4) drawLine(GRAFITO_OSC, Offset(x0 + paso * j, tira.top), Offset(x0 + paso * j, tira.bottom), 0.3f * u)
        for (i in 0 until puertos) {
            val pw = paso * 0.7f; val px = x0 + paso * i + (paso - pw) / 2
            drawRect(NEGRO, Offset(px, py), Size(pw, ph))
            drawLine(ORO.copy(alpha = 0.7f), Offset(px + pw * 0.18f, py + ph * 0.28f), Offset(px + pw * 0.82f, py + ph * 0.28f), 0.3f * u,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.25f * u, 0.25f * u)))
            if (azar(e.semilla, i) < 0.55f) {
                val col = CABLES[(azar(e.semilla + 1, i) * CABLES.size).toInt().coerceIn(0, CABLES.size - 1)]
                val b = Rect(px - pw * 0.1f, py + ph * 0.05f, px + pw * 1.1f, py + ph * 1.08f)
                // El cable: sale de la bota, se dobla y sube hacia los lados.
                val cx = b.center.x
                val dx = (if (i < puertos / 2) -1f else 1f) * (1.5f + 3.5f * azar(e.semilla + 2, i)) * u
                drawPath(Path().apply { moveTo(cx, b.top + 0.4f * u); cubicTo(cx, b.top - 2.2f * u, cx + dx, pp.top + 1.2f * u, cx + dx * 1.3f, -1f) },
                    mez(col, NEGRO, 0.25f), style = Stroke(pw * 0.42f))
                drawPath(Path().apply { moveTo(cx - pw * 0.08f, b.top + 0.4f * u); cubicTo(cx - pw * 0.08f, b.top - 2.2f * u, cx + dx - pw * 0.08f, pp.top + 1.2f * u, cx + dx * 1.3f - pw * 0.08f, -1f) },
                    mez(col, Color.White, 0.25f).copy(alpha = 0.5f), style = Stroke(pw * 0.1f))
                // La bota del conector.
                drawRoundRect(mez(col, NEGRO, 0.4f), b.topLeft, b.size, CornerRadius(0.4f * u))
                drawRect(mez(col, Color.White, 0.15f), b.topLeft, Size(b.width, b.height * 0.25f))
                drawRect(NEGRO.copy(alpha = 0.5f), Offset(b.left + b.width * 0.3f, b.top + b.height * 0.45f), Size(b.width * 0.4f, b.height * 0.35f))
            }
        }

        // 2. El monitor de bastidor: las orejas atornilladas, el bisel beige y la pantalla (el nicho).
        val mon = Rect(w * 0.083f, h * 0.148f, w * 0.917f, h * 0.848f)
        listOf(mon.top + mon.height * 0.06f, mon.bottom - mon.height * 0.2f).forEach { y ->
            listOf(Rect(w * 0.035f, y, mon.left + 1f * u, y + mon.height * 0.14f), Rect(mon.right - 1f * u, y, w * 0.965f, y + mon.height * 0.14f)).forEach { o ->
                pieza(o, CARCASA_SOMBRA, CARCASA, CARCASA_OSC, 0.4f * u)
                tornillo(Offset(if (o.left < w / 2) o.left + 2.3f * u else o.right - 2.3f * u, o.center.y), 1.05f * u)
            }
        }
        drawRoundRect(NEGRO.copy(alpha = 0.6f), Offset(mon.left + 0.6f * u, mon.top + 1.2f * u), mon.size, CornerRadius(2.4f * u))
        pieza(mon, CARCASA, CARCASA_LUZ, CARCASA_SOMBRA, 0.6f * u, 2.4f * u)
        // Las ranuras del bisel de arriba.
        ranuras(Rect(w * 0.4f, mon.top + 1.2f * u, w * 0.6f, n.top - 2.6f * u), 14, true, 0.55f * u, CARCASA_OSC, CARCASA_LUZ)
        pantalla(n, e, u, CARCASA_OSC, CARCASA_LUZ)
        // El bisel de abajo: la placa del modelo con sus franjas, los botones del menú y el encendido con su piloto.
        val by0 = n.bottom + 2.4f * u; val by1 = mon.bottom - 1.6f * u
        val bc = (by0 + by1) / 2; val bh = by1 - by0
        val placa = Rect(n.left + 0.5f * u, bc - bh * 0.32f, n.left + w * 0.09f, bc + bh * 0.32f)
        drawRect(GRAFITO_OSC, placa.topLeft, placa.size)
        listOf(e.acento, AMBAR, PILOTO).forEachIndexed { j, c ->
            drawRect(c.copy(alpha = 0.85f), Offset(placa.left + placa.width * (0.12f + j * 0.12f), placa.top + placa.height * 0.25f), Size(placa.width * 0.08f, placa.height * 0.5f))
        }
        drawLine(CARCASA_LUZ.copy(alpha = 0.6f), Offset(placa.left + placa.width * 0.55f, bc), Offset(placa.right - placa.width * 0.1f, bc), 0.35f * u)
        for (j in 0 until 5) {
            val bw = 3.2f * u
            val bx = w / 2 + (j - 2) * 4.4f * u - bw / 2
            pieza(Rect(bx, bc - bh * 0.22f, bx + bw, bc + bh * 0.22f), CARCASA_LUZ, Color(0xFFC4BDA6), CARCASA_OSC, 0.3f * u, 0.5f * u)
        }
        val enc = Offset(n.right - 3f * u, bc)
        drawCircle(CARCASA_OSC, bh * 0.36f, Offset(enc.x + 0.25f * u, enc.y + 0.3f * u))
        drawCircle(CARCASA_LUZ, bh * 0.32f, enc)
        drawCircle(CARCASA, bh * 0.22f, enc)
        piloto(Offset(enc.x - 4.2f * u, bc), 0.65f * u, PILOTO, true)

        // 3. El switch: la placa beige, un par de luces por puerto y los dos puertos de fibra.
        val sw = Rect(w * 0.035f, h * 0.862f, w * 0.965f, h * 0.985f)
        drawRect(NEGRO.copy(alpha = 0.6f), Offset(sw.left, sw.top + 0.8f * u), sw.size)
        pieza(sw, GRAFITO, GRAFITO_LUZ, GRAFITO_OSC, 0.5f * u)
        tornillo(Offset(sw.left + 2.3f * u, sw.center.y), 1.1f * u)
        tornillo(Offset(sw.right - 2.3f * u, sw.center.y), 1.1f * u)
        val et = Rect(w * 0.1f, sw.top + sw.height * 0.28f, w * 0.19f, sw.bottom - sw.height * 0.28f)
        drawRect(CARCASA_SOMBRA, et.topLeft, et.size)
        drawLine(GRAFITO_OSC, Offset(et.left + et.width * 0.12f, et.center.y), Offset(et.right - et.width * 0.3f, et.center.y), 0.45f * u)
        piloto(Offset(et.right - et.width * 0.14f, et.center.y), 0.5f * u, PILOTO, true)
        val sp = 16; val sx0 = w * 0.23f; val sx1 = w * 0.8f; val spaso = (sx1 - sx0) / sp
        for (i in 0 until sp) {
            val pw = spaso * 0.68f; val px = sx0 + spaso * i + (spaso - pw) / 2
            val pr = Rect(px, sw.top + sw.height * 0.48f, px + pw, sw.bottom - sw.height * 0.2f)
            drawRect(NEGRO, pr.topLeft, pr.size)
            drawRect(GRAFITO_OSC, Offset(pr.left + pw * 0.3f, pr.bottom - pr.height * 0.3f), Size(pw * 0.4f, pr.height * 0.3f))
            if (azar(e.semilla + 7, i) < 0.4f) {
                // Un cable conectado: la bota en el puerto y el cable que baja y sale del lienzo.
                val col = CABLES[(azar(e.semilla + 8, i) * CABLES.size).toInt().coerceIn(0, CABLES.size - 1)]
                drawLine(mez(col, NEGRO, 0.25f), Offset(pr.center.x, pr.bottom), Offset(pr.center.x + (i - sp / 2f) * 0.15f * u, h + 1f), pw * 0.42f)
                val b = Rect(pr.left - pw * 0.08f, pr.top + pr.height * 0.15f, pr.right + pw * 0.08f, pr.bottom + 0.4f * u)
                drawRoundRect(mez(col, NEGRO, 0.4f), b.topLeft, b.size, CornerRadius(0.4f * u))
                drawRect(mez(col, Color.White, 0.15f), b.topLeft, Size(b.width, b.height * 0.25f))
            }
            val ly = sw.top + sw.height * 0.3f
            piloto(Offset(px + pw * 0.25f, ly), 0.42f * u, PILOTO, azar(e.semilla + 5, i) < 0.7f)
            piloto(Offset(px + pw * 0.75f, ly), 0.42f * u, AMBAR, azar(e.semilla + 6, i) < 0.25f)
        }
        listOf(w * 0.83f, w * 0.87f).forEach { x ->
            val fr = Rect(x, sw.top + sw.height * 0.32f, x + w * 0.03f, sw.bottom - sw.height * 0.28f)
            pieza(fr, METAL_OSC, METAL, NEGRO, 0.3f * u)
            drawRect(NEGRO, Offset(fr.left + fr.width * 0.2f, fr.top + fr.height * 0.35f), Size(fr.width * 0.6f, fr.height * 0.3f))
        }
    }

    // ---------------------------------------------------------------- Mapas: la mesa trazadora

    override fun DrawScope.muebleMapas(e: EstadoMueble, oscuro: Boolean) {
        if (size.minDimension < 1f) return
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val n = nicho(MuebleVida.MAPAS)
        pared(u)
        // El piso técnico con sus losetas, y la sombra de la consola.
        val piso = h * 0.94f
        drawRect(LOSETA, Offset(0f, piso), Size(w, h - piso))
        drawLine(e.acento.copy(alpha = 0.25f), Offset(0f, piso), Offset(w, piso), 0.4f * u)
        var lx = 0f; var k = 0
        while (lx < w && k < 30) { drawLine(NEGRO, Offset(lx, piso), Offset(lx - 3f * u, h), 0.4f * u); lx += 18f * u; k++ }
        drawOval(NEGRO.copy(alpha = 0.6f), Offset(w * 0.04f, piso - 1.2f * u), Size(w * 0.92f, 3.6f * u))

        // Las patas, el travesaño y el cable que cuelga entre ellas.
        listOf(w * 0.085f, w * 0.89f).forEach { x ->
            val p = Rect(x, h * 0.86f, x + w * 0.025f, piso)
            pieza(p, GRAFITO, GRAFITO_LUZ, GRAFITO_OSC, 0.4f * u)
            pieza(Rect(p.left - 1.2f * u, piso - 1.4f * u, p.right + 1.2f * u, piso + 0.4f * u), GRAFITO_OSC, GRAFITO_LUZ, NEGRO, 0.3f * u, 0.4f * u)
        }
        drawRect(GRAFITO_OSC, Offset(w * 0.11f, h * 0.905f), Size(w * 0.78f, 1.2f * u))
        drawLine(GRAFITO_LUZ, Offset(w * 0.11f, h * 0.905f), Offset(w * 0.89f, h * 0.905f), 0.3f * u)
        drawPath(Path().apply { moveTo(w * 0.3f, h * 0.87f); cubicTo(w * 0.36f, h * 0.99f, w * 0.52f, h * 0.99f, w * 0.6f, h * 0.88f) }, Color(0xFF2B2F33), style = Stroke(0.9f * u, cap = StrokeCap.Round))
        drawPath(Path().apply { moveTo(w * 0.62f, h * 0.87f); cubicTo(w * 0.66f, h * 0.95f, w * 0.72f, h * 0.96f, w * 0.75f, h * 0.88f) }, CABLES[1].copy(alpha = 0.7f), style = Stroke(0.6f * u, cap = StrokeCap.Round))

        // La consola: el cuerpo beige, la pantalla (el nicho) y el frente inclinado con su teclado.
        val cuerpo = Rect(w * 0.035f, h * 0.06f, w * 0.965f, h * 0.8f)
        drawRoundRect(NEGRO.copy(alpha = 0.6f), Offset(cuerpo.left + 0.6f * u, cuerpo.top + 1.2f * u), cuerpo.size, CornerRadius(2f * u))
        pieza(cuerpo, CARCASA, CARCASA_LUZ, CARCASA_SOMBRA, 0.6f * u, 2f * u)
        pantalla(n, e, u, CARCASA_OSC, CARCASA_LUZ)
        val frente = Path().apply {
            moveTo(w * 0.035f, h * 0.8f); lineTo(w * 0.965f, h * 0.8f); lineTo(w * 0.95f, h * 0.875f); lineTo(w * 0.05f, h * 0.875f); close()
        }
        drawPath(frente, CARCASA_SOMBRA)
        drawLine(CARCASA_LUZ, Offset(w * 0.04f, h * 0.802f), Offset(w * 0.96f, h * 0.802f), 0.4f * u)
        drawLine(CARCASA_OSC, Offset(w * 0.05f, h * 0.875f), Offset(w * 0.95f, h * 0.875f), 0.6f * u)
        // El teclado: dos hileras de teclas con su sombra.
        for (fila in 0 until 2) {
            val ty = h * (0.812f + fila * 0.03f); val th = h * 0.022f
            val nt = 18; val tx0 = w * (0.17f + fila * 0.008f); val tx1 = w * 0.66f
            val tp = (tx1 - tx0) / nt
            for (j in 0 until nt) {
                val tw = if (fila == 1 && j == 7) tp * 4.6f else tp * 0.8f
                if (fila == 1 && j in 8..11) continue
                pieza(Rect(tx0 + tp * j, ty, tx0 + tp * j + tw, ty + th), Color(0xFFB9B29C), Color(0xFFCFC8B2), CARCASA_OSC, 0.25f * u, 0.3f * u)
            }
        }
        // Los interruptores de palanca y el botón rojo.
        listOf(0.72f, 0.76f, 0.8f).forEachIndexed { j, f ->
            val p = Offset(w * f, h * 0.838f)
            drawCircle(METAL_OSC, 1.2f * u, p)
            drawLine(METAL_LUZ, p, Offset(p.x + (if (j == 1) -0.8f else 0.8f) * u, p.y - 2.2f * u), 0.7f * u, StrokeCap.Round)
        }
        drawCircle(NEGRO.copy(alpha = 0.5f), 1.9f * u, Offset(w * 0.86f + 0.3f * u, h * 0.84f + 0.3f * u))
        drawCircle(Brush.radialGradient(listOf(Color(0xFFFF7A6A), ROJO, Color(0xFF8A2018)), Offset(w * 0.86f - 0.5f * u, h * 0.84f - 0.5f * u), 2.4f * u), 1.8f * u, Offset(w * 0.86f, h * 0.84f))
        // Las rejillas y la perilla del costado izquierdo.
        ranuras(Rect(w * 0.047f, h * 0.2f, w * 0.092f, h * 0.42f), 7, false, 0.55f * u, CARCASA_OSC, CARCASA_LUZ)
        val pc = Offset(w * 0.07f, h * 0.58f); val pr = 3.4f * u
        for (j in 0 until 9) {
            val a = Math.toRadians(135.0 + j * 33.75)
            drawLine(CARCASA_OSC, Offset(pc.x + (pr * 1.25f * cos(a)).toFloat(), pc.y + (pr * 1.25f * sin(a)).toFloat()),
                Offset(pc.x + (pr * 1.5f * cos(a)).toFloat(), pc.y + (pr * 1.5f * sin(a)).toFloat()), 0.35f * u)
        }
        drawCircle(NEGRO.copy(alpha = 0.5f), pr, Offset(pc.x + 0.4f * u, pc.y + 0.5f * u))
        drawCircle(Brush.radialGradient(listOf(GRAFITO_LUZ, GRAFITO, GRAFITO_OSC), Offset(pc.x - pr * 0.3f, pc.y - pr * 0.3f), pr * 1.4f), pr, pc)
        drawLine(e.acento, pc, Offset(pc.x + pr * 0.55f, pc.y - pr * 0.55f), 0.5f * u, StrokeCap.Round)
        // El osciloscopio y los dos interruptores del costado derecho.
        val oc = Offset(w * 0.93f, h * 0.3f); val orr = 3.6f * u
        drawCircle(CARCASA_OSC, orr * 1.3f, oc)
        drawCircle(CARCASA_LUZ, orr * 1.3f, Offset(oc.x + 0.3f * u, oc.y + 0.3f * u), style = Stroke(0.3f * u))
        drawCircle(PANTALLA, orr, oc)
        halo(oc, orr, e.acento.copy(alpha = 0.18f))
        drawPath(Path().apply {
            moveTo(oc.x - orr * 0.8f, oc.y)
            cubicTo(oc.x - orr * 0.5f, oc.y - orr * 1.1f, oc.x - orr * 0.1f, oc.y - orr * 1.1f, oc.x, oc.y)
            cubicTo(oc.x + orr * 0.1f, oc.y + orr * 1.1f, oc.x + orr * 0.5f, oc.y + orr * 1.1f, oc.x + orr * 0.8f, oc.y)
        }, e.acento, style = Stroke(0.45f * u, cap = StrokeCap.Round))
        drawLine(e.acento.copy(alpha = 0.25f), Offset(oc.x - orr, oc.y), Offset(oc.x + orr, oc.y), 0.2f * u)
        listOf(h * 0.5f, h * 0.6f).forEachIndexed { j, y ->
            val p = Offset(w * 0.93f, y)
            pieza(Rect(p.x - 1.8f * u, p.y - 1.4f * u, p.x + 1.8f * u, p.y + 1.4f * u), GRAFITO, GRAFITO_LUZ, NEGRO, 0.3f * u, 0.4f * u)
            drawLine(METAL_LUZ, p, Offset(p.x, p.y + (if (j == 0) -2.4f else 2.4f) * u), 0.7f * u, StrokeCap.Round)
            piloto(Offset(p.x, y + 3.6f * u), 0.5f * u, if (j == 0) PILOTO else AMBAR, j == 0)
        }
        // El bisel de abajo: la placa del modelo y la fila de pilotos.
        val bc = (n.bottom + cuerpo.bottom) / 2 + 0.4f * u
        val pl = Rect(n.left, bc - 1.2f * u, n.left + w * 0.11f, bc + 1.2f * u)
        drawRect(GRAFITO_OSC, pl.topLeft, pl.size)
        drawLine(e.acento.copy(alpha = 0.7f), Offset(pl.left + pl.width * 0.1f, bc), Offset(pl.left + pl.width * 0.55f, bc), 0.45f * u)
        for (j in 0 until 6) piloto(Offset(n.right - 2f * u - j * 3f * u, bc), 0.5f * u, if (j == 2) AMBAR else PILOTO, azar(e.semilla + 9, j) < 0.6f)

        // El puente del trazador: el riel con su correa, los dos motores y el carro con tres plumillas.
        val puente = Rect(w * 0.03f, h * 0.035f, w * 0.97f, h * 0.085f)
        drawRect(NEGRO.copy(alpha = 0.5f), Offset(puente.left, puente.top + 1f * u), puente.size)
        pieza(puente, GRAFITO, GRAFITO_LUZ, GRAFITO_OSC, 0.4f * u)
        drawLine(NEGRO, Offset(puente.left + 2f * u, puente.center.y), Offset(puente.right - 2f * u, puente.center.y), 0.8f * u,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.5f * u, 0.4f * u)))
        listOf(Rect(w * 0.012f, h * 0.012f, w * 0.075f, h * 0.12f), Rect(w * 0.925f, h * 0.012f, w * 0.988f, h * 0.12f)).forEach { m ->
            drawRect(NEGRO.copy(alpha = 0.5f), Offset(m.left + 0.5f * u, m.top + 0.8f * u), m.size)
            pieza(m, GRAFITO_OSC, GRAFITO_LUZ, NEGRO, 0.4f * u, 0.5f * u)
            ranuras(Rect(m.left + 1.2f * u, m.top + 1.4f * u, m.right - 1.2f * u, m.bottom - 3.6f * u), 4, false, 0.5f * u, NEGRO, GRAFITO_LUZ)
            drawRect(CARCASA_SOMBRA, Offset(m.left + 1.2f * u, m.bottom - 2.8f * u), Size(m.width - 2.4f * u, 1.4f * u))
        }
        val cx = w * (0.56f + 0.12f * azar(e.semilla, 3))
        val carro = Rect(cx - w * 0.045f, h * 0.02f, cx + w * 0.045f, h * 0.11f)
        drawRect(NEGRO.copy(alpha = 0.5f), Offset(carro.left + 0.5f * u, carro.top + 1f * u), carro.size)
        pieza(carro, CARCASA_LUZ, Color(0xFFC4BDA6), CARCASA_SOMBRA, 0.4f * u, 0.6f * u)
        listOf(e.acento, AMBAR, ROJO).forEachIndexed { j, c ->
            val px = carro.left + carro.width * (0.25f + j * 0.25f)
            drawRect(mez(c, NEGRO, 0.2f), Offset(px - 0.8f * u, carro.top + 1.2f * u), Size(1.6f * u, carro.height * 0.55f))
            drawRect(Color.White.copy(alpha = 0.3f), Offset(px - 0.8f * u, carro.top + 1.2f * u), Size(0.5f * u, carro.height * 0.55f))
            drawPath(Path().apply { moveTo(px - 0.6f * u, carro.bottom - 0.3f * u); lineTo(px + 0.6f * u, carro.bottom - 0.3f * u); lineTo(px, carro.bottom + 1.6f * u); close() }, METAL_LUZ)
        }
    }

    // ---------------------------------------------------------------- Otras vidas: el gabinete de servidores

    override fun DrawScope.muebleOtrasVidas(e: EstadoMueble, oscuro: Boolean) {
        if (size.minDimension < 1f) return
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val n = nicho(MuebleVida.OTRAS_VIDAS)
        pared(u)
        drawRect(LOSETA, Offset(0f, h * 0.965f), Size(w, h * 0.035f))
        // El gabinete: su sombra, la chapa y los rieles de los costados.
        val gab = Rect(w * 0.012f, h * 0.015f, w * 0.988f, h * 0.955f)
        drawRect(NEGRO.copy(alpha = 0.6f), Offset(gab.left + 0.8f * u, gab.top + 1.2f * u), gab.size)
        pieza(gab, GRAFITO, GRAFITO_LUZ, GRAFITO_OSC, 0.6f * u, 1f * u)
        riel(Rect(gab.left + 0.8f * u, n.top - 1.4f * u, n.left - 1.6f * u, n.bottom + 1.4f * u), u)
        riel(Rect(n.right + 1.6f * u, n.top - 1.4f * u, gab.right - 0.8f * u, n.bottom + 1.4f * u), u)

        // La moldura: el filete beige de arriba y el tablero con la pantallita, la rejilla hexagonal, los pilotos y la cerradura.
        val molde = Rect(gab.left, gab.top, gab.right, gab.top + h * 0.035f)
        pieza(molde, CARCASA, CARCASA_LUZ, CARCASA_SOMBRA, 0.4f * u, 1f * u)
        drawLine(CARCASA_OSC, Offset(molde.left + 1f * u, molde.bottom - 0.6f * u), Offset(molde.right - 1f * u, molde.bottom - 0.6f * u), 0.3f * u)
        val tab = Rect(gab.left + 1.2f * u, molde.bottom + 0.8f * u, gab.right - 1.2f * u, n.top - 2.4f * u)
        pieza(tab, GRAFITO_OSC, GRAFITO, NEGRO, 0.4f * u, 0.5f * u)
        val lcd = Rect(w * 0.085f, tab.top + tab.height * 0.2f, w * 0.3f, tab.bottom - tab.height * 0.2f)
        pieza(lcd, PANTALLA, NEGRO, GRAFITO_LUZ, 0.35f * u, 0.4f * u)
        halo(lcd.center, lcd.width * 0.55f, e.acento.copy(alpha = 0.1f))
        listOf(0.7f, 0.45f, 0.85f).forEachIndexed { j, f ->
            val y = lcd.top + lcd.height * (0.3f + j * 0.2f)
            drawLine(e.acento.copy(alpha = 0.85f), Offset(lcd.left + 1.2f * u, y), Offset(lcd.left + 1.2f * u + (lcd.width - 2.4f * u) * f, y), 0.55f * u)
        }
        drawRect(e.acento, Offset(lcd.left + 1.2f * u + (lcd.width - 2.4f * u) * 0.9f, lcd.top + lcd.height * 0.62f), Size(0.9f * u, lcd.height * 0.22f))
        // La rejilla hexagonal: una malla de agujeros en tresbolillo.
        val rej = Rect(w * 0.34f, tab.top + tab.height * 0.22f, w * 0.68f, tab.bottom - tab.height * 0.22f)
        val ag = ArrayList<Offset>()
        var fy = rej.top; var fila = 0
        while (fy <= rej.bottom + 0.1f && fila < 12) {
            var fx = rej.left + if (fila % 2 == 1) 0.9f * u else 0f
            while (fx <= rej.right) { ag.add(Offset(fx, fy)); fx += 1.8f * u }
            fy += 1.55f * u; fila++
        }
        drawPoints(ag, PointMode.Points, NEGRO, 1.1f * u, StrokeCap.Round)
        drawPoints(ag.map { Offset(it.x + 0.3f * u, it.y + 0.4f * u) }, PointMode.Points, GRAFITO.copy(alpha = 0.7f), 0.4f * u, StrokeCap.Round)
        for (j in 0 until 5) piloto(Offset(w * (0.73f + j * 0.03f), tab.center.y), 0.6f * u, if (j == 3) AMBAR else PILOTO, j != 4)
        val ll = Offset(w * 0.9f, tab.center.y)
        drawCircle(NEGRO.copy(alpha = 0.5f), 2.1f * u, Offset(ll.x + 0.3f * u, ll.y + 0.4f * u))
        drawCircle(Brush.radialGradient(listOf(METAL_LUZ, METAL, METAL_OSC), Offset(ll.x - 0.6f * u, ll.y - 0.6f * u), 3f * u), 2f * u, ll)
        drawRect(NEGRO, Offset(ll.x - 0.3f * u, ll.y - 1.2f * u), Size(0.6f * u, 2.4f * u))

        // Las cinco bahías (el nicho): cada una oscura, con su luz de techo y sus pilotos, entre divisiones de grafito.
        drawRect(NEGRO, Offset(n.left - 1.2f * u, n.top - 1.2f * u), Size(n.width + 2.4f * u, n.height + 2.4f * u))
        val cw = n.width / 5
        for (i in 0 until 5) {
            val b = Rect(n.left + cw * i, n.top, n.left + cw * (i + 1), n.bottom)
            drawRect(PANTALLA, b.topLeft, b.size)
            drawRect(Brush.verticalGradient(listOf(e.acento.copy(alpha = 0.1f), e.acento.copy(alpha = 0f)), b.top, b.top + b.height * 0.55f), b.topLeft, Size(b.width, b.height * 0.55f))
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, NEGRO.copy(alpha = 0.5f)), b.bottom - b.height * 0.25f, b.bottom), Offset(b.left, b.bottom - b.height * 0.25f), Size(b.width, b.height * 0.25f))
            // La lámpara de techo: el tubo y su filo.
            drawRect(GRAFITO_OSC, Offset(b.left + b.width * 0.2f, b.top), Size(b.width * 0.6f, 1.1f * u))
            drawLine(TUBO.copy(alpha = 0.85f), Offset(b.left + b.width * 0.24f, b.top + 1.1f * u), Offset(b.right - b.width * 0.24f, b.top + 1.1f * u), 0.45f * u)
            piloto(Offset(b.left + 2.2f * u, b.top + 3f * u), 0.45f * u, PILOTO, true)
            piloto(Offset(b.left + 4f * u, b.top + 3f * u), 0.45f * u, AMBAR, (i + e.semilla) % 3 == 0)
            // La sombra que cae de la división izquierda.
            drawRect(Brush.horizontalGradient(listOf(NEGRO.copy(alpha = 0.45f), Color.Transparent), b.left, b.left + 1.6f * u), b.topLeft, Size(1.6f * u, b.height))
        }
        for (i in 1 until 5) {
            val x = n.left + cw * i
            drawRect(GRAFITO, Offset(x - 0.6f * u, n.top), Size(1.2f * u, n.height))
            drawLine(GRAFITO_LUZ, Offset(x - 0.6f * u, n.top), Offset(x - 0.6f * u, n.bottom), 0.3f * u)
        }
        drawLine(GRAFITO_LUZ, Offset(n.left, n.bottom + 0.6f * u), Offset(n.right, n.bottom + 0.6f * u), 0.4f * u)

        // Abajo: las manijas con su etiqueta, una por bahía; la rejilla y las ruedas.
        val my = n.bottom + (gab.bottom - n.bottom) * 0.32f
        for (i in 0 until 5) {
            val cx = n.left + cw * (i + 0.5f)
            val hw = cw * 0.24f
            drawLine(NEGRO.copy(alpha = 0.6f), Offset(cx - hw, my + 0.6f * u), Offset(cx + hw, my + 0.6f * u), 1.1f * u, StrokeCap.Round)
            drawLine(METAL, Offset(cx - hw, my), Offset(cx + hw, my), 1f * u, StrokeCap.Round)
            drawLine(METAL_LUZ, Offset(cx - hw, my - 0.25f * u), Offset(cx + hw, my - 0.25f * u), 0.3f * u, StrokeCap.Round)
            listOf(cx - hw, cx + hw).forEach { x -> drawRect(METAL_OSC, Offset(x - 0.6f * u, my - 0.9f * u), Size(1.2f * u, 1.8f * u)) }
            drawRect(CARCASA_SOMBRA, Offset(cx - cw * 0.12f, my + 2f * u), Size(cw * 0.24f, 1.6f * u))
            drawLine(GRAFITO_OSC, Offset(cx - cw * 0.08f, my + 2.8f * u), Offset(cx + cw * 0.04f, my + 2.8f * u), 0.35f * u)
        }
        ranuras(Rect(w * 0.12f, gab.bottom - h * 0.05f, w * 0.88f, gab.bottom - h * 0.02f), 2, false, 0.6f * u, NEGRO, GRAFITO_LUZ)
        listOf(w * 0.06f, w * 0.94f).forEach { x ->
            drawRect(GRAFITO_OSC, Offset(x - 1.2f * u, gab.bottom - 0.4f * u), Size(2.4f * u, 1.4f * u))
            drawCircle(NEGRO, 1.5f * u, Offset(x, h * 0.975f))
            drawCircle(METAL_OSC, 0.6f * u, Offset(x, h * 0.975f))
        }
    }

    // ---------------------------------------------------------------- Íconos de los mapas

    override fun DrawScope.iconoMapa(id: String, e: EstadoIcono, oscuro: Boolean) {
        if (size.minDimension < 1f) return
        val k = minOf(size.height / 70f, size.width / 160f)
        val c = Offset(size.width / 2, size.height / 2)
        when (id) {
            "unesco" -> torre(c, k, e)
            "rascar" -> monitorRascar(c, k, e)
            else -> circuito(c, k, e)
        }
    }

    /** La torre de radio: la caseta beige, la celosía con su cara en luz y su cara en sombra, la parabólica y la baliza. */
    private fun DrawScope.torre(c: Offset, k: Float, e: EstadoIcono) {
        val suelo = c.y + 27f * k
        drawOval(NEGRO.copy(alpha = 0.5f), Offset(c.x - 34f * k, suelo - 2f * k), Size(68f * k, 4f * k))
        // La caseta, con su cara de lado.
        val cas = Rect(c.x + 6f * k, suelo - 13f * k, c.x + 24f * k, suelo)
        drawPath(Path().apply { moveTo(cas.right, cas.top); lineTo(cas.right + 6f * k, cas.top - 3f * k); lineTo(cas.right + 6f * k, cas.bottom - 3f * k); lineTo(cas.right, cas.bottom); close() }, CARCASA_SOMBRA)
        drawPath(Path().apply { moveTo(cas.left, cas.top); lineTo(cas.left + 6f * k, cas.top - 3f * k); lineTo(cas.right + 6f * k, cas.top - 3f * k); lineTo(cas.right, cas.top); close() }, CARCASA_LUZ)
        drawRect(CARCASA, cas.topLeft, cas.size)
        drawRect(GRAFITO_OSC, Offset(cas.left + 3f * k, cas.top + 4f * k), Size(5f * k, cas.height - 4f * k))
        ranuras(Rect(cas.left + 11f * k, cas.top + 3.5f * k, cas.right - 2f * k, cas.top + 8f * k), 3, false, 0.9f * k, CARCASA_OSC, CARCASA_LUZ)
        piloto(Offset(cas.right - 3f * k, cas.bottom - 3f * k), 1f * k, PILOTO, true)
        // La celosía.
        val tx = c.x - 12f * k
        val izqB = Offset(tx - 11f * k, suelo); val derB = Offset(tx + 11f * k, suelo)
        val cima = c.y - 13f * k
        val izqA = Offset(tx - 2.6f * k, cima); val derA = Offset(tx + 2.6f * k, cima)
        fun en(a: Offset, b: Offset, f: Float) = Offset(a.x + (b.x - a.x) * f, a.y + (b.y - a.y) * f)
        val tramos = 6
        for (j in 0 until tramos) {
            val f0 = j / tramos.toFloat(); val f1 = (j + 1) / tramos.toFloat()
            drawLine(METAL_OSC, en(izqB, izqA, f0), en(derB, derA, f1), 0.9f * k)
            drawLine(METAL, en(derB, derA, f0), en(izqB, izqA, f1), 0.9f * k)
            drawLine(METAL, en(izqB, izqA, f1), en(derB, derA, f1), 0.8f * k)
        }
        drawLine(METAL_LUZ, izqB, izqA, 1.8f * k, StrokeCap.Round)
        drawLine(METAL_OSC, derB, derA, 1.8f * k, StrokeCap.Round)
        val punta = Offset(tx, c.y - 23f * k)
        drawLine(METAL_LUZ, Offset(tx - 0.5f * k, cima), Offset(tx - 0.5f * k, punta.y), 1.2f * k)
        drawLine(METAL_OSC, Offset(tx + 0.6f * k, cima), Offset(tx + 0.6f * k, punta.y), 0.8f * k)
        // La parabólica, con su volumen y su alimentador.
        val dc = Offset(tx - 9f * k, c.y - 1f * k)
        drawLine(METAL, Offset(tx - 4f * k, dc.y), dc, 1f * k)
        rotate(-28f, dc) {
            drawOval(CARCASA_SOMBRA, Offset(dc.x - 3.6f * k, dc.y - 7.4f * k), Size(7.2f * k, 14.8f * k))
            drawOval(CARCASA_LUZ, Offset(dc.x - 2.6f * k, dc.y - 7f * k), Size(6.4f * k, 14f * k))
            drawOval(CARCASA, Offset(dc.x - 1.4f * k, dc.y - 5.6f * k), Size(4.4f * k, 11.2f * k))
            drawLine(METAL_OSC, Offset(dc.x + 0.6f * k, dc.y), Offset(dc.x - 6f * k, dc.y), 0.7f * k)
            drawCircle(GRAFITO, 1f * k, Offset(dc.x - 6f * k, dc.y))
        }
        // Las ondas y la baliza roja de la punta.
        listOf(4.5f, 8.5f, 12.5f).forEachIndexed { j, r ->
            val a = 1f - j * 0.28f
            drawArc(e.c1.copy(alpha = a), -60f, 50f, false, Offset(punta.x - r * k, punta.y - r * k), Size(2 * r * k, 2 * r * k), style = Stroke(1.3f * k, cap = StrokeCap.Round))
            drawArc(e.c1.copy(alpha = a), 190f, 50f, false, Offset(punta.x - r * k, punta.y - r * k), Size(2 * r * k, 2 * r * k), style = Stroke(1.3f * k, cap = StrokeCap.Round))
        }
        piloto(punta, 1.6f * k, ROJO, true)
        piloto(Offset(tx, c.y + 6f * k), 1f * k, ROJO, true)
        drawLine(e.c2.copy(alpha = 0.7f), Offset(c.x - 36f * k, suelo), Offset(c.x + 36f * k, suelo), 0.8f * k)
    }

    /** El mapa para rascar: el monitor de tubo con su costado, la costa en fósforo y la capa que se rasca, con el cursor. */
    private fun DrawScope.monitorRascar(c: Offset, k: Float, e: EstadoIcono) {
        val m = Rect(c.x - 27f * k, c.y - 28f * k, c.x + 21f * k, c.y + 14f * k)
        drawOval(NEGRO.copy(alpha = 0.5f), Offset(c.x - 26f * k, c.y + 26f * k), Size(54f * k, 5f * k))
        // El pie y la base.
        drawPath(Path().apply { moveTo(c.x - 9f * k, m.bottom); lineTo(c.x + 5f * k, m.bottom); lineTo(c.x + 8f * k, c.y + 23f * k); lineTo(c.x - 12f * k, c.y + 23f * k); close() }, CARCASA_SOMBRA)
        pieza(Rect(c.x - 20f * k, c.y + 22f * k, c.x + 16f * k, c.y + 28f * k), CARCASA, CARCASA_LUZ, CARCASA_SOMBRA, 0.8f * k, 2f * k)
        // El costado y la tapa, para que se vea la caja.
        drawPath(Path().apply { moveTo(m.right, m.top + 2f * k); lineTo(m.right + 8f * k, m.top + 6f * k); lineTo(m.right + 8f * k, m.bottom - 4f * k); lineTo(m.right, m.bottom); close() }, CARCASA_SOMBRA)
        ranuras(Rect(m.right + 2f * k, m.top + 10f * k, m.right + 6f * k, m.top + 26f * k), 5, false, 0.8f * k, CARCASA_OSC, CARCASA)
        pieza(m, CARCASA, CARCASA_LUZ, CARCASA_SOMBRA, 1f * k, 3f * k)
        val v = Rect(m.left + 4f * k, m.top + 4f * k, m.right - 4f * k, m.bottom - 8f * k)
        drawRoundRect(CARCASA_OSC, Offset(v.left - 1.2f * k, v.top - 1.2f * k), Size(v.width + 2.4f * k, v.height + 2.4f * k), CornerRadius(3f * k))
        val forma = Path().apply { addRoundRect(RoundRect(v, CornerRadius(2.4f * k))) }
        drawPath(forma, PANTALLA)
        clipPath(forma) {
            // La costa descubierta.
            val tierra = Path().apply {
                moveTo(v.left, v.top + v.height * 0.25f)
                cubicTo(v.left + v.width * 0.25f, v.top + v.height * 0.1f, v.left + v.width * 0.35f, v.top + v.height * 0.55f, v.left + v.width * 0.55f, v.top + v.height * 0.5f)
                cubicTo(v.left + v.width * 0.7f, v.top + v.height * 0.45f, v.left + v.width * 0.75f, v.top + v.height * 0.85f, v.left + v.width * 0.9f, v.bottom)
                lineTo(v.left, v.bottom); close()
            }
            drawPath(tierra, e.c1.copy(alpha = 0.22f))
            drawPath(tierra, e.c1, style = Stroke(1f * k))
            drawCircle(e.c1, 1.3f * k, Offset(v.left + v.width * 0.3f, v.top + v.height * 0.62f))
            // La capa por rascar, con su trama y su orilla irregular.
            val capa = Path().apply {
                moveTo(v.left + v.width * 0.5f, v.top)
                lineTo(v.left + v.width * 0.47f, v.top + v.height * 0.2f); lineTo(v.left + v.width * 0.56f, v.top + v.height * 0.32f)
                lineTo(v.left + v.width * 0.52f, v.top + v.height * 0.5f); lineTo(v.left + v.width * 0.64f, v.top + v.height * 0.66f)
                lineTo(v.left + v.width * 0.6f, v.top + v.height * 0.84f); lineTo(v.left + v.width * 0.7f, v.bottom)
                lineTo(v.right, v.bottom); lineTo(v.right, v.top); close()
            }
            drawPath(capa, mez(e.c2, GRAFITO, 0.45f))
            clipPath(capa) {
                var x = v.left; var j = 0
                while (x < v.right + v.height && j < 40) { drawLine(mez(e.c2, Color.White, 0.2f).copy(alpha = 0.35f), Offset(x, v.top), Offset(x - v.height, v.bottom), 0.6f * k); x += 2.4f * k; j++ }
            }
            drawRect(Brush.radialGradient(0f to Color.Transparent, 0.7f to Color.Transparent, 1f to NEGRO.copy(alpha = 0.5f), center = v.center, radius = v.maxDimension * 0.62f), v.topLeft, v.size)
        }
        // El cursor de bloque que va borrando.
        drawRect(e.c1, Offset(v.left + v.width * 0.5f, v.top + v.height * 0.36f), Size(3f * k, 5f * k))
        halo(Offset(v.left + v.width * 0.5f + 1.5f * k, v.top + v.height * 0.36f + 2.5f * k), 5f * k, e.c1.copy(alpha = 0.3f))
        // El bisel de abajo con su piloto.
        piloto(Offset(m.right - 5f * k, m.bottom - 4f * k), 1f * k, PILOTO, true)
        ranuras(Rect(m.left + 6f * k, m.bottom - 5.2f * k, m.left + 18f * k, m.bottom - 2.8f * k), 1, false, 0.9f * k, CARCASA_OSC, CARCASA_LUZ)
    }

    /** Tus lugares: la tarjeta de circuito con su canto, sus pistas de oro, el chip y tres LED encendidos como marcadores. */
    private fun DrawScope.circuito(c: Offset, k: Float, e: EstadoIcono) {
        val p = Rect(c.x - 44f * k, c.y - 10f * k, c.x + 44f * k, c.y + 22f * k)
        drawOval(NEGRO.copy(alpha = 0.45f), Offset(p.left, p.bottom), Size(p.width, 5f * k))
        drawRoundRect(PCB_OSC, Offset(p.left, p.top + 2.4f * k), p.size, CornerRadius(2.4f * k))
        drawRoundRect(PCB, p.topLeft, p.size, CornerRadius(2.4f * k))
        drawRoundRect(Color.White.copy(alpha = 0.18f), Offset(p.left + 1.6f * k, p.top + 1.6f * k), Size(p.width - 3.2f * k, p.height - 3.2f * k), CornerRadius(1.6f * k), style = Stroke(0.5f * k))
        listOf(Offset(p.left + 3.5f * k, p.top + 3.5f * k), Offset(p.right - 3.5f * k, p.top + 3.5f * k), Offset(p.left + 3.5f * k, p.bottom - 3.5f * k), Offset(p.right - 3.5f * k, p.bottom - 3.5f * k)).forEach {
            drawCircle(ORO, 1.4f * k, it); drawCircle(PCB_OSC, 0.7f * k, it)
        }
        val leds = listOf(Offset(c.x - 26f * k, c.y + 6f * k), Offset(c.x + 2f * k, c.y - 2f * k), Offset(c.x + 28f * k, c.y + 8f * k))
        // Las pistas con sus quiebres a 45° y sus vías.
        val pista = Path().apply {
            moveTo(leds[0].x, leds[0].y + 2f * k); lineTo(leds[0].x + 8f * k, leds[0].y + 10f * k); lineTo(leds[1].x - 6f * k, leds[0].y + 10f * k)
            lineTo(leds[1].x, leds[1].y + 16f * k); lineTo(leds[1].x, leds[1].y + 2f * k)
            moveTo(leds[1].x + 2f * k, leds[1].y + 8f * k); lineTo(leds[2].x - 8f * k, leds[1].y + 8f * k); lineTo(leds[2].x, leds[2].y + 2f * k)
            moveTo(p.left + 6f * k, c.y - 4f * k); lineTo(c.x - 34f * k, c.y - 4f * k); lineTo(leds[0].x - 2f * k, leds[0].y - 2f * k)
        }
        drawPath(pista, ORO.copy(alpha = 0.85f), style = Stroke(1.1f * k, cap = StrokeCap.Round, join = StrokeJoin.Round))
        listOf(Offset(leds[1].x - 6f * k, leds[0].y + 10f * k), Offset(leds[2].x - 8f * k, leds[1].y + 8f * k), Offset(c.x - 34f * k, c.y - 4f * k)).forEach {
            drawCircle(ORO, 1.5f * k, it); drawCircle(PCB_OSC, 0.6f * k, it)
        }
        // El chip, con sus patas.
        val ch = Rect(c.x + 10f * k, c.y + 10f * k, c.x + 22f * k, c.y + 18f * k)
        for (j in 0 until 5) {
            val x = ch.left + 1.5f * k + j * 2.3f * k
            drawLine(METAL_LUZ, Offset(x, ch.top - 1.2f * k), Offset(x, ch.bottom + 1.2f * k), 0.8f * k)
        }
        pieza(ch, GRAFITO_OSC, GRAFITO_LUZ, NEGRO, 0.5f * k, 0.6f * k)
        drawCircle(GRAFITO_LUZ, 0.7f * k, Offset(ch.left + 1.6f * k, ch.top + 1.6f * k))
        // Los LED: la base, el domo de color con su luz y su reflejo, y el halo.
        listOf(e.c1, e.c2, PILOTO).forEachIndexed { j, col ->
            val b = leds[j]
            val alto = if (j == 1) 15f * k else 12f * k
            halo(Offset(b.x, b.y - alto * 0.6f), 13f * k, col.copy(alpha = 0.35f))
            drawOval(NEGRO.copy(alpha = 0.5f), Offset(b.x - 4.4f * k, b.y - 1f * k), Size(8.8f * k, 3.4f * k))
            drawRoundRect(mez(col, NEGRO, 0.3f), Offset(b.x - 4f * k, b.y - 2.4f * k), Size(8f * k, 2.4f * k), CornerRadius(0.6f * k))
            val domo = Path().apply {
                moveTo(b.x - 3.2f * k, b.y - 2.2f * k); lineTo(b.x - 3.2f * k, b.y - alto + 3.2f * k)
                arcTo(Rect(b.x - 3.2f * k, b.y - alto, b.x + 3.2f * k, b.y - alto + 6.4f * k), 180f, 180f, false)
                lineTo(b.x + 3.2f * k, b.y - 2.2f * k); close()
            }
            drawPath(domo, Brush.horizontalGradient(listOf(mez(col, Color.White, 0.45f), col, mez(col, NEGRO, 0.35f)), b.x - 3.2f * k, b.x + 3.2f * k))
            drawLine(Color.White.copy(alpha = 0.75f), Offset(b.x - 1.6f * k, b.y - alto + 3f * k), Offset(b.x - 1.6f * k, b.y - 5f * k), 0.9f * k, StrokeCap.Round)
        }
    }

    // ---------------------------------------------------------------- Notas: el tablero perforado

    override fun DrawScope.tableroNotas(e: EstadoMueble, oscuro: Boolean) {
        if (size.minDimension < 1f) return
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val n = nicho(MuebleVida.NOTAS)
        pared(u)
        // La lámpara de tubo, colgada de sus cadenas, con su halo sobre el marco.
        halo(Offset(w / 2, h * 0.06f), w * 0.4f, TUBO.copy(alpha = 0.1f))
        val lam = Rect(w * 0.22f, h * 0.012f, w * 0.78f, h * 0.06f)
        listOf(lam.left + lam.width * 0.1f, lam.right - lam.width * 0.1f).forEach { x ->
            drawLine(METAL_OSC, Offset(x, 0f), Offset(x, lam.top), 0.5f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.6f * u, 0.3f * u)))
        }
        drawPath(Path().apply {
            moveTo(lam.left + 2f * u, lam.top); lineTo(lam.right - 2f * u, lam.top); lineTo(lam.right, lam.bottom); lineTo(lam.left, lam.bottom); close()
        }, GRAFITO)
        drawLine(GRAFITO_LUZ, Offset(lam.left + 2f * u, lam.top), Offset(lam.right - 2f * u, lam.top), 0.4f * u)
        drawRect(TUBO.copy(alpha = 0.25f), Offset(lam.left + 1f * u, lam.bottom - 0.4f * u), Size(lam.width - 2f * u, 1.8f * u))
        drawRect(TUBO, Offset(lam.left + 1.6f * u, lam.bottom), Size(lam.width - 3.2f * u, 0.9f * u))
        // El marco de grafito con sus esquineras beige atornilladas.
        val marco = Rect(w * 0.035f, h * 0.085f, w * 0.965f, h * 0.905f)
        drawRect(NEGRO.copy(alpha = 0.6f), Offset(marco.left + 0.8f * u, marco.top + 1.2f * u), marco.size)
        pieza(marco, GRAFITO, GRAFITO_LUZ, GRAFITO_OSC, 0.6f * u, 0.8f * u)
        drawRect(NEGRO, Offset(n.left - 0.8f * u, n.top - 0.8f * u), Size(n.width + 1.6f * u, n.height + 1.6f * u))
        // El tablero (el nicho): la chapa oscura, la luz de la lámpara arriba y los agujeros en una sola llamada.
        drawRect(TABLERO, n.topLeft, n.size)
        drawRect(Brush.verticalGradient(listOf(TUBO.copy(alpha = 0.05f), Color.Transparent), n.top, n.top + n.height * 0.5f), n.topLeft, Size(n.width, n.height * 0.5f))
        val paso = 3.4f * u
        val ag = ArrayList<Offset>()
        var y = n.top + paso * 0.6f
        while (y < n.bottom) { var x = n.left + paso * 0.6f; while (x < n.right) { ag.add(Offset(x, y)); x += paso }; y += paso }
        drawPoints(ag, PointMode.Points, NEGRO.copy(alpha = 0.7f), 0.95f * u, StrokeCap.Round)
        drawPoints(ag.map { Offset(it.x + 0.3f * u, it.y + 0.35f * u) }, PointMode.Points, GRAFITO.copy(alpha = 0.55f), 0.35f * u, StrokeCap.Round)
        drawRect(Brush.verticalGradient(listOf(NEGRO.copy(alpha = 0.5f), Color.Transparent), n.top, n.top + 2f * u), n.topLeft, Size(n.width, 2f * u))
        listOf(marco.topLeft, Offset(marco.right, marco.top), Offset(marco.left, marco.bottom), marco.bottomRight).forEach { p ->
            val sx = if (p.x < w / 2) 1f else -1f; val sy = if (p.y < h / 2) 1f else -1f
            val esq = Path().apply { moveTo(p.x, p.y); lineTo(p.x + sx * 7f * u, p.y); lineTo(p.x, p.y + sy * 7f * u); close() }
            drawPath(esq, CARCASA)
            drawPath(esq, CARCASA_SOMBRA, style = Stroke(0.4f * u))
            tornillo(Offset(p.x + sx * 2.2f * u, p.y + sy * 2.2f * u), 0.9f * u)
        }
        // El cable que baja por el costado derecho, con sus amarres.
        drawPath(Path().apply { moveTo(w * 0.985f, 0f); cubicTo(w * 0.995f, h * 0.3f, w * 0.975f, h * 0.6f, w * 0.99f, h) }, CABLES[0].copy(alpha = 0.8f), style = Stroke(0.9f * u))
        // La repisa beige con su cinta rotulada y sus ménsulas.
        val rep = Rect(w * 0.025f, h * 0.915f, w * 0.975f, h * 0.955f)
        listOf(w * 0.12f, w * 0.88f).forEach { x ->
            drawPath(Path().apply { moveTo(x - 1f * u, rep.bottom); lineTo(x + 1f * u, rep.bottom); lineTo(x + 1f * u, h); lineTo(x - 1f * u, h * 0.985f); close() }, GRAFITO_OSC)
        }
        drawRect(NEGRO.copy(alpha = 0.6f), Offset(rep.left, rep.bottom), Size(rep.width, 1.4f * u))
        pieza(rep, CARCASA, CARCASA_LUZ, CARCASA_SOMBRA, 0.45f * u, 0.4f * u)
        val cinta = Rect(w * 0.08f, rep.top + rep.height * 0.25f, w * 0.26f, rep.bottom - rep.height * 0.25f)
        drawRect(GRAFITO_OSC, cinta.topLeft, cinta.size)
        var cx = cinta.left + 1f * u; var j = 0
        while (cx < cinta.right - 1.5f * u && j < 20) {
            val l = (0.6f + 1.2f * azar(e.semilla + 30, j)) * u
            drawLine(CARCASA_LUZ.copy(alpha = 0.85f), Offset(cx, cinta.center.y), Offset(cx + l, cinta.center.y), 0.45f * u)
            cx += l + 0.7f * u; j++
        }
        for (i in 0 until 3) {
            val d = Rect(w * (0.7f + i * 0.055f), rep.top + rep.height * 0.18f, w * (0.745f + i * 0.055f), rep.bottom - rep.height * 0.18f)
            drawRect(listOf(GRAFITO_OSC, Color(0xFF2B4A6A), Color(0xFF6A2B2B))[i], d.topLeft, d.size)
            drawRect(METAL, Offset(d.left + d.width * 0.3f, d.top), Size(d.width * 0.4f, d.height * 0.5f))
        }
        piloto(Offset(w * 0.95f, rep.center.y), 0.55f * u, PILOTO, true)
    }

    /** El color impreso de la franja de cada tipo de nota. */
    private fun franja(tipo: String): Color = when (tipo) {
        "idea" -> Color(0xFF1F8796)
        "pensamiento" -> Color(0xFF51609C)
        "proposito" -> Color(0xFF3B8449)
        "recordatorio" -> Color(0xFFB0473A)
        else -> Color(0xFF6E747B)
    }

    override fun DrawScope.notaTablero(e: EstadoNota, oscuro: Boolean) {
        val r = e.rect
        if (r.width < 1f || r.height < 1f) return
        val k = minOf(r.width, r.height) / 100f
        // La tarjeta perforada: la esquina de arriba a la izquierda, cortada.
        val f = Rect(r.left + 2f * k, r.top + 4.5f * k, r.right - 2f * k, r.bottom - 2.5f * k)
        val corte = f.width * 0.1f
        fun silueta(dx: Float, dy: Float) = Path().apply {
            moveTo(f.left + corte + dx, f.top + dy); lineTo(f.right + dx, f.top + dy); lineTo(f.right + dx, f.bottom + dy)
            lineTo(f.left + dx, f.bottom + dy); lineTo(f.left + dx, f.top + corte + dy); close()
        }
        val ficha = silueta(0f, 0f)
        if (e.fijada) drawPath(silueta(0f, 0f), e.acento.copy(alpha = 0.35f), style = Stroke(3f * k, join = StrokeJoin.Round))
        drawPath(silueta(1.2f * k, 2.4f * k), NEGRO.copy(alpha = 0.6f))
        drawPath(ficha, FICHA)
        val col = franja(e.tipo)
        clipPath(ficha) {
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, FICHA_SOMBRA.copy(alpha = 0.45f)), f.top, f.bottom), f.topLeft, f.size)
            // La franja del tipo, arriba.
            val fh = f.height * 0.09f
            drawRect(col, f.topLeft, Size(f.width, fh))
            drawLine(mez(col, NEGRO, 0.3f), Offset(f.left, f.top + fh), Offset(f.right, f.top + fh), 0.5f * k)
            // Abajo, los números de columna impresos y las perforaciones (que dejan ver el tablero).
            val nb = f.bottom - 4.2f * k
            drawLine(col.copy(alpha = 0.45f), Offset(f.left + 4f * k, nb), Offset(f.right - 4f * k, nb), 0.9f * k,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.7f * k, 1.6f * k)))
            val cols = 16; val cw = (f.width - 8f * k) / cols
            for (j in 0 until cols) for (fila in 0 until 2) {
                if (azar(e.indice * 31 + fila, j) < 0.42f) {
                    drawRect(TABLERO, Offset(f.left + 4f * k + cw * j + cw * 0.3f, f.bottom - 12.5f * k + fila * 4f * k), Size(cw * 0.42f, 2.6f * k))
                }
            }
        }
        drawPath(ficha, FICHA_SOMBRA, style = Stroke(0.6f * k))
        // El signo del tipo, en claro sobre la franja: «*» idea, «~» pensamiento, «✓» propósito, «!» recordatorio.
        val gc = Offset(f.right - 6f * k, f.top + f.height * 0.045f); val gr = f.height * 0.03f; val g = 0.9f * k
        when (e.tipo) {
            "idea" -> for (j in 0 until 3) {
                val a = Math.toRadians(j * 60.0 + 90.0)
                val dx = (cos(a) * gr).toFloat(); val dy = (sin(a) * gr).toFloat()
                drawLine(FICHA, Offset(gc.x - dx, gc.y - dy), Offset(gc.x + dx, gc.y + dy), g, StrokeCap.Round)
            }
            "pensamiento" -> drawPath(Path().apply {
                moveTo(gc.x - gr * 1.4f, gc.y); cubicTo(gc.x - gr * 0.6f, gc.y - gr * 1.2f, gc.x + gr * 0.6f, gc.y + gr * 1.2f, gc.x + gr * 1.4f, gc.y)
            }, FICHA, style = Stroke(g, cap = StrokeCap.Round))
            "proposito" -> drawPath(Path().apply { moveTo(gc.x - gr, gc.y); lineTo(gc.x - gr * 0.2f, gc.y + gr * 0.8f); lineTo(gc.x + gr * 1.2f, gc.y - gr) }, FICHA, style = Stroke(g, cap = StrokeCap.Round, join = StrokeJoin.Round))
            "recordatorio" -> {
                drawLine(FICHA, Offset(gc.x, gc.y - gr), Offset(gc.x, gc.y + gr * 0.3f), g, StrokeCap.Round)
                drawCircle(FICHA, g * 0.6f, Offset(gc.x, gc.y + gr * 0.95f))
            }
            else -> drawLine(FICHA, Offset(gc.x - gr, gc.y), Offset(gc.x + gr, gc.y), g, StrokeCap.Round)
        }
        // Lo que la sujeta: un imán redondo, o la pinza negra si está fijada.
        val sx = r.center.x; val sy = f.top + 0.5f * k
        if (e.fijada) {
            // La pinza: el cuerpo negro de lomo curvo y sus dos asas de alambre dobladas sobre la tarjeta.
            val pz = Path().apply {
                moveTo(sx - 8f * k, sy - 2.2f * k); lineTo(sx + 8f * k, sy - 2.2f * k); lineTo(sx + 9.5f * k, sy + 5f * k)
                quadraticTo(sx, sy + 6.6f * k, sx - 9.5f * k, sy + 5f * k); close()
            }
            drawPath(Path().apply { addPath(pz, Offset(0.8f * k, 1.4f * k)) }, NEGRO.copy(alpha = 0.55f))
            drawPath(pz, Brush.verticalGradient(listOf(GRAFITO_LUZ, GRAFITO_OSC, NEGRO), sy - 2.2f * k, sy + 6f * k))
            drawLine(Color.White.copy(alpha = 0.35f), Offset(sx - 7f * k, sy - 1.4f * k), Offset(sx + 7f * k, sy - 1.4f * k), 0.5f * k)
            listOf(-1f, 1f).forEach { s ->
                val asa = Path().apply {
                    moveTo(sx + s * 6.5f * k, sy + 3.6f * k); lineTo(sx + s * 4.5f * k, sy + 10.5f * k)
                    lineTo(sx + s * 1.2f * k, sy + 10.5f * k); lineTo(sx + s * 2.2f * k, sy + 3.6f * k)
                }
                drawPath(Path().apply { addPath(asa, Offset(0.5f * k, 0.9f * k)) }, NEGRO.copy(alpha = 0.4f), style = Stroke(0.9f * k, cap = StrokeCap.Round, join = StrokeJoin.Round))
                drawPath(asa, METAL_LUZ, style = Stroke(0.9f * k, cap = StrokeCap.Round, join = StrokeJoin.Round))
            }
        } else {
            val mr = 4f * k
            drawCircle(NEGRO.copy(alpha = 0.55f), mr, Offset(sx + 0.8f * k, sy + 1.4f * k))
            drawCircle(Brush.radialGradient(listOf(mez(col, Color.White, 0.35f), col, mez(col, NEGRO, 0.45f)), Offset(sx - mr * 0.35f, sy - mr * 0.35f), mr * 1.5f), mr, Offset(sx, sy))
            drawCircle(Color.White.copy(alpha = 0.5f), mr * 0.28f, Offset(sx - mr * 0.35f, sy - mr * 0.35f))
        }
    }

    override fun tintaNota(oscuro: Boolean): Color = TINTA
}
