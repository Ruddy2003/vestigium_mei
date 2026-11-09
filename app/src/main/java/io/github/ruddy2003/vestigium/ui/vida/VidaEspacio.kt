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
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.lerp
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * Espacio: los muebles de Vida y de Diario de la estación orbital, de la misma nave que su Ideario de racks, su estantería
 * de cristales de memoria con el ojo de buey a la Tierra y su Hall de ventanales: de día, la sala de control (paneles
 * claros del casco con sus juntas y sus sujeciones, marcos de consola azul marino, el naranja de los avisos); de noche, la
 * misma nave con las luces bajas: paneles de noche, pilotos encendidos con su halo y el negro del espacio en los ojos de buey.
 * El acento del esquema va en los corchetes y las luces; el secundario, en los pasamanos y las franjas de aviso.
 * - Social: el comedor de la tripulación: la pizarra de la tripulación (el nicho) en su marco atornillado con corchetes y su
 *   cabecera de pilotos; a los lados, dos ojos de buey (la Tierra y la Luna) con su brida de pernos y un panelito de
 *   telemetría; arriba, el techo con la lámpara y los mazos de cables; abajo, el pasamanos, la mesa con sus tiras de velcro,
 *   las bolsas de bebida y las latas, las patas y el piso con los estribos.
 * - Mapas: la consola de navegación: el tablero de trazado (el nicho) en su bisel; arriba, la fila de indicadores; a la
 *   izquierda, la esfera de actitud (la «bola de ocho») y la perilla; a la derecha, el globo en su cardán y los interruptores
 *   con tapa de seguridad; abajo, el teclado del ordenador de guiado con su pantallita verde, el zócalo con franjas de aviso,
 *   las patas magnéticas y el piso de rejilla.
 * - Otras vidas: el armario de muestras: cinco bahías iguales (el nicho) con su lámpara, separadas por montantes
 *   atornillados; arriba, la cabecera con el ojo de buey al planeta anillado y las dos pantallas de telemetría; a los lados,
 *   los rieles perforados; abajo, las placas de cada bahía, las rejillas y las patas.
 * - Íconos: la estación orbital con sus alas solares; la pantalla de la carta estelar con la capa plateada que raspa una
 *   medalla de la misión; tres balizas en la Luna unidas por la trayectoria.
 * - Notas: el tablero de velcro de la tripulación en su marco con corchetes, bajo la lámpara, entre dos pasamanos, con la
 *   repisa de la cinta americana, el rotulador atado y la linterna; cada nota es una ficha de procedimiento plastificada con
 *   la franja del color de su tipo, sus ojales y su signo (la chispa, el planeta, el cohete o el reloj), sujeta con una tira
 *   de velcro o, si está fijada, con el parche de la misión.
 */
internal object VidaEspacio : DisenoVida {
    // ---------------------------------------------------------------- materiales

    /** Los materiales que cambian entre el día (sala de control) y la noche (luces bajas). */
    private class Pal(
        val pared: Color, val paredLuz: Color, val paredOsc: Color, val junta: Color,
        val casco: Color, val cascoLuz: Color, val cascoOsc: Color,
        val nicho: Color, val nichoLinea: Color, val piso: Color, val pisoLinea: Color,
        val tecla: Color, val teclaLuz: Color, val oscuro: Boolean,
    )

    private val DIA = Pal(
        pared = Color(0xFFDDE3EB), paredLuz = Color(0xFFF2F5F9), paredOsc = Color(0xFFB4BDCB), junta = Color(0xFF97A2B3),
        casco = Color(0xFF34405A), cascoLuz = Color(0xFF52607E), cascoOsc = Color(0xFF1E2638),
        nicho = Color(0xFFEBEFF5), nichoLinea = Color(0xFFDDE3EC), piso = Color(0xFFBCC5D2), pisoLinea = Color(0xFF8F9AAD),
        tecla = Color(0xFFD5DBE4), teclaLuz = Color(0xFFF3F5F8), oscuro = false,
    )
    private val NOCHE = Pal(
        pared = Color(0xFF141B2D), paredLuz = Color(0xFF1F2840), paredOsc = Color(0xFF0B101D), junta = Color(0xFF060A14),
        casco = Color(0xFF2B354E), cascoLuz = Color(0xFF445275), cascoOsc = Color(0xFF151B2B),
        nicho = Color(0xFF0B1122), nichoLinea = Color(0xFF141B31), piso = Color(0xFF0E1426), pisoLinea = Color(0xFF1F2842),
        tecla = Color(0xFF2E3854), teclaLuz = Color(0xFF46527A), oscuro = true,
    )

    private fun pal(oscuro: Boolean) = if (oscuro) NOCHE else DIA

    private val VACIO = Color(0xFF03050C)
    private val VACIO_AZUL = Color(0xFF111A36)
    private val ESTRELLA = Color(0xFFF2F4FF)
    private val NEBULOSA = Color(0xFF7A3AA8)
    private val ROSA = Color(0xFFC85AA0)
    private val PLANETA = Color(0xFFD8A06A)
    private val PLANETA_OSC = Color(0xFF8E6440)
    private val MAR = Color(0xFF2A66B0)
    private val MAR_LUZ = Color(0xFF4A92DA)
    private val MAR_OSC = Color(0xFF163C74)
    private val TIERRA = Color(0xFF4A8A4E)
    private val DESIERTO = Color(0xFFB8955E)
    private val NUBE = Color(0xFFF0F5FB)
    private val ATMOSFERA = Color(0xFF8FD4FF)
    private val LUNA = Color(0xFFC9CBD0)
    private val LUNA_OSC = Color(0xFF8C9098)
    private val CIAN = Color(0xFF5CE1FF)
    private val VERDE = Color(0xFF5CFFB0)
    private val AMBAR = Color(0xFFFFB84A)
    private val ROJO = Color(0xFFE5484D)
    private val METAL = Color(0xFFB9C1CE)
    private val METAL_LUZ = Color(0xFFEEF1F6)
    private val METAL_OSC = Color(0xFF66718A)
    private val ORO = Color(0xFFD9A93A)
    private val ORO_LUZ = Color(0xFFF2D27A)
    private val ORO_OSC = Color(0xFF8C6418)
    private val CELDA_SOLAR = Color(0xFF243A86)
    private val CELDA_LUZ = Color(0xFF4A66C4)
    private val NEGRO = Color(0xFF05070D)
    private val PANTALLA = Color(0xFF07111A)
    private val CINTA = Color(0xFF9DA3AC)
    /** La ficha plastificada y la tinta con que se escribe en ella. */
    private val FICHA = Color(0xFFF6F8FB)
    private val FICHA_NOCHE = Color(0xFFE1E6EE)
    private val TINTA = Color(0xFF0D1526)
    private val CABLES = listOf(Color(0xFF2E6FA8), Color(0xFFE0A030), Color(0xFFB8463C), Color(0xFF3E8A5A), Color(0xFF8A90A0))

    override fun area(m: MuebleVida): Rect = when (m) {
        MuebleVida.SOCIAL -> Rect(0.155f, 0.175f, 0.845f, 0.795f)
        MuebleVida.MAPAS -> Rect(0.165f, 0.165f, 0.835f, 0.705f)
        MuebleVida.OTRAS_VIDAS -> Rect(0.075f, 0.255f, 0.925f, 0.83f)
        MuebleVida.NOTAS -> Rect(0.085f, 0.135f, 0.915f, 0.865f)
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
        drawRoundRect(sombra, r.topLeft, r.size, CornerRadius(radio))
        drawRoundRect(luz, r.topLeft, Size((r.width - g).coerceAtLeast(0f), (r.height - g).coerceAtLeast(0f)), CornerRadius(radio))
        drawRoundRect(cara, Offset(r.left + g, r.top + g), Size((r.width - 2 * g).coerceAtLeast(0f), (r.height - 2 * g).coerceAtLeast(0f)), CornerRadius((radio - g).coerceAtLeast(0f)))
    }

    /** La sombra que una pieza echa sobre lo que tiene detrás. */
    private fun DrawScope.sombra(r: Rect, u: Float, radio: Float = 0f, alfa: Float = 0.35f) {
        drawRoundRect(NEGRO.copy(alpha = alfa), Offset(r.left + 0.5f * u, r.top + 0.9f * u), r.size, CornerRadius(radio))
    }

    /** Un tornillo de cabeza redonda con su cruz, su brillo y su sombra. */
    private fun DrawScope.tornillo(p: Offset, r: Float, oscuro: Boolean) {
        val f = if (oscuro) 0.4f else 0f
        drawCircle(NEGRO.copy(alpha = 0.45f), r * 1.1f, Offset(p.x + r * 0.25f, p.y + r * 0.3f))
        drawCircle(Brush.radialGradient(listOf(mez(METAL_LUZ, NEGRO, f), mez(METAL, NEGRO, f), mez(METAL_OSC, NEGRO, f)), Offset(p.x - r * 0.35f, p.y - r * 0.35f), r * 1.6f), r, p)
        drawLine(mez(METAL_OSC, NEGRO, f + 0.2f), Offset(p.x - r * 0.55f, p.y), Offset(p.x + r * 0.55f, p.y), r * 0.26f)
        drawLine(mez(METAL_OSC, NEGRO, f + 0.2f), Offset(p.x, p.y - r * 0.55f), Offset(p.x, p.y + r * 0.55f), r * 0.26f)
    }

    /** Un piloto: encendido, con su halo y su reflejo; apagado, el vidrio oscuro del color. */
    private fun DrawScope.piloto(p: Offset, r: Float, color: Color, encendido: Boolean) {
        if (encendido) {
            halo(p, r * 4f, color.copy(alpha = 0.3f))
            drawCircle(color, r, p)
            drawCircle(Color.White.copy(alpha = 0.65f), r * 0.38f, Offset(p.x - r * 0.3f, p.y - r * 0.3f))
        } else {
            drawCircle(mez(color, NEGRO, 0.72f), r, p)
            drawCircle(Color.White.copy(alpha = 0.15f), r * 0.35f, Offset(p.x - r * 0.3f, p.y - r * 0.3f))
        }
    }

    /** Los corchetes de las esquinas, como en las pantallas de la nave. */
    private fun DrawScope.corchetes(r: Rect, l: Float, color: Color, g: Float) {
        listOf(r.topLeft to Offset(1f, 1f), Offset(r.right, r.top) to Offset(-1f, 1f), Offset(r.left, r.bottom) to Offset(1f, -1f), r.bottomRight to Offset(-1f, -1f)).forEach { (o, s) ->
            drawLine(color, o, Offset(o.x + s.x * l, o.y), g, StrokeCap.Square)
            drawLine(color, o, Offset(o.x, o.y + s.y * l), g, StrokeCap.Square)
        }
    }

    /** Ranuras de ventilación: la ranura oscura y el filo de luz bajo ella. */
    private fun DrawScope.ranuras(r: Rect, n: Int, g: Float, hueco: Color, filo: Color) {
        for (i in 0 until n) {
            val x = r.left + r.width * (i + 0.5f) / n
            drawLine(filo, Offset(x + g * 0.5f, r.top + g * 0.4f), Offset(x + g * 0.5f, r.bottom + g * 0.4f), g * 0.4f, StrokeCap.Round)
            drawLine(hueco, Offset(x, r.top), Offset(x, r.bottom), g, StrokeCap.Round)
        }
    }

    /** Franjas de aviso en diagonal, del color dado sobre el casi negro. */
    private fun DrawScope.franjas(r: Rect, color: Color, paso: Float) {
        drawRect(mez(NEGRO, color, 0.12f), r.topLeft, r.size)
        clipRect(r.left, r.top, r.right, r.bottom) {
            var x = r.left - r.height
            while (x < r.right) {
                drawPath(Path().apply {
                    moveTo(x, r.bottom); lineTo(x + r.height, r.top); lineTo(x + r.height + paso * 0.5f, r.top); lineTo(x + paso * 0.5f, r.bottom); close()
                }, color)
                x += paso
            }
        }
        drawLine(Color.White.copy(alpha = 0.18f), Offset(r.left, r.top), Offset(r.right, r.top), paso * 0.08f)
    }

