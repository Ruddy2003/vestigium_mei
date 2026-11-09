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
import java.util.Random
import kotlin.math.cos
import kotlin.math.sin

/**
 * Vapor victoriano: Vida en el club de los ingenieros y en el gabinete de patentes, con la caoba, el latón, el cobre, el
 * ladrillo, el cuero verde y la luz del gas de alumbrado de su Ideario, sus estantes y su salón de logros.
 * - Social: el comedor del club, en la vista de los juegos de mesa (la pared de frente, el piso desde arriba). La pared de
 *   ladrillo con su tubería de cobre, el reloj-manómetro entre engranes, dos apliques de gas (encendidos de noche, con su
 *   halo) y la válvula que suelta vapor; el arrimadero de caoba; el parqué, la alfombra con su orla dorada y sus flecos, la
 *   mesa de caoba con su filete de latón y el mantel de lino (el nicho); las sillas de respaldo de globo con su asiento de
 *   cuero capitoneado, dos butacas Chesterfield en las cabeceras, dos mesitas con el servicio de té y dos palmeras en sus
 *   maceteros de latón.
 * - Mapas: el escritorio del cartógrafo. El tablero de caoba con su veta, sus cantoneras y su filete de latón, el plano
 *   clavado con chinchetas (el nicho), el tintero con la pluma, el compás, la lámpara de pantalla verde, el reloj de
 *   bolsillo con su cadena, la lupa y la regla de boj; el canto moldurado con sus clavos, el faldón con dos cajones de asa
 *   y bocallave y las patas torneadas con sus ruedas de latón.
 * - Otras vidas: la vitrina del gabinete de curiosidades. Cornisa de caoba con dentículos, el reloj-manómetro entre engranes
 *   y la tubería, cinco urnas de arco rebajado con su moldura de latón y el brillo del vidrio, forradas de seda (el nicho,
 *   partido en cinco por varillas de latón), pilastras con capitel y basa de latón y la cajonera con sus pomos y sus patas.
 * - Íconos: la estación de hierro y vidrio con su torre del reloj, la placa de cobre que una moneda dentada rasca, las
 *   chinchetas de latón a lo largo de la vía.
 * - Notas: el tablón de avisos de la estación. Marco de caoba con su filete de latón, el frontón con el reloj de la estación
 *   entre volutas, dos faroles de gas y el corcho (el nicho); las notas son papeles de su tipo (telegrama, carta, billete,
 *   etiqueta de equipaje) clavados con una chincheta de latón; las fijadas, con chincheta roja y su sello de lacre.
 */
internal object VidaVapor : DisenoVida {
    private val LATON = Color(0xFFC9A04E)
    private val LATON_LUZ = Color(0xFFE8CC84)
    private val LATON_OSC = Color(0xFF8A6A2C)
    private val COBRE = Color(0xFFB06A3B)
    private val COBRE_LUZ = Color(0xFFD99464)
    private val COBRE_OSC = Color(0xFF7A4224)
    private val CAOBA = Color(0xFF5E2C1B)
    private val CAOBA_LUZ = Color(0xFF834127)
    private val CAOBA_OSC = Color(0xFF2E140C)
    private val HIERRO = Color(0xFF2E3230)
    private val HIERRO_LUZ = Color(0xFF59605C)
    private val LADRILLO = Color(0xFF8A4430)
    private val JUNTA = Color(0xFF4C2A20)
    private val ROBLE = Color(0xFFA77A49)
    private val ROBLE_OSC = Color(0xFF6E4A28)
    private val CUERO = Color(0xFF2F5240)
    private val CUERO_MARRON = Color(0xFF8A5530)
    private val ALFOMBRA = Color(0xFF7A2420)
    private val CARA = Color(0xFFF1E8D0)
    private val ROJO = Color(0xFF9C3D24)
    private val GAS = Color(0xFFFFC870)
    private val LLAMA = Color(0xFFFFE9B0)
    private val VAPOR_B = Color(0xFFF2F0EA)
    private val PALMA = Color(0xFF3F6B3A)
    private val PALMA_LUZ = Color(0xFF6E9A55)
    private val CIANOTIPIA = Color(0xFF2C4F7C)
    private val CORCHO = Color(0xFFD9BE8E)
    private val CORCHO_NOCHE = Color(0xFF30261B)

    /** Los papeles de las notas, de día y de noche (a la luz del gas). */
    private val TELEGRAMA = Color(0xFFF6E7B8)
    private val CARTA = Color(0xFFF7F0DE)
    private val BILLETE = Color(0xFFE4EAD6)
    private val ETIQUETA = Color(0xFFEBD6A6)
    private val TELEGRAMA_N = Color(0xFF3A3222)
    private val CARTA_N = Color(0xFF35302A)
    private val BILLETE_N = Color(0xFF2E3329)
    private val ETIQUETA_N = Color(0xFF3B3020)

    // ---------------------------------------------------------------------
    //  Ayudas
    // ---------------------------------------------------------------------

    private fun DrawScope.u() = size.minDimension / 100f

    private fun mezcla(a: Color, b: Color, f: Float) = lerp(a, b, f.coerceIn(0f, 1f))

