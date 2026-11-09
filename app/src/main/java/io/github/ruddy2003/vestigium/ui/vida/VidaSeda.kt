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
import androidx.compose.ui.graphics.lerp
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

/**
 * Ruta de la Seda: los muebles de Vida y de Diario del caravasar, de la misma casa que su estante del caravasar en el
 * Ideario (el muro de ladrillo con su banda de azulejos turquesa, el gran arco y las cajas de laca de cinabrio), su
 * estantería de rollos de la biblioteca (la laca roja y negra, las nubes de oro, el jade y el papel) y su sala del tesoro
 * del Hall: ladrillo, azulejo turquesa y lapislázuli, laca de cinabrio con filetes de oro, latón, kilims, seda y jade.
 * - Social: el patio del caravasar visto desde arriba: el piso de ladrillo en cestería, el gran kilim con sus orlas (la
 *   guarda de puntos, la cenefa roja de rombos y la guarda ocre en dientes de sierra) y sus flecos, los cojines de seda
 *   alrededor con sus borlas, dos almohadones largos en las cabeceras, la bandeja de latón con la tetera y los cuencos, el
 *   cuenco turquesa de dátiles y orejones y dos lámparas de aceite (de noche, encendidas). El grafo va en el campo del kilim.
 * - Mapas: la mesa baja de laca: el tablero de cinabrio con su orla de laca negra y nubes de oro, el rollo de seda extendido
 *   (el nicho) con su montura de brocado y sus rodillos con perillas de jade; a un lado, el astrolabio de latón, el sello de
 *   jade con su caja de pasta roja y la piedra de tinta con el pincel; al otro, la brújula de cuchara sobre su placa de
 *   bronce, las piezas de seda enrolladas y la sarta de monedas con agujero; el canto de laca negra, el faldón recortado en
 *   nube y las patas de casco.
 * - Otras vidas: el muro del caravasar con cinco hornacinas de arco apuntado (yeso de día, índigo de noche) con su lámpara
 *   de latón colgada, separadas por columnillas de azulejo con capitel y basa de oro; arriba, la banda de azulejo turquesa
 *   con estrellas de ocho puntas; alrededor, el marco de azulejo; abajo, la repisa tallada, el kilim de rombos y el suelo.
 * - Íconos: la madraza con su portada de arco, sus dos alminares y la cúpula turquesa gallonada; el rollo de seda raspado
 *   por una moneda con agujero cuadrado; tres farolillos de papel rojo clavados en el mapa del desierto, unidos por la ruta.
 * - Notas: el biombo de laca: el alero negro con sus tejas redondas, la puerta de luna de celosía al centro, dos farolillos
 *   (de noche, encendidos), el marco de cinabrio con esquinas de oro, el papel de arroz tensado y el zócalo con faldón de
 *   nube; las notas son rollos colgantes con su montura de brocado (lapislázuli, turquesa, cinabrio u ocre según el tipo),
 *   colgados de un clavo de latón o, si están fijados, con un nudo chino rojo y su disco de jade, y con un detalle según el
 *   tipo (el sello rojo, la nube de oro, la moneda o la campanilla).
 */
internal object VidaSeda : DisenoVida {
    // ---------------------------------------------------------------- materiales (los de su Ideario, su biblioteca y su Hall)
    private val LACA = Color(0xFFA8261C)
    private val LACA_LUZ = Color(0xFFC9402E)
    private val LACA_OSC = Color(0xFF6E1610)
    private val NEGRO = Color(0xFF1E1412)
    private val ORO = Color(0xFFD2AE5A)
    private val ORO_LUZ = Color(0xFFF0D898)
    private val ORO_OSC = Color(0xFF8C6A28)
    private val TURQUESA = Color(0xFF1A8C8C)
    private val TURQ_LUZ = Color(0xFF5FC0B8)
    private val TURQ_OSC = Color(0xFF0E5A5C)
    private val LAPIS = Color(0xFF23408E)
    private val LAPIS_LUZ = Color(0xFF4166B8)
    private val LAPIS_OSC = Color(0xFF162A62)
    private val LADRILLO = Color(0xFFC49A6C)
    private val LADRILLO_OSC = Color(0xFF9C7048)
    private val MORTERO = Color(0xFFE6D6B8)
    private val MADERA = Color(0xFF6B4528)
    private val MADERA_LUZ = Color(0xFF8A5E38)
    private val MADERA_VETA = Color(0xFF3E2614)
    private val PAPEL = Color(0xFFF1E6CC)
    private val MARFIL = Color(0xFFEFE6CF)
    private val JADE = Color(0xFF6E9C80)
    private val JADE_LUZ = Color(0xFFA8CDB4)
    private val KILIM_ROJO = Color(0xFF9E2A22)
    private val KILIM_AZUL = Color(0xFF23305E)
    private val KILIM_OCRE = Color(0xFFD39A3A)
    private val KILIM_CREMA = Color(0xFFEFE2C4)
    private val ARENA = Color(0xFFE2C994)
    private val PIEDRA = Color(0xFF7A7670)
    private val LUZ = Color(0xFFF2C46B)
    private val TINTA = Color(0xFF1F1B3A)
    private val NOCHE = Color(0xFF0A0D1C)