    /** Un pasamanos con sus dos soportes: la barra con su sombra, su color y su brillo. */
    private fun DrawScope.pasamanos(a: Offset, b: Offset, g: Float, color: Color) {
        val vertical = abs(b.x - a.x) < abs(b.y - a.y)
        val soportes = listOf(0.12f, 0.88f).map { f -> Offset(a.x + (b.x - a.x) * f, a.y + (b.y - a.y) * f) }
        soportes.forEach { s ->
            val sr = if (vertical) Rect(s.x - g * 1.4f, s.y - g * 0.7f, s.x + g * 1.4f, s.y + g * 0.7f) else Rect(s.x - g * 0.7f, s.y - g * 1.4f, s.x + g * 0.7f, s.y + g * 1.4f)
            drawRect(NEGRO.copy(alpha = 0.3f), Offset(sr.left + g * 0.3f, sr.top + g * 0.4f), sr.size)
            drawRect(METAL_OSC, sr.topLeft, sr.size)
        }
        val d = if (vertical) Offset(g * 0.35f, g * 0.5f) else Offset(g * 0.2f, g * 0.6f)
        drawLine(NEGRO.copy(alpha = 0.3f), a + d, b + d, g, StrokeCap.Round)
        drawLine(mez(color, NEGRO, 0.25f), a, b, g, StrokeCap.Round)
        val l = if (vertical) Offset(-g * 0.22f, 0f) else Offset(0f, -g * 0.22f)
        drawLine(mez(color, Color.White, 0.45f), a + l, b + l, g * 0.32f, StrokeCap.Round)
    }

    /** Estrellas en [r]: puntos de distinto brillo y tamaño, siempre los mismos para la semilla. */
    private fun DrawScope.estrellas(r: Rect, semilla: Int, n: Int, s: Float) {
        for (k in 0 until n) {
            val o = Offset(r.left + r.width * azar(semilla, k), r.top + r.height * azar(semilla + 1, k))
            val t = azar(semilla + 2, k)
            drawCircle(ESTRELLA.copy(alpha = 0.35f + 0.6f * t), (0.25f + 0.75f * t * t) * s, o)
        }
    }

    /**
     * Lo que se ve por un vidrio redondo en [c] de radio [r]: 0, el filo de la Tierra con su atmósfera, mares, continentes y
     * nubes; 1, la Luna con sus cráteres; 2, la nebulosa con el planeta anillado.
     */
    private fun DrawScope.vista(c: Offset, r: Float, tipo: Int, semilla: Int) {
        val vidrio = Path().apply { addOval(Rect(c, r)) }
        clipPath(vidrio) {
            drawCircle(Brush.radialGradient(listOf(VACIO_AZUL, VACIO), Offset(c.x - r * 0.3f, c.y - r * 0.4f), r * 1.6f), r, c)
            when (tipo) {
                0 -> {
                    estrellas(Rect(c, r), semilla, 16, r * 0.06f)
                    val tc = Offset(c.x + r * 0.25f, c.y + r * 1.75f); val tr = r * 1.4f
                    halo(tc, tr * 1.2f, ATMOSFERA.copy(alpha = 0.55f))
                    drawCircle(Brush.radialGradient(listOf(MAR_LUZ, MAR, MAR_OSC), Offset(tc.x - tr * 0.3f, tc.y - tr * 0.75f), tr * 1.2f), tr, tc)
                    clipPath(Path().apply { addOval(Rect(tc, tr)) }) {
                        rotate(-8f, tc) {
                            drawOval(TIERRA, Offset(tc.x - tr * 0.8f, tc.y - tr * 1.04f), Size(tr * 0.55f, tr * 0.3f))
                            drawOval(DESIERTO, Offset(tc.x - tr * 0.18f, tc.y - tr * 1.06f), Size(tr * 0.5f, tr * 0.24f))
                            drawOval(TIERRA, Offset(tc.x + tr * 0.25f, tc.y - tr * 0.98f), Size(tr * 0.32f, tr * 0.34f))
                            drawOval(NUBE.copy(alpha = 0.9f), Offset(tc.x - tr * 0.55f, tc.y - tr * 1.0f), Size(tr * 0.32f, tr * 0.06f))
                            drawOval(NUBE.copy(alpha = 0.85f), Offset(tc.x - tr * 0.05f, tc.y - tr * 0.93f), Size(tr * 0.26f, tr * 0.05f))
                            drawOval(NUBE.copy(alpha = 0.8f), Offset(tc.x + tr * 0.32f, tc.y - tr * 1.02f), Size(tr * 0.2f, tr * 0.05f))
                        }
                        drawRect(Brush.horizontalGradient(listOf(Color.Transparent, VACIO.copy(alpha = 0.75f)), tc.x + tr * 0.1f, tc.x + tr * 0.85f), Offset(tc.x - tr, tc.y - tr), Size(tr * 2, tr * 2))
                    }
                    drawArc(ATMOSFERA.copy(alpha = 0.9f), 205f, 110f, false, Offset(tc.x - tr, tc.y - tr), Size(tr * 2, tr * 2), style = Stroke(r * 0.045f))
                }
                1 -> {
                    estrellas(Rect(c, r), semilla, 18, r * 0.06f)
                    val lc = Offset(c.x - r * 0.12f, c.y + r * 0.08f); val lr = r * 0.52f
                    halo(lc, lr * 1.4f, LUNA.copy(alpha = 0.18f))
                    drawCircle(Brush.radialGradient(listOf(Color(0xFFE6E7EA), LUNA, LUNA_OSC), Offset(lc.x - lr * 0.4f, lc.y - lr * 0.4f), lr * 1.6f), lr, lc)
                    clipPath(Path().apply { addOval(Rect(lc, lr)) }) {
                        listOf(Triple(-0.35f, -0.2f, 0.22f), Triple(0.2f, 0.25f, 0.16f), Triple(0.3f, -0.35f, 0.12f), Triple(-0.15f, 0.45f, 0.1f), Triple(-0.5f, 0.25f, 0.08f)).forEach { (dx, dy, cr) ->
                            val cc = Offset(lc.x + lr * dx, lc.y + lr * dy)
                            drawCircle(LUNA_OSC.copy(alpha = 0.7f), lr * cr, cc)
                            drawArc(Color.White.copy(alpha = 0.45f), 20f, 140f, false, Offset(cc.x - lr * cr, cc.y - lr * cr), Size(lr * cr * 2, lr * cr * 2), style = Stroke(lr * 0.03f))
                        }
                        drawOval(LUNA_OSC.copy(alpha = 0.35f), Offset(lc.x - lr * 0.2f, lc.y - lr * 0.7f), Size(lr * 0.7f, lr * 0.4f))
                        drawCircle(VACIO.copy(alpha = 0.72f), lr * 1.05f, Offset(lc.x + lr * 0.75f, lc.y + lr * 0.25f))
                    }
                }
                else -> {
                    halo(Offset(c.x - r * 0.25f, c.y), r * 0.95f, NEBULOSA.copy(alpha = 0.55f))
                    halo(Offset(c.x + r * 0.1f, c.y - r * 0.35f), r * 0.6f, ROSA.copy(alpha = 0.35f))
                    estrellas(Rect(c, r), semilla, 26, r * 0.055f)
                    val pc = Offset(c.x + r * 0.22f, c.y + r * 0.12f); val pr = r * 0.36f
                    val anillo = Rect(pc.x - pr * 1.9f, pc.y - pr * 0.42f, pc.x + pr * 1.9f, pc.y + pr * 0.42f)
                    drawArc(CIAN.copy(alpha = 0.55f), 180f, 180f, false, anillo.topLeft, anillo.size, style = Stroke(pr * 0.14f))
                    drawCircle(Brush.radialGradient(listOf(Color(0xFFF0C08A), PLANETA, PLANETA_OSC), Offset(pc.x - pr * 0.4f, pc.y - pr * 0.4f), pr * 1.5f), pr, pc)
                    clipPath(Path().apply { addOval(Rect(pc, pr)) }) {
                        for (j in 0 until 3) drawRect(PLANETA_OSC.copy(alpha = 0.3f), Offset(pc.x - pr, pc.y - pr * 0.5f + j * pr * 0.42f), Size(pr * 2, pr * 0.12f))
                        drawCircle(VACIO.copy(alpha = 0.45f), pr, Offset(pc.x + pr * 0.45f, pc.y + pr * 0.3f))
                    }
                    drawArc(CIAN, 0f, 180f, false, anillo.topLeft, anillo.size, style = Stroke(pr * 0.14f))
                }
            }
            // El reflejo del vidrio.
            drawArc(Color.White.copy(alpha = 0.2f), 200f, 55f, false, Offset(c.x - r * 0.78f, c.y - r * 0.78f), Size(r * 1.56f, r * 1.56f), style = Stroke(r * 0.07f, cap = StrokeCap.Round))
        }
    }

    /** Un ojo de buey: la brida con ocho pernos, el labio interior y el vidrio con su vista. */
    private fun DrawScope.ojoDeBuey(c: Offset, r: Float, tipo: Int, p: Pal, semilla: Int) {
        drawCircle(NEGRO.copy(alpha = 0.35f), r * 1.34f, Offset(c.x + r * 0.06f, c.y + r * 0.1f))
        drawCircle(Brush.radialGradient(listOf(p.cascoLuz, p.casco, p.cascoOsc), Offset(c.x - r * 0.5f, c.y - r * 0.5f), r * 2f), r * 1.3f, c)
        drawCircle(p.cascoLuz, r * 1.3f, c, style = Stroke(r * 0.04f))
        for (j in 0 until 8) {
            val a = Math.toRadians(22.5 + j * 45.0)
            tornillo(Offset(c.x + (r * 1.16f * cos(a)).toFloat(), c.y + (r * 1.16f * sin(a)).toFloat()), r * 0.075f, p.oscuro)
        }
        drawCircle(p.cascoOsc, r * 1.05f, c)
        drawCircle(NEGRO, r * 1.01f, c)
        vista(c, r, tipo, semilla)
    }

    /** La pared del módulo: los paneles del casco con sus juntas, sus sujeciones y la curva que oscurece los lados. */
    private fun DrawScope.pared(p: Pal, u: Float) {
        val w = size.width; val h = size.height
        drawRect(Brush.verticalGradient(listOf(p.paredLuz, p.pared), 0f, h))
        val cols = (w / (32f * u)).toInt().coerceIn(3, 8)
        val pw = w / cols
        for (i in 0..cols) {
            val x = pw * i
            drawLine(p.junta, Offset(x, 0f), Offset(x, h), 0.5f * u)
            drawLine(p.paredLuz, Offset(x + 0.5f * u, 0f), Offset(x + 0.5f * u, h), 0.3f * u)
        }
        listOf(h * 0.5f).forEach { y ->
            drawLine(p.junta, Offset(0f, y), Offset(w, y), 0.5f * u)
            drawLine(p.paredLuz, Offset(0f, y + 0.5f * u), Offset(w, y + 0.5f * u), 0.3f * u)
        }
        val puntos = ArrayList<Offset>()
        for (i in 0 until cols) for (y in listOf(2.2f * u, h * 0.5f - 2.2f * u, h * 0.5f + 2.2f * u, h - 2.2f * u)) {
            puntos.add(Offset(pw * i + 2.2f * u, y)); puntos.add(Offset(pw * (i + 1) - 2.2f * u, y))
        }
        drawPoints(puntos, PointMode.Points, p.paredOsc, 1.1f * u, StrokeCap.Round)
        drawPoints(puntos.map { Offset(it.x - 0.2f * u, it.y - 0.2f * u) }, PointMode.Points, p.paredLuz, 0.4f * u, StrokeCap.Round)
        drawRect(Brush.horizontalGradient(listOf(NEGRO.copy(alpha = 0.16f), Color.Transparent, Color.Transparent, NEGRO.copy(alpha = 0.16f)), 0f, w))
    }

