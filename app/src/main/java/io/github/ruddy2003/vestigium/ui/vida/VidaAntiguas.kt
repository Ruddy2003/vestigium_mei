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
 * Tierras Antiguas: la casa de piedra de una compañía de viaje, con los materiales de su armario del Ideario y de su
 * biblioteca: muros de sillares grises, madera oscura con veta, flejes y clavos de hierro, oro gastado, gemas (esmeralda,
 * rubí, zafiro), vitela, faroles encendidos y runas inventadas talladas en la piedra.
 * - Social: el salón de la compañía. El dintel con su banda de runas de oro y sus dentículos, dos pilares de sillares con
 *   capitel y basa, de los que cuelgan los estandartes (verde y rubí, con su runa) y los faroles; al centro, la crónica de
 *   la compañía (el nicho): una gran hoja de vitela en su marco de madera tallada con anillos entrelazados, escuadras de oro
 *   y gemas engastadas; abajo, el banco largo con las jarras, el yelmo, la copa y los mapas enrollados, sobre el piso de losas.
 * - Mapas: la mesa del cartógrafo vista desde arriba: tablero de tablas con su canto moldurado y escuadras de hierro, el mapa
 *   de vitela clavado (el nicho); a la izquierda, el tintero de cuerno con su pluma, los mapas enrollados con cinta roja y la
 *   bolsa de gemas; a la derecha, la brújula en su caja de latón, la vela en su palmatoria, el compás de puntas y las
 *   monedas; abajo, el faldón con dientes tallados, la cerradura y los tiradores, y las patas torneadas.
 * - Otras vidas: la cámara del tesoro: dintel de runas con la gran esmeralda engastada, cinco hornacinas de medio punto
 *   talladas en el muro (el nicho) con su gema en la clave, columnillas con anillo de oro, antorchas a los lados, la repisa
 *   con runas y el zócalo con sus tableros rúnicos.
 * - Íconos: la puerta de la montaña, el mapa de vitela rascado con una moneda rúnica, y tres gemas engastadas sobre un
 *   camino punteado.
 * - Notas: el tablón de la posada: la corona de madera tallada con su esmeralda y sus runas, los postes con flejes de hierro,
 *   el fondo de tablas claras (el nicho) y la repisa con ménsulas. Cada nota es una página del cronista: vitela de borde
 *   barbado con nudos en las esquinas, clavada; su tipo se ve en la esquina (la esmeralda, la luna, la rosa de los vientos o
 *   la cinta roja con la esquina doblada) y, si está fijada, lleva un clavo de oro y un sello de lacre con cintas.
 */
internal object VidaAntiguas : DisenoVida {
    private val PIEDRA = Color(0xFF8C867B)
    private val PIEDRA_LUZ = Color(0xFFB0A99B)
    private val PIEDRA_OSC = Color(0xFF5F5A52)
    private val JUNTA = Color(0xFF34312C)
    private val MADERA = Color(0xFF5A3A22)
    private val MADERA_LUZ = Color(0xFF7E5636)
    private val MADERA_OSC = Color(0xFF33210F)
    private val FRESNO = Color(0xFFD9C29A)
    private val HIERRO = Color(0xFF2A2A2E)
    private val HIERRO_LUZ = Color(0xFF75757C)
    private val ORO = Color(0xFFC9A04A)
    private val ORO_LUZ = Color(0xFFEFD48C)
    private val ORO_OSC = Color(0xFF7E5F22)
    private val ESMERALDA = Color(0xFF2E7A5A)
    private val RUBI = Color(0xFF8E1B24)
    private val ZAFIRO = Color(0xFF2B4A8C)
    private val TERCIOPELO = Color(0xFF1F4A38)
    private val VITELA = Color(0xFFEDE2C6)
    private val VITELA_OSC = Color(0xFFCDB98C)
    private val CUERO = Color(0xFF8A5A32)
    private val FAROL = Color(0xFFF2C46B)
    private val FAROL_LUZ = Color(0xFFFFF0C2)
    private val CERA = Color(0xFFF2E3B6)
    private val TINTA = Color(0xFF1E1D1A)
    private val NOCHE = Color(0xFF0B0B0D)

    // ---------------------------------------------------------------- Ayudas

    /** Un número estable de 0 a 1: cada piedra y cada veta tienen sus irregularidades, siempre las mismas. */
    private fun azar(semilla: Int, k: Int): Float {
        var x = semilla * 374761393 + k * 668265263
        x = (x xor (x ushr 13)) * 1274126177
        x = x xor (x ushr 16)
        return (x and 0x7fffffff) / 2147483647f
    }

    private fun mezcla(a: Color, b: Color, f: Float): Color = lerp(a, b, f.coerceIn(0f, 1f))

    /** El material de día o de noche: de noche, la piedra y la madera en penumbra de verdad. */
    private fun t(c: Color, oscuro: Boolean, cuanto: Float = 0.5f): Color = if (oscuro) mezcla(c, NOCHE, cuanto) else c

    /** El color sólido de un nicho: el material llevado a la luminancia de la tarjeta. */
    private fun nicho(e: EstadoMueble, oscuro: Boolean, material: Color): Color =
        if (oscuro) mezcla(e.fondo, material, 0.12f) else mezcla(e.fondo, material, 0.42f)

    private fun DrawScope.caja(r: Rect) = Rect(size.width * r.left, size.height * r.top, size.width * r.right, size.height * r.bottom)

    private fun Rect.crece(d: Float) = Rect(left - d, top - d, right + d, bottom + d)

    /** Una mancha radial suave (la luz de un farol). */
    private fun DrawScope.halo(c: Offset, r: Float, color: Color) =
        drawCircle(Brush.radialGradient(listOf(color, color.copy(alpha = 0f)), c, r.coerceAtLeast(1f)), r, c)