    /** El color en oscuro: más hundido, el caravasar bajo las estrellas (como su biblioteca y su Hall). */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, NOCHE, 0.36f) else c

    private fun mezcla(a: Color, b: Color, f: Float): Color = lerp(a, b, f.coerceIn(0f, 1f))

    /** El campo del kilim (el nicho de Social): lana cruda de día; de noche, índigo. */
    private fun campo(oscuro: Boolean) = if (oscuro) Color(0xFF1B2242) else Color(0xFFF7EFDC)

    /** La seda del rollo del mapa: cruda de día; de noche, índigo. */
    private fun sedaRollo(oscuro: Boolean) = if (oscuro) Color(0xFF1E2441) else Color(0xFFF6EDD7)

    /** El yeso de las hornacinas y el papel del biombo: crema de día; de noche, la penumbra índigo. */
    private fun yeso(oscuro: Boolean) = if (oscuro) Color(0xFF18203D) else Color(0xFFF6EEDC)

    override fun area(m: MuebleVida): Rect = when (m) {
        MuebleVida.SOCIAL -> Rect(0.13f, 0.2f, 0.87f, 0.8f)
        MuebleVida.MAPAS -> Rect(0.18f, 0.09f, 0.82f, 0.7f)
        MuebleVida.OTRAS_VIDAS -> Rect(0.075f, 0.2f, 0.925f, 0.8f)
        MuebleVida.NOTAS -> Rect(0.065f, 0.15f, 0.935f, 0.865f)
    }

    private fun DrawScope.nicho(m: MuebleVida): Rect {
        val a = area(m)
        return Rect(size.width * a.left, size.height * a.top, size.width * a.right, size.height * a.bottom)
    }

    // ---------------------------------------------------------------- ayudas de dibujo

    /** Un número estable de 0 a 1 para lo que varía (ladrillos, vetas, estrellas). */
    private fun azar(semilla: Int, k: Int): Float {
        var x = semilla * 374761393 + k * 668265263
        x = (x xor (x ushr 13)) * 1274126177
        x = x xor (x ushr 16)
        return (x and 0x7fffffff) / 2147483647f
    }

    /** Las vetas de la madera en [r], recortadas a la pieza. */
    private fun DrawScope.vetas(r: Rect, color: Color, semilla: Int, densidad: Float = 1f, vertical: Boolean = false, grosor: Float = 1f) =
        clipRect(r.left, r.top, r.right, r.bottom) {
            val d = density
            val largo = if (vertical) r.height else r.width
            val ancho = if (vertical) r.width else r.height
            val n = (ancho / (3.2f * d) * densidad).toInt().coerceIn(2, 60)
            for (k in 0 until n) {
                val base = ancho * (k + azar(semilla, k)) / n
                val amp = (0.6f + 1.8f * azar(semilla, k + 100)) * d * 0.8f
                val fase = azar(semilla, k + 200) * 6.28f
                val ondas = 1.5f + 2.5f * azar(semilla, k + 300)
                val p = Path()
                for (s in 0..18) {
                    val a = largo * s / 18
                    val v = base + amp * sin(fase + ondas * 6.28f * s / 18)
                    val x = if (vertical) r.left + v else r.left + a
                    val y = if (vertical) r.top + a else r.top + v
                    if (s == 0) p.moveTo(x, y) else p.lineTo(x, y)
                }
                drawPath(p, color.copy(alpha = color.alpha * (0.25f + 0.75f * azar(semilla, k + 400))), style = Stroke(grosor * d * (0.5f + azar(semilla, k + 500))))
            }
        }

    /** Una tabla de madera en [r] con su veta. */
    private fun DrawScope.madera(r: Rect, oscuro: Boolean, semilla: Int, vertical: Boolean = false, color: Color = MADERA) {
        drawRect(t(color, oscuro), r.topLeft, r.size)
        vetas(r, t(MADERA_VETA, oscuro).copy(alpha = 0.55f), semilla, densidad = 0.8f, vertical = vertical, grosor = 0.7f)
    }

    /** Una pieza de laca en [r]: el color, el brillo arriba y la sombra abajo (la laca no tiene veta: tiene reflejo). */
    private fun DrawScope.laca(r: Rect, oscuro: Boolean, color: Color = LACA, radio: Float = 0f) {
        drawRoundRect(t(color, oscuro), r.topLeft, r.size, CornerRadius(radio))
        drawRoundRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.16f), Color.Transparent, Color.Black.copy(alpha = 0.22f)), r.top, r.bottom), r.topLeft, r.size, CornerRadius(radio))
    }

    /** Una pieza de oro o latón en [r]: el metal, su luz arriba y su sombra abajo. */
    private fun DrawScope.oro(r: Rect, oscuro: Boolean, radio: Float = 0f) {
        drawRoundRect(t(ORO_OSC, oscuro), r.topLeft, r.size, CornerRadius(radio))
        drawRoundRect(t(ORO, oscuro), r.topLeft, Size(r.width, r.height * 0.78f), CornerRadius(radio))
        drawRoundRect(t(ORO_LUZ, oscuro), Offset(r.left + r.width * 0.04f, r.top + r.height * 0.12f), Size(r.width * 0.92f, r.height * 0.18f), CornerRadius(radio))
    }

    /** Una sombra suave debajo de algo. */
    private fun DrawScope.sombra(r: Rect, alcance: Float, fuerza: Float) {
        for (k in 4 downTo 1) {
            val e = alcance * k / 4f
            drawRoundRect(Color.Black.copy(alpha = fuerza / 4f), Offset(r.left - e * 0.3f, r.top + e * 0.5f), Size(r.width + e * 0.6f, r.height + e * 0.4f), CornerRadius(e))
        }
    }

    /** La sombra de dentro de un hueco. */
    private fun DrawScope.sombraInterior(r: Rect, ancho: Float, fuerza: Float) {
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = fuerza), Color.Transparent), r.top, r.top + ancho), r.topLeft, Size(r.width, ancho))
        drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = fuerza * 0.6f), Color.Transparent), r.left, r.left + ancho * 0.7f), r.topLeft, Size(ancho * 0.7f, r.height))
        drawRect(Brush.horizontalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = fuerza * 0.6f)), r.right - ancho * 0.7f, r.right), Offset(r.right - ancho * 0.7f, r.top), Size(ancho * 0.7f, r.height))
    }

    /** Un halo de luz (las lámparas y los farolillos encendidos). */
    private fun DrawScope.halo(c: Offset, r: Float, color: Color) =
        drawCircle(Brush.radialGradient(listOf(color, color.copy(alpha = 0f)), c, r.coerceAtLeast(1f)), r, c)

    /** Una estrella de [puntas] puntas; [hondo] es el radio de los entrantes, en fracción de [r]. */
    private fun estrella(c: Offset, r: Float, puntas: Int, hondo: Float, giro: Double = -90.0): Path = Path().apply {
        for (k in 0 until puntas * 2) {
            val a = Math.toRadians(giro + k * 180.0 / puntas)
            val rr = if (k % 2 == 0) r else r * hondo
            val x = c.x + rr * cos(a).toFloat(); val y = c.y + rr * sin(a).toFloat()
            if (k == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
    }

    /** Un rombo de semiejes [rx] y [ry]. */
    private fun rombo(c: Offset, rx: Float, ry: Float): Path = Path().apply { moveTo(c.x, c.y - ry); lineTo(c.x + rx, c.y); lineTo(c.x, c.y + ry); lineTo(c.x - rx, c.y); close() }

    /** Una nube de oro (ruyi) tallada, centrada en [c] y de ancho [s]: tres lóbulos y la voluta de abajo. */
    private fun DrawScope.nube(c: Offset, s: Float, color: Color, linea: Color) {
        val lob = listOf(Offset(-0.26f, 0.06f) to 0.2f, Offset(0f, -0.08f) to 0.24f, Offset(0.26f, 0.06f) to 0.2f)
        lob.forEach { (o, r) -> drawCircle(color, s * r, Offset(c.x + o.x * s, c.y + o.y * s)) }
        drawRect(color, Offset(c.x - s * 0.3f, c.y), Size(s * 0.6f, s * 0.2f))
        lob.forEach { (o, r) -> drawCircle(linea, s * r * 0.45f, Offset(c.x + o.x * s, c.y + o.y * s), style = Stroke(s * 0.035f)) }
        drawPath(Path().apply { moveTo(c.x - s * 0.42f, c.y + s * 0.2f); quadraticTo(c.x, c.y + s * 0.34f, c.x + s * 0.42f, c.y + s * 0.2f) }, linea, style = Stroke(s * 0.04f, cap = StrokeCap.Round))
    }

    /** Una moneda de bronce con agujero cuadrado, centrada en [c]: el canto, la cara con sus cuatro signos y el agujero. */
    private fun DrawScope.moneda(c: Offset, r: Float, oscuro: Boolean, fondo: Color) {
        drawCircle(t(ORO_OSC, oscuro), r, c)
        drawCircle(t(ORO, oscuro), r * 0.86f, c)
        drawCircle(t(ORO_OSC, oscuro), r * 0.78f, c, style = Stroke(r * 0.05f))
        drawArc(t(ORO_LUZ, oscuro), 200f, 90f, false, Offset(c.x - r * 0.7f, c.y - r * 0.7f), Size(r * 1.4f, r * 1.4f), style = Stroke(r * 0.08f))
        // Los cuatro signos alrededor del agujero, en relieve.
        listOf(Offset(0f, -0.52f), Offset(0.52f, 0f), Offset(0f, 0.52f), Offset(-0.52f, 0f)).forEachIndexed { i, o ->
            val p = Offset(c.x + o.x * r, c.y + o.y * r)
            val l = r * 0.13f
            drawLine(t(ORO_OSC, oscuro), Offset(p.x - l, p.y - l * (if (i % 2 == 0) 0.6f else 0f)), Offset(p.x + l, p.y - l * (if (i % 2 == 0) 0.6f else 0f)), r * 0.06f)
            drawLine(t(ORO_OSC, oscuro), Offset(p.x, p.y - l), Offset(p.x, p.y + l), r * 0.06f)
            drawLine(t(ORO_OSC, oscuro), Offset(p.x - l, p.y + l * 0.6f), Offset(p.x + l, p.y + l * 0.6f), r * 0.06f)
        }
        val q = r * 0.24f
        drawRect(t(ORO_OSC, oscuro), Offset(c.x - q - r * 0.06f, c.y - q - r * 0.06f), Size(2 * q + r * 0.12f, 2 * q + r * 0.12f))
        drawRect(fondo, Offset(c.x - q, c.y - q), Size(2 * q, 2 * q))
    }

    /** Un farolillo de papel rojo colgado de [p] (el gancho arriba), de alto [a]: las tapas de oro, las costillas y la borla. */
    private fun DrawScope.farolillo(p: Offset, a: Float, oscuro: Boolean, encendido: Boolean = oscuro) {
        val c = Offset(p.x, p.y + a * 0.5f)
        val rx = a * 0.34f; val ry = a * 0.32f
        if (encendido) halo(c, a * 1.4f, LUZ.copy(alpha = 0.4f))
        drawLine(t(ORO_OSC, oscuro), p, Offset(p.x, c.y - ry), a * 0.03f)
        val cuerpo = if (encendido) mezcla(LACA_LUZ, LUZ, 0.25f) else t(LACA, oscuro)
        drawOval(cuerpo, Offset(c.x - rx, c.y - ry), Size(rx * 2, ry * 2))
        for (k in 1 until 4) {
            val f = k / 4f
            drawLine(t(LACA_OSC, oscuro).copy(alpha = 0.7f), Offset(c.x - rx, c.y - ry + ry * 2 * f), Offset(c.x + rx, c.y - ry + ry * 2 * f), a * 0.015f)
        }
        // Las costillas de bambú, en husos.
        listOf(0.35f, 0.72f).forEach { f -> drawOval(t(LACA_OSC, oscuro).copy(alpha = 0.6f), Offset(c.x - rx * f, c.y - ry), Size(rx * 2 * f, ry * 2), style = Stroke(a * 0.015f)) }
        if (encendido) drawOval(LUZ.copy(alpha = 0.5f), Offset(c.x - rx * 0.4f, c.y - ry * 0.6f), Size(rx * 0.8f, ry * 1.2f))
        drawOval(Color.White.copy(alpha = 0.2f), Offset(c.x - rx * 0.7f, c.y - ry * 0.7f), Size(rx * 0.35f, ry * 1f))
        oro(Rect(c.x - rx * 0.5f, c.y - ry - a * 0.08f, c.x + rx * 0.5f, c.y - ry + a * 0.02f), oscuro)
        oro(Rect(c.x - rx * 0.5f, c.y + ry - a * 0.02f, c.x + rx * 0.5f, c.y + ry + a * 0.08f), oscuro)
        // La borla.
        val b = Offset(c.x, c.y + ry + a * 0.08f)
        drawCircle(t(ORO, oscuro), a * 0.04f, Offset(b.x, b.y + a * 0.04f))
        drawPath(Path().apply { moveTo(b.x - a * 0.04f, b.y + a * 0.07f); lineTo(b.x + a * 0.04f, b.y + a * 0.07f); lineTo(b.x + a * 0.08f, b.y + a * 0.3f); lineTo(b.x - a * 0.08f, b.y + a * 0.3f); close() }, t(LACA, oscuro))
    }

    /** Una borla de seda que cuelga de [p], de largo [l]. */
    private fun DrawScope.borla(p: Offset, l: Float, color: Color, oscuro: Boolean) {
        drawLine(t(color, oscuro), p, Offset(p.x, p.y + l * 0.3f), l * 0.08f)
        drawCircle(t(ORO, oscuro), l * 0.12f, Offset(p.x, p.y + l * 0.34f))
        drawPath(Path().apply { moveTo(p.x - l * 0.1f, p.y + l * 0.42f); lineTo(p.x + l * 0.1f, p.y + l * 0.42f); lineTo(p.x + l * 0.18f, p.y + l); lineTo(p.x - l * 0.18f, p.y + l); close() }, t(color, oscuro))
        drawLine(Color.Black.copy(alpha = 0.25f), Offset(p.x + l * 0.04f, p.y + l * 0.45f), Offset(p.x + l * 0.08f, p.y + l * 0.98f), l * 0.04f)
    }

    /** Un muro de ladrillo a soga en [r], con su mortero y ladrillos de tonos variados. */
    private fun DrawScope.ladrillos(r: Rect, oscuro: Boolean, semilla: Int) {
        val u = size.minDimension / 100f
        drawRect(t(MORTERO, oscuro), r.topLeft, r.size)
        clipRect(r.left, r.top, r.right, r.bottom) {
            val al = 3.6f * u; val an = 9f * u; val j = 0.55f * u
            var y = r.top; var f = 0
            while (y < r.bottom) {
                var x = r.left - (if (f % 2 == 0) 0f else an / 2)
                var k = 0
                while (x < r.right) {
                    val col = mezcla(LADRILLO, LADRILLO_OSC, azar(semilla + f, k) * 0.7f)
                    drawRect(t(col, oscuro), Offset(x + j / 2, y + j / 2), Size(an - j, al - j))
                    drawRect(Color.White.copy(alpha = 0.1f), Offset(x + j / 2, y + j / 2), Size(an - j, 0.4f * u))
                    x += an; k++
                }
                y += al; f++
            }
        }
    }

    /** La banda de azulejos turquesa en [r]: estrellas de ocho puntas alternas de lapislázuli y crema, y sus filetes. */
    private fun DrawScope.cenefaAzulejo(r: Rect, oscuro: Boolean) {
        drawRect(t(TURQUESA, oscuro), r.topLeft, r.size)
        val n = max(4, (r.width / (r.height * 1.1f)).toInt())
        val paso = r.width / n
        for (i in 0 until n) {
            val c = Offset(r.left + paso * (i + 0.5f), r.center.y)
            val col = if (i % 2 == 0) LAPIS else KILIM_CREMA
            drawPath(estrella(c, r.height * 0.36f, 8, 0.62f, -90.0), t(mezcla(col, Color.Black, 0.2f), oscuro))
            drawPath(estrella(c, r.height * 0.3f, 8, 0.62f, -90.0), t(col, oscuro))
            drawCircle(t(if (i % 2 == 0) KILIM_CREMA else LAPIS, oscuro), r.height * 0.07f, c)
            // Las crucecitas entre estrella y estrella.
            val x = r.left + paso * (i + 1)
            if (i < n - 1) drawPath(estrella(Offset(x, r.center.y), r.height * 0.16f, 4, 0.4f, -90.0), t(TURQ_LUZ, oscuro))
        }
        drawRect(Color.White.copy(alpha = 0.12f), r.topLeft, Size(r.width, r.height * 0.12f))
        drawRect(t(LAPIS_OSC, oscuro), r.topLeft, Size(r.width, r.height * 0.07f))
        drawRect(t(LAPIS_OSC, oscuro), Offset(r.left, r.bottom - r.height * 0.07f), Size(r.width, r.height * 0.07f))
    }

    /** Una banda de kilim en [r]: el rojo con rombos alternos de lapislázuli y ocre con su centro crema, y sus orillas. */
    private fun DrawScope.bandaKilim(r: Rect, oscuro: Boolean) {
        drawRect(t(KILIM_ROJO, oscuro), r.topLeft, r.size)
        val n = max(4, (r.width / (r.height * 1.25f)).toInt())
        val paso = r.width / n
        for (i in 0 until n) {
            val c = Offset(r.left + paso * (i + 0.5f), r.center.y)
            val col = if (i % 2 == 0) KILIM_AZUL else KILIM_OCRE
            drawPath(rombo(c, r.height * 0.4f, r.height * 0.36f), t(col, oscuro))
            drawPath(rombo(c, r.height * 0.22f, r.height * 0.2f), t(KILIM_CREMA, oscuro))
            drawPath(rombo(c, r.height * 0.08f, r.height * 0.07f), t(col, oscuro))
            if (i < n - 1) {
                val x = r.left + paso * (i + 1)
                drawPath(Path().apply { moveTo(x - r.height * 0.12f, r.top + r.height * 0.12f); lineTo(x, r.top + r.height * 0.3f); lineTo(x + r.height * 0.12f, r.top + r.height * 0.12f); close() }, t(KILIM_CREMA, oscuro))
                drawPath(Path().apply { moveTo(x - r.height * 0.12f, r.bottom - r.height * 0.12f); lineTo(x, r.bottom - r.height * 0.3f); lineTo(x + r.height * 0.12f, r.bottom - r.height * 0.12f); close() }, t(KILIM_CREMA, oscuro))
            }
        }
        drawRect(t(KILIM_CREMA, oscuro), Offset(r.left, r.top + r.height * 0.05f), Size(r.width, r.height * 0.04f))
        drawRect(t(KILIM_CREMA, oscuro), Offset(r.left, r.bottom - r.height * 0.09f), Size(r.width, r.height * 0.04f))
    }

    // ---------------------------------------------------------------- Social: el patio del caravasar con su kilim

    /** Un cojín cuadrado de seda visto desde arriba en [c], de lado [l]: la seda con su rombo de oro, el vivo y las borlas. */
    private fun DrawScope.cojin(c: Offset, l: Float, color: Color, oscuro: Boolean, giro: Float) = rotate(giro, c) {
        val r = Rect(c.x - l / 2, c.y - l / 2, c.x + l / 2, c.y + l / 2)
        sombra(r, l * 0.12f, 0.35f)
        listOf(r.topLeft, Offset(r.right, r.top), Offset(r.left, r.bottom), r.bottomRight).forEach { q ->
            drawCircle(t(ORO, oscuro), l * 0.07f, q)
            drawCircle(t(ORO_OSC, oscuro), l * 0.07f, q, style = Stroke(l * 0.02f))
        }
        drawRoundRect(t(color, oscuro), r.topLeft, r.size, CornerRadius(l * 0.18f))
        drawRoundRect(Brush.radialGradient(listOf(Color.White.copy(alpha = 0.18f), Color.Transparent, Color.Black.copy(alpha = 0.25f)), c, l * 0.72f), r.topLeft, r.size, CornerRadius(l * 0.18f))
        drawPath(rombo(c, l * 0.32f, l * 0.32f), t(ORO, oscuro), style = Stroke(l * 0.04f))
        drawPath(rombo(c, l * 0.16f, l * 0.16f), t(ORO, oscuro))
        drawCircle(t(mezcla(color, Color.Black, 0.3f), oscuro), l * 0.05f, c)
        drawRoundRect(t(ORO, oscuro).copy(alpha = 0.8f), r.deflate(l * 0.07f).topLeft, r.deflate(l * 0.07f).size, CornerRadius(l * 0.13f), style = Stroke(l * 0.02f))
    }

    /** Un almohadón largo (cilindro) visto desde arriba en [r], vertical: la seda con sus bandas de oro y los remates con borla. */
    private fun DrawScope.almohadon(r: Rect, oscuro: Boolean) {
        sombra(r, r.width * 0.2f, 0.4f)
        drawRoundRect(t(LACA, oscuro), r.topLeft, r.size, CornerRadius(r.width * 0.35f))
        drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.25f), Color.White.copy(alpha = 0.18f), Color.Transparent, Color.Black.copy(alpha = 0.35f)), r.left, r.right), Offset(r.left, r.top + r.width * 0.3f), Size(r.width, r.height - r.width * 0.6f))
        val n = 5
        for (k in 1 until n) {
            val y = r.top + r.height * k / n
            drawRect(t(ORO, oscuro), Offset(r.left, y - r.width * 0.08f), Size(r.width, r.width * 0.16f))
            drawRect(t(LAPIS, oscuro), Offset(r.left, y - r.width * 0.03f), Size(r.width, r.width * 0.06f))
        }
        listOf(r.top, r.bottom).forEach { y ->
            drawCircle(t(ORO_OSC, oscuro), r.width * 0.22f, Offset(r.center.x, y))
            drawCircle(t(ORO, oscuro), r.width * 0.16f, Offset(r.center.x, y))
        }
        borla(Offset(r.center.x, r.bottom + r.width * 0.15f), r.width * 0.9f, LACA, oscuro)
        rotate(180f, Offset(r.center.x, r.top)) { borla(Offset(r.center.x, r.top + r.width * 0.15f), r.width * 0.9f, LACA, oscuro) }
    }

    /** La bandeja de latón con la tetera y dos cuencos de té, vista desde arriba, en [c] y de radio [r]. */
    private fun DrawScope.bandejaTe(c: Offset, r: Float, oscuro: Boolean) {
        drawCircle(Color.Black.copy(alpha = 0.3f), r, c + Offset(r * 0.06f, r * 0.1f))
        drawCircle(t(ORO_OSC, oscuro), r, c)
        drawCircle(t(ORO, oscuro), r * 0.92f, c)
        drawCircle(t(ORO_OSC, oscuro), r * 0.78f, c, style = Stroke(r * 0.03f))
        for (k in 0 until 12) { val a = k * Math.PI / 6; drawCircle(t(ORO_OSC, oscuro), r * 0.03f, Offset(c.x + r * 0.85f * cos(a).toFloat(), c.y + r * 0.85f * sin(a).toFloat())) }
        drawArc(t(ORO_LUZ, oscuro), 200f, 80f, false, Offset(c.x - r * 0.88f, c.y - r * 0.88f), Size(r * 1.76f, r * 1.76f), style = Stroke(r * 0.05f))
        // La tetera de cerámica azul con su pico, su asa y la tapa.
        val tc = Offset(c.x - r * 0.15f, c.y - r * 0.1f)
        drawLine(t(LAPIS_OSC, oscuro), tc, Offset(tc.x + r * 0.55f, tc.y - r * 0.3f), r * 0.1f, StrokeCap.Round)
        drawArc(t(LAPIS_OSC, oscuro), 120f, 120f, false, Offset(tc.x - r * 0.55f, tc.y - r * 0.3f), Size(r * 0.6f, r * 0.6f), style = Stroke(r * 0.07f))
        drawCircle(Color.Black.copy(alpha = 0.25f), r * 0.36f, tc + Offset(r * 0.04f, r * 0.06f))
        drawCircle(t(LAPIS, oscuro), r * 0.36f, tc)
        drawCircle(t(KILIM_CREMA, oscuro), r * 0.26f, tc, style = Stroke(r * 0.03f))
        drawCircle(t(LAPIS_LUZ, oscuro), r * 0.16f, tc)
        drawCircle(t(ORO, oscuro), r * 0.06f, tc)
        drawCircle(Color.White.copy(alpha = 0.5f), r * 0.07f, tc - Offset(r * 0.14f, r * 0.14f))
        // Dos cuencos con té.
        listOf(Offset(r * 0.42f, r * 0.32f), Offset(-r * 0.2f, r * 0.5f)).forEach { o ->
            val q = c + o
            drawCircle(Color.Black.copy(alpha = 0.25f), r * 0.17f, q + Offset(r * 0.03f, r * 0.04f))
            drawCircle(t(KILIM_CREMA, oscuro), r * 0.17f, q)
            drawCircle(t(TURQUESA, oscuro), r * 0.17f, q, style = Stroke(r * 0.03f))
            drawCircle(t(Color(0xFF9A5A22), oscuro), r * 0.11f, q)
            drawCircle(Color.White.copy(alpha = 0.4f), r * 0.03f, q - Offset(r * 0.04f, r * 0.04f))
        }
    }

    /** El cuenco de cerámica turquesa con dátiles y orejones, visto desde arriba. */
    private fun DrawScope.cuencoFrutas(c: Offset, r: Float, oscuro: Boolean) {
        drawCircle(Color.Black.copy(alpha = 0.3f), r, c + Offset(r * 0.06f, r * 0.1f))
        drawCircle(t(TURQUESA, oscuro), r, c)
        drawCircle(t(KILIM_CREMA, oscuro), r * 0.86f, c, style = Stroke(r * 0.05f))
        for (k in 0 until 8) { val a = k * Math.PI / 4; drawPath(rombo(Offset(c.x + r * 0.86f * cos(a).toFloat(), c.y + r * 0.86f * sin(a).toFloat()), r * 0.07f, r * 0.07f), t(LAPIS, oscuro)) }
        drawCircle(t(TURQ_OSC, oscuro), r * 0.76f, c)
        for (k in 0 until 9) {
            val a = azar(5, k) * 6.28f; val d = r * 0.5f * azar(6, k)
            val p = Offset(c.x + d * cos(a), c.y + d * sin(a))
            if (k % 3 == 0) {
                drawCircle(t(KILIM_OCRE, oscuro), r * 0.2f, p)
                drawCircle(t(Color(0xFFE8B45A), oscuro), r * 0.12f, p - Offset(r * 0.04f, r * 0.04f))
            } else {
                rotate(a * 57f, p) {
                    drawOval(t(Color(0xFF4A2414), oscuro), Offset(p.x - r * 0.22f, p.y - r * 0.11f), Size(r * 0.44f, r * 0.22f))
                    drawOval(Color.White.copy(alpha = 0.25f), Offset(p.x - r * 0.12f, p.y - r * 0.08f), Size(r * 0.18f, r * 0.06f))
                }
            }
        }
        drawArc(Color.White.copy(alpha = 0.45f), 200f, 70f, false, Offset(c.x - r * 0.92f, c.y - r * 0.92f), Size(r * 1.84f, r * 1.84f), style = Stroke(r * 0.07f))
    }

    /** Una lámpara de aceite de latón vista desde arriba en [c]: el cuerpo, el asa, el pico y la llama (de noche, encendida). */
    private fun DrawScope.lamparaAceite(c: Offset, r: Float, oscuro: Boolean, giro: Float) = rotate(giro, c) {
        val pico = Offset(c.x + r * 1.15f, c.y)
        if (oscuro) halo(pico, r * 4.5f, LUZ.copy(alpha = 0.35f))
        drawCircle(Color.Black.copy(alpha = 0.3f), r, c + Offset(r * 0.08f, r * 0.12f))
        drawPath(Path().apply { moveTo(c.x, c.y - r * 0.45f); lineTo(pico.x, pico.y - r * 0.18f); lineTo(pico.x, pico.y + r * 0.18f); lineTo(c.x, c.y + r * 0.45f); close() }, t(ORO_OSC, oscuro))
        drawCircle(t(ORO_OSC, oscuro), r * 0.4f, Offset(c.x - r * 1.05f, c.y), style = Stroke(r * 0.14f))
        drawCircle(t(ORO_OSC, oscuro), r, c)
        drawCircle(t(ORO, oscuro), r * 0.88f, c)
        drawCircle(t(ORO_OSC, oscuro), r * 0.42f, c)
        drawCircle(t(NEGRO, oscuro), r * 0.3f, c)
        drawArc(t(ORO_LUZ, oscuro), 200f, 80f, false, Offset(c.x - r * 0.8f, c.y - r * 0.8f), Size(r * 1.6f, r * 1.6f), style = Stroke(r * 0.12f))
        if (oscuro) {
            drawCircle(Color(0xFFFFA726), r * 0.3f, pico)
            drawCircle(Color(0xFFFFF3C4), r * 0.16f, pico)
        } else drawCircle(Color.Black.copy(alpha = 0.6f), r * 0.1f, pico)
    }

    override fun DrawScope.muebleSocial(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val n = nicho(MuebleVida.SOCIAL)
        // El piso de ladrillo en cestería: cuadros de dos ladrillos, alternando a lo largo y a lo ancho.
        drawRect(t(MORTERO, oscuro))
        val q = 7.2f * u; val j = 0.5f * u
        var fy = 0; var y = 0f
        while (y < h) {
            var fx = 0; var x = 0f
            while (x < w) {
                for (b in 0 until 2) {
                    val col = t(mezcla(LADRILLO, LADRILLO_OSC, azar(e.semilla + fy * 31 + fx, b) * 0.75f), oscuro)
                    val r = if ((fx + fy) % 2 == 0) Rect(x + j / 2, y + b * q / 2 + j / 2, x + q - j / 2, y + (b + 1) * q / 2 - j / 2)
                    else Rect(x + b * q / 2 + j / 2, y + j / 2, x + (b + 1) * q / 2 - j / 2, y + q - j / 2)
                    drawRect(col, r.topLeft, r.size)
                    drawRect(Color.White.copy(alpha = 0.1f), r.topLeft, Size(r.width, 0.35f * u))
                }
                x += q; fx++
            }
            y += q; fy++
        }
        // El kilim: los flecos, el fondo rojo, la guarda de puntos, la cenefa de rombos y la guarda en dientes de sierra.
        val a = Rect(w * 0.045f, h * 0.05f, w * 0.955f, h * 0.95f)
        sombra(a, 2 * u, if (oscuro) 0.5f else 0.35f)
        listOf(a.left, a.right).forEach { xx ->
            var yy = a.top + 0.6f * u
            val s = if (xx < w / 2) -1f else 1f
            while (yy < a.bottom) { drawLine(t(KILIM_CREMA, oscuro), Offset(xx, yy), Offset(xx + s * (2.2f + 0.5f * azar(e.semilla, (yy / u).toInt())) * u, yy), 0.4f * u); yy += 0.9f * u }
        }
        drawRect(t(KILIM_ROJO, oscuro), a.topLeft, a.size)
        val g1 = a.deflate(1.1f * u)
        drawRect(t(KILIM_AZUL, oscuro), g1.topLeft, g1.size, style = Stroke(2f * u))
        drawRect(t(KILIM_CREMA, oscuro), g1.topLeft, g1.size, style = Stroke(0.6f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.6f * u, 1.4f * u))))
        val campoR = n.inflate(1.6f * u)
        val g3 = campoR.inflate(1.1f * u)
        drawRect(t(KILIM_OCRE, oscuro), g3.topLeft, g3.size, style = Stroke(2.2f * u))
        drawRect(t(KILIM_ROJO, oscuro), g3.topLeft, g3.size, style = Stroke(1.2f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1f * u, 1f * u))))
        // Los rombos de la cenefa, a lo largo de los cuatro lados.
        val ex = Rect(g1.left + 1f * u, g1.top + 1f * u, g1.right - 1f * u, g1.bottom - 1f * u)
        val inn = g3.inflate(1.1f * u)
        val bandaV = (inn.top - ex.top)
        val bandaH = (inn.left - ex.left)
        fun rombos(desde: Offset, hasta: Offset, alto: Float) {
            val largo = (hasta - desde).getDistance()
            val nr = max(2, (largo / (alto * 1.3f)).toInt())
            for (i in 0 until nr) {
                val f = (i + 0.5f) / nr
                val c = Offset(desde.x + (hasta.x - desde.x) * f, desde.y + (hasta.y - desde.y) * f)
                val col = if (i % 2 == 0) KILIM_AZUL else KILIM_OCRE
                val rr = alto * 0.36f
                drawPath(rombo(c, rr, rr), t(col, oscuro))
                drawPath(rombo(c, rr * 0.55f, rr * 0.55f), t(KILIM_CREMA, oscuro))
                drawPath(rombo(c, rr * 0.2f, rr * 0.2f), t(col, oscuro))
            }
        }
        rombos(Offset(inn.left, ex.top + bandaV / 2), Offset(inn.right, ex.top + bandaV / 2), bandaV)
        rombos(Offset(inn.left, inn.bottom + bandaV / 2), Offset(inn.right, inn.bottom + bandaV / 2), bandaV)
        rombos(Offset(ex.left + bandaH / 2, inn.top), Offset(ex.left + bandaH / 2, inn.bottom), bandaH)
        rombos(Offset(inn.right + bandaH / 2, inn.top), Offset(inn.right + bandaH / 2, inn.bottom), bandaH)
        // Las estrellas de ocho puntas en las esquinas de la cenefa.
        listOf(Offset(ex.left + bandaH / 2, ex.top + bandaV / 2), Offset(inn.right + bandaH / 2, ex.top + bandaV / 2), Offset(ex.left + bandaH / 2, inn.bottom + bandaV / 2), Offset(inn.right + bandaH / 2, inn.bottom + bandaV / 2)).forEach { c ->
            val rr = min(bandaH, bandaV) * 0.42f
            drawPath(estrella(c, rr, 8, 0.6f), t(KILIM_CREMA, oscuro))
            drawPath(estrella(c, rr * 0.7f, 8, 0.6f), t(KILIM_AZUL, oscuro))
            drawCircle(t(KILIM_OCRE, oscuro), rr * 0.2f, c)
        }
        // El campo (el nicho): lana lisa con un enrejado de rombos muy suave y su filete.
        drawRect(campo(oscuro), campoR.topLeft, campoR.size)
        clipRect(n.left, n.top, n.right, n.bottom) {
            val tono = if (oscuro) Color.White.copy(alpha = 0.035f) else KILIM_ROJO.copy(alpha = 0.06f)
            var yy = n.top + 6 * u; var f = 0
            while (yy < n.bottom) {
                var xx = n.left + (if (f % 2 == 0) 6f else 14f) * u
                while (xx < n.right) { drawPath(rombo(Offset(xx, yy), 1.4f * u, 2f * u), tono); xx += 16 * u }
                yy += 9 * u; f++
            }
        }
        drawRect(t(KILIM_AZUL, oscuro).copy(alpha = 0.6f), campoR.deflate(0.7f * u).topLeft, campoR.deflate(0.7f * u).size, style = Stroke(0.3f * u))
        // Los cojines de seda alrededor: tres por lado; los almohadones largos en las cabeceras.
        val ya = (a.top + n.top) / 2
        val yb = (a.bottom + n.bottom) / 2
        val lc = min(min(bandaV * 1.25f, (n.top - a.top) * 0.78f), 11f * u)
        val sedas = listOf(LAPIS, TURQUESA, LACA)
        listOf(0.3f, 0.5f, 0.7f).forEachIndexed { i, f ->
            cojin(Offset(w * f, ya), lc, sedas[i], oscuro, -4f + 8f * azar(e.semilla, i))
            cojin(Offset(w * f, yb), lc, sedas[2 - i], oscuro, -4f + 8f * azar(e.semilla, i + 9))
        }
        val aw = min(bandaH * 0.6f, 7f * u)
        listOf((a.left + n.left) / 2, (a.right + n.right) / 2).forEach { xx -> almohadon(Rect(xx - aw / 2, h * 0.33f, xx + aw / 2, h * 0.67f), oscuro) }
        // En las esquinas: la bandeja de té, el cuenco de frutas y dos lámparas de aceite.
        val rc = min(min(n.left - a.left, n.top - a.top) * 0.4f, 5.6f * u)
        val xi = (a.left + n.left) / 2; val xd = (a.right + n.right) / 2
        bandejaTe(Offset(xd, yb), rc, oscuro)
        cuencoFrutas(Offset(xi, ya), rc * 0.85f, oscuro)
        lamparaAceite(Offset(xd - 0.6f * u, ya), rc * 0.55f, oscuro, 200f)
        lamparaAceite(Offset(xi + 0.6f * u, yb), rc * 0.55f, oscuro, 20f)
    }

    // ---------------------------------------------------------------- Mapas: la mesa baja de laca

    /** El astrolabio de latón, colgado de su anilla, centrado en [c] y de radio [r]: la madre, la red con sus punteros y la alidada. */
    private fun DrawScope.astrolabio(c: Offset, r: Float, oscuro: Boolean) {
        drawCircle(Color.Black.copy(alpha = 0.3f), r, c + Offset(r * 0.06f, r * 0.1f))
        // La anilla y el trono de arriba.
        drawCircle(t(ORO_OSC, oscuro), r * 0.18f, Offset(c.x, c.y - r * 1.22f), style = Stroke(r * 0.07f))
        drawPath(Path().apply { moveTo(c.x - r * 0.3f, c.y - r * 0.9f); quadraticTo(c.x, c.y - r * 1.25f, c.x + r * 0.3f, c.y - r * 0.9f); close() }, t(ORO, oscuro))
        drawCircle(t(ORO_OSC, oscuro), r, c)
        drawCircle(t(ORO, oscuro), r * 0.92f, c)
        // La graduación del limbo.
        for (k in 0 until 36) {
            val a = k * Math.PI / 18
            val l = if (k % 3 == 0) 0.14f else 0.07f
            drawLine(t(ORO_OSC, oscuro), Offset(c.x + r * 0.9f * cos(a).toFloat(), c.y + r * 0.9f * sin(a).toFloat()), Offset(c.x + r * (0.9f - l) * cos(a).toFloat(), c.y + r * (0.9f - l) * sin(a).toFloat()), r * 0.025f)
        }
        drawCircle(t(LAPIS_OSC, oscuro), r * 0.7f, c)
        // La red: el círculo de la eclíptica, desplazado, y los punteros de las estrellas.
        val ec = Offset(c.x + r * 0.12f, c.y - r * 0.1f)
        drawCircle(t(ORO, oscuro), r * 0.42f, ec, style = Stroke(r * 0.05f))
        drawCircle(t(ORO, oscuro), r * 0.68f, c, style = Stroke(r * 0.04f))
        for (k in 0 until 6) {
            val a = Math.toRadians(k * 60.0 + 20.0)
            val p = Offset(c.x + r * 0.55f * cos(a).toFloat(), c.y + r * 0.55f * sin(a).toFloat())
            drawLine(t(ORO, oscuro), c, p, r * 0.035f)
            drawPath(Path().apply { moveTo(p.x, p.y); lineTo(p.x - r * 0.06f * cos(a + 1.2).toFloat() * 1.6f, p.y - r * 0.06f * sin(a + 1.2).toFloat() * 1.6f); lineTo(p.x - r * 0.12f * cos(a).toFloat(), p.y - r * 0.12f * sin(a).toFloat()); close() }, t(ORO_LUZ, oscuro))
        }
        // La alidada y el eje.
        rotate(-30f, c) { drawRect(t(ORO_OSC, oscuro), Offset(c.x - r * 0.95f, c.y - r * 0.05f), Size(r * 1.9f, r * 0.1f)) }
        drawCircle(t(ORO_LUZ, oscuro), r * 0.08f, c)
        drawArc(t(ORO_LUZ, oscuro), 200f, 80f, false, Offset(c.x - r * 0.95f, c.y - r * 0.95f), Size(r * 1.9f, r * 1.9f), style = Stroke(r * 0.06f))
    }

    /** La brújula de cuchara (sinan): la placa cuadrada de bronce con sus anillos y la cuchara de piedra imán, en [c]. */
    private fun DrawScope.brujula(c: Offset, s: Float, oscuro: Boolean) {
        val r = Rect(c.x - s / 2, c.y - s / 2, c.x + s / 2, c.y + s / 2)
        sombra(r, s * 0.08f, 0.35f)
        drawRect(t(ORO_OSC, oscuro), r.topLeft, r.size)
        drawRect(t(Color(0xFFB58A40), oscuro), r.deflate(s * 0.04f).topLeft, r.deflate(s * 0.04f).size)
        drawRect(t(ORO_OSC, oscuro), r.deflate(s * 0.1f).topLeft, r.deflate(s * 0.1f).size, style = Stroke(s * 0.015f))
        for (k in 0 until 24) {
            val a = k * Math.PI / 12
            drawLine(t(ORO_OSC, oscuro), Offset(c.x + s * 0.4f * cos(a).toFloat(), c.y + s * 0.4f * sin(a).toFloat()), Offset(c.x + s * 0.33f * cos(a).toFloat(), c.y + s * 0.33f * sin(a).toFloat()), s * 0.015f)
        }
        drawCircle(t(ORO_OSC, oscuro), s * 0.32f, c, style = Stroke(s * 0.02f))
        drawCircle(t(ORO, oscuro), s * 0.26f, c)
        drawCircle(t(ORO_OSC, oscuro), s * 0.26f, c, style = Stroke(s * 0.015f))
        // La cuchara de piedra imán, que apunta al sur.
        rotate(-35f, c) {
            drawLine(t(NEGRO, oscuro), Offset(c.x, c.y), Offset(c.x, c.y + s * 0.34f), s * 0.05f, StrokeCap.Round)
            drawOval(t(NEGRO, oscuro), Offset(c.x - s * 0.1f, c.y - s * 0.17f), Size(s * 0.2f, s * 0.26f))
            drawOval(Color.White.copy(alpha = 0.3f), Offset(c.x - s * 0.06f, c.y - s * 0.13f), Size(s * 0.05f, s * 0.12f))
        }
    }

    /** Las piezas de seda enrolladas, apiladas, de pie en [p] y de ancho [s]. */
    private fun DrawScope.sedas(p: Offset, s: Float, oscuro: Boolean) {
        val col = listOf(LAPIS, TURQUESA, KILIM_OCRE)
        sombra(Rect(p.x - s * 0.5f, p.y - s * 0.08f, p.x + s * 0.5f, p.y), s * 0.06f, 0.35f)
        val al = s * 0.24f
        col.forEachIndexed { i, cc ->
            val y = p.y - al * (i + 1) - i * s * 0.01f
            val dx = (if (i == 1) s * 0.06f else 0f)
            val r = Rect(p.x - s * 0.45f + dx, y, p.x + s * 0.35f + dx, y + al)
            drawRect(t(cc, oscuro), r.topLeft, r.size)
            drawRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.25f), Color.Transparent, Color.Black.copy(alpha = 0.3f)), r.top, r.bottom), r.topLeft, r.size)
            for (k in 1 until 4) drawLine(t(ORO, oscuro).copy(alpha = 0.7f), Offset(r.left + r.width * k / 4, r.top), Offset(r.left + r.width * k / 4, r.bottom), s * 0.012f)
            // El extremo del rollo: la espiral de tela.
            val e = Offset(r.right, r.center.y)
            drawOval(t(mezcla(cc, Color.White, 0.2f), oscuro), Offset(e.x - al * 0.25f, r.top), Size(al * 0.5f, al))
            drawOval(t(mezcla(cc, Color.Black, 0.3f), oscuro), Offset(e.x - al * 0.15f, r.top + al * 0.2f), Size(al * 0.3f, al * 0.6f), style = Stroke(s * 0.012f))
            drawCircle(t(mezcla(cc, Color.Black, 0.4f), oscuro), al * 0.06f, e)
        }
    }

    /** La sarta de monedas en su cordón rojo, de pie en [p] y de ancho [s]. */
    private fun DrawScope.sarta(p: Offset, s: Float, oscuro: Boolean, fondo: Color) {
        sombra(Rect(p.x - s * 0.5f, p.y - s * 0.08f, p.x + s * 0.5f, p.y), s * 0.06f, 0.3f)
        val r = s * 0.17f
        val pts = listOf(Offset(-0.32f, -0.2f), Offset(0f, -0.26f), Offset(0.32f, -0.2f), Offset(-0.16f, -0.52f), Offset(0.16f, -0.52f))
        val cuerda = Path().apply { moveTo(p.x - s * 0.48f, p.y - s * 0.1f); pts.forEach { lineTo(p.x + it.x * s, p.y + it.y * s) } }
        drawPath(cuerda, t(LACA, oscuro), style = Stroke(s * 0.035f))
        pts.forEach { moneda(Offset(p.x + it.x * s, p.y + it.y * s), r, oscuro, fondo) }
        borla(Offset(p.x - s * 0.48f, p.y - s * 0.12f), s * 0.24f, LACA, oscuro)
    }

    /** La piedra de tinta con su pocillo y el pincel apoyado, de pie en [p]. */
    private fun DrawScope.piedraTinta(p: Offset, s: Float, oscuro: Boolean) {
        val r = Rect(p.x - s * 0.42f, p.y - s * 0.3f, p.x + s * 0.42f, p.y)
        sombra(r, s * 0.06f, 0.35f)
        drawRoundRect(t(Color(0xFF2A2A30), oscuro), r.topLeft, r.size, CornerRadius(s * 0.05f))
        drawRoundRect(Color.White.copy(alpha = 0.12f), r.topLeft, Size(r.width, s * 0.05f), CornerRadius(s * 0.05f))
        drawOval(t(Color(0xFF0E0E12), oscuro), Offset(r.left + s * 0.08f, r.top + s * 0.06f), Size(r.width * 0.5f, r.height * 0.55f))
        drawOval(Color.White.copy(alpha = 0.15f), Offset(r.left + s * 0.12f, r.top + s * 0.08f), Size(r.width * 0.2f, r.height * 0.12f))
        // El pincel: el mango de bambú con sus nudos, la virola y la punta negra.
        rotate(-28f, Offset(p.x, r.top)) {
            val a = Offset(p.x - s * 0.55f, r.top - s * 0.05f)
            drawLine(t(Color(0xFFC9AE6E), oscuro), a, Offset(a.x + s * 0.85f, a.y), s * 0.06f, StrokeCap.Round)
            listOf(0.25f, 0.55f).forEach { f -> drawLine(t(Color(0xFF8A6A3A), oscuro), Offset(a.x + s * 0.85f * f, a.y - s * 0.035f), Offset(a.x + s * 0.85f * f, a.y + s * 0.035f), s * 0.02f) }
            drawRect(t(LACA, oscuro), Offset(a.x + s * 0.82f, a.y - s * 0.04f), Size(s * 0.06f, s * 0.08f))
            drawPath(Path().apply { moveTo(a.x + s * 0.88f, a.y - s * 0.04f); quadraticTo(a.x + s * 1f, a.y - s * 0.03f, a.x + s * 1.05f, a.y); quadraticTo(a.x + s * 1f, a.y + s * 0.03f, a.x + s * 0.88f, a.y + s * 0.04f); close() }, t(NEGRO, oscuro))
        }
    }

    /** El sello de jade con su caja de pasta roja, de pie en [p]. */
    private fun DrawScope.sello(p: Offset, s: Float, oscuro: Boolean) {
        sombra(Rect(p.x - s * 0.45f, p.y - s * 0.06f, p.x + s * 0.45f, p.y), s * 0.05f, 0.3f)
        // La cajita redonda de pasta de cinabrio, abierta.
        val cj = Offset(p.x + s * 0.18f, p.y - s * 0.1f)
        drawOval(t(TURQ_OSC, oscuro), Offset(cj.x - s * 0.22f, cj.y - s * 0.05f), Size(s * 0.44f, s * 0.16f))
        drawRect(t(TURQUESA, oscuro), Offset(cj.x - s * 0.22f, cj.y - s * 0.1f), Size(s * 0.44f, s * 0.12f))
        drawOval(t(KILIM_CREMA, oscuro), Offset(cj.x - s * 0.22f, cj.y - s * 0.16f), Size(s * 0.44f, s * 0.12f))
        drawOval(t(LACA, oscuro), Offset(cj.x - s * 0.16f, cj.y - s * 0.14f), Size(s * 0.32f, s * 0.08f))
        // El sello: el bloque de jade con el león tallado arriba.
        val b = Rect(p.x - s * 0.36f, p.y - s * 0.5f, p.x - s * 0.06f, p.y)
        drawRect(t(JADE, oscuro), b.topLeft, b.size)
        drawRect(Color.White.copy(alpha = 0.25f), b.topLeft, Size(b.width * 0.25f, b.height))
        drawRect(Color.Black.copy(alpha = 0.2f), Offset(b.right - b.width * 0.25f, b.top), Size(b.width * 0.25f, b.height))
        drawRect(t(LACA, oscuro), Offset(b.left, b.bottom - s * 0.05f), Size(b.width, s * 0.05f))
        drawCircle(t(JADE_LUZ, oscuro), b.width * 0.36f, Offset(b.center.x, b.top - b.width * 0.15f))
        drawCircle(t(JADE, oscuro), b.width * 0.2f, Offset(b.center.x + b.width * 0.12f, b.top - b.width * 0.3f))
    }

    override fun DrawScope.muebleMapas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val n = nicho(MuebleVida.MAPAS)
        val canto = h * 0.765f
        // La sombra en el suelo y las patas de laca, que se afinan al bajar y acaban en un casco negro vuelto hacia dentro.
        sombra(Rect(w * 0.03f, h * 0.965f, w * 0.97f, h * 0.99f), 3 * u, if (oscuro) 0.5f else 0.25f)
        listOf(w * 0.06f to 1f, w * 0.94f to -1f).forEach { (x, s) ->
            val pw = w * 0.05f
            val y0 = h * 0.86f; val y1 = h * 0.955f
            val p = Path().apply {
                moveTo(x - pw / 2, y0); lineTo(x + pw / 2, y0)
                lineTo(x + pw * 0.36f + s * pw * 0.1f, y1); lineTo(x - pw * 0.36f + s * pw * 0.1f, y1); close()
            }
            drawPath(p, t(LACA, oscuro))
            clipPath(p) { drawRect(Brush.horizontalGradient(listOf(Color.White.copy(alpha = 0.2f), Color.Transparent, Color.Black.copy(alpha = 0.3f)), x - pw / 2, x + pw / 2), Offset(x - pw, y0), Size(pw * 2, y1 - y0)) }
            drawRect(t(ORO, oscuro), Offset(x - pw / 2, y0 + 0.4f * u), Size(pw, 0.5f * u))
            oro(Rect(x - pw * 0.42f + s * pw * 0.1f, y1 - 1.4f * u, x + pw * 0.42f + s * pw * 0.1f, y1 - 0.4f * u), oscuro)
            val casco = Path().apply {
                moveTo(x - pw * 0.4f + s * pw * 0.1f, y1 - 0.5f * u)
                lineTo(x + pw * 0.4f + s * pw * 0.1f, y1 - 0.5f * u)
                quadraticTo(x + s * pw * 0.5f + pw * 0.4f * s, h * 0.995f, x + s * pw * 0.95f, h * 0.993f)
                lineTo(x - s * pw * 0.45f, h * 0.995f)
                close()
            }
            drawPath(casco, t(NEGRO, oscuro))
            drawPath(Path().apply { moveTo(x - pw * 0.3f, y1); quadraticTo(x + s * pw * 0.2f, y1 + 1 * u, x + s * pw * 0.7f, h * 0.985f) }, Color.White.copy(alpha = 0.18f), style = Stroke(0.4f * u))
        }
        // El faldón de laca recortado en nube, con su filete de oro y la nube del centro.
        val fal = Rect(w * 0.02f, h * 0.81f, w * 0.98f, h * 0.875f)
        val fp = Path().apply {
            moveTo(fal.left, fal.top); lineTo(fal.right, fal.top); lineTo(fal.right, fal.bottom)
            val nn = 6
            for (k in nn - 1 downTo 0) {
                val x0 = fal.left + fal.width * (k + 1) / nn; val x1 = fal.left + fal.width * k / nn
                cubicTo(x0 - fal.width * 0.03f, fal.bottom + 2.2f * u, x1 + fal.width * 0.06f, fal.bottom - 1.2f * u, (x0 + x1) / 2, fal.bottom - 0.6f * u)
                cubicTo(x1 + fal.width * 0.04f, fal.bottom + 0.4f * u, x1 + fal.width * 0.02f, fal.bottom + 1.6f * u, x1, fal.bottom)
            }
            close()
        }
        drawPath(fp, t(LACA, oscuro))
        drawPath(fp, Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.25f), Color.Transparent), fal.top, fal.top + 2 * u))
        drawPath(fp, t(ORO, oscuro), style = Stroke(0.4f * u))
        nube(Offset(w / 2, fal.center.y - 0.6f * u), 7 * u, t(ORO, oscuro), t(ORO_OSC, oscuro))
        listOf(0.25f, 0.75f).forEach { f -> drawPath(estrella(Offset(w * f, fal.center.y - 0.4f * u), 1.4f * u, 4, 0.4f, -90.0), t(ORO, oscuro)) }
        // El canto de laca negra con su filete de oro y la banda roja.
        val ct = Rect(w * 0.01f, canto, w * 0.99f, h * 0.81f)
        laca(ct, oscuro, NEGRO)
        drawRect(t(LACA, oscuro), Offset(ct.left, ct.center.y - 0.6f * u), Size(ct.width, 1.2f * u))
        drawRect(t(ORO, oscuro), Offset(ct.left, ct.top + 0.6f * u), Size(ct.width, 0.35f * u))
        drawRect(t(ORO, oscuro), Offset(ct.left, ct.bottom - 1f * u), Size(ct.width, 0.35f * u))
        // El tablero: la orla de laca negra con nubes de oro y el campo de cinabrio con su greca en las esquinas.
        val tab = Rect(w * 0.01f, h * 0.015f, w * 0.99f, canto)
        laca(tab, oscuro, NEGRO)
        val cu = Rect(tab.left + w * 0.03f, tab.top + 3.4f * u, tab.right - w * 0.03f, tab.bottom - 3.4f * u)
        val nx = max(4, (tab.width / (22 * u)).toInt())
        for (k in 0 until nx) {
            val x = tab.left + tab.width * (k + 0.5f) / nx
            nube(Offset(x, (tab.top + cu.top) / 2 + 0.2f * u), 4.2f * u, t(ORO, oscuro).copy(alpha = 0.85f), t(NEGRO, oscuro))
            nube(Offset(x, (tab.bottom + cu.bottom) / 2 + 0.2f * u), 4.2f * u, t(ORO, oscuro).copy(alpha = 0.85f), t(NEGRO, oscuro))
        }
        laca(cu, oscuro, LACA)
        drawRect(t(ORO, oscuro), cu.topLeft, cu.size, style = Stroke(0.45f * u))
        drawRect(t(ORO, oscuro).copy(alpha = 0.7f), cu.deflate(1.4f * u).topLeft, cu.deflate(1.4f * u).size, style = Stroke(0.25f * u))
        listOf(cu.topLeft, Offset(cu.right, cu.top), Offset(cu.left, cu.bottom), cu.bottomRight).forEach { c ->
            val sx = if (c.x < w / 2) 1f else -1f; val sy = if (c.y < h / 2) 1f else -1f
            val o = Offset(c.x + sx * 2.4f * u, c.y + sy * 2.4f * u)
            val g = Path().apply {
                moveTo(o.x, o.y + sy * 4 * u); lineTo(o.x, o.y); lineTo(o.x + sx * 4 * u, o.y); lineTo(o.x + sx * 4 * u, o.y + sy * 2.4f * u)
                lineTo(o.x + sx * 1.6f * u, o.y + sy * 2.4f * u); lineTo(o.x + sx * 1.6f * u, o.y + sy * 1.4f * u)
            }
            drawPath(g, t(ORO, oscuro), style = Stroke(0.45f * u))
        }
        // El rollo de seda: la montura de brocado, los rodillos con perillas de jade y la seda del mapa.
        val br = n.inflate(1.4f * u)
        val mon = Rect(br.left - 3.2f * u, br.top, br.right + 3.2f * u, br.bottom)
        sombra(mon, 2.2f * u, if (oscuro) 0.5f else 0.35f)
        drawRect(t(LAPIS, oscuro), mon.topLeft, mon.size)
        clipRect(mon.left, mon.top, mon.right, mon.bottom) {
            var yy = mon.top + 1.2f * u; var f = 0
            while (yy < mon.bottom) {
                var xx = mon.left + (if (f % 2 == 0) 1f else 2.2f) * u
                while (xx < mon.right) { drawCircle(t(ORO, oscuro).copy(alpha = 0.6f), 0.3f * u, Offset(xx, yy)); xx += 2.4f * u }
                yy += 1.6f * u; f++
            }
        }
        drawRect(t(ORO, oscuro), br.topLeft, br.size, style = Stroke(0.4f * u))
        drawRect(sedaRollo(oscuro), n.inflate(0.9f * u).topLeft, n.inflate(0.9f * u).size)
        drawRect(t(KILIM_OCRE, oscuro).copy(alpha = 0.5f), n.inflate(0.9f * u).topLeft, n.inflate(0.9f * u).size, style = Stroke(0.25f * u))
        listOf(mon.left, mon.right).forEach { x ->
            val rr = Rect(x - 1.4f * u, mon.top - 2.4f * u, x + 1.4f * u, mon.bottom + 2.4f * u)
            madera(rr, oscuro, e.semilla + 3, vertical = true, color = MADERA_LUZ)
            drawRect(Brush.horizontalGradient(listOf(Color.White.copy(alpha = 0.25f), Color.Transparent, Color.Black.copy(alpha = 0.35f)), rr.left, rr.right), rr.topLeft, rr.size)
            listOf(rr.top, rr.bottom).forEach { y ->
                drawCircle(Color.Black.copy(alpha = 0.3f), 1.8f * u, Offset(x + 0.3f * u, y + 0.4f * u))
                drawCircle(t(JADE, oscuro), 1.8f * u, Offset(x, y))
                drawCircle(t(JADE_LUZ, oscuro), 0.7f * u, Offset(x - 0.5f * u, y - 0.5f * u))
            }
        }
        // A un lado, el astrolabio, el sello y la piedra de tinta; al otro, la brújula, las sedas y la sarta de monedas.
        val izq = (cu.left + mon.left - 1.4f * u) / 2
        val der = (cu.right + mon.right + 1.4f * u) / 2
        val col = (mon.left - 1.6f * u - cu.left) * 0.86f
        val s = col.coerceAtMost(13 * u)
        astrolabio(Offset(izq, cu.top + 4.5f * u + s * 0.62f), s * 0.44f, oscuro)
        sello(Offset(izq, cu.top + cu.height * 0.64f), s * 1.15f, oscuro)
        piedraTinta(Offset(izq, cu.bottom - 3 * u), s, oscuro)
        brujula(Offset(der, cu.top + 4 * u + s * 0.42f), s * 0.82f, oscuro)
        sedas(Offset(der, cu.top + cu.height * 0.68f), s * 1.1f, oscuro)
        sarta(Offset(der, cu.bottom - 2.4f * u), s, oscuro, t(LACA, oscuro))
    }

    // ---------------------------------------------------------------- Otras vidas: las hornacinas del caravasar

    /** Una columnilla de azulejo en [r]: el fuste turquesa con su enrejado de rombos, el capitel de mocárabes y la basa de oro. */
    private fun DrawScope.columnaAzulejo(r: Rect, oscuro: Boolean, color: Color = TURQUESA) {
        val w = r.width
        val cap = w * 1.0f
        val fuste = Rect(r.left + w * 0.16f, r.top + cap, r.right - w * 0.16f, r.bottom - cap * 0.6f)
        drawRect(t(color, oscuro), fuste.topLeft, fuste.size)
        clipRect(fuste.left, fuste.top, fuste.right, fuste.bottom) {
            val p = fuste.width
            var y = fuste.top
            var k = 0
            while (y < fuste.bottom) {
                drawPath(rombo(Offset(fuste.center.x, y + p * 0.6f), p * 0.42f, p * 0.6f), t(if (k % 2 == 0) LAPIS else KILIM_CREMA, oscuro).copy(alpha = 0.85f))
                drawPath(rombo(Offset(fuste.center.x, y + p * 0.6f), p * 0.15f, p * 0.22f), t(if (k % 2 == 0) TURQ_LUZ else LAPIS, oscuro))
                y += p * 1.2f; k++
            }
        }
        drawRect(Brush.horizontalGradient(listOf(Color.White.copy(alpha = 0.25f), Color.Transparent, Color.Black.copy(alpha = 0.3f)), fuste.left, fuste.right), fuste.topLeft, fuste.size)
        // El capitel en gradas de mocárabes y el ábaco.
        val gr = 3
        for (k in 0 until gr) {
            val f0 = k / gr.toFloat(); val f1 = (k + 1) / gr.toFloat()
            val y0 = fuste.top - cap * 0.75f * f1; val y1 = fuste.top - cap * 0.75f * f0
            val an = fuste.width + (w - fuste.width) * f1
            oro(Rect(r.center.x - an / 2, y0, r.center.x + an / 2, y1), oscuro)
            drawLine(t(ORO_OSC, oscuro), Offset(r.center.x, y0), Offset(r.center.x, y1), w * 0.04f)
        }
        oro(Rect(r.left - w * 0.06f, r.top, r.right + w * 0.06f, r.top + cap * 0.25f), oscuro)
        oro(Rect(fuste.left - w * 0.08f, fuste.bottom, fuste.right + w * 0.08f, fuste.bottom + cap * 0.25f), oscuro, cap * 0.1f)
        oro(Rect(r.left, fuste.bottom + cap * 0.25f, r.right, r.bottom), oscuro)
    }

    /** El arco apuntado de una hornacina en [c] (el compartimento), con el arranque a [arranque] de su alto. */
    private fun arcoApuntado(c: Rect, r: Float, arriba: Float): Path = Path().apply {
        val cx = c.center.x
        val ys = arriba + r * 1.1f
        moveTo(cx - r, c.bottom); lineTo(cx - r, ys)
        cubicTo(cx - r, ys - r * 0.7f, cx - r * 0.25f, arriba + r * 0.25f, cx, arriba)
        cubicTo(cx + r * 0.25f, arriba + r * 0.25f, cx + r, ys - r * 0.7f, cx + r, ys)
        lineTo(cx + r, c.bottom); close()
    }

    /** Una lámpara colgante de latón calado, colgada de [p] con su cadena de largo [l]. */
    private fun DrawScope.lamparaColgante(p: Offset, l: Float, s: Float, oscuro: Boolean) {
        drawLine(t(ORO_OSC, oscuro), p, Offset(p.x, p.y + l), s * 0.06f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(s * 0.12f, s * 0.06f)))
        val c = Offset(p.x, p.y + l + s * 0.3f)
        if (oscuro) halo(c, s * 2.6f, LUZ.copy(alpha = 0.35f))
        drawPath(Path().apply { moveTo(c.x - s * 0.5f, c.y - s * 0.2f); lineTo(c.x + s * 0.5f, c.y - s * 0.2f); quadraticTo(c.x + s * 0.5f, c.y + s * 0.5f, c.x, c.y + s * 0.6f); quadraticTo(c.x - s * 0.5f, c.y + s * 0.5f, c.x - s * 0.5f, c.y - s * 0.2f); close() }, t(ORO, oscuro))
        drawRect(t(ORO_OSC, oscuro), Offset(c.x - s * 0.55f, c.y - s * 0.28f), Size(s * 1.1f, s * 0.12f))
        for (k in -1..1) drawCircle(if (oscuro) LUZ else t(ORO_OSC, oscuro), s * 0.07f, Offset(c.x + k * s * 0.22f, c.y + s * 0.05f))
        drawCircle(t(ORO_OSC, oscuro), s * 0.08f, Offset(c.x, c.y + s * 0.66f))
    }

    override fun DrawScope.muebleOtrasVidas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val n = nicho(MuebleVida.OTRAS_VIDAS)
        // El muro de ladrillo y el suelo de losas.
        ladrillos(Rect(0f, 0f, w, h * 0.95f), oscuro, e.semilla)
        val suelo = Rect(0f, h * 0.95f, w, h)
        drawRect(t(PIEDRA, oscuro), suelo.topLeft, suelo.size)
        var x = 0f
        while (x < w) { drawLine(t(mezcla(PIEDRA, Color.Black, 0.3f), oscuro), Offset(x, suelo.top), Offset(x, suelo.bottom), 0.35f * u); x += 12 * u }
        drawRect(Color.Black.copy(alpha = 0.25f), suelo.topLeft, Size(w, 0.8f * u))
        // La banda de azulejo turquesa con sus estrellas, arriba.
        val ban = Rect(0f, h * 0.025f, w, h * 0.135f)
        drawRect(t(LAPIS_OSC, oscuro), Rect(0f, 0f, w, ban.top).topLeft, Size(w, ban.top))
        cenefaAzulejo(ban, oscuro)
        drawRect(t(ORO, oscuro), Offset(0f, ban.bottom), Size(w, 0.45f * u))
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(0f, ban.bottom + 0.45f * u), Size(w, 0.8f * u))
        // El marco de azulejo turquesa alrededor de las hornacinas, con su filete de lapislázuli y oro.
        val mar = n.inflate(3.2f * u)
        sombra(mar, 2 * u, if (oscuro) 0.4f else 0.25f)
        drawRect(t(TURQUESA, oscuro), mar.topLeft, mar.size)
        clipRect(mar.left, mar.top, mar.right, mar.bottom) {
            var xx = mar.left + 1.6f * u
            while (xx < mar.right) {
                drawPath(rombo(Offset(xx, mar.top + 1.6f * u), 0.9f * u, 0.9f * u), t(TURQ_LUZ, oscuro))
                drawPath(rombo(Offset(xx, mar.bottom - 1.6f * u), 0.9f * u, 0.9f * u), t(TURQ_LUZ, oscuro))
                xx += 3.2f * u
            }
            var yy = mar.top + 4.8f * u
            while (yy < mar.bottom - 2 * u) {
                drawPath(rombo(Offset(mar.left + 1.6f * u, yy), 0.9f * u, 0.9f * u), t(TURQ_LUZ, oscuro))
                drawPath(rombo(Offset(mar.right - 1.6f * u, yy), 0.9f * u, 0.9f * u), t(TURQ_LUZ, oscuro))
                yy += 3.2f * u
            }
        }
        drawRect(t(LAPIS_OSC, oscuro), mar.topLeft, mar.size, style = Stroke(0.5f * u))
        drawRect(t(ORO, oscuro), n.inflate(0.4f * u).topLeft, n.inflate(0.4f * u).size, style = Stroke(0.6f * u))
        // Las hornacinas: el yeso, el arco apuntado en sombra con su filete de azulejo y la lámpara colgada.
        drawRect(yeso(oscuro), n.topLeft, n.size)
        val paso = n.width / 5
        for (k in 0 until 5) {
            val c = Rect(n.left + paso * k, n.top, n.left + paso * (k + 1), n.bottom)
            val r = c.width * 0.4f
            val arco = arcoApuntado(c, r, c.top + 2 * u)
            drawPath(arco, if (oscuro) Color.Black.copy(alpha = 0.2f) else ORO_OSC.copy(alpha = 0.08f))
            drawPath(arco, Brush.verticalGradient(listOf(if (oscuro) Color.Black.copy(alpha = 0.25f) else ORO_OSC.copy(alpha = 0.1f), Color.Transparent), c.top, c.top + r * 1.6f))
            drawPath(arco, t(TURQUESA, oscuro).copy(alpha = if (oscuro) 0.6f else 0.7f), style = Stroke(0.6f * u))
            drawPath(arco, t(ORO, oscuro).copy(alpha = 0.5f), style = Stroke(0.2f * u))
            lamparaColgante(Offset(c.center.x, c.top + 2 * u), 2.4f * u, 3.4f * u, oscuro)
        }
        sombraInterior(n, 4 * u, if (oscuro) 0.35f else 0.14f)
        // La repisa tallada de cada hornacina.
        val rep = Rect(n.left, n.bottom - 2.2f * u, n.right, n.bottom)
        madera(rep, oscuro, e.semilla + 4, color = MADERA_LUZ)
        drawRect(t(ORO, oscuro), rep.topLeft, Size(rep.width, 0.4f * u))
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(rep.left, rep.bottom - 0.5f * u), Size(rep.width, 0.5f * u))
        for (k in 1 until 5) {
            val xx = n.left + paso * k
            columnaAzulejo(Rect(xx - 1.8f * u, n.top, xx + 1.8f * u, n.bottom), oscuro)
        }
        // Las columnas grandes a los lados, de azulejo de lapislázuli.
        val cw = w * 0.042f
        listOf(mar.left - cw * 0.7f, mar.right - cw * 0.3f).forEach { xx -> columnaAzulejo(Rect(xx, mar.top - 1.6f * u, xx + cw, mar.bottom + 1.6f * u), oscuro, LAPIS) }
        // Abajo: el kilim de rombos sobre el suelo.
        val ki = Rect(w * 0.02f, mar.bottom + 1.8f * u, w * 0.98f, h * 0.955f)
        sombra(ki, 1.2f * u, 0.3f)
        bandaKilim(ki, oscuro)
        drawRect(t(KILIM_ROJO, oscuro), ki.topLeft, ki.size, style = Stroke(0.4f * u))
    }

    // ---------------------------------------------------------------- Íconos de mapas

    override fun DrawScope.iconoMapa(id: String, e: EstadoIcono, oscuro: Boolean) {
        val w = size.width; val h = size.height
        val s = h / 70f * 60f
        val c = Offset(w / 2, h / 2)
        val u = s / 60f
        when (id) {
            "unesco" -> {
                // La madraza: la cúpula gallonada turquesa en su tambor, los dos alminares y la portada con su gran arco.
                val base = c.y + s * 0.46f
                drawOval(Color.Black.copy(alpha = 0.22f), Offset(c.x - s * 0.95f, base - 2 * u), Size(s * 1.9f, 4 * u))
                val dc = Offset(c.x, c.y - s * 0.1f)
                val dr = s * 0.26f
                drawRect(t(ARENA, oscuro), Offset(dc.x - dr * 0.8f, dc.y), Size(dr * 1.6f, s * 0.2f))
                drawRect(e.c1, Offset(dc.x - dr * 0.8f, dc.y + s * 0.05f), Size(dr * 1.6f, s * 0.05f))
                val cup = Path().apply { moveTo(dc.x - dr, dc.y); cubicTo(dc.x - dr * 1.1f, dc.y - dr * 1.1f, dc.x - dr * 0.3f, dc.y - dr * 1.3f, dc.x, dc.y - dr * 1.45f); cubicTo(dc.x + dr * 0.3f, dc.y - dr * 1.3f, dc.x + dr * 1.1f, dc.y - dr * 1.1f, dc.x + dr, dc.y); close() }
                drawPath(cup, t(TURQUESA, oscuro))
                clipPath(cup) {
                    for (k in -3..3) drawPath(Path().apply { moveTo(dc.x + k * dr * 0.3f, dc.y); quadraticTo(dc.x + k * dr * 0.22f, dc.y - dr * 0.9f, dc.x, dc.y - dr * 1.45f) }, t(TURQ_OSC, oscuro), style = Stroke(0.6f * u))
                    drawRect(Color.Black.copy(alpha = 0.2f), Offset(dc.x + dr * 0.3f, dc.y - dr * 1.5f), Size(dr, dr * 1.5f))
                    drawRect(Color.White.copy(alpha = 0.18f), Offset(dc.x - dr, dc.y - dr * 1.5f), Size(dr * 0.4f, dr * 1.5f))
                }
                drawLine(t(ORO, oscuro), Offset(dc.x, dc.y - dr * 1.45f), Offset(dc.x, dc.y - dr * 1.8f), 0.7f * u)
                drawCircle(t(ORO, oscuro), 0.9f * u, Offset(dc.x, dc.y - dr * 1.8f))
                // Los alminares, con sus bandas de azulejo y su linterna.
                listOf(-1f, 1f).forEach { k ->
                    val mx = c.x + k * s * 0.72f
                    val mw = s * 0.11f
                    val mt = c.y - s * 0.42f
                    drawRect(t(ARENA, oscuro), Offset(mx - mw / 2, mt), Size(mw, base - mt))
                    drawRect(Color.Black.copy(alpha = 0.18f), Offset(mx + mw * 0.15f, mt), Size(mw * 0.35f, base - mt))
                    listOf(0.15f, 0.45f, 0.75f).forEach { f -> drawRect(e.c1, Offset(mx - mw / 2, mt + (base - mt) * f), Size(mw, 1.6f * u)) }
                    drawRect(t(TURQUESA, oscuro), Offset(mx - mw * 0.7f, mt - 1.6f * u), Size(mw * 1.4f, 1.6f * u))
                    drawPath(Path().apply { moveTo(mx - mw * 0.5f, mt - 1.6f * u); quadraticTo(mx, mt - 6 * u, mx + mw * 0.5f, mt - 1.6f * u); close() }, t(TURQUESA, oscuro))
                }
                // La portada (pishtaq): el rectángulo con su orla de azulejo y el gran arco apuntado.
                val po = Rect(c.x - s * 0.36f, c.y - s * 0.2f, c.x + s * 0.36f, base)
                drawRect(t(ARENA, oscuro), po.topLeft, po.size)
                drawRect(e.c1, po.topLeft, po.size, style = Stroke(1.6f * u))
                drawRect(t(TURQUESA, oscuro), po.deflate(1.6f * u).topLeft, po.deflate(1.6f * u).size, style = Stroke(0.7f * u))
                val ar = Rect(c.x - s * 0.17f, po.top + 4 * u, c.x + s * 0.17f, base)
                val arco = arcoApuntado(ar, ar.width / 2, ar.top)
                drawPath(arco, e.c1)
                drawPath(arco, Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.35f), Color.Transparent), ar.top, ar.bottom))
                drawPath(arco, t(ORO, oscuro), style = Stroke(0.6f * u))
                listOf(-1f, 1f).forEach { k -> drawPath(estrella(Offset(c.x + k * s * 0.26f, po.top + 4.2f * u), 2 * u, 8, 0.55f), t(ORO, oscuro)) }
                val pu = Rect(c.x - s * 0.07f, base - 9 * u, c.x + s * 0.07f, base)
                drawRoundRect(t(MADERA, oscuro), pu.topLeft, pu.size, CornerRadius(pu.width / 2, pu.width / 2))
                if (oscuro) halo(pu.center, 6 * u, LUZ.copy(alpha = 0.3f))
            }
            "rascar" -> {
                // El rollo de seda con sus rodillos de perillas de jade, la capa por rascar ya raspada y la moneda que la raspa.
                val m = Rect(c.x - s * 0.6f, c.y - s * 0.38f, c.x + s * 0.48f, c.y + s * 0.38f)
                drawRect(Color.Black.copy(alpha = 0.25f), m.topLeft + Offset(1.2f * u, 1.6f * u), m.size)
                drawRect(e.c1, m.topLeft, m.size)
                val se = m.deflate(2.4f * u)
                drawRect(t(MARFIL, oscuro), se.topLeft, se.size)
                listOf(m.left, m.right).forEach { x ->
                    drawRect(t(MADERA_LUZ, oscuro), Offset(x - 1.2f * u, m.top - 2.4f * u), Size(2.4f * u, m.height + 4.8f * u))
                    listOf(m.top - 2.4f * u, m.bottom + 2.4f * u).forEach { y -> drawCircle(t(JADE, oscuro), 1.6f * u, Offset(x, y)) }
                }
                val capa = se.deflate(2f * u)
                drawRect(t(Color(0xFFB8B2A2), oscuro), capa.topLeft, capa.size)
                clipRect(capa.left, capa.top, capa.right, capa.bottom) {
                    var k = capa.left - capa.height
                    while (k < capa.right) { drawLine(Color.White.copy(alpha = 0.25f), Offset(k, capa.bottom), Offset(k + capa.height, capa.top), 0.5f * u); k += 2.4f * u }
                    val raspa = Path().apply { moveTo(capa.left, capa.top + capa.height * 0.35f); cubicTo(capa.left + capa.width * 0.3f, capa.top - capa.height * 0.1f, capa.left + capa.width * 0.5f, capa.bottom, capa.right, capa.top + capa.height * 0.55f) }
                    drawPath(raspa, e.fondo, style = Stroke(capa.height * 0.42f, cap = StrokeCap.Round))
                    drawPath(raspa, e.c2.copy(alpha = 0.85f), style = Stroke(capa.height * 0.24f, cap = StrokeCap.Round))
                }
                val mc = Offset(m.right + s * 0.04f, m.bottom - s * 0.1f)
                drawCircle(Color.Black.copy(alpha = 0.3f), s * 0.24f, mc + Offset(1.2f * u, 1.6f * u))
                moneda(mc, s * 0.24f, oscuro, e.fondo)
            }
            else -> {
                // Tus lugares: tres farolillos rojos en sus astas, clavados en el mapa del desierto y unidos por la ruta.
                val m = Rect(c.x - s * 0.95f, c.y + s * 0.08f, c.x + s * 0.95f, c.y + s * 0.46f)
                val mp = Path().apply { moveTo(m.left + 4 * u, m.top); lineTo(m.right, m.top + 1 * u); lineTo(m.right - 4 * u, m.bottom); lineTo(m.left, m.bottom - 1 * u); close() }
                drawPath(mp, Color.Black.copy(alpha = 0.22f), style = Stroke(2 * u))
                drawPath(mp, t(ARENA, oscuro))
                clipPath(mp) {
                    for (k in 0 until 4) drawPath(Path().apply { moveTo(m.left, m.top + m.height * (0.3f + 0.2f * k)); quadraticTo(m.center.x, m.top + m.height * (0.1f + 0.2f * k), m.right, m.top + m.height * (0.35f + 0.2f * k)) }, t(KILIM_OCRE, oscuro).copy(alpha = 0.5f), style = Stroke(0.5f * u))
                }
                drawPath(mp, t(KILIM_OCRE, oscuro), style = Stroke(0.6f * u))
                val pts = listOf(Offset(c.x - s * 0.62f, m.top + m.height * 0.6f), Offset(c.x, m.top + m.height * 0.38f), Offset(c.x + s * 0.62f, m.top + m.height * 0.62f))
                val ruta = Path().apply { moveTo(pts[0].x, pts[0].y); quadraticTo(c.x - s * 0.3f, m.bottom, pts[1].x, pts[1].y); quadraticTo(c.x + s * 0.3f, m.top, pts[2].x, pts[2].y) }
                drawPath(ruta, e.c1, style = Stroke(1.1f * u, cap = StrokeCap.Round, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2.4f * u, 1.8f * u))))
                pts.forEachIndexed { i, p ->
                    val alto = s * (if (i == 1) 0.66f else 0.56f)
                    drawOval(Color.Black.copy(alpha = 0.3f), Offset(p.x - 2 * u, p.y - 0.8f * u), Size(4 * u, 1.6f * u))
                    drawLine(t(MADERA, oscuro), p, Offset(p.x, p.y - alto), 1.1f * u)
                    drawLine(t(MADERA, oscuro), Offset(p.x, p.y - alto), Offset(p.x + 5 * u, p.y - alto), 0.9f * u)
                    farolillo(Offset(p.x + 5 * u, p.y - alto), alto * 0.48f, oscuro)
                }
            }
        }
    }

    // ---------------------------------------------------------------- Notas: el biombo de laca

    override fun DrawScope.tableroNotas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val n = nicho(MuebleVida.NOTAS)
        // El suelo de ladrillo gris y el zócalo de laca negra con su faldón de nube.
        val suelo = Rect(0f, h * 0.965f, w, h)
        drawRect(t(PIEDRA, oscuro), suelo.topLeft, suelo.size)
        var x = 0f
        while (x < w) { drawLine(t(mezcla(PIEDRA, Color.Black, 0.3f), oscuro), Offset(x, suelo.top), Offset(x, suelo.bottom), 0.3f * u); x += 7 * u }
        val zo = Rect(w * 0.012f, h * 0.905f, w * 0.988f, h * 0.965f)
        sombra(zo, 1.5f * u, 0.35f)
        laca(zo, oscuro, NEGRO)
        val fal = Path().apply {
            moveTo(zo.left + w * 0.06f, zo.top + 1.2f * u); lineTo(zo.right - w * 0.06f, zo.top + 1.2f * u)
            quadraticTo(zo.right - w * 0.08f, zo.bottom - 0.6f * u, zo.right - w * 0.14f, zo.bottom - 1.2f * u)
            quadraticTo(w * 0.62f, zo.bottom - 2.4f * u, w * 0.5f, zo.bottom - 0.4f * u)
            quadraticTo(w * 0.38f, zo.bottom - 2.4f * u, zo.left + w * 0.14f, zo.bottom - 1.2f * u)
            quadraticTo(zo.left + w * 0.08f, zo.bottom - 0.6f * u, zo.left + w * 0.06f, zo.top + 1.2f * u); close()
        }
        drawPath(fal, t(LACA, oscuro))
        drawPath(fal, t(ORO, oscuro), style = Stroke(0.35f * u))
        // El marco de cinabrio con su filete de oro y las esquinas de oro en nube.
        val marco = Rect(w * 0.012f, h * 0.075f, w * 0.988f, h * 0.905f)
        sombra(marco, 3 * u, if (oscuro) 0.5f else 0.25f)
        laca(marco, oscuro)
        val med = Rect((marco.left + n.left) / 2, (marco.top + n.top) / 2, (marco.right + n.right) / 2, (marco.bottom + n.bottom) / 2)
        drawRect(t(ORO, oscuro), med.topLeft, med.size, style = Stroke(0.35f * u))
        drawRect(t(LACA_OSC, oscuro), n.inflate(1.2f * u).topLeft, n.inflate(1.2f * u).size)
        drawRect(t(ORO, oscuro), n.inflate(0.6f * u).topLeft, n.inflate(0.6f * u).size, style = Stroke(0.45f * u))
        // El papel de arroz tensado sobre la celosía, que se transparenta muy suave.
        drawRect(yeso(oscuro), n.topLeft, n.size)
        clipRect(n.left, n.top, n.right, n.bottom) {
            val tono = if (oscuro) Color.White.copy(alpha = 0.035f) else MADERA.copy(alpha = 0.06f)
            var xx = n.left + 9 * u
            while (xx < n.right) { drawLine(tono, Offset(xx, n.top), Offset(xx, n.bottom), 0.7f * u); xx += 18 * u }
            var yy = n.top + 9 * u
            while (yy < n.bottom) { drawLine(tono, Offset(n.left, yy), Offset(n.right, yy), 0.7f * u); yy += 18 * u }
        }
        sombraInterior(n, 3 * u, if (oscuro) 0.3f else 0.1f)
        listOf(med.topLeft, Offset(med.right, med.top), Offset(med.left, med.bottom), med.bottomRight).forEach { c ->
            val sx = if (c.x < w / 2) 1f else -1f; val sy = if (c.y < h / 2) 1f else -1f
            val p = Path().apply { moveTo(c.x, c.y + sy * 4.5f * u); lineTo(c.x, c.y); lineTo(c.x + sx * 4.5f * u, c.y); quadraticTo(c.x + sx * 2.2f * u, c.y + sy * 0.6f * u, c.x + sx * 1.2f * u, c.y + sy * 1.2f * u); quadraticTo(c.x + sx * 0.6f * u, c.y + sy * 2.2f * u, c.x, c.y + sy * 4.5f * u); close() }
            drawPath(p, t(ORO, oscuro))
            drawCircle(t(ORO_LUZ, oscuro), 0.5f * u, Offset(c.x + sx * 1f * u, c.y + sy * 1f * u))
        }
        // El alero de laca negra con sus extremos alzados y la fila de tejas redondas.
        val al = Path().apply {
            moveTo(0f, h * 0.012f)
            quadraticTo(w * 0.04f, h * 0.045f, w * 0.1f, h * 0.045f)
            lineTo(w * 0.9f, h * 0.045f)
            quadraticTo(w * 0.96f, h * 0.045f, w, h * 0.012f)
            lineTo(w, h * 0.04f)
            quadraticTo(w * 0.96f, h * 0.08f, w * 0.9f, h * 0.08f)
            lineTo(w * 0.1f, h * 0.08f)
            quadraticTo(w * 0.04f, h * 0.08f, 0f, h * 0.04f); close()
        }
        drawPath(al, t(NEGRO, oscuro))
        drawPath(Path().apply { moveTo(w * 0.1f, h * 0.05f); lineTo(w * 0.9f, h * 0.05f) }, t(ORO, oscuro), style = Stroke(0.35f * u))
        x = w * 0.1f + 1.6f * u
        while (x < w * 0.9f) {
            drawCircle(t(Color(0xFF3A3632), oscuro), 1.3f * u, Offset(x, h * 0.075f))
            drawCircle(t(ORO, oscuro), 1.3f * u, Offset(x, h * 0.075f), style = Stroke(0.25f * u))
            drawCircle(t(ORO_OSC, oscuro), 0.4f * u, Offset(x, h * 0.075f))
            x += 3.2f * u
        }
        // La puerta de luna de celosía al centro, entre dos nubes de oro.
        val cc = Offset(w / 2, h * 0.075f)
        val rr = 6f * u
        listOf(-1f, 1f).forEach { s -> nube(Offset(cc.x + s * 11 * u, cc.y + 0.6f * u), 7 * u, t(ORO, oscuro), t(LACA_OSC, oscuro)) }
        drawCircle(Color.Black.copy(alpha = 0.3f), rr + 1.2f * u, cc + Offset(0.3f * u, 0.6f * u))
        drawCircle(t(LACA_OSC, oscuro), rr + 1.2f * u, cc)
        drawCircle(t(ORO, oscuro), rr + 1.2f * u, cc, style = Stroke(0.4f * u))
        drawCircle(if (oscuro) mezcla(PAPEL, LUZ, 0.4f) else t(PAPEL, oscuro), rr, cc)
        if (oscuro) halo(cc, rr * 1.6f, LUZ.copy(alpha = 0.25f))
        clipPath(Path().apply { addOval(Rect(cc.x - rr, cc.y - rr, cc.x + rr, cc.y + rr)) }) {
            val pp = rr / 2.5f
            for (i in -3..3) for (j in -3..3) {
                val q = Offset(cc.x + i * pp, cc.y + j * pp)
                drawRect(t(LACA_OSC, oscuro), Offset(q.x - pp * 0.4f, q.y - pp * 0.4f), Size(pp * 0.8f, pp * 0.8f), style = Stroke(0.35f * u))
            }
            drawLine(t(LACA_OSC, oscuro), Offset(cc.x, cc.y - rr), Offset(cc.x, cc.y + rr), 0.5f * u)
            drawLine(t(LACA_OSC, oscuro), Offset(cc.x - rr, cc.y), Offset(cc.x + rr, cc.y), 0.5f * u)
        }
        drawCircle(t(LACA_OSC, oscuro), rr, cc, style = Stroke(0.7f * u))
        // Los dos farolillos colgados del alero, sobre los costados del marco.
        listOf((marco.left + n.left) / 2, (marco.right + n.right) / 2).forEach { xx -> farolillo(Offset(xx, h * 0.08f), 10 * u, oscuro) }
    }

    /** El papel del rollo: claro de día; de noche, más tostado a la luz de la lámpara (la tinta se lee en los dos). */
    private fun papel(oscuro: Boolean) = if (oscuro) Color(0xFFDCCFB0) else Color(0xFFF8F1E0)

    override fun DrawScope.notaTablero(e: EstadoNota, oscuro: Boolean) {
        val r = e.rect
        val u = r.width / 100f
        val brocado = when (e.tipo) {
            "idea" -> LAPIS
            "pensamiento" -> TURQ_OSC
            "proposito" -> LACA
            else -> Color(0xFFA87A28)
        }
        // La montura: los brocados de arriba y de abajo, las orillas y el papel; el palo de arriba y el rodillo de abajo.
        val mo = Rect(r.left + 5 * u, r.top + 8 * u, r.right - 5 * u, r.bottom - 4.5f * u)
        sombra(mo, 3 * u, if (oscuro) 0.5f else 0.28f)
        drawRect(brocado, mo.topLeft, mo.size)
        clipRect(mo.left, mo.top, mo.right, mo.bottom) {
            var yy = mo.top + 1.2f * u; var f = 0
            while (yy < mo.bottom) {
                var xx = mo.left + (if (f % 2 == 0) 1.2f else 3f) * u
                while (xx < mo.right) { drawPath(rombo(Offset(xx, yy), 0.6f * u, 0.6f * u), ORO.copy(alpha = 0.55f)); xx += 3.6f * u }
                yy += 2.2f * u; f++
            }
        }
        val pa = Rect(mo.left + 2.6f * u, mo.top + 7.5f * u, mo.right - 2.6f * u, mo.bottom - 5.5f * u)
        drawRect(Color.Black.copy(alpha = 0.25f), pa.inflate(0.6f * u).topLeft, pa.inflate(0.6f * u).size)
        drawRect(ORO, pa.inflate(0.5f * u).topLeft, pa.inflate(0.5f * u).size, style = Stroke(0.5f * u))
        drawRect(papel(oscuro), pa.topLeft, pa.size)
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, KILIM_OCRE.copy(alpha = 0.18f)), pa.top + pa.height * 0.6f, pa.bottom), Offset(pa.left, pa.top + pa.height * 0.6f), Size(pa.width, pa.height * 0.4f))
        // Las dos cintas que cuelgan del brocado de arriba (las «golondrinas»).
        listOf(0.36f, 0.64f).forEach { f -> drawRect(mezcla(brocado, Color.Black, 0.25f), Offset(mo.left + mo.width * f - 0.8f * u, mo.top), Size(1.6f * u, 7.5f * u)) }
        val palo = Rect(mo.left - 1.4f * u, mo.top - 1.6f * u, mo.right + 1.4f * u, mo.top + 0.4f * u)
        drawRoundRect(MADERA, palo.topLeft, palo.size, CornerRadius(0.8f * u))
        drawRect(Color.White.copy(alpha = 0.2f), Offset(palo.left, palo.top + 0.3f * u), Size(palo.width, 0.4f * u))
        val ro = Rect(mo.left - 1 * u, mo.bottom - 0.6f * u, mo.right + 1 * u, mo.bottom + 2.2f * u)
        drawRect(Color.Black.copy(alpha = 0.3f), ro.topLeft + Offset(0.4f * u, 0.6f * u), ro.size)
        drawRect(MADERA_LUZ, ro.topLeft, ro.size)
        drawRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.25f), Color.Transparent, Color.Black.copy(alpha = 0.3f)), ro.top, ro.bottom), ro.topLeft, ro.size)
        listOf(ro.left, ro.right).forEach { xx ->
            drawCircle(Color.Black.copy(alpha = 0.3f), 2 * u, Offset(xx + 0.3f * u, ro.center.y + 0.5f * u))
            drawCircle(JADE, 2 * u, Offset(xx, ro.center.y))
            drawCircle(JADE_LUZ, 0.7f * u, Offset(xx - 0.6f * u, ro.center.y - 0.6f * u))
        }
        // El detalle del tipo, abajo a la derecha del papel (fuera del texto).
        val d = Offset(pa.right - 7 * u, pa.bottom - 6.5f * u)
        when (e.tipo) {
            "idea" -> {
                // El sello rojo del dueño, con sus trazos en blanco.
                val sq = Rect(d.x - 4.4f * u, d.y - 4.4f * u, d.x + 4.4f * u, d.y + 4.4f * u)
                drawRect(Color(0xFFB8322A), sq.topLeft, sq.size)
                drawRect(papel(oscuro), sq.deflate(0.8f * u).topLeft, sq.deflate(0.8f * u).size, style = Stroke(0.5f * u))
                drawLine(papel(oscuro), Offset(sq.left + 2.2f * u, sq.top + 2.6f * u), Offset(sq.right - 2.2f * u, sq.top + 2.6f * u), 0.6f * u)
                drawLine(papel(oscuro), Offset(sq.center.x, sq.top + 2.6f * u), Offset(sq.center.x, sq.bottom - 2 * u), 0.6f * u)
                drawLine(papel(oscuro), Offset(sq.left + 2.2f * u, sq.bottom - 2.6f * u), Offset(sq.right - 2.2f * u, sq.bottom - 2.6f * u), 0.6f * u)
                drawLine(papel(oscuro), Offset(sq.left + 2.6f * u, sq.center.y), Offset(sq.center.x, sq.center.y), 0.6f * u)
            }
            "pensamiento" -> nube(Offset(d.x - 0.5f * u, d.y + 0.5f * u), 10 * u, ORO, ORO_OSC)
            "proposito" -> {
                drawCircle(Color.Black.copy(alpha = 0.2f), 4.6f * u, d + Offset(0.4f * u, 0.6f * u))
                moneda(d, 4.6f * u, false, papel(oscuro))
            }
            else -> {
                // La campanilla de latón con su cordón rojo.
                drawLine(LACA, Offset(d.x, d.y - 6 * u), Offset(d.x, d.y - 3 * u), 0.6f * u)
                val cb = Path().apply { moveTo(d.x - 4 * u, d.y + 3.4f * u); quadraticTo(d.x - 3.4f * u, d.y - 3.6f * u, d.x, d.y - 3.6f * u); quadraticTo(d.x + 3.4f * u, d.y - 3.6f * u, d.x + 4 * u, d.y + 3.4f * u); close() }
                drawPath(cb, Color.Black.copy(alpha = 0.2f), style = Stroke(1 * u))
                drawPath(cb, ORO)
                drawPath(Path().apply { moveTo(d.x - 1.4f * u, d.y - 2.8f * u); quadraticTo(d.x - 2.6f * u, d.y, d.x - 2.8f * u, d.y + 2.8f * u) }, ORO_LUZ, style = Stroke(0.8f * u, cap = StrokeCap.Round))
                drawRect(ORO_OSC, Offset(d.x - 4 * u, d.y + 2.8f * u), Size(8 * u, 1 * u))
                drawCircle(ORO_OSC, 1 * u, Offset(d.x, d.y + 4.4f * u))
                borla(Offset(d.x + 4 * u, d.y - 3 * u), 6 * u, LACA, false)
            }
        }
        // Arriba: el cordón que cuelga del clavo, o el nudo chino rojo con su disco de jade si está fijada.
        val cl = Offset(r.center.x, r.top + 2.2f * u)
        val cord = Path().apply { moveTo(palo.left + 6 * u, palo.center.y); lineTo(cl.x, cl.y); lineTo(palo.right - 6 * u, palo.center.y) }
        if (e.fijada) {
            drawPath(cord, LACA, style = Stroke(0.7f * u))
            // El nudo (pan chang): un rombo de cordón entrelazado, con sus lazadas y las dos borlas.
            val k = Offset(cl.x, cl.y + 1.6f * u)
            drawPath(rombo(k, 4 * u, 4 * u), LACA_OSC)
            drawPath(rombo(k, 3.6f * u, 3.6f * u), LACA)
            listOf(-1f, 1f).forEach { s ->
                drawLine(LACA_OSC, Offset(k.x - 2.4f * u, k.y + s * 1.2f * u - 1.2f * u * s), Offset(k.x + 2.4f * u, k.y - s * 1.2f * u + 1.2f * u * s), 0.35f * u)
                drawCircle(LACA, 1.2f * u, Offset(k.x + s * 4.2f * u, k.y))
                drawCircle(LACA_OSC, 1.2f * u, Offset(k.x + s * 4.2f * u, k.y), style = Stroke(0.3f * u))
            }
            drawLine(LACA_OSC, Offset(k.x - 1.8f * u, k.y - 1.8f * u), Offset(k.x + 1.8f * u, k.y + 1.8f * u), 0.35f * u)
            drawLine(LACA_OSC, Offset(k.x + 1.8f * u, k.y - 1.8f * u), Offset(k.x - 1.8f * u, k.y + 1.8f * u), 0.35f * u)
            drawCircle(Color.Black.copy(alpha = 0.25f), 2.4f * u, Offset(k.x + 0.3f * u, k.y + 6.4f * u))
            drawCircle(JADE, 2.4f * u, Offset(k.x, k.y + 6 * u))
            drawCircle(papel(oscuro), 0.8f * u, Offset(k.x, k.y + 6 * u))
            drawCircle(JADE_LUZ, 1.7f * u, Offset(k.x, k.y + 6 * u), style = Stroke(0.3f * u))
        } else {
            drawPath(cord, Color(0xFF8A6A3A), style = Stroke(0.5f * u))
            drawCircle(Color.Black.copy(alpha = 0.3f), 1.6f * u, cl + Offset(0.4f * u, 0.6f * u))
            drawCircle(ORO_OSC, 1.6f * u, cl)
            drawCircle(ORO, 1.2f * u, cl)
            drawCircle(ORO_LUZ, 0.45f * u, cl - Offset(0.4f * u, 0.4f * u))
        }
    }

    override fun tintaNota(oscuro: Boolean): Color = TINTA
}