    /** Una lámpara de techo: la carcasa y el difusor, que de noche se enciende con su halo. */
    private fun DrawScope.lampara(r: Rect, p: Pal, e: EstadoMueble, u: Float) {
        if (p.oscuro) halo(Offset(r.center.x, r.bottom), r.width * 0.55f, mez(e.acento, Color.White, 0.5f).copy(alpha = 0.16f))
        sombra(r, u, 0.6f * u)
        pieza(r, p.casco, p.cascoLuz, p.cascoOsc, 0.35f * u, 0.6f * u)
        val dif = Rect(r.left + 1f * u, r.top + r.height * 0.3f, r.right - 1f * u, r.bottom - r.height * 0.22f)
        val luz = if (p.oscuro) mez(e.acento, Color.White, 0.72f) else Color(0xFFF9FBFE)
        drawRoundRect(luz, dif.topLeft, dif.size, CornerRadius(0.4f * u))
        if (!p.oscuro) drawRoundRect(p.paredOsc, dif.topLeft, dif.size, CornerRadius(0.4f * u), style = Stroke(0.25f * u))
        var x = dif.left + 2.5f * u
        while (x < dif.right - 1f * u) { drawLine(NEGRO.copy(alpha = 0.08f), Offset(x, dif.top), Offset(x, dif.bottom), 0.25f * u); x += 2.5f * u }
    }

    /** El marco atornillado alrededor de un nicho: la sombra, el bisel, el labio y los corchetes del acento. */
    private fun DrawScope.marco(n: Rect, g: Float, p: Pal, e: EstadoMueble, u: Float): Rect {
        val fr = Rect(n.left - g, n.top - g, n.right + g, n.bottom + g)
        sombra(fr, u, 1.4f * u, 0.4f)
        pieza(fr, p.casco, p.cascoLuz, p.cascoOsc, 0.5f * u, 1.4f * u)
        drawRoundRect(p.cascoOsc, Offset(n.left - 0.7f * u, n.top - 0.7f * u), Size(n.width + 1.4f * u, n.height + 1.4f * u), CornerRadius(0.9f * u))
        drawLine(p.cascoLuz, Offset(n.left - 0.4f * u, n.bottom + 0.75f * u), Offset(n.right + 0.4f * u, n.bottom + 0.75f * u), 0.3f * u)
        corchetes(Rect(fr.left + 0.9f * u, fr.top + 0.9f * u, fr.right - 0.9f * u, fr.bottom - 0.9f * u), 4.5f * u, e.acento.copy(alpha = 0.9f), 0.45f * u)
        return fr
    }

    /** El fondo de un nicho: sólido, de la luminancia de la tarjeta, con la cuadrícula muy tenue y la sombra del labio. */
    private fun DrawScope.fondoNicho(n: Rect, p: Pal, u: Float, paso: Float) {
        drawRoundRect(p.nicho, n.topLeft, n.size, CornerRadius(0.6f * u))
        clipRect(n.left, n.top, n.right, n.bottom) {
            var x = n.left + paso
            while (x < n.right) { drawLine(p.nichoLinea, Offset(x, n.top), Offset(x, n.bottom), 0.22f * u); x += paso }
            var y = n.top + paso
            while (y < n.bottom) { drawLine(p.nichoLinea, Offset(n.left, y), Offset(n.right, y), 0.22f * u); y += paso }
            drawRect(Brush.verticalGradient(listOf(NEGRO.copy(alpha = if (p.oscuro) 0.4f else 0.1f), Color.Transparent), n.top, n.top + 2f * u), n.topLeft, Size(n.width, 2f * u))
            drawRect(Brush.horizontalGradient(listOf(NEGRO.copy(alpha = if (p.oscuro) 0.3f else 0.07f), Color.Transparent), n.left, n.left + 1.6f * u), n.topLeft, Size(1.6f * u, n.height))
        }
    }

    /** Una pantallita de telemetría: el vidrio oscuro, la onda y las barras. */
    private fun DrawScope.telemetria(r: Rect, color: Color, semilla: Int, u: Float) {
        pieza(r, PANTALLA, NEGRO, Color(0xFF3A4560), 0.3f * u, 0.5f * u)
        val t = Rect(r.left + 0.9f * u, r.top + 0.9f * u, r.right - 0.9f * u, r.bottom - 0.9f * u)
        halo(t.center, t.width * 0.6f, color.copy(alpha = 0.1f))
        clipRect(t.left, t.top, t.right, t.bottom) {
            val onda = Path()
            for (k in 0..24) {
                val x = t.left + t.width * k / 24f
                val y = t.top + t.height * (0.32f + 0.2f * sin(k * 0.95f + semilla))
                if (k == 0) onda.moveTo(x, y) else onda.lineTo(x, y)
            }
            drawPath(onda, color, style = Stroke(0.4f * u, join = StrokeJoin.Round))
            for (j in 0 until 3) drawRect(color.copy(alpha = 0.55f), Offset(t.left + 0.4f * u, t.top + t.height * (0.66f + j * 0.12f)), Size(t.width * (0.3f + 0.5f * azar(semilla, j)), t.height * 0.06f))
        }
    }

    // ---------------------------------------------------------------- Social: el comedor de la tripulación

    override fun DrawScope.muebleSocial(e: EstadoMueble, oscuro: Boolean) {
        if (size.minDimension < 1f) return
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val p = pal(oscuro)
        val n = nicho(MuebleVida.SOCIAL)
        pared(p, u)

        // El techo: la viga con sus mazos de cables y la lámpara del centro.
        val techo = Rect(0f, 0f, w, h * 0.065f)
        pieza(techo, p.casco, p.cascoLuz, p.cascoOsc, 0.4f * u)
        drawRect(NEGRO.copy(alpha = 0.25f), Offset(0f, techo.bottom), Size(w, 0.8f * u))
        listOf(-1f, 1f).forEach { s ->
            val x0 = if (s < 0) 0f else w; val x1 = w / 2 + s * w * 0.24f
            CABLES.take(4).forEachIndexed { j, col ->
                val y = techo.bottom + (0.8f + j * 0.75f) * u
                drawPath(Path().apply { moveTo(x0, y); quadraticTo((x0 + x1) / 2, y + 2.2f * u, x1, y - 0.2f * u) }, mez(col, NEGRO, if (oscuro) 0.45f else 0.1f), style = Stroke(0.7f * u, cap = StrokeCap.Round))
            }
            listOf(0.25f, 0.6f).forEach { f ->
                val cx = x0 + (x1 - x0) * f
                drawRoundRect(METAL_OSC, Offset(cx - 0.7f * u, techo.bottom + 0.2f * u), Size(1.4f * u, 4f * u), CornerRadius(0.3f * u))
            }
        }
        lampara(Rect(w * 0.36f, techo.bottom - 0.6f * u, w * 0.64f, techo.bottom + 3.6f * u), p, e, u)

        // La pizarra de la tripulación (el nicho) en su marco, con la cabecera de pilotos.
        val fr = marco(n, 2.8f * u, p, e, u)
        fondoNicho(n, p, u, 5f * u)
        listOf(fr.topLeft, Offset(fr.right, fr.top), Offset(fr.left, fr.bottom), fr.bottomRight).forEach { c ->
            tornillo(Offset(c.x + (if (c.x < w / 2) 1.4f else -1.4f) * u, c.y + (if (c.y < h / 2) 1.4f else -1.4f) * u), 0.7f * u, oscuro)
        }
        val cab = Rect(w / 2 - 9f * u, fr.top - 2.6f * u, w / 2 + 9f * u, fr.top + 1.2f * u)
        sombra(cab, u, 0.6f * u)
        pieza(cab, p.casco, p.cascoLuz, p.cascoOsc, 0.35f * u, 0.6f * u)
        listOf(VERDE, VERDE, AMBAR, e.acento).forEachIndexed { j, c -> piloto(Offset(cab.left + 3f * u + j * 4f * u, cab.center.y - 0.3f * u), 0.55f * u, c, j != 2 || oscuro) }

        // Los costados: un ojo de buey (la Tierra a la izquierda, la Luna a la derecha) y su panelito de telemetría.
        val col = fr.left
        listOf(0, 1).forEach { lado ->
            val cx = if (lado == 0) col * 0.5f else w - col * 0.5f
            val r = minOf(col * 0.31f, h * 0.13f)
            ojoDeBuey(Offset(cx, h * 0.33f), r, lado, p, e.semilla + lado * 7)
            val pr = Rect(cx - col * 0.34f, h * 0.555f, cx + col * 0.34f, h * 0.69f)
            sombra(pr, u, 0.6f * u)
            pieza(pr, p.casco, p.cascoLuz, p.cascoOsc, 0.35f * u, 0.6f * u)
            telemetria(Rect(pr.left + 1f * u, pr.top + 1f * u, pr.right - 1f * u, pr.bottom - 3.2f * u), if (lado == 0) VERDE else CIAN, e.semilla + lado, u)
            piloto(Offset(pr.left + 2f * u, pr.bottom - 1.6f * u), 0.45f * u, VERDE, true)
            piloto(Offset(pr.left + 4f * u, pr.bottom - 1.6f * u), 0.45f * u, AMBAR, lado == 1)
            drawLine(p.cascoOsc, Offset(pr.right - 6f * u, pr.bottom - 1.6f * u), Offset(pr.right - 1.6f * u, pr.bottom - 1.6f * u), 0.6f * u, StrokeCap.Round)
        }

        // El pasamanos bajo la pizarra.
        val ph = fr.bottom + (h * 0.855f - fr.bottom) * 0.5f
        pasamanos(Offset(w * 0.05f, ph), Offset(w * 0.95f, ph), 1.1f * u, e.secundario)

        // La mesa: el tablero claro, el canto con las tiras de velcro y los pernos, las patas y el piso con sus estribos.
        val piso = h * 0.965f
        drawRect(p.piso, Offset(0f, piso), Size(w, h - piso))
        drawLine(p.pisoLinea, Offset(0f, piso), Offset(w, piso), 0.5f * u)
        var gx = 0f; var k = 0
        while (gx < w && k < 40) { drawLine(p.pisoLinea, Offset(gx, piso), Offset(gx, h), 0.3f * u); gx += 9f * u; k++ }
        val tab = Rect(w * 0.03f, h * 0.858f, w * 0.97f, h * 0.875f)
        val canto = Rect(tab.left, tab.bottom, tab.right, h * 0.912f)
        listOf(w * 0.16f, w * 0.84f).forEach { x ->
            val pata = Rect(x - 2.4f * u, canto.bottom, x + 2.4f * u, piso)
            pieza(pata, p.casco, p.cascoLuz, p.cascoOsc, 0.35f * u)
            pieza(Rect(x - 3.6f * u, piso - 1.2f * u, x + 3.6f * u, piso + 0.4f * u), p.cascoOsc, p.cascoLuz, NEGRO, 0.3f * u, 0.4f * u)
            listOf(-1f, 1f).forEach { s ->
                val ex = x + s * 9f * u
                drawArc(mez(e.secundario, NEGRO, 0.2f), 180f, 180f, false, Offset(ex - 3f * u, piso - 1.6f * u), Size(6f * u, 3.6f * u), style = Stroke(0.9f * u))
            }
        }
        drawRect(NEGRO.copy(alpha = 0.3f), Offset(canto.left + 1f * u, canto.bottom), Size(canto.width - 2f * u, 1.2f * u))
        drawRect(Brush.verticalGradient(listOf(p.paredLuz, mez(p.paredLuz, p.paredOsc, 0.5f)), tab.top, tab.bottom), tab.topLeft, tab.size)
        drawLine(Color.White.copy(alpha = if (oscuro) 0.12f else 0.7f), Offset(tab.left, tab.top + 0.2f * u), Offset(tab.right, tab.top + 0.2f * u), 0.35f * u)
        pieza(canto, p.casco, p.cascoLuz, p.cascoOsc, 0.35f * u)
        listOf(0.22f, 0.5f, 0.78f).forEach { f ->
            val v = Rect(w * f - w * 0.08f, canto.top + canto.height * 0.25f, w * f + w * 0.08f, canto.bottom - canto.height * 0.25f)
            drawRoundRect(mez(CINTA, NEGRO, if (oscuro) 0.5f else 0f), v.topLeft, v.size, CornerRadius(0.4f * u))
            clipRect(v.left, v.top, v.right, v.bottom) {
                val pts = ArrayList<Offset>()
                var vx = v.left + 0.5f * u
                while (vx < v.right) { pts.add(Offset(vx, v.center.y - 0.4f * u)); pts.add(Offset(vx + 0.45f * u, v.center.y + 0.4f * u)); vx += 0.9f * u }
                drawPoints(pts, PointMode.Points, NEGRO.copy(alpha = 0.25f), 0.35f * u, StrokeCap.Round)
            }
        }
        listOf(canto.left + 1.6f * u, canto.right - 1.6f * u).forEach { x -> tornillo(Offset(x, canto.center.y), 0.6f * u, oscuro) }

        // Sobre la mesa: dos bolsas de bebida a la izquierda y dos latas a la derecha, sujetas con velcro.
        val base = tab.top + 0.3f * u
        listOf(col * 0.3f, col * 0.66f).forEachIndexed { j, x ->
            val bw = 4.6f * u; val bh = 9f * u
            val b = Rect(x - bw / 2, base - bh, x + bw / 2, base)
            drawRoundRect(NEGRO.copy(alpha = 0.3f), Offset(b.left + 0.4f * u, b.top + 0.6f * u), b.size, CornerRadius(1f * u))
            drawRoundRect(Brush.horizontalGradient(listOf(METAL_LUZ, METAL, METAL_OSC), b.left, b.right), b.topLeft, b.size, CornerRadius(1f * u))
            val et = listOf(e.secundario, CIAN)[j]
            drawRect(mez(et, NEGRO, if (oscuro) 0.3f else 0f), Offset(b.left, b.top + bh * 0.45f), Size(bw, bh * 0.3f))
            drawLine(Color.White.copy(alpha = 0.5f), Offset(b.left + bw * 0.25f, b.top + 1f * u), Offset(b.left + bw * 0.25f, b.bottom - 1f * u), 0.35f * u)
            // La pajilla con su pinza.
            drawLine(Color(0xFFEEF0F4), Offset(x + bw * 0.2f, b.top + 0.4f * u), Offset(x + bw * 0.2f, b.top - 3f * u), 0.6f * u, StrokeCap.Round)
            drawRect(Color(0xFF2B2F38), Offset(x + bw * 0.2f - 0.8f * u, b.top - 1.6f * u), Size(1.6f * u, 1f * u))
        }
        listOf(w - col * 0.32f, w - col * 0.66f).forEachIndexed { j, x ->
            val lw = 4.4f * u; val lh = 6.4f * u
            val l = Rect(x - lw / 2, base - lh, x + lw / 2, base)
            drawRect(NEGRO.copy(alpha = 0.3f), Offset(l.left + 0.4f * u, l.top + 0.6f * u), l.size)
            drawRect(Brush.horizontalGradient(listOf(METAL_LUZ, METAL, METAL_OSC), l.left, l.right), l.topLeft, l.size)
            drawRect(mez(listOf(e.acento, VERDE)[j], NEGRO, if (oscuro) 0.25f else 0.1f), Offset(l.left, l.top + lh * 0.25f), Size(lw, lh * 0.5f))
            drawOval(METAL_LUZ, Offset(l.left, l.top - 0.6f * u), Size(lw, 1.2f * u))
            drawOval(METAL_OSC, Offset(l.left, l.top - 0.6f * u), Size(lw, 1.2f * u), style = Stroke(0.2f * u))
        }
    }

