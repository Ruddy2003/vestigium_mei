package io.github.ruddy2003.vestigium.ui.vida

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
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
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import kotlin.math.cos
import kotlin.math.sin

/**
 * Gótico: los muebles de Vida y de Diario de la capilla de vitrales, de la misma casa que su armario de sacristía del Ideario,
 * su biblioteca con rosetón y su relicario de logros: roble tallado con tracería (arcos apuntados, tréboles, cuadrifolios,
 * pináculos con sus crochets), piedra caliza en sillares, la bóveda azul con estrellas de oro, vidrieras emplomadas de rubí,
 * zafiro, oro, esmeralda y amatista, vitela, pan de oro y cirios.
 * - Social: la sala capitular vista de frente: el muro de sillería, la bóveda estrellada con sus nervios y su clave dorada,
 *   las vidrieras en lanceta a los lados, los haces de columnillas con capitel de hojas y una arandela de hierro con su cirio;
 *   el doselete de roble con su arquería de tréboles y sus pináculos sobre el paño de honor (el nicho: vitela de día, azul de
 *   bóveda de noche) orlado de rubí con cuadrifolios de oro; abajo, la sillería del coro con sus tracerías, sus pomos de flor
 *   de lis y sus paneles de pergamino plegado, sobre el piso de losas en damero.
 * - Mapas: la mesa del cabildo vista desde arriba: el tablero de tablas de roble con su canto moldurado y sus cantoneras de
 *   bronce, el paño azul estrellado con su fleco de oro y el mapa de vitela (el nicho) clavado con tachuelas doradas; a un lado
 *   el astrolabio de latón con su red y su alidada, el compás y la palmatoria encendida; al otro el libro de horas abierto con
 *   su inicial iluminada y el tintero de cuerno con su pluma; al frente, el faldón con su arquería ciega, la cerradura de
 *   hierro y las patas con su pináculo.
 * - Otras vidas: el retablo de cinco calles: la crestería de gabletes con crochets y florón, un rosetón calado en cada uno,
 *   los pináculos, el guardapolvo azul con estrellas, las calles de pan de oro con su arco trilobulado (el nicho), los
 *   pilarcillos dorados entre ellas, la predela con cuadrifolios sobre rubí y zafiro y la grada de piedra.
 * - Íconos: la Santa Capilla con sus vidrieras entre contrafuertes, su tejado de plomo con crestería y su aguja; la vitela de
 *   grisalla que un dinero de plata raspa y deja ver la vidriera; tres rosetas de vidrio clavadas en el mapa con el camino
 *   rubricado en rojo.
 * - Notas: el tablón del capítulo: el marco de roble con su gablete y su rosetón, la arquería de tréboles, los contrafuertes
 *   con pináculo, el muro encalado con su despiece fingido de sillares (el nicho) y la repisa con dos cirios; las notas son
 *   hojas de vitela con su filete de oro y sus hojas de hiedra en el margen, clavadas con un clavo de hierro, con un detalle
 *   según el tipo (la estrella de la bóveda, el trébol azul, la flor de lis roja o la campanilla con la esquina doblada) y,
 *   si están fijadas, con el canto dorado y un clavo de oro que brilla.
 */
internal object VidaGotico : DisenoVida {
    // ---------------------------------------------------------------- materiales (los de su armario, su biblioteca y su Hall)
    private val ROBLE = Color(0xFF5A3E24)
    private val ROBLE_LUZ = Color(0xFF80603A)
    private val ROBLE_OSC = Color(0xFF2E1F12)
    private val VETA = Color(0xFF1B120B)
    private val ORO = Color(0xFFC9A24E)
    private val ORO_LUZ = Color(0xFFF0D58A)
    private val ORO_OSC = Color(0xFF8A6A24)
    private val PIEDRA = Color(0xFFD9CFBB)
    private val PIEDRA_LUZ = Color(0xFFEDE6D6)
    private val PIEDRA_OSC = Color(0xFFA89D86)
    private val JUNTA = Color(0xFF8E8573)
    private val BOVEDA = Color(0xFF1C2A5C)
    private val BOVEDA_OSC = Color(0xFF111A40)
    private val VITELA = Color(0xFFEDE3C9)
    private val VITELA_OSC = Color(0xFFD6C7A0)
    private val CAL = Color(0xFFF3ECDD)
    private val OCRE_ROJO = Color(0xFFA0472E)
    private val RUBI = Color(0xFFA0222A)
    private val ZAFIRO = Color(0xFF2A4C9A)
    private val ESMERALDA = Color(0xFF2E7A4C)
    private val AMATISTA = Color(0xFF5A3A8A)
    private val AMBAR = Color(0xFFD8A73A)
    private val PLOMO = Color(0xFF1C1A22)
    private val HIERRO = Color(0xFF2A282C)
    private val HIERRO_LUZ = Color(0xFF6E6A72)
    private val PLATA = Color(0xFFD5D7DE)
    private val CERA = Color(0xFFF2EAD6)
    private val LLAMA = Color(0xFFFFC870)
    private val CUERNO = Color(0xFF3A2E22)
    private val TINTA = Color(0xFF1B1830)
    private val NOCHE = Color(0xFF0C1230)
    /** Los vidrios de las vidrieras: zafiro, rubí, oro, esmeralda y amatista. */
    private val VIDRIOS = listOf(ZAFIRO, RUBI, AMBAR, ESMERALDA, AMATISTA)

    /** El material de día o de noche: de noche, la madera y la piedra en la penumbra azul de la capilla. */
    private fun t(c: Color, oscuro: Boolean, cuanto: Float = 0.45f): Color = if (oscuro) mezcla(c, NOCHE, cuanto) else c

    private fun mezcla(a: Color, b: Color, f: Float): Color = lerp(a, b, f.coerceIn(0f, 1f))

    override fun area(m: MuebleVida): Rect = when (m) {
        MuebleVida.SOCIAL -> Rect(0.13f, 0.19f, 0.87f, 0.8f)
        MuebleVida.MAPAS -> Rect(0.18f, 0.09f, 0.82f, 0.7f)
        MuebleVida.OTRAS_VIDAS -> Rect(0.075f, 0.25f, 0.925f, 0.84f)
        MuebleVida.NOTAS -> Rect(0.075f, 0.18f, 0.925f, 0.875f)
    }

    // ---------------------------------------------------------------- ayudas de dibujo

    /** Un rectángulo dado en fracciones del lienzo, en píxeles. */
    private fun DrawScope.caja(r: Rect) = Rect(size.width * r.left, size.height * r.top, size.width * r.right, size.height * r.bottom)

    private fun Rect.crece(d: Float) = Rect(left - d, top - d, right + d, bottom + d)

    private fun DrawScope.rect(c: Color, r: Rect) = drawRect(c, r.topLeft, r.size)

