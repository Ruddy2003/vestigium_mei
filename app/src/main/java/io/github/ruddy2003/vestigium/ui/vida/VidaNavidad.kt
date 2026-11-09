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
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.lerp
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

/**
 * Navidad: los muebles de Vida y de Diario de la casa en Navidad, de la misma casa que su alacena de la cocina del Ideario,
 * su repisa junto a la chimenea de la biblioteca y su sala de la chimenea del Hall: nogal veteado, papel pintado de rayas,
 * abeto en guirnaldas con su cordón de luces de colores, lazos de raso rojo, acebo, velas, oro, nieve y bastones de caramelo.
 * - Social: la mesa de Nochebuena vista desde arriba: el piso de tablas, la alfombra roja con su cenefa, las sillas de nogal
 *   con su cojín de terciopelo, la mesa con su moldura y el mantel de lino con puntilla y cenefa bordada en punto de cruz; en
 *   el borde del mantel, los cubiertos (bajoplato dorado, plato con filete rojo y verde, tenedor, cuchillo y copa), los
 *   petardos navideños y cuatro candeleros con velas rojas y su corona de acebo (de noche, encendidas). El grafo va en el
 *   centro del mantel, dentro de su filete dorado.
 * - Mapas: el escritorio de las cartas de Navidad: el tablero de nogal con su filete, el paño verde con hierros dorados, el
 *   mapa de pergamino prendido con ramitas de acebo; a un lado, la vela en su palmatoria y el montón de cartas atadas con
 *   cinta y lacre; al otro, la bola de nieve y un regalo; el canto con su filete, el faldón con su cajón y la guirnalda de
 *   abeto en festones con lazos y luces, y las patas de cabriolé con zapata dorada.
 * - Otras vidas: el aparador de nogal: la estrella dorada en lo alto, la cornisa, el friso con la guirnalda en festones,
 *   cinco ventanitas de adviento (crema de día, verde abeto de noche) con su arco dorado y su alféizar nevado, separadas por
 *   columnillas de nogal; dos columnas de bastón de caramelo a los lados y el zócalo con puntilla, cajones y patas de bollo.
 * - Íconos: la iglesia nevada con su campanario, el rosetón y las ventanas encendidas; la tarjeta de felicitación con su
 *   capa para rascar y la moneda de chocolate envuelta en oro; tres abetos con su estrella sobre el mapa nevado, unidos por
 *   la huella del trineo.
 * - Notas: el panel de papel pintado de la sala en su marco de nogal, con la corona de puerta con lazo arriba, la guirnalda
 *   con luces, dos cordeles de panadero a lo ancho y la repisa con velas y calcetines; las notas son tarjetas de felicitación
 *   con doble filete rojo y verde, colgadas del cordel con una pinza de madera o, si están fijadas, con un lazo de raso y su
 *   cascabel, y con un detalle según el tipo (la estrella, el acebo, la campana o el bastón de caramelo).
 */
internal object VidaNavidad : DisenoVida {
    // ---------------------------------------------------------------- materiales (los de su Ideario, su biblioteca y su Hall)
    private val NOGAL = Color(0xFF6A4528)
    private val NOGAL_LUZ = Color(0xFF8A603A)
    private val NOGAL_VETA = Color(0xFF432A17)
    private val NOGAL_OSC = Color(0xFF3A2414)
    private val ABETO = Color(0xFF2E5A3A)
    private val ABETO_LUZ = Color(0xFF4A8256)
    private val ABETO_OSC = Color(0xFF1B3B24)
    private val ROJO = Color(0xFFB3262E)
    private val ROJO_LUZ = Color(0xFFD5464C)
    private val ROJO_OSC = Color(0xFF7A1418)
    private val VERDE = Color(0xFF2F6A45)
    private val ORO = Color(0xFFD9B04F)
    private val ORO_LUZ = Color(0xFFF2D98C)
    private val ORO_OSC = Color(0xFF8A6A24)
    private val NIEVE = Color(0xFFF4F6FA)
    private val NIEVE_SOMBRA = Color(0xFFC9D4E0)
    private val CREMA = Color(0xFFF3E8D2)
    private val CREMA_OSC = Color(0xFFD8C8A8)
    private val PERGAMINO = Color(0xFFEFE3C6)
    private val PERGAMINO_OSC = Color(0xFFCDB88E)
    private val TABLA = Color(0xFF7A5434)
    private val TABLA_OSC = Color(0xFF55381F)
    private val ALFOMBRA = Color(0xFF8E2A30)
    private val ALFOMBRA_OSC = Color(0xFF5E161C)
    private val PLATA = Color(0xFFC8CCD0)
    private val CIELO = Color(0xFFBFD0DD)
    private val CERA = Color(0xFFF2E6C8)
    private val LLAMA = Color(0xFFFFD27A)
    private val TINTA = Color(0xFF1E2A22)
    private val NOCHE = Color(0xFF09110C)
    /** Las bombillas del cordón: roja, verde, amarilla, azul y rosa (las de su biblioteca). */
    private val BOMBILLAS = listOf(Color(0xFFE84A3C), Color(0xFF4CC46A), Color(0xFFF2C94C), Color(0xFF4C8CE8), Color(0xFFF08CC8))