    // ---------------------------------------------------------------- Mapas: la consola de navegación

    override fun DrawScope.muebleMapas(e: EstadoMueble, oscuro: Boolean) {
        if (size.minDimension < 1f) return
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val p = pal(oscuro)
        val n = nicho(MuebleVida.MAPAS)
        pared(p, u)

        // El piso de rejilla y la sombra de la consola.
        val piso = h * 0.955f
        drawRect(p.piso, Offset(0f, piso), Size(w, h - piso))
        var gx = 0f; var k = 0
        while (gx < w && k < 60) { drawLine(p.pisoLinea, Offset(gx, piso), Offset(gx - 1.5f * u, h), 0.35f * u); gx += 4.5f * u; k++ }
        drawLine(p.pisoLinea, Offset(0f, piso), Offset(w, piso), 0.5f * u)
        drawOval(NEGRO.copy(alpha = 0.3f), Offset(w * 0.06f, piso - 1.2f * u), Size(w * 0.88f, 3f * u))

        // Las patas magnéticas.
        listOf(w * 0.12f, w * 0.88f).forEach { x ->
            pieza(Rect(x - 2.2f * u, h * 0.9f, x + 2.2f * u, piso - 1f * u), p.cascoOsc, p.cascoLuz, NEGRO, 0.3f * u)
            pieza(Rect(x - 4f * u, piso - 1.8f * u, x + 4f * u, piso + 0.3f * u), METAL_OSC, METAL, NEGRO, 0.3f * u, 0.8f * u)
        }

        // El cuerpo de la consola.
        val cuerpo = Rect(w * 0.03f, h * 0.025f, w * 0.97f, h * 0.775f)
        sombra(cuerpo, u, 1.8f * u, 0.4f)
        pieza(cuerpo, p.casco, p.cascoLuz, p.cascoOsc, 0.6f * u, 1.8f * u)
        // La fila de indicadores de arriba: cinco relojes con su aguja y sus lecturas entre ellos.
        val fy = (cuerpo.top + n.top - 2.6f * u) / 2
        val fr = minOf((n.top - 2.6f * u - cuerpo.top) * 0.36f, w * 0.03f)
        for (j in 0 until 5) {
            val x = n.left + n.width * (0.1f + j * 0.2f)
            drawCircle(NEGRO.copy(alpha = 0.4f), fr * 1.15f, Offset(x + 0.3f * u, fy + 0.4f * u))
            drawCircle(p.cascoLuz, fr * 1.15f, Offset(x, fy))
            drawCircle(if (oscuro) PANTALLA else Color(0xFFF2F4F8), fr, Offset(x, fy))
            for (t in 0 until 7) {
                val a = Math.toRadians(150.0 + t * 40.0)
                drawLine(if (oscuro) CIAN.copy(alpha = 0.6f) else p.casco, Offset(x + (fr * 0.7f * cos(a)).toFloat(), fy + (fr * 0.7f * sin(a)).toFloat()), Offset(x + (fr * 0.88f * cos(a)).toFloat(), fy + (fr * 0.88f * sin(a)).toFloat()), 0.25f * u)
            }
            val a = Math.toRadians(170.0 + 200.0 * azar(e.semilla, j))
            drawLine(e.secundario, Offset(x, fy), Offset(x + (fr * 0.75f * cos(a)).toFloat(), fy + (fr * 0.75f * sin(a)).toFloat()), 0.4f * u, StrokeCap.Round)
            drawCircle(p.cascoOsc, fr * 0.14f, Offset(x, fy))
            if (j < 4) {
                val lx = x + n.width * 0.1f
                val lr = Rect(lx - n.width * 0.055f, fy - fr * 0.45f, lx + n.width * 0.055f, fy + fr * 0.45f)
                pieza(lr, PANTALLA, NEGRO, p.cascoLuz, 0.25f * u, 0.3f * u)
                for (d in 0 until 4) drawRect((if (j % 2 == 0) VERDE else AMBAR).copy(alpha = if (azar(e.semilla + 3, j * 4 + d) < 0.75f) 0.85f else 0.2f),
                    Offset(lr.left + lr.width * (0.12f + d * 0.2f), lr.top + lr.height * 0.28f), Size(lr.width * 0.13f, lr.height * 0.44f))
            }
        }

        // El tablero de trazado (el nicho) en su bisel.
        val bis = Rect(n.left - 2.4f * u, n.top - 2.4f * u, n.right + 2.4f * u, n.bottom + 2.4f * u)
        pieza(bis, p.cascoOsc, NEGRO.copy(alpha = 0.6f), p.cascoLuz, 0.5f * u, 1f * u)
        fondoNicho(n, p, u, 6f * u)
        corchetes(Rect(n.left - 1.4f * u, n.top - 1.4f * u, n.right + 1.4f * u, n.bottom + 1.4f * u), 4f * u, e.acento, 0.4f * u)

        // A la izquierda: la esfera de actitud en su caja atornillada, y la perilla con su escala.
        val lado = n.left - 2.4f * u - cuerpo.left
        val ix = cuerpo.left + lado / 2
        val er = minOf(lado * 0.3f, h * 0.11f)
        val caja = Rect(ix - er * 1.45f, h * 0.3f - er * 1.45f, ix + er * 1.45f, h * 0.3f + er * 1.45f)
        pieza(caja, p.cascoOsc, p.cascoLuz, NEGRO, 0.35f * u, 0.8f * u)
        listOf(caja.topLeft, Offset(caja.right, caja.top), Offset(caja.left, caja.bottom), caja.bottomRight).forEach { c ->
            tornillo(Offset(c.x + (if (c.x < ix) 1f else -1f) * er * 0.22f, c.y + (if (c.y < h * 0.3f) 1f else -1f) * er * 0.22f), er * 0.09f, oscuro)
        }
        val ec = Offset(ix, h * 0.3f)
        drawCircle(NEGRO, er * 1.12f, ec)
        clipPath(Path().apply { addOval(Rect(ec, er)) }) {
            rotate(-14f, ec) {
                drawRect(Brush.verticalGradient(listOf(Color(0xFFF4F5F7), Color(0xFFBFC5CF)), ec.y - er, ec.y), Offset(ec.x - er * 1.5f, ec.y - er * 1.5f), Size(er * 3f, er * 1.5f))
                drawRect(Brush.verticalGradient(listOf(Color(0xFF3A3F4A), Color(0xFF15181E)), ec.y, ec.y + er), Offset(ec.x - er * 1.5f, ec.y), Size(er * 3f, er * 1.5f))
                listOf(-0.55f, -0.28f, 0.28f, 0.55f).forEach { f ->
                    drawArc(if (f < 0) Color(0xFF5A6070) else Color(0xFFB8BEC8), if (f < 0) 200f else 20f, 140f, false,
                        Offset(ec.x - er * 1.3f, ec.y + er * f - er * 0.18f), Size(er * 2.6f, er * 0.36f), style = Stroke(0.25f * u))
                }
                drawLine(Color.White, Offset(ec.x - er * 1.2f, ec.y), Offset(ec.x + er * 1.2f, ec.y), 0.35f * u)
            }
            drawCircle(Brush.radialGradient(listOf(Color.White.copy(alpha = 0.25f), Color.Transparent), Offset(ec.x - er * 0.4f, ec.y - er * 0.4f), er), er, ec)
        }
        drawCircle(p.cascoLuz, er * 1.06f, ec, style = Stroke(er * 0.12f))
        // El símbolo del vehículo: las alas y el punto, en el secundario.
        drawLine(e.secundario, Offset(ec.x - er * 0.62f, ec.y), Offset(ec.x - er * 0.2f, ec.y), 0.6f * u, StrokeCap.Round)
        drawLine(e.secundario, Offset(ec.x + er * 0.2f, ec.y), Offset(ec.x + er * 0.62f, ec.y), 0.6f * u, StrokeCap.Round)
        drawCircle(e.secundario, 0.6f * u, ec)
        val kc = Offset(ix, h * 0.6f); val kr = minOf(lado * 0.17f, h * 0.06f)
        for (t in 0 until 9) {
            val a = Math.toRadians(135.0 + t * 33.75)
            drawLine(p.cascoLuz, Offset(kc.x + (kr * 1.3f * cos(a)).toFloat(), kc.y + (kr * 1.3f * sin(a)).toFloat()), Offset(kc.x + (kr * 1.55f * cos(a)).toFloat(), kc.y + (kr * 1.55f * sin(a)).toFloat()), 0.3f * u)
        }
        drawCircle(NEGRO.copy(alpha = 0.45f), kr, Offset(kc.x + 0.3f * u, kc.y + 0.5f * u))
        drawCircle(Brush.radialGradient(listOf(METAL_LUZ, METAL, METAL_OSC), Offset(kc.x - kr * 0.3f, kc.y - kr * 0.3f), kr * 1.4f), kr, kc)
        drawLine(p.cascoOsc, kc, Offset(kc.x + kr * 0.6f, kc.y - kr * 0.6f), 0.5f * u, StrokeCap.Round)

        // A la derecha: el globo terrestre en su cardán y los dos interruptores con tapa de seguridad.
        val dx = cuerpo.right - lado / 2
        val gr = minOf(lado * 0.27f, h * 0.1f)
        val gc = Offset(dx, h * 0.27f)
        drawRoundRect(METAL_OSC, Offset(dx - 0.5f * u, gc.y + gr), Size(1f * u, gr * 0.55f))
        drawOval(NEGRO.copy(alpha = 0.4f), Offset(dx - gr * 0.75f, gc.y + gr * 1.5f), Size(gr * 1.5f, gr * 0.35f))
        drawOval(Brush.horizontalGradient(listOf(METAL_LUZ, METAL_OSC), dx - gr * 0.7f, dx + gr * 0.7f), Offset(dx - gr * 0.7f, gc.y + gr * 1.42f), Size(gr * 1.4f, gr * 0.32f))
        drawCircle(Brush.radialGradient(listOf(MAR_LUZ, MAR, MAR_OSC), Offset(gc.x - gr * 0.4f, gc.y - gr * 0.4f), gr * 1.5f), gr, gc)
        clipPath(Path().apply { addOval(Rect(gc, gr)) }) {
            drawOval(TIERRA, Offset(gc.x - gr * 0.75f, gc.y - gr * 0.65f), Size(gr * 0.65f, gr * 0.55f))
            drawOval(DESIERTO, Offset(gc.x - gr * 0.05f, gc.y - gr * 0.2f), Size(gr * 0.45f, gr * 0.7f))
            drawOval(TIERRA, Offset(gc.x - gr * 0.5f, gc.y + gr * 0.1f), Size(gr * 0.35f, gr * 0.6f))
            drawOval(NUBE.copy(alpha = 0.7f), Offset(gc.x - gr * 0.6f, gc.y - gr * 0.05f), Size(gr * 0.7f, gr * 0.1f))
            drawCircle(NEGRO.copy(alpha = 0.35f), gr, Offset(gc.x + gr * 0.5f, gc.y + gr * 0.35f))
        }
        drawArc(Brush.linearGradient(listOf(METAL_LUZ, METAL_OSC), Offset(gc.x - gr, gc.y), Offset(gc.x + gr, gc.y)), -110f, 220f, false,
            Offset(gc.x - gr * 1.18f, gc.y - gr * 1.18f), Size(gr * 2.36f, gr * 2.36f), style = Stroke(0.55f * u))
        drawCircle(METAL, 0.5f * u, Offset(gc.x - gr * 0.4f, gc.y - gr * 1.1f))
        listOf(h * 0.55f, h * 0.66f).forEachIndexed { j, y ->
            val sw = Rect(dx - lado * 0.2f, y - 1.8f * u, dx + lado * 0.2f, y + 1.8f * u)
            pieza(sw, p.cascoOsc, p.cascoLuz, NEGRO, 0.3f * u, 0.4f * u)
            drawCircle(METAL_OSC, 0.7f * u, sw.center)
            drawLine(METAL_LUZ, sw.center, Offset(sw.center.x, sw.center.y - 2f * u), 0.6f * u, StrokeCap.Round)
            // La tapa de seguridad roja, levantada en la de arriba.
            val tapa = if (j == 0) Rect(sw.left - 0.3f * u, sw.top - 2.6f * u, sw.right + 0.3f * u, sw.top - 0.4f * u) else Rect(sw.left - 0.3f * u, sw.top - 0.3f * u, sw.right + 0.3f * u, sw.bottom + 0.3f * u)
            drawRoundRect(ROJO.copy(alpha = 0.6f), tapa.topLeft, tapa.size, CornerRadius(0.5f * u))
            drawRoundRect(mez(ROJO, NEGRO, 0.3f), tapa.topLeft, tapa.size, CornerRadius(0.5f * u), style = Stroke(0.3f * u))
            drawLine(Color.White.copy(alpha = 0.5f), Offset(tapa.left + 0.6f * u, tapa.top + 0.5f * u), Offset(tapa.right - 0.6f * u, tapa.top + 0.5f * u), 0.25f * u)
            piloto(Offset(sw.right + 1.6f * u, y), 0.45f * u, if (j == 0) AMBAR else VERDE, true)
        }

        // Abajo del tablero: la pantallita verde del ordenador de guiado y los pilotos.
        val by = (n.bottom + 2.4f * u + cuerpo.bottom) / 2
        val bh = (cuerpo.bottom - n.bottom - 2.4f * u) * 0.5f
        val disp = Rect(n.left, by - bh / 2, n.left + n.width * 0.24f, by + bh / 2)
        pieza(disp, PANTALLA, NEGRO, p.cascoLuz, 0.25f * u, 0.3f * u)
        halo(disp.center, disp.width * 0.5f, VERDE.copy(alpha = 0.12f))
        for (d in 0 until 6) drawRect(VERDE.copy(alpha = 0.85f), Offset(disp.left + disp.width * (0.08f + d * 0.15f), disp.top + disp.height * 0.3f), Size(disp.width * 0.1f, disp.height * 0.4f))
        for (j in 0 until 8) piloto(Offset(n.right - 2f * u - j * 3.2f * u, by), 0.5f * u, if (j == 3) AMBAR else if (j == 6) e.acento else VERDE, azar(e.semilla + 9, j) < 0.7f)
        ranuras(Rect(n.left + n.width * 0.32f, by - bh * 0.35f, n.left + n.width * 0.6f, by + bh * 0.35f), 12, 0.5f * u, NEGRO, p.cascoLuz)

        // El frente inclinado con el teclado de guiado y el zócalo con las franjas de aviso.
        val fa = cuerpo.bottom; val fb = h * 0.865f
        drawPath(Path().apply { moveTo(cuerpo.left, fa); lineTo(cuerpo.right, fa); lineTo(cuerpo.right - w * 0.015f, fb); lineTo(cuerpo.left + w * 0.015f, fb); close() }, p.cascoOsc)
        drawLine(p.cascoLuz, Offset(cuerpo.left + 1f * u, fa + 0.3f * u), Offset(cuerpo.right - 1f * u, fa + 0.3f * u), 0.4f * u)
        val filas = 2; val nt = 14
        val tx0 = w * 0.2f; val tx1 = w * 0.8f; val tp = (tx1 - tx0) / nt
        for (fi in 0 until filas) {
            val ty = fa + (fb - fa) * (0.14f + fi * 0.43f); val th = (fb - fa) * 0.32f
            for (j in 0 until nt) {
                val tr = Rect(tx0 + tp * j + tp * 0.1f, ty, tx0 + tp * (j + 1) - tp * 0.1f, ty + th)
                val cara = if (j == nt - 1 && fi == 0) e.secundario else p.tecla
                pieza(tr, cara, if (j == nt - 1 && fi == 0) mez(e.secundario, Color.White, 0.3f) else p.teclaLuz, NEGRO.copy(alpha = 0.7f), 0.22f * u, 0.3f * u)
                if (cara == p.tecla) drawLine(p.cascoOsc.copy(alpha = 0.6f), Offset(tr.center.x - tr.width * 0.2f, tr.center.y), Offset(tr.center.x + tr.width * 0.2f, tr.center.y), 0.25f * u)
            }
        }
        listOf(w * 0.085f, w * 0.13f).forEachIndexed { j, x -> piloto(Offset(x, (fa + fb) / 2), 0.55f * u, if (j == 0) VERDE else e.acento, true) }
        listOf(w * 0.87f, w * 0.915f).forEach { x ->
            drawCircle(NEGRO.copy(alpha = 0.4f), 1.3f * u, Offset(x + 0.2f * u, (fa + fb) / 2 + 0.3f * u))
            drawCircle(Brush.radialGradient(listOf(METAL_LUZ, METAL_OSC), Offset(x - 0.4f * u, (fa + fb) / 2 - 0.4f * u), 1.8f * u), 1.2f * u, Offset(x, (fa + fb) / 2))
        }
        val zoc = Rect(cuerpo.left + w * 0.015f, fb, cuerpo.right - w * 0.015f, h * 0.9f)
        pieza(zoc, p.casco, p.cascoLuz, p.cascoOsc, 0.3f * u)
        franjas(Rect(zoc.left + w * 0.04f, zoc.top + zoc.height * 0.25f, zoc.right - w * 0.04f, zoc.bottom - zoc.height * 0.25f), mez(e.secundario, NEGRO, if (oscuro) 0.2f else 0f), 3f * u)
    }