    /** Un número estable de 0 a 1 para lo que varía (vetas, sillares, vidrios). */
    private fun azar(semilla: Int, k: Int): Float {
        var x = semilla * 374761393 + k * 668265263
        x = (x xor (x ushr 13)) * 1274126177
        x = x xor (x ushr 16)
        return (x and 0x7fffffff) / 2147483647f
    }

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
                    val x = if (vertical) r.left + v else r.left + a
                    val y = if (vertical) r.top + a else r.top + v
                    if (s == 0) p.moveTo(x, y) else p.lineTo(x, y)
                }
                drawPath(p, color.copy(alpha = color.alpha * (0.3f + 0.7f * azar(semilla, k + 400))), style = Stroke(grosor * (0.5f + azar(semilla, k + 500))))
            }
        }
    }

    /** Una pieza de roble con su veta, luz arriba y sombra abajo. */
    private fun DrawScope.roble(r: Rect, oscuro: Boolean, semilla: Int, u: Float, base: Color = ROBLE, vertical: Boolean = false) {
        rect(t(base, oscuro), r)
        vetas(r, t(VETA, oscuro).copy(alpha = 0.5f), semilla, 1.1f * u, vertical, 0.3f * u)
        drawRect(Color.White.copy(alpha = if (oscuro) 0.05f else 0.12f), r.topLeft, Size(r.width, 0.3f * u))
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(r.left, r.bottom - 0.3f * u), Size(r.width, 0.3f * u))
    }

    /** Un bisel: luz arriba e izquierda, sombra abajo y derecha. */
    private fun DrawScope.bisel(r: Rect, luz: Color, sombra: Color, ancho: Float) {
        drawRect(luz, r.topLeft, Size(r.width, ancho))
        drawRect(luz.copy(alpha = luz.alpha * 0.7f), r.topLeft, Size(ancho, r.height))
        drawRect(sombra, Offset(r.left, r.bottom - ancho), Size(r.width, ancho))
        drawRect(sombra.copy(alpha = sombra.alpha * 0.8f), Offset(r.right - ancho, r.top), Size(ancho, r.height))
    }

    /** El muro de sillería: hiladas de piedra caliza a soga, con su junta, su tono que varía y la luz en el lecho de arriba. */
    private fun DrawScope.sillares(r: Rect, alto: Float, oscuro: Boolean, semilla: Int, u: Float) {
        clipRect(r.left, r.top, r.right, r.bottom) {
            rect(t(PIEDRA, oscuro, 0.62f), r)
            val largo = alto * 2.3f
            var y = r.top; var f = 0
            while (y < r.bottom) {
                var x = r.left - (if (f % 2 == 0) largo * 0.25f else largo * 0.75f)
                var k = 0
                while (x < r.right) {
                    val tono = mezcla(mezcla(PIEDRA, PIEDRA_LUZ, 0.4f * azar(semilla + f, k + 50)), PIEDRA_OSC, 0.3f * azar(semilla + f, k))
                    drawRect(t(tono, oscuro, 0.62f), Offset(x, y), Size(largo, alto))
                    drawRect(Color.White.copy(alpha = if (oscuro) 0.04f else 0.22f), Offset(x, y + 0.25f * u), Size(largo, alto * 0.1f))
                    drawRect(Color.Black.copy(alpha = if (oscuro) 0.12f else 0.06f), Offset(x, y + alto * 0.82f), Size(largo, alto * 0.18f))
                    drawLine(t(JUNTA, oscuro, 0.62f), Offset(x, y), Offset(x, y + alto), 0.3f * u)
                    x += largo; k++
                }
                drawLine(t(JUNTA, oscuro, 0.62f), Offset(r.left, y), Offset(r.right, y), 0.3f * u)
                y += alto; f++
            }
        }
    }

    /** El piso de losas en damero, gris y blanco, con su brillo. */
    private fun DrawScope.losas(r: Rect, lado: Float, oscuro: Boolean) {
        clipRect(r.left, r.top, r.right, r.bottom) {
            var y = r.top; var f = 0
            while (y < r.bottom) {
                var x = r.left; var k = 0
                while (x < r.right) {
                    val c = if ((k + f) % 2 == 0) Color(0xFFCFC8B8) else Color(0xFF55504A)
                    drawRect(t(c, oscuro, 0.6f), Offset(x, y), Size(lado + 0.5f, lado + 0.5f))
                    drawRect(Color.White.copy(alpha = if (oscuro) 0.04f else 0.12f), Offset(x + lado * 0.1f, y + lado * 0.1f), Size(lado * 0.4f, lado * 0.12f))
                    x += lado; k++
                }
                y += lado; f++
            }
        }
    }

    /** Un arco apuntado (ojival): [r] con la punta arriba al centro; [flecha] es el alto de la parte curva. */
    private fun ojiva(r: Rect, flecha: Float = r.width * 0.9f): Path = Path().apply {
        val cx = r.center.x
        val fl = flecha.coerceAtMost(r.height)
        moveTo(r.left, r.bottom); lineTo(r.left, r.top + fl)
        cubicTo(r.left, r.top + fl * 0.45f, cx - r.width * 0.22f, r.top + fl * 0.1f, cx, r.top)
        cubicTo(cx + r.width * 0.22f, r.top + fl * 0.1f, r.right, r.top + fl * 0.45f, r.right, r.top + fl)
        lineTo(r.right, r.bottom); close()
    }

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

    /** Un trébol: tres lóbulos alrededor de [c] (el de arriba, apuntando al cielo). */
    private fun DrawScope.trebol(c: Offset, r: Float, color: Color) {
        for (k in 0 until 3) {
            val a = Math.toRadians(-90.0 + k * 120.0)
            drawCircle(color, r * 0.48f, Offset(c.x + r * 0.5f * cos(a).toFloat(), c.y + r * 0.5f * sin(a).toFloat()))
        }
    }

    /** Un cuadrifolio: cuatro lóbulos alrededor de [c], con su botón al centro. */
    private fun DrawScope.cuadrifolio(c: Offset, r: Float, color: Color, centro: Color) {
        listOf(Offset(0f, -1f), Offset(1f, 0f), Offset(0f, 1f), Offset(-1f, 0f)).forEach { d -> drawCircle(color, r * 0.48f, c + d * (r * 0.5f)) }
        drawCircle(centro, r * 0.26f, c)
    }

    /** Una flor de lis de [s] de alto, centrada en [c]. */
    private fun DrawScope.florDeLis(c: Offset, s: Float, color: Color) {
        drawPath(Path().apply {
            moveTo(c.x, c.y - s * 0.48f); quadraticTo(c.x + s * 0.17f, c.y - s * 0.16f, c.x, c.y + s * 0.06f); quadraticTo(c.x - s * 0.17f, c.y - s * 0.16f, c.x, c.y - s * 0.48f); close()
            moveTo(c.x + s * 0.04f, c.y + s * 0.04f); cubicTo(c.x + s * 0.3f, c.y - s * 0.04f, c.x + s * 0.46f, c.y - s * 0.28f, c.x + s * 0.32f, c.y - s * 0.36f)
            quadraticTo(c.x + s * 0.36f, c.y - s * 0.1f, c.x + s * 0.08f, c.y + s * 0.1f); close()
            moveTo(c.x - s * 0.04f, c.y + s * 0.04f); cubicTo(c.x - s * 0.3f, c.y - s * 0.04f, c.x - s * 0.46f, c.y - s * 0.28f, c.x - s * 0.32f, c.y - s * 0.36f)
            quadraticTo(c.x - s * 0.36f, c.y - s * 0.1f, c.x - s * 0.08f, c.y + s * 0.1f); close()
            moveTo(c.x - s * 0.06f, c.y + s * 0.14f); lineTo(c.x + s * 0.06f, c.y + s * 0.14f); lineTo(c.x + s * 0.02f, c.y + s * 0.46f); lineTo(c.x - s * 0.02f, c.y + s * 0.46f); close()
        }, color)
        drawRect(color, Offset(c.x - s * 0.22f, c.y + s * 0.06f), Size(s * 0.44f, s * 0.09f))
    }

    /** Un pináculo: la aguja con su mitad en luz, sus crochets a los lados y su florón dorado; [b] es el centro de su base. */
    private fun DrawScope.pinaculo(b: Offset, ancho: Float, alto: Float, oscuro: Boolean, u: Float, cuerpo: Color = ROBLE_LUZ, oro: Boolean = true) {
        val aguja = Path().apply { moveTo(b.x - ancho / 2, b.y); lineTo(b.x, b.y - alto); lineTo(b.x + ancho / 2, b.y); close() }
        drawPath(aguja, t(cuerpo, oscuro))
        drawPath(Path().apply { moveTo(b.x, b.y); lineTo(b.x, b.y - alto); lineTo(b.x + ancho / 2, b.y); close() }, Color.Black.copy(alpha = 0.22f))
        drawPath(aguja, t(ROBLE_OSC, oscuro).copy(alpha = 0.8f), style = Stroke(0.25f * u))
        val rc = (ancho * 0.16f).coerceAtLeast(0.35f * u)
        for (k in 1..3) {
            val f = k / 4f
            val y = b.y - alto * f
            val dx = ancho / 2 * (1 - f)
            drawCircle(t(cuerpo, oscuro), rc, Offset(b.x - dx - rc * 0.7f, y))
            drawCircle(t(mezcla(cuerpo, Color.Black, 0.25f), oscuro), rc, Offset(b.x + dx + rc * 0.7f, y))
        }
        val fc = Offset(b.x, b.y - alto - rc * 0.8f)
        drawCircle(t(if (oro) ORO else cuerpo, oscuro, 0.25f), rc * 1.3f, fc)
        drawCircle(Color.White.copy(alpha = 0.35f), rc * 0.5f, fc - Offset(rc * 0.4f, rc * 0.4f))
    }

    /** Un gablete: el frontón agudo sobre [base] (de izquierda a derecha en su y), con crochets en los lados, su florón y un rosetón calado. */
    private fun DrawScope.gablete(izq: Float, der: Float, y: Float, alto: Float, oscuro: Boolean, u: Float, relleno: Color, calado: Color) {
        val cx = (izq + der) / 2
        val p = Path().apply {
            moveTo(izq, y)
            quadraticTo(izq + (cx - izq) * 0.55f, y - alto * 0.42f, cx, y - alto)
            quadraticTo(der - (der - cx) * 0.55f, y - alto * 0.42f, der, y)
            close()
        }
        drawPath(p, relleno)
        drawPath(Path().apply { moveTo(cx, y); lineTo(cx, y - alto); quadraticTo(der - (der - cx) * 0.55f, y - alto * 0.42f, der, y); close() }, Color.Black.copy(alpha = 0.16f))
        drawPath(p, t(ROBLE_OSC, oscuro), style = Stroke(0.3f * u))
        // El rosetón calado del tímpano.
        val rr = minOf((der - izq) * 0.2f, alto * 0.22f)
        val rc = Offset(cx, y - alto * 0.34f)
        drawCircle(calado, rr, rc)
        trebol(rc, rr * 0.95f, relleno)
        drawCircle(calado, rr * 0.18f, rc)
        drawCircle(t(ROBLE_OSC, oscuro), rr, rc, style = Stroke(0.25f * u))
        // Los crochets por los dos lados.
        val rcr = (alto * 0.055f).coerceAtLeast(0.4f * u)
        for (k in 1..4) {
            val f = k / 5f
            val yy = y - alto * f
            val dx = (cx - izq) * (1 - f)
            drawCircle(relleno, rcr, Offset(cx - dx - rcr * 0.5f, yy - rcr * 0.3f))
            drawCircle(mezcla(relleno, Color.Black, 0.2f), rcr, Offset(cx + dx + rcr * 0.5f, yy - rcr * 0.3f))
        }
        // El florón.
        florDeLis(Offset(cx, y - alto - rcr * 1.6f), rcr * 4f, t(ORO, oscuro, 0.25f))
    }

    /** Una vidriera en lanceta: el derrame de piedra, el vidrio emplomado en paneles con un medallón y su brillo. */
    private fun DrawScope.vidriera(r: Rect, oscuro: Boolean, semilla: Int, u: Float) {
        val marco = r.crece(1.1f * u)
        drawPath(ojiva(marco, marco.width * 0.95f), t(PIEDRA_OSC, oscuro, 0.62f))
        val p = ojiva(r, r.width * 0.95f)
        val plomo = t(PLOMO, oscuro, 0.2f)
        drawPath(p, plomo)
        val f = if (oscuro) 0.35f else 0f
        clipPath(p) {
            val lado = r.width / 2f
            var y = r.top; var fila = 0
            while (y < r.bottom) {
                for (c in 0 until 2) {
                    val col = VIDRIOS[((azar(semilla + fila, c) * 5).toInt()).coerceIn(0, 4)]
                    drawRect(mezcla(col, NOCHE, f), Offset(r.left + c * lado + 0.25f * u, y + 0.25f * u), Size(lado - 0.5f * u, lado * 1.1f - 0.5f * u))
                    drawRect(Color.White.copy(alpha = if (oscuro) 0.06f else 0.16f), Offset(r.left + c * lado + 0.5f * u, y + 0.5f * u), Size(lado * 0.25f, lado * 0.5f))
                }
                // Cada tres paneles, un medallón redondo de otro color.
                if (fila % 3 == 1) {
                    val mc = Offset(r.center.x, y + lado * 0.55f)
                    drawCircle(plomo, lado * 0.82f, mc)
                    drawCircle(mezcla(VIDRIOS[(fila + semilla) % 5], NOCHE, f), lado * 0.68f, mc)
                    drawCircle(mezcla(AMBAR, NOCHE, f), lado * 0.24f, mc)
                }
                y += lado * 1.1f; fila++
            }
            // El brillo de la luz que entra (de día) o la penumbra (de noche).
            drawRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = if (oscuro) 0.05f else 0.2f), Color.Transparent), r.top, r.bottom), r.topLeft, r.size)
        }
        drawPath(p, plomo, style = Stroke(0.45f * u))
        drawLine(plomo, Offset(r.center.x, r.top + r.width * 0.5f), Offset(r.center.x, r.bottom), 0.35f * u)
        // El alféizar de piedra.
        val alf = Rect(marco.left - 0.6f * u, r.bottom, marco.right + 0.6f * u, r.bottom + 1.2f * u)
        rect(t(PIEDRA_LUZ, oscuro, 0.62f), alf)
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(alf.left, alf.bottom), Size(alf.width, 0.5f * u))
    }

    /** Un rosetón: el aro de piedra, los pétalos de vidrio en sectores, el anillo de óculos y el botón de oro. */
    private fun DrawScope.roseton(c: Offset, r: Float, oscuro: Boolean, u: Float) {
        val f = if (oscuro) 0.3f else 0f
        drawCircle(Color.Black.copy(alpha = 0.3f), r * 1.12f, c + Offset(0.3f * u, 0.5f * u))
        drawCircle(t(PIEDRA, oscuro, 0.55f), r * 1.12f, c)
        drawCircle(t(PLOMO, oscuro, 0.2f), r, c)
        for (k in 0 until 8) {
            drawArc(mezcla(VIDRIOS[k % 5], NOCHE, f), -90f + k * 45f + 2f, 41f, true, Offset(c.x - r * 0.68f, c.y - r * 0.68f), Size(r * 1.36f, r * 1.36f))
        }
        for (k in 0 until 12) {
            val a = Math.toRadians(k * 30.0)
            val p = Offset(c.x + r * 0.84f * cos(a).toFloat(), c.y + r * 0.84f * sin(a).toFloat())
            drawCircle(mezcla(if (k % 2 == 0) ZAFIRO else RUBI, NOCHE, f), r * 0.11f, p)
        }
        drawCircle(t(PIEDRA, oscuro, 0.55f), r * 0.72f, c, style = Stroke(r * 0.07f))
        drawCircle(mezcla(AMBAR, NOCHE, f), r * 0.24f, c)
        drawCircle(t(PLOMO, oscuro, 0.2f), r * 0.24f, c, style = Stroke(r * 0.05f))
        drawCircle(Color.White.copy(alpha = if (oscuro) 0.06f else 0.2f), r * 0.5f, c - Offset(r * 0.3f, r * 0.3f))
        drawCircle(t(PIEDRA_OSC, oscuro, 0.55f), r * 1.12f, c, style = Stroke(0.3f * u))
    }

    /** Las estrellas de oro de la bóveda, sembradas en [r]. */
    private fun DrawScope.estrellas(r: Rect, semilla: Int, paso: Float, tam: Float, oscuro: Boolean) {
        clipRect(r.left, r.top, r.right, r.bottom) {
            var y = r.top + paso * 0.4f; var f = 0
            while (y < r.bottom + paso) {
                var x = r.left + (if (f % 2 == 0) paso * 0.3f else paso * 0.8f)
                var k = 0
                while (x < r.right + paso) {
                    val dx = (azar(semilla + f, k) - 0.5f) * paso * 0.4f
                    val dy = (azar(semilla + f, k + 9) - 0.5f) * paso * 0.3f
                    val tt = tam * (0.7f + 0.6f * azar(semilla + f, k + 19))
                    drawPath(estrella(Offset(x + dx, y + dy), tt, 8, 0.42f), (if (oscuro) ORO_LUZ else ORO).copy(alpha = 0.9f))
                    x += paso; k++
                }
                y += paso * 0.55f; f++
            }
        }
    }

    /** Un panel de pergamino plegado tallado en el roble: pliegues verticales con sus remates curvos. */
    private fun DrawScope.pergaminoPlegado(r: Rect, oscuro: Boolean, u: Float) {
        rect(t(mezcla(ROBLE, ROBLE_OSC, 0.3f), oscuro), r)
        val n = 3
        val pw = r.width / n
        for (k in 0 until n) {
            val x = r.left + pw * k
            drawLine(Color.White.copy(alpha = if (oscuro) 0.1f else 0.2f), Offset(x + pw * 0.28f, r.top + pw * 0.45f), Offset(x + pw * 0.28f, r.bottom - pw * 0.45f), pw * 0.18f)
            drawLine(Color.Black.copy(alpha = 0.35f), Offset(x + pw * 0.72f, r.top + pw * 0.45f), Offset(x + pw * 0.72f, r.bottom - pw * 0.45f), pw * 0.18f)
            drawArc(Color.Black.copy(alpha = 0.35f), 180f, 180f, false, Offset(x + pw * 0.15f, r.top + pw * 0.12f), Size(pw * 0.7f, pw * 0.5f), style = Stroke(0.3f * u))
            drawArc(Color.White.copy(alpha = 0.15f), 0f, 180f, false, Offset(x + pw * 0.15f, r.bottom - pw * 0.62f), Size(pw * 0.7f, pw * 0.5f), style = Stroke(0.3f * u))
        }
        drawRect(Color.Black.copy(alpha = 0.4f), r.topLeft, r.size, style = Stroke(0.3f * u))
    }

    /** Una mancha radial suave (la luz de la vela). */
    private fun DrawScope.halo(c: Offset, r: Float, color: Color) =
        drawCircle(Brush.radialGradient(listOf(color, color.copy(alpha = 0f)), c, r.coerceAtLeast(1f)), r, c)

    /** Un clavo de cabeza redonda, con su brillo. */
    private fun DrawScope.clavo(c: Offset, r: Float, metal: Color = HIERRO, luz: Color = HIERRO_LUZ) {
        drawCircle(Color.Black.copy(alpha = 0.35f), r, c + Offset(r * 0.35f, r * 0.45f))
        drawCircle(metal, r, c)
        drawCircle(luz, r * 0.42f, c - Offset(r * 0.3f, r * 0.3f))
    }

    /** Un cirio de pie con su llama y su halo; [base] es donde asienta la cera. */
    private fun DrawScope.cirio(base: Offset, ancho: Float, alto: Float, oscuro: Boolean) {
        val cuerpo = Rect(base.x - ancho / 2, base.y - alto, base.x + ancho / 2, base.y)
        drawRect(t(CERA, oscuro, 0.2f), cuerpo.topLeft, cuerpo.size)
        drawRect(Color.Black.copy(alpha = 0.15f), Offset(cuerpo.right - ancho * 0.35f, cuerpo.top), Size(ancho * 0.35f, cuerpo.height))
        drawOval(t(CERA, oscuro, 0.1f), Offset(cuerpo.left - ancho * 0.08f, cuerpo.top - ancho * 0.15f), Size(ancho * 1.16f, ancho * 0.4f))
        drawLine(t(CERA, oscuro, 0.1f), Offset(cuerpo.left + ancho * 0.15f, cuerpo.top), Offset(cuerpo.left + ancho * 0.15f, cuerpo.top + alto * 0.25f), ancho * 0.22f, StrokeCap.Round)
        llama(Offset(base.x, cuerpo.top - ancho * 0.1f), ancho * 1.9f, oscuro)
    }

    /** Una llama de vela vista de frente, con su halo. */
    private fun DrawScope.llama(base: Offset, alto: Float, oscuro: Boolean) {
        halo(base - Offset(0f, alto * 0.5f), alto * 2.4f, LLAMA.copy(alpha = if (oscuro) 0.45f else 0.3f))
        val p = Path().apply {
            moveTo(base.x, base.y - alto)
            quadraticTo(base.x + alto * 0.42f, base.y - alto * 0.3f, base.x, base.y)
            quadraticTo(base.x - alto * 0.42f, base.y - alto * 0.3f, base.x, base.y - alto)
            close()
        }
        drawPath(p, Color(0xFFF5A62E))
        drawOval(Color(0xFFFFF3C4), Offset(base.x - alto * 0.12f, base.y - alto * 0.55f), Size(alto * 0.24f, alto * 0.45f))
    }

    /** Un haz de columnillas: el fuste central con dos columnillas, el capitel de hojas y la basa; de [arriba] a [abajo]. */
    private fun DrawScope.columna(cx: Float, arriba: Float, abajo: Float, ancho: Float, oscuro: Boolean, u: Float) {
        val piedra = t(PIEDRA, oscuro, 0.58f); val luz = t(PIEDRA_LUZ, oscuro, 0.58f); val osc = t(PIEDRA_OSC, oscuro, 0.58f)
        drawRect(Color.Black.copy(alpha = 0.18f), Offset(cx - ancho * 0.5f + 0.6f * u, arriba), Size(ancho, abajo - arriba))
        listOf(-0.36f to 0.26f, 0.36f to 0.26f, 0f to 0.46f).forEach { (dx, a) ->
            val x0 = cx + ancho * dx - ancho * a / 2
            drawRect(Brush.horizontalGradient(listOf(luz, piedra, osc), x0, x0 + ancho * a), Offset(x0, arriba), Size(ancho * a, abajo - arriba))
            drawLine(Color.Black.copy(alpha = 0.2f), Offset(x0, arriba), Offset(x0, abajo), 0.2f * u)
        }
        // El capitel de hojas: la campana, dos hojas que se enroscan y el ábaco.
        val ch = ancho * 0.75f
        val cap = Rect(cx - ancho * 0.62f, arriba, cx + ancho * 0.62f, arriba + ch)
        drawPath(Path().apply { moveTo(cap.left, cap.top); lineTo(cap.right, cap.top); lineTo(cx + ancho * 0.45f, cap.bottom); lineTo(cx - ancho * 0.45f, cap.bottom); close() }, piedra)
        listOf(-1f, 1f).forEach { s ->
            drawCircle(luz, ch * 0.24f, Offset(cx + s * ancho * 0.48f, cap.top + ch * 0.32f))
            drawCircle(osc, ch * 0.24f, Offset(cx + s * ancho * 0.48f, cap.top + ch * 0.32f), style = Stroke(0.2f * u))
        }
        drawCircle(luz, ch * 0.22f, Offset(cx, cap.top + ch * 0.55f))
        drawRect(luz, Offset(cap.left - ancho * 0.08f, cap.top - ch * 0.25f), Size(cap.width + ancho * 0.16f, ch * 0.28f))
        drawRect(osc, Offset(cap.left - ancho * 0.08f, cap.top + ch * 0.01f), Size(cap.width + ancho * 0.16f, ch * 0.06f))
        drawLine(Color.Black.copy(alpha = 0.3f), Offset(cx - ancho * 0.45f, cap.bottom), Offset(cx + ancho * 0.45f, cap.bottom), 0.3f * u)
        // La basa: el toro y el plinto.
        val bh = ancho * 0.5f
        drawRoundRect(luz, Offset(cx - ancho * 0.58f, abajo - bh * 1.4f), Size(ancho * 1.16f, bh * 0.6f), CornerRadius(bh * 0.3f))
        drawRect(piedra, Offset(cx - ancho * 0.66f, abajo - bh * 0.85f), Size(ancho * 1.32f, bh * 0.85f))
        drawRect(osc, Offset(cx - ancho * 0.66f, abajo - bh * 0.85f), Size(ancho * 1.32f, bh * 0.85f), style = Stroke(0.2f * u))
    }

    // ---------------------------------------------------------------- Social

    override fun DrawScope.muebleSocial(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val n = caja(area(MuebleVida.SOCIAL))
        // El muro de sillería y el piso de losas.
        sillares(Rect(0f, 0f, w, h), 6.2f * u, oscuro, e.semilla, u)
        val piso = h * 0.955f
        losas(Rect(0f, piso, w, h), 4.2f * u, oscuro)
        // La bóveda azul con estrellas de oro, sus nervios de piedra y la clave dorada.
        val yb = h * 0.085f
        val boveda = Rect(0f, 0f, w, yb)
        rect(t(BOVEDA, oscuro, 0.25f), boveda)
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.25f), Color.Transparent), 0f, yb), boveda.topLeft, boveda.size)
        estrellas(boveda, e.semilla, 7f * u, 0.9f * u, oscuro)
        val lado = n.left
        val colX = lado * 0.7f
        val clave = Offset(w / 2, yb * 0.3f)
        listOf(colX, w - colX).forEach { x ->
            val p = Path().apply { moveTo(x, yb); quadraticTo(x + (clave.x - x) * 0.2f, clave.y - yb * 0.1f, clave.x, clave.y) }
            drawPath(p, t(PIEDRA_OSC, oscuro, 0.55f), style = Stroke(2.2f * u))
            drawPath(p, t(PIEDRA_LUZ, oscuro, 0.55f), style = Stroke(1f * u))
        }
        drawCircle(Color.Black.copy(alpha = 0.3f), 2.2f * u, clave + Offset(0.3f * u, 0.4f * u))
        drawCircle(t(ORO, oscuro, 0.2f), 2.2f * u, clave)
        drawPath(estrella(clave, 1.7f * u, 8, 0.5f), t(ORO_LUZ, oscuro, 0.2f))
        drawCircle(t(RUBI, oscuro, 0.2f), 0.6f * u, clave)
        // La imposta de piedra que corre bajo la bóveda, con su filete dorado.
        val imposta = Rect(0f, yb, w, yb + 1.8f * u)
        rect(t(PIEDRA_LUZ, oscuro, 0.58f), imposta)
        drawRect(t(PIEDRA_OSC, oscuro, 0.58f), Offset(0f, imposta.bottom - 0.6f * u), Size(w, 0.6f * u))
        drawLine(t(ORO, oscuro, 0.3f), Offset(0f, imposta.top + 0.5f * u), Offset(w, imposta.top + 0.5f * u), 0.3f * u)
        drawRect(Color.Black.copy(alpha = 0.18f), Offset(0f, imposta.bottom), Size(w, 0.8f * u))
        // Las vidrieras en lanceta, una a cada lado.
        val vw = minOf(lado * 0.3f, 5.5f * u)
        listOf(lado * 0.3f, w - lado * 0.3f).forEachIndexed { i, x ->
            vidriera(Rect(x - vw / 2, h * 0.2f, x + vw / 2, h * 0.7f), oscuro, e.semilla + i * 11, u)
        }
        // Los haces de columnillas, del capitel bajo la imposta al piso.
        val cw = minOf(lado * 0.24f, 4.6f * u)
        listOf(colX, w - colX).forEach { x -> columna(x, imposta.bottom, piso, cw, oscuro, u) }
        // El halo de los cirios, detrás del paño (no cae sobre el nicho).
        val yc = h * 0.5f
        listOf(colX - cw * 1.25f, w - colX + cw * 1.25f).forEach { x -> halo(Offset(x, yc - 4.5f * u), 13f * u, LLAMA.copy(alpha = if (oscuro) 0.3f else 0.16f)) }
        // El paño de honor: su sombra, su orla de rubí con cuadrifolios de oro, el fleco y el campo (el nicho).
        val orla = n.crece(2.2f * u)
        drawRect(Color.Black.copy(alpha = 0.28f), Offset(orla.left + 0.6f * u, orla.top + 0.9f * u), orla.size)
        rect(t(RUBI, oscuro, 0.35f), orla)
        val oro = t(ORO, oscuro, 0.25f)
        drawRect(oro, orla.topLeft, orla.size, style = Stroke(0.4f * u))
        val bm = 1.1f * u
        fun cuadrifolios(a: Offset, z: Offset) {
            val largo = (z - a).getDistance(); val k = (largo / (3.6f * u)).toInt().coerceAtLeast(1)
            for (i in 0..k) cuadrifolio(a + (z - a) * (i / k.toFloat()), 1.4f * u, oro, t(ZAFIRO, oscuro, 0.3f))
        }
        cuadrifolios(Offset(orla.left + bm, orla.top + bm), Offset(orla.right - bm, orla.top + bm))
        cuadrifolios(Offset(orla.left + bm, orla.bottom - bm), Offset(orla.right - bm, orla.bottom - bm))
        cuadrifolios(Offset(orla.left + bm, orla.top + bm), Offset(orla.left + bm, orla.bottom - bm))
        cuadrifolios(Offset(orla.right - bm, orla.top + bm), Offset(orla.right - bm, orla.bottom - bm))
        val campo = if (oscuro) mezcla(e.fondo, BOVEDA, 0.3f) else mezcla(e.fondo, VITELA, 0.4f)
        rect(campo, n)
        clipRect(n.left, n.top, n.right, n.bottom) {
            // El adamascado, apenas: rombos y flores de lis al tresbolillo.
            val p = 12f * u
            var d0 = n.left - n.height
            while (d0 < n.right + n.height) {
                drawLine((if (oscuro) ORO else ORO_OSC).copy(alpha = 0.06f), Offset(d0, n.top), Offset(d0 + n.height, n.bottom), 0.25f * u)
                drawLine((if (oscuro) ORO else ORO_OSC).copy(alpha = 0.06f), Offset(d0 + n.height, n.top), Offset(d0, n.bottom), 0.25f * u)
                d0 += p
            }
            var y = n.top + p / 2; var f = 0
            while (y < n.bottom) {
                var x = n.left + (if (f % 2 == 0) p / 2 else p)
                while (x < n.right) { florDeLis(Offset(x, y), 3f * u, (if (oscuro) ORO else ORO_OSC).copy(alpha = 0.08f)); x += p }
                y += p / 2; f++
            }
            // Los pliegues del paño colgado.
            var x = n.left
            var k = 0
            while (x < n.right) {
                drawRect(Brush.horizontalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = if (oscuro) 0.05f else 0.035f), Color.Transparent), x, x + 9f * u), Offset(x, n.top), Size(9f * u, n.height))
                x += 9f * u + 6f * u * azar(e.semilla, k); k++
            }
        }
        // El doselete de roble: la arquería de tréboles, el fleco de oro y los pináculos.
        val dosel = Rect(orla.left - 1.5f * u, imposta.bottom + 0.3f * u, orla.right + 1.5f * u, orla.top - 0.2f * u)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(dosel.left + 0.5f * u, dosel.top + 0.8f * u), dosel.size)
        roble(dosel, oscuro, e.semilla + 5, u, ROBLE_LUZ)
        val nA = ((dosel.width) / (8.5f * u)).toInt().coerceAtLeast(6)
        val aw = dosel.width / nA
        for (j in 0 until nA) {
            val r = Rect(dosel.left + aw * j + aw * 0.14f, dosel.top + dosel.height * 0.18f, dosel.left + aw * (j + 1) - aw * 0.14f, dosel.bottom - dosel.height * 0.28f)
            drawPath(ojiva(r, r.width * 0.8f), t(ROBLE_OSC, oscuro))
            trebol(Offset(r.center.x, r.top + r.width * 0.55f), r.width * 0.42f, t(ROBLE_LUZ, oscuro))
            drawLine(t(ROBLE_LUZ, oscuro), Offset(r.center.x, r.top + r.width * 0.75f), Offset(r.center.x, r.bottom), 0.45f * u)
        }
        drawLine(oro, Offset(dosel.left, dosel.top + 0.4f * u), Offset(dosel.right, dosel.top + 0.4f * u), 0.5f * u)
        val fleco = Rect(dosel.left, dosel.bottom - dosel.height * 0.22f, dosel.right, dosel.bottom)
        rect(t(ORO_OSC, oscuro, 0.3f), fleco)
        var fx = fleco.left + 0.4f * u
        while (fx < fleco.right) { drawLine(oro, Offset(fx, fleco.top), Offset(fx, fleco.bottom), 0.35f * u); fx += 0.9f * u }
        for (j in 0..nA) pinaculo(Offset(dosel.left + aw * j, dosel.top + 0.2f * u), 2.2f * u, yb * 0.55f, oscuro, u)
        // La arandela de hierro en cada columna, con su cirio encendido.
        listOf(-1f, 1f).forEach { s ->
            val x = if (s < 0) colX else w - colX
            val cx = x + s * cw * 1.25f
            drawRect(t(HIERRO, oscuro, 0.2f), Offset(x - cw * 0.6f, yc - 0.6f * u), Size(cw * 1.2f, 1.2f * u))
            drawLine(t(HIERRO, oscuro, 0.2f), Offset(x, yc), Offset(cx, yc + 1.5f * u), 0.6f * u)
            drawLine(t(HIERRO, oscuro, 0.2f), Offset(x, yc + 3f * u), Offset(cx, yc + 1.5f * u), 0.45f * u)
            drawOval(t(HIERRO_LUZ, oscuro, 0.2f), Offset(cx - 1.7f * u, yc + 0.9f * u), Size(3.4f * u, 1.2f * u))
            cirio(Offset(cx, yc + 1.3f * u), 1.3f * u, 5.5f * u, oscuro)
        }
        // La sillería del coro: el respaldo de tracerías, el asiento, los brazales y los paneles de pergamino plegado.
        val sx0 = orla.left - 0.6f * u; val sx1 = orla.right + 0.6f * u
        val respaldo = Rect(sx0, orla.bottom + 0.8f * u, sx1, orla.bottom + 0.8f * u + (piso - orla.bottom) * 0.38f)
        val asiento = Rect(sx0 - 0.8f * u, respaldo.bottom, sx1 + 0.8f * u, respaldo.bottom + 1.8f * u)
        val frente = Rect(sx0, asiento.bottom, sx1, piso)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(sx0, respaldo.top + 0.6f * u), Size(sx1 - sx0, piso - respaldo.top))
        roble(respaldo, oscuro, e.semilla + 21, u, ROBLE)
        roble(frente, oscuro, e.semilla + 22, u, ROBLE_OSC)
        val nS = ((sx1 - sx0) / (13f * u)).toInt().coerceAtLeast(5)
        val sw = (sx1 - sx0) / nS
        for (j in 0 until nS) {
            val x0 = sx0 + sw * j
            // Dos arcos ciegos por silla en el respaldo.
            for (k in 0 until 2) {
                val r = Rect(x0 + sw * (0.12f + 0.4f * k), respaldo.top + respaldo.height * 0.16f, x0 + sw * (0.48f + 0.4f * k), respaldo.bottom - respaldo.height * 0.12f)
                drawPath(ojiva(r, r.width * 0.85f), t(ROBLE_OSC, oscuro))
                drawCircle(t(ROBLE_LUZ, oscuro), r.width * 0.16f, Offset(r.center.x, r.top + r.width * 0.55f), style = Stroke(0.25f * u))
            }
            // El panel de pergamino plegado del frente.
            pergaminoPlegado(Rect(x0 + sw * 0.16f, frente.top + frente.height * 0.18f, x0 + sw * 0.84f, frente.bottom - frente.height * 0.18f), oscuro, u)
        }
        drawLine(oro, Offset(respaldo.left, respaldo.top + 0.3f * u), Offset(respaldo.right, respaldo.top + 0.3f * u), 0.4f * u)
        roble(asiento, oscuro, e.semilla + 23, u, ROBLE_LUZ)
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(asiento.left, asiento.bottom), Size(asiento.width, 0.7f * u))
        for (j in 0..nS) {
            val x = sx0 + sw * j
            val br = Rect(x - 0.8f * u, respaldo.top + respaldo.height * 0.45f, x + 0.8f * u, piso)
            roble(br, oscuro, e.semilla + 30 + j, u, ROBLE_LUZ, vertical = true)
            drawCircle(t(ROBLE_LUZ, oscuro), 1.1f * u, Offset(x, br.top))
            drawCircle(Color.White.copy(alpha = 0.18f), 0.4f * u, Offset(x - 0.35f * u, br.top - 0.35f * u))
        }
        // Los testeros de la sillería, con su pomo de flor de lis.
        listOf(sx0 - 2.6f * u, sx1 + 0.4f * u).forEachIndexed { i, x ->
            val r = Rect(x, orla.bottom - 3.5f * u, x + 2.2f * u, piso)
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(r.left + 0.5f * u, r.top + 0.5f * u), r.size)
            roble(r, oscuro, e.semilla + 40 + i, u, ROBLE, vertical = true)
            bisel(r, Color.White.copy(alpha = 0.12f), Color.Black.copy(alpha = 0.3f), 0.3f * u)
            florDeLis(Offset(r.center.x, r.top - 1.8f * u), 4.2f * u, t(ROBLE_LUZ, oscuro))
        }
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(0f, piso), Size(w, 0.8f * u))
    }

    // ---------------------------------------------------------------- Mapas

    override fun DrawScope.muebleMapas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val n = caja(area(MuebleVida.MAPAS))
        // El piso de losas de la sala, en penumbra bajo la mesa.
        losas(Rect(0f, 0f, w, h), 5f * u, oscuro)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.45f else 0.3f))
        // Las patas: postes de roble con su contrafuerte y su pináculo tallado.
        val faldon = Rect(w * 0.02f, h * 0.78f, w * 0.98f, h * 0.9f)
        listOf(w * 0.025f, w * 0.975f - 5f * u).forEachIndexed { i, x ->
            val p = Rect(x, faldon.top, x + 5f * u, h * 0.985f)
            drawRect(Color.Black.copy(alpha = 0.35f), Offset(p.left - 1f * u, p.bottom - 0.6f * u), Size(p.width + 2f * u, 1.6f * u))
            roble(p, oscuro, e.semilla + 60 + i, u, ROBLE, vertical = true)
            drawRect(t(ROBLE_OSC, oscuro), Offset(p.center.x - 0.4f * u, faldon.bottom), Size(0.8f * u, p.bottom - faldon.bottom - 1f * u))
            pinaculo(Offset(p.center.x, p.bottom - 1.2f * u), 3f * u, (p.bottom - faldon.bottom) * 0.55f, oscuro, u, ROBLE_LUZ, oro = false)
            bisel(p, Color.White.copy(alpha = 0.1f), Color.Black.copy(alpha = 0.3f), 0.3f * u)
        }
        // El faldón: la arquería ciega de tréboles, el filete de oro y la cerradura de hierro.
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(faldon.left, faldon.bottom), Size(faldon.width, 1.2f * u))
        roble(faldon, oscuro, e.semilla + 70, u, ROBLE)
        val nA = (faldon.width / (7f * u)).toInt().coerceAtLeast(8)
        val aw = faldon.width / nA
        for (j in 0 until nA) {
            if (j == nA / 2 || j == (nA - 1) / 2) continue
            val r = Rect(faldon.left + aw * j + aw * 0.15f, faldon.top + faldon.height * 0.2f, faldon.left + aw * (j + 1) - aw * 0.15f, faldon.bottom - faldon.height * 0.14f)
            drawPath(ojiva(r, r.width * 0.85f), t(ROBLE_OSC, oscuro))
            trebol(Offset(r.center.x, r.top + r.width * 0.55f), r.width * 0.4f, t(ROBLE_LUZ, oscuro))
            drawLine(t(ROBLE_LUZ, oscuro), Offset(r.center.x, r.top + r.width * 0.78f), Offset(r.center.x, r.bottom), 0.4f * u)
        }
        val ce = Rect(faldon.center.x - 3.5f * u, faldon.top + faldon.height * 0.18f, faldon.center.x + 3.5f * u, faldon.bottom - faldon.height * 0.18f)
        drawRoundRect(t(HIERRO, oscuro, 0.2f), ce.topLeft, ce.size, CornerRadius(0.6f * u))
        listOf(ce.topLeft, Offset(ce.right, ce.top), Offset(ce.left, ce.bottom), ce.bottomRight).forEach { c -> drawCircle(t(HIERRO, oscuro, 0.2f), 1f * u, c) }
        listOf(Offset(ce.left + 1f * u, ce.top + 1f * u), Offset(ce.right - 1f * u, ce.top + 1f * u), Offset(ce.left + 1f * u, ce.bottom - 1f * u), Offset(ce.right - 1f * u, ce.bottom - 1f * u)).forEach { c -> clavo(c, 0.45f * u, t(HIERRO_LUZ, oscuro, 0.2f), Color(0xFFB0ACB4)) }
        drawCircle(Color.Black, 0.8f * u, Offset(ce.center.x, ce.center.y - 0.4f * u))
        drawRect(Color.Black, Offset(ce.center.x - 0.3f * u, ce.center.y - 0.4f * u), Size(0.6f * u, 1.8f * u))
        drawLine(t(ORO, oscuro, 0.25f), Offset(faldon.left, faldon.top + 0.4f * u), Offset(faldon.right, faldon.top + 0.4f * u), 0.5f * u)
        // El tablero de tablas de roble visto desde arriba, con su canto moldurado.
        val mesa = Rect(w * 0.012f, h * 0.012f, w * 0.988f, faldon.top + 0.5f * u)
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(mesa.left + 0.8f * u, mesa.top + 1.2f * u), mesa.size)
        rect(t(ROBLE_OSC, oscuro), mesa)
        val tab = mesa.crece(-1.2f * u)
        val nT = 5
        for (k in 0 until nT) {
            val r = Rect(tab.left, tab.top + tab.height * k / nT, tab.right, tab.top + tab.height * (k + 1) / nT)
            rect(t(mezcla(ROBLE, ROBLE_LUZ, 0.2f + 0.3f * azar(e.semilla, k)), oscuro), r)
            vetas(r, t(VETA, oscuro).copy(alpha = 0.45f), e.semilla * 5 + k, 1.2f * u, grosor = 0.3f * u)
            drawLine(Color.Black.copy(alpha = 0.45f), Offset(r.left, r.top), Offset(r.right, r.top), 0.3f * u)
        }
        drawRect(t(ROBLE_LUZ, oscuro), tab.topLeft, tab.size, style = Stroke(0.5f * u))
        drawRect(Color.Black.copy(alpha = 0.3f), tab.crece(-0.9f * u).topLeft, tab.crece(-0.9f * u).size, style = Stroke(0.3f * u))
        // Las cantoneras de bronce con su cuadrifolio.
        listOf(tab.topLeft, Offset(tab.right, tab.top), Offset(tab.left, tab.bottom), tab.bottomRight).forEach { c ->
            val sx = if (c.x < w / 2) 1f else -1f; val sy = if (c.y < h / 2) 1f else -1f
            drawPath(Path().apply { moveTo(c.x, c.y); lineTo(c.x + sx * 5f * u, c.y); lineTo(c.x, c.y + sy * 5f * u); close() }, t(ORO, oscuro, 0.25f))
            cuadrifolio(Offset(c.x + sx * 1.6f * u, c.y + sy * 1.6f * u), 1.6f * u, t(ORO_OSC, oscuro, 0.25f), t(ORO_LUZ, oscuro, 0.25f))
        }
        // El paño azul estrellado bajo el mapa, con su fleco de oro.
        val pano = Rect(n.left - 4.5f * u, n.top - 2.6f * u, n.right + 4.5f * u, n.bottom + 3f * u)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(pano.left + 0.6f * u, pano.top + 0.9f * u), pano.size)
        rect(t(BOVEDA, oscuro, 0.25f), pano)
        estrellas(pano, e.semilla + 3, 5.5f * u, 0.65f * u, oscuro)
        drawRect(t(ORO, oscuro, 0.25f), pano.topLeft, pano.size, style = Stroke(0.4f * u))
        listOf(pano.left, pano.right).forEach { x ->
            var y = pano.top + 0.6f * u
            while (y < pano.bottom) { drawLine(t(ORO, oscuro, 0.25f), Offset(x, y), Offset(x + (if (x < w / 2) -1.4f else 1.4f) * u, y), 0.35f * u); y += 1f * u }
        }
        // El mapa de vitela (el nicho), con su sombra y su doble filete rubricado.
        val hoja = n.crece(1.4f * u)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(hoja.left + 0.5f * u, hoja.top + 0.8f * u), hoja.size)
        rect(if (oscuro) mezcla(e.fondo, VITELA_OSC, 0.14f) else mezcla(VITELA, VITELA_OSC, 0.25f), hoja)
        rect(if (oscuro) mezcla(e.fondo, BOVEDA, 0.18f) else mezcla(e.fondo, VITELA, 0.5f), n)
        clipRect(n.left, n.top, n.right, n.bottom) {
            for (k in 0 until 18) {
                val y = n.top + n.height * azar(e.semilla + 7, k)
                drawLine((if (oscuro) Color.White else VITELA_OSC).copy(alpha = if (oscuro) 0.02f else 0.25f), Offset(n.left, y), Offset(n.right, y + n.height * 0.03f), 0.25f * u)
            }
        }
        drawRect(t(RUBI, oscuro, 0.3f).copy(alpha = 0.8f), n.topLeft, n.size, style = Stroke(0.3f * u))
        listOf(hoja.topLeft, Offset(hoja.right, hoja.top), Offset(hoja.left, hoja.bottom), hoja.bottomRight).forEach { c ->
            clavo(c + Offset(if (c.x < w / 2) 0.7f * u else -0.7f * u, if (c.y < h / 2) 0.7f * u else -0.7f * u), 0.75f * u, t(ORO, oscuro, 0.2f), ORO_LUZ)
        }
        // A la izquierda: el astrolabio, el compás y la palmatoria encendida.
        val izq = Rect(tab.left + 1.5f * u, tab.top + 1.5f * u, pano.left - 1f * u, tab.bottom - 1.5f * u)
        val ra = minOf(izq.width * 0.44f, izq.height * 0.25f)
        astrolabio(Offset(izq.center.x, izq.top + ra * 1.15f), ra, oscuro, u)
        val c0 = Offset(izq.left + izq.width * 0.22f, izq.top + izq.height * 0.52f)
        compas(c0, Offset(izq.left + izq.width * 0.9f, izq.top + izq.height * 0.6f), Offset(izq.left + izq.width * 0.66f, izq.top + izq.height * 0.78f), oscuro, u)
        val pal = Offset(izq.left + izq.width * 0.3f, izq.bottom - izq.height * 0.08f)
        palmatoria(pal, minOf(izq.width * 0.18f, 3.6f * u), oscuro, u)
        // A la derecha: el libro de horas abierto y el tintero de cuerno con su pluma.
        val der = Rect(pano.right + 1f * u, tab.top + 1.5f * u, tab.right - 1.5f * u, tab.bottom - 1.5f * u)
        val lw = minOf(der.width * 0.94f, der.height * 0.7f)
        libroDeHoras(Rect(der.center.x - lw / 2, der.top + der.height * 0.04f, der.center.x + lw / 2, der.top + der.height * 0.04f + lw * 0.72f), oscuro, u)
        val ti = Offset(der.left + der.width * 0.35f, der.top + der.height * 0.78f)
        val rt = minOf(der.width * 0.16f, 3f * u)
        drawCircle(Color.Black.copy(alpha = 0.35f), rt * 1.1f, ti + Offset(0.4f * u, 0.6f * u))
        drawCircle(t(CUERNO, oscuro, 0.2f), rt * 1.1f, ti)
        drawCircle(t(ORO, oscuro, 0.25f), rt * 1.1f, ti, style = Stroke(0.35f * u))
        drawCircle(Color.Black, rt * 0.55f, ti)
        drawCircle(Color.White.copy(alpha = 0.25f), rt * 0.3f, ti - Offset(rt * 0.5f, rt * 0.5f))
        pluma(ti, Offset(der.right - der.width * 0.05f, der.top + der.height * 0.52f), oscuro, u)
    }

    /** El astrolabio de latón: la madre con su limbo graduado, la red calada con sus punteros, la alidada y la argolla. */
    private fun DrawScope.astrolabio(c: Offset, r: Float, oscuro: Boolean, u: Float) {
        val laton = t(ORO, oscuro, 0.25f); val osc = t(ORO_OSC, oscuro, 0.25f); val luz = t(ORO_LUZ, oscuro, 0.25f)
        drawCircle(Color.Black.copy(alpha = 0.35f), r, c + Offset(0.5f * u, 0.8f * u))
        // La argolla y el trono.
        drawCircle(osc, r * 0.16f, c - Offset(0f, r * 1.08f), style = Stroke(r * 0.07f))
        drawPath(Path().apply { moveTo(c.x - r * 0.2f, c.y - r * 0.92f); quadraticTo(c.x, c.y - r * 1.12f, c.x + r * 0.2f, c.y - r * 0.92f); close() }, laton)
        drawCircle(laton, r, c)
        drawCircle(osc, r * 0.86f, c)
        for (k in 0 until 72) {
            val a = Math.toRadians(k * 5.0)
            val r0 = if (k % 6 == 0) r * 0.78f else r * 0.82f
            drawLine(luz.copy(alpha = 0.8f), Offset(c.x + r0 * cos(a).toFloat(), c.y + r0 * sin(a).toFloat()), Offset(c.x + r * 0.86f * cos(a).toFloat(), c.y + r * 0.86f * sin(a).toFloat()), 0.15f * u)
        }
        // La lámina con sus almicantarates.
        drawCircle(mezcla(laton, osc, 0.4f), r * 0.76f, c)
        drawCircle(osc, r * 0.5f, c + Offset(0f, r * 0.12f), style = Stroke(0.2f * u))
        drawCircle(osc, r * 0.3f, c + Offset(0f, r * 0.22f), style = Stroke(0.2f * u))
        drawLine(osc, Offset(c.x - r * 0.76f, c.y), Offset(c.x + r * 0.76f, c.y), 0.2f * u)
        // La red: el anillo de la eclíptica, sus brazos y los punteros de estrellas.
        drawCircle(luz, r * 0.48f, c + Offset(0f, -r * 0.2f), style = Stroke(r * 0.06f))
        drawCircle(luz, r * 0.7f, c, style = Stroke(r * 0.05f))
        for (k in 0 until 6) {
            val a = Math.toRadians(20.0 + k * 60.0)
            val p = Offset(c.x + r * 0.7f * cos(a).toFloat(), c.y + r * 0.7f * sin(a).toFloat())
            drawLine(luz, c, p, r * 0.04f)
            val q = Offset(c.x + r * 0.58f * cos(a + 0.3).toFloat(), c.y + r * 0.58f * sin(a + 0.3).toFloat())
            drawPath(Path().apply { moveTo(q.x, q.y); lineTo(p.x, p.y); lineTo(q.x + r * 0.06f, q.y); close() }, luz)
        }
        // La alidada y el eje.
        drawLine(osc, c - Offset(r * 0.8f, -r * 0.3f), c + Offset(r * 0.8f, -r * 0.3f), r * 0.1f, StrokeCap.Round)
        drawLine(luz.copy(alpha = 0.7f), c - Offset(r * 0.8f, -r * 0.27f), c + Offset(r * 0.8f, -r * 0.27f), r * 0.03f)
        drawCircle(luz, r * 0.08f, c)
        drawCircle(osc, r * 0.08f, c, style = Stroke(0.15f * u))
        drawCircle(Color.White.copy(alpha = if (oscuro) 0.08f else 0.22f), r * 0.4f, c - Offset(r * 0.4f, r * 0.4f))
    }

    /** El compás de hierro con su cabeza de latón, abierto entre [a] y [b] con la bisagra en [cab]. */
    private fun DrawScope.compas(cab: Offset, a: Offset, b: Offset, oscuro: Boolean, u: Float) {
        listOf(a, b).forEach { p ->
            val d = p - cab; val l = d.getDistance(); if (l < 1f) return@forEach
            val nx = -d.y / l; val ny = d.x / l
            val g = 0.75f * u
            val pata = Path().apply {
                moveTo(cab.x + nx * g, cab.y + ny * g); lineTo(p.x, p.y); lineTo(cab.x - nx * g, cab.y - ny * g); close()
            }
            translate(0.4f * u, 0.7f * u) { drawPath(pata, Color.Black.copy(alpha = 0.3f)) }
            drawPath(pata, t(HIERRO_LUZ, oscuro, 0.2f))
            drawPath(Path().apply { moveTo(cab.x, cab.y); lineTo(p.x, p.y); lineTo(cab.x - nx * g, cab.y - ny * g); close() }, Color.Black.copy(alpha = 0.25f))
            drawLine(Color.White.copy(alpha = 0.35f), cab + Offset(nx * g * 0.5f, ny * g * 0.5f), p, 0.2f * u)
        }
        drawCircle(Color.Black.copy(alpha = 0.3f), 1.7f * u, cab + Offset(0.4f * u, 0.6f * u))
        drawCircle(t(ORO, oscuro, 0.25f), 1.7f * u, cab)
        drawCircle(t(ORO_OSC, oscuro, 0.25f), 1.7f * u, cab, style = Stroke(0.25f * u))
        drawCircle(t(ORO_LUZ, oscuro, 0.25f), 0.6f * u, cab - Offset(0.4f * u, 0.4f * u))
    }

    /** La palmatoria de latón vista desde arriba, con su vela encendida. */
    private fun DrawScope.palmatoria(c: Offset, r: Float, oscuro: Boolean, u: Float) {
        halo(c, r * 4.5f, LLAMA.copy(alpha = if (oscuro) 0.4f else 0.22f))
        drawCircle(Color.Black.copy(alpha = 0.35f), r, c + Offset(0.4f * u, 0.6f * u))
        drawCircle(t(ORO, oscuro, 0.2f), r, c)
        drawCircle(t(ORO_OSC, oscuro, 0.2f), r, c, style = Stroke(0.25f * u))
        drawCircle(t(ORO_LUZ, oscuro, 0.2f), r * 0.75f, c, style = Stroke(0.2f * u))
        drawLine(t(ORO, oscuro, 0.2f), c + Offset(r, 0f), c + Offset(r * 1.6f, 0f), 0.6f * u, StrokeCap.Round)
        drawCircle(CERA, r * 0.45f, c)
        drawCircle(Color(0xFFFFE6A0), r * 0.26f, c)
        drawCircle(Color(0xFFF5A62E), r * 0.12f, c)
    }

    /** El libro de horas abierto: las tapas de terciopelo, las dos páginas con su inicial iluminada, sus renglones y su registro. */
    private fun DrawScope.libroDeHoras(r: Rect, oscuro: Boolean, u: Float) {
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(r.left + 0.6f * u, r.top + 0.9f * u), r.size)
        drawRoundRect(t(Color(0xFF7A1424), oscuro, 0.3f), r.topLeft, r.size, CornerRadius(0.6f * u))
        listOf(r.topLeft, Offset(r.right, r.top), Offset(r.left, r.bottom), r.bottomRight).forEach { c -> drawCircle(t(ORO, oscuro, 0.25f), 0.6f * u, c + Offset(if (c.x < r.center.x) 0.6f * u else -0.6f * u, if (c.y < r.center.y) 0.6f * u else -0.6f * u)) }
        val pag = Rect(r.left + 0.8f * u, r.top + 0.8f * u, r.right - 0.8f * u, r.bottom - 0.8f * u)
        val papel = t(VITELA, oscuro, 0.35f)
        listOf(Rect(pag.left, pag.top, pag.center.x, pag.bottom), Rect(pag.center.x, pag.top, pag.right, pag.bottom)).forEachIndexed { i, p ->
            rect(papel, p)
            drawRect(Brush.horizontalGradient(if (i == 0) listOf(Color.Transparent, Color.Black.copy(alpha = 0.22f)) else listOf(Color.Black.copy(alpha = 0.22f), Color.Transparent), p.left, p.right), p.topLeft, p.size)
            val m = p.width * 0.16f
            // La orla de hiedra en el margen y la inicial dorada.
            drawLine(t(ORO, oscuro, 0.25f), Offset(if (i == 0) p.left + m * 0.6f else p.right - m * 0.6f, p.top + m), Offset(if (i == 0) p.left + m * 0.6f else p.right - m * 0.6f, p.bottom - m), 0.3f * u)
            if (i == 0) {
                val ini = Rect(p.left + m * 1.2f, p.top + m, p.left + m * 1.2f + p.width * 0.28f, p.top + m + p.width * 0.28f)
                rect(t(ZAFIRO, oscuro, 0.3f), ini)
                drawRect(t(ORO, oscuro, 0.25f), ini.topLeft, ini.size, style = Stroke(0.25f * u))
                drawCircle(t(ORO_LUZ, oscuro, 0.25f), ini.width * 0.25f, ini.center)
            }
            var y = p.top + m * 1.1f; var k = 0
            while (y < p.bottom - m) {
                val x0 = if (i == 0 && y < p.top + m + p.width * 0.3f) p.left + m * 1.4f + p.width * 0.28f else p.left + m * 1.2f
                drawLine(TINTA.copy(alpha = 0.45f), Offset(x0, y), Offset(p.right - m * (1f + 0.6f * azar(i, k)), y), 0.25f * u)
                if (k % 4 == 2) drawLine(t(RUBI, oscuro, 0.25f).copy(alpha = 0.8f), Offset(x0, y), Offset(x0 + p.width * 0.15f, y), 0.3f * u)
                y += 1.1f * u; k++
            }
        }
        drawLine(Color.Black.copy(alpha = 0.4f), Offset(pag.center.x, pag.top), Offset(pag.center.x, pag.bottom), 0.3f * u)
        // El registro de seda que cuelga.
        drawLine(t(RUBI, oscuro, 0.25f), Offset(pag.center.x + 0.4f * u, pag.top), Offset(pag.center.x + 1.2f * u, r.bottom + 3f * u), 0.6f * u)
    }

    /** La pluma de ave: el cañón y las barbas, de [a] (la punta, en el tintero) a [b]. */
    private fun DrawScope.pluma(a: Offset, b: Offset, oscuro: Boolean, u: Float) {
        val d = b - a; val l = d.getDistance(); if (l < 1f) return
        val nx = -d.y / l; val ny = d.x / l
        val m = a + d * 0.35f
        val p = Path().apply {
            moveTo(m.x, m.y)
            quadraticTo(m.x + d.x * 0.3f + nx * 2.2f * u, m.y + d.y * 0.3f + ny * 2.2f * u, b.x, b.y)
            quadraticTo(m.x + d.x * 0.4f - nx * 1.2f * u, m.y + d.y * 0.4f - ny * 1.2f * u, m.x, m.y)
            close()
        }
        translate(0.4f * u, 0.7f * u) { drawPath(p, Color.Black.copy(alpha = 0.3f)) }
        drawPath(p, t(Color(0xFFF2EEE4), oscuro, 0.3f))
        drawLine(t(PIEDRA_OSC, oscuro, 0.3f), a, b, 0.3f * u)
        drawLine(Color(0xFF3A3020), a, a + d * 0.12f, 0.5f * u, StrokeCap.Round)
    }

    // ---------------------------------------------------------------- Otras vidas

    override fun DrawScope.muebleOtrasVidas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val n = caja(area(MuebleVida.OTRAS_VIDAS))
        // La grada de piedra y el muro de detrás.
        sillares(Rect(0f, 0f, w, h), 6f * u, oscuro, e.semilla, u)
        val grada = Rect(0f, h * 0.955f, w, h)
        rect(t(PIEDRA_LUZ, oscuro, 0.58f), grada)
        drawRect(t(PIEDRA_OSC, oscuro, 0.58f), Offset(0f, grada.top), Size(w, 0.6f * u))
        // El guardapolvo azul con estrellas, que enmarca todo el retablo.
        val retablo = Rect(w * 0.012f, h * 0.02f, w * 0.988f, grada.top)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(retablo.left + 0.8f * u, retablo.top + 1f * u), retablo.size)
        rect(t(BOVEDA, oscuro, 0.25f), retablo)
        estrellas(retablo, e.semilla + 1, 6f * u, 0.8f * u, oscuro)
        drawRect(t(ORO, oscuro, 0.25f), retablo.topLeft, retablo.size, style = Stroke(0.6f * u))
        val cuerpo = Rect(n.left - 3.2f * u, h * 0.17f, n.right + 3.2f * u, h * 0.955f)
        // La predela: los cuadrifolios de oro sobre rubí y zafiro, entre sus pilarcillos.
        val predela = Rect(cuerpo.left, n.bottom + 1.2f * u, cuerpo.right, grada.top - 0.8f * u)
        roble(predela, oscuro, e.semilla + 2, u, ROBLE)
        val cw = n.width / 5f
        for (i in 0 until 5) {
            val p = Rect(n.left + cw * i + cw * 0.12f, predela.top + predela.height * 0.2f, n.left + cw * (i + 1) - cw * 0.12f, predela.bottom - predela.height * 0.2f)
            rect(t(if (i % 2 == 0) RUBI else ZAFIRO, oscuro, 0.35f), p)
            drawRect(t(ORO, oscuro, 0.25f), p.topLeft, p.size, style = Stroke(0.4f * u))
            val rq = minOf(p.height * 0.42f, p.width * 0.2f)
            cuadrifolio(p.center, rq * 2f, t(ORO, oscuro, 0.25f), t(if (i % 2 == 0) ZAFIRO else RUBI, oscuro, 0.3f))
            listOf(p.left + p.width * 0.15f, p.right - p.width * 0.15f).forEach { x -> drawCircle(t(ORO_LUZ, oscuro, 0.25f), 0.5f * u, Offset(x, p.center.y)) }
        }
        drawRect(t(ORO, oscuro, 0.25f), Offset(predela.left, predela.top), Size(predela.width, 0.7f * u))
        drawRect(t(ORO_OSC, oscuro, 0.25f), Offset(predela.left, predela.bottom - 0.6f * u), Size(predela.width, 0.6f * u))
        // El banco de oro bajo las calles.
        val banco = Rect(cuerpo.left - 0.6f * u, n.bottom, cuerpo.right + 0.6f * u, n.bottom + 1.4f * u)
        rect(t(ORO, oscuro, 0.25f), banco)
        drawRect(t(ORO_LUZ, oscuro, 0.25f), banco.topLeft, Size(banco.width, 0.4f * u))
        // Las calles de pan de oro (el nicho): el campo, el estofado y los arcos trilobulados arriba.
        val campo = if (oscuro) mezcla(e.fondo, BOVEDA, 0.28f) else mezcla(e.fondo, ORO_LUZ, 0.2f)
        rect(campo, n)
        clipRect(n.left, n.top, n.right, n.bottom) {
            if (oscuro) {
                estrellas(n, e.semilla + 9, 6f * u, 0.5f * u, true)
                drawRect(campo.copy(alpha = 0.75f), n.topLeft, n.size)
            } else {
                var y = n.top + 1f * u
                while (y < n.bottom) {
                    var x = n.left + 1f * u
                    while (x < n.right) { drawCircle(ORO_OSC.copy(alpha = 0.1f), 0.22f * u, Offset(x, y)); x += 2.2f * u }
                    y += 2.2f * u
                }
            }
        }
        val tracer = t(ORO, oscuro, 0.25f)
        for (i in 0 until 5) {
            val c = Rect(n.left + cw * i, n.top, n.left + cw * (i + 1), n.bottom)
            // Las enjutas del arco, de roble dorado, y el arco de tres lóbulos.
            val arco = Rect(c.left + 0.9f * u, c.top + 0.7f * u, c.right - 0.9f * u, c.bottom)
            val fl = minOf(n.height * 0.2f, arco.width * 0.55f)
            val hueco = ojiva(arco, fl)
            clipRect(c.left, c.top, c.right, c.top + fl + 0.7f * u) {
                clipPath(hueco, clipOp = ClipOp.Difference) {
                    rect(t(ROBLE, oscuro), c)
                    vetas(c, t(VETA, oscuro).copy(alpha = 0.4f), e.semilla + i, 1f * u, grosor = 0.25f * u)
                    trebol(Offset(c.left + c.width * 0.13f, c.top + fl * 0.32f), minOf(fl * 0.4f, c.width * 0.12f), tracer)
                    trebol(Offset(c.right - c.width * 0.13f, c.top + fl * 0.32f), minOf(fl * 0.4f, c.width * 0.12f), tracer)
                }
            }
            drawPath(hueco, tracer, style = Stroke(0.55f * u))
            // Los lóbulos de la punta: dos medias lunas doradas que cuelgan del arco.
            val lob = minOf(arco.width * 0.1f, fl * 0.24f)
            listOf(-1f, 1f).forEach { s ->
                drawArc(tracer, if (s < 0) 200f else -20f, 160f, false, Offset(arco.center.x + s * arco.width * 0.25f - lob, arco.top + fl * 0.55f - lob * 0.6f), Size(lob * 2f, lob * 2f), style = Stroke(0.4f * u))
            }
            drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.28f else 0.12f), Color.Transparent), c.top, c.top + n.height * 0.3f), c.topLeft, Size(c.width, n.height * 0.3f))
        }
        // Los pilarcillos dorados entre las calles, con su aguja en la crestería.
        val corn = Rect(cuerpo.left, n.top - 2.4f * u, cuerpo.right, n.top)
        for (i in 1 until 5) {
            val x = n.left + cw * i
            val pr = Rect(x - 0.8f * u, corn.top, x + 0.8f * u, n.bottom)
            drawRect(Color.Black.copy(alpha = 0.25f), Offset(pr.right, pr.top), Size(0.6f * u, pr.height))
            drawRect(Brush.horizontalGradient(listOf(t(ORO_LUZ, oscuro, 0.25f), t(ORO, oscuro, 0.25f), t(ORO_OSC, oscuro, 0.25f)), pr.left, pr.right), pr.topLeft, pr.size)
            var y = n.top + n.height * 0.3f
            while (y < n.bottom - 2f * u) { drawRect(t(ORO_OSC, oscuro, 0.25f), Offset(pr.left - 0.3f * u, y), Size(pr.width + 0.6f * u, 0.5f * u)); y += n.height * 0.3f }
        }
        // Las entrecalles de los lados: haces de columnillas doradas sobre roble.
        listOf(Rect(cuerpo.left, corn.top, n.left, n.bottom), Rect(n.right, corn.top, cuerpo.right, n.bottom)).forEachIndexed { i, r ->
            roble(r, oscuro, e.semilla + 30 + i, u, ROBLE, vertical = true)
            listOf(0.3f, 0.7f).forEach { f ->
                val x = r.left + r.width * f
                drawRect(Brush.horizontalGradient(listOf(t(ORO_LUZ, oscuro, 0.25f), t(ORO_OSC, oscuro, 0.25f)), x - 0.45f * u, x + 0.45f * u), Offset(x - 0.45f * u, r.top), Size(0.9f * u, r.height))
            }
        }
        // La cornisa de oro con su friso de hojas.
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(corn.left, corn.bottom), Size(corn.width, 0.8f * u))
        rect(t(ORO, oscuro, 0.25f), corn)
        drawRect(t(ORO_LUZ, oscuro, 0.25f), corn.topLeft, Size(corn.width, 0.5f * u))
        var hx = corn.left + 1f * u
        while (hx < corn.right - 0.5f * u) { drawCircle(t(ORO_OSC, oscuro, 0.25f), 0.55f * u, Offset(hx, corn.center.y + 0.3f * u)); hx += 1.8f * u }
        // La crestería: un gablete sobre cada calle, con su rosetón calado, y pináculos entre uno y otro.
        val gAlto = corn.top - retablo.top - 3.5f * u
        for (i in 0 until 5) {
            val x0 = n.left + cw * i + cw * 0.08f; val x1 = n.left + cw * (i + 1) - cw * 0.08f
            gablete(x0, x1, corn.top, gAlto, oscuro, u, t(ORO, oscuro, 0.25f), t(BOVEDA_OSC, oscuro, 0.25f))
        }
        for (i in 0..5) {
            val x = n.left + cw * i
            pinaculo(Offset(x, corn.top), 2.4f * u, gAlto * 0.8f, oscuro, u, ORO_OSC)
        }
        listOf(cuerpo.left + (n.left - cuerpo.left) / 2, n.right + (cuerpo.right - n.right) / 2).forEach { x ->
            pinaculo(Offset(x, corn.top), 3f * u, gAlto * 0.95f, oscuro, u, ORO_OSC)
        }
    }

    // ---------------------------------------------------------------- Íconos

    override fun DrawScope.iconoMapa(id: String, e: EstadoIcono, oscuro: Boolean) {
        val w = size.width; val h = size.height
        val s = h / 70f
        val cx = w / 2; val base = h / 2 + 30f * s
        when (id) {
            "unesco" -> {
                // La Santa Capilla: la nave con sus vidrieras entre contrafuertes, el tejado de plomo con crestería y la aguja.
                drawOval(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.18f), Offset(cx - 40f * s, base - 2.5f * s), Size(80f * s, 5f * s))
                val nave = Rect(cx - 32f * s, base - 30f * s, cx + 32f * s, base)
                rect(t(PIEDRA, oscuro, 0.4f), nave)
                drawRect(Color.Black.copy(alpha = 0.12f), Offset(nave.left, nave.bottom - 4f * s), Size(nave.width, 4f * s))
                val nV = 4
                val paso = nave.width / nV
                for (k in 0 until nV) {
                    val r = Rect(nave.left + paso * k + paso * 0.24f, nave.top + 4f * s, nave.left + paso * (k + 1) - paso * 0.24f, nave.bottom - 4f * s)
                    drawPath(ojiva(r, r.width), t(PLOMO, oscuro, 0.2f))
                    clipPath(ojiva(r.crece(-0.6f * s), r.width)) {
                        var y = r.top; var f = 0
                        while (y < r.bottom) {
                            for (c in 0 until 2) {
                                val col = listOf(e.c1, e.c2, AMBAR, ZAFIRO, RUBI)[(f + c + k) % 5]
                                drawRect(t(col, oscuro, 0.2f), Offset(r.left + c * r.width / 2 + 0.3f * s, y + 0.3f * s), Size(r.width / 2 - 0.6f * s, 2.6f * s))
                            }
                            y += 3.2f * s; f++
                        }
                    }
                    drawCircle(t(AMBAR, oscuro, 0.2f), r.width * 0.22f, Offset(r.center.x, r.top + r.width * 0.55f))
                }
                // Los contrafuertes con su pináculo.
                for (k in 0..nV) {
                    val x = nave.left + paso * k
                    val cf = Rect(x - 2f * s, nave.top + 2f * s, x + 2f * s, nave.bottom)
                    drawRect(t(PIEDRA_LUZ, oscuro, 0.4f), cf.topLeft, cf.size)
                    drawRect(Color.Black.copy(alpha = 0.18f), Offset(cf.center.x, cf.top), Size(cf.width / 2, cf.height))
                    drawPath(Path().apply { moveTo(cf.left, cf.top); lineTo(cf.center.x, cf.top - 8f * s); lineTo(cf.right, cf.top); close() }, t(PIEDRA_LUZ, oscuro, 0.4f))
                    drawCircle(t(ORO, oscuro, 0.2f), 0.9f * s, Offset(cf.center.x, cf.top - 8.5f * s))
                }
                // El tejado de plomo azulado con su crestería dorada.
                val techo = Path().apply { moveTo(nave.left - 1f * s, nave.top); lineTo(nave.left + 6f * s, nave.top - 13f * s); lineTo(nave.right - 6f * s, nave.top - 13f * s); lineTo(nave.right + 1f * s, nave.top); close() }
                drawPath(techo, t(Color(0xFF4A5A78), oscuro, 0.3f))
                clipPath(techo) {
                    var y = nave.top - 13f * s
                    while (y < nave.top) { drawLine(Color.Black.copy(alpha = 0.18f), Offset(nave.left - 2f * s, y), Offset(nave.right + 2f * s, y), 0.4f * s); y += 2.2f * s }
                    drawRect(Color.White.copy(alpha = 0.12f), Offset(nave.left, nave.top - 13f * s), Size(nave.width, 3f * s))
                }
                var fx = nave.left + 7f * s
                while (fx < nave.right - 6f * s) { florDeLis(Offset(fx, nave.top - 14.5f * s), 3f * s, t(ORO, oscuro, 0.2f)); fx += 4f * s }
                drawLine(t(ORO, oscuro, 0.2f), Offset(nave.left + 6f * s, nave.top - 13f * s), Offset(nave.right - 6f * s, nave.top - 13f * s), 0.7f * s)
                // La aguja.
                val ag = Offset(cx + 6f * s, nave.top - 13f * s)
                drawPath(Path().apply { moveTo(ag.x - 2.6f * s, ag.y); lineTo(ag.x, ag.y - 17f * s); lineTo(ag.x + 2.6f * s, ag.y); close() }, t(Color(0xFF4A5A78), oscuro, 0.3f))
                drawLine(t(ORO, oscuro, 0.2f), Offset(ag.x, ag.y - 17f * s), Offset(ag.x, ag.y - 21f * s), 0.6f * s)
                drawLine(t(ORO, oscuro, 0.2f), Offset(ag.x - 1.4f * s, ag.y - 19.5f * s), Offset(ag.x + 1.4f * s, ag.y - 19.5f * s), 0.6f * s)
                for (k in 1..3) drawLine(t(ORO, oscuro, 0.2f), Offset(ag.x - 2.6f * s * (1 - k / 4f), ag.y - 17f * s * k / 4f), Offset(ag.x + 2.6f * s * (1 - k / 4f), ag.y - 17f * s * k / 4f), 0.4f * s)
            }
            "rascar" -> {
                // La hoja de vitela cubierta de grisalla que un dinero de plata raspa: debajo asoma la vidriera.
                val hoja = Rect(cx - 30f * s, base - 54f * s, cx + 30f * s, base - 4f * s)
                rotate(-4f, hoja.center) {
                    drawRect(Color.Black.copy(alpha = 0.3f), Offset(hoja.left + 1.2f * s, hoja.top + 1.8f * s), hoja.size)
                    rect(t(VITELA, oscuro, 0.3f), hoja)
                    val dentro = hoja.crece(-3f * s)
                    // La vidriera debajo.
                    clipRect(dentro.left, dentro.top, dentro.right, dentro.bottom) {
                        rect(t(PLOMO, oscuro, 0.2f), dentro)
                        val l = 6f * s
                        var y = dentro.top; var f = 0
                        while (y < dentro.bottom) {
                            var x = dentro.left; var k = 0
                            while (x < dentro.right) {
                                val col = listOf(e.c1, e.c2, AMBAR, ESMERALDA, ZAFIRO, RUBI)[(f * 2 + k) % 6]
                                drawRect(t(col, oscuro, 0.15f), Offset(x + 0.5f * s, y + 0.5f * s), Size(l - 1f * s, l - 1f * s))
                                drawRect(Color.White.copy(alpha = 0.18f), Offset(x + 1f * s, y + 1f * s), Size(l * 0.3f, l * 0.2f))
                                x += l; k++
                            }
                            y += l; f++
                        }
                    }
                    // La grisalla que aún cubre la parte de arriba y la izquierda, con el borde raspado.
                    val capa = Path().apply {
                        moveTo(dentro.left, dentro.top); lineTo(dentro.right, dentro.top); lineTo(dentro.right, dentro.top + dentro.height * 0.3f)
                        cubicTo(dentro.right - dentro.width * 0.3f, dentro.top + dentro.height * 0.4f, dentro.left + dentro.width * 0.55f, dentro.top + dentro.height * 0.25f, dentro.left + dentro.width * 0.45f, dentro.bottom)
                        lineTo(dentro.left, dentro.bottom); close()
                    }
                    drawPath(capa, t(Color(0xFF9A9890), oscuro, 0.3f))
                    clipPath(capa) {
                        var y = dentro.top
                        while (y < dentro.bottom) { drawLine(Color.Black.copy(alpha = 0.08f), Offset(dentro.left, y), Offset(dentro.right, y + 3f * s), 0.4f * s); y += 1.6f * s }
                    }
                    drawPath(capa, Color.White.copy(alpha = 0.5f), style = Stroke(0.6f * s, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.2f * s, 0.8f * s))))
                    drawRect(t(ORO, oscuro, 0.2f), dentro.topLeft, dentro.size, style = Stroke(0.7f * s))
                    drawRect(t(VITELA_OSC, oscuro, 0.3f), hoja.topLeft, hoja.size, style = Stroke(0.5f * s))
                }
                // El dinero de plata, de canto, con su cruz.
                val m = Offset(cx + 22f * s, base - 12f * s)
                drawOval(Color.Black.copy(alpha = 0.35f), Offset(m.x - 9f * s, m.y - 6f * s), Size(19f * s, 14f * s))
                drawOval(t(Color(0xFF8E9098), oscuro, 0.2f), Offset(m.x - 10f * s, m.y - 8.4f * s), Size(20f * s, 15f * s))
                drawOval(t(PLATA, oscuro, 0.2f), Offset(m.x - 10f * s, m.y - 9.4f * s), Size(20f * s, 14f * s))
                drawOval(t(Color(0xFF8E9098), oscuro, 0.2f), Offset(m.x - 7.5f * s, m.y - 7.6f * s), Size(15f * s, 10.4f * s), style = Stroke(0.5f * s))
                drawLine(t(Color(0xFF6E7078), oscuro, 0.2f), Offset(m.x - 4.5f * s, m.y - 2.4f * s), Offset(m.x + 4.5f * s, m.y - 2.4f * s), 1.2f * s)
                drawLine(t(Color(0xFF6E7078), oscuro, 0.2f), Offset(m.x, m.y - 6f * s), Offset(m.x, m.y + 1.2f * s), 1.2f * s)
                drawOval(Color.White.copy(alpha = 0.45f), Offset(m.x - 7f * s, m.y - 8.4f * s), Size(6f * s, 2.6f * s))
            }
            else -> {
                // Tus lugares: tres rosetas de vidrio clavadas en el mapa de vitela, con el camino rubricado.
                val hoja = Path().apply {
                    moveTo(cx - 44f * s, base - 40f * s)
                    cubicTo(cx - 20f * s, base - 43f * s, cx + 20f * s, base - 37f * s, cx + 44f * s, base - 41f * s)
                    lineTo(cx + 46f * s, base - 6f * s)
                    cubicTo(cx + 20f * s, base - 3f * s, cx - 20f * s, base - 9f * s, cx - 46f * s, base - 5f * s)
                    close()
                }
                translate(1.2f * s, 1.8f * s) { drawPath(hoja, Color.Black.copy(alpha = 0.3f)) }
                drawPath(hoja, t(VITELA, oscuro, 0.3f))
                drawPath(hoja, t(VITELA_OSC, oscuro, 0.3f), style = Stroke(0.6f * s))
                clipPath(hoja) {
                    drawPath(Path().apply { moveTo(cx - 46f * s, base - 18f * s); cubicTo(cx - 25f * s, base - 26f * s, cx - 10f * s, base - 12f * s, cx + 8f * s, base - 20f * s) }, ZAFIRO.copy(alpha = 0.35f), style = Stroke(1.6f * s))
                    drawCircle(t(VITELA_OSC, oscuro, 0.3f), 6f * s, Offset(cx + 26f * s, base - 30f * s), style = Stroke(0.5f * s))
                }
                val pts = listOf(Offset(cx - 30f * s, base - 22f * s), Offset(cx + 2f * s, base - 30f * s), Offset(cx + 30f * s, base - 15f * s))
                val camino = Path().apply { moveTo(pts[0].x, pts[0].y); quadraticTo(cx - 14f * s, base - 10f * s, pts[1].x, pts[1].y); quadraticTo(cx + 18f * s, base - 40f * s, pts[2].x, pts[2].y) }
                drawPath(camino, t(RUBI, oscuro, 0.15f), style = Stroke(1f * s, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2f * s, 1.6f * s))))
                pts.forEachIndexed { i, p ->
                    val c = p - Offset(0f, 9f * s)
                    drawLine(Color.Black.copy(alpha = 0.3f), p + Offset(1f * s, 0f), c + Offset(3f * s, 2f * s), 0.8f * s)
                    drawLine(t(HIERRO_LUZ, oscuro, 0.2f), p, c, 0.8f * s)
                    val r = 6.2f * s
                    drawCircle(Color.Black.copy(alpha = 0.3f), r, c + Offset(0.8f * s, 1.2f * s))
                    drawCircle(t(ORO, oscuro, 0.2f), r, c)
                    drawCircle(t(PLOMO, oscuro, 0.2f), r * 0.82f, c)
                    val cols = listOf(e.c1, e.c2, AMBAR, ESMERALDA)
                    for (k in 0 until 6) drawArc(t(cols[(k + i) % 4], oscuro, 0.15f), -90f + k * 60f + 4f, 52f, true, Offset(c.x - r * 0.72f, c.y - r * 0.72f), Size(r * 1.44f, r * 1.44f))
                    drawCircle(t(ORO_LUZ, oscuro, 0.15f), r * 0.24f, c)
                    drawCircle(Color.White.copy(alpha = 0.35f), r * 0.3f, c - Offset(r * 0.35f, r * 0.35f))
                }
            }
        }
    }

    // ---------------------------------------------------------------- Notas

    override fun DrawScope.tableroNotas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val n = caja(area(MuebleVida.NOTAS))
        // El muro de sillería detrás del tablón.
        sillares(Rect(0f, 0f, w, h), 5.2f * u, oscuro, e.semilla, u)
        // El marco de roble.
        val marco = Rect(w * 0.035f, h * 0.125f, w * 0.965f, h * 0.935f)
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(marco.left + 0.8f * u, marco.top + 1.2f * u), marco.size)
        roble(marco, oscuro, e.semilla + 1, u, ROBLE)
        bisel(marco, Color.White.copy(alpha = 0.1f), Color.Black.copy(alpha = 0.35f), 0.5f * u)
        // El muro encalado con su despiece fingido de sillares en ocre rojo (el nicho).
        val rebaje = n.crece(1.2f * u)
        rect(t(ROBLE_OSC, oscuro), rebaje)
        drawRect(t(ORO, oscuro, 0.25f), rebaje.topLeft, rebaje.size, style = Stroke(0.45f * u))
        rect(if (oscuro) mezcla(e.fondo, BOVEDA, 0.25f) else mezcla(e.fondo, CAL, 0.5f), n)
        clipRect(n.left, n.top, n.right, n.bottom) {
            val lc = (if (oscuro) ORO else OCRE_ROJO).copy(alpha = if (oscuro) 0.1f else 0.16f)
            val ah = n.height / 6f; val lg = ah * 2.4f
            for (f in 0..6) {
                val y = n.top + ah * f
                drawLine(lc, Offset(n.left, y), Offset(n.right, y), 0.3f * u)
                var x = n.left - (if (f % 2 == 0) lg * 0.3f else lg * 0.8f)
                while (x < n.right) { drawLine(lc, Offset(x, y), Offset(x, y + ah), 0.3f * u); x += lg }
            }
            sombraInterior(n, 3f * u, if (oscuro) 0.35f else 0.18f)
        }
        // Los contrafuertes de los lados, con sus gabletillos escalonados y su pináculo.
        listOf(Rect(w * 0.01f, h * 0.1f, n.left - 1.6f * u, h * 0.955f), Rect(n.right + 1.6f * u, h * 0.1f, w * 0.99f, h * 0.955f)).forEachIndexed { i, r ->
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(r.left + 0.5f * u, r.top + 0.8f * u), r.size)
            roble(r, oscuro, e.semilla + 10 + i, u, ROBLE_LUZ, vertical = true)
            drawRect(t(ROBLE_OSC, oscuro), Offset(r.left + r.width * 0.36f, r.top + 3f * u), Size(r.width * 0.28f, r.height - 6f * u))
            var yy = r.top + r.height * 0.3f
            while (yy < r.bottom - 6f * u) {
                drawPath(Path().apply { moveTo(r.left, yy); lineTo(r.center.x, yy - r.width * 0.55f); lineTo(r.right, yy); close() }, t(ROBLE_LUZ, oscuro))
                drawPath(Path().apply { moveTo(r.center.x, yy); lineTo(r.center.x, yy - r.width * 0.55f); lineTo(r.right, yy); close() }, Color.Black.copy(alpha = 0.18f))
                drawRect(Color.Black.copy(alpha = 0.3f), Offset(r.left, yy), Size(r.width, 0.5f * u))
                yy += r.height * 0.28f
            }
            bisel(r, Color.White.copy(alpha = 0.1f), Color.Black.copy(alpha = 0.3f), 0.35f * u)
            pinaculo(Offset(r.center.x, r.top), r.width * 0.9f, h * 0.08f, oscuro, u)
        }
        // La arquería de tréboles del copete y, al centro, el gablete con su rosetón.
        val banda = Rect(n.left - 1.2f * u, h * 0.125f, n.right + 1.2f * u, n.top - 1.2f * u)
        roble(banda, oscuro, e.semilla + 20, u, ROBLE_LUZ)
        val nA = (banda.width / (7.5f * u)).toInt().coerceAtLeast(8)
        val aw = banda.width / nA
        for (j in 0 until nA) {
            val r = Rect(banda.left + aw * j + aw * 0.14f, banda.top + banda.height * 0.2f, banda.left + aw * (j + 1) - aw * 0.14f, banda.bottom - banda.height * 0.16f)
            drawPath(ojiva(r, r.width * 0.8f), t(ROBLE_OSC, oscuro))
            trebol(Offset(r.center.x, r.top + r.width * 0.55f), r.width * 0.42f, t(ROBLE_LUZ, oscuro))
            drawLine(t(ROBLE_LUZ, oscuro), Offset(r.center.x, r.top + r.width * 0.78f), Offset(r.center.x, r.bottom), 0.4f * u)
        }
        drawLine(t(ORO, oscuro, 0.25f), Offset(banda.left, banda.top + 0.4f * u), Offset(banda.right, banda.top + 0.4f * u), 0.5f * u)
        for (j in 0..nA step 2) pinaculo(Offset(banda.left + aw * j, banda.top), 2.2f * u, h * 0.05f, oscuro, u)
        val gw = minOf(n.width * 0.32f, 34f * u)
        val gAlto = banda.top - h * 0.01f
        gablete(w / 2 - gw / 2, w / 2 + gw / 2, banda.top + 0.5f * u, gAlto, oscuro, u, t(ROBLE_LUZ, oscuro), t(ROBLE_OSC, oscuro))
        roseton(Offset(w / 2, banda.top - gAlto * 0.36f), minOf(gAlto * 0.3f, gw * 0.2f), oscuro, u)
        // La repisa de abajo con su friso de cuadrifolios y dos cirios en sus arandelas.
        val repisa = Rect(n.left - 1.2f * u, n.bottom + 1.2f * u, n.right + 1.2f * u, h * 0.935f)
        roble(repisa, oscuro, e.semilla + 30, u, ROBLE)
        var qx = repisa.left + 3f * u
        while (qx < repisa.right - 2f * u) { cuadrifolio(Offset(qx, repisa.center.y), repisa.height * 0.55f, t(ROBLE_LUZ, oscuro), t(ROBLE_OSC, oscuro)); qx += 5f * u }
        drawLine(t(ORO, oscuro, 0.25f), Offset(repisa.left, repisa.top + 0.3f * u), Offset(repisa.right, repisa.top + 0.3f * u), 0.45f * u)
        val zocalo = Rect(marco.left - 0.6f * u, h * 0.935f, marco.right + 0.6f * u, h * 0.965f)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(zocalo.left, zocalo.bottom), Size(zocalo.width, 1f * u))
        roble(zocalo, oscuro, e.semilla + 31, u, ROBLE_LUZ)
        listOf(w * 0.035f, w * 0.965f).forEach { x ->
            val b = Offset(x, h * 0.62f)
            drawRect(t(HIERRO, oscuro, 0.2f), Offset(x - 1.4f * u, b.y), Size(2.8f * u, 0.8f * u))
            drawLine(t(HIERRO, oscuro, 0.2f), Offset(x, b.y + 0.8f * u), Offset(x, b.y + 4f * u), 0.5f * u)
            cirio(b, 1.4f * u, 6f * u, oscuro)
        }
    }

    override fun DrawScope.notaTablero(e: EstadoNota, oscuro: Boolean) {
        val r = e.rect; val w = r.width; val h = r.height; val s = minOf(w, h) / 100f
        // La hoja de vitela con su sombra; el borde apenas irregular.
        val hoja = Rect(r.left + w * 0.02f, r.top + h * 0.02f, r.right - w * 0.02f, r.bottom - h * 0.02f)
        val giro = (azar(e.indice + 3, 1) - 0.5f) * 2.4f
        rotate(giro, hoja.center) {
            val forma = Path().apply {
                moveTo(hoja.left, hoja.top + 1f * s); lineTo(hoja.left + w * 0.3f, hoja.top); lineTo(hoja.right - w * 0.2f, hoja.top + 0.8f * s); lineTo(hoja.right, hoja.top)
                lineTo(hoja.right - 0.6f * s, hoja.top + h * 0.5f); lineTo(hoja.right, hoja.bottom - 1f * s); lineTo(hoja.left + w * 0.6f, hoja.bottom); lineTo(hoja.left + 0.6f * s, hoja.bottom - 0.6f * s)
                lineTo(hoja.left, hoja.top + h * 0.45f); close()
            }
            translate(1.2f * s, 2f * s) { drawPath(forma, Color.Black.copy(alpha = if (oscuro) 0.5f else 0.28f)) }
            val papel = if (oscuro) mezcla(VITELA, Color(0xFF2A2014), 0.34f) else VITELA
            drawPath(forma, papel)
            clipPath(forma) {
                // El lado del pelo, más amarillo hacia los bordes.
                val b = 7f * s
                drawRect(Brush.verticalGradient(listOf(VITELA_OSC.copy(alpha = 0.45f), Color.Transparent), hoja.top, hoja.top + b), hoja.topLeft, Size(hoja.width, b))
                drawRect(Brush.verticalGradient(listOf(Color.Transparent, VITELA_OSC.copy(alpha = 0.45f)), hoja.bottom - b, hoja.bottom), Offset(hoja.left, hoja.bottom - b), Size(hoja.width, b))
                drawRect(Brush.horizontalGradient(listOf(VITELA_OSC.copy(alpha = 0.35f), Color.Transparent), hoja.left, hoja.left + b), hoja.topLeft, Size(b, hoja.height))
                drawRect(Brush.horizontalGradient(listOf(Color.Transparent, VITELA_OSC.copy(alpha = 0.35f)), hoja.right - b, hoja.right), Offset(hoja.right - b, hoja.top), Size(b, hoja.height))
            }
            drawPath(forma, mezcla(VITELA_OSC, Color.Black, if (oscuro) 0.45f else 0.18f), style = Stroke(0.7f * s))
            // Si está fijada, el canto de pan de oro.
            if (e.fijada) drawPath(forma, t(ORO, oscuro, 0.15f), style = Stroke(1.6f * s))
            // El filete de oro del margen con sus hojas de hiedra azules y rojas.
            val fx = r.left + w * 0.065f
            val y0 = r.top + h * 0.16f; val y1 = r.bottom - h * 0.12f
            drawLine(t(ORO, oscuro, 0.15f), Offset(fx, y0), Offset(fx, y1), 1f * s)
            drawCircle(t(ORO, oscuro, 0.15f), 1.6f * s, Offset(fx + 1.4f * s, y0 - 1f * s), style = Stroke(0.6f * s))
            var y = y0 + 4f * s; var k = 0
            while (y < y1 - 2f * s) {
                val izq = k % 2 == 0
                val c = Offset(fx + (if (izq) -1.8f else 1.8f) * s, y)
                rotate(if (izq) -35f else 35f, c) { drawOval(t(if (k % 3 == 0) RUBI else ZAFIRO, oscuro, 0.15f), Offset(c.x - 1.5f * s, c.y - 0.8f * s), Size(3f * s, 1.6f * s)) }
                y += 6.5f * s; k++
            }
            // El detalle del tipo, en la esquina de arriba a la derecha.
            val esq = Offset(r.right - w * 0.1f, r.top + h * 0.085f)
            when (e.tipo) {
                "idea" -> {
                    drawCircle(t(BOVEDA, oscuro, 0.15f), 4.2f * s, esq)
                    drawPath(estrella(esq, 3.4f * s, 8, 0.45f), t(ORO_LUZ, oscuro, 0.1f))
                    drawCircle(t(ORO, oscuro, 0.1f), 4.2f * s, esq, style = Stroke(0.5f * s))
                }
                "pensamiento" -> {
                    trebol(esq, 5.4f * s, t(ZAFIRO, oscuro, 0.15f))
                    drawLine(t(ZAFIRO, oscuro, 0.15f), esq, esq + Offset(0f, 5f * s), 0.9f * s, StrokeCap.Round)
                    drawCircle(t(ORO_LUZ, oscuro, 0.1f), 0.8f * s, esq)
                }
                "proposito" -> florDeLis(esq + Offset(0f, 0.5f * s), 8f * s, t(RUBI, oscuro, 0.15f))
                else -> {
                    // La campanilla de oro y la esquina de abajo doblada.
                    val cp = Path().apply {
                        moveTo(esq.x - 3.4f * s, esq.y + 2.6f * s)
                        quadraticTo(esq.x - 2.6f * s, esq.y - 3.4f * s, esq.x, esq.y - 3.4f * s)
                        quadraticTo(esq.x + 2.6f * s, esq.y - 3.4f * s, esq.x + 3.4f * s, esq.y + 2.6f * s)
                        close()
                    }
                    drawPath(cp, t(ORO, oscuro, 0.1f))
                    drawPath(cp, t(ORO_OSC, oscuro, 0.1f), style = Stroke(0.4f * s))
                    drawCircle(t(ORO_OSC, oscuro, 0.1f), 1f * s, esq + Offset(0f, 3.3f * s))
                    drawCircle(t(ORO_OSC, oscuro, 0.1f), 0.9f * s, esq - Offset(0f, 4f * s), style = Stroke(0.4f * s))
                    val d = 10f * s
                    val ex = hoja.right; val ey = hoja.bottom
                    drawPath(Path().apply { moveTo(ex - d, ey); lineTo(ex, ey - d); lineTo(ex, ey); close() }, t(PIEDRA, oscuro, 0.62f))
                    drawPath(Path().apply { moveTo(ex - d, ey); lineTo(ex, ey - d); lineTo(ex - d, ey - d); close() }, mezcla(VITELA_OSC, NOCHE, if (oscuro) 0.35f else 0f))
                    drawLine(Color.Black.copy(alpha = 0.25f), Offset(ex - d, ey), Offset(ex, ey - d), 0.5f * s)
                }
            }
            // El clavo de arriba: de hierro; si la nota está fijada, de oro, con su estrella y su brillo.
            val cc = Offset(r.center.x, r.top + h * 0.075f)
            if (e.fijada) {
                halo(cc, 8f * s, ORO_LUZ.copy(alpha = 0.55f))
                drawCircle(Color.Black.copy(alpha = 0.35f), 3.2f * s, cc + Offset(0.8f * s, 1f * s))
                drawCircle(mezcla(e.acento, ORO, 0.5f), 3.2f * s, cc)
                drawPath(estrella(cc, 2.6f * s, 8, 0.45f), ORO_LUZ)
                drawCircle(Color.White.copy(alpha = 0.5f), 0.8f * s, cc - Offset(1f * s, 1f * s))
            } else clavo(cc, 2.2f * s)
        }
    }

    /** La tinta de las notas: el azul negro de la letra textura; la vitela es clara también de noche, a la luz de los cirios. */
    override fun tintaNota(oscuro: Boolean): Color = TINTA

    /** Los cuatro lados de [r] más hondos que su centro. */
    private fun DrawScope.sombraInterior(r: Rect, ancho: Float, fuerza: Float) {
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = fuerza), Color.Transparent), r.top, r.top + ancho), r.topLeft, Size(r.width, ancho))
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = fuerza * 0.6f)), r.bottom - ancho, r.bottom), Offset(r.left, r.bottom - ancho), Size(r.width, ancho))
        drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = fuerza * 0.8f), Color.Transparent), r.left, r.left + ancho), r.topLeft, Size(ancho, r.height))
        drawRect(Brush.horizontalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = fuerza * 0.8f)), r.right - ancho, r.right), Offset(r.right - ancho, r.top), Size(ancho, r.height))
    }
}