    /** El color en oscuro: más hundido, la sala de noche (como su biblioteca y su Hall). */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, NOCHE, 0.36f) else c

    private fun mezcla(a: Color, b: Color, f: Float): Color = lerp(a, b, f.coerceIn(0f, 1f))

    /** El lino del mantel (el nicho de Social): crema de día; de noche, terciopelo verde abeto. */
    private fun mantel(oscuro: Boolean) = if (oscuro) Color(0xFF1A2E22) else Color(0xFFFAF4E6)

    /** El pergamino del mapa: claro de día; de noche, en la penumbra del cuarto. */
    private fun pergaminoMapa(oscuro: Boolean) = if (oscuro) Color(0xFF1F2B23) else Color(0xFFF8F0DD)

    /** El fondo de las ventanitas de adviento y del papel pintado: crema de día, verde abeto de noche. */
    private fun forro(oscuro: Boolean) = if (oscuro) Color(0xFF16271D) else Color(0xFFF8F0E0)

    override fun area(m: MuebleVida): Rect = when (m) {
        MuebleVida.SOCIAL -> Rect(0.15f, 0.215f, 0.85f, 0.785f)
        MuebleVida.MAPAS -> Rect(0.16f, 0.085f, 0.84f, 0.71f)
        MuebleVida.OTRAS_VIDAS -> Rect(0.065f, 0.2f, 0.935f, 0.81f)
        MuebleVida.NOTAS -> Rect(0.065f, 0.15f, 0.935f, 0.865f)
    }

    private fun DrawScope.nicho(m: MuebleVida): Rect {
        val a = area(m)
        return Rect(size.width * a.left, size.height * a.top, size.width * a.right, size.height * a.bottom)
    }

    // ---------------------------------------------------------------- ayudas de dibujo

    /** Un número estable de 0 a 1 para lo que varía (vetas, nudos, copos). */
    private fun azar(semilla: Int, k: Int): Float {
        var x = semilla * 374761393 + k * 668265263
        x = (x xor (x ushr 13)) * 1274126177
        x = x xor (x ushr 16)
        return (x and 0x7fffffff) / 2147483647f
    }

    /** Las vetas de la madera en [r], recortadas a la pieza: líneas onduladas a lo largo, más o menos marcadas. */
    private fun DrawScope.vetas(r: Rect, color: Color, semilla: Int, densidad: Float = 1f, vertical: Boolean = false, grosor: Float = 1f, ondulacion: Float = 1f) =
        clipRect(r.left, r.top, r.right, r.bottom) {
            val d = density
            val largo = if (vertical) r.height else r.width
            val ancho = if (vertical) r.width else r.height
            val n = (ancho / (3.2f * d) * densidad).toInt().coerceIn(2, 60)
            for (k in 0 until n) {
                val base = ancho * (k + azar(semilla, k)) / n
                val amp = (0.6f + 1.8f * azar(semilla, k + 100)) * d * ondulacion
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

    /** Una tabla de nogal en [r]: el color y la veta. */
    private fun DrawScope.nogal(r: Rect, oscuro: Boolean, semilla: Int, vertical: Boolean = false, color: Color = NOGAL) {
        drawRect(t(color, oscuro), r.topLeft, r.size)
        vetas(r, t(NOGAL_VETA, oscuro).copy(alpha = 0.55f), semilla, densidad = 0.8f, vertical = vertical, grosor = 0.7f, ondulacion = 0.8f)
    }

    /** Una pieza de latón dorado en [r]: el oro, su luz arriba y su sombra abajo. */
    private fun DrawScope.oro(r: Rect, oscuro: Boolean, radio: Float = 0f) {
        drawRoundRect(t(ORO_OSC, oscuro), r.topLeft, r.size, CornerRadius(radio))
        drawRoundRect(t(ORO, oscuro), r.topLeft, Size(r.width, r.height * 0.78f), CornerRadius(radio))
        drawRoundRect(t(ORO_LUZ, oscuro), Offset(r.left + r.width * 0.04f, r.top + r.height * 0.12f), Size(r.width * 0.92f, r.height * 0.18f), CornerRadius(radio))
    }

    /** Una sombra suave debajo de algo: varias capas cada vez más transparentes. */
    private fun DrawScope.sombra(r: Rect, alcance: Float, fuerza: Float) {
        for (k in 4 downTo 1) {
            val e = alcance * k / 4f
            drawRoundRect(Color.Black.copy(alpha = fuerza / 4f), Offset(r.left - e * 0.3f, r.top + e * 0.5f), Size(r.width + e * 0.6f, r.height + e * 0.4f), CornerRadius(e))
        }
    }

    /** La sombra de dentro de un hueco: más honda arriba y a los lados. */
    private fun DrawScope.sombraInterior(r: Rect, ancho: Float, fuerza: Float) {
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = fuerza), Color.Transparent), r.top, r.top + ancho), r.topLeft, Size(r.width, ancho))
        drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = fuerza * 0.6f), Color.Transparent), r.left, r.left + ancho * 0.7f), r.topLeft, Size(ancho * 0.7f, r.height))
        drawRect(Brush.horizontalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = fuerza * 0.6f)), r.right - ancho * 0.7f, r.right), Offset(r.right - ancho * 0.7f, r.top), Size(ancho * 0.7f, r.height))
    }

    /** Un halo de luz (las velas y las bombillas encendidas). */
    private fun DrawScope.halo(c: Offset, r: Float, color: Color) =
        drawCircle(Brush.radialGradient(listOf(color, color.copy(alpha = 0f)), c, r.coerceAtLeast(1f)), r, c)

    /** Una estrella de [puntas] puntas; [hondo] es el radio de los entrantes, en fracción de [r]. */
    private fun estrella(c: Offset, r: Float, puntas: Int, hondo: Float): Path = Path().apply {
        for (k in 0 until puntas * 2) {
            val a = Math.toRadians(-90.0 + k * 180.0 / puntas)
            val rr = if (k % 2 == 0) r else r * hondo
            val x = c.x + rr * cos(a).toFloat(); val y = c.y + rr * sin(a).toFloat()
            if (k == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
    }

    /** La estrella dorada con volumen: la sombra, el oro en dos caras por punta y el brillo. */
    private fun DrawScope.estrellaOro(c: Offset, r: Float, oscuro: Boolean) {
        drawPath(estrella(c + Offset(r * 0.06f, r * 0.1f), r, 5, 0.46f), Color.Black.copy(alpha = 0.25f))
        drawPath(estrella(c, r, 5, 0.46f), t(ORO_OSC, oscuro))
        drawPath(estrella(c, r * 0.86f, 5, 0.46f), t(ORO, oscuro))
        // La mitad izquierda de cada punta, más clara: el relieve de la estrella tallada.
        for (k in 0 until 5) {
            val a = Math.toRadians(-90.0 + k * 72.0)
            val b = Math.toRadians(-90.0 + k * 72.0 - 36.0)
            val p = Path().apply {
                moveTo(c.x, c.y)
                lineTo(c.x + r * 0.86f * cos(a).toFloat(), c.y + r * 0.86f * sin(a).toFloat())
                lineTo(c.x + r * 0.4f * cos(b).toFloat(), c.y + r * 0.4f * sin(b).toFloat())
                close()
            }
            drawPath(p, t(ORO_LUZ, oscuro).copy(alpha = 0.75f))
        }
        drawCircle(t(ORO_LUZ, oscuro), r * 0.1f, c)
    }

    /** Una hoja (un óvalo girado). */
    private fun DrawScope.hoja(c: Offset, largo: Float, ancho: Float, grados: Float, color: Color) =
        rotate(grados, c) { drawOval(color, Offset(c.x - largo / 2, c.y - ancho / 2), Size(largo, ancho)) }

    /** Una hoja de acebo dentada, de [c] hacia el ángulo [grados], de largo [s]. */
    private fun DrawScope.hojaAcebo(c: Offset, s: Float, grados: Float, color: Color, nervio: Color) = rotate(grados, c) {
        val p = Path().apply {
            moveTo(c.x, c.y)
            for (k in 1..4) { val f = k / 4f; lineTo(c.x + s * f - s * 0.06f, c.y - s * (if (k % 2 == 0) 0.12f else 0.26f) * (1 - f * 0.55f)) }
            lineTo(c.x + s, c.y)
            for (k in 4 downTo 1) { val f = k / 4f; lineTo(c.x + s * f - s * 0.06f, c.y + s * (if (k % 2 == 0) 0.12f else 0.26f) * (1 - f * 0.55f)) }
            close()
        }
        drawPath(p, color)
        drawLine(nervio, c, Offset(c.x + s * 0.9f, c.y), s * 0.05f)
    }

    /** Una ramita de acebo: dos hojas dentadas en dos tonos y tres bayas rojas con su brillo. */
    private fun DrawScope.acebo(c: Offset, s: Float, oscuro: Boolean, giro: Float = 0f) {
        hojaAcebo(c, s, 200f + giro, t(ABETO_OSC, oscuro), t(ABETO_LUZ, oscuro))
        hojaAcebo(c, s, -20f + giro, t(VERDE, oscuro), t(ABETO_LUZ, oscuro))
        listOf(Offset(-0.12f, -0.1f), Offset(0.12f, -0.08f), Offset(0f, 0.1f)).forEach { o ->
            val b = Offset(c.x + o.x * s, c.y + o.y * s)
            drawCircle(t(ROJO_OSC, oscuro), s * 0.15f, b)
            drawCircle(t(ROJO, oscuro), s * 0.12f, b - Offset(s * 0.02f, s * 0.02f))
            drawCircle(Color.White.copy(alpha = 0.7f), s * 0.035f, b - Offset(s * 0.05f, s * 0.05f))
        }
    }

    /** Un copo de nieve de seis brazos con sus ramitas. */
    private fun DrawScope.copo(c: Offset, r: Float, color: Color, grosor: Float) {
        for (k in 0 until 6) {
            val a = k * Math.PI / 3
            val e = Offset(c.x + r * cos(a).toFloat(), c.y + r * sin(a).toFloat())
            drawLine(color, c, e, grosor, StrokeCap.Round)
            val m = Offset(c.x + r * 0.6f * cos(a).toFloat(), c.y + r * 0.6f * sin(a).toFloat())
            listOf(0.6, -0.6).forEach { da -> drawLine(color, m, Offset(m.x + r * 0.3f * cos(a + da).toFloat(), m.y + r * 0.3f * sin(a + da).toFloat()), grosor, StrokeCap.Round) }
        }
    }

    /** Un lazo de raso visto de frente, centrado en [c] y de ancho [s]: las dos lazadas, el nudo y las dos caídas. */
    private fun DrawScope.lazo(c: Offset, s: Float, color: Color, oscuro: Boolean) {
        val base = t(color, oscuro)
        val osc = mezcla(base, Color.Black, 0.35f)
        val luz = mezcla(base, Color.White, 0.25f)
        // Las caídas, con su corte en cola de golondrina.
        listOf(-1f, 1f).forEach { k ->
            val p = Path().apply {
                moveTo(c.x - s * 0.04f * k, c.y)
                lineTo(c.x + k * s * 0.12f, c.y)
                lineTo(c.x + k * s * 0.34f, c.y + s * 0.62f)
                lineTo(c.x + k * s * 0.24f, c.y + s * 0.54f)
                lineTo(c.x + k * s * 0.16f, c.y + s * 0.66f)
                close()
            }
            drawPath(p, if (k < 0) base else osc)
        }
        // Las lazadas: el raso, su sombra dentro y el brillo.
        listOf(-1f, 1f).forEach { k ->
            val p = Path().apply {
                moveTo(c.x, c.y)
                cubicTo(c.x + k * s * 0.2f, c.y - s * 0.42f, c.x + k * s * 0.56f, c.y - s * 0.34f, c.x + k * s * 0.5f, c.y)
                cubicTo(c.x + k * s * 0.48f, c.y + s * 0.2f, c.x + k * s * 0.2f, c.y + s * 0.14f, c.x, c.y)
                close()
            }
            drawPath(p, base)
            drawOval(osc.copy(alpha = 0.7f), Offset(c.x + (if (k < 0) -s * 0.34f else s * 0.14f), c.y - s * 0.1f), Size(s * 0.2f, s * 0.12f))
            drawPath(Path().apply { moveTo(c.x + k * s * 0.1f, c.y - s * 0.1f); quadraticTo(c.x + k * s * 0.3f, c.y - s * 0.3f, c.x + k * s * 0.44f, c.y - s * 0.16f) }, luz.copy(alpha = 0.8f), style = Stroke(s * 0.04f, cap = StrokeCap.Round))
        }
        drawRoundRect(osc, Offset(c.x - s * 0.09f, c.y - s * 0.1f), Size(s * 0.18f, s * 0.2f), CornerRadius(s * 0.05f))
        drawRoundRect(base, Offset(c.x - s * 0.07f, c.y - s * 0.09f), Size(s * 0.13f, s * 0.16f), CornerRadius(s * 0.04f))
    }

    /** Una bombilla del cordón en [c], colgando hacia [abajo]: el casquillo, el cristal y, de noche, su halo encendido. */
    private fun DrawScope.bombilla(c: Offset, r: Float, color: Color, oscuro: Boolean, grados: Float = 0f) {
        if (oscuro) halo(c, r * 4.2f, color.copy(alpha = 0.42f))
        rotate(grados, c) {
            drawRect(t(ABETO_OSC, oscuro), Offset(c.x - r * 0.45f, c.y - r * 1.5f), Size(r * 0.9f, r * 0.7f))
            val vidrio = if (oscuro) mezcla(color, Color.White, 0.25f) else mezcla(color, Color.Black, 0.1f)
            drawOval(vidrio, Offset(c.x - r * 0.7f, c.y - r * 0.9f), Size(r * 1.4f, r * 2f))
            drawOval(Color.White.copy(alpha = if (oscuro) 0.75f else 0.45f), Offset(c.x - r * 0.4f, c.y - r * 0.6f), Size(r * 0.4f, r * 0.7f))
        }
    }

    /**
     * Una guirnalda de abeto que cuelga de [a] a [b] con una caída de [caida]: las ramitas en tres verdes, unas piñas y bolas, y
     * el cordón de luces de colores que la sigue.
     */
    private fun DrawScope.guirnalda(a: Offset, b: Offset, caida: Float, grueso: Float, oscuro: Boolean, semilla: Int, luces: Boolean = true) {
        val ctl = Offset((a.x + b.x) / 2, (a.y + b.y) / 2 + caida * 2)
        fun punto(tt: Float): Offset {
            val m = 1 - tt
            return Offset(m * m * a.x + 2 * m * tt * ctl.x + tt * tt * b.x, m * m * a.y + 2 * m * tt * ctl.y + tt * tt * b.y)
        }
        val largo = (b - a).getDistance() + caida
        val n = max(6, (largo / (grueso * 0.55f)).toInt())
        // La masa de ramas: primero la sombra, luego las agujas.
        for (i in 0..n) drawCircle(t(ABETO_OSC, oscuro), grueso * 0.5f, punto(i / n.toFloat()) + Offset(0f, grueso * 0.08f))
        for (i in 0..n) {
            val p = punto(i / n.toFloat())
            for (j in 0 until 5) {
                val ang = azar(semilla, i * 7 + j) * 6.28f
                val l = grueso * (0.35f + 0.25f * azar(semilla, i * 7 + j + 50))
                val col = if (j % 2 == 0) ABETO else ABETO_LUZ
                drawLine(t(col, oscuro), p, Offset(p.x + l * cos(ang), p.y + l * sin(ang) * 0.8f), grueso * 0.11f, StrokeCap.Round)
            }
        }
        // Unas bolas rojas y doradas entre las ramas.
        for (i in 1 until n step 4) {
            val p = punto(i / n.toFloat()) + Offset(0f, grueso * 0.15f)
            val col = if ((i / 4) % 2 == 0) ROJO else ORO
            drawCircle(t(mezcla(col, Color.Black, 0.3f), oscuro), grueso * 0.24f, p)
            drawCircle(t(col, oscuro), grueso * 0.19f, p - Offset(grueso * 0.03f, grueso * 0.03f))
            drawCircle(Color.White.copy(alpha = 0.6f), grueso * 0.06f, p - Offset(grueso * 0.08f, grueso * 0.08f))
        }
        if (luces) {
            // El cordón de luces, un poco por debajo, con sus bombillas de colores.
            val cord = Path()
            for (i in 0..24) { val p = punto(i / 24f) + Offset(0f, grueso * 0.35f); if (i == 0) cord.moveTo(p.x, p.y) else cord.lineTo(p.x, p.y) }
            drawPath(cord, t(ABETO_OSC, oscuro), style = Stroke(grueso * 0.06f))
            val nb = max(3, (largo / (grueso * 1.5f)).toInt())
            for (i in 1 until nb) {
                val p = punto(i / nb.toFloat()) + Offset(0f, grueso * 0.6f)
                bombilla(p, grueso * 0.17f, BOMBILLAS[(i + semilla) % BOMBILLAS.size], oscuro, if (i % 2 == 0) -12f else 12f)
            }
        }
    }

    /** Una vela de cera de pie en [base], de ancho [an] y alto [al]: el cuerpo con su chorreón, el pabilo y la llama. */
    private fun DrawScope.vela(base: Offset, an: Float, al: Float, color: Color, oscuro: Boolean) {
        val top = base.y - al
        if (oscuro) halo(Offset(base.x, top - an * 0.9f), an * 6f, LLAMA.copy(alpha = 0.3f))
        else halo(Offset(base.x, top - an * 0.9f), an * 2.4f, LLAMA.copy(alpha = 0.25f))
        drawRect(t(color, oscuro), Offset(base.x - an / 2, top), Size(an, al))
        drawRect(Color.White.copy(alpha = 0.22f), Offset(base.x - an / 2, top), Size(an * 0.25f, al))
        drawRect(Color.Black.copy(alpha = 0.2f), Offset(base.x + an * 0.25f, top), Size(an * 0.25f, al))
        // El chorreón de cera, arriba.
        drawRoundRect(t(mezcla(color, Color.White, 0.15f), oscuro), Offset(base.x - an / 2, top - an * 0.06f), Size(an, an * 0.28f), CornerRadius(an * 0.14f))
        drawRoundRect(t(mezcla(color, Color.White, 0.15f), oscuro), Offset(base.x + an * 0.08f, top), Size(an * 0.2f, an * 0.8f), CornerRadius(an * 0.1f))
        drawLine(Color.Black.copy(alpha = 0.7f), Offset(base.x, top), Offset(base.x, top - an * 0.3f), an * 0.08f)
        val lc = Offset(base.x, top - an * 0.75f)
        val llama = Path().apply { moveTo(lc.x, lc.y - an * 0.7f); quadraticTo(lc.x + an * 0.42f, lc.y + an * 0.05f, lc.x, lc.y + an * 0.36f); quadraticTo(lc.x - an * 0.42f, lc.y + an * 0.05f, lc.x, lc.y - an * 0.7f); close() }
        drawPath(llama, Color(0xFFFFA726))
        drawOval(Color(0xFFFFF3C4), Offset(lc.x - an * 0.13f, lc.y - an * 0.15f), Size(an * 0.26f, an * 0.42f))
    }

    /** Una columna de bastón de caramelo en [r]: el fuste con la espiral roja y su brillo, el capitel y la basa dorados. */
    private fun DrawScope.baston(r: Rect, oscuro: Boolean) {
        val w = r.width
        val cap = w * 0.7f
        val fuste = Rect(r.left + w * 0.14f, r.top + cap, r.right - w * 0.14f, r.bottom - cap * 0.8f)
        drawRect(t(NIEVE, oscuro), fuste.topLeft, fuste.size)
        clipRect(fuste.left, fuste.top, fuste.right, fuste.bottom) {
            val paso = fuste.width * 1.5f
            var y = fuste.top - fuste.width * 2
            var k = 0
            while (y < fuste.bottom + fuste.width) {
                val p = Path().apply { moveTo(fuste.left, y); lineTo(fuste.right, y - fuste.width * 0.9f); lineTo(fuste.right, y - fuste.width * 0.9f + paso * 0.45f); lineTo(fuste.left, y + paso * 0.45f); close() }
                drawPath(p, t(if (k % 2 == 0) ROJO else ROJO_LUZ, oscuro))
                if (k % 2 == 1) drawPath(Path().apply { moveTo(fuste.left, y + paso * 0.5f); lineTo(fuste.right, y - fuste.width * 0.9f + paso * 0.5f) }, t(VERDE, oscuro), style = Stroke(fuste.width * 0.08f))
                y += paso; k++
            }
        }
        // El volumen del cilindro: luz a la izquierda y sombra a la derecha.
        drawRect(Brush.horizontalGradient(listOf(Color.White.copy(alpha = 0.35f), Color.Transparent, Color.Black.copy(alpha = 0.3f)), fuste.left, fuste.right), fuste.topLeft, fuste.size)
        // El capitel (collarino, equino y ábaco) y la basa (toro y plinto), dorados.
        oro(Rect(fuste.left - w * 0.04f, fuste.top - cap * 0.25f, fuste.right + w * 0.04f, fuste.top), oscuro)
        drawPath(Path().apply { moveTo(fuste.left - w * 0.04f, fuste.top - cap * 0.25f); lineTo(r.left, r.top + cap * 0.38f); lineTo(r.right, r.top + cap * 0.38f); lineTo(fuste.right + w * 0.04f, fuste.top - cap * 0.25f); close() }, t(ORO, oscuro))
        oro(Rect(r.left - w * 0.04f, r.top, r.right + w * 0.04f, r.top + cap * 0.38f), oscuro)
        oro(Rect(fuste.left - w * 0.08f, fuste.bottom, fuste.right + w * 0.08f, fuste.bottom + cap * 0.36f), oscuro, cap * 0.15f)
        oro(Rect(r.left - w * 0.04f, fuste.bottom + cap * 0.36f, r.right + w * 0.04f, r.bottom), oscuro)
    }

    /** Una columnilla de nogal en [r]: el fuste con sus estrías, el capitel y la basa de latón. */
    private fun DrawScope.columnilla(r: Rect, oscuro: Boolean, semilla: Int) {
        val w = r.width
        val cap = w * 0.75f
        val fuste = Rect(r.left + w * 0.14f, r.top + cap, r.right - w * 0.14f, r.bottom - cap * 0.6f)
        nogal(fuste, oscuro, semilla, vertical = true, color = NOGAL_LUZ)
        val nn = if (fuste.width > 8 * density) 3 else 2
        for (k in 1..nn) {
            val x = fuste.left + fuste.width * k / (nn + 1)
            drawLine(Color.Black.copy(alpha = 0.3f), Offset(x, fuste.top + cap * 0.2f), Offset(x, fuste.bottom - cap * 0.2f), max(0.5f * density, fuste.width * 0.06f), StrokeCap.Round)
        }
        drawRect(Color.White.copy(alpha = 0.12f), fuste.topLeft, Size(fuste.width * 0.22f, fuste.height))
        drawRect(Color.Black.copy(alpha = 0.22f), Offset(fuste.right - fuste.width * 0.22f, fuste.top), Size(fuste.width * 0.22f, fuste.height))
        oro(Rect(fuste.left - w * 0.04f, fuste.top - cap * 0.3f, fuste.right + w * 0.04f, fuste.top), oscuro)
        oro(Rect(r.left, r.top, r.right, r.top + cap * 0.7f), oscuro)
        oro(Rect(fuste.left - w * 0.06f, fuste.bottom, fuste.right + w * 0.06f, fuste.bottom + cap * 0.3f), oscuro, cap * 0.1f)
        oro(Rect(r.left, fuste.bottom + cap * 0.3f, r.right, r.bottom), oscuro)
    }

    /** Un calcetín de lana colgado de [p] (el puño arriba), de alto [a]: el puño blanco, la pierna, el pie, el talón y sus rayas. */
    private fun DrawScope.calcetin(p: Offset, a: Float, color: Color, oscuro: Boolean) {
        val an = a * 0.42f
        val pierna = Path().apply {
            moveTo(p.x - an / 2, p.y + a * 0.2f)
            lineTo(p.x - an / 2, p.y + a * 0.78f)
            quadraticTo(p.x - an / 2, p.y + a, p.x - an * 0.1f, p.y + a)
            lineTo(p.x + an * 1.0f, p.y + a)
            quadraticTo(p.x + an * 1.25f, p.y + a * 0.86f, p.x + an * 0.9f, p.y + a * 0.72f)
            lineTo(p.x + an / 2, p.y + a * 0.66f)
            lineTo(p.x + an / 2, p.y + a * 0.2f)
            close()
        }
        drawPath(pierna, t(color, oscuro))
        clipPath(pierna) {
            listOf(0.4f, 0.52f).forEach { f -> drawRect(t(if (color == ROJO) VERDE else ROJO, oscuro), Offset(p.x - an, p.y + a * f), Size(an * 2, a * 0.05f)) }
            drawRect(Color.Black.copy(alpha = 0.18f), Offset(p.x + an * 0.2f, p.y), Size(an, a * 0.7f))
        }
        drawOval(t(NIEVE, oscuro), Offset(p.x + an * 0.55f, p.y + a * 0.78f), Size(an * 0.6f, a * 0.22f))
        drawRoundRect(t(NIEVE, oscuro), Offset(p.x - an * 0.6f, p.y), Size(an * 1.2f, a * 0.24f), CornerRadius(a * 0.06f))
        drawRoundRect(t(NIEVE_SOMBRA, oscuro), Offset(p.x - an * 0.6f, p.y + a * 0.17f), Size(an * 1.2f, a * 0.07f), CornerRadius(a * 0.03f))
        drawLine(t(ORO, oscuro), Offset(p.x, p.y), Offset(p.x, p.y - a * 0.12f), a * 0.04f)
    }

    /** Un regalo visto de frente, con su base en [base]: la caja con su tapa, la cinta en cruz y el lazo encima. */
    private fun DrawScope.regalo(base: Offset, an: Float, al: Float, papel: Color, cinta: Color, oscuro: Boolean) {
        val caja = Rect(base.x - an / 2, base.y - al, base.x + an / 2, base.y)
        sombra(Rect(caja.left, base.y - al * 0.1f, caja.right, base.y), an * 0.08f, 0.35f)
        drawRect(t(papel, oscuro), caja.topLeft, caja.size)
        clipRect(caja.left, caja.top, caja.right, caja.bottom) {
            for (k in 0 until 6) drawCircle(t(mezcla(papel, Color.White, 0.5f), oscuro).copy(alpha = 0.5f), an * 0.035f, Offset(caja.left + an * (0.15f + 0.17f * k), caja.top + al * (0.35f + 0.3f * (k % 2))))
        }
        drawRect(Color.Black.copy(alpha = 0.18f), Offset(caja.right - an * 0.22f, caja.top), Size(an * 0.22f, al))
        val tapa = Rect(caja.left - an * 0.05f, caja.top, caja.right + an * 0.05f, caja.top + al * 0.22f)
        drawRect(t(mezcla(papel, Color.Black, 0.12f), oscuro), tapa.topLeft, tapa.size)
        drawRect(Color.White.copy(alpha = 0.18f), tapa.topLeft, Size(tapa.width, tapa.height * 0.25f))
        drawRect(t(cinta, oscuro), Offset(base.x - an * 0.08f, tapa.top), Size(an * 0.16f, al))
        drawRect(t(cinta, oscuro), Offset(caja.left, caja.top + al * 0.55f), Size(an, al * 0.1f))
        lazo(Offset(base.x, tapa.top), an * 0.62f, cinta, oscuro)
    }

    /** El piso de tablas de nogal claro en [r], a lo ancho, con sus juntas escalonadas. */
    private fun DrawScope.tablas(r: Rect, oscuro: Boolean, semilla: Int) {
        val u = size.minDimension / 100f
        drawRect(t(TABLA, oscuro), r.topLeft, r.size)
        val alto = 5.2f * u
        var y = r.top
        var k = 0
        while (y < r.bottom) {
            val f = Rect(r.left, y, r.right, y + alto)
            drawRect(t(if (k % 3 == 1) mezcla(TABLA, TABLA_OSC, 0.25f) else if (k % 3 == 2) mezcla(TABLA, NOGAL_LUZ, 0.3f) else TABLA, oscuro), f.topLeft, f.size)
            vetas(f, t(TABLA_OSC, oscuro).copy(alpha = 0.45f), semilla + k, densidad = 0.5f, grosor = 0.5f)
            drawLine(t(TABLA_OSC, oscuro), Offset(r.left, y), Offset(r.right, y), 0.35f * u)
            var x = r.left + r.width * azar(semilla, k) * 0.5f
            while (x < r.right) { drawLine(t(TABLA_OSC, oscuro), Offset(x, y), Offset(x, y + alto), 0.3f * u); x += r.width * 0.45f }
            y += alto; k++
        }
    }

    // ---------------------------------------------------------------- Social: la mesa de Nochebuena

    /** Una silla de nogal vista desde arriba, con el asiento en [c] y el respaldo hacia fuera de la mesa (girada [grados]). */
    private fun DrawScope.silla(c: Offset, grados: Float, u: Float, oscuro: Boolean, semilla: Int) = rotate(grados, c) {
        val asiento = Rect(c.x - 5.6f * u, c.y - 3.2f * u, c.x + 5.6f * u, c.y + 3.4f * u)
        sombra(asiento.inflate(0.6f * u), 1.6f * u, 0.3f)
        drawRoundRect(t(NOGAL, oscuro), asiento.topLeft, asiento.size, CornerRadius(1.2f * u))
        vetas(asiento, t(NOGAL_VETA, oscuro).copy(alpha = 0.4f), semilla, densidad = 0.6f, grosor = 0.5f)
        // El cojín de terciopelo rojo con su vivo y su botón.
        val coj = asiento.deflate(1f * u)
        drawRoundRect(t(ROJO_OSC, oscuro), coj.topLeft, coj.size, CornerRadius(1.4f * u))
        drawRoundRect(t(ROJO, oscuro), coj.topLeft, Size(coj.width, coj.height * 0.82f), CornerRadius(1.4f * u))
        drawRoundRect(t(ROJO_LUZ, oscuro).copy(alpha = 0.6f), Offset(coj.left + 0.8f * u, coj.top + 0.6f * u), Size(coj.width * 0.5f, 0.6f * u), CornerRadius(0.3f * u))
        drawRoundRect(t(ORO, oscuro).copy(alpha = 0.8f), coj.topLeft, coj.size, CornerRadius(1.4f * u), style = Stroke(0.3f * u))
        drawCircle(t(ROJO_OSC, oscuro), 0.5f * u, coj.center)
        // El respaldo: el travesaño curvo con sus dos postes y tres barrotes que bajan al asiento.
        for (k in 0 until 3) {
            val x = c.x + (k - 1) * 2.6f * u
            drawLine(t(NOGAL_OSC, oscuro), Offset(x, asiento.top - 1.6f * u), Offset(x, asiento.top + 0.4f * u), 0.6f * u)
        }
        val tr = Path().apply {
            moveTo(c.x - 6.2f * u, asiento.top - 1.2f * u)
            quadraticTo(c.x, asiento.top - 3.6f * u, c.x + 6.2f * u, asiento.top - 1.2f * u)
            lineTo(c.x + 6.2f * u, asiento.top - 0.1f * u)
            quadraticTo(c.x, asiento.top - 2.4f * u, c.x - 6.2f * u, asiento.top - 0.1f * u)
            close()
        }
        drawPath(tr, t(NOGAL_LUZ, oscuro))
        drawPath(tr, t(NOGAL_OSC, oscuro), style = Stroke(0.25f * u))
        listOf(-1f, 1f).forEach { s -> drawCircle(t(NOGAL_OSC, oscuro), 0.9f * u, Offset(c.x + s * 5.6f * u, asiento.top - 0.6f * u)); drawCircle(t(ORO, oscuro), 0.4f * u, Offset(c.x + s * 5.6f * u, asiento.top - 0.6f * u)) }
    }

    /** Un cubierto visto desde arriba con el plato en [c]; el comensal se sienta del lado de arriba (girado [grados]). */
    private fun DrawScope.cubierto(c: Offset, grados: Float, u: Float, oscuro: Boolean) = rotate(grados, c) {
        // El bajoplato dorado, el plato de loza con su filete rojo y verde y una ramita de acebo pintada.
        drawCircle(Color.Black.copy(alpha = 0.18f), 4.4f * u, c + Offset(0.3f * u, 0.5f * u))
        drawCircle(t(ORO_OSC, oscuro), 4.4f * u, c)
        drawCircle(t(ORO, oscuro), 4.1f * u, c)
        drawArc(t(ORO_LUZ, oscuro), 200f, 90f, false, Offset(c.x - 3.9f * u, c.y - 3.9f * u), Size(7.8f * u, 7.8f * u), style = Stroke(0.35f * u))
        drawCircle(t(NIEVE, oscuro), 3.5f * u, c)
        drawCircle(t(ROJO, oscuro), 3.05f * u, c, style = Stroke(0.4f * u))
        drawCircle(t(VERDE, oscuro), 2.5f * u, c, style = Stroke(0.2f * u))
        drawCircle(t(NIEVE_SOMBRA, oscuro).copy(alpha = 0.5f), 2.2f * u, c)
        acebo(Offset(c.x, c.y - 3.05f * u), 1.3f * u, oscuro)
        // El tenedor a un lado y el cuchillo al otro, de plata.
        val pl = t(PLATA, oscuro)
        drawLine(pl, Offset(c.x - 5.6f * u, c.y - 0.6f * u), Offset(c.x - 5.6f * u, c.y + 3.6f * u), 0.55f * u, StrokeCap.Round)
        drawRoundRect(pl, Offset(c.x - 6.3f * u, c.y - 3.4f * u), Size(1.4f * u, 2.4f * u), CornerRadius(0.4f * u))
        for (k in 0 until 3) drawLine(pl, Offset(c.x - 6.1f * u + k * 0.5f * u, c.y - 3.4f * u), Offset(c.x - 6.1f * u + k * 0.5f * u, c.y - 4.6f * u), 0.25f * u, StrokeCap.Round)
        drawLine(pl, Offset(c.x + 5.6f * u, c.y + 0.2f * u), Offset(c.x + 5.6f * u, c.y + 3.6f * u), 0.6f * u, StrokeCap.Round)
        drawRoundRect(pl, Offset(c.x + 5.2f * u, c.y - 4f * u), Size(1f * u, 4.4f * u), CornerRadius(0.5f * u))
        drawLine(Color.White.copy(alpha = 0.6f), Offset(c.x + 5.4f * u, c.y - 3.6f * u), Offset(c.x + 5.4f * u, c.y), 0.15f * u)
        // La copa de vino tinto, arriba a la derecha.
        val cp = Offset(c.x + 5.9f * u, c.y - 6f * u)
        drawCircle(Color.Black.copy(alpha = 0.15f), 1.5f * u, cp + Offset(0.3f * u, 0.4f * u))
        drawCircle(Color.White.copy(alpha = if (oscuro) 0.25f else 0.7f), 1.5f * u, cp)
        drawCircle(t(Color(0xFF7A1828), oscuro), 1.05f * u, cp)
        drawCircle(Color.White.copy(alpha = 0.8f), 0.3f * u, cp - Offset(0.5f * u, 0.5f * u))
    }

    /** Un petardo navideño visto desde arriba: el tubo de papel brillante, sus anillos dorados y los extremos rizados. */
    private fun DrawScope.petardo(c: Offset, grados: Float, color: Color, u: Float, oscuro: Boolean) = rotate(grados, c) {
        val l = 4.2f * u; val g = 1f * u
        drawRect(Color.Black.copy(alpha = 0.18f), Offset(c.x - l, c.y - g + 0.4f * u), Size(2 * l, 2 * g))
        listOf(-1f, 1f).forEach { s ->
            val p = Path().apply {
                moveTo(c.x + s * l * 0.62f, c.y - g * 0.6f)
                lineTo(c.x + s * l, c.y - g * 1.2f); lineTo(c.x + s * l * 0.92f, c.y - g * 0.4f); lineTo(c.x + s * l * 1.04f, c.y)
                lineTo(c.x + s * l * 0.92f, c.y + g * 0.4f); lineTo(c.x + s * l, c.y + g * 1.2f)
                lineTo(c.x + s * l * 0.62f, c.y + g * 0.6f); close()
            }
            drawPath(p, t(color, oscuro))
        }
        drawRoundRect(t(color, oscuro), Offset(c.x - l * 0.62f, c.y - g), Size(l * 1.24f, 2 * g), CornerRadius(g * 0.3f))
        drawRect(Color.White.copy(alpha = 0.3f), Offset(c.x - l * 0.62f, c.y - g * 0.75f), Size(l * 1.24f, g * 0.35f))
        listOf(-0.62f, 0.62f).forEach { f -> drawRect(t(ORO, oscuro), Offset(c.x + f * l - g * 0.2f, c.y - g * 0.75f), Size(g * 0.4f, g * 1.5f)) }
        drawPath(estrella(c, g * 0.7f, 5, 0.45f), t(ORO, oscuro))
    }

    /** Un candelero de latón visto desde arriba con su vela roja y una corona de acebo alrededor; de noche, encendido. */
    private fun DrawScope.candelero(c: Offset, u: Float, oscuro: Boolean) {
        for (k in 0 until 6) {
            val a = k * 60f + 15f
            val p = Offset(c.x + 3.1f * u * cos(Math.toRadians(a.toDouble())).toFloat(), c.y + 3.1f * u * sin(Math.toRadians(a.toDouble())).toFloat())
            hojaAcebo(p, 2.4f * u, a, t(if (k % 2 == 0) VERDE else ABETO_OSC, oscuro), t(ABETO_LUZ, oscuro))
        }
        for (k in 0 until 3) {
            val a = Math.toRadians(k * 120.0 + 75.0)
            val b = Offset(c.x + 3.3f * u * cos(a).toFloat(), c.y + 3.3f * u * sin(a).toFloat())
            drawCircle(t(ROJO, oscuro), 0.6f * u, b)
            drawCircle(Color.White.copy(alpha = 0.6f), 0.2f * u, b - Offset(0.2f * u, 0.2f * u))
        }
        drawCircle(Color.Black.copy(alpha = 0.25f), 2.7f * u, c + Offset(0.4f * u, 0.6f * u))
        drawCircle(t(ORO_OSC, oscuro), 2.7f * u, c)
        drawCircle(t(ORO, oscuro), 2.4f * u, c)
        drawArc(t(ORO_LUZ, oscuro), 190f, 100f, false, Offset(c.x - 2.1f * u, c.y - 2.1f * u), Size(4.2f * u, 4.2f * u), style = Stroke(0.35f * u))
        drawCircle(t(ORO_OSC, oscuro), 1.6f * u, c)
        drawCircle(t(ROJO, oscuro), 1.15f * u, c)
        drawCircle(t(ROJO_LUZ, oscuro), 0.8f * u, c - Offset(0.15f * u, 0.15f * u))
        if (oscuro) {
            halo(c, 9 * u, LLAMA.copy(alpha = 0.3f))
            drawCircle(Color(0xFFFFA726), 0.7f * u, c)
            drawCircle(Color(0xFFFFF3C4), 0.38f * u, c)
        } else drawCircle(Color.Black.copy(alpha = 0.7f), 0.2f * u, c)
    }

    override fun DrawScope.muebleSocial(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val n = nicho(MuebleVida.SOCIAL)
        // El piso de tablas y la alfombra roja con su cenefa verde y dorada.
        tablas(Rect(0f, 0f, w, h), oscuro, e.semilla)
        val alf = Rect(w * 0.016f, h * 0.025f, w * 0.984f, h * 0.975f)
        sombra(alf, 1.2f * u, 0.25f)
        drawRect(t(ALFOMBRA, oscuro), alf.topLeft, alf.size)
        val cen = alf.deflate(1.4f * u)
        drawRect(t(ABETO, oscuro), cen.topLeft, cen.size, style = Stroke(2f * u))
        drawRect(t(ORO, oscuro), cen.deflate(1.3f * u).topLeft, cen.deflate(1.3f * u).size, style = Stroke(0.35f * u))
        drawRect(t(ORO, oscuro), cen.inflate(1.3f * u).topLeft, cen.inflate(1.3f * u).size, style = Stroke(0.35f * u))
        drawRect(t(NIEVE, oscuro).copy(alpha = 0.8f), cen.topLeft, cen.size, style = Stroke(0.5f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.6f * u, 1.8f * u))))
        drawRect(t(ALFOMBRA_OSC, oscuro), alf.topLeft, alf.size, style = Stroke(0.5f * u))
        // Las sillas, metidas bajo la mesa: tres por lado y una en cada cabecera.
        val mesa = Rect(w * 0.055f, h * 0.085f, w * 0.945f, h * 0.915f)
        val xs = listOf(0.3f, 0.5f, 0.7f)
        xs.forEachIndexed { i, f ->
            silla(Offset(w * f, mesa.top - 1.4f * u), 0f, u, oscuro, e.semilla + i)
            silla(Offset(w * f, mesa.bottom + 1.4f * u), 180f, u, oscuro, e.semilla + 10 + i)
        }
        silla(Offset(mesa.left - 1.4f * u, h / 2), -90f, u, oscuro, e.semilla + 20)
        silla(Offset(mesa.right + 1.4f * u, h / 2), 90f, u, oscuro, e.semilla + 21)
        // La mesa de nogal con su moldura: la sombra, la veta, el canto oscuro y el bisel de luz.
        sombra(mesa, 2.4f * u, if (oscuro) 0.5f else 0.35f)
        drawRoundRect(t(NOGAL_OSC, oscuro), mesa.topLeft, mesa.size, CornerRadius(2.4f * u))
        val tab = mesa.deflate(0.7f * u)
        drawRoundRect(t(NOGAL, oscuro), tab.topLeft, tab.size, CornerRadius(2f * u))
        vetas(tab, t(NOGAL_VETA, oscuro).copy(alpha = 0.5f), e.semilla + 30, densidad = 0.7f, grosor = 0.6f, ondulacion = 1.4f)
        drawRoundRect(t(NOGAL_LUZ, oscuro), tab.deflate(0.6f * u).topLeft, tab.deflate(0.6f * u).size, CornerRadius(1.6f * u), style = Stroke(0.4f * u))
        // El mantel de lino con su puntilla en el borde y la cenefa bordada en punto de cruz.
        val m = Rect(w * 0.072f, h * 0.11f, w * 0.928f, h * 0.89f)
        sombra(m, 1.2f * u, 0.3f)
        val enc = t(CREMA, oscuro)
        var x = m.left
        while (x <= m.right) { drawCircle(enc, 0.9f * u, Offset(x, m.top)); drawCircle(enc, 0.9f * u, Offset(x, m.bottom)); x += 1.8f * u }
        var y = m.top
        while (y <= m.bottom) { drawCircle(enc, 0.9f * u, Offset(m.left, y)); drawCircle(enc, 0.9f * u, Offset(m.right, y)); y += 1.8f * u }
        drawRect(mantel(oscuro), m.topLeft, m.size)
        val b1 = m.deflate(1.3f * u)
        drawRect(t(ROJO, oscuro), b1.topLeft, b1.size, style = Stroke(0.45f * u))
        val b2 = m.deflate(2.4f * u)
        drawRect(t(ROJO, oscuro), b2.topLeft, b2.size, style = Stroke(0.7f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.7f * u, 0.7f * u))))
        drawRect(t(VERDE, oscuro), b2.topLeft, b2.size, style = Stroke(0.7f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.7f * u, 0.7f * u), 0.7f * u)))
        val b3 = m.deflate(3.4f * u)
        drawRect(t(VERDE, oscuro), b3.topLeft, b3.size, style = Stroke(0.3f * u))
        // El centro del mantel (el nicho): un adamascado de copos muy suave y su filete dorado con acebo en las esquinas.
        clipRect(n.left, n.top, n.right, n.bottom) {
            val tono = if (oscuro) Color.White.copy(alpha = 0.04f) else ORO_OSC.copy(alpha = 0.08f)
            var yy = n.top + 7 * u; var f = 0
            while (yy < n.bottom) {
                var xx = n.left + (if (f % 2 == 0) 7f else 15f) * u
                while (xx < n.right) { copo(Offset(xx, yy), 1.6f * u, tono, 0.25f * u); xx += 16 * u }
                yy += 10 * u; f++
            }
        }
        drawRect(t(ORO, oscuro), n.inflate(0.8f * u).topLeft, n.inflate(0.8f * u).size, style = Stroke(0.35f * u))
        drawRect(t(ORO, oscuro).copy(alpha = 0.6f), n.inflate(1.6f * u).topLeft, n.inflate(1.6f * u).size, style = Stroke(0.2f * u))
        // Los cubiertos: tres por lado y uno en cada cabecera.
        val yArr = (m.top + n.top) / 2 + 0.4f * u
        val yAba = (m.bottom + n.bottom) / 2 - 0.4f * u
        xs.forEach { f -> cubierto(Offset(w * f, yArr), 0f, u, oscuro); cubierto(Offset(w * f, yAba), 180f, u, oscuro) }
        val xIz = (m.left + n.left) / 2
        val xDe = (m.right + n.right) / 2
        cubierto(Offset(xIz, h / 2), -90f, u, oscuro)
        cubierto(Offset(xDe, h / 2), 90f, u, oscuro)
        // Los petardos entre plato y plato, y los candeleros en las esquinas del mantel.
        listOf(0.4f to ROJO, 0.6f to VERDE).forEachIndexed { i, (f, col) ->
            petardo(Offset(w * f, yArr), if (i == 0) -8f else 8f, col, u, oscuro)
            petardo(Offset(w * f, yAba), if (i == 0) 8f else -8f, if (col == ROJO) VERDE else ROJO, u, oscuro)
        }
        listOf(Offset(xIz, yArr), Offset(xDe, yArr), Offset(xIz, yAba), Offset(xDe, yAba)).forEach { candelero(it, u, oscuro) }
        listOf(n.topLeft, Offset(n.right, n.top), Offset(n.left, n.bottom), n.bottomRight).forEachIndexed { i, c ->
            acebo(c, 2.2f * u, oscuro, giro = listOf(-45f, 45f, -135f, 135f)[i] + 45f)
        }
    }

    // ---------------------------------------------------------------- Mapas: el escritorio de las cartas de Navidad

    /** La palmatoria de latón con su asa y una vela roja, de pie en [p]. */
    private fun DrawScope.palmatoria(p: Offset, s: Float, oscuro: Boolean) {
        sombra(Rect(p.x - s * 0.5f, p.y - s * 0.06f, p.x + s * 0.5f, p.y), s * 0.06f, 0.35f)
        drawCircle(t(ORO_OSC, oscuro), s * 0.12f, Offset(p.x + s * 0.5f, p.y - s * 0.1f), style = Stroke(s * 0.05f))
        drawOval(t(ORO_OSC, oscuro), Offset(p.x - s * 0.5f, p.y - s * 0.14f), Size(s, s * 0.18f))
        drawOval(t(ORO, oscuro), Offset(p.x - s * 0.46f, p.y - s * 0.17f), Size(s * 0.92f, s * 0.15f))
        drawOval(t(ORO_LUZ, oscuro), Offset(p.x - s * 0.3f, p.y - s * 0.16f), Size(s * 0.4f, s * 0.05f))
        oro(Rect(p.x - s * 0.14f, p.y - s * 0.32f, p.x + s * 0.14f, p.y - s * 0.1f), oscuro, s * 0.04f)
        drawOval(t(ORO, oscuro), Offset(p.x - s * 0.22f, p.y - s * 0.36f), Size(s * 0.44f, s * 0.08f))
        vela(Offset(p.x, p.y - s * 0.34f), s * 0.2f, s * 0.95f, ROJO, oscuro)
    }

    /** El tintero de cristal tallado con su tapa de latón y la pluma de ave, de pie en [p]. */
    private fun DrawScope.tintero(p: Offset, s: Float, oscuro: Boolean) {
        sombra(Rect(p.x - s * 0.4f, p.y - s * 0.06f, p.x + s * 0.4f, p.y), s * 0.06f, 0.35f)
        val v = Rect(p.x - s * 0.3f, p.y - s * 0.36f, p.x + s * 0.3f, p.y)
        drawRect(t(Color(0xFF1C2230), oscuro), v.topLeft, v.size)
        drawRect(Color.White.copy(alpha = 0.35f), v.topLeft, v.size, style = Stroke(s * 0.025f))
        listOf(0.33f, 0.66f).forEach { f -> drawLine(Color.White.copy(alpha = 0.22f), Offset(v.left + v.width * f, v.top), Offset(v.left + v.width * f, v.bottom), s * 0.02f) }
        drawRect(Color.White.copy(alpha = 0.3f), Offset(v.left + s * 0.04f, v.top + s * 0.04f), Size(s * 0.06f, v.height - s * 0.08f))
        oro(Rect(p.x - s * 0.2f, v.top - s * 0.09f, p.x + s * 0.2f, v.top), oscuro, s * 0.03f)
        val a = Offset(p.x + s * 0.02f, v.top - s * 0.06f); val b = Offset(p.x + s * 0.5f, v.top - s * 1.05f)
        val pl = Path().apply { moveTo(a.x, a.y); quadraticTo(b.x - s * 0.45f, b.y + s * 0.45f, b.x, b.y); quadraticTo(b.x - s * 0.02f, b.y + s * 0.6f, a.x + s * 0.06f, a.y - s * 0.28f); close() }
        drawPath(pl, t(NIEVE, oscuro))
        drawPath(pl, t(NIEVE_SOMBRA, oscuro), style = Stroke(s * 0.02f))
        drawLine(t(CREMA_OSC, oscuro), a, b, s * 0.025f)
        drawCircle(t(ROJO, oscuro), s * 0.05f, Offset(p.x - s * 0.18f, v.top - s * 0.12f))
    }

    /** Una galleta de jengibre en forma de muñeco sobre su platito, centrada en [p] y de ancho [s]. */
    private fun DrawScope.galleta(p: Offset, s: Float, oscuro: Boolean) {
        drawOval(Color.Black.copy(alpha = 0.25f), Offset(p.x - s * 0.5f, p.y + s * 0.42f), Size(s, s * 0.2f))
        drawOval(t(NIEVE, oscuro), Offset(p.x - s * 0.5f, p.y + s * 0.34f), Size(s, s * 0.22f))
        drawOval(t(ROJO, oscuro), Offset(p.x - s * 0.42f, p.y + s * 0.37f), Size(s * 0.84f, s * 0.15f), style = Stroke(s * 0.025f))
        val g = t(Color(0xFFB0703A), oscuro)
        val gl = t(NIEVE, oscuro)
        drawCircle(g, s * 0.16f, Offset(p.x, p.y - s * 0.28f))
        drawRoundRect(g, Offset(p.x - s * 0.16f, p.y - s * 0.16f), Size(s * 0.32f, s * 0.38f), CornerRadius(s * 0.08f))
        listOf(-1f, 1f).forEach { k ->
            rotate(k * -25f, Offset(p.x + k * s * 0.12f, p.y - s * 0.1f)) { drawRoundRect(g, Offset(p.x + k * s * 0.12f - s * 0.18f * (if (k < 0) 1f else 0f), p.y - s * 0.16f), Size(s * 0.18f, s * 0.11f), CornerRadius(s * 0.05f)) }
            rotate(k * -14f, Offset(p.x + k * s * 0.08f, p.y + s * 0.18f)) { drawRoundRect(g, Offset(p.x + k * s * 0.08f - s * 0.06f, p.y + s * 0.12f), Size(s * 0.12f, s * 0.22f), CornerRadius(s * 0.05f)) }
        }
        drawPath(Path().apply { moveTo(p.x - s * 0.08f, p.y - s * 0.23f); quadraticTo(p.x, p.y - s * 0.17f, p.x + s * 0.08f, p.y - s * 0.23f) }, gl, style = Stroke(s * 0.025f, cap = StrokeCap.Round))
        drawCircle(gl, s * 0.022f, Offset(p.x - s * 0.06f, p.y - s * 0.31f)); drawCircle(gl, s * 0.022f, Offset(p.x + s * 0.06f, p.y - s * 0.31f))
        listOf(-0.05f, 0.05f, 0.14f).forEach { f -> drawCircle(t(if (f < 0.1f) ROJO else VERDE, oscuro), s * 0.03f, Offset(p.x, p.y + s * f)) }
    }

    /** El montón de cartas atadas con cinta roja y su sello de lacre, de pie en [p] y de ancho [s]. */
    private fun DrawScope.cartas(p: Offset, s: Float, oscuro: Boolean) {
        sombra(Rect(p.x - s * 0.5f, p.y - s * 0.1f, p.x + s * 0.5f, p.y), s * 0.06f, 0.35f)
        listOf(Triple(-6f, 0.12f, CREMA_OSC), Triple(4f, 0.06f, PERGAMINO), Triple(-2f, 0f, CREMA)).forEach { (g, dy, col) ->
            val r = Rect(p.x - s * 0.5f, p.y - s * 0.62f - dy * s, p.x + s * 0.5f, p.y - dy * s)
            rotate(g, r.center) {
                drawRect(t(col, oscuro), r.topLeft, r.size)
                drawPath(Path().apply { moveTo(r.left, r.top); lineTo(r.center.x, r.top + r.height * 0.5f); lineTo(r.right, r.top) }, t(CREMA_OSC, oscuro), style = Stroke(s * 0.02f))
                drawRect(t(ROJO, oscuro), Offset(r.right - s * 0.2f, r.top + s * 0.06f), Size(s * 0.13f, s * 0.15f))
                drawRect(t(NIEVE, oscuro), Offset(r.right - s * 0.18f, r.top + s * 0.08f), Size(s * 0.09f, s * 0.11f), style = Stroke(s * 0.012f))
            }
        }
        val top = p.y - s * 0.62f
        drawRect(t(ROJO, oscuro), Offset(p.x - s * 0.05f, top), Size(s * 0.1f, s * 0.62f))
        drawRect(t(ROJO, oscuro), Offset(p.x - s * 0.5f, top + s * 0.27f), Size(s, s * 0.08f))
        drawCircle(t(ROJO_OSC, oscuro), s * 0.12f, Offset(p.x, top + s * 0.31f))
        drawPath(estrella(Offset(p.x, top + s * 0.31f), s * 0.13f, 9, 0.8f), t(ROJO_OSC, oscuro))
        drawCircle(t(ROJO, oscuro), s * 0.08f, Offset(p.x, top + s * 0.31f))
        drawPath(estrella(Offset(p.x, top + s * 0.31f), s * 0.06f, 5, 0.45f), t(ROJO_OSC, oscuro))
    }

    /** La bola de nieve sobre su peana de nogal, de pie en [p]: el cristal con el abeto, la nieve y el cielo, y su brillo. */
    private fun DrawScope.bolaNieve(p: Offset, r: Float, oscuro: Boolean) {
        val c = Offset(p.x, p.y - r * 0.55f - r)
        sombra(Rect(p.x - r * 0.9f, p.y - r * 0.1f, p.x + r * 0.9f, p.y), r * 0.1f, 0.35f)
        // La peana.
        val pea = Path().apply { moveTo(p.x - r * 0.95f, p.y); lineTo(p.x + r * 0.95f, p.y); lineTo(p.x + r * 0.72f, p.y - r * 0.6f); lineTo(p.x - r * 0.72f, p.y - r * 0.6f); close() }
        drawPath(pea, t(NOGAL, oscuro))
        drawRect(t(ORO, oscuro), Offset(p.x - r * 0.84f, p.y - r * 0.32f), Size(r * 1.68f, r * 0.08f))
        drawPath(Path().apply { moveTo(p.x + r * 0.3f, p.y); lineTo(p.x + r * 0.95f, p.y); lineTo(p.x + r * 0.72f, p.y - r * 0.6f); lineTo(p.x + r * 0.2f, p.y - r * 0.6f); close() }, Color.Black.copy(alpha = 0.25f))
        // El interior: cielo, nieve, el abeto con su estrella y los copos.
        clipPath(Path().apply { addOval(Rect(c.x - r, c.y - r, c.x + r, c.y + r)) }) {
            drawRect(Brush.verticalGradient(listOf(if (oscuro) Color(0xFF1A2440) else CIELO, if (oscuro) Color(0xFF2C3A5A) else Color(0xFFE2ECF2)), c.y - r, c.y + r), Offset(c.x - r, c.y - r), Size(2 * r, 2 * r))
            drawOval(t(NIEVE, oscuro), Offset(c.x - r * 1.2f, c.y + r * 0.35f), Size(r * 2.4f, r * 1.2f))
            listOf(0.8f, 0.55f, 0.32f).forEachIndexed { i, f ->
                val ty = c.y + r * 0.45f - i * r * 0.32f
                drawPath(Path().apply { moveTo(c.x - r * f * 0.6f, ty); lineTo(c.x + r * f * 0.6f, ty); lineTo(c.x, ty - r * 0.5f); close() }, t(if (i % 2 == 0) ABETO else ABETO_LUZ, oscuro))
            }
            drawPath(estrella(Offset(c.x, c.y - r * 0.55f), r * 0.14f, 5, 0.45f), t(ORO, oscuro))
            for (k in 0 until 9) drawCircle(Color.White.copy(alpha = 0.9f), r * 0.045f, Offset(c.x - r * 0.75f + r * 1.5f * azar(3, k), c.y - r * 0.8f + r * 1.2f * azar(4, k)))
        }
        drawCircle(Color.White.copy(alpha = 0.5f), r, c, style = Stroke(r * 0.05f))
        drawArc(Color.White.copy(alpha = 0.75f), 200f, 60f, false, Offset(c.x - r * 0.78f, c.y - r * 0.78f), Size(r * 1.56f, r * 1.56f), style = Stroke(r * 0.1f, cap = StrokeCap.Round))
        drawCircle(Color.White.copy(alpha = 0.7f), r * 0.07f, Offset(c.x + r * 0.45f, c.y - r * 0.5f))
    }

    /** Una pata de cabriolé de nogal con su rodilla y su zapata dorada, de [y0] a [y1], en [x] (hacia fuera según [lado]). */
    private fun DrawScope.pataCabriole(x: Float, y0: Float, y1: Float, an: Float, lado: Float, oscuro: Boolean, semilla: Int) {
        val l = y1 - y0
        val p = Path().apply {
            moveTo(x - an * 0.5f, y0)
            lineTo(x + an * 0.5f, y0)
            cubicTo(x + an * 0.5f + lado * an * 0.4f, y0 + l * 0.3f, x + an * 0.05f - lado * an * 0.2f, y0 + l * 0.6f, x + an * 0.18f + lado * an * 0.35f, y0 + l * 0.92f)
            lineTo(x - an * 0.3f + lado * an * 0.35f, y0 + l * 0.92f)
            cubicTo(x - an * 0.4f - lado * an * 0.15f, y0 + l * 0.6f, x - an * 0.5f + lado * an * 0.35f, y0 + l * 0.3f, x - an * 0.5f, y0)
            close()
        }
        drawPath(p, t(NOGAL, oscuro))
        clipPath(p) {
            vetas(Rect(x - an, y0, x + an, y1), t(NOGAL_VETA, oscuro).copy(alpha = 0.5f), semilla, vertical = true, grosor = 0.6f)
            drawRect(Color.Black.copy(alpha = 0.25f), Offset(x + an * 0.05f, y0), Size(an, l))
        }
        // La concha tallada en la rodilla y la zapata dorada.
        drawOval(t(ORO, oscuro).copy(alpha = 0.85f), Offset(x - an * 0.25f, y0 + l * 0.08f), Size(an * 0.5f, l * 0.16f))
        oro(Rect(x - an * 0.36f + lado * an * 0.35f, y0 + l * 0.9f, x + an * 0.26f + lado * an * 0.35f, y1), oscuro, an * 0.12f)
    }

    override fun DrawScope.muebleMapas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val n = nicho(MuebleVida.MAPAS)
        val canto = h * 0.765f
        // La sombra en el suelo y las patas de cabriolé.
        sombra(Rect(w * 0.03f, h * 0.965f, w * 0.97f, h * 0.99f), 3 * u, if (oscuro) 0.5f else 0.25f)
        listOf(w * 0.055f to -1f, w * 0.945f to 1f).forEachIndexed { i, (x, lado) -> pataCabriole(x, h * 0.88f, h * 0.995f, w * 0.05f, lado, oscuro, e.semilla + 30 + i) }
        // El faldón de nogal con su cajón y los dados de las esquinas con su estrella.
        val fal = Rect(w * 0.025f, h * 0.81f, w * 0.975f, h * 0.89f)
        nogal(fal, oscuro, e.semilla + 1)
        drawRect(Color.Black.copy(alpha = 0.3f), fal.topLeft, Size(fal.width, 1.2f * u))
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(fal.left, fal.bottom - 0.8f * u), Size(fal.width, 0.8f * u))
        val caj = Rect(w * 0.38f, fal.top + 1.6f * u, w * 0.62f, fal.bottom - 1.4f * u)
        drawRect(t(NOGAL_LUZ, oscuro), caj.topLeft, caj.size)
        vetas(caj, t(NOGAL_VETA, oscuro).copy(alpha = 0.4f), e.semilla + 4, densidad = 0.6f, grosor = 0.5f)
        drawRect(t(NOGAL_OSC, oscuro), caj.topLeft, caj.size, style = Stroke(0.5f * u))
        drawRect(t(ORO, oscuro), caj.deflate(0.9f * u).topLeft, caj.deflate(0.9f * u).size, style = Stroke(0.3f * u))
        drawCircle(t(ORO_OSC, oscuro), 1.3f * u, caj.center + Offset(0.2f * u, 0.3f * u))
        drawCircle(t(ORO, oscuro), 1.1f * u, caj.center)
        drawCircle(t(ORO_LUZ, oscuro), 0.4f * u, caj.center - Offset(0.3f * u, 0.3f * u))
        listOf(fal.left, fal.right - w * 0.06f).forEach { xx ->
            val d = Rect(xx, fal.top, xx + w * 0.06f, fal.bottom)
            drawRect(t(NOGAL_LUZ, oscuro), d.topLeft, d.size)
            drawRect(t(ORO, oscuro), d.deflate(0.8f * u).topLeft, d.deflate(0.8f * u).size, style = Stroke(0.3f * u))
            estrellaOro(d.center, 2.2f * u, oscuro)
        }
        // El canto del tablero: la moldura de luz, el filete dorado y la sombra de debajo.
        val ct = Rect(w * 0.01f, canto, w * 0.99f, h * 0.81f)
        nogal(ct, oscuro, e.semilla + 2, color = NOGAL_LUZ)
        drawRect(Color.White.copy(alpha = 0.18f), ct.topLeft, Size(ct.width, 0.7f * u))
        drawRect(t(ORO, oscuro), Offset(ct.left, ct.center.y), Size(ct.width, 0.4f * u))
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(ct.left, ct.bottom - 0.6f * u), Size(ct.width, 0.6f * u))
        // La guirnalda de abeto en tres festones a lo largo del faldón, con un lazo rojo en cada punto de donde cuelga.
        val puntos = listOf(0.07f, 0.36f, 0.64f, 0.93f).map { Offset(w * it, ct.bottom - 0.4f * u) }
        for (k in 0 until 3) guirnalda(puntos[k], puntos[k + 1], h * 0.035f, 3.4f * u, oscuro, e.semilla + k * 13)
        puntos.forEach { lazo(it, 5.2f * u, ROJO, oscuro) }
        // El tablero visto desde arriba: el nogal en hojas casadas, el filete de latón y el paño verde con hierros dorados.
        val tab = Rect(w * 0.01f, h * 0.015f, w * 0.99f, canto)
        nogal(tab, oscuro, e.semilla + 5)
        drawRect(t(ORO, oscuro), tab.deflate(1.4f * u).topLeft, tab.deflate(1.4f * u).size, style = Stroke(0.35f * u))
        val cu = Rect(tab.left + w * 0.035f, tab.top + 3.2f * u, tab.right - w * 0.035f, tab.bottom - 3.2f * u)
        drawRect(t(ABETO, oscuro), cu.topLeft, cu.size)
        for (k in 0 until 10) {
            val c = Offset(cu.left + cu.width * azar(e.semilla, k), cu.top + cu.height * azar(e.semilla, k + 20))
            drawCircle(Brush.radialGradient(listOf(t(ABETO_OSC, oscuro).copy(alpha = 0.5f), Color.Transparent), c, 12 * u), 12 * u, c)
        }
        drawRect(t(ORO, oscuro), cu.deflate(1.4f * u).topLeft, cu.deflate(1.4f * u).size, style = Stroke(0.45f * u))
        drawRect(t(ORO, oscuro).copy(alpha = 0.7f), cu.deflate(2.4f * u).topLeft, cu.deflate(2.4f * u).size, style = Stroke(0.25f * u))
        listOf(cu.topLeft, Offset(cu.right, cu.top), Offset(cu.left, cu.bottom), cu.bottomRight).forEach { c ->
            copo(Offset(c.x + (if (c.x < w / 2) 2.6f else -2.6f) * u, c.y + (if (c.y < h / 2) 2.6f else -2.6f) * u), 1.4f * u, t(ORO, oscuro), 0.35f * u)
        }
        // El mapa de pergamino: un poco más grande que el nicho, con su sombra y una ramita de acebo en cada esquina.
        val pg = n.inflate(2.6f * u)
        sombra(pg, 2.2f * u, if (oscuro) 0.5f else 0.3f)
        drawRect(if (oscuro) Color(0xFF3E3424) else PERGAMINO_OSC, pg.topLeft, pg.size)
        drawRect(pergaminoMapa(oscuro), n.inflate(1.2f * u).topLeft, n.inflate(1.2f * u).size)
        drawRect(t(ROJO, oscuro).copy(alpha = 0.6f), n.inflate(1.2f * u).topLeft, n.inflate(1.2f * u).size, style = Stroke(0.3f * u))
        listOf(pg.topLeft to -45f, Offset(pg.right, pg.top) to 45f, Offset(pg.left, pg.bottom) to -135f, pg.bottomRight to 135f).forEach { (c, g) ->
            acebo(c + Offset(if (c.x < w / 2) 1.2f * u else -1.2f * u, if (c.y < h / 2) 1.2f * u else -1.2f * u), 2.6f * u, oscuro, giro = g + 45f)
        }
        // A un lado, la vela en su palmatoria y las cartas; al otro, la bola de nieve y un regalo.
        val izq = (cu.left + pg.left) / 2
        val der = (cu.right + pg.right) / 2
        val col = (pg.left - cu.left) * 0.86f
        palmatoria(Offset(izq, cu.top + 24 * u), col.coerceAtMost(12 * u), oscuro)
        tintero(Offset(izq, cu.top + 44 * u), col.coerceAtMost(12 * u), oscuro)
        galleta(Offset(der, cu.top + 37 * u), col.coerceAtMost(12 * u), oscuro)
        cartas(Offset(izq, cu.bottom - 3 * u), col.coerceAtMost(13 * u), oscuro)
        bolaNieve(Offset(der, cu.top + 22 * u), (col * 0.46f).coerceAtMost(6 * u), oscuro)
        regalo(Offset(der, cu.bottom - 3 * u), col.coerceAtMost(11 * u) * 0.9f, col.coerceAtMost(11 * u) * 0.75f, ROJO, ORO, oscuro)
    }

    // ---------------------------------------------------------------- Otras vidas: el aparador de nogal

    override fun DrawScope.muebleOtrasVidas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val n = nicho(MuebleVida.OTRAS_VIDAS)
        // La sombra en el suelo y las patas de bollo.
        sombra(Rect(w * 0.02f, h * 0.95f, w * 0.98f, h * 0.99f), 3 * u, if (oscuro) 0.5f else 0.25f)
        listOf(0.05f, 0.35f, 0.65f, 0.95f).forEach { f ->
            val x = w * f
            drawOval(t(NOGAL, oscuro), Offset(x - 3.6f * u, h * 0.93f), Size(7.2f * u, h * 0.055f))
            drawOval(Color.White.copy(alpha = 0.12f), Offset(x - 2.6f * u, h * 0.938f), Size(2.2f * u, h * 0.025f))
            oro(Rect(x - 1.6f * u, h * 0.975f, x + 1.6f * u, h * 0.995f), oscuro, 0.5f * u)
        }
        // El cuerpo de nogal.
        val cuerpo = Rect(w * 0.02f, h * 0.1f, w * 0.98f, h * 0.94f)
        nogal(cuerpo, oscuro, e.semilla)
        // El zócalo: los cinco cajones con su tirador, la moldura y la puntilla de papel que cuelga del borde.
        val zoc = Rect(w * 0.01f, h * 0.835f, w * 0.99f, h * 0.94f)
        nogal(zoc, oscuro, e.semilla + 2, color = NOGAL_LUZ)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(zoc.left, zoc.bottom - 1f * u), Size(zoc.width, 1f * u))
        val paso = n.width / 5
        for (k in 0 until 5) {
            val c = Rect(n.left + paso * k + 1.2f * u, zoc.top + 3.2f * u, n.left + paso * (k + 1) - 1.2f * u, zoc.bottom - 2f * u)
            drawRect(t(NOGAL, oscuro), c.topLeft, c.size)
            vetas(c, t(NOGAL_VETA, oscuro).copy(alpha = 0.4f), e.semilla + 40 + k, densidad = 0.6f, grosor = 0.5f)
            drawRect(t(NOGAL_OSC, oscuro), c.topLeft, c.size, style = Stroke(0.4f * u))
            drawRect(t(ORO, oscuro).copy(alpha = 0.8f), c.deflate(0.8f * u).topLeft, c.deflate(0.8f * u).size, style = Stroke(0.25f * u))
            drawCircle(t(ORO_OSC, oscuro), 1f * u, c.center + Offset(0.2f * u, 0.3f * u))
            drawCircle(t(ORO, oscuro), 0.85f * u, c.center)
            drawCircle(t(ORO_LUZ, oscuro), 0.3f * u, c.center - Offset(0.25f * u, 0.25f * u))
        }
        drawRect(Color.White.copy(alpha = 0.15f), zoc.topLeft, Size(zoc.width, 0.8f * u))
        val pu = t(CREMA, oscuro)
        var x = zoc.left + 0.9f * u
        while (x < zoc.right) {
            drawArc(pu, 0f, 180f, true, Offset(x - 0.9f * u, zoc.top + 0.9f * u - 0.9f * u), Size(1.8f * u, 1.8f * u))
            drawCircle(t(NOGAL_OSC, oscuro), 0.25f * u, Offset(x, zoc.top + 1.2f * u))
            x += 1.8f * u
        }
        drawRect(pu, Offset(zoc.left, zoc.top), Size(zoc.width, 0.9f * u))
        // Las columnas de bastón de caramelo a los lados.
        val cw = w * 0.042f
        listOf(w * 0.018f, w * 0.982f - cw).forEach { xx -> baston(Rect(xx, h * 0.17f, xx + cw, h * 0.835f), oscuro) }
        // El nicho: cinco ventanitas de adviento con su arco dorado y su alféizar nevado, separadas por columnillas de nogal.
        drawRect(t(NOGAL_OSC, oscuro), n.inflate(1.2f * u).topLeft, n.inflate(1.2f * u).size)
        drawRect(forro(oscuro), n.topLeft, n.size)
        for (k in 0 until 5) {
            val c = Rect(n.left + paso * k, n.top, n.left + paso * (k + 1), n.bottom)
            val r = c.width * 0.4f
            val arco = Path().apply {
                moveTo(c.center.x - r, c.bottom); lineTo(c.center.x - r, c.top + r + 2.2f * u)
                arcTo(Rect(c.center.x - r, c.top + 2.2f * u, c.center.x + r, c.top + 2.2f * u + 2 * r), 180f, 180f, false)
                lineTo(c.center.x + r, c.bottom); close()
            }
            drawPath(arco, if (oscuro) Color.Black.copy(alpha = 0.16f) else ORO_OSC.copy(alpha = 0.07f))
            // Unas rayas muy suaves del papel pintado dentro del arco.
            clipPath(arco) {
                var xx = c.center.x - r + 2.4f * u
                while (xx < c.center.x + r) { drawLine(if (oscuro) Color.White.copy(alpha = 0.03f) else ORO_OSC.copy(alpha = 0.07f), Offset(xx, c.top), Offset(xx, c.bottom), 0.6f * u); xx += 4.8f * u }
            }
            drawPath(arco, t(ORO, oscuro).copy(alpha = if (oscuro) 0.45f else 0.6f), style = Stroke(0.4f * u))
            // La estrellita de la clave del arco.
            drawPath(estrella(Offset(c.center.x, c.top + 2.2f * u), 1.3f * u, 5, 0.45f), t(ORO, oscuro))
        }
        sombraInterior(n, 4 * u, if (oscuro) 0.35f else 0.14f)
        // El alféizar: el listón de nogal con su canto dorado y la nieve encima.
        val rep = Rect(n.left, n.bottom - 2.4f * u, n.right, n.bottom)
        nogal(rep, oscuro, e.semilla + 4, color = NOGAL_LUZ)
        drawRect(t(ORO, oscuro), Offset(rep.left, rep.bottom - 0.4f * u), Size(rep.width, 0.4f * u))
        val nieve = Path().apply {
            moveTo(rep.left, rep.top + 0.6f * u)
            var xx = rep.left
            var k = 0
            while (xx < rep.right) { val nx = (xx + 3.2f * u).coerceAtMost(rep.right); quadraticTo((xx + nx) / 2, rep.top - (0.9f + 0.6f * azar(e.semilla, k)) * u, nx, rep.top + 0.4f * u); xx = nx; k++ }
            lineTo(rep.right, rep.top + 1.1f * u); lineTo(rep.left, rep.top + 1.1f * u); close()
        }
        drawPath(nieve, t(NIEVE, oscuro))
        drawPath(nieve, t(NIEVE_SOMBRA, oscuro), style = Stroke(0.25f * u))
        for (k in 1 until 5) {
            val xx = n.left + paso * k
            columnilla(Rect(xx - 1.8f * u, n.top, xx + 1.8f * u, n.bottom), oscuro, e.semilla + 20 + k)
        }
        // El friso de nogal con su filete y la guirnalda de abeto en cinco festones, con lazos y luces.
        val fr = Rect(w * 0.01f, h * 0.075f, w * 0.99f, h * 0.175f)
        nogal(fr, oscuro, e.semilla + 6)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(fr.left, fr.bottom - 0.8f * u), Size(fr.width, 0.8f * u))
        drawRect(t(ORO, oscuro), Offset(fr.left, fr.bottom - 1.6f * u), Size(fr.width, 0.5f * u))
        val ganchos = (0..5).map { Offset(n.left + paso * it, fr.top + 1.4f * u) }
        for (k in 0 until 5) guirnalda(ganchos[k], ganchos[k + 1], h * 0.03f, 3.2f * u, oscuro, e.semilla + k * 11)
        ganchos.forEachIndexed { i, g -> if (i != 0 && i != 5) lazo(g, 4.6f * u, ROJO, oscuro) }
        // La cornisa: el listón oscuro con sus dentículos y la moldura de arriba.
        val den = Rect(0f, h * 0.045f, w, h * 0.075f)
        drawRect(t(NOGAL_OSC, oscuro), den.topLeft, den.size)
        x = den.left + 0.6f * u
        while (x < den.right) { drawRect(t(NOGAL_LUZ, oscuro), Offset(x, den.top), Size(1.6f * u, den.height * 0.8f)); x += 2.8f * u }
        val tope = Rect(0f, 0f, w, h * 0.045f)
        nogal(tope, oscuro, e.semilla + 8, color = NOGAL_LUZ)
        drawRect(Color.White.copy(alpha = 0.2f), tope.topLeft, Size(w, 0.7f * u))
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(0f, tope.bottom - 0.7f * u), Size(w, 0.7f * u))
        // Los extremos de la guirnalda caen por las esquinas, con su lazo.
        listOf(ganchos[0], ganchos[5]).forEach { lazo(it, 5f * u, ROJO, oscuro) }
        // La estrella dorada en lo alto, con su halo.
        val es = Offset(w / 2, h * 0.06f)
        halo(es, if (oscuro) 13 * u else 10 * u, LLAMA.copy(alpha = if (oscuro) 0.5f else 0.3f))
        estrellaOro(es, 5f * u, false)
    }

    // ---------------------------------------------------------------- Íconos de mapas

    override fun DrawScope.iconoMapa(id: String, e: EstadoIcono, oscuro: Boolean) {
        val w = size.width; val h = size.height
        val s = h / 70f * 60f
        val c = Offset(w / 2, h / 2)
        val u = s / 60f
        when (id) {
            "unesco" -> {
                // La iglesia nevada: el suelo de nieve, dos abetos, la nave con su tejado nevado, el rosetón, la puerta y el
                // campanario con su aguja y su estrella.
                val base = c.y + s * 0.44f
                drawOval(t(NIEVE, oscuro), Offset(c.x - s * 1.05f, base - 3 * u), Size(s * 2.1f, 7 * u))
                drawOval(t(NIEVE_SOMBRA, oscuro), Offset(c.x - s * 0.9f, base + 0.4f * u), Size(s * 1.8f, 2.6f * u))
                listOf(-1f, 1f).forEach { k ->
                    val ax = c.x + k * s * 0.78f
                    listOf(0, 1, 2).forEach { i ->
                        val ty = base - i * 5.2f * u
                        val an = (8f - i * 2.2f) * u
                        val tri = Path().apply { moveTo(ax - an, ty); lineTo(ax + an, ty); lineTo(ax, ty - 8 * u); close() }
                        drawPath(tri, if (i % 2 == 0) e.c2 else mezcla(e.c2, Color.Black, 0.2f))
                        drawPath(Path().apply { moveTo(ax - an * 0.6f, ty - 3 * u); lineTo(ax, ty - 8 * u); lineTo(ax + an * 0.2f, ty - 6.6f * u); close() }, t(NIEVE, oscuro))
                    }
                }
                val nave = Rect(c.x - s * 0.46f, c.y - s * 0.08f, c.x + s * 0.22f, base)
                drawRect(t(CREMA, oscuro), nave.topLeft, nave.size)
                drawRect(t(CREMA_OSC, oscuro), Offset(nave.right - 3 * u, nave.top), Size(3 * u, nave.height))
                for (k in 1 until 4) drawLine(t(CREMA_OSC, oscuro), Offset(nave.left, nave.top + nave.height * k / 4), Offset(nave.right, nave.top + nave.height * k / 4), 0.4f * u)
                drawRect(t(CREMA_OSC, oscuro), nave.topLeft, nave.size, style = Stroke(0.5f * u))
                // El tejado rojo de la nave, de faldón inclinado, con la nieve que lo cubre y cuelga del alero.
                val tej = Path().apply { moveTo(nave.left - 2.4f * u, nave.top + 0.6f * u); lineTo(nave.left + 5 * u, nave.top - s * 0.24f); lineTo(nave.right - 1 * u, nave.top - s * 0.24f); lineTo(nave.right + 2.4f * u, nave.top + 0.6f * u); close() }
                drawPath(tej, e.c1)
                for (k in 1 until 4) { val yy = nave.top + 0.6f * u - (s * 0.24f + 0.6f * u) * k / 4; drawLine(mezcla(e.c1, Color.Black, 0.25f), Offset(nave.left - 2.4f * u + 7.4f * u * k / 4, yy), Offset(nave.right + 2.4f * u - 3.4f * u * k / 4, yy), 0.4f * u) }
                val niev = Path().apply {
                    moveTo(nave.left + 4.6f * u, nave.top - s * 0.24f - 0.8f * u); lineTo(nave.right - 0.6f * u, nave.top - s * 0.24f - 0.8f * u)
                    lineTo(nave.right + 1.2f * u, nave.top - s * 0.1f)
                    quadraticTo(nave.right - 3 * u, nave.top - s * 0.04f, nave.center.x + 3 * u, nave.top - s * 0.09f)
                    quadraticTo(nave.center.x - 1 * u, nave.top - s * 0.02f, nave.center.x - 5 * u, nave.top - s * 0.08f)
                    quadraticTo(nave.left + 2 * u, nave.top - s * 0.03f, nave.left - 0.4f * u, nave.top - s * 0.06f)
                    close()
                }
                drawPath(niev, t(NIEVE, oscuro))
                drawPath(niev, t(NIEVE_SOMBRA, oscuro), style = Stroke(0.4f * u))
                val luz = if (oscuro) Color(0xFFFFD27A) else Color(0xFFF2C46B)
                listOf(0.2f, 0.42f).forEach { f ->
                    val vx = nave.left + nave.width * f
                    val v = Rect(vx - 1.6f * u, nave.top + 4 * u, vx + 1.6f * u, nave.top + 10 * u)
                    if (oscuro) halo(v.center, 5 * u, luz.copy(alpha = 0.45f))
                    drawRoundRect(luz, v.topLeft, v.size, CornerRadius(1.6f * u, 1.6f * u))
                    drawLine(t(NOGAL_OSC, oscuro), Offset(vx, v.top), Offset(vx, v.bottom), 0.4f * u)
                }
                val pu = Rect(nave.left + nave.width * 0.62f, base - 11 * u, nave.left + nave.width * 0.86f, base)
                drawRoundRect(t(NOGAL, oscuro), pu.topLeft, pu.size, CornerRadius(pu.width / 2, pu.width / 2))
                drawLine(t(NOGAL_OSC, oscuro), Offset(pu.center.x, pu.top + 1 * u), Offset(pu.center.x, pu.bottom), 0.4f * u)
                acebo(Offset(pu.center.x, pu.top + 2.4f * u), 2.6f * u, oscuro)
                // El campanario: la torre, la campana, el reloj y la aguja nevada.
                val to = Rect(c.x + s * 0.18f, c.y - s * 0.3f, c.x + s * 0.44f, base)
                drawRect(t(CREMA, oscuro), to.topLeft, to.size)
                drawRect(t(CREMA_OSC, oscuro), Offset(to.right - 2.6f * u, to.top), Size(2.6f * u, to.height))
                val va = Rect(to.left + 3 * u, to.top + 2.4f * u, to.right - 3 * u, to.top + 10 * u)
                drawRoundRect(t(NOGAL_OSC, oscuro), va.topLeft, va.size, CornerRadius(va.width / 2, va.width / 2))
                drawPath(Path().apply { moveTo(va.center.x - 2.2f * u, va.bottom - 1 * u); quadraticTo(va.center.x - 2 * u, va.top + 2 * u, va.center.x, va.top + 2 * u); quadraticTo(va.center.x + 2 * u, va.top + 2 * u, va.center.x + 2.2f * u, va.bottom - 1 * u); close() }, t(ORO, oscuro))
                val ro = Offset(to.center.x, to.top + 16 * u)
                if (oscuro) halo(ro, 6 * u, luz.copy(alpha = 0.4f))
                drawCircle(luz, 3 * u, ro)
                for (k in 0 until 8) { val a = k * Math.PI / 4; drawLine(t(NOGAL_OSC, oscuro), ro, Offset(ro.x + 3 * u * cos(a).toFloat(), ro.y + 3 * u * sin(a).toFloat()), 0.35f * u) }
                drawCircle(t(NOGAL_OSC, oscuro), 3 * u, ro, style = Stroke(0.5f * u))
                val ag = Path().apply { moveTo(to.left - 1.4f * u, to.top); lineTo(to.center.x, to.top - s * 0.3f); lineTo(to.right + 1.4f * u, to.top); close() }
                drawPath(ag, e.c1)
                drawPath(Path().apply { moveTo(to.left - 1.4f * u, to.top); lineTo(to.center.x, to.top - s * 0.3f); lineTo(to.center.x - 0.6f * u, to.top - 2 * u); close() }, t(NIEVE, oscuro))
                drawRect(t(NIEVE, oscuro), Offset(to.left - 1.6f * u, to.top - 0.6f * u), Size(to.width + 3.2f * u, 1.6f * u))
                estrellaOro(Offset(to.center.x, to.top - s * 0.3f - 2 * u), 3 * u, oscuro)
                for (k in 0 until 10) drawCircle(Color.White.copy(alpha = if (oscuro) 0.8f else 0.95f), 0.5f * u, Offset(c.x - s * 1f + s * 2f * azar(9, k), c.y - s * 0.5f + s * 0.6f * azar(10, k)))
            }
            "rascar" -> {
                // La tarjeta de felicitación con su doble filete, la capa plateada por rascar (ya raspada en una franja) y la
                // moneda de chocolate envuelta en oro que la raspa.
                val m = Rect(c.x - s * 0.62f, c.y - s * 0.42f, c.x + s * 0.5f, c.y + s * 0.42f)
                drawRect(Color.Black.copy(alpha = 0.25f), m.topLeft + Offset(1.2f * u, 1.6f * u), m.size)
                drawRect(t(CREMA, oscuro), m.topLeft, m.size)
                drawRect(e.c1, m.deflate(1.6f * u).topLeft, m.deflate(1.6f * u).size, style = Stroke(0.8f * u))
                drawRect(e.c2, m.deflate(3f * u).topLeft, m.deflate(3f * u).size, style = Stroke(0.5f * u))
                val capa = m.deflate(5 * u)
                drawRect(t(PLATA, oscuro), capa.topLeft, capa.size)
                clipRect(capa.left, capa.top, capa.right, capa.bottom) {
                    var k = capa.left - capa.height
                    while (k < capa.right) { drawLine(Color.White.copy(alpha = 0.35f), Offset(k, capa.bottom), Offset(k + capa.height, capa.top), 0.5f * u); k += 2.4f * u }
                    for (j in 0 until 6) copo(Offset(capa.left + capa.width * azar(7, j), capa.top + capa.height * azar(8, j)), 1.4f * u, Color.White.copy(alpha = 0.6f), 0.3f * u)
                    val raspa = Path().apply { moveTo(capa.left, capa.top + capa.height * 0.35f); cubicTo(capa.left + capa.width * 0.3f, capa.top - capa.height * 0.1f, capa.left + capa.width * 0.5f, capa.bottom, capa.right, capa.top + capa.height * 0.55f) }
                    drawPath(raspa, e.fondo, style = Stroke(capa.height * 0.42f, cap = StrokeCap.Round))
                    drawPath(raspa, e.c2.copy(alpha = 0.85f), style = Stroke(capa.height * 0.24f, cap = StrokeCap.Round))
                }
                acebo(Offset(m.left + 3.4f * u, m.top + 3.4f * u), 3.6f * u, oscuro, giro = 30f)
                val mc = Offset(m.right + s * 0.02f, m.bottom - s * 0.12f)
                val mr = s * 0.24f
                drawCircle(Color.Black.copy(alpha = 0.3f), mr, mc + Offset(1.2f * u, 1.6f * u))
                // El papel dorado con su borde rizado.
                drawPath(estrella(mc, mr, 28, 0.93f), t(ORO_OSC, oscuro))
                drawCircle(t(ORO, oscuro), mr * 0.86f, mc)
                drawCircle(t(ORO_OSC, oscuro), mr * 0.7f, mc, style = Stroke(mr * 0.05f))
                drawArc(t(ORO_LUZ, oscuro), 200f, 100f, false, Offset(mc.x - mr * 0.78f, mc.y - mr * 0.78f), Size(mr * 1.56f, mr * 1.56f), style = Stroke(mr * 0.1f))
                drawPath(estrella(mc, mr * 0.42f, 5, 0.45f), t(ORO_OSC, oscuro))
                drawPath(estrella(mc - Offset(mr * 0.03f, mr * 0.03f), mr * 0.36f, 5, 0.45f), t(ORO_LUZ, oscuro))
            }
            else -> {
                // Tus lugares: tres abetos con su estrella sobre el mapa nevado, unidos por la doble huella del trineo.
                val m = Rect(c.x - s * 0.95f, c.y + s * 0.08f, c.x + s * 0.95f, c.y + s * 0.46f)
                val mp = Path().apply { moveTo(m.left + 4 * u, m.top); lineTo(m.right, m.top + 1 * u); lineTo(m.right - 4 * u, m.bottom); lineTo(m.left, m.bottom - 1 * u); close() }
                drawPath(mp, Color.Black.copy(alpha = 0.22f), style = Stroke(2 * u))
                drawPath(mp, t(NIEVE, oscuro))
                clipPath(mp) { for (k in 0 until 4) drawOval(t(NIEVE_SOMBRA, oscuro).copy(alpha = 0.6f), Offset(m.left + m.width * (k * 0.27f), m.top + m.height * (0.4f + 0.2f * (k % 2))), Size(m.width * 0.3f, m.height * 0.6f)) }
                drawPath(mp, t(NIEVE_SOMBRA, oscuro), style = Stroke(0.6f * u))
                val pts = listOf(Offset(c.x - s * 0.62f, m.top + m.height * 0.6f), Offset(c.x, m.top + m.height * 0.38f), Offset(c.x + s * 0.62f, m.top + m.height * 0.62f))
                listOf(-0.7f, 0.7f).forEach { dy ->
                    val ruta = Path().apply { moveTo(pts[0].x, pts[0].y + dy * u); quadraticTo(c.x - s * 0.3f, m.bottom + dy * u, pts[1].x, pts[1].y + dy * u); quadraticTo(c.x + s * 0.3f, m.top + dy * u, pts[2].x, pts[2].y + dy * u) }
                    drawPath(ruta, e.c1, style = Stroke(0.6f * u, cap = StrokeCap.Round, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2.4f * u, 1.4f * u))))
                }
                pts.forEachIndexed { i, p ->
                    val alto = s * (if (i == 1) 0.6f else 0.5f)
                    drawOval(Color.Black.copy(alpha = 0.3f), Offset(p.x - 5 * u, p.y - 1 * u), Size(10 * u, 2.4f * u))
                    drawRect(t(NOGAL, oscuro), Offset(p.x - 1 * u, p.y - 3 * u), Size(2 * u, 3 * u))
                    for (j in 0 until 3) {
                        val ty = p.y - 2.4f * u - j * alto * 0.26f
                        val an = alto * (0.34f - j * 0.08f)
                        val tri = Path().apply { moveTo(p.x - an, ty); lineTo(p.x + an, ty); lineTo(p.x, ty - alto * 0.38f); close() }
                        drawPath(tri, if (j % 2 == 0) e.c2 else mezcla(e.c2, Color.White, 0.12f))
                        drawPath(Path().apply { moveTo(p.x + an * 0.1f, ty); lineTo(p.x + an, ty); lineTo(p.x, ty - alto * 0.38f); close() }, Color.Black.copy(alpha = 0.18f))
                        drawCircle(e.c1, 0.9f * u, Offset(p.x - an * 0.45f, ty - 1 * u))
                        drawCircle(t(ORO, oscuro), 0.8f * u, Offset(p.x + an * 0.5f, ty - 1.4f * u))
                    }
                    estrellaOro(Offset(p.x, p.y - 2.4f * u - alto * 0.52f - alto * 0.38f + 1 * u), 2.6f * u, oscuro)
                }
            }
        }
    }

    // ---------------------------------------------------------------- Notas: el panel de papel pintado de la sala

    /** Una corona de puerta de abeto con bolas, acebo y un lazo rojo abajo, centrada en [c] y de radio [r]. */
    private fun DrawScope.corona(c: Offset, r: Float, oscuro: Boolean, semilla: Int) {
        drawCircle(Color.Black.copy(alpha = 0.25f), r * 1.1f, c + Offset(r * 0.06f, r * 0.12f), style = Stroke(r * 0.5f))
        drawCircle(t(ABETO_OSC, oscuro), r, c, style = Stroke(r * 0.5f))
        for (k in 0 until 26) {
            val a = k * 6.283f / 26
            val p = Offset(c.x + r * cos(a), c.y + r * sin(a))
            for (j in 0 until 3) {
                val g = a + 1.57f + (azar(semilla, k * 3 + j) - 0.5f) * 1.4f
                val l = r * (0.22f + 0.12f * azar(semilla, k * 3 + j + 90))
                drawLine(t(if (j == 1) ABETO_LUZ else ABETO, oscuro), p, Offset(p.x + l * cos(g), p.y + l * sin(g)), r * 0.07f, StrokeCap.Round)
            }
        }
        for (k in 0 until 6) {
            val a = Math.toRadians(-90.0 + k * 50.0 - 125.0)
            val p = Offset(c.x + r * cos(a).toFloat(), c.y + r * sin(a).toFloat())
            val col = if (k % 2 == 0) ORO else ROJO
            drawCircle(t(mezcla(col, Color.Black, 0.3f), oscuro), r * 0.15f, p)
            drawCircle(t(col, oscuro), r * 0.12f, p - Offset(r * 0.02f, r * 0.02f))
            drawCircle(Color.White.copy(alpha = 0.6f), r * 0.04f, p - Offset(r * 0.05f, r * 0.05f))
        }
        acebo(Offset(c.x, c.y - r), r * 0.42f, oscuro)
        lazo(Offset(c.x, c.y + r * 0.95f), r * 1.1f, ROJO, oscuro)
    }

    override fun DrawScope.tableroNotas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val n = nicho(MuebleVida.NOTAS)
        // El marco de nogal con su moldura, la sombra y el filete dorado.
        val marco = Rect(w * 0.012f, h * 0.085f, w * 0.988f, h * 0.9f)
        sombra(marco, 3 * u, if (oscuro) 0.5f else 0.25f)
        nogal(marco, oscuro, e.semilla)
        drawRect(Color.White.copy(alpha = 0.14f), marco.topLeft, Size(marco.width, 0.8f * u))
        drawRect(Color.White.copy(alpha = 0.1f), marco.topLeft, Size(0.8f * u, marco.height))
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(marco.right - 0.8f * u, marco.top), Size(0.8f * u, marco.height))
        val med = Rect((marco.left + n.left) / 2, (marco.top + n.top) / 2, (marco.right + n.right) / 2, (marco.bottom + n.bottom) / 2)
        drawRect(t(ORO, oscuro).copy(alpha = 0.8f), med.topLeft, med.size, style = Stroke(0.3f * u))
        // El papel pintado de rayas (crema de día, verde abeto de noche), con un ramito entre raya y raya.
        drawRect(t(NOGAL_OSC, oscuro), n.inflate(1.2f * u).topLeft, n.inflate(1.2f * u).size)
        drawRect(t(ORO, oscuro), n.inflate(0.6f * u).topLeft, n.inflate(0.6f * u).size, style = Stroke(0.4f * u))
        drawRect(forro(oscuro), n.topLeft, n.size)
        clipRect(n.left, n.top, n.right, n.bottom) {
            val raya = if (oscuro) Color.White.copy(alpha = 0.035f) else ORO_OSC.copy(alpha = 0.1f)
            val punto = if (oscuro) ROJO.copy(alpha = 0.18f) else ROJO.copy(alpha = 0.16f)
            var x = n.left + 4 * u
            var k = 0
            while (x < n.right) {
                drawLine(raya, Offset(x, n.top), Offset(x, n.bottom), 0.8f * u)
                drawLine(raya, Offset(x + 1.4f * u, n.top), Offset(x + 1.4f * u, n.bottom), 0.3f * u)
                var y = n.top + (if (k % 2 == 0) 5f else 12f) * u
                while (y < n.bottom) { drawCircle(punto, 0.6f * u, Offset(x + 5.2f * u, y)); y += 14 * u }
                x += 10.4f * u; k++
            }
        }
        sombraInterior(n, 3 * u, if (oscuro) 0.3f else 0.12f)
        // Los dos cordeles de panadero, a lo ancho, a la altura de las pinzas de cada fila, con sus clavos dorados.
        val hueco = n.width * 0.03f
        val ch = (n.height - hueco) / 2f
        val cwn = (n.width - 2 * hueco) / 3f
        listOf(n.top, n.top + ch + hueco).forEach { fy ->
            val y = fy + 5.5f * cwn / 100f
            drawLine(Color.Black.copy(alpha = 0.2f), Offset(n.left, y + 0.6f * u), Offset(n.right, y + 0.6f * u), 0.5f * u)
            drawLine(t(NIEVE, oscuro), Offset(n.left, y), Offset(n.right, y), 0.55f * u)
            drawLine(t(ROJO, oscuro), Offset(n.left, y), Offset(n.right, y), 0.55f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.9f * u, 0.9f * u)))
            listOf(n.left - 2.2f * u, n.right + 2.2f * u).forEach { xx ->
                drawLine(t(ROJO, oscuro), Offset(xx, y), Offset(if (xx < n.left) n.left else n.right, y), 0.55f * u)
                drawCircle(Color.Black.copy(alpha = 0.3f), 1.1f * u, Offset(xx + 0.3f * u, y + 0.4f * u))
                drawCircle(t(ORO_OSC, oscuro), 1.1f * u, Offset(xx, y))
                drawCircle(t(ORO, oscuro), 0.8f * u, Offset(xx, y))
                drawCircle(t(ORO_LUZ, oscuro), 0.3f * u, Offset(xx - 0.25f * u, y - 0.25f * u))
            }
        }
        // La repisa de abajo con dos velas y cuatro calcetines colgados.
        val rep = Rect(w * 0.005f, h * 0.9f, w * 0.995f, h * 0.935f)
        nogal(rep, oscuro, e.semilla + 2, color = NOGAL_LUZ)
        drawRect(Color.White.copy(alpha = 0.2f), rep.topLeft, Size(rep.width, 0.6f * u))
        drawRect(t(ORO, oscuro), Offset(rep.left, rep.bottom - 0.9f * u), Size(rep.width, 0.4f * u))
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(rep.left, rep.bottom), Size(rep.width, 0.6f * u))
        listOf(0.2f, 0.4f, 0.6f, 0.8f).forEachIndexed { i, f -> calcetin(Offset(w * f, rep.bottom + 1f * u), h * 0.05f, if (i % 2 == 0) ROJO else VERDE, oscuro) }
        // La guirnalda a lo largo del marco de arriba, que cae por las esquinas, y la corona de puerta al centro.
        val gy = marco.top + 0.8f * u
        guirnalda(Offset(marco.left + 1.2f * u, gy), Offset(w / 2, gy), h * 0.022f, 3.4f * u, oscuro, e.semilla + 7)
        guirnalda(Offset(w / 2, gy), Offset(marco.right - 1.2f * u, gy), h * 0.022f, 3.4f * u, oscuro, e.semilla + 9)
        listOf(marco.left + 1.2f * u, marco.right - 1.2f * u).forEach { lazo(Offset(it, gy), 5f * u, ROJO, oscuro) }
        corona(Offset(w / 2, h * 0.065f), 4.6f * u, oscuro, e.semilla)
        // Las velas de la repisa, delante de todo.
        listOf((marco.left + n.left) / 2, (marco.right + n.right) / 2).forEach { x ->
            oro(Rect(x - 2.2f * u, rep.top - 1.2f * u, x + 2.2f * u, rep.top), oscuro, 0.4f * u)
            vela(Offset(x, rep.top - 1.2f * u), 2f * u, 6f * u, ROJO, oscuro)
        }
    }

    /** La cartulina de la tarjeta: crema de día; de noche, más tostada bajo la luz de las velas (la tinta se lee en las dos). */
    private fun cartulina(oscuro: Boolean) = if (oscuro) Color(0xFFD9CCAE) else Color(0xFFFBF5E8)

    /** Un bastón de caramelo de [alto], con la curva arriba, de pie en [p]. */
    private fun DrawScope.bastoncito(p: Offset, alto: Float) {
        val g = alto * 0.16f
        val ruta = Path().apply { moveTo(p.x, p.y); lineTo(p.x, p.y - alto * 0.72f); cubicTo(p.x, p.y - alto * 1.02f, p.x + alto * 0.46f, p.y - alto * 1.02f, p.x + alto * 0.46f, p.y - alto * 0.72f) }
        drawPath(ruta, Color.Black.copy(alpha = 0.25f), style = Stroke(g, cap = StrokeCap.Round), alpha = 1f)
        drawPath(ruta, NIEVE, style = Stroke(g, cap = StrokeCap.Round))
        drawPath(ruta, ROJO, style = Stroke(g, pathEffect = PathEffect.dashPathEffect(floatArrayOf(g * 0.7f, g * 0.7f))))
    }

    override fun DrawScope.notaTablero(e: EstadoNota, oscuro: Boolean) {
        val r = e.rect
        val u = r.width / 100f
        val p = Rect(r.left + 4 * u, r.top + 5.5f * u, r.right - 4 * u, r.bottom - 2 * u)
        // La sombra, la cartulina (un poco más oscura abajo) y su relieve.
        sombra(p, 3 * u, if (oscuro) 0.5f else 0.28f)
        drawRect(cartulina(oscuro), p.topLeft, p.size)
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, CREMA_OSC.copy(alpha = 0.4f)), p.top + p.height * 0.6f, p.bottom), Offset(p.left, p.top + p.height * 0.6f), Size(p.width, p.height * 0.4f))
        drawRect(Color.White.copy(alpha = 0.4f), p.topLeft, Size(p.width, 0.8f * u))
        // El doble filete rojo y verde de la tarjeta de felicitación, con un copo en relieve en la esquina de arriba.
        val f1 = p.deflate(3.2f * u)
        drawRect(ROJO.copy(alpha = 0.85f), f1.topLeft, f1.size, style = Stroke(1f * u))
        drawRect(VERDE.copy(alpha = 0.7f), f1.deflate(1.8f * u).topLeft, f1.deflate(1.8f * u).size, style = Stroke(0.5f * u))
        listOf(f1.topLeft, Offset(f1.right, f1.top), Offset(f1.left, f1.bottom), f1.bottomRight).forEach { drawCircle(ORO, 1.1f * u, it) }
        // El detalle del tipo, abajo a la derecha (fuera del texto).
        val d = Offset(p.right - 10 * u, p.bottom - 10 * u)
        when (e.tipo) {
            "idea" -> {
                // La estrella dorada con su destello.
                halo(d, 9 * u, LLAMA.copy(alpha = 0.45f))
                estrellaOro(d, 5.4f * u, false)
            }
            "pensamiento" -> scale(1.5f, d) {
                // Una ramita de acebo.
                acebo(d, 5 * u, false, giro = -20f)
            }
            "proposito" -> {
                // La campana dorada con su lazo rojo.
                val cb = Path().apply { moveTo(d.x - 5 * u, d.y + 4 * u); quadraticTo(d.x - 4 * u, d.y + 3 * u, d.x - 3.4f * u, d.y - 1 * u); quadraticTo(d.x - 3 * u, d.y - 5 * u, d.x, d.y - 5 * u); quadraticTo(d.x + 3 * u, d.y - 5 * u, d.x + 3.4f * u, d.y - 1 * u); quadraticTo(d.x + 4 * u, d.y + 3 * u, d.x + 5 * u, d.y + 4 * u); close() }
                drawPath(cb, Color.Black.copy(alpha = 0.2f), style = Stroke(1.2f * u))
                drawPath(cb, ORO)
                drawPath(Path().apply { moveTo(d.x - 1.6f * u, d.y - 4 * u); quadraticTo(d.x - 2.8f * u, d.y, d.x - 3.4f * u, d.y + 3.4f * u) }, ORO_LUZ, style = Stroke(0.9f * u, cap = StrokeCap.Round))
                drawRect(ORO_OSC, Offset(d.x - 5 * u, d.y + 3.4f * u), Size(10 * u, 1.2f * u))
                drawCircle(ORO_OSC, 1.2f * u, Offset(d.x, d.y + 5.2f * u))
                lazo(Offset(d.x, d.y - 5.4f * u), 6 * u, ROJO, false)
            }
            else -> {
                // El recordatorio: un bastón de caramelo.
                rotate(-15f, d) { bastoncito(Offset(d.x - 1.5f * u, d.y + 6 * u), 12 * u) }
            }
        }
        // Arriba al centro: el lazo de raso con su cascabel si está fijada; si no, la pinza de madera que la prende del cordel.
        val s = Offset(p.center.x, r.top + 5.5f * u)
        if (e.fijada) {
            val l = Offset(s.x, r.top + 4 * u)
            lazo(l, 16 * u, ROJO, false)
            drawCircle(Color.Black.copy(alpha = 0.25f), 2.2f * u, Offset(l.x + 0.4f * u, l.y + 5f * u))
            drawCircle(ORO_OSC, 2.2f * u, Offset(l.x, l.y + 4.6f * u))
            drawCircle(ORO, 1.8f * u, Offset(l.x, l.y + 4.4f * u))
            drawLine(ORO_OSC, Offset(l.x - 1.5f * u, l.y + 4.8f * u), Offset(l.x + 1.5f * u, l.y + 4.8f * u), 0.4f * u)
            drawCircle(ORO_LUZ, 0.55f * u, Offset(l.x - 0.6f * u, l.y + 3.7f * u))
        } else {
            val pz = Rect(s.x - 2.4f * u, r.top + 0.6f * u, s.x + 2.4f * u, r.top + 13 * u)
            drawRect(Color.Black.copy(alpha = 0.25f), pz.topLeft + Offset(0.6f * u, 0.8f * u), pz.size)
            drawRoundRect(Color(0xFFC9A26A), pz.topLeft, Size(pz.width / 2, pz.height), CornerRadius(0.6f * u))
            drawRoundRect(Color(0xFFB08550), Offset(pz.center.x, pz.top), Size(pz.width / 2, pz.height), CornerRadius(0.6f * u))
            drawLine(Color(0xFF7A5634), Offset(pz.center.x, pz.top), Offset(pz.center.x, pz.bottom), 0.3f * u)
            drawRect(Color(0xFFA8ADB2), Offset(pz.left - 0.3f * u, pz.top + pz.height * 0.42f), Size(pz.width + 0.6f * u, 1.6f * u))
            drawRect(Color.White.copy(alpha = 0.5f), Offset(pz.left - 0.3f * u, pz.top + pz.height * 0.42f), Size(pz.width + 0.6f * u, 0.4f * u))
            drawLine(Color.White.copy(alpha = 0.3f), Offset(pz.left + 0.6f * u, pz.top + 1 * u), Offset(pz.left + 0.6f * u, pz.bottom - 1 * u), 0.4f * u)
        }
    }

    override fun tintaNota(oscuro: Boolean): Color = TINTA
}