    // ---------------------------------------------------------------- Otras vidas: el armario de muestras

    override fun DrawScope.muebleOtrasVidas(e: EstadoMueble, oscuro: Boolean) {
        if (size.minDimension < 1f) return
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val p = pal(oscuro)
        val n = nicho(MuebleVida.OTRAS_VIDAS)
        pared(p, u)
        val piso = h * 0.965f
        drawRect(p.piso, Offset(0f, piso), Size(w, h - piso))
        drawLine(p.pisoLinea, Offset(0f, piso), Offset(w, piso), 0.5f * u)

        // El armario: su sombra, la chapa y los rieles perforados de los costados.
        val arm = Rect(w * 0.012f, h * 0.02f, w * 0.988f, h * 0.935f)
        sombra(arm, u, 1.4f * u, 0.4f)
        pieza(arm, p.casco, p.cascoLuz, p.cascoOsc, 0.6f * u, 1.4f * u)
        listOf(Rect(arm.left + 1f * u, n.top - 1.2f * u, n.left - 1.4f * u, n.bottom + 1.2f * u), Rect(n.right + 1.4f * u, n.top - 1.2f * u, arm.right - 1f * u, n.bottom + 1.2f * u)).forEach { r ->
            pieza(r, mez(METAL, p.casco, if (oscuro) 0.6f else 0.25f), METAL_LUZ.copy(alpha = if (oscuro) 0.3f else 0.9f), METAL_OSC, 0.3f * u)
            var y = r.top + 2f * u; var k = 0
            while (y < r.bottom - 1.6f * u && k < 60) { drawRect(p.cascoOsc, Offset(r.center.x - 0.6f * u, y), Size(1.2f * u, 1.2f * u)); y += 2.6f * u; k++ }
        }

        // La cabecera: el panel hundido con el ojo de buey al planeta anillado, las dos pantallas y las filas de pilotos.
        val cab = Rect(arm.left + 1.4f * u, arm.top + 1.4f * u, arm.right - 1.4f * u, n.top - 2.4f * u)
        pieza(cab, p.cascoOsc, NEGRO.copy(alpha = 0.5f), p.cascoLuz, 0.4f * u, 0.8f * u)
        val oc = Offset(w / 2, cab.center.y)
        val or = cab.height * 0.36f
        ojoDeBuey(oc, or, 2, p, e.semilla + 4)
        val tw = minOf(w * 0.17f, (w / 2 - or * 1.5f - w * 0.17f))
        telemetria(Rect(oc.x - or * 1.5f - tw, cab.top + cab.height * 0.2f, oc.x - or * 1.5f, cab.bottom - cab.height * 0.2f), VERDE, e.semilla, u)
        telemetria(Rect(oc.x + or * 1.5f, cab.top + cab.height * 0.2f, oc.x + or * 1.5f + tw, cab.bottom - cab.height * 0.2f), CIAN, e.semilla + 2, u)
        for (j in 0 until 4) {
            piloto(Offset(cab.left + 3f * u + j * 2.6f * u, cab.center.y), 0.5f * u, if (j == 2) AMBAR else VERDE, j != 3)
            piloto(Offset(cab.right - 3f * u - j * 2.6f * u, cab.center.y), 0.5f * u, if (j == 1) e.acento else VERDE, j != 3 || oscuro)
        }

        // Las cinco bahías (el nicho): cada una con su arco, su lámpara y su luz, entre montantes atornillados.
        drawRect(p.cascoOsc, Offset(n.left - 0.8f * u, n.top - 0.8f * u), Size(n.width + 1.6f * u, n.height + 1.6f * u))
        val cw = n.width / 5
        for (i in 0 until 5) {
            val b = Rect(n.left + cw * i + 0.7f * u, n.top, n.left + cw * (i + 1) - 0.7f * u, n.bottom)
            val forma = Path().apply { addRoundRect(RoundRect(b, CornerRadius(2.4f * u, 2.4f * u))) }
            drawPath(forma, p.nicho)
            clipPath(forma) {
                drawRect(Brush.verticalGradient(listOf(mez(e.acento, p.nicho, 0.88f), p.nicho), b.top, b.top + b.height * 0.5f), b.topLeft, Size(b.width, b.height * 0.5f))
                drawRect(Brush.horizontalGradient(listOf(NEGRO.copy(alpha = if (oscuro) 0.35f else 0.08f), Color.Transparent), b.left, b.left + 1.4f * u), b.topLeft, Size(1.4f * u, b.height))
                drawRect(Brush.verticalGradient(listOf(Color.Transparent, NEGRO.copy(alpha = if (oscuro) 0.3f else 0.07f)), b.bottom - 3f * u, b.bottom), Offset(b.left, b.bottom - 3f * u), Size(b.width, 3f * u))
                // La lámpara del techo de la bahía.
                drawRoundRect(p.casco, Offset(b.left + b.width * 0.22f, b.top - 0.4f * u), Size(b.width * 0.56f, 1.5f * u), CornerRadius(0.5f * u))
                drawLine(if (oscuro) mez(e.acento, Color.White, 0.6f) else Color.White, Offset(b.left + b.width * 0.26f, b.top + 1.1f * u), Offset(b.right - b.width * 0.26f, b.top + 1.1f * u), 0.45f * u, StrokeCap.Round)
            }
        }
        for (i in 0..5) {
            val x = n.left + cw * i
            val mr = Rect(x - 0.9f * u, n.top - 0.6f * u, x + 0.9f * u, n.bottom + 0.6f * u)
            pieza(mr, p.casco, p.cascoLuz, p.cascoOsc, 0.25f * u)
            tornillo(Offset(x, n.top + 1.2f * u), 0.5f * u, oscuro)
            tornillo(Offset(x, n.bottom - 1.2f * u), 0.5f * u, oscuro)
        }
        drawLine(p.cascoLuz, Offset(n.left, n.bottom + 0.9f * u), Offset(n.right, n.bottom + 0.9f * u), 0.35f * u)

        // Abajo: la placa de cada bahía con sus dos tornillos, las rejillas y las patas.
        val py = n.bottom + (arm.bottom - n.bottom) * 0.42f
        val ph = (arm.bottom - n.bottom) * 0.32f
        for (i in 0 until 5) {
            val cx = n.left + cw * (i + 0.5f)
            val pl = Rect(cx - cw * 0.26f, py - ph / 2, cx + cw * 0.26f, py + ph / 2)
            pieza(pl, if (oscuro) mez(METAL, NEGRO, 0.55f) else METAL, if (oscuro) mez(METAL_LUZ, NEGRO, 0.5f) else METAL_LUZ, METAL_OSC, 0.25f * u, 0.4f * u)
            listOf(pl.left + 1.1f * u, pl.right - 1.1f * u).forEach { x -> drawCircle(METAL_OSC, 0.35f * u, Offset(x, pl.center.y)) }
            drawLine(e.acento.copy(alpha = 0.8f), Offset(pl.left + 2.4f * u, pl.center.y), Offset(pl.left + 2.4f * u + pl.width * 0.25f, pl.center.y), 0.4f * u, StrokeCap.Round)
        }
        ranuras(Rect(arm.left + 2f * u, arm.bottom - (arm.bottom - n.bottom) * 0.62f, n.left - 0.4f * u, arm.bottom - 1.8f * u), 3, 0.45f * u, NEGRO, p.cascoLuz)
        ranuras(Rect(n.right + 0.4f * u, arm.bottom - (arm.bottom - n.bottom) * 0.62f, arm.right - 2f * u, arm.bottom - 1.8f * u), 3, 0.45f * u, NEGRO, p.cascoLuz)
        listOf(w * 0.08f, w * 0.92f).forEach { x ->
            pieza(Rect(x - 3.4f * u, arm.bottom - 0.4f * u, x + 3.4f * u, piso + 0.2f * u), METAL_OSC, METAL, NEGRO, 0.3f * u, 0.6f * u)
        }
    }