    /** Vetas de madera: líneas onduladas a lo largo, separadas ≈ [paso]. */
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
                    val x = if (vertical) r.left + v else r.left + a
                    val y = if (vertical) r.top + a else r.top + v
                    if (s == 0) p.moveTo(x, y) else p.lineTo(x, y)
                }
                drawPath(p, color.copy(alpha = color.alpha * (0.3f + 0.7f * azar(semilla, k + 400))), style = Stroke(grosor * (0.5f + azar(semilla, k + 500))))
            }
        }
    }

    /** Una tabla de madera con veta, luz arriba y sombra abajo. */
    private fun DrawScope.tabla(r: Rect, base: Color, oscuro: Boolean, semilla: Int, u: Float, vertical: Boolean = false) {
        drawRect(t(base, oscuro), r.topLeft, r.size)
        vetas(r, t(MADERA_OSC, oscuro).copy(alpha = 0.5f), semilla, 1.3f * u, vertical, 0.35f * u)
        drawRect(Color.White.copy(alpha = if (oscuro) 0.06f else 0.14f), r.topLeft, Size(r.width, 0.35f * u))
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(r.left, r.bottom - 0.35f * u), Size(r.width, 0.35f * u))
    }

    /** Un clavo o un remache de cabeza redonda, con su brillo. */
    private fun DrawScope.clavo(c: Offset, r: Float, oscuro: Boolean, metal: Color = HIERRO, luz: Color = HIERRO_LUZ) {
        drawCircle(Color.Black.copy(alpha = 0.35f), r, c + Offset(r * 0.35f, r * 0.45f))
        drawCircle(t(metal, oscuro, 0.3f), r, c)
        drawCircle(t(luz, oscuro, 0.3f), r * 0.42f, c - Offset(r * 0.3f, r * 0.3f))
    }

    /** El bisel de una piedra: luz arriba y a la izquierda, sombra abajo y a la derecha. */
    private fun DrawScope.bisel(r: Rect, g: Float, oscuro: Boolean) {
        drawRect(Color.White.copy(alpha = if (oscuro) 0.07f else 0.2f), r.topLeft, Size(r.width, g))
        drawRect(Color.White.copy(alpha = if (oscuro) 0.04f else 0.1f), r.topLeft, Size(g, r.height))
        drawRect(Color.Black.copy(alpha = 0.28f), Offset(r.left, r.bottom - g), Size(r.width, g))
        drawRect(Color.Black.copy(alpha = 0.18f), Offset(r.right - g, r.top), Size(g, r.height))
    }

    /** Un muro de sillares: hiladas de piedras de largo y tono distintos, con juntas alternadas, bisel y poros. */
    private fun DrawScope.sillares(r: Rect, alto: Float, oscuro: Boolean, semilla: Int, u: Float) {
        clipRect(r.left, r.top, r.right, r.bottom) {
            drawRect(t(JUNTA, oscuro), r.topLeft, r.size)
            var y = r.top; var fila = 0
            while (y < r.bottom) {
                var x = r.left - (if (fila % 2 == 0) 0f else alto * 1.15f)
                var k = 0
                while (x < r.right) {
                    val largo = alto * (1.8f + 1.2f * azar(semilla + fila, k))
                    val p = Rect(x + 0.3f * u, y + 0.3f * u, x + largo - 0.3f * u, y + alto - 0.3f * u)
                    val tono = mezcla(PIEDRA_OSC, PIEDRA_LUZ, 0.2f + 0.55f * azar(semilla + 50 + fila, k))
                    drawRect(t(tono, oscuro, 0.62f), p.topLeft, p.size)
                    bisel(p, 0.4f * u, oscuro)
                    for (j in 0 until 2) {
                        val pc = Offset(p.left + p.width * azar(semilla + fila * 7, k * 3 + j), p.top + p.height * (0.25f + 0.5f * azar(semilla + fila * 11, k * 5 + j)))
                        drawCircle(Color.Black.copy(alpha = 0.14f), (0.25f + 0.25f * azar(fila, k + j)) * u, pc)
                    }
                    x += largo; k++
                }
                y += alto; fila++
            }
        }
    }

    /** El piso de losas, con las juntas en perspectiva. */
    private fun DrawScope.losas(r: Rect, oscuro: Boolean, u: Float) {
        drawRect(Brush.verticalGradient(listOf(t(PIEDRA_OSC, oscuro, 0.6f), t(mezcla(PIEDRA_OSC, JUNTA, 0.5f), oscuro, 0.6f)), r.top, r.bottom), r.topLeft, r.size)
        val n = 9
        for (j in 0..n) {
            val x = r.left + r.width * j / n
            drawLine(t(JUNTA, oscuro), Offset(x, r.top), Offset(x + (j - n / 2f) * 1.6f * u, r.bottom), 0.35f * u)
        }
        drawLine(t(JUNTA, oscuro), Offset(r.left, r.top + r.height * 0.45f), Offset(r.right, r.top + r.height * 0.45f), 0.35f * u)
        drawLine(Color.White.copy(alpha = if (oscuro) 0.04f else 0.12f), Offset(r.left, r.top + 0.3f * u), Offset(r.right, r.top + 0.3f * u), 0.4f * u)
    }

    /** Una runa inventada: un tallo y uno a tres trazos rectos, siempre los mismos para cada [k]. */
    private fun DrawScope.runa(k: Int, o: Offset, s: Float, color: Color, grosor: Float) {
        drawLine(color, Offset(o.x, o.y - s / 2), Offset(o.x, o.y + s / 2), grosor, StrokeCap.Round)
        val trazos = 1 + k.mod(3)
        for (j in 0 until trazos) {
            val y0 = o.y - s / 2 + s * (0.1f + 0.35f * j)
            val dx = if ((k + j) % 2 == 0) s * 0.4f else -s * 0.4f
            drawLine(color, Offset(o.x, y0), Offset(o.x + dx, y0 + s * (if (k % 4 < 2) 0.3f else -0.2f + 0.5f * (j % 2))), grosor, StrokeCap.Round)
        }
    }

    /** Una banda de runas talladas: la incisión oscura y el oro encima, un poco corrido. */
    private fun DrawScope.bandaRunas(r: Rect, oscuro: Boolean, u: Float, semilla: Int, paso: Float, oro: Color = ORO) {
        val n = (r.width / paso).toInt().coerceAtLeast(1)
        val s = r.height * 0.72f
        for (j in 0 until n) {
            val o = Offset(r.left + r.width * (j + 0.5f) / n, r.center.y)
            runa(j * 5 + semilla, o + Offset(0.25f * u, 0.3f * u), s, Color.Black.copy(alpha = 0.45f), 0.55f * u)
            runa(j * 5 + semilla, o, s, t(oro, oscuro, 0.15f), 0.5f * u)
        }
    }

    /** Un hexágono de punta arriba. */
    private fun hexagono(c: Offset, r: Float): Path = Path().apply {
        for (k in 0 until 6) {
            val a = Math.toRadians(-90.0 + k * 60.0)
            val x = c.x + r * cos(a).toFloat(); val y = c.y + r * sin(a).toFloat()
            if (k == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
    }

    /** Una gema tallada: la mesa clara, las facetas, el brillo; con [engaste], su montura de oro. */
    private fun DrawScope.gema(c: Offset, r: Float, color: Color, oscuro: Boolean, engaste: Boolean = true) {
        if (engaste) {
            drawPath(hexagono(c + Offset(r * 0.2f, r * 0.3f), r * 1.4f), Color.Black.copy(alpha = 0.35f))
            drawPath(hexagono(c, r * 1.4f), t(ORO, oscuro, 0.2f))
            drawPath(hexagono(c, r * 1.4f), t(ORO_OSC, oscuro, 0.2f), style = Stroke(r * 0.12f))
            drawPath(hexagono(c, r * 1.18f), t(ORO_LUZ, oscuro, 0.25f), style = Stroke(r * 0.1f))
        }
        val g = if (oscuro) mezcla(color, Color.White, 0.08f) else color
        drawPath(hexagono(c, r), mezcla(g, Color.Black, 0.25f))
        drawPath(hexagono(c, r * 0.55f), mezcla(g, Color.White, 0.22f))
        for (k in 0 until 6) {
            val a = Math.toRadians(-90.0 + k * 60.0)
            val d = Offset(cos(a).toFloat(), sin(a).toFloat())
            drawLine(Color.Black.copy(alpha = 0.25f), c + d * (r * 0.55f), c + d * r, r * 0.07f)
        }
        // La faceta de arriba a la izquierda, con la luz.
        val a0 = Math.toRadians(-150.0); val a1 = Math.toRadians(-90.0)
        drawPath(Path().apply {
            moveTo(c.x + r * cos(a0).toFloat(), c.y + r * sin(a0).toFloat()); lineTo(c.x + r * cos(a1).toFloat(), c.y + r * sin(a1).toFloat())
            lineTo(c.x + r * 0.55f * cos(a1).toFloat(), c.y + r * 0.55f * sin(a1).toFloat()); lineTo(c.x + r * 0.55f * cos(a0).toFloat(), c.y + r * 0.55f * sin(a0).toFloat()); close()
        }, Color.White.copy(alpha = 0.35f))
        drawCircle(Color.White.copy(alpha = 0.8f), r * 0.14f, c - Offset(r * 0.25f, r * 0.25f))
    }

    /** Anillos entrelazados tallados en una banda de madera, como en el friso de la biblioteca. */
    private fun DrawScope.anillos(r: Rect, oscuro: Boolean, u: Float) {
        val rad = r.height * 0.4f
        val paso = rad * 1.55f
        val n = ((r.width - 2 * rad) / paso).toInt().coerceAtLeast(1)
        val x0 = r.center.x - paso * n / 2
        clipRect(r.left, r.top, r.right, r.bottom) {
            for (k in 0..n) {
                val c = Offset(x0 + paso * k, r.center.y)
                drawCircle(Color.Black.copy(alpha = 0.4f), rad, c + Offset(0.2f * u, 0.3f * u), style = Stroke(0.6f * u))
                drawCircle(t(mezcla(MADERA_LUZ, ORO, 0.35f), oscuro, 0.35f), rad, c, style = Stroke(0.5f * u))
            }
        }
    }

    /** Un contorno de vitela de borde barbado dentro de [r]. */
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

    /** La rosa de los vientos: cuatro puntas largas partidas en luz y sombra, y cuatro cortas. */
    private fun DrawScope.rosa(c: Offset, r: Float, claro: Color, oscuroC: Color, norte: Color) {
        for (k in 0 until 8) {
            val largo = if (k % 2 == 0) r else r * 0.55f
            val a = Math.toRadians(-90.0 + k * 45.0)
            val d = Offset(cos(a).toFloat(), sin(a).toFloat()); val p = Offset(-d.y, d.x)
            val ancho = r * (if (k % 2 == 0) 0.2f else 0.14f)
            drawPath(Path().apply { moveTo(c.x, c.y); lineTo(c.x + d.x * largo, c.y + d.y * largo); lineTo(c.x + p.x * ancho, c.y + p.y * ancho); close() }, if (k == 0) norte else claro)
            drawPath(Path().apply { moveTo(c.x, c.y); lineTo(c.x + d.x * largo, c.y + d.y * largo); lineTo(c.x - p.x * ancho, c.y - p.y * ancho); close() }, oscuroC)
        }
        drawCircle(oscuroC, r * 0.12f, c)
    }

    /** Una llama vista de frente. */
    private fun DrawScope.llama(base: Offset, alto: Float) {
        val p = Path().apply {
            moveTo(base.x, base.y - alto)
            quadraticTo(base.x + alto * 0.45f, base.y - alto * 0.3f, base.x, base.y)
            quadraticTo(base.x - alto * 0.45f, base.y - alto * 0.3f, base.x, base.y - alto)
            close()
        }
        drawPath(p, Color(0xFFF5A62E))
        drawOval(FAROL_LUZ, Offset(base.x - alto * 0.12f, base.y - alto * 0.55f), Size(alto * 0.24f, alto * 0.45f))
    }

    /** Un farol de hierro colgado de su cadena, encendido, con su halo. [s] es el ancho del farol. */
    private fun DrawScope.farol(x: Float, yCadena: Float, yTecho: Float, s: Float, oscuro: Boolean) {
        val hierro = t(HIERRO, oscuro, 0.2f)
        var cy = yCadena
        while (cy < yTecho - s * 0.12f) { drawOval(hierro, Offset(x - s * 0.1f, cy), Size(s * 0.2f, s * 0.28f), style = Stroke(s * 0.06f)); cy += s * 0.22f }
        val cuerpo = Rect(x - s * 0.36f, yTecho + s * 0.22f, x + s * 0.36f, yTecho + s * 1.05f)
        halo(cuerpo.center, s * (if (oscuro) 3.4f else 2.2f), FAROL.copy(alpha = if (oscuro) 0.4f else 0.26f))
        drawCircle(hierro, s * 0.09f, Offset(x, yTecho), style = Stroke(s * 0.06f))
        drawPath(Path().apply { moveTo(x - s * 0.5f, yTecho + s * 0.26f); lineTo(x - s * 0.14f, yTecho + s * 0.06f); lineTo(x + s * 0.14f, yTecho + s * 0.06f); lineTo(x + s * 0.5f, yTecho + s * 0.26f); close() }, hierro)
        drawRect(Brush.verticalGradient(listOf(FAROL_LUZ, FAROL, mezcla(FAROL, ORO_OSC, 0.3f)), cuerpo.top, cuerpo.bottom), cuerpo.topLeft, cuerpo.size)
        llama(Offset(x, cuerpo.bottom - s * 0.14f), s * 0.42f)
        drawLine(hierro, Offset(x, cuerpo.top), Offset(x, cuerpo.bottom), s * 0.07f)
        drawRect(hierro, cuerpo.topLeft, cuerpo.size, style = Stroke(s * 0.1f))
        drawRect(hierro, Offset(cuerpo.left - s * 0.08f, cuerpo.bottom), Size(cuerpo.width + s * 0.16f, s * 0.12f))
        drawPath(Path().apply { moveTo(x - s * 0.2f, cuerpo.bottom + s * 0.12f); lineTo(x, cuerpo.bottom + s * 0.34f); lineTo(x + s * 0.2f, cuerpo.bottom + s * 0.12f); close() }, hierro)
    }

    /** Un estandarte de tela colgado de su barra, de cola partida, con orla de oro, una runa en un círculo y su gema. */
    private fun DrawScope.estandarte(x: Float, arriba: Float, ancho: Float, largo: Float, tela: Color, oscuro: Boolean, u: Float, k: Int) {
        val a = ancho / 2
        fun forma(m: Float) = Path().apply {
            moveTo(x - a + m, arriba + m * 0.5f); lineTo(x + a - m, arriba + m * 0.5f)
            lineTo(x + a - m, arriba + largo - m * 1.6f); lineTo(x, arriba + largo * 0.8f - m * 0.6f); lineTo(x - a + m, arriba + largo - m * 1.6f); close()
        }
        drawPath(forma(0f).apply { translate(Offset(0.8f * u, 1f * u)) }, Color.Black.copy(alpha = 0.35f))
        val f = forma(0f)
        drawPath(f, Brush.horizontalGradient(listOf(t(mezcla(tela, Color.Black, 0.3f), oscuro, 0.35f), t(tela, oscuro, 0.35f), t(mezcla(tela, Color.White, 0.12f), oscuro, 0.35f), t(tela, oscuro, 0.35f), t(mezcla(tela, Color.Black, 0.3f), oscuro, 0.35f)), x - a, x + a))
        // Los pliegues de la tela.
        clipPath(f) {
            listOf(-0.45f, 0.1f, 0.5f).forEach { fx -> drawLine(Color.Black.copy(alpha = 0.16f), Offset(x + a * fx, arriba), Offset(x + a * fx * 1.05f, arriba + largo), 0.5f * u) }
        }
        drawPath(forma(ancho * 0.12f), t(ORO, oscuro, 0.25f), style = Stroke(0.45f * u))
        drawPath(f, t(ORO_OSC, oscuro, 0.25f), style = Stroke(0.4f * u))
        // La runa en su círculo y la gema debajo.
        val c = Offset(x, arriba + largo * 0.32f)
        val rr = ancho * 0.28f
        drawCircle(t(ORO, oscuro, 0.2f), rr, c, style = Stroke(0.5f * u))
        runa(k, c, rr * 1.2f, t(ORO_LUZ, oscuro, 0.2f), 0.55f * u)
        gema(Offset(x, arriba + largo * 0.58f), ancho * 0.1f, if (k % 2 == 0) RUBI else ESMERALDA, oscuro, engaste = true)
        // La barra con sus remates, y las borlas de las puntas.
        drawLine(Color.Black.copy(alpha = 0.35f), Offset(x - a * 1.25f, arriba + 0.5f * u), Offset(x + a * 1.25f, arriba + 0.5f * u), 1f * u)
        drawLine(t(HIERRO, oscuro, 0.2f), Offset(x - a * 1.25f, arriba), Offset(x + a * 1.25f, arriba), 0.9f * u, StrokeCap.Round)
        listOf(-1f, 1f).forEach { s -> clavo(Offset(x + s * a * 1.3f, arriba), 0.75f * u, oscuro, ORO, ORO_LUZ) }
        listOf(x - a, x + a).forEach { bx ->
            val b = Offset(bx, arriba + largo)
            drawLine(t(ORO, oscuro, 0.25f), b, b + Offset(0f, 1.6f * u), 0.4f * u)
            drawOval(t(ORO, oscuro, 0.25f), Offset(b.x - 0.6f * u, b.y + 1.2f * u), Size(1.2f * u, 1.6f * u))
        }
    }

    /** El dintel de piedra: la cornisa, la banda de runas de oro y la fila de dentículos, con su sombra abajo. */
    private fun DrawScope.dintel(r: Rect, oscuro: Boolean, u: Float, semilla: Int) {
        val corn = Rect(r.left, r.top, r.right, r.top + r.height * 0.18f)
        val banda = Rect(r.left, corn.bottom, r.right, r.bottom - r.height * 0.24f)
        val dent = Rect(r.left, banda.bottom, r.right, r.bottom)
        drawRect(Brush.verticalGradient(listOf(t(PIEDRA_LUZ, oscuro, 0.55f), t(PIEDRA, oscuro, 0.55f)), corn.top, corn.bottom), corn.topLeft, corn.size)
        drawRect(Color.Black.copy(alpha = 0.4f), Offset(r.left, corn.bottom - 0.4f * u), Size(r.width, 0.4f * u))
        drawRect(Brush.verticalGradient(listOf(t(PIEDRA_LUZ, oscuro, 0.55f), t(PIEDRA, oscuro, 0.55f), t(PIEDRA_OSC, oscuro, 0.55f)), banda.top, banda.bottom), banda.topLeft, banda.size)
        drawRect(t(JUNTA, oscuro), Offset(r.left, banda.top + banda.height * 0.14f), Size(r.width, 0.3f * u))
        drawRect(t(JUNTA, oscuro), Offset(r.left, banda.bottom - banda.height * 0.14f - 0.3f * u), Size(r.width, 0.3f * u))
        bandaRunas(Rect(r.left, banda.top + banda.height * 0.24f, r.right, banda.bottom - banda.height * 0.24f), oscuro, u, semilla, banda.height * 0.85f)
        drawRect(t(PIEDRA_OSC, oscuro, 0.55f), dent.topLeft, dent.size)
        val paso = dent.height * 1.1f
        var x = r.left + paso * 0.2f
        while (x < r.right) {
            val d = Rect(x, dent.top + dent.height * 0.12f, x + paso * 0.6f, dent.bottom - dent.height * 0.1f)
            drawRect(t(PIEDRA_LUZ, oscuro, 0.55f), d.topLeft, d.size)
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(d.right - d.width * 0.25f, d.top), Size(d.width * 0.25f, d.height))
            x += paso
        }
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.4f), Color.Transparent), r.bottom, r.bottom + 2.4f * u), Offset(r.left, r.bottom), Size(r.width, 2.4f * u))
    }

    /** Un pilar de sillares con capitel y basa, y su sombra en el muro. */
    private fun DrawScope.pilar(r: Rect, oscuro: Boolean, u: Float, semilla: Int) {
        drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.35f), Color.Transparent), r.right, r.right + 3f * u), Offset(r.right, r.top), Size(3f * u, r.height))
        val cap = Rect(r.left - 0.8f * u, r.top, r.right + 0.8f * u, r.top + 4.2f * u)
        val basa = Rect(r.left - 1f * u, r.bottom - 5f * u, r.right + 1f * u, r.bottom)
        val fuste = Rect(r.left, cap.bottom, r.right, basa.top)
        drawRect(t(JUNTA, oscuro), fuste.topLeft, fuste.size)
        var y = fuste.top; var k = 0
        val alto = 5.5f * u
        while (y < fuste.bottom) {
            val p = Rect(fuste.left + 0.25f * u, y + 0.25f * u, fuste.right - 0.25f * u, minOf(y + alto, fuste.bottom) - 0.25f * u)
            drawRect(Brush.horizontalGradient(listOf(t(PIEDRA_LUZ, oscuro, 0.58f), t(mezcla(PIEDRA, PIEDRA_LUZ, azar(semilla, k)), oscuro, 0.58f), t(PIEDRA_OSC, oscuro, 0.58f)), p.left, p.right), p.topLeft, p.size)
            bisel(p, 0.35f * u, oscuro)
            y += alto; k++
        }
        // El capitel: el ábaco, el equino y un anillo de oro.
        drawRect(Brush.verticalGradient(listOf(t(PIEDRA_LUZ, oscuro, 0.55f), t(PIEDRA, oscuro, 0.55f)), cap.top, cap.bottom), cap.topLeft, cap.size)
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(cap.left, cap.top + cap.height * 0.45f), Size(cap.width, 0.3f * u))
        drawRect(t(ORO, oscuro, 0.25f), Offset(r.left, cap.bottom - 0.9f * u), Size(r.width, 0.9f * u))
        drawRect(Color.Black.copy(alpha = 0.4f), Offset(cap.left, cap.bottom), Size(cap.width, 0.4f * u))
        // La basa en dos escalones.
        drawRect(t(PIEDRA, oscuro, 0.55f), Offset(basa.left + 0.8f * u, basa.top), Size(basa.width - 1.6f * u, basa.height * 0.45f))
        drawRect(t(PIEDRA_LUZ, oscuro, 0.55f), Offset(basa.left, basa.top + basa.height * 0.45f), Size(basa.width, basa.height * 0.55f))
        drawRect(Color.White.copy(alpha = if (oscuro) 0.05f else 0.18f), Offset(basa.left, basa.top + basa.height * 0.45f), Size(basa.width, 0.35f * u))
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(basa.left, basa.top + basa.height * 0.45f - 0.3f * u), Size(basa.width, 0.3f * u))
    }

    /** Un soporte de hierro con su farol, clavado en la cara de un pilar. */
    private fun DrawScope.farolDePared(x: Float, y: Float, s: Float, oscuro: Boolean, u: Float) {
        val hierro = t(HIERRO, oscuro, 0.2f)
        drawRect(hierro, Offset(x - s * 0.25f, y - s * 0.55f), Size(s * 0.5f, s * 0.22f))
        clavo(Offset(x, y - s * 0.44f), 0.45f * u, oscuro)
        drawLine(hierro, Offset(x, y - s * 0.34f), Offset(x, y - s * 0.12f), 0.5f * u)
        farol(x, y - s * 0.12f, y, s, oscuro)
    }

    // ---------------------------------------------------------------- Nichos

    override fun area(m: MuebleVida): Rect = when (m) {
        MuebleVida.SOCIAL -> Rect(0.155f, 0.205f, 0.845f, 0.775f)
        MuebleVida.MAPAS -> Rect(0.18f, 0.1f, 0.82f, 0.78f)
        MuebleVida.OTRAS_VIDAS -> Rect(0.06f, 0.25f, 0.94f, 0.79f)
        MuebleVida.NOTAS -> Rect(0.075f, 0.15f, 0.925f, 0.875f)
    }

    // ---------------------------------------------------------------- Social

    override fun DrawScope.muebleSocial(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val n = caja(area(MuebleVida.SOCIAL))
        val piso = h * 0.935f
        // El muro de sillares del salón, en penumbra hacia los bordes.
        sillares(Rect(0f, 0f, w, piso), 7.5f * u, oscuro, e.semilla, u)
        drawRect(Brush.radialGradient(listOf(Color.Transparent, Color.Black.copy(alpha = if (oscuro) 0.45f else 0.2f)), Offset(w / 2, h * 0.45f), w * 0.62f), Offset.Zero, Size(w, piso))
        losas(Rect(0f, piso, w, h), oscuro, u)
        // El dintel con su banda de runas.
        dintel(Rect(0f, 0f, w, h * 0.125f), oscuro, u, e.semilla)
        // Los dos pilares, con su estandarte y su farol.
        val pw = w * 0.092f
        listOf(Rect(w * 0.014f, h * 0.125f, w * 0.014f + pw, piso), Rect(w * 0.986f - pw, h * 0.125f, w * 0.986f, piso)).forEachIndexed { i, p ->
            pilar(p, oscuro, u, e.semilla + i)
            val tela = if (i == 0) mezcla(TERCIOPELO, e.acento, 0.35f) else RUBI
            estandarte(p.center.x, h * 0.2f, p.width * 0.78f, h * 0.36f, tela, oscuro, u, i * 3 + 1)
            farolDePared(p.center.x, h * 0.64f, p.width * 0.46f, oscuro, u)
        }
        // El marco de madera tallada de la crónica.
        val fr = n.crece(5f * u)
        drawRect(Color.Black.copy(alpha = 0.4f), Offset(fr.left + 1f * u, fr.top + 1.4f * u), fr.size)
        drawRect(t(MADERA, oscuro, 0.45f), fr.topLeft, fr.size)
        vetas(fr, t(MADERA_OSC, oscuro, 0.45f).copy(alpha = 0.55f), e.semilla + 7, 1.2f * u, grosor = 0.3f * u)
        drawRect(t(MADERA_OSC, oscuro, 0.45f), fr.topLeft, fr.size, style = Stroke(0.6f * u))
        val labio = fr.crece(-0.9f * u)
        drawRect(t(MADERA_LUZ, oscuro, 0.45f), labio.topLeft, labio.size, style = Stroke(0.35f * u))
        anillos(Rect(fr.left + 7f * u, fr.top + 1.3f * u, fr.right - 7f * u, n.top - 1.6f * u), oscuro, u)
        anillos(Rect(fr.left + 7f * u, n.bottom + 1.6f * u, fr.right - 7f * u, fr.bottom - 1.3f * u), oscuro, u)
        // El filete de oro y la vitela (el nicho), con los bordes apenas más tostados.
        drawRect(Color.Black.copy(alpha = 0.45f), n.crece(1.4f * u).topLeft, n.crece(1.4f * u).size)
        drawRect(t(ORO, oscuro, 0.3f), n.crece(1.1f * u).topLeft, n.crece(1.1f * u).size)
        drawRect(t(ORO_LUZ, oscuro, 0.3f), n.crece(1.1f * u).topLeft, n.crece(1.1f * u).size, style = Stroke(0.25f * u))
        val fondo = nicho(e, oscuro, VITELA)
        drawRect(fondo, n.topLeft, n.size)
        val b = 2.2f * u
        drawRect(Brush.verticalGradient(listOf(VITELA_OSC.copy(alpha = if (oscuro) 0.05f else 0.25f), Color.Transparent), n.top, n.top + b), n.topLeft, Size(n.width, b))
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, VITELA_OSC.copy(alpha = if (oscuro) 0.05f else 0.25f)), n.bottom - b, n.bottom), Offset(n.left, n.bottom - b), Size(n.width, b))
        drawRect(Color.Black.copy(alpha = 0.25f), n.topLeft, Size(n.width, 0.4f * u))
        // Las escuadras de oro de las esquinas, con sus clavos.
        val ee = 6.5f * u; val eg = 1.6f * u
        listOf(fr.topLeft to Offset(1f, 1f), Offset(fr.right, fr.top) to Offset(-1f, 1f), Offset(fr.left, fr.bottom) to Offset(1f, -1f), fr.bottomRight to Offset(-1f, -1f)).forEach { (c, d) ->
            val ox = if (d.x > 0) c.x else c.x - ee; val oy = if (d.y > 0) c.y else c.y - eg
            drawRect(t(ORO, oscuro, 0.25f), Offset(ox, oy), Size(ee, eg))
            drawRect(t(ORO, oscuro, 0.25f), Offset(if (d.x > 0) c.x else c.x - eg, if (d.y > 0) c.y else c.y - ee), Size(eg, ee))
            drawRect(t(ORO_OSC, oscuro, 0.25f), Offset(ox, oy), Size(ee, eg), style = Stroke(0.2f * u))
            clavo(c + Offset(d.x * eg * 0.5f, d.y * eg * 0.5f), 0.5f * u, oscuro, ORO_OSC, ORO_LUZ)
            clavo(c + Offset(d.x * (ee - eg * 0.55f), d.y * eg * 0.5f), 0.4f * u, oscuro, ORO_OSC, ORO_LUZ)
            clavo(c + Offset(d.x * eg * 0.5f, d.y * (ee - eg * 0.55f)), 0.4f * u, oscuro, ORO_OSC, ORO_LUZ)
        }
        // Las gemas engastadas en el marco: la esmeralda arriba y el rubí abajo.
        gema(Offset(fr.center.x, fr.top + 0.6f * u), 3f * u, mezcla(ESMERALDA, e.acento, 0.3f), oscuro)
        gema(Offset(fr.center.x, fr.bottom - 0.4f * u), 2.2f * u, RUBI, oscuro)
        // El banco largo, con lo que dejó la compañía encima.
        val asiento = h * 0.872f
        val banco = Rect(w * 0.13f, asiento, w * 0.87f, asiento + 2.4f * u)
        val delantal = Rect(w * 0.14f, banco.bottom, w * 0.86f, banco.bottom + 2.8f * u)
        listOf(delantal.left + 1f * u, delantal.left + delantal.width * 0.33f, delantal.right - delantal.width * 0.33f - 2f * u, delantal.right - 3f * u).forEach { x ->
            drawOval(Color.Black.copy(alpha = 0.35f), Offset(x - 1f * u, h - 2.6f * u), Size(4f * u, 1.6f * u))
            drawRect(t(MADERA_OSC, oscuro, 0.4f), Offset(x, delantal.bottom), Size(2f * u, h - 1.8f * u - delantal.bottom))
            drawRect(t(MADERA, oscuro, 0.4f), Offset(x, delantal.bottom), Size(0.8f * u, h - 1.8f * u - delantal.bottom))
        }
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(banco.left + 0.6f * u, banco.top + 0.8f * u), Size(banco.width, banco.height + delantal.height))
        tabla(delantal, MADERA, oscuro, e.semilla + 21, u)
        tabla(banco, MADERA_LUZ, oscuro, e.semilla + 22, u)
        listOf(0.1f, 0.5f, 0.9f).forEach { f ->
            val x = delantal.left + delantal.width * f
            drawRect(t(HIERRO, oscuro, 0.2f), Offset(x - 1.2f * u, delantal.top), Size(2.4f * u, delantal.height))
            clavo(Offset(x, delantal.center.y), 0.4f * u, oscuro, ORO_OSC, ORO_LUZ)
        }
        // Dos jarras de madera con aros de hierro.
        listOf(0.205f, 0.79f).forEachIndexed { i, fx ->
            val x = w * fx
            val cu = Rect(x - 2.1f * u, asiento - 5.6f * u, x + 2.1f * u, asiento)
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(cu.left + 0.5f * u, cu.bottom - 0.6f * u), Size(cu.width, 0.8f * u))
            val asa = if (i == 0) 1f else -1f
            drawArc(t(MADERA_OSC, oscuro, 0.4f), -90f, 180f * asa, false, Offset(if (asa > 0) cu.right - 1.6f * u else cu.left - 1.6f * u, cu.top + 1f * u), Size(3.2f * u, 3.2f * u), style = Stroke(0.7f * u))
            drawRect(Brush.horizontalGradient(listOf(t(MADERA, oscuro, 0.4f), t(MADERA_LUZ, oscuro, 0.4f), t(MADERA_OSC, oscuro, 0.4f)), cu.left, cu.right), cu.topLeft, cu.size)
            listOf(0.33f, 0.66f).forEach { f -> drawLine(Color.Black.copy(alpha = 0.3f), Offset(cu.left + cu.width * f, cu.top), Offset(cu.left + cu.width * f, cu.bottom), 0.2f * u) }
            listOf(cu.top + 0.9f * u, cu.bottom - 1.6f * u).forEach { y -> drawRect(t(HIERRO, oscuro, 0.2f), Offset(cu.left - 0.2f * u, y), Size(cu.width + 0.4f * u, 0.6f * u)) }
            drawOval(t(Color(0xFFD9B26A), oscuro, 0.35f), Offset(cu.left, cu.top - 0.5f * u), Size(cu.width, 1f * u))
        }
        // El yelmo de hierro con su nasal y su cresta de oro.
        run {
            val c = Offset(w * 0.31f, asiento)
            val r = 3.2f * u
            drawOval(Color.Black.copy(alpha = 0.3f), Offset(c.x - r, c.y - 0.6f * u), Size(2 * r, 1.2f * u))
            drawArc(Brush.horizontalGradient(listOf(t(HIERRO_LUZ, oscuro, 0.3f), t(HIERRO, oscuro, 0.3f)), c.x - r, c.x + r), 180f, 180f, true, Offset(c.x - r, c.y - r * 1.25f), Size(2 * r, 2.5f * r))
            drawRect(t(HIERRO, oscuro, 0.3f), Offset(c.x - r * 1.05f, c.y - 1f * u), Size(r * 2.1f, 1f * u))
            drawRect(t(HIERRO_LUZ, oscuro, 0.3f), Offset(c.x - 0.35f * u, c.y - r * 0.9f), Size(0.7f * u, r * 0.9f))
            drawArc(t(ORO, oscuro, 0.25f), 200f, 140f, false, Offset(c.x - r * 0.85f, c.y - r * 1.15f), Size(1.7f * r, 2.3f * r), style = Stroke(0.6f * u))
            drawCircle(Color.White.copy(alpha = 0.35f), 0.5f * u, Offset(c.x - r * 0.45f, c.y - r * 0.75f))
        }
        // Los mapas enrollados, atados con cinta roja, al centro del banco.
        listOf(0f, 1f).forEach { j ->
            val r = Rect(w * 0.43f + j * 1.2f * u, asiento - 2.4f * u - j * 2f * u, w * 0.57f + j * 1.2f * u, asiento - j * 2f * u)
            drawRoundRect(Brush.verticalGradient(listOf(t(VITELA, oscuro, 0.35f), t(VITELA_OSC, oscuro, 0.35f)), r.top, r.bottom), r.topLeft, r.size, CornerRadius(r.height / 2))
            drawOval(t(mezcla(VITELA_OSC, MADERA, 0.3f), oscuro, 0.35f), Offset(r.right - r.height * 0.6f, r.top), Size(r.height * 0.6f, r.height), style = Stroke(0.3f * u))
            drawRect(t(RUBI, oscuro, 0.3f), Offset(r.center.x - 0.6f * u, r.top - 0.2f * u), Size(1.2f * u, r.height + 0.4f * u))
        }
        // La copa de oro con su gema.
        run {
            val x = w * 0.66f; val y = asiento
            drawOval(Color.Black.copy(alpha = 0.3f), Offset(x - 2f * u, y - 0.5f * u), Size(4f * u, 1f * u))
            drawRect(t(ORO_OSC, oscuro, 0.25f), Offset(x - 1.6f * u, y - 0.7f * u), Size(3.2f * u, 0.7f * u))
            drawRect(t(ORO, oscuro, 0.25f), Offset(x - 0.35f * u, y - 3f * u), Size(0.7f * u, 2.4f * u))
            drawPath(Path().apply { moveTo(x - 2.3f * u, y - 6.4f * u); lineTo(x + 2.3f * u, y - 6.4f * u); quadraticTo(x + 2.1f * u, y - 3f * u, x, y - 2.9f * u); quadraticTo(x - 2.1f * u, y - 3f * u, x - 2.3f * u, y - 6.4f * u); close() },
                Brush.horizontalGradient(listOf(t(ORO_LUZ, oscuro, 0.25f), t(ORO, oscuro, 0.25f), t(ORO_OSC, oscuro, 0.25f)), x - 2.3f * u, x + 2.3f * u))
            gema(Offset(x, y - 4.9f * u), 0.7f * u, mezcla(ESMERALDA, e.acento, 0.3f), oscuro, engaste = false)
        }
    }

    // ---------------------------------------------------------------- Mapas

    override fun DrawScope.muebleMapas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val n = caja(area(MuebleVida.MAPAS))
        // El piso de losas, en penumbra detrás de la mesa.
        losas(Rect(0f, 0f, w, h), oscuro, u)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.45f else 0.25f))
        val tope = h * 0.845f; val faldon = h * 0.93f
        // Las patas torneadas.
        listOf(w * 0.05f, w * 0.95f - 4f * u).forEach { x ->
            drawOval(Color.Black.copy(alpha = 0.4f), Offset(x - 1.5f * u, h - 1.8f * u), Size(7f * u, 2.2f * u))
            drawRect(Brush.horizontalGradient(listOf(t(MADERA_LUZ, oscuro, 0.45f), t(MADERA, oscuro, 0.45f), t(MADERA_OSC, oscuro, 0.45f)), x, x + 4f * u), Offset(x, faldon), Size(4f * u, h - faldon - 0.6f * u))
            listOf(0.3f, 0.7f).forEach { f -> drawRoundRect(t(MADERA_LUZ, oscuro, 0.45f), Offset(x - 0.6f * u, faldon + (h - faldon) * f - 0.6f * u), Size(5.2f * u, 1.2f * u), CornerRadius(0.6f * u)) }
        }
        // El faldón, con su fila de dientes tallados, la cerradura y los tiradores.
        val fal = Rect(w * 0.03f, tope, w * 0.97f, faldon)
        tabla(fal, MADERA, oscuro, 61, u)
        val dien = Rect(fal.left + 1f * u, fal.bottom - fal.height * 0.42f, fal.right - 1f * u, fal.bottom - fal.height * 0.12f)
        val nd = (dien.width / (2.4f * u)).toInt()
        for (k in 0 until nd) {
            val x0 = dien.left + dien.width * k / nd; val x1 = dien.left + dien.width * (k + 1) / nd
            drawPath(Path().apply { moveTo(x0, dien.top); lineTo(x1, dien.top); lineTo((x0 + x1) / 2, dien.bottom); close() }, t(MADERA_OSC, oscuro, 0.45f))
            drawLine(t(MADERA_LUZ, oscuro, 0.45f), Offset(x0, dien.top), Offset((x0 + x1) / 2, dien.bottom), 0.25f * u)
        }
        val cer = Rect(fal.center.x - 3.4f * u, fal.top + fal.height * 0.1f, fal.center.x + 3.4f * u, fal.bottom - fal.height * 0.1f)
        drawRect(t(HIERRO, oscuro, 0.2f), cer.topLeft, cer.size)
        drawRect(t(ORO, oscuro, 0.25f), cer.topLeft, cer.size, style = Stroke(0.3f * u))
        drawCircle(Color.Black, 0.6f * u, Offset(cer.center.x, cer.center.y - 0.4f * u))
        drawRect(Color.Black, Offset(cer.center.x - 0.25f * u, cer.center.y - 0.2f * u), Size(0.5f * u, 1.3f * u))
        listOf(cer.left + 0.9f * u, cer.right - 0.9f * u).forEach { x -> clavo(Offset(x, cer.center.y), 0.45f * u, oscuro, ORO_OSC, ORO_LUZ) }
        listOf(0.25f, 0.75f).forEach { f ->
            val c = Offset(fal.left + fal.width * f, fal.top + fal.height * 0.32f)
            drawCircle(t(HIERRO, oscuro, 0.2f), 1f * u, c)
            drawArc(t(HIERRO_LUZ, oscuro, 0.3f), 0f, 180f, false, Offset(c.x - 1.4f * u, c.y - 1f * u), Size(2.8f * u, 2.8f * u), style = Stroke(0.4f * u))
        }
        // El tablero, con su canto moldurado y sus tablas a lo largo.
        val top = Rect(w * 0.012f, h * 0.012f, w * 0.988f, tope)
        drawRect(Color.Black.copy(alpha = 0.45f), Offset(top.left, top.bottom), Size(top.width, 1.2f * u))
        drawRoundRect(t(MADERA_OSC, oscuro, 0.45f), top.topLeft, top.size, CornerRadius(1f * u))
        val canto = top.crece(-0.6f * u)
        drawRoundRect(t(MADERA_LUZ, oscuro, 0.45f), canto.topLeft, canto.size, CornerRadius(0.8f * u))
        val campo = canto.crece(-1.1f * u)
        for (k in 0 until 5) {
            val r = Rect(campo.left, campo.top + campo.height * k / 5, campo.right, campo.top + campo.height * (k + 1) / 5)
            drawRect(t(mezcla(MADERA, MADERA_LUZ, 0.1f + 0.3f * azar(e.semilla, k)), oscuro, 0.45f), r.topLeft, r.size)
            vetas(r, t(MADERA_OSC, oscuro, 0.45f).copy(alpha = 0.55f), e.semilla * 5 + k, 1.2f * u, grosor = 0.3f * u)
            drawLine(Color.Black.copy(alpha = 0.45f), Offset(r.left, r.top), Offset(r.right, r.top), 0.3f * u)
        }
        drawRect(Color.Black.copy(alpha = 0.35f), campo.topLeft, campo.size, style = Stroke(0.3f * u))
        // Las escuadras de hierro de las esquinas.
        val ee = 6f * u; val eg = 1.5f * u
        listOf(campo.topLeft to Offset(1f, 1f), Offset(campo.right, campo.top) to Offset(-1f, 1f), Offset(campo.left, campo.bottom) to Offset(1f, -1f), campo.bottomRight to Offset(-1f, -1f)).forEach { (c, d) ->
            val ox = if (d.x > 0) c.x else c.x - ee; val oy = if (d.y > 0) c.y else c.y - eg
            drawRect(t(HIERRO, oscuro, 0.2f), Offset(ox, oy), Size(ee, eg))
            drawRect(t(HIERRO, oscuro, 0.2f), Offset(if (d.x > 0) c.x else c.x - eg, if (d.y > 0) c.y else c.y - ee), Size(eg, ee))
            clavo(c + Offset(d.x * eg * 0.5f, d.y * eg * 0.5f), 0.45f * u, oscuro, ORO_OSC, ORO_LUZ)
            clavo(c + Offset(d.x * (ee - eg * 0.6f), d.y * eg * 0.5f), 0.4f * u, oscuro)
            clavo(c + Offset(d.x * eg * 0.5f, d.y * (ee - eg * 0.6f)), 0.4f * u, oscuro)
        }
        // El mapa de vitela, de borde barbado y clavado en sus esquinas; su campo es el nicho.
        val perg = n.crece(2.6f * u)
        val forma = barbado(perg, e.semilla, 40, 0.012f)
        translate(0.7f * u, 1f * u) { drawPath(forma, Color.Black.copy(alpha = 0.4f)) }
        val colorPerg = nicho(e, oscuro, VITELA)
        drawPath(forma, colorPerg)
        clipPath(forma) {
            val b = 2.6f * u
            drawRect(Brush.horizontalGradient(listOf(VITELA_OSC.copy(alpha = if (oscuro) 0.12f else 0.55f), Color.Transparent), perg.left, perg.left + b), perg.topLeft, Size(b, perg.height))
            drawRect(Brush.horizontalGradient(listOf(Color.Transparent, VITELA_OSC.copy(alpha = if (oscuro) 0.12f else 0.55f)), perg.right - b, perg.right), Offset(perg.right - b, perg.top), Size(b, perg.height))
        }
        drawPath(forma, t(VITELA_OSC, oscuro, 0.4f), style = Stroke(0.4f * u))
        drawRect(t(ORO_OSC, oscuro, 0.3f), n.crece(1.3f * u).topLeft, n.crece(1.3f * u).size, style = Stroke(0.3f * u))
        drawRect(colorPerg, n.topLeft, n.size)
        listOf(perg.topLeft + Offset(1.4f * u, 1.4f * u), Offset(perg.right - 1.4f * u, perg.top + 1.4f * u), Offset(perg.left + 1.4f * u, perg.bottom - 1.4f * u), perg.bottomRight - Offset(1.4f * u, 1.4f * u)).forEach { c ->
            clavo(c, 0.7f * u, oscuro, ORO, ORO_LUZ)
        }
        // A la izquierda: el tintero de cuerno con la pluma, los mapas enrollados y la bolsa de gemas.
        val lx = (campo.left + perg.left) / 2
        val lan = (perg.left - campo.left) * 0.5f
        run {
            val c = Offset(lx, h * 0.17f); val r = minOf(3.6f * u, lan * 0.6f)
            drawCircle(Color.Black.copy(alpha = 0.35f), r, c + Offset(0.5f * u, 0.7f * u))
            drawCircle(t(Color(0xFF8A6A40), oscuro, 0.35f), r, c)
            drawCircle(t(Color(0xFFC9A876), oscuro, 0.35f), r, c, style = Stroke(0.5f * u))
            drawCircle(Color(0xFF0E0C0A), r * 0.55f, c)
            drawCircle(Color.White.copy(alpha = 0.3f), r * 0.15f, c - Offset(r * 0.2f, r * 0.2f))
            // La pluma, metida en el tintero y apoyada en la mesa.
            rotate(-38f, c) {
                val pl = Rect(c.x - 0.9f * u, c.y - 12f * u, c.x + 0.9f * u, c.y - 1f * u)
                drawOval(t(Color(0xFFF2EEE4), oscuro, 0.3f), pl.topLeft, pl.size)
                for (k in 1..7) {
                    val y = pl.top + pl.height * k / 8
                    drawLine(Color.Black.copy(alpha = 0.18f), Offset(c.x, y), Offset(c.x - 0.8f * u, y - 0.9f * u), 0.15f * u)
                    drawLine(Color.Black.copy(alpha = 0.18f), Offset(c.x, y), Offset(c.x + 0.8f * u, y - 0.9f * u), 0.15f * u)
                }
                drawLine(t(Color(0xFFBDB6A4), oscuro, 0.3f), Offset(c.x, pl.top), Offset(c.x, c.y), 0.25f * u)
            }
        }
        listOf(-0.5f, 0.55f).forEachIndexed { j, dx ->
            val x = lx + dx * lan * 0.8f
            val r = Rect(x - 1.7f * u, h * (0.33f + 0.04f * j), x + 1.7f * u, h * (0.63f + 0.03f * j))
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(r.left + 0.6f * u, r.top + 0.8f * u), r.size)
            drawRoundRect(Brush.horizontalGradient(listOf(t(VITELA_OSC, oscuro, 0.4f), t(VITELA, oscuro, 0.4f), t(VITELA_OSC, oscuro, 0.4f)), r.left, r.right), r.topLeft, r.size, CornerRadius(r.width / 2))
            drawOval(t(mezcla(VITELA_OSC, MADERA, 0.35f), oscuro, 0.4f), Offset(r.left, r.top), Size(r.width, r.width * 0.6f), style = Stroke(0.3f * u))
            drawRect(t(if (j == 0) RUBI else mezcla(TERCIOPELO, e.acento, 0.35f), oscuro, 0.3f), Offset(r.left - 0.2f * u, r.center.y - 0.6f * u), Size(r.width + 0.4f * u, 1.2f * u))
        }
        run {
            val c = Offset(lx, h * 0.74f); val r = minOf(4.6f * u, lan * 0.75f)
            drawCircle(Color.Black.copy(alpha = 0.35f), r, c + Offset(0.5f * u, 0.7f * u))
            drawCircle(Brush.radialGradient(listOf(t(Color(0xFFA9774A), oscuro, 0.4f), t(CUERO, oscuro, 0.4f), t(Color(0xFF5A3A1E), oscuro, 0.4f)), c - Offset(r * 0.3f, r * 0.3f), r * 1.4f), r, c)
            for (k in 0 until 8) {
                val a = k * 0.785f
                drawLine(Color.Black.copy(alpha = 0.25f), c + Offset(cos(a), sin(a)) * (r * 0.25f), c + Offset(cos(a), sin(a)) * (r * 0.9f), 0.2f * u)
            }
            drawCircle(t(Color(0xFF5A3A1E), oscuro, 0.4f), r * 0.28f, c)
            drawCircle(t(RUBI, oscuro, 0.3f), r * 0.3f, c, style = Stroke(0.35f * u))
            gema(c + Offset(r * 0.75f, r * 1.05f), 1.3f * u, ESMERALDA, oscuro, engaste = false)
            gema(c + Offset(-r * 0.6f, r * 1.15f), 1.15f * u, RUBI, oscuro, engaste = false)
            gema(c + Offset(r * 0.1f, r * 1.55f), 1f * u, ZAFIRO, oscuro, engaste = false)
        }
        // A la derecha: la brújula en su caja de latón, la vela, el compás de puntas y las monedas.
        val rx = (perg.right + campo.right) / 2
        val ran = (campo.right - perg.right) * 0.5f
        run {
            val a = minOf(5f * u, ran * 0.8f)
            val r = Rect(rx - a, h * 0.17f - a, rx + a, h * 0.17f + a)
            drawRect(Color.Black.copy(alpha = 0.35f), Offset(r.left + 0.6f * u, r.top + 0.8f * u), r.size)
            drawRoundRect(Brush.linearGradient(listOf(t(ORO_LUZ, oscuro, 0.3f), t(ORO, oscuro, 0.3f), t(ORO_OSC, oscuro, 0.3f)), r.topLeft, r.bottomRight), r.topLeft, r.size, CornerRadius(0.5f * u))
            drawCircle(t(VITELA, oscuro, 0.3f), a * 0.8f, r.center)
            drawCircle(t(ORO_OSC, oscuro, 0.3f), a * 0.8f, r.center, style = Stroke(0.3f * u))
            rosa(r.center, a * 0.72f, t(ORO, oscuro, 0.3f), t(TINTA, oscuro, 0.2f), t(RUBI, oscuro, 0.2f))
            drawCircle(Color.White.copy(alpha = 0.25f), a * 0.3f, r.center - Offset(a * 0.3f, a * 0.3f))
        }
        run {
            val c = Offset(rx, h * 0.4f); val r = minOf(3.8f * u, ran * 0.7f)
            halo(c, 14f * u, FAROL.copy(alpha = if (oscuro) 0.42f else 0.3f))
            drawCircle(Color.Black.copy(alpha = 0.35f), r, c + Offset(0.5f * u, 0.7f * u))
            drawCircle(t(ORO, oscuro, 0.25f), r, c)
            drawCircle(t(ORO_OSC, oscuro, 0.25f), r, c, style = Stroke(0.3f * u))
            drawArc(t(ORO, oscuro, 0.25f), -30f, 60f, false, Offset(c.x + r * 0.7f, c.y - r * 0.4f), Size(r * 0.8f, r * 0.8f), style = Stroke(0.5f * u))
            drawCircle(CERA, r * 0.5f, c)
            drawCircle(Color(0xFFFFE6A0), r * 0.3f, c)
            drawCircle(Color(0xFFF5A62E), r * 0.14f, c)
        }
        run {
            val bisagra = Offset(rx, h * 0.5f); val largo = h * 0.2f
            listOf(-1f, 1f).forEach { s ->
                val punta = bisagra + Offset(s * ran * 0.45f, largo)
                drawLine(Color.Black.copy(alpha = 0.3f), bisagra + Offset(0.4f * u, 0.5f * u), punta + Offset(0.4f * u, 0.5f * u), 0.7f * u, StrokeCap.Round)
                drawLine(t(ORO, oscuro, 0.25f), bisagra, punta, 0.6f * u, StrokeCap.Round)
                drawLine(t(HIERRO_LUZ, oscuro, 0.25f), punta - Offset(s * ran * 0.08f, largo * 0.15f), punta, 0.35f * u, StrokeCap.Round)
            }
            clavo(bisagra, 0.9f * u, oscuro, ORO, ORO_LUZ)
        }
        listOf(0f to 0f, 1.2f to -0.8f, -0.9f to -1.4f).forEachIndexed { k, (dx, dy) ->
            val c = Offset(rx + dx * 1.6f * u, h * 0.8f + dy * 1.4f * u); val r = minOf(3f * u, ran * 0.55f)
            drawCircle(Color.Black.copy(alpha = 0.3f), r, c + Offset(0.3f * u, 0.5f * u))
            drawCircle(t(ORO, oscuro, 0.25f), r, c)
            drawCircle(t(ORO_OSC, oscuro, 0.25f), r * 0.8f, c, style = Stroke(0.2f * u))
            if (k == 2) runa(4, c, r * 0.9f, t(ORO_OSC, oscuro, 0.25f), 0.3f * u)
            drawCircle(Color.White.copy(alpha = 0.3f), r * 0.25f, c - Offset(r * 0.35f, r * 0.35f))
        }
    }

    // ---------------------------------------------------------------- Otras vidas

    override fun DrawScope.muebleOtrasVidas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val n = caja(area(MuebleVida.OTRAS_VIDAS))
        // El muro de la cámara del tesoro.
        sillares(Rect(0f, 0f, w, h), 6.5f * u, oscuro, e.semilla, u)
        drawRect(Brush.radialGradient(listOf(Color.Transparent, Color.Black.copy(alpha = if (oscuro) 0.45f else 0.2f)), Offset(w / 2, h * 0.5f), w * 0.6f))
        // El dintel con su banda de runas y, al centro, la gran esmeralda en su tablero de piedra.
        val din = Rect(0f, 0f, w, h * 0.2f)
        dintel(din, oscuro, u, e.semilla + 3)
        val tab = Rect(w / 2 - 9f * u, h * 0.012f, w / 2 + 9f * u, din.bottom - 1.2f * u)
        drawRect(Color.Black.copy(alpha = 0.4f), Offset(tab.left + 0.6f * u, tab.top + 0.8f * u), tab.size)
        drawRect(Brush.verticalGradient(listOf(t(PIEDRA_LUZ, oscuro, 0.5f), t(PIEDRA_OSC, oscuro, 0.5f)), tab.top, tab.bottom), tab.topLeft, tab.size)
        drawRect(t(ORO, oscuro, 0.25f), tab.crece(-0.7f * u).topLeft, tab.crece(-0.7f * u).size, style = Stroke(0.4f * u))
        listOf(tab.left + 3f * u, tab.right - 3f * u).forEachIndexed { i, x -> runa(i * 7 + 2, Offset(x, tab.center.y), tab.height * 0.42f, t(ORO, oscuro, 0.2f), 0.5f * u) }
        gema(tab.center, 3.6f * u, mezcla(ESMERALDA, e.acento, 0.3f), oscuro)
        // Las jambas de los lados, con una antorcha encendida en cada una.
        listOf(Rect(0f, din.bottom, n.left - 0.6f * u, h * 0.9f), Rect(n.right + 0.6f * u, din.bottom, w, h * 0.9f)).forEach { j ->
            drawRect(Brush.horizontalGradient(listOf(t(PIEDRA_OSC, oscuro, 0.55f), t(PIEDRA_LUZ, oscuro, 0.55f), t(PIEDRA_OSC, oscuro, 0.55f)), j.left, j.right), j.topLeft, j.size)
            var y = j.top; while (y < j.bottom) { drawLine(t(JUNTA, oscuro), Offset(j.left, y), Offset(j.right, y), 0.3f * u); y += 6.5f * u }
            val c = Offset(j.center.x, h * 0.42f)
            halo(c, 16f * u, FAROL.copy(alpha = if (oscuro) 0.45f else 0.28f))
            drawRect(t(HIERRO, oscuro, 0.2f), Offset(c.x - 0.5f * u, c.y), Size(1f * u, 7f * u))
            drawRect(t(HIERRO, oscuro, 0.2f), Offset(c.x - 1.6f * u, c.y + 6f * u), Size(3.2f * u, 0.8f * u))
            drawPath(Path().apply { moveTo(c.x - 1.4f * u, c.y - 2f * u); lineTo(c.x + 1.4f * u, c.y - 2f * u); lineTo(c.x + 0.7f * u, c.y + 1.2f * u); lineTo(c.x - 0.7f * u, c.y + 1.2f * u); close() }, t(MADERA, oscuro, 0.3f))
            llama(Offset(c.x, c.y - 1.6f * u), 4.6f * u)
        }
        // Cinco hornacinas de medio punto, talladas en el muro, con columnillas entre ellas.
        val cw = n.width / 5f
        val fondo = nicho(e, oscuro, mezcla(PIEDRA_LUZ, VITELA, 0.6f))
        drawRect(Color.Black.copy(alpha = 0.45f), n.crece(0.6f * u).topLeft, n.crece(0.6f * u).size)
        for (i in 0 until 5) {
            val c = Rect(n.left + cw * i + 0.7f * u, n.top, n.left + cw * (i + 1) - 0.7f * u, n.bottom)
            drawRect(fondo, c.topLeft, c.size)
            // La sombra que hace el arco adentro, apenas.
            drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.22f else 0.1f), Color.Transparent), c.top, c.top + c.height * 0.45f), c.topLeft, Size(c.width, c.height * 0.45f))
            // Las enjutas de piedra por encima del arco.
            val rad = c.width / 2
            val enjuta = Path().apply {
                moveTo(c.left, c.top); lineTo(c.right, c.top); lineTo(c.right, c.top + rad)
                arcTo(Rect(c.left, c.top, c.right, c.top + 2 * rad), 0f, -180f, false)
                close()
            }
            drawPath(enjuta, t(PIEDRA, oscuro, 0.55f))
            clipPath(enjuta) { drawRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = if (oscuro) 0.05f else 0.15f), Color.Black.copy(alpha = 0.15f)), c.top, c.top + rad), c.topLeft, Size(c.width, rad)) }
            drawArc(t(PIEDRA_LUZ, oscuro, 0.5f), 180f, 180f, false, Offset(c.left + 0.5f * u, c.top + 0.5f * u), Size(c.width - 1f * u, 2 * rad - 1f * u), style = Stroke(1f * u))
            drawArc(t(ORO, oscuro, 0.25f), 180f, 180f, false, Offset(c.left + 1.2f * u, c.top + 1.2f * u), Size(c.width - 2.4f * u, 2 * rad - 2.4f * u), style = Stroke(0.3f * u))
            gema(Offset(c.center.x, c.top + 1.4f * u), 1.1f * u, if (i % 2 == 0) RUBI else ZAFIRO, oscuro)
            // El umbral de la hornacina.
            drawRect(t(PIEDRA_LUZ, oscuro, 0.5f), Offset(c.left, c.bottom - 1.2f * u), Size(c.width, 1.2f * u))
            drawRect(Color.Black.copy(alpha = 0.25f), Offset(c.left, c.bottom - 1.2f * u), Size(c.width, 0.3f * u))
        }
        for (i in 0..5) {
            val x = n.left + cw * i
            val ancho = if (i == 0 || i == 5) 1.8f * u else 1.4f * u
            val col = Rect(x - ancho / 2, n.top + cw * 0.36f, x + ancho / 2, n.bottom)
            drawRect(Brush.horizontalGradient(listOf(t(PIEDRA_LUZ, oscuro, 0.5f), t(PIEDRA, oscuro, 0.5f), t(PIEDRA_OSC, oscuro, 0.5f)), col.left, col.right), col.topLeft, col.size)
            drawRect(t(ORO, oscuro, 0.25f), Offset(col.left - 0.4f * u, col.top), Size(col.width + 0.8f * u, 1f * u))
            drawRect(t(ORO_LUZ, oscuro, 0.25f), Offset(col.left - 0.4f * u, col.top), Size(col.width + 0.8f * u, 0.3f * u))
            drawRect(t(PIEDRA_LUZ, oscuro, 0.5f), Offset(col.left - 0.5f * u, col.bottom - 2f * u), Size(col.width + 1f * u, 2f * u))
        }
        // La repisa de piedra con runas de oro.
        val rep = Rect(w * 0.02f, n.bottom, w * 0.98f, n.bottom + 4.4f * u)
        drawRect(Brush.verticalGradient(listOf(t(PIEDRA_LUZ, oscuro, 0.5f), t(PIEDRA, oscuro, 0.5f)), rep.top, rep.bottom), rep.topLeft, rep.size)
        drawRect(Color.White.copy(alpha = if (oscuro) 0.06f else 0.2f), rep.topLeft, Size(rep.width, 0.4f * u))
        bandaRunas(Rect(rep.left + 2f * u, rep.top + rep.height * 0.2f, rep.right - 2f * u, rep.bottom - rep.height * 0.2f), oscuro, u, e.semilla + 9, 4.2f * u)
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.45f), Color.Transparent), rep.bottom, rep.bottom + 2f * u), Offset(rep.left, rep.bottom), Size(rep.width, 2f * u))
        // El zócalo, con un tablero rúnico bajo cada hornacina.
        val zoc = Rect(w * 0.03f, rep.bottom, w * 0.97f, h)
        drawRect(t(PIEDRA_OSC, oscuro, 0.55f), zoc.topLeft, zoc.size)
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.4f), Color.Transparent), zoc.top, zoc.top + 2f * u), zoc.topLeft, Size(zoc.width, 2f * u))
        for (i in 0 until 5) {
            val cx = n.left + cw * (i + 0.5f)
            val p = Rect(cx - cw * 0.3f, zoc.top + 1.6f * u, cx + cw * 0.3f, zoc.bottom - 1.6f * u)
            if (p.height < 2f * u) continue
            drawRect(t(mezcla(PIEDRA_OSC, JUNTA, 0.4f), oscuro, 0.55f), p.topLeft, p.size)
            drawRect(Color.White.copy(alpha = if (oscuro) 0.05f else 0.15f), Offset(p.left, p.bottom - 0.3f * u), Size(p.width, 0.3f * u))
            drawRect(Color.Black.copy(alpha = 0.35f), p.topLeft, Size(p.width, 0.35f * u))
            runa(i * 3 + 1, p.center, p.height * 0.6f, t(ORO, oscuro, 0.2f), 0.4f * u)
            listOf(p.left + 1.4f * u, p.right - 1.4f * u).forEach { x -> clavo(Offset(x, p.center.y), 0.4f * u, oscuro, ORO_OSC, ORO_LUZ) }
        }
    }

    // ---------------------------------------------------------------- Íconos

    override fun DrawScope.iconoMapa(id: String, e: EstadoIcono, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 108f
        val c = Offset(w / 2, h / 2)
        val linea = t(Color(0xFF2E2A24), oscuro, 0.1f)
        when (id) {
            "unesco" -> {
                // La puerta de la montaña: la montaña de dos picos nevados y, en su pie, la portada tallada con runas y luz adentro.
                val suelo = c.y + 40 * u
                drawOval(Color.Black.copy(alpha = 0.2f), Offset(c.x - 56 * u, suelo - 3 * u), Size(112 * u, 6 * u))
                val monte = Path().apply {
                    moveTo(c.x - 56 * u, suelo); lineTo(c.x - 24 * u, c.y - 30 * u); lineTo(c.x - 12 * u, c.y - 16 * u)
                    lineTo(c.x + 8 * u, c.y - 46 * u); lineTo(c.x + 56 * u, suelo); close()
                }
                drawPath(monte, Brush.verticalGradient(listOf(t(PIEDRA_LUZ, oscuro, 0.3f), t(PIEDRA, oscuro, 0.3f), t(PIEDRA_OSC, oscuro, 0.3f)), c.y - 46 * u, suelo))
                drawPath(Path().apply { moveTo(c.x + 8 * u, c.y - 46 * u); lineTo(c.x + 56 * u, suelo); lineTo(c.x + 14 * u, suelo); lineTo(c.x + 4 * u, c.y - 18 * u); close() }, Color.Black.copy(alpha = 0.2f))
                drawPath(Path().apply { moveTo(c.x - 24 * u, c.y - 30 * u); lineTo(c.x - 30 * u, c.y - 21 * u); lineTo(c.x - 25 * u, c.y - 23 * u); lineTo(c.x - 22 * u, c.y - 19 * u); lineTo(c.x - 18 * u, c.y - 23 * u); close() }, t(Color(0xFFF4F2EC), oscuro, 0.25f))
                drawPath(Path().apply { moveTo(c.x + 8 * u, c.y - 46 * u); lineTo(c.x + 0 * u, c.y - 34 * u); lineTo(c.x + 5 * u, c.y - 36 * u); lineTo(c.x + 9 * u, c.y - 31 * u); lineTo(c.x + 13 * u, c.y - 36 * u); lineTo(c.x + 17 * u, c.y - 33 * u); close() }, t(Color(0xFFF4F2EC), oscuro, 0.25f))
                drawPath(monte, linea, style = Stroke(1.2f * u))
                // La portada: dos pilares, el arco con runas y la luz dorada.
                val por = Rect(c.x - 17 * u, c.y + 2 * u, c.x + 17 * u, suelo)
                drawRect(t(PIEDRA_LUZ, oscuro, 0.3f), por.topLeft, por.size)
                drawRect(linea, por.topLeft, por.size, style = Stroke(1f * u))
                val puerta = Rect(c.x - 8 * u, c.y + 10 * u, c.x + 8 * u, suelo)
                drawPath(medioPunto(puerta.crece(2.6f * u)), t(PIEDRA, oscuro, 0.3f))
                drawPath(medioPunto(puerta.crece(2.6f * u)), linea, style = Stroke(0.8f * u))
                drawPath(medioPunto(puerta), Brush.verticalGradient(listOf(t(ORO_LUZ, oscuro, 0.1f), t(mezcla(e.c2, ORO, 0.4f), oscuro, 0.1f)), puerta.top, puerta.bottom))
                drawLine(linea, Offset(c.x, puerta.top + 2 * u), Offset(c.x, suelo), 1f * u)
                for (k in 0 until 5) {
                    val a = Math.toRadians(180.0 + 22.5 + k * 33.75)
                    val rr = 10.3f * u
                    runa(k * 2 + 1, Offset(c.x + rr * cos(a).toFloat(), puerta.top + 8 * u + rr * sin(a).toFloat()), 3f * u, t(ORO_OSC, oscuro, 0.1f), 0.6f * u)
                }
                listOf(por.left + 3 * u, por.right - 3 * u).forEach { x ->
                    drawRect(t(PIEDRA, oscuro, 0.3f), Offset(x - 2.2f * u, por.top + 2 * u), Size(4.4f * u, por.height - 2 * u))
                    drawRect(linea, Offset(x - 2.2f * u, por.top + 2 * u), Size(4.4f * u, por.height - 2 * u), style = Stroke(0.6f * u))
                }
                drawRect(t(PIEDRA_OSC, oscuro, 0.3f), Offset(por.left - 2 * u, por.top - 2.5f * u), Size(por.width + 4 * u, 3 * u))
                gema(Offset(c.x, por.top - 1f * u), 2.4f * u, e.c1, oscuro)
                // Los pinos al pie.
                listOf(-44f to 1f, -36f to 0.8f, 40f to 0.9f).forEach { (dx, s) ->
                    val b = Offset(c.x + dx * u, suelo)
                    drawRect(t(MADERA_OSC, oscuro, 0.2f), Offset(b.x - 1 * u, b.y - 4 * u * s), Size(2 * u, 4 * u * s))
                    for (k in 0 until 3) {
                        val y = b.y - (4 + 6 * k) * u * s
                        drawPath(Path().apply { moveTo(b.x - (8 - 2 * k) * u * s, y); lineTo(b.x, y - 10 * u * s); lineTo(b.x + (8 - 2 * k) * u * s, y); close() }, t(mezcla(TERCIOPELO, e.c1, 0.3f), oscuro, 0.1f))
                    }
                }
            }
            "rascar" -> {
                // El mapa de vitela con la esquina rascada, y la moneda rúnica que la raspa.
                val r = Rect(c.x - 40 * u, c.y - 30 * u, c.x + 26 * u, c.y + 32 * u)
                rotate(-4f, c) {
                    val f = barbado(r, 5, 30, 0.015f)
                    translate(1.5f * u, 2f * u) { drawPath(f, Color.Black.copy(alpha = 0.25f)) }
                    drawPath(f, t(VITELA, oscuro, 0.3f))
                    drawPath(f, t(VITELA_OSC, oscuro, 0.3f), style = Stroke(1f * u))
                    // Los montes, el río y el camino de la compañía.
                    listOf(-30f, -22f, -14f).forEach { dx ->
                        drawPath(Path().apply { moveTo(c.x + dx * u - 4 * u, c.y - 10 * u); lineTo(c.x + dx * u, c.y - 18 * u); lineTo(c.x + dx * u + 4 * u, c.y - 10 * u) }, linea, style = Stroke(1.1f * u))
                    }
                    drawPath(Path().apply { moveTo(c.x + 2 * u, c.y - 28 * u); cubicTo(c.x - 6 * u, c.y - 10 * u, c.x + 10 * u, c.y + 4 * u, c.x - 2 * u, c.y + 30 * u) }, t(ZAFIRO, oscuro, 0.1f), style = Stroke(1.3f * u))
                    drawPath(Path().apply { moveTo(c.x - 34 * u, c.y + 20 * u); quadraticTo(c.x - 16 * u, c.y + 2 * u, c.x + 18 * u, c.y + 16 * u) }, t(RUBI, oscuro, 0.1f), style = Stroke(1.2f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2f * u, 2.6f * u))))
                    // La esquina rascada: debajo asoma el acento, con las rayas de la moneda.
                    val esquina = Path().apply { moveTo(r.right - 22 * u, r.top + 1 * u); lineTo(r.right - 1 * u, r.top + 1 * u); lineTo(r.right - 1 * u, r.top + 24 * u); quadraticTo(r.right - 14 * u, r.top + 16 * u, r.right - 22 * u, r.top + 1 * u); close() }
                    drawPath(esquina, t(e.c1, oscuro, 0.1f))
                    clipPath(esquina) { for (k in 0 until 6) drawLine(Color.White.copy(alpha = 0.3f), Offset(r.right - 24 * u + k * 4 * u, r.top), Offset(r.right - 10 * u + k * 4 * u, r.top + 24 * u), 0.7f * u) }
                    drawPath(esquina, t(VITELA_OSC, oscuro, 0.3f), style = Stroke(0.8f * u))
                }
                // La moneda de oro con su runa, inclinada sobre el mapa.
                val mc = Offset(c.x + 34 * u, c.y + 8 * u); val mr = 15 * u
                drawOval(Color.Black.copy(alpha = 0.25f), Offset(mc.x - mr + 2 * u, mc.y - mr * 0.92f + 3 * u), Size(2 * mr, 1.84f * mr))
                drawOval(t(ORO_OSC, oscuro, 0.2f), Offset(mc.x - mr, mc.y - mr * 0.92f + 2 * u), Size(2 * mr, 1.84f * mr))
                drawOval(Brush.linearGradient(listOf(t(ORO_LUZ, oscuro, 0.2f), t(ORO, oscuro, 0.2f), t(ORO_OSC, oscuro, 0.2f)), mc - Offset(mr, mr), mc + Offset(mr, mr)), Offset(mc.x - mr, mc.y - mr * 0.92f), Size(2 * mr, 1.84f * mr))
                drawOval(t(ORO_OSC, oscuro, 0.2f), Offset(mc.x - mr * 0.78f, mc.y - mr * 0.72f), Size(1.56f * mr, 1.44f * mr), style = Stroke(1f * u))
                runa(5, mc, mr * 0.9f, t(ORO_OSC, oscuro, 0.2f), 1.5f * u)
                drawCircle(Color.White.copy(alpha = 0.45f), 2 * u, mc - Offset(mr * 0.45f, mr * 0.45f))
                listOf(-1f, 0f, 1f).forEach { k -> drawCircle(t(e.c1, oscuro, 0.1f).copy(alpha = 0.7f), 1 * u, Offset(c.x + (18 + 3 * k) * u, c.y - (6 + 4 * k) * u)) }
            }
            else -> {
                // Tus lugares: tres gemas engastadas sobre el mapa enrollado, unidas por el camino de la compañía.
                val r = Rect(c.x - 50 * u, c.y - 22 * u, c.x + 50 * u, c.y + 30 * u)
                drawRect(Color.Black.copy(alpha = 0.22f), Offset(r.left + 2 * u, r.top + 3 * u), r.size)
                drawRect(Brush.verticalGradient(listOf(t(VITELA, oscuro, 0.3f), t(mezcla(VITELA, VITELA_OSC, 0.5f), oscuro, 0.3f)), r.top, r.bottom), r.topLeft, r.size)
                listOf(r.left, r.right).forEach { x ->
                    val rr = Rect(x - 4 * u, r.top - 3 * u, x + 4 * u, r.bottom + 3 * u)
                    drawRoundRect(Brush.horizontalGradient(listOf(t(MADERA_LUZ, oscuro, 0.3f), t(MADERA, oscuro, 0.3f), t(MADERA_OSC, oscuro, 0.3f)), rr.left, rr.right), rr.topLeft, rr.size, CornerRadius(3 * u))
                    listOf(rr.top, rr.bottom).forEach { y -> drawCircle(t(ORO, oscuro, 0.2f), 2.6f * u, Offset(x, y)) }
                }
                listOf(-36f, -28f).forEach { dx -> drawPath(Path().apply { moveTo(c.x + dx * u - 4 * u, c.y + 22 * u); lineTo(c.x + dx * u, c.y + 15 * u); lineTo(c.x + dx * u + 4 * u, c.y + 22 * u) }, linea.copy(alpha = 0.6f), style = Stroke(1f * u)) }
                listOf(24f, 30f, 36f).forEach { dx -> drawCircle(t(mezcla(TERCIOPELO, e.c1, 0.3f), oscuro, 0.1f), 1.8f * u, Offset(c.x + dx * u, c.y + 22 * u)) }
                val pts = listOf(Offset(c.x - 30 * u, c.y + 6 * u), Offset(c.x, c.y - 10 * u), Offset(c.x + 30 * u, c.y + 4 * u))
                drawPath(Path().apply { moveTo(pts[0].x, pts[0].y); quadraticTo(c.x - 16 * u, c.y - 14 * u, pts[1].x, pts[1].y); quadraticTo(c.x + 16 * u, c.y - 6 * u, pts[2].x, pts[2].y) },
                    t(RUBI, oscuro, 0.1f), style = Stroke(1.2f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2.2f * u, 2.4f * u))))
                listOf(e.c1, RUBI, mezcla(e.c2, ZAFIRO, 0.5f)).forEachIndexed { i, col ->
                    val p = pts[i]
                    drawOval(Color.Black.copy(alpha = 0.25f), Offset(p.x - 5 * u, p.y + 9 * u), Size(10 * u, 3 * u))
                    drawPath(Path().apply { moveTo(p.x - 4 * u, p.y + 2 * u); lineTo(p.x, p.y + 10 * u); lineTo(p.x + 4 * u, p.y + 2 * u); close() }, t(ORO_OSC, oscuro, 0.2f))
                    gema(Offset(p.x, p.y - 3 * u), 5f * u, col, oscuro)
                }
            }
        }
    }

    // ---------------------------------------------------------------- Notas

    override fun DrawScope.tableroNotas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val n = caja(area(MuebleVida.NOTAS))
        // El muro de la posada.
        sillares(Rect(0f, 0f, w, h), 7f * u, oscuro, e.semilla, u)
        drawRect(Brush.radialGradient(listOf(Color.Transparent, Color.Black.copy(alpha = if (oscuro) 0.45f else 0.2f)), Offset(w / 2, h * 0.5f), w * 0.62f))
        // El marco del tablón, de madera oscura.
        val fr = Rect(n.left - 3.4f * u, n.top - 2f * u, n.right + 3.4f * u, n.bottom + 2f * u)
        drawRect(Color.Black.copy(alpha = 0.45f), Offset(fr.left + 1f * u, fr.top + 1.4f * u), fr.size)
        listOf(Rect(fr.left, fr.top, n.left, fr.bottom), Rect(n.right, fr.top, fr.right, fr.bottom)).forEachIndexed { i, p ->
            tabla(p, MADERA, oscuro, e.semilla + 30 + i, u, vertical = true)
            drawRect(t(MADERA_OSC, oscuro), p.topLeft, p.size, style = Stroke(0.4f * u))
            // Los flejes de hierro de los postes, con sus clavos.
            var y = p.top + 6f * u
            while (y < p.bottom - 4f * u) {
                drawRect(t(HIERRO, oscuro, 0.2f), Offset(p.left - 0.3f * u, y), Size(p.width + 0.6f * u, 1.8f * u))
                drawRect(t(HIERRO_LUZ, oscuro, 0.3f), Offset(p.left - 0.3f * u, y), Size(p.width + 0.6f * u, 0.3f * u))
                clavo(Offset(p.center.x, y + 0.9f * u), 0.45f * u, oscuro, ORO_OSC, ORO_LUZ)
                y += 14f * u
            }
        }
        // El fondo de tablas claras: el nicho.
        val fondo = nicho(e, oscuro, FRESNO)
        drawRect(fondo, n.topLeft, n.size)
        val nt = 9
        for (k in 0 until nt) {
            val r = Rect(n.left + n.width * k / nt, n.top, n.left + n.width * (k + 1) / nt, n.bottom)
            vetas(r, MADERA.copy(alpha = if (oscuro) 0.1f else 0.08f), e.semilla * 3 + k, 1.6f * u, vertical = true, grosor = 0.25f * u)
            if (k > 0) drawLine(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.12f), Offset(r.left, r.top), Offset(r.left, r.bottom), 0.3f * u)
        }
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.14f), Color.Transparent), n.top, n.top + 3f * u), n.topLeft, Size(n.width, 3f * u))
        // La corona tallada: la cornisa, la banda de anillos y la esmeralda con runas a los lados.
        val cor = Rect(fr.left - 1.6f * u, h * 0.012f, fr.right + 1.6f * u, n.top)
        drawRect(Color.Black.copy(alpha = 0.4f), Offset(cor.left + 0.8f * u, cor.top + 1f * u), cor.size)
        tabla(cor, MADERA, oscuro, e.semilla + 40, u)
        val cornisa = Rect(cor.left - 0.8f * u, cor.top, cor.right + 0.8f * u, cor.top + 2.2f * u)
        drawRect(Brush.verticalGradient(listOf(t(MADERA_LUZ, oscuro), t(MADERA_OSC, oscuro)), cornisa.top, cornisa.bottom), cornisa.topLeft, cornisa.size)
        val bandaR = Rect(cor.left + 2f * u, cornisa.bottom + 0.8f * u, cor.right - 2f * u, cor.bottom - 1.4f * u)
        drawRect(t(MADERA_OSC, oscuro), bandaR.topLeft, bandaR.size)
        val placa = Rect(w / 2 - 12f * u, bandaR.top - 0.4f * u, w / 2 + 12f * u, bandaR.bottom + 0.4f * u)
        bandaRunas(Rect(bandaR.left + 5f * u, bandaR.top + bandaR.height * 0.18f, placa.left - 2f * u, bandaR.bottom - bandaR.height * 0.18f), oscuro, u, e.semilla, 3.6f * u)
        bandaRunas(Rect(placa.right + 2f * u, bandaR.top + bandaR.height * 0.18f, bandaR.right - 5f * u, bandaR.bottom - bandaR.height * 0.18f), oscuro, u, e.semilla + 5, 3.6f * u)
        drawRect(t(MADERA, oscuro), placa.topLeft, placa.size)
        anillos(Rect(placa.left + 1f * u, placa.top + 0.6f * u, placa.right - 1f * u, placa.bottom - 0.6f * u), oscuro, u)
        gema(placa.center, 2.6f * u, mezcla(ESMERALDA, e.acento, 0.3f), oscuro)
        listOf(cor.left + 2.6f * u, cor.right - 2.6f * u).forEach { x -> gema(Offset(x, bandaR.center.y), 1.2f * u, RUBI, oscuro) }
        drawRect(Color.Black.copy(alpha = 0.4f), Offset(cor.left, cor.bottom - 0.5f * u), Size(cor.width, 0.5f * u))
        // La repisa de abajo, con sus ménsulas.
        val rep = Rect(fr.left - 1.6f * u, n.bottom, fr.right + 1.6f * u, n.bottom + 3.4f * u)
        drawRect(Color.Black.copy(alpha = 0.4f), Offset(rep.left + 0.8f * u, rep.top + 1f * u), rep.size)
        tabla(rep, MADERA_LUZ, oscuro, e.semilla + 50, u)
        drawRect(t(MADERA_OSC, oscuro), rep.topLeft, rep.size, style = Stroke(0.35f * u))
        listOf(rep.left + 5f * u, rep.right - 9f * u, w / 2 - 2f * u).forEach { x ->
            val m = Path().apply { moveTo(x, rep.bottom); lineTo(x + 4f * u, rep.bottom); lineTo(x + 4f * u, rep.bottom + 1.6f * u); quadraticTo(x + 1f * u, rep.bottom + 1.8f * u, x + 2f * u, h - 0.5f * u); lineTo(x, h - 0.5f * u); close() }
            drawPath(m, t(MADERA_OSC, oscuro))
            drawLine(t(MADERA_LUZ, oscuro).copy(alpha = 0.6f), Offset(x + 0.4f * u, rep.bottom), Offset(x + 0.4f * u, h - 0.6f * u), 0.3f * u)
        }
        listOf(fr.left + 1.6f * u, fr.right - 1.6f * u).forEach { x -> clavo(Offset(x, rep.center.y), 0.6f * u, oscuro, ORO, ORO_LUZ) }
    }

    override fun DrawScope.notaTablero(e: EstadoNota, oscuro: Boolean) {
        val r = e.rect; val w = r.width; val h = r.height; val s = minOf(w, h) / 100f
        // La página del cronista: vitela de borde barbado, con su sombra en las tablas.
        val hoja = Rect(r.left + w * 0.025f, r.top + h * 0.025f, r.right - w * 0.025f, r.bottom - h * 0.025f)
        val forma = barbado(hoja, 41 + e.indice * 7, 28, 0.014f)
        translate(1.3f * s, 2f * s) { drawPath(forma, Color.Black.copy(alpha = if (oscuro) 0.55f else 0.3f)) }
        val papel = if (oscuro) mezcla(VITELA, NOCHE, 0.32f) else VITELA
        drawPath(forma, papel)
        clipPath(forma) {
            val b = 7f * s
            drawRect(Brush.verticalGradient(listOf(VITELA_OSC.copy(alpha = 0.45f), Color.Transparent), hoja.top, hoja.top + b), hoja.topLeft, Size(hoja.width, b))
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, VITELA_OSC.copy(alpha = 0.45f)), hoja.bottom - b, hoja.bottom), Offset(hoja.left, hoja.bottom - b), Size(hoja.width, b))
            drawRect(Brush.horizontalGradient(listOf(VITELA_OSC.copy(alpha = 0.3f), Color.Transparent), hoja.left, hoja.left + b), hoja.topLeft, Size(b, hoja.height))
            drawRect(Brush.horizontalGradient(listOf(Color.Transparent, VITELA_OSC.copy(alpha = 0.3f)), hoja.right - b, hoja.right), Offset(hoja.right - b, hoja.top), Size(b, hoja.height))
        }
        drawPath(forma, mezcla(VITELA_OSC, Color.Black, if (oscuro) 0.45f else 0.2f), style = Stroke(0.8f * s))
        // Los nudos de las esquinas de abajo: dos lazos entrelazados con tinta.
        val tn = TINTA.copy(alpha = 0.55f)
        listOf(Offset(r.left + w * 0.075f, r.bottom - h * 0.075f), Offset(r.right - w * 0.075f, r.bottom - h * 0.075f)).forEach { k ->
            drawCircle(tn, 2.2f * s, k + Offset(-1.3f * s, 0f), style = Stroke(0.6f * s))
            drawCircle(tn, 2.2f * s, k + Offset(1.3f * s, 0f), style = Stroke(0.6f * s))
            drawCircle(tn, 2.2f * s, k + Offset(0f, -1.3f * s), style = Stroke(0.6f * s))
        }
        // El detalle del tipo, en la esquina de arriba a la derecha.
        val esq = Offset(r.right - w * 0.09f, r.top + h * 0.095f)
        when (e.tipo) {
            "idea" -> gema(esq, 3.2f * s, mezcla(ESMERALDA, e.acento, 0.25f), oscuro = false)
            "pensamiento" -> {
                // La luna creciente de oro, con su estrellita.
                val luna = Path().apply {
                    addOval(Rect(esq.x - 4f * s, esq.y - 4f * s, esq.x + 4f * s, esq.y + 4f * s))
                }
                clipPath(Path().apply { addRect(Rect(esq.x - 6f * s, esq.y - 6f * s, esq.x + 6f * s, esq.y + 6f * s)) }) {
                    drawPath(luna, ORO)
                    drawCircle(papel, 3.5f * s, esq + Offset(1.9f * s, -1.1f * s))
                }
                drawPath(estrella(esq + Offset(3.2f * s, 2.6f * s), 1.5f * s, 4, 0.35f), ORO_OSC)
            }
            "proposito" -> {
                drawCircle(VITELA_OSC, 4.2f * s, esq)
                rosa(esq, 4f * s, ORO, TINTA, RUBI)
            }
            else -> {
                // La cinta roja que cuelga por el margen y la esquina de abajo doblada.
                val cx = r.right - w * 0.06f
                drawRect(Color.Black.copy(alpha = 0.25f), Offset(cx - 1.4f * s + 0.6f * s, r.top), Size(2.8f * s, h * 0.42f))
                drawRect(RUBI, Offset(cx - 1.4f * s, r.top), Size(2.8f * s, h * 0.42f))
                drawPath(Path().apply { moveTo(cx - 1.4f * s, r.top + h * 0.42f); lineTo(cx, r.top + h * 0.39f); lineTo(cx + 1.4f * s, r.top + h * 0.42f); lineTo(cx + 1.4f * s, r.top + h * 0.44f); lineTo(cx - 1.4f * s, r.top + h * 0.44f); close() }, papel)
                val d = 9f * s
                val ex = hoja.right; val ey = hoja.bottom
                drawPath(Path().apply { moveTo(ex - d, ey); lineTo(ex, ey - d); lineTo(ex, ey); close() }, if (oscuro) mezcla(FRESNO, NOCHE, 0.85f) else mezcla(FRESNO, Color.White, 0.2f))
                drawPath(Path().apply { moveTo(ex - d, ey); lineTo(ex, ey - d); lineTo(ex - d, ey - d); close() }, mezcla(VITELA_OSC, NOCHE, if (oscuro) 0.3f else 0f))
                drawLine(Color.Black.copy(alpha = 0.25f), Offset(ex - d, ey), Offset(ex, ey - d), 0.5f * s)
            }
        }
        // El clavo de arriba; si la nota está fijada, de oro y con un sello de lacre con cintas abajo.
        val clavoC = Offset(r.center.x, r.top + h * 0.075f)
        if (e.fijada) {
            val rs = 5f * s
            val sc = Offset(r.center.x, r.bottom - h * 0.07f)
            listOf(-1f, 1f).forEach { k ->
                drawPath(Path().apply { moveTo(sc.x + k * 1.2f * s, sc.y); lineTo(sc.x + k * 5f * s, sc.y + rs * 1.3f); lineTo(sc.x + k * 2.8f * s, sc.y + rs * 1.1f); lineTo(sc.x + k * 0.4f * s, sc.y + rs * 1.4f); close() },
                    mezcla(TERCIOPELO, e.acento, 0.35f))
            }
            drawCircle(Color.Black.copy(alpha = 0.3f), rs, sc + Offset(0.8f * s, 1f * s))
            drawCircle(RUBI, rs, sc)
            drawCircle(mezcla(RUBI, Color.Black, 0.35f), rs * 0.72f, sc, style = Stroke(0.8f * s))
            runa(3, sc, rs * 0.9f, mezcla(RUBI, Color.Black, 0.45f), 0.8f * s)
            drawCircle(Color.White.copy(alpha = 0.35f), rs * 0.22f, sc - Offset(rs * 0.4f, rs * 0.4f))
            halo(clavoC, 7f * s, ORO_LUZ.copy(alpha = 0.55f))
            clavo(clavoC, 2.8f * s, oscuro = false, metal = ORO, luz = Color(0xFFFFF0B8))
        } else clavo(clavoC, 2.3f * s, oscuro = false)
    }

    override fun tintaNota(oscuro: Boolean): Color = TINTA
}