    /** El color en modo oscuro: la sala de máquinas a media luz, como en sus estantes. */
    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color(0xFF0D0B09), 0.38f) else c

    /** El latón y el cobre se hunden menos: siguen brillando a la luz del gas. */
    private fun tl(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color(0xFF0D0B09), 0.2f) else c

    private fun azar(semilla: Int, k: Int): Float {
        var x = semilla * 374761393 + k * 668265263
        x = (x xor (x ushr 13)) * 1274126177
        x = x xor (x ushr 16)
        return (x and 0x7fffffff) / 2147483647f
    }

    private fun DrawScope.nicho(m: MuebleVida): Rect {
        val a = area(m)
        return Rect(size.width * a.left, size.height * a.top, size.width * a.right, size.height * a.bottom)
    }

    /** Un engrane: dientes cuadrados alrededor de [c]. */
    private fun engrane(c: Offset, r: Float, dientes: Int): Path = Path().apply {
        val n = dientes * 4
        for (k in 0 until n) {
            val a = 2 * Math.PI * k / n
            val rr = if ((k / 2) % 2 == 0) r else r * 0.8f
            val p = Offset(c.x + rr * cos(a).toFloat(), c.y + rr * sin(a).toFloat())
            if (k == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
        }
        close()
    }

    /** Un engrane de latón con sus rayos, su cubo y su eje. */
    private fun DrawScope.engraneLaton(c: Offset, r: Float, dientes: Int, cuerpo: Color, oscuro: Boolean, u: Float) {
        drawPath(engrane(Offset(c.x + r * 0.08f, c.y + r * 0.1f), r, dientes), Color.Black.copy(alpha = 0.3f))
        drawPath(engrane(c, r, dientes), tl(cuerpo, oscuro))
        drawPath(engrane(c, r, dientes), tl(mezcla(cuerpo, Color.Black, 0.35f), oscuro), style = Stroke(0.25f * u))
        drawCircle(tl(mezcla(cuerpo, Color.Black, 0.3f), oscuro), r * 0.6f, c, style = Stroke(0.3f * u))
        for (k in 0 until 4) {
            val a = Math.toRadians(45.0 + 90.0 * k)
            drawLine(tl(mezcla(cuerpo, Color.Black, 0.3f), oscuro), c, Offset(c.x + r * 0.6f * cos(a).toFloat(), c.y + r * 0.6f * sin(a).toFloat()), 0.45f * u)
        }
        drawCircle(tl(LATON_LUZ, oscuro), r * 0.26f, c)
        drawCircle(t(HIERRO, oscuro), r * 0.11f, c)
    }

    /** El reloj-manómetro: bisel de latón, la esfera crema, las marcas y la aguja roja. */
    private fun DrawScope.manometro(c: Offset, r: Float, oscuro: Boolean, valor: Float = 0.62f, romano: Boolean = false) {
        drawCircle(Color.Black.copy(alpha = 0.35f), r, Offset(c.x + r * 0.08f, c.y + r * 0.12f))
        drawCircle(Brush.linearGradient(listOf(tl(LATON_LUZ, oscuro), tl(LATON, oscuro), tl(LATON_OSC, oscuro)), Offset(c.x - r, c.y - r), Offset(c.x + r, c.y + r)), r, c)
        drawCircle(tl(LATON_OSC, oscuro), r * 0.86f, c, style = Stroke(r * 0.04f))
        val cara = if (oscuro) mezcla(CARA, GAS, 0.18f).let { mezcla(it, Color.Black, 0.12f) } else CARA
        drawCircle(cara, r * 0.8f, c)
        val tinta = Color(0xFF231B14)
        if (romano) {
            for (k in 0 until 12) {
                val a = Math.toRadians(k * 30.0 - 90)
                val r0 = r * (if (k % 3 == 0) 0.56f else 0.64f)
                drawLine(tinta, Offset(c.x + r0 * cos(a).toFloat(), c.y + r0 * sin(a).toFloat()), Offset(c.x + r * 0.72f * cos(a).toFloat(), c.y + r * 0.72f * sin(a).toFloat()), r * (if (k % 3 == 0) 0.07f else 0.04f))
            }
            drawLine(tinta, c, Offset(c.x + r * 0.32f * cos(Math.toRadians(-150.0)).toFloat(), c.y + r * 0.32f * sin(Math.toRadians(-150.0)).toFloat()), r * 0.08f, StrokeCap.Round)
            drawLine(tinta, c, Offset(c.x + r * 0.6f * cos(Math.toRadians(-62.0)).toFloat(), c.y + r * 0.6f * sin(Math.toRadians(-62.0)).toFloat()), r * 0.05f, StrokeCap.Round)
        } else {
            for (k in 0..10) {
                val a = Math.toRadians(135.0 + k * 27.0)
                val r0 = r * (if (k % 5 == 0) 0.56f else 0.63f)
                drawLine(tinta.copy(alpha = 0.75f), Offset(c.x + r0 * cos(a).toFloat(), c.y + r0 * sin(a).toFloat()), Offset(c.x + r * 0.72f * cos(a).toFloat(), c.y + r * 0.72f * sin(a).toFloat()), r * (if (k % 5 == 0) 0.06f else 0.035f))
            }
            drawArc(ROJO.copy(alpha = 0.75f), 345f, 60f, false, Offset(c.x - r * 0.68f, c.y - r * 0.68f), Size(r * 1.36f, r * 1.36f), style = Stroke(r * 0.07f))
            val a = Math.toRadians(135.0 + 270.0 * valor)
            drawLine(ROJO, c, Offset(c.x + r * 0.62f * cos(a).toFloat(), c.y + r * 0.62f * sin(a).toFloat()), r * 0.06f, StrokeCap.Round)
        }
        drawCircle(tl(LATON_OSC, oscuro), r * 0.1f, c)
        // El brillo del cristal.
        drawArc(Color.White.copy(alpha = if (oscuro) 0.12f else 0.35f), 200f, 70f, false, Offset(c.x - r * 0.66f, c.y - r * 0.66f), Size(r * 1.32f, r * 1.32f), style = Stroke(r * 0.08f, cap = StrokeCap.Round))
    }

    private fun DrawScope.sombraSuave(r: Rect, radio: Float, alcance: Float, fuerza: Float = 0.22f) {
        for (k in 4 downTo 1) {
            val e = alcance * k / 4f
            drawRoundRect(Color.Black.copy(alpha = fuerza / 4f), Offset(r.left - e * 0.3f, r.top + e * 0.6f), Size(r.width + e * 0.6f, r.height + e * 0.4f), CornerRadius(radio + e))
        }
    }

    private fun DrawScope.sombraInterior(r: Rect, ancho: Float, fuerza: Float) {
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = fuerza), Color.Transparent), r.top, r.top + ancho), r.topLeft, Size(r.width, ancho))
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = fuerza * 0.5f)), r.bottom - ancho, r.bottom), Offset(r.left, r.bottom - ancho), Size(r.width, ancho))
        drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = fuerza * 0.7f), Color.Transparent), r.left, r.left + ancho), r.topLeft, Size(ancho, r.height))
        drawRect(Brush.horizontalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = fuerza * 0.7f)), r.right - ancho, r.right), Offset(r.right - ancho, r.top), Size(ancho, r.height))
    }

    private fun DrawScope.halo(c: Offset, r: Float, color: Color, fuerza: Float) =
        drawCircle(Brush.radialGradient(listOf(color.copy(alpha = fuerza), color.copy(alpha = fuerza * 0.35f), Color.Transparent), c, r.coerceAtLeast(1f)), r, c)

    /** Un remache o clavo de latón con su punto de luz. */
    private fun DrawScope.remache(p: Offset, r: Float, oscuro: Boolean, cuerpo: Color = LATON) {
        drawCircle(Color.Black.copy(alpha = 0.35f), r, Offset(p.x + r * 0.3f, p.y + r * 0.35f))
        drawCircle(tl(cuerpo, oscuro), r, p)
        drawCircle(Color.White.copy(alpha = if (oscuro) 0.25f else 0.5f), r * 0.42f, Offset(p.x - r * 0.3f, p.y - r * 0.3f))
    }

    private fun DrawScope.remaches(a: Offset, b: Offset, paso: Float, r: Float, oscuro: Boolean, cuerpo: Color = LATON) {
        val l = kotlin.math.hypot(b.x - a.x, b.y - a.y)
        val n = (l / paso).toInt().coerceIn(1, 200)
        for (k in 0..n) {
            val f = k / n.toFloat()
            remache(Offset(a.x + (b.x - a.x) * f, a.y + (b.y - a.y) * f), r, oscuro, cuerpo)
        }
    }

    /** Vetas de madera en [r]: líneas onduladas a lo largo, más o menos marcadas. */
    private fun DrawScope.vetas(r: Rect, color: Color, semilla: Int, u: Float, vertical: Boolean = false, paso: Float = 1.3f) {
        val largo = if (vertical) r.height else r.width
        val ancho = if (vertical) r.width else r.height
        val n = (ancho / (paso * u)).toInt().coerceIn(2, 60)
        for (k in 0 until n) {
            val base = ancho * (k + azar(semilla, k)) / n
            val amp = (0.2f + 0.6f * azar(semilla, k + 100)) * u
            val fase = azar(semilla, k + 200) * 6.28f
            val ondas = 1f + 2f * azar(semilla, k + 300)
            val p = Path()
            for (s in 0..14) {
                val q = largo * s / 14f
                val v = base + amp * sin(fase + ondas * 6.28f * s / 14f)
                val x = if (vertical) r.left + v else r.left + q
                val y = if (vertical) r.top + q else r.top + v
                if (s == 0) p.moveTo(x, y) else p.lineTo(x, y)
            }
            drawPath(p, color.copy(alpha = color.alpha * (0.3f + 0.7f * azar(semilla, k + 400))), style = Stroke(u * (0.15f + 0.25f * azar(semilla, k + 500))))
        }
    }

    /** Una tabla de caoba en [r], con su veta y su bisel. */
    private fun DrawScope.caoba(r: Rect, oscuro: Boolean, u: Float, semilla: Int, vertical: Boolean = false, tono: Color = CAOBA) {
        drawRect(t(tono, oscuro), r.topLeft, r.size)
        clipRect(r.left, r.top, r.right, r.bottom) { vetas(r, t(CAOBA_OSC, oscuro).copy(alpha = 0.55f), semilla, u, vertical) }
        drawRect(Color.White.copy(alpha = if (oscuro) 0.05f else 0.1f), r.topLeft, Size(r.width, 0.4f * u))
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(r.left, r.bottom - 0.4f * u), Size(r.width, 0.4f * u))
    }

    /** Una moldura de latón horizontal en [r] (degradado de bruñido). */
    private fun DrawScope.laton(r: Rect, oscuro: Boolean) =
        drawRect(Brush.verticalGradient(listOf(tl(LATON_LUZ, oscuro), tl(LATON, oscuro), tl(LATON_OSC, oscuro)), r.top, r.bottom), r.topLeft, r.size)

    /** Un muro de ladrillo a soga en [r], con sus juntas y el tizne del hollín. */
    private fun DrawScope.ladrillos(r: Rect, oscuro: Boolean, u: Float, semilla: Int, fila: Float) {
        drawRect(t(JUNTA, oscuro), r.topLeft, r.size)
        val pieza = fila * 2.6f
        var y = r.top; var k = 0
        clipRect(r.left, r.top, r.right, r.bottom) {
            while (y < r.bottom) {
                var x = r.left - if (k % 2 == 0) 0f else pieza / 2
                var j = 0
                while (x < r.right) {
                    drawRect(t(mezcla(LADRILLO, Color.Black, 0.22f * azar(semilla, k * 50 + j)), oscuro), Offset(x + 0.25f * u, y + 0.25f * u), Size(pieza - 0.5f * u, fila - 0.5f * u))
                    drawRect(Color.White.copy(alpha = 0.06f), Offset(x + 0.25f * u, y + 0.25f * u), Size(pieza - 0.5f * u, 0.3f * u))
                    x += pieza; j++
                }
                y += fila; k++
            }
        }
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.3f), Color.Transparent), r.top, r.top + r.height * 0.5f), r.topLeft, Size(r.width, r.height * 0.5f))
    }

    /** Un tubo de cobre horizontal de [y0] a [y0] + [grosor], con su brillo. */
    private fun DrawScope.tubo(x0: Float, x1: Float, y0: Float, grosor: Float, oscuro: Boolean) {
        drawRect(Brush.verticalGradient(listOf(tl(COBRE_LUZ, oscuro), tl(COBRE, oscuro), tl(COBRE_OSC, oscuro)), y0, y0 + grosor), Offset(x0, y0), Size(x1 - x0, grosor))
    }

    /** Una brida de latón sobre un tubo horizontal. */
    private fun DrawScope.brida(x: Float, y0: Float, grosor: Float, oscuro: Boolean, u: Float) {
        drawRect(Brush.horizontalGradient(listOf(tl(LATON_LUZ, oscuro), tl(LATON_OSC, oscuro)), x - 0.9f * u, x + 0.9f * u), Offset(x - 0.9f * u, y0 - grosor * 0.25f), Size(1.8f * u, grosor * 1.5f))
    }

    /** Un aplique de gas: la placa de latón en la pared, el brazo, el globo de vidrio y la llama, con su halo. */
    private fun DrawScope.aplique(c: Offset, alto: Float, oscuro: Boolean, u: Float) {
        halo(c, alto * (if (oscuro) 3.4f else 2.2f), GAS, if (oscuro) 0.42f else 0.2f)
        val k = alto / 10f
        drawRoundRect(tl(LATON_OSC, oscuro), Offset(c.x - 1.6f * k, c.y + 2.5f * k), Size(3.2f * k, 4.6f * k), CornerRadius(1.6f * k))
        drawLine(tl(LATON, oscuro), Offset(c.x, c.y + 4.5f * k), Offset(c.x, c.y + 2f * k), 1f * k)
        drawRect(tl(LATON, oscuro), Offset(c.x - 2f * k, c.y + 1.2f * k), Size(4f * k, 1.2f * k))
        val globo = Path().apply {
            moveTo(c.x - 1.8f * k, c.y + 1.2f * k); cubicTo(c.x - 3.6f * k, c.y - 0.5f * k, c.x - 3.2f * k, c.y - 4.2f * k, c.x - 1.4f * k, c.y - 5f * k)
            lineTo(c.x + 1.4f * k, c.y - 5f * k); cubicTo(c.x + 3.2f * k, c.y - 4.2f * k, c.x + 3.6f * k, c.y - 0.5f * k, c.x + 1.8f * k, c.y + 1.2f * k); close()
        }
        drawPath(globo, if (oscuro) GAS.copy(alpha = 0.7f) else VAPOR_B.copy(alpha = 0.75f))
        drawPath(globo, tl(LATON_OSC, oscuro), style = Stroke(0.3f * k))
        drawPath(Path().apply { moveTo(c.x, c.y + 0.6f * k); quadraticTo(c.x - 1.3f * k, c.y - 1.2f * k, c.x, c.y - 3f * k); quadraticTo(c.x + 1.3f * k, c.y - 1.2f * k, c.x, c.y + 0.6f * k) },
            if (oscuro) LLAMA else GAS)
        drawLine(Color.White.copy(alpha = 0.5f), Offset(c.x - 2f * k, c.y - 3.4f * k), Offset(c.x - 2.2f * k, c.y - 0.6f * k), 0.35f * k, StrokeCap.Round)
        drawRect(tl(LATON, oscuro), Offset(c.x - 1.6f * k, c.y - 5.6f * k), Size(3.2f * k, 0.8f * k))
    }

    /** Una nube de vapor saliendo de [o] hacia arriba a la derecha. */
    private fun DrawScope.vapor(o: Offset, r: Float, oscuro: Boolean) {
        for (k in 0 until 4) drawCircle(VAPOR_B.copy(alpha = if (oscuro) 0.16f - 0.025f * k else 0.62f - 0.1f * k), r * (0.5f + 0.35f * k), Offset(o.x + r * 0.7f * k, o.y - r * 0.9f * k))
    }

    /** El lino del mantel y la seda de la vitrina: claros de día, en penumbra de noche, cerca del fondo de la tarjeta. */
    private fun forro(e: EstadoMueble, oscuro: Boolean, tono: Color) =
        if (oscuro) mezcla(e.fondo, mezcla(tono, Color.Black, 0.72f), 0.55f) else mezcla(e.fondo, tono, 0.22f)

    // ---------------------------------------------------------------------
    //  Los nichos
    // ---------------------------------------------------------------------

    override fun area(m: MuebleVida): Rect = when (m) {
        MuebleVida.SOCIAL -> Rect(0.15f, 0.27f, 0.85f, 0.85f)
        MuebleVida.MAPAS -> Rect(0.16f, 0.085f, 0.84f, 0.72f)
        MuebleVida.OTRAS_VIDAS -> Rect(0.05f, 0.27f, 0.95f, 0.83f)
        MuebleVida.NOTAS -> Rect(0.075f, 0.165f, 0.925f, 0.885f)
    }

    // ---------------------------------------------------------------------
    //  Social: el comedor del club
    // ---------------------------------------------------------------------

    /** Una silla de respaldo de globo vista desde arriba, mirando hacia +y antes de girar [giro] grados alrededor de [c]. */
    private fun DrawScope.silla(c: Offset, ancho: Float, fondo: Float, giro: Float, cuero: Color, oscuro: Boolean, u: Float) {
        rotate(giro, c) {
            val asiento = Path().apply {
                moveTo(c.x - ancho * 0.42f, c.y - fondo * 0.18f); lineTo(c.x + ancho * 0.42f, c.y - fondo * 0.18f)
                quadraticTo(c.x + ancho * 0.56f, c.y + fondo * 0.5f, c.x + ancho * 0.46f, c.y + fondo * 0.5f)
                lineTo(c.x - ancho * 0.46f, c.y + fondo * 0.5f); quadraticTo(c.x - ancho * 0.56f, c.y + fondo * 0.5f, c.x - ancho * 0.42f, c.y - fondo * 0.18f); close()
            }
            drawPath(Path().apply { addPath(asiento, Offset(0.7f * u, 1f * u)) }, Color.Black.copy(alpha = 0.35f))
            drawPath(asiento, t(CAOBA, oscuro))
            val cojin = Rect(c.x - ancho * 0.36f, c.y - fondo * 0.12f, c.x + ancho * 0.36f, c.y + fondo * 0.44f)
            drawRoundRect(t(cuero, oscuro), cojin.topLeft, cojin.size, CornerRadius(1.4f * u))
            drawRoundRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.2f), Color.Transparent, Color.Black.copy(alpha = 0.25f)), cojin.top, cojin.bottom), cojin.topLeft, cojin.size, CornerRadius(1.4f * u))
            for (i in 0 until 3) for (j in 0 until 2) {
                val b = Offset(cojin.left + cojin.width * (0.22f + 0.28f * i), cojin.top + cojin.height * (0.3f + 0.4f * j))
                drawCircle(Color.Black.copy(alpha = 0.4f), 0.35f * u, b)
            }
            // El respaldo de globo: un aro de caoba con su brillo.
            val globo = Rect(c.x - ancho * 0.5f, c.y - fondo * 0.5f, c.x + ancho * 0.5f, c.y - fondo * 0.5f + fondo * 0.6f)
            drawArc(Color.Black.copy(alpha = 0.3f), 180f, 180f, false, Offset(globo.left + 0.6f * u, globo.top + 0.9f * u), globo.size, style = Stroke(1.7f * u, cap = StrokeCap.Round))
            drawArc(t(CAOBA_LUZ, oscuro), 180f, 180f, false, globo.topLeft, globo.size, style = Stroke(1.7f * u, cap = StrokeCap.Round))
            drawArc(Color.White.copy(alpha = if (oscuro) 0.08f else 0.22f), 200f, 70f, false, globo.topLeft, globo.size, style = Stroke(0.5f * u, cap = StrokeCap.Round))
            drawCircle(tl(LATON, oscuro), 0.5f * u, Offset(c.x, globo.top))
        }
    }

    /** Una butaca Chesterfield vista desde arriba: el respaldo y los brazos enrollados, capitoneados, y el cojín. */
    private fun DrawScope.butaca(c: Offset, ancho: Float, fondo: Float, giro: Float, cuero: Color, oscuro: Boolean, u: Float) {
        rotate(giro, c) {
            val r = Rect(c.x - ancho / 2, c.y - fondo / 2, c.x + ancho / 2, c.y + fondo / 2)
            drawRoundRect(Color.Black.copy(alpha = 0.38f), Offset(r.left + 0.8f * u, r.top + 1.1f * u), r.size, CornerRadius(2.4f * u))
            drawRoundRect(t(mezcla(cuero, Color.Black, 0.2f), oscuro), r.topLeft, r.size, CornerRadius(2.4f * u))
            val brazo = ancho * 0.2f
            // El respaldo y los brazos: tres rollos con sus botones.
            listOf(Rect(r.left, r.top, r.right, r.top + fondo * 0.32f), Rect(r.left, r.top, r.left + brazo, r.bottom), Rect(r.right - brazo, r.top, r.right, r.bottom)).forEach { q ->
                drawRoundRect(t(cuero, oscuro), q.topLeft, q.size, CornerRadius(2f * u))
                drawRoundRect(Brush.linearGradient(listOf(Color.White.copy(alpha = 0.18f), Color.Transparent, Color.Black.copy(alpha = 0.2f)), q.topLeft, q.bottomRight), q.topLeft, q.size, CornerRadius(2f * u))
            }
            var x = r.left + brazo + 1.6f * u
            while (x < r.right - brazo - 1f * u) { drawCircle(Color.Black.copy(alpha = 0.4f), 0.35f * u, Offset(x, r.top + fondo * 0.16f)); x += 2.6f * u }
            listOf(r.left + brazo / 2, r.right - brazo / 2).forEach { bx ->
                var y = r.top + fondo * 0.42f
                while (y < r.bottom - 1.2f * u) { drawCircle(Color.Black.copy(alpha = 0.4f), 0.35f * u, Offset(bx, y)); y += 2.6f * u }
            }
            val cojin = Rect(r.left + brazo + 0.3f * u, r.top + fondo * 0.34f, r.right - brazo - 0.3f * u, r.bottom - 0.6f * u)
            drawRoundRect(t(mezcla(cuero, Color.White, 0.08f), oscuro), cojin.topLeft, cojin.size, CornerRadius(1.4f * u))
            drawRoundRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.16f), Color.Black.copy(alpha = 0.2f)), cojin.top, cojin.bottom), cojin.topLeft, cojin.size, CornerRadius(1.4f * u))
            // Los clavos de latón a lo largo del frente de los brazos.
            listOf(r.left + brazo * 0.5f, r.right - brazo * 0.5f).forEach { bx -> drawCircle(tl(LATON, oscuro), 0.55f * u, Offset(bx, r.bottom - 1f * u)) }
        }
    }

    /** Una palmera en su macetero de latón, vista desde arriba. */
    private fun DrawScope.palmera(c: Offset, r: Float, semilla: Int, oscuro: Boolean, u: Float) {
        drawCircle(Color.Black.copy(alpha = 0.3f), r * 0.5f, Offset(c.x + r * 0.08f, c.y + r * 0.12f))
        drawCircle(Brush.linearGradient(listOf(tl(LATON_LUZ, oscuro), tl(LATON_OSC, oscuro)), Offset(c.x - r * 0.45f, c.y - r * 0.45f), Offset(c.x + r * 0.45f, c.y + r * 0.45f)), r * 0.45f, c)
        drawCircle(t(Color(0xFF3A2616), oscuro), r * 0.34f, c)
        for (k in 0 until 7) {
            val a = (k / 7f) * 360f + 20f * azar(semilla, k)
            rotate(a, c) {
                val hoja = Path().apply {
                    moveTo(c.x, c.y); quadraticTo(c.x + r * 0.5f, c.y - r * 0.26f, c.x + r * 1.05f, c.y + r * 0.06f)
                    quadraticTo(c.x + r * 0.5f, c.y + r * 0.12f, c.x, c.y); close()
                }
                drawPath(Path().apply { addPath(hoja, Offset(r * 0.06f, r * 0.1f)) }, Color.Black.copy(alpha = 0.25f))
                drawPath(hoja, t(if (k % 2 == 0) PALMA else mezcla(PALMA, PALMA_LUZ, 0.5f), oscuro))
                drawLine(t(PALMA_LUZ, oscuro), c, Offset(c.x + r * 0.95f, c.y + r * 0.02f), 0.25f * u)
            }
        }
    }

    /** Una mesita redonda de caoba con el servicio de té: la tetera de latón y dos tazas. */
    private fun DrawScope.mesitaTe(c: Offset, r: Float, oscuro: Boolean, u: Float) {
        drawCircle(Color.Black.copy(alpha = 0.35f), r, Offset(c.x + r * 0.08f, c.y + r * 0.14f))
        drawCircle(t(CAOBA, oscuro), r, c)
        drawCircle(t(CAOBA_LUZ, oscuro), r * 0.86f, c, style = Stroke(0.4f * u))
        drawCircle(tl(LATON_OSC, oscuro), r, c, style = Stroke(0.4f * u))
        // La bandeja, la tetera y las tazas.
        drawCircle(tl(LATON_OSC, oscuro), r * 0.58f, Offset(c.x - r * 0.1f, c.y))
        drawCircle(tl(LATON, oscuro), r * 0.52f, Offset(c.x - r * 0.1f, c.y))
        val te = Offset(c.x - r * 0.18f, c.y - r * 0.05f)
        drawLine(tl(LATON_OSC, oscuro), te, Offset(te.x + r * 0.42f, te.y - r * 0.2f), r * 0.1f, StrokeCap.Round)
        drawCircle(Brush.radialGradient(listOf(tl(LATON_LUZ, oscuro), tl(LATON_OSC, oscuro)), Offset(te.x - r * 0.1f, te.y - r * 0.1f), r * 0.32f), r * 0.27f, te)
        drawCircle(t(HIERRO, oscuro), r * 0.07f, te)
        listOf(Offset(c.x + r * 0.5f, c.y + r * 0.35f), Offset(c.x + r * 0.3f, c.y - r * 0.55f)).forEach { p ->
            drawCircle(Color.Black.copy(alpha = 0.25f), r * 0.18f, Offset(p.x + r * 0.03f, p.y + r * 0.05f))
            drawCircle(t(CARA, oscuro), r * 0.17f, p)
            drawCircle(t(Color(0xFF6A3A1C), oscuro), r * 0.1f, p)
        }
    }

    override fun DrawScope.muebleSocial(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        if (w < 1f || h < 1f) return
        val a = nicho(MuebleVida.SOCIAL)
        val azarV = Random(e.semilla.toLong())
        // El parqué: tablas de roble a lo largo, con sus juntas escalonadas.
        val pared = h * 0.155f
        val piso = Rect(0f, pared, w, h)
        drawRect(t(ROBLE_OSC, oscuro), piso.topLeft, piso.size)
        val tabla = 3.4f * u
        var y = piso.top; var fila = 0
        while (y < h) {
            var x = -tabla * 6f * azar(e.semilla, fila)
            while (x < w) {
                val largo = tabla * (7f + 6f * azarV.nextFloat())
                val r = Rect(x + 0.15f * u, y + 0.15f * u, x + largo - 0.15f * u, y + tabla - 0.15f * u)
                drawRect(t(mezcla(ROBLE, ROBLE_OSC, 0.3f * azarV.nextFloat()), oscuro), r.topLeft, r.size)
                x += largo
            }
            clipRect(0f, y, w, y + tabla) { vetas(Rect(0f, y, w, y + tabla), t(ROBLE_OSC, oscuro).copy(alpha = 0.4f), e.semilla + fila, u, paso = 1.1f) }
            y += tabla; fila++
        }
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.35f), Color.Transparent), piso.top, piso.top + 6f * u), piso.topLeft, Size(w, 6f * u))
        // La alfombra: el campo granate, la orla dorada con sus rombos y los flecos de las cabeceras.
        val alf = Rect(w * 0.035f, a.top - 9.5f * u, w * 0.965f, minOf(h - 1.4f * u, a.bottom + 9.5f * u))
        val campo = t(mezcla(ALFOMBRA, e.acento, 0.15f), oscuro)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(alf.left + 0.5f * u, alf.top + 0.8f * u), alf.size)
        drawRect(campo, alf.topLeft, alf.size)
        val orla = 2.6f * u
        drawRect(mezcla(t(Color(0xFF1E2C3E), oscuro), campo, 0.3f), alf.topLeft, alf.size, style = Stroke(orla * 2))
        listOf(0.4f * u, orla * 1.05f).forEach { m -> drawRect(tl(LATON, oscuro), Offset(alf.left + m, alf.top + m), Size(alf.width - 2 * m, alf.height - 2 * m), style = Stroke(0.35f * u)) }
        val rombo = 0.9f * u
        fun rombos(p0: Offset, p1: Offset) {
            val l = kotlin.math.hypot(p1.x - p0.x, p1.y - p0.y)
            val n = (l / (2.8f * u)).toInt()
            for (k in 0..n) {
                val f = k / n.toFloat()
                val p = Offset(p0.x + (p1.x - p0.x) * f, p0.y + (p1.y - p0.y) * f)
                drawPath(Path().apply { moveTo(p.x, p.y - rombo); lineTo(p.x + rombo, p.y); lineTo(p.x, p.y + rombo); lineTo(p.x - rombo, p.y); close() }, tl(LATON, oscuro).copy(alpha = 0.85f))
            }
        }
        val om = orla * 0.72f
        rombos(Offset(alf.left + om, alf.top + om), Offset(alf.right - om, alf.top + om))
        rombos(Offset(alf.left + om, alf.bottom - om), Offset(alf.right - om, alf.bottom - om))
        listOf(alf.left, alf.right).forEach { x ->
            var fy = alf.top + 0.6f * u
            val s = if (x < w / 2) -1f else 1f
            while (fy < alf.bottom) { drawLine(t(CARA, oscuro), Offset(x, fy), Offset(x + s * 1.3f * u, fy), 0.3f * u); fy += 1f * u }
        }
        // La pared: ladrillo, la tubería de cobre con sus bridas, el reloj entre engranes y los apliques de gas.
        val muro = Rect(0f, 0f, w, pared * 0.74f)
        ladrillos(muro, oscuro, u, e.semilla, muro.height / 4.5f)
        val arrimadero = Rect(0f, muro.bottom, w, pared)
        caoba(arrimadero, oscuro, u, e.semilla + 3)
        laton(Rect(0f, arrimadero.top, w, arrimadero.top + 0.7f * u), oscuro)
        var px = w * 0.04f
        while (px < w) { drawRect(Color.Black.copy(alpha = 0.25f), Offset(px, arrimadero.top + 1.2f * u), Size(0.3f * u, arrimadero.height - 2f * u)); px += w * 0.08f }
        val ty = muro.height * 0.32f; val tg = 1.6f * u
        tubo(0f, w, ty, tg, oscuro)
        listOf(0.06f, 0.36f, 0.64f, 0.94f).forEach { brida(w * it, ty, tg, oscuro, u) }
        listOf(w * 0.12f, w * 0.88f).forEach { x ->
            drawRect(Brush.horizontalGradient(listOf(tl(COBRE_LUZ, oscuro), tl(COBRE_OSC, oscuro)), x - tg / 2, x + tg / 2), Offset(x - tg / 2, ty), Size(tg, muro.bottom - ty))
        }
        val rr = pared * 0.42f
        engraneLaton(Offset(w / 2 - rr * 1.55f, pared * 0.38f), rr * 0.62f, 10, LATON, oscuro, u)
        engraneLaton(Offset(w / 2 + rr * 1.5f, pared * 0.3f), rr * 0.5f, 8, COBRE, oscuro, u)
        manometro(Offset(w / 2, pared * 0.46f), rr, oscuro)
        listOf(w * 0.27f, w * 0.73f).forEach { aplique(Offset(it, pared * 0.44f), pared * 0.5f, oscuro, u) }
        val valv = Offset(w * 0.965f, ty)
        drawRect(tl(LATON_OSC, oscuro), Offset(valv.x - 0.8f * u, valv.y - 2.4f * u), Size(1.6f * u, 2.4f * u))
        vapor(Offset(valv.x - 1.6f * u, valv.y - 3.4f * u), 1.6f * u, oscuro)
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.45f), Color.Transparent), pared, pared + 3f * u), Offset(0f, pared), Size(w, 3f * u))
        // Las mesitas del té y las palmeras de las esquinas.
        val esquina = a.left - 3.2f * u
        val rm = minOf(esquina * 0.42f, 6.4f * u)
        mesitaTe(Offset(esquina * 0.5f, pared + rm + 1.6f * u), rm, oscuro, u)
        mesitaTe(Offset(w - esquina * 0.5f, pared + rm + 1.6f * u), rm, oscuro, u)
        palmera(Offset(esquina * 0.48f, h - rm * 1.45f), rm * 1.25f, e.semilla, oscuro, u)
        palmera(Offset(w - esquina * 0.48f, h - rm * 1.45f), rm * 1.25f, e.semilla + 7, oscuro, u)
        // Las sillas de los lados largos y las butacas de las cabeceras.
        val aro = 3f * u
        val cuero = mezcla(CUERO, e.secundario, 0.25f)
        val fondoS = minOf(a.top - aro - pared - 0.8f * u, 10.5f * u)
        val anchoS = fondoS * 1.15f
        val n = ((a.width - 4f * u) / (anchoS * 1.85f)).toInt().coerceIn(3, 7)
        for (k in 0 until n) {
            val x = a.left + a.width * (k + 0.5f) / n
            silla(Offset(x, a.top - aro - fondoS * 0.4f), anchoS, fondoS, 0f, cuero, oscuro, u)
            silla(Offset(x, a.bottom + aro + fondoS * 0.4f), anchoS, fondoS, 180f, cuero, oscuro, u)
        }
        val fondoB = minOf(fondoS * 1.25f, esquina - 0.5f * u)
        butaca(Offset(a.left - aro - fondoB * 0.5f, a.center.y), anchoS * 1.5f, fondoB, -90f, CUERO_MARRON, oscuro, u)
        butaca(Offset(a.right + aro + fondoB * 0.5f, a.center.y), anchoS * 1.5f, fondoB, 90f, CUERO_MARRON, oscuro, u)
        // La mesa: canto de caoba, el filete de latón y el mantel de lino (el nicho).
        val fuera = Rect(a.left - aro, a.top - aro, a.right + aro, a.bottom + aro)
        sombraSuave(fuera, 3f * u, 4f * u, if (oscuro) 0.5f else 0.32f)
        drawRoundRect(t(CAOBA_OSC, oscuro), Offset(fuera.left - 0.5f * u, fuera.top - 0.5f * u), Size(fuera.width + 1f * u, fuera.height + 1f * u), CornerRadius(4f * u))
        drawRoundRect(t(CAOBA, oscuro), fuera.topLeft, fuera.size, CornerRadius(3.6f * u))
        clipPath(Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(fuera, CornerRadius(3.6f * u))) }) {
            vetas(fuera, t(CAOBA_OSC, oscuro).copy(alpha = 0.6f), e.semilla + 21, u)
            drawRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = if (oscuro) 0.06f else 0.14f), Color.Transparent), fuera.top, fuera.top + aro), fuera.topLeft, Size(fuera.width, aro))
        }
        drawRoundRect(tl(LATON, oscuro), Offset(fuera.left + 1.1f * u, fuera.top + 1.1f * u), Size(fuera.width - 2.2f * u, fuera.height - 2.2f * u), CornerRadius(2.6f * u), style = Stroke(0.4f * u))
        val lino = forro(e, oscuro, Color(0xFFD8C69C))
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(a.left + 0.3f * u, a.top + 0.5f * u), a.size)
        drawRect(lino, a.topLeft, a.size)
        // La trama del lino y el dobladillo bordado, muy suaves.
        val hilo = (if (oscuro) Color.White else Color(0xFF6E5A36)).copy(alpha = if (oscuro) 0.018f else 0.035f)
        var lx = a.left + 1.2f * u; while (lx < a.right) { drawLine(hilo, Offset(lx, a.top), Offset(lx, a.bottom), 0.25f * u); lx += 1.4f * u }
        drawRect(e.secundario.copy(alpha = 0.3f), Offset(a.left + 1.4f * u, a.top + 1.4f * u), Size(a.width - 2.8f * u, a.height - 2.8f * u), style = Stroke(0.3f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1f * u, 0.8f * u))))
        sombraInterior(a, 1.8f * u, if (oscuro) 0.22f else 0.08f)
    }

    // ---------------------------------------------------------------------
    //  Mapas: el escritorio del cartógrafo
    // ---------------------------------------------------------------------

    /** Una pata torneada de caoba en [r], con su anillo de latón y la rueda abajo. */
    private fun DrawScope.pataTorneada(r: Rect, oscuro: Boolean, u: Float) {
        val cx = r.center.x; val wd = r.width
        val madera = Brush.horizontalGradient(listOf(t(CAOBA_LUZ, oscuro), t(CAOBA, oscuro), t(CAOBA_OSC, oscuro)), r.left, r.right)
        drawRect(madera, Offset(cx - wd * 0.3f, r.top), Size(wd * 0.6f, r.height * 0.85f))
        drawOval(madera, Offset(r.left, r.top + r.height * 0.18f), Size(wd, r.height * 0.36f))
        drawRect(Brush.horizontalGradient(listOf(tl(LATON_LUZ, oscuro), tl(LATON_OSC, oscuro)), r.left, r.right), Offset(cx - wd * 0.38f, r.top + r.height * 0.1f), Size(wd * 0.76f, r.height * 0.07f))
        drawOval(madera, Offset(cx - wd * 0.36f, r.top + r.height * 0.6f), Size(wd * 0.72f, r.height * 0.18f))
        drawCircle(t(HIERRO, oscuro), wd * 0.24f, Offset(cx, r.bottom - wd * 0.24f))
        drawCircle(tl(LATON, oscuro), wd * 0.12f, Offset(cx, r.bottom - wd * 0.24f))
    }

    override fun DrawScope.muebleMapas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        if (w < 1f || h < 1f) return
        val a = nicho(MuebleVida.MAPAS)
        // Detrás: la pared de ladrillo, el zócalo y el piso.
        ladrillos(Rect(0f, 0f, w, h * 0.9f), oscuro, u, e.semilla, 4f * u)
        caoba(Rect(0f, h * 0.88f, w, h * 0.94f), oscuro, u, e.semilla + 2)
        drawRect(t(ROBLE_OSC, oscuro), Offset(0f, h * 0.94f), Size(w, h * 0.06f))
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(0f, h * 0.94f), Size(w, 0.6f * u))
        val tablero = Rect(w * 0.015f, h * 0.02f, w * 0.985f, h * 0.775f)
        // Las patas torneadas y el faldón con sus dos cajones.
        listOf(w * 0.06f, w * 0.94f).forEach { x -> pataTorneada(Rect(x - 2.8f * u, h * 0.86f, x + 2.8f * u, h - 0.6f * u), oscuro, u) }
        val faldon = Rect(w * 0.03f, h * 0.835f, w * 0.97f, h * 0.9f)
        caoba(faldon, oscuro, u, e.semilla + 4)
        listOf(0.3f, 0.7f).forEach { f ->
            val cx = w * f
            val cajon = Rect(cx - w * 0.15f, faldon.top + 0.6f * u, cx + w * 0.15f, faldon.bottom - 0.6f * u)
            drawRect(t(CAOBA_LUZ, oscuro), cajon.topLeft, cajon.size)
            clipRect(cajon.left, cajon.top, cajon.right, cajon.bottom) { vetas(cajon, t(CAOBA_OSC, oscuro).copy(alpha = 0.5f), e.semilla + (f * 10).toInt(), u) }
            drawRect(Color.Black.copy(alpha = 0.4f), cajon.topLeft, cajon.size, style = Stroke(0.35f * u))
            // El asa de latón y la bocallave.
            drawArc(tl(LATON, oscuro), 0f, 180f, false, Offset(cx - 3.4f * u, cajon.center.y - 1.6f * u), Size(6.8f * u, 3f * u), style = Stroke(0.6f * u))
            listOf(cx - 3.4f * u, cx + 3.4f * u).forEach { x -> drawCircle(tl(LATON_OSC, oscuro), 0.7f * u, Offset(x, cajon.center.y - 0.2f * u)) }
            drawCircle(tl(LATON, oscuro), 0.9f * u, Offset(cx, cajon.top + cajon.height * 0.3f))
            drawRect(t(HIERRO, oscuro), Offset(cx - 0.2f * u, cajon.top + cajon.height * 0.3f), Size(0.4f * u, 0.9f * u))
        }
        sombraSuave(tablero, 1f * u, 4f * u, if (oscuro) 0.5f else 0.32f)
        // El canto moldurado, con su junquillo de latón y sus clavos.
        val canto = Rect(w * 0.005f, tablero.bottom, w * 0.995f, h * 0.84f)
        drawRect(Brush.verticalGradient(listOf(t(CAOBA_LUZ, oscuro), t(CAOBA, oscuro), t(CAOBA_OSC, oscuro)), canto.top, canto.bottom), canto.topLeft, canto.size)
        laton(Rect(canto.left, canto.top, canto.right, canto.top + 0.8f * u), oscuro)
        drawLine(t(CAOBA_OSC, oscuro), Offset(canto.left, canto.center.y + 0.6f * u), Offset(canto.right, canto.center.y + 0.6f * u), 0.4f * u)
        remaches(Offset(canto.left + 3f * u, canto.center.y + 2f * u), Offset(canto.right - 3f * u, canto.center.y + 2f * u), 8f * u, 0.45f * u, oscuro)
        // El tablero de caoba, con su filete y sus cantoneras de latón.
        caoba(tablero, oscuro, u, e.semilla + 6)
        drawRect(tl(LATON_OSC, oscuro), Offset(tablero.left + 1.6f * u, tablero.top + 1.6f * u), Size(tablero.width - 3.2f * u, tablero.height - 3.2f * u), style = Stroke(0.4f * u))
        val ck = 6f * u
        listOf(tablero.topLeft to Offset(1f, 1f), Offset(tablero.right, tablero.top) to Offset(-1f, 1f), Offset(tablero.left, tablero.bottom) to Offset(1f, -1f), Offset(tablero.right, tablero.bottom) to Offset(-1f, -1f))
            .forEach { (o, s) ->
                val p = Path().apply {
                    moveTo(o.x, o.y); lineTo(o.x + s.x * ck, o.y)
                    quadraticTo(o.x + s.x * ck * 0.35f, o.y + s.y * ck * 0.35f, o.x, o.y + s.y * ck); close()
                }
                drawPath(p, Brush.linearGradient(listOf(tl(LATON_LUZ, oscuro), tl(LATON_OSC, oscuro)), o, Offset(o.x + s.x * ck, o.y + s.y * ck)))
                remache(Offset(o.x + s.x * 1.7f * u, o.y + s.y * 1.7f * u), 0.5f * u, oscuro, LATON_OSC)
            }
        val izq = Rect(tablero.left + 3f * u, a.top, a.left - 3f * u, a.bottom)
        val der = Rect(a.right + 3f * u, a.top, tablero.right - 3f * u, a.bottom)
        // A la izquierda: el tintero con la pluma y el compás.
        val m = izq.width
        if (m > 2f * u) {
            val ti = Offset(izq.center.x, izq.top + m * 0.5f)
            val rt = m * 0.36f
            drawRoundRect(Color.Black.copy(alpha = 0.35f), Offset(ti.x - rt + 0.7f * u, ti.y - rt + 1f * u), Size(rt * 2, rt * 2), CornerRadius(rt * 0.35f))
            drawRoundRect(t(Color(0xFF3B4A52), oscuro).copy(alpha = 0.9f), Offset(ti.x - rt, ti.y - rt), Size(rt * 2, rt * 2), CornerRadius(rt * 0.35f))
            drawRoundRect(Color.White.copy(alpha = if (oscuro) 0.1f else 0.3f), Offset(ti.x - rt, ti.y - rt), Size(rt * 2, rt * 2), CornerRadius(rt * 0.35f), style = Stroke(0.4f * u))
            drawCircle(Brush.radialGradient(listOf(tl(LATON_LUZ, oscuro), tl(LATON_OSC, oscuro)), Offset(ti.x - rt * 0.2f, ti.y - rt * 0.2f), rt * 0.8f), rt * 0.62f, ti)
            drawCircle(tl(LATON_OSC, oscuro), rt * 0.2f, ti)
            // La pluma de ave, cruzada.
            val p0 = Offset(ti.x + rt * 0.1f, ti.y + rt * 0.1f)
            val p1 = Offset(ti.x + m * 0.55f, ti.y - m * 0.7f)
            val pluma = Path().apply {
                moveTo(p0.x, p0.y); quadraticTo(p1.x - m * 0.42f, p1.y + m * 0.05f, p1.x, p1.y); quadraticTo(p1.x - m * 0.05f, p1.y + m * 0.42f, p0.x, p0.y); close()
            }
            drawPath(pluma, t(VAPOR_B, oscuro))
            drawLine(t(Color(0xFF8C7A62), oscuro), p0, p1, 0.3f * u)
            // El compás de puntas, abierto.
            val bis = Offset(izq.center.x, ti.y + rt + m * 0.5f)
            val largo = minOf(m * 1.4f, izq.bottom - bis.y - 1f * u)
            if (largo > 2f * u) {
                listOf(-1f, 1f).forEach { s ->
                    val p = Offset(bis.x + s * m * 0.28f, bis.y + largo)
                    drawLine(Color.Black.copy(alpha = 0.35f), Offset(bis.x + 0.6f * u, bis.y + 0.8f * u), Offset(p.x + 0.6f * u, p.y + 0.8f * u), 0.9f * u)
                    drawLine(tl(LATON, oscuro), bis, Offset(bis.x + (p.x - bis.x) * 0.8f, bis.y + (p.y - bis.y) * 0.8f), 0.9f * u, StrokeCap.Round)
                    drawLine(t(HIERRO_LUZ, oscuro), Offset(bis.x + (p.x - bis.x) * 0.78f, bis.y + (p.y - bis.y) * 0.78f), p, 0.45f * u, StrokeCap.Round)
                }
                drawCircle(tl(LATON_LUZ, oscuro), 1.3f * u, bis)
                drawCircle(tl(LATON_OSC, oscuro), 0.5f * u, bis)
            }
        }
        // A la derecha: la lámpara de pantalla verde, el reloj de bolsillo y la lupa.
        val n = der.width
        if (n > 2f * u) {
            val lc = Offset(der.center.x, der.top + n * 0.48f)
            val rl = n * 0.46f
            halo(lc, rl * (if (oscuro) 3.6f else 2.4f), GAS, if (oscuro) 0.38f else 0.16f)
            drawCircle(Color.Black.copy(alpha = 0.35f), rl, Offset(lc.x + 0.8f * u, lc.y + 1.1f * u))
            drawCircle(Brush.radialGradient(listOf(t(Color(0xFF4F8A64), oscuro), t(Color(0xFF1E4A32), oscuro)), Offset(lc.x - rl * 0.3f, lc.y - rl * 0.3f), rl * 1.3f), rl, lc)
            drawCircle(tl(LATON, oscuro), rl, lc, style = Stroke(0.6f * u))
            drawCircle(tl(LATON_LUZ, oscuro), rl * 0.3f, lc)
            drawCircle(if (oscuro) LLAMA else tl(LATON_OSC, oscuro), rl * 0.14f, lc)
            // El reloj de bolsillo con su cadena.
            val rc = Offset(der.center.x - n * 0.08f, lc.y + rl + n * 0.55f)
            val rr = n * 0.3f
            if (rc.y + rr < der.bottom) {
                val cadena = Path().apply { moveTo(rc.x, rc.y - rr); cubicTo(rc.x + n * 0.5f, rc.y - rr - n * 0.2f, rc.x + n * 0.55f, rc.y + rr * 1.6f, rc.x + n * 0.2f, rc.y + rr * 2.4f) }
                drawPath(cadena, tl(LATON_OSC, oscuro), style = Stroke(0.5f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.7f * u, 0.35f * u))))
                drawCircle(tl(LATON, oscuro), 0.8f * u, Offset(rc.x, rc.y - rr - 0.6f * u))
                manometro(rc, rr, oscuro, romano = true)
            }
            // La lupa.
            val lu = Offset(der.center.x - n * 0.05f, rc.y + rr + n * 0.75f)
            val rlu = n * 0.26f
            if (lu.y + rlu < der.bottom + 2f * u) {
                drawLine(Color.Black.copy(alpha = 0.3f), Offset(lu.x + 0.6f * u, lu.y + 0.9f * u), Offset(lu.x + rlu * 2.4f + 0.6f * u, lu.y + rlu * 1.4f + 0.9f * u), 1.4f * u, StrokeCap.Round)
                drawLine(t(CAOBA_OSC, oscuro), Offset(lu.x + rlu * 0.9f, lu.y + rlu * 0.5f), Offset(lu.x + rlu * 2.4f, lu.y + rlu * 1.4f), 1.4f * u, StrokeCap.Round)
                drawCircle(Color(0xFFBFD8DC).copy(alpha = if (oscuro) 0.25f else 0.5f), rlu, lu)
                drawCircle(tl(LATON, oscuro), rlu, lu, style = Stroke(0.7f * u))
                drawArc(Color.White.copy(alpha = 0.5f), 200f, 60f, false, Offset(lu.x - rlu * 0.7f, lu.y - rlu * 0.7f), Size(rlu * 1.4f, rlu * 1.4f), style = Stroke(0.4f * u, cap = StrokeCap.Round))
            }
        }
        // La regla de boj, abajo del plano.
        val regla = Rect(a.left + a.width * 0.35f, a.bottom + 1.6f * u, a.right - a.width * 0.04f, minOf(a.bottom + 4.8f * u, tablero.bottom - 1.4f * u))
        if (regla.height > 1f * u) {
            drawRect(Color.Black.copy(alpha = 0.35f), Offset(regla.left + 0.6f * u, regla.top + 0.8f * u), regla.size)
            drawRect(t(Color(0xFFE2C98E), oscuro), regla.topLeft, regla.size)
            drawRect(tl(LATON, oscuro), regla.topLeft, Size(1.4f * u, regla.height))
            drawRect(tl(LATON, oscuro), Offset(regla.right - 1.4f * u, regla.top), Size(1.4f * u, regla.height))
            var x = regla.left + 2.2f * u; var k = 0
            while (x < regla.right - 1.6f * u) {
                drawLine(t(Color(0xFF3A2A18), oscuro), Offset(x, regla.top), Offset(x, regla.top + regla.height * (if (k % 5 == 0) 0.55f else 0.3f)), 0.25f * u)
                x += 1.4f * u; k++
            }
        }
        // El plano (el nicho), clavado con cuatro chinchetas de latón.
        val hoja = Rect(a.left - 1.2f * u, a.top - 1.2f * u, a.right + 1.2f * u, a.bottom + 1.2f * u)
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(hoja.left + 0.8f * u, hoja.top + 1.1f * u), hoja.size)
        val papel = forro(e, oscuro, Color(0xFFD3BE92))
        drawRect(mezcla(papel, Color(0xFF8C7A55), 0.18f), hoja.topLeft, hoja.size)
        drawRect(papel, a.topLeft, a.size)
        val reticula = (if (oscuro) Color(0xFF9DB8D6) else CIANOTIPIA).copy(alpha = 0.07f)
        val paso = a.height / 8f
        var gx = a.left + paso; while (gx < a.right - 0.5f * u) { drawLine(reticula, Offset(gx, a.top), Offset(gx, a.bottom), 0.3f * u); gx += paso }
        var gy = a.top + paso; while (gy < a.bottom - 0.5f * u) { drawLine(reticula, Offset(a.left, gy), Offset(a.right, gy), 0.3f * u); gy += paso }
        listOf(hoja.topLeft, Offset(hoja.right, hoja.top), Offset(hoja.left, hoja.bottom), Offset(hoja.right, hoja.bottom)).forEach { remache(it, 0.95f * u, oscuro) }
    }

    // ---------------------------------------------------------------------
    //  Otras vidas: la vitrina del gabinete de curiosidades
    // ---------------------------------------------------------------------

    /** Un arco rebajado: [r] con la parte de arriba en media elipse de alto [flecha]. */
    private fun arcoRebajado(r: Rect, flecha: Float): Path = Path().apply {
        moveTo(r.left, r.bottom); lineTo(r.left, r.top + flecha)
        arcTo(Rect(r.left, r.top, r.right, r.top + 2 * flecha), 180f, 180f, false)
        lineTo(r.right, r.bottom); close()
    }

    /** Una pilastra de caoba en [r], con capitel y basa de latón y su panel embutido. */
    private fun DrawScope.pilastra(r: Rect, oscuro: Boolean, u: Float, semilla: Int) {
        val cap = minOf(r.height * 0.12f, 4f * u)
        caoba(Rect(r.left + r.width * 0.1f, r.top + cap, r.right - r.width * 0.1f, r.bottom - cap), oscuro, u, semilla, vertical = true)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(r.left + r.width * 0.3f, r.top + cap + 1.2f * u), Size(r.width * 0.4f, r.height - 2 * cap - 2.4f * u), style = Stroke(0.35f * u))
        drawLine(tl(LATON, oscuro), Offset(r.center.x, r.top + cap + 2f * u), Offset(r.center.x, r.bottom - cap - 2f * u), 0.4f * u)
        laton(Rect(r.left, r.top, r.right, r.top + cap * 0.5f), oscuro)
        laton(Rect(r.left + r.width * 0.06f, r.top + cap * 0.5f, r.right - r.width * 0.06f, r.top + cap), oscuro)
        laton(Rect(r.left + r.width * 0.06f, r.bottom - cap, r.right - r.width * 0.06f, r.bottom - cap * 0.5f), oscuro)
        laton(Rect(r.left, r.bottom - cap * 0.5f, r.right, r.bottom), oscuro)
    }

    override fun DrawScope.muebleOtrasVidas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        if (w < 1f || h < 1f) return
        val a = nicho(MuebleVida.OTRAS_VIDAS)
        val seda = forro(e, oscuro, if (oscuro) Color(0xFF3A5A48) else Color(0xFFD8C69C))
        // El cuerpo de caoba.
        caoba(Rect(0f, 0f, w, h), oscuro, u, e.semilla, vertical = true)
        val col = a.width / 5f
        val cornisa = h * 0.15f
        // Las urnas: el arco rebajado de cada una sobre el nicho, forrado de seda, con su moldura de latón.
        for (k in 0 until 5) {
            val x0 = a.left + col * k; val x1 = x0 + col
            val arco = Rect(x0 + 1f * u, cornisa + 1.6f * u, x1 - 1f * u, a.top + 1f)
            val flecha = minOf(arco.height * 0.85f, arco.width * 0.5f)
            if (k > 0) {
                val g = Offset(x0, cornisa + (a.top - cornisa) * 0.4f)
                engraneLaton(g, minOf(col * 0.12f, (a.top - cornisa) * 0.28f), 8, if (k % 2 == 0) LATON else COBRE, oscuro, u)
            }
            drawPath(arcoRebajado(Rect(arco.left - 0.8f * u, arco.top - 0.8f * u, arco.right + 0.8f * u, arco.bottom), flecha + 0.8f * u),
                Brush.verticalGradient(listOf(tl(LATON_LUZ, oscuro), tl(LATON_OSC, oscuro)), arco.top, arco.bottom))
            val dentro = Rect(arco.left + 0.3f * u, arco.top + 0.3f * u, arco.right - 0.3f * u, arco.bottom)
            drawPath(arcoRebajado(dentro, flecha - 0.3f * u), seda)
            clipPath(arcoRebajado(dentro, flecha - 0.3f * u)) {
                drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.35f else 0.14f), Color.Transparent), dentro.top, dentro.bottom), dentro.topLeft, dentro.size)
                // El brillo del vidrio.
                drawLine(Color.White.copy(alpha = if (oscuro) 0.1f else 0.45f), Offset(dentro.left + dentro.width * 0.2f, dentro.bottom), Offset(dentro.left + dentro.width * 0.55f, dentro.top), 1.2f * u)
                drawLine(Color.White.copy(alpha = if (oscuro) 0.06f else 0.3f), Offset(dentro.left + dentro.width * 0.36f, dentro.bottom), Offset(dentro.left + dentro.width * 0.7f, dentro.top), 0.5f * u)
            }
        }
        // El nicho: la seda, partida en cinco por las varillas de latón.
        drawRect(seda, a.topLeft, a.size)
        val tramado = (if (oscuro) Color.White else Color(0xFF6E5A36)).copy(alpha = if (oscuro) 0.018f else 0.035f)
        var sx = a.left + 1f * u; while (sx < a.right) { drawLine(tramado, Offset(sx, a.top), Offset(sx, a.bottom), 0.3f * u); sx += 1.6f * u }
        for (k in 1 until 5) {
            val x = a.left + col * k
            val v = Rect(x - 0.8f * u, a.top, x + 0.8f * u, a.bottom)
            drawRect(Brush.horizontalGradient(listOf(tl(LATON_LUZ, oscuro), tl(LATON, oscuro), tl(LATON_OSC, oscuro)), v.left, v.right), v.topLeft, v.size)
            listOf(a.top + 0.8f * u, a.bottom - 0.8f * u).forEach { yy -> drawCircle(tl(LATON_OSC, oscuro), 1.1f * u, Offset(x, yy)); drawCircle(tl(LATON_LUZ, oscuro), 0.5f * u, Offset(x - 0.2f * u, yy - 0.2f * u)) }
        }
        sombraInterior(a, 2.2f * u, if (oscuro) 0.3f else 0.1f)
        // Las pilastras de los lados.
        pilastra(Rect(0.3f * u, cornisa - 0.5f * u, a.left - 0.3f * u, a.bottom + 1.5f * u), oscuro, u, e.semilla + 1)
        pilastra(Rect(a.right + 0.3f * u, cornisa - 0.5f * u, w - 0.3f * u, a.bottom + 1.5f * u), oscuro, u, e.semilla + 2)
        // La cornisa: la moldura de latón, los dentículos, el friso de caoba con la tubería y el reloj entre engranes.
        val corona = Rect(0f, 0f, w, cornisa * 0.26f)
        laton(corona, oscuro)
        val dent = Rect(0f, corona.bottom, w, cornisa * 0.46f)
        drawRect(t(CAOBA_OSC, oscuro), dent.topLeft, dent.size)
        var dx = 0.5f * u
        while (dx < w) { drawRect(t(CAOBA_LUZ, oscuro), Offset(dx, dent.top), Size(1.6f * u, dent.height * 0.75f)); dx += 2.8f * u }
        val friso = Rect(0f, dent.bottom, w, cornisa)
        caoba(friso, oscuro, u, e.semilla + 5)
        laton(Rect(0f, friso.bottom - 0.9f * u, w, friso.bottom), oscuro)
        drawRect(Color.Black.copy(alpha = 0.4f), Offset(0f, friso.bottom), Size(w, 0.7f * u))
        val ty = friso.center.y - 0.8f * u
        tubo(w * 0.04f, w * 0.96f, ty, 1.5f * u, oscuro)
        listOf(0.08f, 0.3f, 0.7f, 0.92f).forEach { brida(w * it, ty, 1.5f * u, oscuro, u) }
        val rr = minOf(cornisa * 0.88f, a.top * 0.46f, col * 0.44f)
        engraneLaton(Offset(w / 2 - rr * 1.5f, rr * 0.82f), rr * 0.6f, 10, LATON, oscuro, u)
        engraneLaton(Offset(w / 2 + rr * 1.45f, rr * 0.72f), rr * 0.48f, 8, COBRE, oscuro, u)
        manometro(Offset(w / 2, rr * 0.98f), rr, oscuro, valor = 0.5f)
        vapor(Offset(w * 0.93f, ty - 1f * u), 1.4f * u, oscuro)
        // La cajonera: el riel de latón, un cajón por urna con su pomo y su tarjetero, y las patas de bola.
        val base = Rect(0f, a.bottom + 1.5f * u, w, h)
        laton(Rect(0f, base.top, w, base.top + 1.3f * u), oscuro)
        val cajones = Rect(a.left, base.top + 1.3f * u, a.right, h - 4.2f * u)
        drawRect(t(CAOBA_OSC, oscuro), Offset(0f, cajones.top), Size(w, cajones.height))
        for (k in 0 until 5) {
            val c = Rect(a.left + col * k + 0.6f * u, cajones.top + 0.6f * u, a.left + col * (k + 1) - 0.6f * u, cajones.bottom - 0.6f * u)
            caoba(c, oscuro, u, e.semilla + 30 + k, tono = CAOBA_LUZ)
            val tj = Rect(c.center.x - col * 0.16f, c.top + c.height * 0.18f, c.center.x + col * 0.16f, c.top + c.height * 0.5f)
            drawRect(tl(LATON, oscuro), tj.topLeft, tj.size, style = Stroke(0.4f * u))
            drawRect(t(CARA, oscuro), Offset(tj.left + 0.4f * u, tj.top + 0.4f * u), Size(tj.width - 0.8f * u, tj.height - 0.8f * u))
            remache(Offset(c.center.x, c.top + c.height * 0.74f), 0.9f * u, oscuro)
        }
        val zoc = Rect(0f, cajones.bottom, w, h - 1.4f * u)
        drawRect(Brush.verticalGradient(listOf(t(CAOBA_LUZ, oscuro), t(CAOBA_OSC, oscuro)), zoc.top, zoc.bottom), zoc.topLeft, zoc.size)
        laton(Rect(0f, zoc.top, w, zoc.top + 0.6f * u), oscuro)
        listOf(w * 0.05f, w * 0.95f).forEach { x ->
            drawOval(Brush.horizontalGradient(listOf(t(CAOBA_LUZ, oscuro), t(CAOBA_OSC, oscuro)), x - 3f * u, x + 3f * u), Offset(x - 3f * u, h - 3f * u), Size(6f * u, 3f * u))
        }
    }

    // ---------------------------------------------------------------------
    //  Íconos de los mapas
    // ---------------------------------------------------------------------

    override fun DrawScope.iconoMapa(id: String, e: EstadoIcono, oscuro: Boolean) {
        val w = size.width; val h = size.height
        if (w < 1f || h < 1f) return
        val k = h / 100f
        val u = k
        val c = Offset(w / 2, h / 2)
        val sombra = Color.Black.copy(alpha = if (oscuro) 0.45f else 0.22f)
        when (id) {
            "unesco" -> {
                // La estación de hierro y vidrio: el cobertizo de arco, la fachada de ladrillo y la torre del reloj.
                drawOval(sombra, Offset(c.x - 52f * k, c.y + 36f * k), Size(104f * k, 8f * k))
                val nave = Rect(c.x - 48f * k, c.y - 6f * k, c.x + 48f * k, c.y + 38f * k)
                ladrillos(Rect(nave.left, nave.top + 10f * k, nave.right, nave.bottom), oscuro, k, 4, 4.5f * k)
                // Los dos arcos de vidrio, con sus montantes.
                listOf(c.x - 30f * k, c.x + 30f * k).forEach { x ->
                    val v = Rect(x - 14f * k, nave.top, x + 14f * k, nave.bottom - 4f * k)
                    drawPath(arcoRebajado(Rect(v.left - 2f * k, v.top - 2f * k, v.right + 2f * k, v.bottom), 16f * k), t(HIERRO, oscuro))
                    drawPath(arcoRebajado(v, 14f * k), if (oscuro) mezcla(GAS, Color.Black, 0.35f) else Color(0xFFCFE0E2))
                    clipPath(arcoRebajado(v, 14f * k)) {
                        for (j in 1 until 4) drawLine(t(HIERRO, oscuro), Offset(v.left + v.width * j / 4f, v.top), Offset(v.left + v.width * j / 4f, v.bottom), 1f * k)
                        drawLine(t(HIERRO, oscuro), Offset(v.left, v.top + 16f * k), Offset(v.right, v.top + 16f * k), 1f * k)
                        drawArc(t(HIERRO, oscuro), 180f, 180f, false, Offset(v.left + 5f * k, v.top + 5f * k), Size(v.width - 10f * k, 22f * k), style = Stroke(0.9f * k))
                        drawLine(Color.White.copy(alpha = 0.4f), Offset(v.left + 4f * k, v.bottom), Offset(v.left + 14f * k, v.top + 6f * k), 1.2f * k)
                    }
                }
                laton(Rect(nave.left, nave.bottom - 4f * k, nave.right, nave.bottom), oscuro)
                // La torre del reloj, al centro.
                val torre = Rect(c.x - 11f * k, c.y - 30f * k, c.x + 11f * k, c.y + 38f * k)
                drawRect(t(LADRILLO, oscuro), torre.topLeft, torre.size)
                drawRect(Color.Black.copy(alpha = 0.2f), Offset(c.x, torre.top), Size(torre.width / 2, torre.height))
                drawRect(e.c2, Offset(torre.left - 1.5f * k, torre.top), Size(torre.width + 3f * k, 3f * k))
                drawPath(Path().apply { moveTo(torre.left - 3f * k, torre.top); lineTo(c.x, torre.top - 16f * k); lineTo(torre.right + 3f * k, torre.top); close() }, t(HIERRO, oscuro))
                drawPath(Path().apply { moveTo(c.x, torre.top - 16f * k); lineTo(torre.right + 3f * k, torre.top); lineTo(c.x, torre.top); close() }, Color.Black.copy(alpha = 0.25f))
                drawLine(e.c2, Offset(c.x, torre.top - 16f * k), Offset(c.x, torre.top - 22f * k), 1.4f * k)
                drawCircle(e.c2, 1.6f * k, Offset(c.x, torre.top - 22f * k))
                manometro(Offset(c.x, torre.top + 13f * k), 9f * k, oscuro, romano = true)
                drawPath(arcoRebajado(Rect(c.x - 6f * k, c.y + 18f * k, c.x + 6f * k, c.y + 34f * k), 6f * k), if (oscuro) mezcla(GAS, Color.Black, 0.2f) else t(HIERRO, oscuro))
                vapor(Offset(nave.right - 6f * k, nave.top - 2f * k), 3.4f * k, oscuro)
            }
            "rascar" -> {
                // La placa de cobre: la capa por raspar deja ver el plano grabado; una moneda dentada raspa.
                val placa = Rect(c.x - 46f * k, c.y - 32f * k, c.x + 46f * k, c.y + 32f * k)
                drawRoundRect(sombra, Offset(placa.left + 2f * k, placa.top + 3f * k), placa.size, CornerRadius(6f * k))
                drawRoundRect(Brush.linearGradient(listOf(tl(COBRE_LUZ, oscuro), tl(COBRE, oscuro), tl(COBRE_OSC, oscuro)), placa.topLeft, placa.bottomRight), placa.topLeft, placa.size, CornerRadius(6f * k))
                val campo = Rect(placa.left + 6f * k, placa.top + 6f * k, placa.right - 6f * k, placa.bottom - 6f * k)
                drawRect(t(CARA, oscuro), campo.topLeft, campo.size)
                val grabado = CIANOTIPIA.copy(alpha = if (oscuro) 0.7f else 0.8f)
                var gx = campo.left + 7f * k; while (gx < campo.right) { drawLine(grabado.copy(alpha = 0.18f), Offset(gx, campo.top), Offset(gx, campo.bottom), 0.6f * k); gx += 7f * k }
                drawPath(Path().apply {
                    moveTo(campo.left + 8f * k, campo.top + 12f * k); cubicTo(campo.left + 20f * k, campo.top + 4f * k, campo.left + 32f * k, campo.top + 12f * k, campo.left + 28f * k, campo.top + 24f * k)
                    cubicTo(campo.left + 24f * k, campo.top + 36f * k, campo.left + 12f * k, campo.top + 46f * k, campo.left + 8f * k, campo.top + 34f * k); close()
                }, grabado, style = Stroke(1.3f * k))
                drawCircle(e.c1, 2.6f * k, Offset(campo.left + 18f * k, campo.top + 22f * k))
                val capa = Path().apply {
                    moveTo(campo.right, campo.top); lineTo(campo.left + 46f * k, campo.top)
                    var yy = campo.top; var j = 0
                    while (yy < campo.bottom) { val x = campo.left + 44f * k - (yy - campo.top) * 0.3f + (if (j % 2 == 0) 3f else -2f) * k; lineTo(x, yy); yy += 4f * k; j++ }
                    lineTo(campo.left + 30f * k, campo.bottom); lineTo(campo.right, campo.bottom); close()
                }
                drawPath(capa, t(Color(0xFF8E8A80), oscuro))
                clipPath(capa) {
                    var xx = campo.left; while (xx < campo.right + campo.height) { drawLine(Color.Black.copy(alpha = 0.12f), Offset(xx, campo.top), Offset(xx - campo.height, campo.bottom), 0.7f * k); xx += 3f * k }
                }
                listOf(Offset(placa.left + 3f * k, placa.top + 3f * k), Offset(placa.right - 3f * k, placa.top + 3f * k), Offset(placa.left + 3f * k, placa.bottom - 3f * k), Offset(placa.right - 3f * k, placa.bottom - 3f * k))
                    .forEach { remache(it, 1.5f * k, oscuro) }
                val mo = Offset(campo.left + 48f * k, campo.top + 36f * k)
                drawCircle(sombra, 13f * k, Offset(mo.x + 2f * k, mo.y + 3f * k))
                engraneLaton(mo, 13f * k, 14, LATON, oscuro, u)
                drawCircle(e.c2, 5f * k, mo, style = Stroke(1f * k))
                for (j in 0 until 5) drawRect(t(Color(0xFF8E8A80), oscuro), Offset(mo.x - 16f * k - j * 2.6f * k, mo.y - 2f * k + (j % 2) * 4f * k), Size(1.6f * k, 1.6f * k))
            }
            else -> {
                // Tus lugares: el plano de la línea con la vía y tres chinchetas de latón de cabeza roja.
                val hoja = Rect(c.x - 50f * k, c.y - 30f * k, c.x + 50f * k, c.y + 34f * k)
                drawRect(sombra, Offset(hoja.left + 2f * k, hoja.top + 3f * k), hoja.size)
                drawRect(t(CARA, oscuro), hoja.topLeft, hoja.size)
                drawRect(e.c2.copy(alpha = 0.6f), Offset(hoja.left + 2.5f * k, hoja.top + 2.5f * k), Size(hoja.width - 5f * k, hoja.height - 5f * k), style = Stroke(0.8f * k))
                val pts = listOf(Offset(c.x - 34f * k, c.y + 16f * k), Offset(c.x - 2f * k, c.y - 6f * k), Offset(c.x + 32f * k, c.y + 12f * k))
                val via = Path().apply { moveTo(pts[0].x, pts[0].y); quadraticTo(c.x - 24f * k, c.y - 14f * k, pts[1].x, pts[1].y); quadraticTo(c.x + 20f * k, c.y - 2f * k, pts[2].x, pts[2].y) }
                drawPath(via, t(Color(0xFF6E5A40), oscuro), style = Stroke(5f * k, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.2f * k, 2.2f * k))))
                drawPath(via, t(HIERRO, oscuro), style = Stroke(3.2f * k))
                drawPath(via, t(CARA, oscuro), style = Stroke(1.6f * k))
                pts.forEach { p ->
                    val cab = Offset(p.x, p.y - 12f * k)
                    drawLine(sombra, Offset(p.x + 1f * k, p.y), Offset(p.x + 5f * k, p.y - 2f * k), 2f * k, StrokeCap.Round)
                    drawLine(t(HIERRO_LUZ, oscuro), p, cab, 1.2f * k)
                    drawCircle(sombra, 7f * k, Offset(cab.x + 1.2f * k, cab.y + 1.6f * k))
                    drawCircle(Brush.linearGradient(listOf(tl(LATON_LUZ, oscuro), tl(LATON_OSC, oscuro)), Offset(cab.x - 7f * k, cab.y - 7f * k), Offset(cab.x + 7f * k, cab.y + 7f * k)), 7f * k, cab)
                    drawCircle(e.c1, 4f * k, cab)
                    drawCircle(Color.White.copy(alpha = 0.45f), 1.4f * k, Offset(cab.x - 1.4f * k, cab.y - 1.4f * k))
                }
            }
        }
    }

    // ---------------------------------------------------------------------
    //  Notas: el tablón de avisos de la estación
    // ---------------------------------------------------------------------

    override fun DrawScope.tableroNotas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        if (w < 1f || h < 1f) return
        val a = nicho(MuebleVida.NOTAS)
        // La pared de ladrillo detrás del frontón.
        ladrillos(Rect(0f, 0f, w, a.top), oscuro, u, e.semilla, 3.2f * u)
        // El marco de caoba con su filete de latón.
        val marco = Rect(0f, h * 0.085f, w, h * 0.925f)
        caoba(Rect(0f, marco.top, a.left, marco.bottom), oscuro, u, e.semilla + 1, vertical = true)
        caoba(Rect(a.right, marco.top, w, marco.bottom), oscuro, u, e.semilla + 2, vertical = true)
        caoba(Rect(a.left, marco.top, a.right, a.top), oscuro, u, e.semilla + 3)
        caoba(Rect(a.left, a.bottom, a.right, marco.bottom), oscuro, u, e.semilla + 4)
        listOf(Rect(1.4f * u, marco.top + 1.4f * u, w - 1.4f * u, marco.bottom - 1.4f * u)).forEach { r ->
            drawRect(Color.Black.copy(alpha = 0.3f), r.topLeft, r.size, style = Stroke(0.4f * u))
        }
        val filete = Rect(a.left - 1.2f * u, a.top - 1.2f * u, a.right + 1.2f * u, a.bottom + 1.2f * u)
        drawRect(Brush.linearGradient(listOf(tl(LATON_LUZ, oscuro), tl(LATON_OSC, oscuro)), filete.topLeft, filete.bottomRight), filete.topLeft, filete.size)
        // Los clavos de latón a lo largo de los largueros.
        remaches(Offset(a.left * 0.5f, marco.top + 4f * u), Offset(a.left * 0.5f, marco.bottom - 4f * u), 9f * u, 0.6f * u, oscuro)
        remaches(Offset(w - a.left * 0.5f, marco.top + 4f * u), Offset(w - a.left * 0.5f, marco.bottom - 4f * u), 9f * u, 0.6f * u, oscuro)
        // El corcho (el nicho): sus gránulos, muy suaves.
        val corcho = if (oscuro) mezcla(e.fondo, CORCHO_NOCHE, 0.7f) else mezcla(e.fondo, CORCHO, 0.42f)
        drawRect(corcho, a.topLeft, a.size)
        val rnd = Random(e.semilla.toLong() + 77)
        val grano = if (oscuro) Color.Black.copy(alpha = 0.22f) else Color(0xFF7A5A30).copy(alpha = 0.12f)
        val claro = if (oscuro) Color.White.copy(alpha = 0.04f) else Color.White.copy(alpha = 0.25f)
        repeat(260) { i ->
            val p = Offset(a.left + a.width * rnd.nextFloat(), a.top + a.height * rnd.nextFloat())
            drawCircle(if (i % 3 == 0) claro else grano, (0.25f + 0.35f * rnd.nextFloat()) * u, p)
        }
        sombraInterior(a, 2.6f * u, if (oscuro) 0.35f else 0.16f)
        // El frontón: la cornisa de caoba, las volutas de latón, los engranes y el reloj de la estación.
        val corn = Rect(0f, marco.top - 1.4f * u, w, marco.top + 2.2f * u)
        laton(corn, oscuro)
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(0f, corn.bottom), Size(w, 0.8f * u))
        val rr = minOf(h * 0.085f, w * 0.085f)
        val rc = Offset(w / 2, rr + 0.6f * u)
        val fronton = Path().apply {
            moveTo(w / 2 - rr * 3.4f, corn.top); quadraticTo(w / 2 - rr * 2.2f, corn.top - rr * 0.2f, w / 2 - rr * 1.6f, rc.y - rr * 0.1f)
            lineTo(w / 2 + rr * 1.6f, rc.y - rr * 0.1f); quadraticTo(w / 2 + rr * 2.2f, corn.top - rr * 0.2f, w / 2 + rr * 3.4f, corn.top); close()
        }
        drawPath(fronton, t(CAOBA, oscuro))
        drawPath(fronton, tl(LATON_OSC, oscuro), style = Stroke(0.5f * u))
        listOf(-1f, 1f).forEach { s ->
            val voluta = Path().apply {
                moveTo(w / 2 + s * rr * 1.2f, rc.y + rr * 0.5f)
                cubicTo(w / 2 + s * rr * 2f, rc.y - rr * 0.3f, w / 2 + s * rr * 2.9f, rc.y + rr * 0.2f, w / 2 + s * rr * 2.6f, rc.y + rr * 0.55f)
                cubicTo(w / 2 + s * rr * 2.4f, rc.y + rr * 0.8f, w / 2 + s * rr * 2.15f, rc.y + rr * 0.5f, w / 2 + s * rr * 2.35f, rc.y + rr * 0.4f)
            }
            drawPath(voluta, tl(LATON, oscuro), style = Stroke(0.8f * u, cap = StrokeCap.Round))
            engraneLaton(Offset(w / 2 + s * rr * 3.9f, corn.top - rr * 0.1f), rr * 0.42f, 8, if (s < 0) LATON else COBRE, oscuro, u)
        }
        manometro(rc, rr, oscuro, romano = true)
        // Los faroles de gas de las esquinas de arriba.
        listOf(a.left * 0.5f + 1.6f * u, w - a.left * 0.5f - 1.6f * u).forEach { x -> aplique(Offset(x, marco.top - rr * 0.4f), rr * 1.1f, oscuro, u) }
        // El pie: la repisa de caoba con su placa y sus ménsulas.
        val repisa = Rect(0f, marco.bottom, w, h)
        drawRect(Brush.verticalGradient(listOf(t(CAOBA_LUZ, oscuro), t(CAOBA, oscuro), t(CAOBA_OSC, oscuro)), repisa.top, repisa.bottom), repisa.topLeft, repisa.size)
        laton(Rect(0f, repisa.top, w, repisa.top + 0.8f * u), oscuro)
        val pl = Rect(w / 2 - w * 0.12f, repisa.top + 1.6f * u, w / 2 + w * 0.12f, repisa.bottom - 1.6f * u)
        drawRect(Color.Black.copy(alpha = 0.4f), Offset(pl.left + 0.5f * u, pl.top + 0.7f * u), pl.size)
        drawRect(Brush.verticalGradient(listOf(tl(LATON_LUZ, oscuro), tl(LATON, oscuro), tl(LATON_OSC, oscuro)), pl.top, pl.bottom), pl.topLeft, pl.size)
        drawRect(tl(LATON_OSC, oscuro), Offset(pl.left + 0.8f * u, pl.top + 0.8f * u), Size(pl.width - 1.6f * u, pl.height - 1.6f * u), style = Stroke(0.35f * u))
        remache(Offset(pl.left + 2f * u, pl.center.y), 0.5f * u, oscuro, LATON_OSC)
        remache(Offset(pl.right - 2f * u, pl.center.y), 0.5f * u, oscuro, LATON_OSC)
    }

    override fun DrawScope.notaTablero(e: EstadoNota, oscuro: Boolean) {
        val r0 = e.rect
        if (r0.width < 1f || r0.height < 1f) return
        val k = minOf(r0.width, r0.height) / 100f
        val r = Rect(r0.left + 2.5f * k, r0.top + 3f * k, r0.right - 2.5f * k, r0.bottom - 2f * k)
        val papel = when (e.tipo) {
            "idea" -> if (oscuro) TELEGRAMA_N else TELEGRAMA
            "proposito" -> if (oscuro) BILLETE_N else BILLETE
            "recordatorio" -> if (oscuro) ETIQUETA_N else ETIQUETA
            else -> if (oscuro) CARTA_N else CARTA
        }
        // La silueta del papel: la etiqueta de equipaje lleva las esquinas de arriba cortadas.
        val forma = if (e.tipo == "recordatorio") Path().apply {
            val ch = 12f * k
            moveTo(r.left + ch, r.top); lineTo(r.right - ch, r.top); lineTo(r.right, r.top + ch); lineTo(r.right, r.bottom); lineTo(r.left, r.bottom); lineTo(r.left, r.top + ch); close()
        } else Path().apply { addRect(r) }
        drawPath(Path().apply { addPath(forma, Offset(1.4f * k, 2.4f * k)) }, Color.Black.copy(alpha = if (oscuro) 0.5f else 0.24f))
        drawPath(forma, papel)
        // La esquina de abajo a la derecha, un poco levantada.
        if (e.indice % 2 == 1) {
            val dobla = 9f * k
            drawPath(Path().apply { moveTo(r.right - dobla, r.bottom); lineTo(r.right, r.bottom - dobla); lineTo(r.right, r.bottom); close() }, Color.Black.copy(alpha = if (oscuro) 0.35f else 0.12f))
            drawPath(Path().apply { moveTo(r.right - dobla, r.bottom); lineTo(r.right, r.bottom - dobla); lineTo(r.right - dobla * 0.85f, r.bottom - dobla * 0.85f); close() }, mezcla(papel, Color.Black, 0.12f))
        }
        val linea = if (oscuro) Color.White.copy(alpha = 0.16f) else Color(0xFF231B14).copy(alpha = 0.22f)
        // El detalle de cada tipo, en los márgenes.
        when (e.tipo) {
            "idea" -> {
                // El telegrama: la doble raya azul del membrete.
                val azul = (if (oscuro) Color(0xFF8FA9C8) else CIANOTIPIA).copy(alpha = 0.7f)
                drawLine(azul, Offset(r.left + 4f * k, r.top + 9f * k), Offset(r.right - 4f * k, r.top + 9f * k), 1.2f * k)
                drawLine(azul, Offset(r.left + 4f * k, r.top + 11.4f * k), Offset(r.right - 4f * k, r.top + 11.4f * k), 0.5f * k)
                drawLine(azul, Offset(r.left + 4f * k, r.bottom - 6f * k), Offset(r.left + 30f * k, r.bottom - 6f * k), 0.5f * k)
            }
            "pensamiento" -> {
                // La carta: el margen rojo y las líneas, apenas.
                drawLine(ROJO.copy(alpha = if (oscuro) 0.5f else 0.45f), Offset(r.left + 8f * k, r.top), Offset(r.left + 8f * k, r.bottom), 0.5f * k)
                drawLine(linea, Offset(r.left + 4f * k, r.bottom - 7f * k), Offset(r.right - 4f * k, r.bottom - 7f * k), 0.4f * k)
            }
            "proposito" -> {
                // El billete de cartón: la franja de color y el número perforado.
                drawRect(mezcla(Color(0xFF2F6B5E), e.acento, 0.25f).copy(alpha = if (oscuro) 0.7f else 0.85f), Offset(r.left, r.top + 4f * k), Size(r.width, 4.6f * k))
                var x = r.right - 26f * k
                repeat(6) { drawCircle(linea.copy(alpha = linea.alpha * 1.6f), 0.9f * k, Offset(x, r.bottom - 6f * k)); x += 3.6f * k }
            }
            "recordatorio" -> {
                // La etiqueta de equipaje: el ojal reforzado y el cordel.
                val oj = Offset(r.center.x, r.top + 7f * k)
                drawCircle(tl(LATON, if (oscuro) true else false), 3.4f * k, oj)
                drawCircle(mezcla(papel, Color.Black, 0.5f), 1.6f * k, oj)
                drawPath(Path().apply { moveTo(oj.x, oj.y); cubicTo(oj.x + 6f * k, oj.y - 8f * k, oj.x + 14f * k, oj.y - 2f * k, oj.x + 18f * k, r0.top) }, t(Color(0xFFB8A27A), oscuro), style = Stroke(0.7f * k))
                drawLine(linea, Offset(r.left + 4f * k, r.bottom - 7f * k), Offset(r.right - 4f * k, r.bottom - 7f * k), 0.4f * k)
            }
            else -> {}
        }
        // La chincheta: de latón; la de las fijadas, de cabeza roja, con el sello de lacre abajo.
        val ch = if (e.tipo == "recordatorio") Offset(r.left + 8f * k, r.top + 7f * k) else Offset(r.center.x, r.top + 5f * k)
        drawCircle(Color.Black.copy(alpha = 0.35f), 3f * k, Offset(ch.x + 1f * k, ch.y + 1.4f * k))
        if (e.fijada) {
            drawCircle(Brush.radialGradient(listOf(Color(0xFFE0604A), ROJO, Color(0xFF5A1A10)), Offset(ch.x - 1f * k, ch.y - 1f * k), 4f * k), 3.2f * k, ch)
            drawCircle(Color.White.copy(alpha = 0.5f), 0.9f * k, Offset(ch.x - 1f * k, ch.y - 1f * k))
            val s = Offset(r.right - 11f * k, r.bottom - 8f * k)
            val lacre = Path().apply {
                for (j in 0 until 12) {
                    val ang = j / 12.0 * 2 * Math.PI
                    val rr = 5f * k * (0.9f + 0.16f * azar(e.indice + 3, j))
                    val p = Offset(s.x + rr * cos(ang).toFloat(), s.y + rr * sin(ang).toFloat())
                    if (j == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
                }
                close()
            }
            drawPath(Path().apply { addPath(lacre, Offset(0.6f * k, 0.9f * k)) }, Color.Black.copy(alpha = 0.3f))
            drawPath(lacre, mezcla(ROJO, e.acento, 0.15f))
            drawPath(engrane(s, 2.8f * k, 8), mezcla(ROJO, Color.Black, 0.35f))
            drawCircle(Color.White.copy(alpha = 0.2f), 1f * k, Offset(s.x - 2f * k, s.y - 2f * k))
        } else {
            drawCircle(Brush.radialGradient(listOf(tl(LATON_LUZ, oscuro), tl(LATON, oscuro), tl(LATON_OSC, oscuro)), Offset(ch.x - 1f * k, ch.y - 1f * k), 4f * k), 3f * k, ch)
            drawCircle(Color.White.copy(alpha = 0.45f), 0.8f * k, Offset(ch.x - 1f * k, ch.y - 1f * k))
        }
    }

    /** La tinta de las notas: hollín sobre el papel de día; de noche, el papel a la luz del gas y la tinta clara. */
    override fun tintaNota(oscuro: Boolean): Color = if (oscuro) Color(0xFFEADCC0) else Color(0xFF231B14)
}