    // ---------------------------------------------------------------- Íconos de los mapas

    override fun DrawScope.iconoMapa(id: String, e: EstadoIcono, oscuro: Boolean) {
        if (size.minDimension < 1f) return
        val k = minOf(size.height / 70f, size.width / 160f)
        val c = Offset(size.width / 2, size.height / 2)
        when (id) {
            "unesco" -> estacion(c, k, e)
            "rascar" -> cartaRascar(c, k, e)
            else -> balizas(c, k, e)
        }
    }

    /** Un ala solar: el marco dorado y las celdas azules con su cuadrícula y su brillo. */
    private fun DrawScope.alaSolar(r: Rect, k: Float) {
        drawRect(NEGRO.copy(alpha = 0.3f), Offset(r.left + 0.8f * k, r.top + 1f * k), r.size)
        drawRect(ORO_OSC, r.topLeft, r.size)
        val c = Rect(r.left + 0.6f * k, r.top + 0.6f * k, r.right - 0.6f * k, r.bottom - 0.6f * k)
        drawRect(Brush.linearGradient(listOf(CELDA_LUZ, CELDA_SOLAR, Color(0xFF16245A)), c.topLeft, c.bottomRight), c.topLeft, c.size)
        val nx = 6; val ny = 3
        for (i in 1 until nx) drawLine(ORO.copy(alpha = 0.55f), Offset(c.left + c.width * i / nx, c.top), Offset(c.left + c.width * i / nx, c.bottom), 0.35f * k)
        for (j in 1 until ny) drawLine(ORO.copy(alpha = 0.4f), Offset(c.left, c.top + c.height * j / ny), Offset(c.right, c.top + c.height * j / ny), 0.3f * k)
        drawLine(Color.White.copy(alpha = 0.35f), Offset(c.left + c.width * 0.1f, c.top + 0.6f * k), Offset(c.left + c.width * 0.45f, c.top + 0.6f * k), 0.5f * k)
    }

    /** Un módulo: el cilindro blanco con su luz y su sombra y los anillos de unión. */
    private fun DrawScope.modulo(r: Rect, k: Float, vertical: Boolean) {
        val br = if (vertical) Brush.horizontalGradient(listOf(Color(0xFFFFFFFF), Color(0xFFDCE1E8), Color(0xFF9AA3B2)), r.left, r.right)
        else Brush.verticalGradient(listOf(Color(0xFFFFFFFF), Color(0xFFDCE1E8), Color(0xFF9AA3B2)), r.top, r.bottom)
        drawRoundRect(NEGRO.copy(alpha = 0.3f), Offset(r.left + 0.8f * k, r.top + 1f * k), r.size, CornerRadius(1.6f * k))
        drawRoundRect(br, r.topLeft, r.size, CornerRadius(1.6f * k))
        drawRoundRect(Color(0xFF7D8696), r.topLeft, r.size, CornerRadius(1.6f * k), style = Stroke(0.4f * k))
        for (f in listOf(0.3f, 0.7f)) {
            if (vertical) drawLine(Color(0xFF8C95A5), Offset(r.left, r.top + r.height * f), Offset(r.right, r.top + r.height * f), 0.45f * k)
            else drawLine(Color(0xFF8C95A5), Offset(r.left + r.width * f, r.top), Offset(r.left + r.width * f, r.bottom), 0.45f * k)
        }
    }

    /** La estación orbital: la armadura, cuatro alas solares, los módulos en cruz, los radiadores y la cápsula atracada. */
    private fun DrawScope.estacion(c: Offset, k: Float, e: EstadoIcono) {
        for (j in 0 until 7) {
            val o = Offset(c.x + (azar(41, j) - 0.5f) * 140f * k, c.y + (azar(42, j) - 0.5f) * 60f * k)
            val r = (0.6f + azar(43, j)) * k
            drawLine(e.c1.copy(alpha = 0.6f), Offset(o.x - r * 2, o.y), Offset(o.x + r * 2, o.y), 0.4f * k)
            drawLine(e.c1.copy(alpha = 0.6f), Offset(o.x, o.y - r * 2), Offset(o.x, o.y + r * 2), 0.4f * k)
        }
        // La armadura.
        val ar = Rect(c.x - 52f * k, c.y - 2.2f * k, c.x + 52f * k, c.y + 2.2f * k)
        drawRect(NEGRO.copy(alpha = 0.3f), Offset(ar.left + 0.6f * k, ar.top + 1f * k), ar.size)
        drawRect(Color(0xFFB9C1CE), ar.topLeft, ar.size)
        var x = ar.left; var i = 0
        while (x < ar.right - 0.1f && i < 40) {
            drawLine(Color(0xFF6E788C), Offset(x, ar.top), Offset(x + 2.6f * k, ar.bottom), 0.4f * k)
            drawLine(Color(0xFF6E788C), Offset(x, ar.bottom), Offset(x + 2.6f * k, ar.top), 0.4f * k); x += 2.6f * k; i++
        }
        drawRect(Color(0xFF6E788C), ar.topLeft, ar.size, style = Stroke(0.45f * k))
        // Las alas solares en sus mástiles.
        listOf(-44f, -30f, 30f, 44f).forEach { dx ->
            val mx = c.x + dx * k
            drawLine(Color(0xFF8C95A5), Offset(mx, c.y - 26f * k), Offset(mx, c.y + 26f * k), 0.7f * k)
            alaSolar(Rect(mx - 5.5f * k, c.y - 27f * k, mx + 5.5f * k, c.y - 4f * k), k)
            alaSolar(Rect(mx - 5.5f * k, c.y + 4f * k, mx + 5.5f * k, c.y + 27f * k), k)
        }
        // Los radiadores, blancos y acanalados.
        listOf(-16f, 16f).forEach { dx ->
            val r = Rect(c.x + dx * k - 3f * k, c.y + 4f * k, c.x + dx * k + 3f * k, c.y + 15f * k)
            drawRect(Color(0xFFEEF1F5), r.topLeft, r.size)
            for (j in 1 until 4) drawLine(Color(0xFFB5BCC8), Offset(r.left, r.top + r.height * j / 4), Offset(r.right, r.top + r.height * j / 4), 0.35f * k)
            drawRect(Color(0xFF8C95A5), r.topLeft, r.size, style = Stroke(0.35f * k))
        }
        // Los módulos: el largo vertical, el nodo y los dos laterales.
        modulo(Rect(c.x - 4f * k, c.y - 24f * k, c.x + 4f * k, c.y + 22f * k), k, true)
        modulo(Rect(c.x - 12f * k, c.y - 13f * k, c.x + 12f * k, c.y - 6f * k), k, false)
        drawCircle(Brush.radialGradient(listOf(Color.White, Color(0xFFB9C1CE)), Offset(c.x - 1.5f * k, c.y - 1.5f * k), 6f * k), 4.6f * k, c)
        drawCircle(Color(0xFF7D8696), 4.6f * k, c, style = Stroke(0.4f * k))
        // La cápsula atracada arriba, con su escudo y sus luces de posición.
        val cap = Path().apply { moveTo(c.x - 3.4f * k, c.y - 24f * k); lineTo(c.x - 2f * k, c.y - 30f * k); lineTo(c.x + 2f * k, c.y - 30f * k); lineTo(c.x + 3.4f * k, c.y - 24f * k); close() }
        drawPath(cap, Brush.horizontalGradient(listOf(Color(0xFFF2F4F8), Color(0xFF8C95A5)), c.x - 3.4f * k, c.x + 3.4f * k))
        drawLine(e.c2, Offset(c.x - 2.2f * k, c.y - 27f * k), Offset(c.x + 2.2f * k, c.y - 27f * k), 0.8f * k)
        halo(Offset(c.x - 52f * k, c.y), 4f * k, ROJO.copy(alpha = 0.5f)); drawCircle(ROJO, 1f * k, Offset(c.x - 52f * k, c.y))
        halo(Offset(c.x + 52f * k, c.y), 4f * k, VERDE.copy(alpha = 0.5f)); drawCircle(VERDE, 1f * k, Offset(c.x + 52f * k, c.y))
    }

    /** La pantalla de la carta estelar: la mitad ya rascada (estrellas y nebulosa), la capa plateada y la medalla que la raspa. */
    private fun DrawScope.cartaRascar(c: Offset, k: Float, e: EstadoIcono) {
        val fr = Rect(c.x - 36f * k, c.y - 26f * k, c.x + 36f * k, c.y + 24f * k)
        drawRoundRect(NEGRO.copy(alpha = 0.35f), Offset(fr.left + 1.2f * k, fr.top + 1.8f * k), fr.size, CornerRadius(4f * k))
        drawRoundRect(Brush.verticalGradient(listOf(Color(0xFF52607E), Color(0xFF34405A), Color(0xFF1E2638)), fr.top, fr.bottom), fr.topLeft, fr.size, CornerRadius(4f * k))
        val pa = Rect(fr.left + 4f * k, fr.top + 4f * k, fr.right - 4f * k, fr.bottom - 6f * k)
        drawRoundRect(NEGRO, Offset(pa.left - 0.8f * k, pa.top - 0.8f * k), Size(pa.width + 1.6f * k, pa.height + 1.6f * k), CornerRadius(1.6f * k))
        clipRect(pa.left, pa.top, pa.right, pa.bottom) {
            drawRect(Brush.radialGradient(listOf(VACIO_AZUL, VACIO), pa.center, pa.width * 0.7f), pa.topLeft, pa.size)
            halo(Offset(pa.left + pa.width * 0.3f, pa.center.y), pa.height * 0.6f, NEBULOSA.copy(alpha = 0.6f))
            estrellas(pa, 77, 26, 0.9f * k)
            // Una constelación con sus trazos.
            val cs = listOf(Offset(0.12f, 0.3f), Offset(0.25f, 0.55f), Offset(0.38f, 0.4f), Offset(0.5f, 0.68f)).map { Offset(pa.left + pa.width * it.x, pa.top + pa.height * it.y) }
            for (j in 0 until cs.size - 1) drawLine(e.c1.copy(alpha = 0.7f), cs[j], cs[j + 1], 0.45f * k)
            cs.forEach { drawCircle(ESTRELLA, 0.9f * k, it) }
            // La capa plateada que aún cubre la derecha, con su borde mordido y las rayas del rascado.
            val capa = Path().apply {
                moveTo(pa.right, pa.top); lineTo(pa.left + pa.width * 0.55f, pa.top)
                for (j in 0..8) lineTo(pa.left + pa.width * (0.55f + 0.06f * azar(5, j) - 0.03f * (j % 2)), pa.top + pa.height * j / 8f)
                lineTo(pa.right, pa.bottom); close()
            }
            drawPath(capa, Brush.linearGradient(listOf(Color(0xFFE2E6EC), Color(0xFFAEB5C0), Color(0xFFD0D5DC)), Offset(pa.left + pa.width * 0.55f, pa.top), pa.bottomRight))
            clipPath(capa) { for (j in 0 until 10) drawLine(Color.White.copy(alpha = 0.45f), Offset(pa.left + pa.width * (0.5f + j * 0.06f), pa.bottom), Offset(pa.left + pa.width * (0.62f + j * 0.06f), pa.top), 0.35f * k) }
        }
        // Los pilotos del marco.
        piloto(Offset(fr.left + 6f * k, fr.bottom - 3f * k), 0.9f * k, VERDE, true)
        drawLine(Color(0xFF8C95A5), Offset(fr.left + 10f * k, fr.bottom - 3f * k), Offset(fr.left + 22f * k, fr.bottom - 3f * k), 0.8f * k, StrokeCap.Round)
        // La medalla de la misión: oro con su canto, el anillo del acento y un cohete en relieve.
        val m = Offset(fr.right + 2f * k, fr.bottom - 4f * k); val mr = 12f * k
        drawCircle(NEGRO.copy(alpha = 0.35f), mr, Offset(m.x + 1.2f * k, m.y + 1.8f * k))
        drawCircle(Brush.radialGradient(listOf(ORO_LUZ, ORO, ORO_OSC), Offset(m.x - mr * 0.4f, m.y - mr * 0.4f), mr * 1.6f), mr, m)
        drawCircle(ORO_OSC, mr * 0.8f, m, style = Stroke(0.6f * k))
        drawCircle(e.c1, mr * 0.68f, m)
        val cohete = Path().apply {
            moveTo(m.x, m.y - mr * 0.5f); quadraticTo(m.x + mr * 0.2f, m.y - mr * 0.2f, m.x + mr * 0.16f, m.y + mr * 0.25f)
            lineTo(m.x + mr * 0.32f, m.y + mr * 0.42f); lineTo(m.x - mr * 0.32f, m.y + mr * 0.42f); lineTo(m.x - mr * 0.16f, m.y + mr * 0.25f)
            quadraticTo(m.x - mr * 0.2f, m.y - mr * 0.2f, m.x, m.y - mr * 0.5f); close()
        }
        drawPath(cohete, ORO_LUZ)
        drawCircle(e.c1, mr * 0.07f, Offset(m.x, m.y - mr * 0.1f))
        drawArc(Color.White.copy(alpha = 0.5f), 200f, 60f, false, Offset(m.x - mr * 0.9f, m.y - mr * 0.9f), Size(mr * 1.8f, mr * 1.8f), style = Stroke(0.8f * k, cap = StrokeCap.Round))
    }

    /** Tres balizas en la Luna: el suelo con cráteres, la Tierra en el cielo, la trayectoria y las balizas encendidas. */
    private fun DrawScope.balizas(c: Offset, k: Float, e: EstadoIcono) {
        // La Tierra lejana.
        val tc = Offset(c.x + 40f * k, c.y - 20f * k)
        halo(tc, 9f * k, ATMOSFERA.copy(alpha = 0.4f))
        drawCircle(Brush.radialGradient(listOf(MAR_LUZ, MAR, MAR_OSC), Offset(tc.x - 2f * k, tc.y - 2f * k), 7f * k), 5.2f * k, tc)
        drawOval(TIERRA, Offset(tc.x - 3f * k, tc.y - 2f * k), Size(3.4f * k, 2.4f * k))
        drawCircle(VACIO.copy(alpha = 0.45f), 5.2f * k, Offset(tc.x + 2.6f * k, tc.y + 1f * k))
        // El suelo lunar, en curva, con sus cráteres.
        val suelo = Path().apply {
            moveTo(c.x - 62f * k, c.y + 26f * k); quadraticTo(c.x, c.y - 2f * k, c.x + 62f * k, c.y + 26f * k); lineTo(c.x + 62f * k, c.y + 30f * k); lineTo(c.x - 62f * k, c.y + 30f * k); close()
        }
        drawPath(suelo, Brush.verticalGradient(listOf(Color(0xFFD5D7DC), Color(0xFF9EA3AC), Color(0xFF6E737C)), c.y + 8f * k, c.y + 30f * k))
        clipPath(suelo) {
            listOf(Triple(-36f, 22f, 5f), Triple(-8f, 17f, 3.4f), Triple(18f, 21f, 4.4f), Triple(42f, 25f, 3f), Triple(-50f, 27f, 2.6f)).forEach { (dx, dy, r) ->
                drawOval(Color(0xFF7D828B), Offset(c.x + dx * k - r * k, c.y + dy * k - r * 0.35f * k), Size(r * 2 * k, r * 0.7f * k))
                drawArc(Color.White.copy(alpha = 0.5f), 0f, 180f, false, Offset(c.x + dx * k - r * k, c.y + dy * k - r * 0.35f * k), Size(r * 2 * k, r * 0.7f * k), style = Stroke(0.4f * k))
            }
        }
        // La trayectoria punteada entre las balizas.
        val bs = listOf(Offset(c.x - 38f * k, c.y + 15f * k), Offset(c.x - 2f * k, c.y + 9f * k), Offset(c.x + 34f * k, c.y + 15f * k))
        drawPath(Path().apply {
            moveTo(bs[0].x, bs[0].y - 12f * k); quadraticTo((bs[0].x + bs[1].x) / 2, bs[0].y - 30f * k, bs[1].x, bs[1].y - 12f * k)
            quadraticTo((bs[1].x + bs[2].x) / 2, bs[1].y - 26f * k, bs[2].x, bs[2].y - 12f * k)
        }, e.tinta.copy(alpha = 0.55f), style = Stroke(0.8f * k, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2f * k, 1.6f * k))))
        // Las balizas: tres patas, el cuerpo de lámina dorada, la antena y la luz.
        listOf(e.c1, e.c2, VERDE).forEachIndexed { j, col ->
            val b = bs[j]
            drawOval(NEGRO.copy(alpha = 0.3f), Offset(b.x - 6f * k, b.y - 0.6f * k), Size(12f * k, 2.2f * k))
            listOf(-5f, 0f, 5f).forEach { lx -> drawLine(Color(0xFF8C95A5), Offset(b.x + lx * 0.4f * k, b.y - 4f * k), Offset(b.x + lx * k, b.y), 0.6f * k) }
            val cu = Rect(b.x - 3.6f * k, b.y - 8f * k, b.x + 3.6f * k, b.y - 3.6f * k)
            drawRect(Brush.linearGradient(listOf(ORO_LUZ, ORO, ORO_OSC), cu.topLeft, cu.bottomRight), cu.topLeft, cu.size)
            drawLine(ORO_OSC, Offset(cu.left, cu.center.y), Offset(cu.right, cu.center.y), 0.3f * k)
            drawLine(Color(0xFFB9C1CE), Offset(b.x, cu.top), Offset(b.x, cu.top - 4.5f * k), 0.5f * k)
            val lz = Offset(b.x, cu.top - 5.2f * k)
            halo(lz, 6.5f * k, col.copy(alpha = 0.45f))
            drawCircle(col, 1.5f * k, lz)
            drawCircle(Color.White.copy(alpha = 0.7f), 0.5f * k, Offset(lz.x - 0.4f * k, lz.y - 0.4f * k))
        }
    }

    // ---------------------------------------------------------------- Notas: el tablero de velcro de la tripulación

    override fun DrawScope.tableroNotas(e: EstadoMueble, oscuro: Boolean) {
        if (size.minDimension < 1f) return
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val p = pal(oscuro)
        val n = nicho(MuebleVida.NOTAS)
        pared(p, u)

        // La lámpara de arriba, colgada de sus dos soportes.
        val lam = Rect(w * 0.3f, h * 0.018f, w * 0.7f, h * 0.06f)
        listOf(lam.left + 2f * u, lam.right - 2f * u).forEach { x -> drawRect(METAL_OSC, Offset(x - 0.5f * u, 0f), Size(1f * u, lam.top + 0.5f * u)) }
        lampara(lam, p, e, u)

        // El marco con sus corchetes y sus tornillos, y el tablero de velcro (el nicho).
        val fr = marco(n, 2.4f * u, p, e, u)
        drawRoundRect(p.nicho, n.topLeft, n.size, CornerRadius(0.6f * u))
        clipRect(n.left, n.top, n.right, n.bottom) {
            // La trama del velcro: hileras de puntitos alternados, muy tenues.
            val pts = ArrayList<Offset>()
            var y = n.top + 0.8f * u; var fila = 0
            while (y < n.bottom && fila < 400) {
                var x = n.left + (if (fila % 2 == 0) 0.4f else 1.2f) * u
                while (x < n.right) { pts.add(Offset(x, y)); x += 1.6f * u }
                y += 1.4f * u; fila++
            }
            drawPoints(pts, PointMode.Points, p.nichoLinea, 0.45f * u, StrokeCap.Round)
            // Las costuras de las tiras.
            var sx = n.left + n.width / 3
            while (sx < n.right - 1f) { drawLine(p.nichoLinea, Offset(sx, n.top), Offset(sx, n.bottom), 0.3f * u); sx += n.width / 3 }
            drawRect(Brush.verticalGradient(listOf(NEGRO.copy(alpha = if (oscuro) 0.4f else 0.1f), Color.Transparent), n.top, n.top + 2.4f * u), n.topLeft, Size(n.width, 2.4f * u))
        }
        listOf(fr.topLeft, Offset(fr.right, fr.top), Offset(fr.left, fr.bottom), fr.bottomRight).forEach { c ->
            tornillo(Offset(c.x + (if (c.x < w / 2) 1.2f else -1.2f) * u, c.y + (if (c.y < h / 2) 1.2f else -1.2f) * u), 0.6f * u, oscuro)
        }
        listOf(fr.center.x).forEach { x ->
            tornillo(Offset(x, fr.top + 1.2f * u), 0.6f * u, oscuro)
            tornillo(Offset(x, fr.bottom - 1.2f * u), 0.6f * u, oscuro)
        }

        // Los dos pasamanos a los costados.
        listOf(fr.left * 0.5f, w - fr.left * 0.5f).forEach { x -> pasamanos(Offset(x, h * 0.22f), Offset(x, h * 0.78f), minOf(1.1f * u, fr.left * 0.26f), e.secundario) }

        // La repisa con su franja de aviso, y encima la cinta americana, el rotulador atado y la linterna.
        val rep = Rect(w * 0.03f, h * 0.905f, w * 0.97f, h * 0.94f)
        listOf(w * 0.15f, w * 0.85f).forEach { x ->
            drawPath(Path().apply { moveTo(x - 1.2f * u, rep.bottom); lineTo(x + 1.2f * u, rep.bottom); lineTo(x + 1.2f * u, h); lineTo(x - 1.2f * u, h * 0.975f); close() }, p.cascoOsc)
        }
        drawRect(NEGRO.copy(alpha = 0.3f), Offset(rep.left, rep.bottom), Size(rep.width, 1.2f * u))
        pieza(rep, p.casco, p.cascoLuz, p.cascoOsc, 0.35f * u, 0.4f * u)
        franjas(Rect(w * 0.4f, rep.top + rep.height * 0.28f, w * 0.6f, rep.bottom - rep.height * 0.28f), mez(e.secundario, NEGRO, if (oscuro) 0.2f else 0f), 2.4f * u)
        // La cinta americana, de canto: el rollo gris con su hueco de cartón.
        val tc = Offset(w * 0.17f, rep.top - 3f * u); val tr = 3f * u
        drawCircle(NEGRO.copy(alpha = 0.3f), tr, Offset(tc.x + 0.4f * u, tc.y + 0.6f * u))
        drawCircle(Brush.radialGradient(listOf(Color(0xFFC9CED6), CINTA, Color(0xFF6E747E)), Offset(tc.x - tr * 0.4f, tc.y - tr * 0.4f), tr * 1.5f), tr, tc)
        drawCircle(Color(0xFFB08A5A), tr * 0.58f, tc)
        drawCircle(p.pared, tr * 0.46f, tc)
        drawCircle(Color.White.copy(alpha = 0.25f), tr * 0.8f, tc, style = Stroke(0.25f * u))
        // El rotulador atado: el cuerpo, la tapa del acento y el cordón en espiral que cuelga por delante.
        val rx = w * 0.27f; val ry = rep.top - 0.9f * u
        drawRoundRect(NEGRO.copy(alpha = 0.3f), Offset(rx + 0.3f * u, ry - 0.4f * u), Size(w * 0.11f, 1.8f * u), CornerRadius(0.9f * u))
        drawRoundRect(Brush.verticalGradient(listOf(Color.White, Color(0xFFC9CED6)), ry - 0.9f * u, ry + 0.9f * u), Offset(rx, ry - 0.9f * u), Size(w * 0.11f, 1.8f * u), CornerRadius(0.9f * u))
        drawRoundRect(e.acento, Offset(rx + w * 0.075f, ry - 0.9f * u), Size(w * 0.035f, 1.8f * u), CornerRadius(0.9f * u))
        val cordon = Path().apply {
            moveTo(rx, ry)
            var cx = rx
            for (j in 0 until 6) { cubicTo(cx - 1f * u, ry + 2f * u, cx - 1.6f * u, rep.bottom + 0.5f * u, cx - 1.2f * u, rep.bottom + 1.8f * u); cx -= 1.2f * u }
        }
        drawPath(cordon, Color(0xFF3A3F4A), style = Stroke(0.35f * u))
        // La linterna, acostada, con su anillo y su lente encendida.
        val lx = w * 0.72f; val ly = rep.top - 1.3f * u
        drawRoundRect(NEGRO.copy(alpha = 0.3f), Offset(lx + 0.3f * u, ly - 0.8f * u), Size(w * 0.1f, 2.6f * u), CornerRadius(0.8f * u))
        drawRoundRect(Brush.verticalGradient(listOf(Color(0xFF5A6070), Color(0xFF2A2E38)), ly - 1.3f * u, ly + 1.3f * u), Offset(lx, ly - 1.3f * u), Size(w * 0.1f, 2.6f * u), CornerRadius(0.8f * u))
        drawRect(METAL, Offset(lx + w * 0.07f, ly - 1.5f * u), Size(w * 0.03f, 3f * u))
        if (oscuro) halo(Offset(lx + w * 0.1f, ly), 5f * u, Color(0xFFFFF2C8).copy(alpha = 0.35f))
        drawOval(Color(0xFFFFF4D2), Offset(lx + w * 0.1f - 0.5f * u, ly - 1.4f * u), Size(1f * u, 2.8f * u))
        piloto(Offset(w * 0.93f, rep.center.y), 0.55f * u, VERDE, true)
    }

    /** El color de la franja y del signo de cada tipo de nota. */
    private fun colorTipo(tipo: String): Color = when (tipo) {
        "idea" -> Color(0xFF1F8BB0)
        "pensamiento" -> Color(0xFF6B4FD6)
        "proposito" -> Color(0xFF2E8A5A)
        "recordatorio" -> Color(0xFFD5581C)
        else -> Color(0xFF5A6578)
    }

    override fun DrawScope.notaTablero(e: EstadoNota, oscuro: Boolean) {
        val r = e.rect
        if (r.width < 1f || r.height < 1f) return
        val k = minOf(r.width, r.height) / 100f
        val f = Rect(r.left + 2f * k, r.top + 3f * k, r.right - 2.4f * k, r.bottom - 2.6f * k)
        val rad = CornerRadius(4f * k)
        val papel = if (oscuro) FICHA_NOCHE else FICHA
        val col = colorTipo(e.tipo)
        if (e.fijada) drawRoundRect(e.acento.copy(alpha = 0.4f), Offset(f.left - 1.2f * k, f.top - 1.2f * k), Size(f.width + 2.4f * k, f.height + 2.4f * k), CornerRadius(5f * k), style = Stroke(1.6f * k))
        drawRoundRect(NEGRO.copy(alpha = 0.32f), Offset(f.left + 1f * k, f.top + 2f * k), f.size, rad)
        val forma = Path().apply { addRoundRect(RoundRect(f, rad)) }
        drawPath(forma, papel)
        clipPath(forma) {
            // La franja del tipo, el brillo de la funda plastificada y la sombra del pie.
            val fh = f.height * 0.1f
            drawRect(col, f.topLeft, Size(f.width, fh))
            drawLine(mez(col, NEGRO, 0.3f), Offset(f.left, f.top + fh), Offset(f.right, f.top + fh), 0.5f * k)
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, NEGRO.copy(alpha = 0.06f)), f.top, f.bottom), f.topLeft, f.size)
            drawPath(Path().apply {
                moveTo(f.left, f.top + f.height * 0.55f); lineTo(f.left + f.width * 0.55f, f.top); lineTo(f.left + f.width * 0.68f, f.top); lineTo(f.left, f.top + f.height * 0.75f); close()
            }, Color.White.copy(alpha = 0.16f))
            // Al pie, el código de barras de la ficha.
            var bx = f.left + 4f * k; var j = 0
            while (bx < f.left + f.width * 0.45f && j < 40) {
                val bw = (0.4f + 0.9f * azar(e.indice + 50, j)) * k
                drawRect(TINTA.copy(alpha = 0.5f), Offset(bx, f.bottom - 8f * k), Size(bw, 4.4f * k)); bx += bw + 0.7f * k; j++
            }
        }
        drawRoundRect(mez(papel, NEGRO, 0.25f), f.topLeft, f.size, rad, style = Stroke(0.5f * k))
        // Los dos ojales de arriba.
        listOf(f.left + 5f * k, f.right - 5f * k).forEach { x ->
            val o = Offset(x, f.top + f.height * 0.05f)
            drawCircle(Color(0xFFB9C1CE), 1.8f * k, o)
            drawCircle(mez(col, NEGRO, 0.45f), 1f * k, o)
        }
        // El signo del tipo, abajo a la derecha.
        val g = Offset(f.right - 7f * k, f.bottom - 6.4f * k); val gr = 3.4f * k
        when (e.tipo) {
            "idea" -> drawPath(Path().apply {
                moveTo(g.x, g.y - gr); quadraticTo(g.x, g.y, g.x + gr, g.y); quadraticTo(g.x, g.y, g.x, g.y + gr)
                quadraticTo(g.x, g.y, g.x - gr, g.y); quadraticTo(g.x, g.y, g.x, g.y - gr); close()
            }, col)
            "pensamiento" -> {
                drawCircle(col, gr * 0.55f, g)
                drawOval(col, Offset(g.x - gr * 1.1f, g.y - gr * 0.3f), Size(gr * 2.2f, gr * 0.6f), style = Stroke(0.6f * k))
            }
            "proposito" -> {
                val cohete = Path().apply {
                    moveTo(g.x, g.y - gr * 1.1f); quadraticTo(g.x + gr * 0.5f, g.y - gr * 0.4f, g.x + gr * 0.4f, g.y + gr * 0.4f)
                    lineTo(g.x + gr * 0.8f, g.y + gr * 0.9f); lineTo(g.x - gr * 0.8f, g.y + gr * 0.9f); lineTo(g.x - gr * 0.4f, g.y + gr * 0.4f)
                    quadraticTo(g.x - gr * 0.5f, g.y - gr * 0.4f, g.x, g.y - gr * 1.1f); close()
                }
                drawPath(cohete, col)
                drawCircle(papel, gr * 0.2f, Offset(g.x, g.y - gr * 0.2f))
            }
            "recordatorio" -> {
                drawCircle(col, gr, g, style = Stroke(0.7f * k))
                drawLine(col, g, Offset(g.x, g.y - gr * 0.65f), 0.7f * k, StrokeCap.Round)
                drawLine(col, g, Offset(g.x + gr * 0.5f, g.y + gr * 0.2f), 0.7f * k, StrokeCap.Round)
            }
            else -> drawLine(col, Offset(g.x - gr, g.y), Offset(g.x + gr, g.y), 0.7f * k, StrokeCap.Round)
        }
        // Lo que la sujeta: la tira de velcro del color del tipo, o el parche de la misión si está fijada.
        val sx = r.center.x; val sy = f.top + 0.6f * k
        if (e.fijada) {
            val pr = 6.4f * k
            val pc = Offset(sx, sy + 1.6f * k)
            drawCircle(NEGRO.copy(alpha = 0.35f), pr, Offset(pc.x + 0.8f * k, pc.y + 1.2f * k))
            drawCircle(e.acento, pr, pc)
            drawCircle(Color(0xFF101A38), pr * 0.78f, pc)
            drawCircle(Color.White.copy(alpha = 0.35f), pr * 0.89f, pc, style = Stroke(0.4f * k, pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.6f * k, 0.5f * k))))
            drawOval(e.acento.copy(alpha = 0.9f), Offset(pc.x - pr * 0.6f, pc.y - pr * 0.18f), Size(pr * 1.2f, pr * 0.36f), style = Stroke(0.45f * k))
            drawCircle(Color(0xFFF0C08A), pr * 0.24f, pc)
            drawCircle(Color.White, pr * 0.07f, Offset(pc.x - pr * 0.45f, pc.y - pr * 0.4f))
        } else {
            val v = Rect(sx - 7f * k, sy - 2.2f * k, sx + 7f * k, sy + 3.4f * k)
            drawRoundRect(NEGRO.copy(alpha = 0.3f), Offset(v.left + 0.5f * k, v.top + 0.9f * k), v.size, CornerRadius(1f * k))
            drawRoundRect(mez(col, Color.White, 0.15f), v.topLeft, v.size, CornerRadius(1f * k))
            val pts = ArrayList<Offset>()
            var vx = v.left + 1f * k
            while (vx < v.right - 0.5f * k) { pts.add(Offset(vx, v.top + 1.4f * k)); pts.add(Offset(vx + 0.7f * k, v.bottom - 1.4f * k)); vx += 1.4f * k }
            drawPoints(pts, PointMode.Points, NEGRO.copy(alpha = 0.3f), 0.5f * k, StrokeCap.Round)
            drawLine(Color.White.copy(alpha = 0.4f), Offset(v.left + 1f * k, v.top + 0.5f * k), Offset(v.right - 1f * k, v.top + 0.5f * k), 0.4f * k)
        }
    }

    override fun tintaNota(oscuro: Boolean): Color = TINTA
}
